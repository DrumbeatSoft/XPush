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

import com.heytap.msp.push.mode.DataMessage;
import com.heytap.msp.push.service.DataMessageCallbackService;
import com.xuexiang.xpush.XPush;
import com.xuexiang.xpush.logs.PushLog;

/**
 * OPPO推送消息接收器
 * 1、PushMessageReceiver 是个抽象类，该类继承了 BroadcastReceiver。
 * 2、需要将自定义的 XiaoMiPushReceiver 注册在 AndroidManifest.xml 文件中.
 * 3、XiaoMiPushReceiver 的 onReceivePassThroughMessage 方法用来接收服务器向客户端发送的透传消息。
 * 4、XiaoMiPushReceiver 的 onNotificationMessageClicked 方法用来接收服务器向客户端发送的通知消息，这个回调方法会在用户手动点击通知后触发。
 * 5、XiaoMiPushReceiver 的 onNotificationMessageArrived 方法用来接收服务器向客户端发送的通知消息，这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。
 * 6、XiaoMiPushReceiver 的 onCommandResult 方法用来接收客户端向服务器发送命令后的响应结果。
 * 7、XiaoMiPushReceiver 的 onReceiveRegisterResult 方法用来接收客户端向服务器发送注册命令后的响应结果。
 * 8、以上这些方法运行在非 UI 线程中。
 *
 * @author wangyu
 * @since 2019-08-24 18:23
 */
public class OppoPushDataService extends DataMessageCallbackService {
    private static final String TAG = "OppoPush-";
    @Override
    public void processMessage(Context context, DataMessage dataMessage) {
        super.processMessage(context, dataMessage);

        PushLog.d(TAG + dataMessage.toString());
        XPush.transmitMessage(context, dataMessage.getContent(), dataMessage.getDescription(),null);
    }
}
