/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.xpush.oppo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.heytap.msp.push.HeytapPushManager;
import com.heytap.msp.push.callback.ICallBackResultService;
import com.xuexiang.xpush.XPush;
import com.xuexiang.xpush.core.IPushClient;
import com.xuexiang.xpush.core.XPushManager;
import com.xuexiang.xpush.logs.PushLog;
import com.xuexiang.xpush.oppo.bean.BaseBean;
import com.xuexiang.xpush.oppo.bean.TagAlias;
import com.xuexiang.xpush.oppo.bean.TagAliasRequest;
import com.xuexiang.xpush.oppo.net.HttpUtils;
import com.xuexiang.xpush.oppo.net.OppoPushAPI;
import com.xuexiang.xpush.util.PushUtils;

import java.util.ArrayList;
import java.util.List;

import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_ADD_TAG;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_BIND_ALIAS;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_DEL_TAG;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_GET_ALIAS;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_GET_TAG;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_REGISTER;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_UNREGISTER;
import static com.xuexiang.xpush.core.annotation.ConnectStatus.CONNECTED;
import static com.xuexiang.xpush.core.annotation.ConnectStatus.DISCONNECT;
import static com.xuexiang.xpush.core.annotation.ResultCode.RESULT_ERROR;
import static com.xuexiang.xpush.core.annotation.ResultCode.RESULT_OK;

/**
 * oppo推送客户端
 * 1.oppo推送
 *
 * @author xuexiang
 * @since 2019-08-24 19:22
 */

public class OppoPushClient implements IPushClient {
    public static final String OPPOPUSH_PLATFORM_NAME = "OPPOPush";
    public static final int OPPOPUSH_PLATFORM_CODE = 1005;

    private static final String OPPOPUSH_APPKEY = "OPPOPUSH_APPKEY";
    private static final String OPPOPUSH_SECRET = "OPPOPUSH_SECRET";
    private static final String PROJECT_NAME = "PROJECT_NAME";
    private static final String BASE_URL = "BASE_URL";
    private Context mContext;
    private String mSecret;
    private String mAppKey;
    private String registerId;
    private String projectName;
    private String baseUrl;

    @Override
    public void init(Context context) {
        mContext = context.getApplicationContext();

        //读取OPPO对应的Secret和AppKey
        try {
            Bundle metaData = mContext.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData;
            mSecret = metaData.getString(OPPOPUSH_SECRET).trim();
            mAppKey = metaData.getString(OPPOPUSH_APPKEY).trim();
            projectName = metaData.getString(PROJECT_NAME).trim();
            baseUrl = metaData.getString(BASE_URL).trim();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            PushLog.e("can't find OPPOPUSH_SECRET or OPPOPUSH_APPKEY or PROJECT_NAME or BASE_URL in AndroidManifest.xml");
        } catch (NullPointerException e) {
            e.printStackTrace();
            PushLog.e("can't find OPPOPUSH_SECRET or OPPOPUSH_APPKEY or PROJECT_NAME or BASE_URL in AndroidManifest.xml");
        }

        HeytapPushManager.init(mContext, PushLog.isDebug());
    }

    @Override
    public void register() {
        if (TextUtils.isEmpty(mSecret) || TextUtils.isEmpty(mAppKey) || TextUtils.isEmpty(projectName) || TextUtils.isEmpty(baseUrl)) {
            throw new IllegalArgumentException("oppo push secret or appKey is not init," +
                    "check you AndroidManifest.xml is has OPPOPUSH_SECRET or OPPOPUSH_APPKEY or PROJECT_NAME or BASE_URL meta-data flag please");
        }

        if (HeytapPushManager.isSupportPush()) {
            HeytapPushManager.register(mContext, mAppKey, mSecret, mPushCallback);
        } else {
            XPush.transmitCommandResult(mContext, TYPE_REGISTER, RESULT_ERROR, null, "注册失败", "此设备不支持OPPOPush");
            XPushManager.get().notifyConnectStatusChanged(DISCONNECT);
        }
    }

    @Override
    public void unRegister() {
        String token = getPushToken();
        if (!TextUtils.isEmpty(token)) {
            HeytapPushManager.unRegister();
            PushUtils.deletePushToken(OPPOPUSH_PLATFORM_NAME);
        }
    }

    @Override
    public void bindAlias(String alias) {
        updateAlias(alias);
    }

    @Override
    public void unBindAlias(String alias) {

    }

    @Override
    public void getAlias() {
        getDeviceTagAlias(TYPE_GET_ALIAS);
    }

    @Override
    public void addTags(String... tag) {
        List<String> tagList = new ArrayList<>();
        for (int i = 0; i < tag.length; i++) {
            tagList.add(tag[i]);
        }
        addTagList(tagList);
    }

    @Override
    public void deleteTags(String... tag) {
        List<String> tagList = new ArrayList<>();
        for (int i = 0; i < tag.length; i++) {
            tagList.add(tag[i]);
        }
        deleteTagList(tagList);
    }

    @Override
    public void getTags() {
        getDeviceTagAlias(TYPE_GET_TAG);
    }

    @Override
    public String getPushToken() {
        return registerId;
    }

    @Override
    public int getPlatformCode() {
        return OPPOPUSH_PLATFORM_CODE;
    }

    @Override
    public String getPlatformName() {
        return OPPOPUSH_PLATFORM_NAME;
    }

    private ICallBackResultService mPushCallback = new ICallBackResultService() {

        @Override
        public void onRegister(int code, String s) {
            if (!PushLog.isDebug()) {
                return;
            }
            if (code == 0) {
                registerId = s;
                XPush.transmitCommandResult(mContext, TYPE_REGISTER, RESULT_OK, registerId, "注册成功", null);
                XPushManager.get().notifyConnectStatusChanged(CONNECTED);
            } else {
                registerId = "";
                XPush.transmitCommandResult(mContext, TYPE_REGISTER, RESULT_ERROR, null, "注册失败", "code=" + code + ",msg=" + s);
                XPushManager.get().notifyConnectStatusChanged(DISCONNECT);
            }
        }

        @Override
        public void onUnRegister(int code) {
            if (!PushLog.isDebug()) {
                return;
            }

            if (code == 0) {
                XPush.transmitCommandResult(mContext, TYPE_UNREGISTER, RESULT_OK, null, "注销成功  code=" + code, null);
                XPushManager.get().notifyConnectStatusChanged(DISCONNECT);
            } else {
                XPush.transmitCommandResult(mContext, TYPE_UNREGISTER, RESULT_ERROR, null, null, "注销失败  code=" + code);
            }
        }

        @Override
        public void onGetPushStatus(final int code, int status) {
            if (!PushLog.isDebug()) {
                return;
            }

            if (code == 0 && status == 0) {
                PushLog.d("Push状态正常  code=" + code + ",status=" + status);
            } else {
                PushLog.d("Push状态错误  code=" + code + ",status=" + status);
            }
        }

        @Override
        public void onGetNotificationStatus(final int code, final int status) {
            if (!PushLog.isDebug()) {
                return;
            }

            if (code == 0 && status == 0) {
                PushLog.d("通知状态正常  code=" + code + ",status=" + status);
            } else {
                PushLog.d("通知状态错误  code=" + code + ",status=" + status);
            }
        }

        @Override
        public void onSetPushTime(final int code, final String s) {
            if (!PushLog.isDebug()) {
                return;
            }

            PushLog.d("SetPushTime  code=" + code + ",result:" + s);
        }
    };

    public void updateAlias(String updateAlias) {
        TagAliasRequest params = new TagAliasRequest();
        params.setProjectName(projectName);
        params.setRegistrationId(registerId);
        params.setUpdateAlias(updateAlias);
        updateDeviceTagAlias(params, TYPE_BIND_ALIAS);
    }

    public void addTagList(List<String> addTagList) {
        TagAliasRequest params = new TagAliasRequest();
        params.setProjectName(projectName);
        params.setRegistrationId(registerId);
        params.setAddTagList(addTagList);
        updateDeviceTagAlias(params, TYPE_ADD_TAG);
    }

    public void deleteTagList(List<String> deleteTagList) {
        TagAliasRequest params = new TagAliasRequest();
        params.setProjectName(projectName);
        params.setRegistrationId(registerId);
        params.setDeleteTagList(deleteTagList);
        updateDeviceTagAlias(params, TYPE_DEL_TAG);
    }

    private void updateDeviceTagAlias(TagAliasRequest params, final int type) {
        HttpUtils.postJson(baseUrl + OppoPushAPI.updateDeviceTagAlias, params, new HttpUtils.Callback<BaseBean>() {
            @Override
            public void onSuccess(BaseBean dataBean) {
                XPush.transmitCommandResult(mContext, type,
                        RESULT_OK, null, dataBean.getMessage(), null);
            }

            @Override
            public void onFaileure(int code, Exception e) {
                XPush.transmitCommandResult(mContext, type,
                        RESULT_ERROR, null, null, code + e.getMessage());

            }
        });
    }

    private void getDeviceTagAlias(final int type) {
        TagAliasRequest params = new TagAliasRequest();
        params.setProjectName(projectName);
        params.setRegistrationId(registerId);
        HttpUtils.postJson(baseUrl + OppoPushAPI.getDeviceTagAlias, params, new HttpUtils.Callback<BaseBean<TagAlias>>() {
            @Override
            public void onSuccess(BaseBean<TagAlias> dataBean) {
                List<String> stringList = new ArrayList<>();
                int result;
                String error = null;
                String extraMsg = null;
                TagAlias data = dataBean.getData();

                if (data != null) {
                    result = RESULT_OK;
                    if (type == TYPE_GET_TAG) {
                        stringList.addAll(data.getTagList());
                    } else {
                        String alias = data.getAlias();
                        stringList.add(alias);
                    }
                    extraMsg = dataBean.getMessage();
                } else {
                    result = RESULT_ERROR;
                    error = "Code: " + dataBean.getCode() + " Message:" + dataBean.getMessage();
                }

                XPush.transmitCommandResult(mContext, type, result,
                        PushUtils.collection2String(stringList), extraMsg, error);
            }

            @Override
            public void onFaileure(int code, Exception e) {
                XPush.transmitCommandResult(mContext, type, RESULT_ERROR, null,
                        null, "Code: " + code + " Message:" + e.getMessage());
            }
        });
    }

}
