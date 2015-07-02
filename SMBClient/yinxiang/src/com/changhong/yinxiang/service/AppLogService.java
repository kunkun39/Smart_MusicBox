package com.changhong.yinxiang.service;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Created by Jack Wang
 */
public class AppLogService {

    private static final String TAG = "AppLogService";

    private Context context;

    public AppLogService(Context context) {
        this.context = context;
    }

    public boolean isUserAlreadyEntrance() {
        SharedPreferences preferences = context.getSharedPreferences("changhong_firstentrance", Context.MODE_PRIVATE);
        boolean alreadyEntrance = preferences.getBoolean("ALREADY_ENTRANCE", false);
        if (alreadyEntrance) {
            return true;
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("ALREADY_ENTRANCE", true);
        editor.commit();
        return false;
    }
}
