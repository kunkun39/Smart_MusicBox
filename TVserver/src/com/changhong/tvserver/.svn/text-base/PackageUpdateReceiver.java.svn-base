package com.changhong.tvserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Jack Wang
 */
public class PackageUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TVServer_Package_Update", intent.getAction());

        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED") || intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            MyApplication.generateApplicationInfoIndexJson(context);
        }
    }

}

