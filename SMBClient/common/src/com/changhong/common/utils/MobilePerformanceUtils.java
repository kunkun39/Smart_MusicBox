package com.changhong.common.utils;

import android.content.Context;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by Jack Wang
 */
public class MobilePerformanceUtils {

    private static String TAG = "MobilePerformance";

    public static boolean isPowerFully = false;

    /**
     * 判断服务器是否为正在使用的状态
     */
    public static boolean httpServerUsing = false;

    /**
     * 判断是否在直播分享
     */
    public static boolean sharingDVBPlaying = false;

    /**
     * 判断是否用户在操作键盘和上次操作的时间
     */
    public static boolean sharingRemoteControlling = false;
    public static long sharingRemoteControlLastHappen = 0L;

    /**
     * 这个方法是用于管理我们工程省电，当我们在下面几种情况下，Wake Lock需要，WIFI不用SLEEP
     * 1 - 如果用户在投影
     * 2 - 用户在观看直播分享
     * 3 - 用户在操作遥控器
     */
    public static void openPerformance(Context context) {
        if (!isPowerFully) {
            isPowerFully = true;
            acquireWakeLock(context);
            acquireWifiAlwaysOn(context);
            Log.i("Performance_M", "start power");
        }
    }

    /**
     * 这个方法是用于管理我们工程省电，当我们没有在下面几种情况下，Wake Lock不需要，WIFI设置可以SLEEP
     * 1 - 如果用户在投影
     * 2 - 用户在观看直播分享
     * 3 - 用户在操作遥控器
     */
    public static void closePerformance(Context context) {
        Log.i("Performance_M", "httpServerUsing=" + httpServerUsing + "|sharingDVBPlaying=" + sharingDVBPlaying + "|sharingRemoteControlling=" + sharingRemoteControlling + "|isPowerFully=" + isPowerFully);
        if (!httpServerUsing && !sharingDVBPlaying && !sharingRemoteControlling && isPowerFully) {
            isPowerFully = false;
            releaseWakeLock();
            releaseWifiOn(context);
            Log.i("Performance_M", "stop power");
        }
    }

    private static PowerManager.WakeLock wakeLock;

    //申请设备电源锁
    private static void acquireWakeLock(Context context) {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    //释放设备电源锁
    private static void releaseWakeLock() {
        if (null != wakeLock) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    /**
     * 申请设备WIFI火力全开
     */
    private static void acquireWifiAlwaysOn(Context context) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_NEVER);
    }

    /**
     * 申请设备WIFI可以休息
     */
    private static void releaseWifiOn(Context context) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
    }
}
