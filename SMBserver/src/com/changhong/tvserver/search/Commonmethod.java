package com.changhong.tvserver.search;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

public class Commonmethod {

	
	 /**
     * 判断某个界面是否在前台
     * 
     * @param context
     * @param className
     *            某个界面名称
     */
	public static boolean isActivityForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            String topActivity=cpn.getClassName();
            if (className.equals(topActivity)) {
                return false;
            }
        }

        return true;
    }
}
