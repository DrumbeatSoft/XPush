package com.xuexiang.xpush;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.xuexiang.xpush.logs.PushLog;
import com.xuexiang.xpush.util.RomUtils;

/**
 * 设置桌面角标（测试类）
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
            showToast(context, "Write unread number FAILED!!! e = " + e);
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

    private static void showToast(Context context, String msg) {
        Toast.makeText(context, String.valueOf(msg), Toast.LENGTH_LONG).show();
    }
}
