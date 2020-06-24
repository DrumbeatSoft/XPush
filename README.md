# XPush

在[XPush](https://github.com/xuexiangjys/XPush)的基础上，新增oppo推送、vivo推送，重新集成华为推送，增加自定义角标功能，自定义通知工具增加渠道设置功能
	
## 快速集成指南
	
### 添加Gradle依赖

1.先在项目根目录的 build.gradle 的 repositories 添加:
```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
    华为推送还需要在APP项目路径根目录下配置签名证书指纹agconnect-services.json文件、在build.gradle条件gradle插件
```
apply plugin: 'com.android.application'
apply plugin: 'com.huawei.agconnect'
```
2.添加XPush主要依赖:

```
dependencies {
  ...
  //推送核心库
  implementation 'com.github.DrumbeatSoft.XPush:xpush-core:1.0.1'
  //推送保活库
  implementation 'com.github.DrumbeatSoft.XPush:keeplive:1.0.1'
}
```

3.添加第三方推送依赖（根据自己的需求进行添加，当然也可以全部添加）

```
dependencies {
  ...
  //选择你想要集成的推送库
  implementation 'com.github.DrumbeatSoft.XPush:xpush-jpush:1.0.1'
  implementation 'com.github.DrumbeatSoft.XPush:xpush-umeng:1.0.1'
  implementation 'com.github.DrumbeatSoft.XPush:xpush-huawei:1.0.1'
  implementation 'com.github.DrumbeatSoft.XPush:xpush-xiaomi:1.0.1'
  implementation 'com.github.DrumbeatSoft.XPush:xpush-xg:1.0.1'
  implementation 'com.github.DrumbeatSoft.XPush:xpush-oppo:1.0.1'
}
```
4.华为的比较个性，如果使用华为还需要在项目根目录的 build.gradle 的 repositories、dependencies 添加
```
buildscript {
    repositories {
        //如果使用华为推送的话
        maven { url "http://developer.huawei.com/repo/" }
    }
    dependencies {
        //如果使用华为推送的话
        classpath 'com.huawei.agconnect:agcp:1.3.1.300'
    }
}
```
还需要[配置签名证书指纹](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/Preparations#h2-1575616896242)，里边包含AppKey所以不需要再AndroidManifest.xml配置AppKey

### 初始化XPush配置

1.注册消息推送接收器。方法有两种，选其中一种就行了。

* 如果你想使用`XPushManager`提供的消息管理，直接在AndroidManifest.xml中注册框架默认提供的`XPushReceiver`。当然你也可以继承`XPushReceiver`，并重写相关方法。

* 如果你想实现自己的消息管理，可继承`AbstractPushReceiver`类，重写里面的方法，并在AndroidManifest.xml中注册。

```
    <!--自定义消息推送接收器-->
    <receiver android:name=".push.CustomPushReceiver">
        <intent-filter>
            <action android:name="com.xuexiang.xpush.core.action.RECEIVE_CONNECT_STATUS_CHANGED" />
            <action android:name="com.xuexiang.xpush.core.action.RECEIVE_NOTIFICATION" />
            <action android:name="com.xuexiang.xpush.core.action.RECEIVE_NOTIFICATION_CLICK" />
            <action android:name="com.xuexiang.xpush.core.action.RECEIVE_MESSAGE" />
            <action android:name="com.xuexiang.xpush.core.action.RECEIVE_COMMAND_RESULT" />

            <category android:name="${applicationId}" />
        </intent-filter>
    </receiver>

    <!--默认的消息推送接收器-->
    <receiver android:name="com.xuexiang.xpush.core.receiver.impl.XPushReceiver">
        <intent-filter>
            <action android:name="com.xuexiang.xpush.core.action.RECEIVE_CONNECT_STATUS_CHANGED" />
            <action android:name="com.xuexiang.xpush.core.action.RECEIVE_NOTIFICATION" />
            <action android:name="com.xuexiang.xpush.core.action.RECEIVE_NOTIFICATION_CLICK" />
            <action android:name="com.xuexiang.xpush.core.action.RECEIVE_MESSAGE" />
            <action android:name="com.xuexiang.xpush.core.action.RECEIVE_COMMAND_RESULT" />

            <category android:name="${applicationId}" />
        </intent-filter>
    </receiver>
```

注意，如果你的Android设备是8.0及以上的话，静态注册的广播是无法正常生效的，解决的方法有两种：

* 动态注册消息推送接收器

* 修改推送消息的发射器

```
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    //Android8.0静态广播注册失败解决方案一：动态注册
    XPush.registerPushReceiver(new CustomPushReceiver());

    //Android8.0静态广播注册失败解决方案二：修改发射器
    XPush.setIPushDispatcher(new Android26PushDispatcherImpl(CustomPushReceiver.class));
}
```

2.在AndroidManifest.xml的application标签下，添加第三方推送客户端实现类.

需要注意的是，这里注册的`PlatformName`和`PlatformCode`必须要和推送客户端实现类中的一一对应才行。

```
<!--name格式：XPush_[PlatformName]_[PlatformCode]-->
<!--value格式：对应客户端实体类的全类名路径-->

<!--如果引入了xpush-jpush库-->
<meta-data
    android:name="XPush_JPush_1000"
    android:value="com.xuexiang.xpush.jpush.JPushClient" />

<!--如果引入了xpush-umeng库-->
<meta-data
    android:name="XPush_UMengPush_1001"
    android:value="com.xuexiang.xpush.umeng.UMengPushClient" />
    
<!--如果引入了xpush-huawei库-->
<meta-data
    android:name="XPush_HuaweiPush_1002"
    android:value="com.xuexiang.xpush.huawei.HuaweiPushClient" />

<!--如果引入了xpush-xiaomi库-->
<meta-data
    android:name="XPush_MIPush_1003"
    android:value="com.xuexiang.xpush.xiaomi.XiaoMiPushClient" />
    
<!--如果引入了xpush-xg库-->
<meta-data
    android:name="XPush_XGPush_1004"
    android:value="@string/xpush_xg_client_name" />

<!--如果引入了xpush-oppo库-->
<meta-data
    android:name="XPush_OPush_1005"
    android:value="@string/xpush_oppo_client_name" />

<!--如果引入了xpush-vivo库-->
<meta-data
     android:name="XPush_VPush_1006"
     android:value="@string/xpush_vivo_client_name" />
```

3.添加第三方AppKey和AppSecret.

这里的AppKey和AppSecret需要我们到各自的推送平台上注册应用后获得。注意如果使用了xpush-xiaomi,那么需要在AndroidManifest.xml添加小米的AppKey和AppSecret（注意下面的“\ ”必须加上，否则获取到的是float而不是String，就会导致id和key获取不到正确的数据）

```
<!--极光推送静态注册-->
<meta-data
    android:name="JPUSH_CHANNEL"
    android:value="default_developer" />
<meta-data
    android:name="JPUSH_APPKEY"
    android:value="a32109db64ebe04e2430bb01" />

<!--友盟推送静态注册-->
<meta-data
    android:name="UMENG_APPKEY"
    android:value="5d5a42ce570df37e850002e9" />
<meta-data
    android:name="UMENG_MESSAGE_SECRET"
    android:value="4783a04255ed93ff675aca69312546f4" />
    
<!--小米推送静态注册，下面的“\ ”必须加上，否则将无法正确读取-->
<meta-data
    android:name="MIPUSH_APPID"
    android:value="\ 2882303761518134164"/>
<meta-data
    android:name="MIPUSH_APPKEY"
    android:value="\ 5371813415164"/>
    
<!--信鸽推送静态注册-->
<meta-data
    android:name="XGPUSH_ACCESS_ID"
    android:value="2100343759" />
<meta-data
    android:name="XGPUSH_ACCESS_KEY"
    android:value="A7Q26I8SH7LV" />

<!--oppo推送静态注册-->
<meta-data
    android:name="OPUSH_APPKEY"
    android:value="bf39531e59634188a380af6021129ccc" />
<meta-data
    android:name="OPUSH_SECRET"
    android:value="27bd7eb895c94d0a8ba35619b3f44ecd" />
```

4.在Application中初始化XPush

初始化XPush的方式有两种，根据业务需要选择一种方式就行了：

* 静态注册

```
/**
 * 静态注册初始化推送
 */
private void initPush() {
    XPush.debug(BuildConfig.DEBUG);
    //静态注册，指定使用友盟推送客户端
    XPush.init(this, new UMengPushClient());
    XPush.register();
}
```

* 动态注册

```
/**
 * 动态注册初始化推送
 */
private void initPush() {
    XPush.debug(BuildConfig.DEBUG);
    //动态注册，根据平台名或者平台码动态注册推送客户端
        XPush.init(this, new IPushInitCallback() {
            @Override
            public boolean onInitPush(int platformCode, String platformName) {
                String equalStr = platformCode + platformName;
                if (RomUtils.isOppo()) {
                    return TextUtils.equals(equalStr, OPushClient.OPUSH_PLATFORM_CODE + OPushClient.OPUSH_PLATFORM_NAME);
                } else if (RomUtils.isVivo()) {
                    return TextUtils.equals(equalStr, VPushClient.VPUSH_PLATFORM_CODE + VPushClient.VPUSH_PLATFORM_NAME);
                } else if (RomUtils.isXiaomi()) {
                    return TextUtils.equals(equalStr, XiaoMiPushClient.MIPUSH_PLATFORM_CODE + XiaoMiPushClient.MIPUSH_PLATFORM_NAME);
                } else if (RomUtils.isHuawei()) {
                    return TextUtils.equals(equalStr, HuaweiPushClient.HUAWEI_PUSH_PLATFORM_CODE + HuaweiPushClient.HUAWEI_PUSH_PLATFORM_NAME);
                } else {
                    return TextUtils.equals(equalStr, JPushClient.JPUSH_PLATFORM_CODE + JPushClient.JPUSH_PLATFORM_NAME);
                }
            }
        });
    XPush.register();
}
```

---

## 如何使用XPush

### 1、推送的注册和注销

* 通过调用`XPush.register()`，即可完成推送的注册。

* 通过调用`XPush.unRegister()`，即可完成推送的注销。

* 通过调用`XPush.getPushToken()`，即可获取消息推送的Token(令牌)。

* 通过调用`XPush.getPlatformCode()`，即可获取当前使用推送平台的码。

### 2、推送的标签（tag）处理

* 通过调用`XPush.addTags()`，即可添加标签（支持传入多个）。

* 通过调用`XPush.deleteTags()`，即可删除标签（支持传入多个）。

* 通过调用`XPush.getTags()`，即可获取当前设备所有的标签。

需要注意的是，友盟推送和信鸽推送目前暂不支持标签的获取，华为推送不支持标签的所有操作，小米推送每次只支持一个标签的操作。

### 3、推送的别名（alias）处理

* 通过调用`XPush.bindAlias()`，即可绑定别名。

* 通过调用`XPush.unBindAlias()`，即可解绑别名。

* 通过调用`XPush.getAlias()`，即可获取当前设备所绑定的别名。

需要注意的是，友盟推送和信鸽推送目前暂不支持别名的获取，华为推送不支持别名的所有操作。

### 4、推送消息的接收

* 通过调用`XPushManager.get().register()`方法，注册消息订阅`MessageSubscriber`，即可在任意地方接收到推送的消息。

* 通过调用`XPushManager.get().unregister()`方法，即可取消消息的订阅。

这里需要注意的是，消息订阅的回调并不一定是在主线程，因此在回调中如果进行了UI的操作，一定要确保切换至主线程。下面演示代码中使用了我的另一个开源库[XAOP](https://github.com/xuexiangjys/XAOP),只通过`@MainThread`注解就能自动切换至主线程,可供参考。

```
/**
 * 初始化监听
 */
@Override
protected void initListeners() {
    XPushManager.get().register(mMessageSubscriber);
}

private MessageSubscriber mMessageSubscriber = new MessageSubscriber() {
    @Override
    public void onMessageReceived(CustomMessage message) {
        showMessage(String.format("收到自定义消息:%s", message));
    }

    @Override
    public void onNotification(Notification notification) {
        showMessage(String.format("收到通知:%s", notification));
    }
};

@MainThread
private void showMessage(String msg) {
    tvContent.setText(msg);
}


@Override
public void onDestroyView() {
    XPushManager.get().unregister(mMessageSubscriber);
    super.onDestroyView();
}
```

### 5、推送消息的过滤处理

* 通过调用`XPushManager.get().addFilter()`方法，可增加对订阅推送消息的过滤处理。对于一些我们不想处理的消息，可以通过消息过滤器将它们筛选出来。

* 通过调用`XPushManager.get().removeFilter()`方法，即可去除消息过滤器。

```
/**
 * 初始化监听
 */
@Override
protected void initListeners() {
    XPushManager.get().addFilter(mMessageFilter);
}

private IMessageFilter mMessageFilter = new IMessageFilter() {
    @Override
    public boolean filter(Notification notification) {
        if (notification.getContent().contains("XPush")) {
            showMessage("通知被拦截");
            return true;
        }
        return false;
    }

    @Override
    public boolean filter(CustomMessage message) {
        if (message.getMsg().contains("XPush")) {
            showMessage("自定义消息被拦截");
            return true;
        }
        return false;
    }
};

@Override
public void onDestroyView() {
    XPushManager.get().removeFilter(mMessageFilter);
    super.onDestroyView();
}
```

### 6、推送通知的点击处理

> 对于通知的点击事件，我们可以处理得更优雅，自定义其点击后的动作，打开我们想让用户看到的页面。

我们可以在全局消息推送的接收器`IPushReceiver`中的`onNotificationClick`回调中，增加打开指定页面的操作。

```
@Override
public void onNotificationClick(Context context, XPushMsg msg) {
    super.onNotificationClick(context, msg);
    //打开自定义的Activity
    Intent intent = IntentUtils.getIntent(context, TestActivity.class, null, true);
    intent.putExtra(KEY_PARAM_STRING, msg.getContent());
    intent.putExtra(KEY_PARAM_INT, msg.getId());
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    ActivityUtils.startActivity(intent);
}
```

需要注意的是，这需要你在消息推送平台推送的通知使用的是`自定义动作`或者`打开指定页面`类型，并且传入的Intent uri 内容满足如下格式:

* title：通知的标题

* content：通知的内容

* extraMsg：通知附带的拓展字段，可存放json或其他内容

* keyValue：通知附带的键值对

```
xpush://com.xuexiang.xpush/notification?title=这是一个通知&content=这是通知的内容&extraMsg=xxxxxxxxx&keyValue={"param1": "1111", "param2": "2222"}
```

当然你也可以自定义传入的Intent uri 格式，具体可参考项目中的[XPushNotificationClickActivity](https://github.com/xuexiangjys/XPush/blob/master/xpush-core/src/main/java/com/xuexiang/xpush/core/XPushNotificationClickActivity.java)和[AndroidManifest.xml](https://github.com/xuexiangjys/XPush/blob/master/xpush-core/src/main/AndroidManifest.xml)

### 7、渠道设置
oppo推送的渠道设置需要在oppo推送运营平台 配置管理->新建通道 中进行设置，并且APP的的渠道设置需要和oppo推送运营平台中的渠道保持一致

```
        NotificationUtils.addChannel(String channelId, String channelName, String channelDescription);
```

### 8、自定义角标

目前只支持oppo、vivo的角标设置，需要在AndroidManifest.xml文件中配置，且oppo角标设置需要申请权限
```
        <!--oppo、vivo启动图标角标-->
        <meta-data
            android:name="APPLICATION_ID"
            android:value="${applicationId}" />
        <meta-data
            android:name="LAUNCHER_CLASS_PATH"
            android:value="您申请oppo角标权限时的主界面路径如：com.xxx.xxx.SplashActivity" />
```

```
AppBadgeUtils.setAppBadge(Context context, int count);
```

## 实体介绍

### XPushMsg

> 推送消息转译实体，携带消息的原始数据

字段名 | 类型 | 备注
:-|:-:|:-
mId | int | 消息ID / 状态
mTitle | String | 通知标题
mContent | String | 通知内容
mMsg | String | 自定义（透传）消息
mExtraMsg | String | 消息拓展字段
mKeyValue | String | 消息键值对


### Notification

> 推送通知，由XPushMsg转化而来

字段名 | 类型 | 备注
:-|:-:|:-
mId | int | 消息ID / 状态
mTitle | String | 通知标题
mContent | String | 通知内容
mExtraMsg | String | 消息拓展字段
mKeyValue | String | 消息键值对


### CustomMessage

> 自定义（透传）消息，由XPushMsg转化而来

字段名 | 类型 | 备注
:-|:-:|:-
mMsg | String | 自定义（透传）消息
mExtraMsg | String | 消息拓展字段
mKeyValue | String | 消息键值对


### XPushCommand

> IPushClient执行相关命令的结果信息实体

字段名 | 类型 | 备注
:-|:-:|:-
mType | int | 命令类型
mResultCode | int | 结果码
mContent | String | 命令内容
mExtraMsg | String | 拓展字段
mError | String | 错误信息


## 常量介绍

### CommandType

> 命令的类型

命令名 | 命令码 | 备注
:-|:-:|:-
TYPE_REGISTER | 2000 | 注册推送
TYPE_UNREGISTER | 2001 | 注销推送
TYPE_ADD_TAG | 2002 | 添加标签
TYPE_DEL_TAG | 2003 | 删除标签
TYPE_GET_TAG | 2004 | 获取标签
TYPE_BIND_ALIAS | 2005 | 绑定别名
TYPE_UNBIND_ALIAS | 2006 | 解绑别名
TYPE_GET_ALIAS | 2007 | 获取别名
TYPE_AND_OR_DEL_TAG | 2008 | 添加或删除标签

### ResultCode

> 命令的结果码

结果名 | 结果码 | 备注
:-|:-:|:-
RESULT_OK | 0 | 成功
RESULT_ERROR | 1 | 失败

### ConnectStatus

> 推送连接状态

状态名 | 状态码 | 备注
:-|:-:|:-
DISCONNECT | 10 | 已断开
CONNECTING | 11 | 连接中
CONNECTED | 12 | 已连接

---
