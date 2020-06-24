package com.xuexiang.xpush;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.xuexiang.xpush.logs.PushLog;
import com.xuexiang.xpush.util.RomUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 设置桌面角标
 *
 * @author ZuoHailong
 * @date 2020/4/20
 */
public class AppBadgeUtils {

    /**
     * 设置应用桌面角标
     *
     * @param context
     */
    public static void setAppBadge(Context context, int count) {

        /**
         * 应用ID
         */
        String applicationId = null;
        /**
         * 启动页路径，形如 com.drumbeat.project.splashActivity
         */
        String launcherClassPath = null;

        try {
            Bundle metaData = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData;
            applicationId = metaData.getString("APPLICATION_ID").trim();
            launcherClassPath = metaData.getString("LAUNCHER_CLASS_PATH").trim();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            PushLog.e("can't find APPLICATION_ID or LAUNCHER_CLASS_PATH in AndroidManifest.xml");
        } catch (NullPointerException e) {
            e.printStackTrace();
            PushLog.e("can't find APPLICATION_ID or LAUNCHER_CLASS_PATH in AndroidManifest.xml");
        }

        if (RomUtils.isOppo()) {
            setOppo(context, count);
        } else if (RomUtils.isVivo()) {
            setVivo(context, count, applicationId, launcherClassPath);
        } else if (RomUtils.isHuawei()) {
            setHuaWei(context, count, applicationId, launcherClassPath);
        }
    }

    /**
     * OPPO
     *
     * @param context
     * @param count
     */
    private static void setOppo(Context context, int count) {
        try {
            Bundle extras = new Bundle();
            extras.putInt("app_badge_count", count);
            context.getContentResolver().call(
                    Uri.parse("content://com.android.badge/badge"),
                    "setAppBadgeCount", null, extras);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * vivo
     *
     * @param context
     * @param count
     * @param applicationId     应用ID
     * @param launcherClassPath 启动页路径，形如 com.drumbeat.project.splashActivity
     */
    @SuppressLint("WrongConstant")
    private static void setVivo(Context context, int count, String applicationId, String launcherClassPath) {
        Intent intent = new Intent();
        intent.setAction("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
        intent.putExtra("packageName", applicationId);
        intent.putExtra("className", launcherClassPath);
        intent.putExtra("notificationNum", count);
        intent.addFlags(0x01000000);
        context.sendBroadcast(intent);
    }

    /**
     * huawei
     *
     * @param context
     * @param count
     * @param applicationId     应用ID
     * @param launcherClassPath 启动页路径，形如 com.drumbeat.project.splashActivity
     */
    private static void setHuaWei(Context context, int count, String applicationId, String launcherClassPath) {
        try {
            Bundle bunlde = new Bundle();
            bunlde.putString("package", applicationId);
            bunlde.putString("class", launcherClassPath);
            bunlde.putInt("badgenumber", count);
            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bunlde);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 小米推送设置角标，只能在发通知的时候在通知里设置，且设置的是多少在通知被删除、点击的时候角标数量就减少多少，
     * 应用在前台不展示角标、应用进程杀掉角标消失
     *
     * @param count
     * @param notification
     */
    public static void setXiaomiBadgeUtils(int count, Notification notification) {
        try {
            Field field = notification.getClass().getDeclaredField("extraNotification");
            Object extraNotification = field.get(notification);
            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
            method.invoke(extraNotification, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
