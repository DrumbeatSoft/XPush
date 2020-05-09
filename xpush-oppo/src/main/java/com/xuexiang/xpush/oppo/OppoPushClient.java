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
import com.xuexiang.xpush.util.PushUtils;

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

    private Context mContext;
    private String mSecret;
    private String mAppKey;
    private String registerId;

    @Override
    public void init(Context context) {
        mContext = context.getApplicationContext();

        //读取OPPO对应的Secret和AppKey
        try {
            Bundle metaData = mContext.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData;
            mSecret = metaData.getString(OPPOPUSH_SECRET).trim();
            mAppKey = metaData.getString(OPPOPUSH_APPKEY).trim();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            PushLog.e("can't find OPPOPUSH_SECRET or OPPOPUSH_APPKEY in AndroidManifest.xml");
        } catch (NullPointerException e) {
            e.printStackTrace();
            PushLog.e("can't find OPPOPUSH_SECRET or OPPOPUSH_APPKEY in AndroidManifest.xml");
        }

        HeytapPushManager.init(mContext, PushLog.isDebug());
    }

    @Override
    public void register() {
        if (TextUtils.isEmpty(mSecret) || TextUtils.isEmpty(mAppKey)) {
            throw new IllegalArgumentException("oppo push secret or appKey is not init," +
                    "check you AndroidManifest.xml is has OPPOPUSH_SECRET or OPPOPUSH_APPKEY meta-data flag please");
        }

        HeytapPushManager.register(mContext, mAppKey, mSecret, mPushCallback);
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
//        MiPushClient.setAlias(mContext, alias, null);
    }

    @Override
    public void unBindAlias(String alias) {
//        MiPushClient.unsetAlias(mContext, alias, null);
    }

    @Override
    public void getAlias() {
//        List<String> alias = MiPushClient.getAllAlias(mContext);
//        XPush.transmitCommandResult(mContext, TYPE_GET_ALIAS,
//                RESULT_OK,
//                PushUtils.collection2String(alias), null, null);

    }

    @Override
    public void addTags(String... tag) {
//        MiPushClient.subscribe(mContext, tag[0], null);
    }

    @Override
    public void deleteTags(String... tag) {
//        MiPushClient.unsubscribe(mContext, tag[0], null);
    }

    @Override
    public void getTags() {
//        List<String> tags = MiPushClient.getAllTopic(mContext);
//        XPush.transmitCommandResult(mContext, TYPE_GET_TAG,
//                RESULT_OK,
//                PushUtils.collection2String(tags), null, null);
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
                XPush.transmitCommandResult(mContext, TYPE_REGISTER, RESULT_OK, s, null, "注册成功 registerId:" + s);
                XPushManager.get().notifyConnectStatusChanged(CONNECTED);
            } else {
                registerId = "";
                XPush.transmitCommandResult(mContext, TYPE_REGISTER, RESULT_ERROR, null, null, "注册失败  code=" + code + ",msg=" + s);
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

}
