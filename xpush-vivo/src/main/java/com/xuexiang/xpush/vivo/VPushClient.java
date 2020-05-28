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

package com.xuexiang.xpush.vivo;

import android.content.Context;
import android.text.TextUtils;

import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.vivo.push.util.VivoPushException;
import com.xuexiang.xpush.XPush;
import com.xuexiang.xpush.core.IPushClient;
import com.xuexiang.xpush.core.XPushManager;
import com.xuexiang.xpush.util.PushUtils;

import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_ADD_TAG;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_BIND_ALIAS;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_DEL_TAG;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_GET_ALIAS;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_GET_TAG;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_REGISTER;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_UNBIND_ALIAS;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_UNREGISTER;
import static com.xuexiang.xpush.core.annotation.ConnectStatus.DISCONNECT;
import static com.xuexiang.xpush.core.annotation.ResultCode.RESULT_ERROR;
import static com.xuexiang.xpush.core.annotation.ResultCode.RESULT_OK;

/**
 * vivo推送客户端
 *
 * @author xuexiang
 * @since 2019-08-24 19:22
 */
public class VPushClient implements IPushClient {
    public static final String VPUSH_PLATFORM_NAME = "VPush";
    public static final int VPUSH_PLATFORM_CODE = 1006;

    private Context mContext;

    @Override
    public void init(Context context) {
        mContext = context.getApplicationContext();
        PushClient.getInstance(mContext).initialize();
    }

    @Override
    public void register() {
        try {
            PushClient.getInstance(mContext).checkManifest();
        } catch (VivoPushException e) {
            e.printStackTrace();
        }

        if (PushClient.getInstance(mContext).isSupport()) {
            PushClient.getInstance(mContext).turnOnPush(new IPushActionListener() {
                @Override
                public void onStateChanged(int i) {
                    if (i != 0) {
                        XPush.transmitCommandResult(mContext, TYPE_REGISTER, RESULT_ERROR, null, "注册失败", "code=" + i);
                        XPushManager.get().notifyConnectStatusChanged(DISCONNECT);
                    }
                }
            });
        } else {
            XPush.transmitCommandResult(mContext, TYPE_REGISTER, RESULT_ERROR, null, "注册失败", "此设备不支持VPush");
            XPushManager.get().notifyConnectStatusChanged(DISCONNECT);
        }
    }

    @Override
    public void unRegister() {
        String token = getPushToken();
        if (!TextUtils.isEmpty(token)) {
            PushClient.getInstance(mContext).turnOffPush(new IPushActionListener() {
                @Override
                public void onStateChanged(int i) {
                    if (i == 0) {
                        PushUtils.deletePushToken(VPUSH_PLATFORM_NAME);
                        XPush.transmitCommandResult(mContext, TYPE_UNREGISTER, RESULT_OK, null, "注销成功", null);
                        XPushManager.get().notifyConnectStatusChanged(DISCONNECT);
                    } else {
                        XPush.transmitCommandResult(mContext, TYPE_UNREGISTER, RESULT_ERROR, null, "注销失败", "code=" + i);
                    }
                }
            });
        }
    }

    @Override
    public void bindAlias(String alias) {
        PushClient.getInstance(mContext).bindAlias(alias, new IPushActionListener() {
            @Override
            public void onStateChanged(int i) {
                if (i == 0) {
                    XPush.transmitCommandResult(mContext, TYPE_BIND_ALIAS,
                            RESULT_OK, null, "绑定别名成功", null);
                } else {
                    XPush.transmitCommandResult(mContext, TYPE_BIND_ALIAS,
                            RESULT_ERROR, null, "绑定别名失败", "code:" + i);
                }
            }
        });
    }

    @Override
    public void unBindAlias(String alias) {
        PushClient.getInstance(mContext).unBindAlias(alias, new IPushActionListener() {
            @Override
            public void onStateChanged(int i) {
                if (i == 0) {
                    XPush.transmitCommandResult(mContext, TYPE_UNBIND_ALIAS,
                            RESULT_OK, null, "解除绑定别名成功", null);
                } else {
                    XPush.transmitCommandResult(mContext, TYPE_UNBIND_ALIAS,
                            RESULT_ERROR, null, "解除绑定别名失败", "code:" + i);
                }
            }
        });

    }

    @Override
    public void getAlias() {
        String alias = PushClient.getInstance(mContext).getAlias();
        XPush.transmitCommandResult(mContext, TYPE_GET_ALIAS,
                RESULT_OK, alias, null, null);
    }

    @Override
    public void addTags(String... tag) {
        XPush.transmitCommandResult(mContext, TYPE_ADD_TAG, RESULT_ERROR, null,
                null, mContext.getString(R.string.vivo_seting_fail));
    }

    @Override
    public void deleteTags(String... tag) {
        XPush.transmitCommandResult(mContext, TYPE_DEL_TAG, RESULT_ERROR, null,
                null, mContext.getString(R.string.vivo_seting_fail));
    }

    @Override
    public void getTags() {
        XPush.transmitCommandResult(mContext, TYPE_GET_TAG, RESULT_ERROR, null,
                null, mContext.getString(R.string.vivo_seting_fail));
    }

    @Override
    public String getPushToken() {
        return PushUtils.getPushToken(VPUSH_PLATFORM_NAME);
    }

    @Override
    public int getPlatformCode() {
        return VPUSH_PLATFORM_CODE;
    }

    @Override
    public String getPlatformName() {
        return VPUSH_PLATFORM_NAME;
    }

}
