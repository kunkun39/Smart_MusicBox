package com.changhong.tvserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private static final String TAG = "TVserver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "android.intent.action.BOOT_COMPLETED");
        Intent mIntent = new Intent(context, TVSocketControllerService.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.putExtra("message", 1);
        context.startService(mIntent);
    }
}

