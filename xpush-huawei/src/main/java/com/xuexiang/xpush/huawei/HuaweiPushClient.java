/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
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

package com.xuexiang.xpush.huawei;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.xuexiang.xpush.XPush;
import com.xuexiang.xpush.core.IPushClient;
import com.xuexiang.xpush.logs.PushLog;
import com.xuexiang.xpush.util.PushUtils;

import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_REGISTER;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_UNREGISTER;
import static com.xuexiang.xpush.core.annotation.ResultCode.RESULT_ERROR;
import static com.xuexiang.xpush.core.annotation.ResultCode.RESULT_OK;

/**
 * 华为推送客户端
 * 1.tag和alias都不支持
 * 2.通知到达事件不支持
 *
 * @author xuexiang
 * @since 2019-08-23 14:29
 */
public class HuaweiPushClient implements IPushClient {
    public static final String HUAWEI_PUSH_PLATFORM_NAME = "HuaweiPush";
    public static final String HUAWEIPUSH_APPID = "HUAWEIPUSH_APPID";
    public static final int HUAWEI_PUSH_PLATFORM_CODE = 1002;

    private Application mApplication;

    /**
     * 初始化
     *
     * @param context
     */
    @Override
    public void init(Context context) {
        if (context instanceof Application) {
            mApplication = (Application) context;
        } else {
            mApplication = (Application) context.getApplicationContext();
        }

    }

    /**
     * 注册推送
     */
    @Override
    public void register() {
        new Thread() {
            @Override
            public void run() {
                try {
                    String appId = AGConnectServicesConfig.fromContext(mApplication).getString("client/app_id");
                    String token = HmsInstanceId.getInstance(mApplication).getToken(appId, "HCM");
                    PushLog.d("get token:" + token);
                    if (!TextUtils.isEmpty(token)) {
                        PushUtils.savePushToken(HUAWEI_PUSH_PLATFORM_NAME, token);
                        XPush.transmitCommandResult(mApplication, TYPE_REGISTER, RESULT_OK, token, null, null);
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 注销推送
     */
    @Override
    public void unRegister() {
        new Thread() {
            @Override
            public void run() {
                try {
                    String appId = AGConnectServicesConfig.fromContext(mApplication).getString("client/app_id");
                    HmsInstanceId.getInstance(mApplication).deleteToken(appId, "HCM");
                    PushLog.d( "deleteToken success.");
                    XPush.transmitCommandResult(mApplication, TYPE_UNREGISTER, RESULT_OK, null, null, null);
                } catch (ApiException e) {
                    XPush.transmitCommandResult(mApplication, TYPE_UNREGISTER, RESULT_ERROR, null, null, "huawei-push unRegister error code : " + e);
                    PushLog.d("huawei-push deleteToken onResult=" + e);
                }
            }
        }.start();
    }

    /**
     * 绑定别名【别名是唯一的】
     *
     * @param alias 别名
     */
    @Override
    public void bindAlias(String alias) {

    }

    /**
     * 解绑别名
     *
     * @param alias 别名
     */
    @Override
    public void unBindAlias(String alias) {

    }

    /**
     * 获取别名
     */
    @Override
    public void getAlias() {

    }

    /**
     * 增加标签
     *
     * @param tag 标签
     */
    @Override
    public void addTags(String... tag) {

    }

    /**
     * 删除标签
     *
     * @param tag 标签
     */
    @Override
    public void deleteTags(String... tag) {

    }

    /**
     * 获取标签
     */
    @Override
    public void getTags() {

    }

    /**
     * @return 获取推送令牌
     */
    @Override
    public String getPushToken() {
        return PushUtils.getPushToken(HUAWEI_PUSH_PLATFORM_NAME);
    }

    /**
     * @return 获取平台码
     */
    @Override
    public int getPlatformCode() {
        return HUAWEI_PUSH_PLATFORM_CODE;
    }

    /**
     * @return 获取平台名
     */
    @Override
    public String getPlatformName() {
        return HUAWEI_PUSH_PLATFORM_NAME;
    }

}
