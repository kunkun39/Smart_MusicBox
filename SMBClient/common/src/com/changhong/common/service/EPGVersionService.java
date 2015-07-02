package com.changhong.common.service;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jack Wang
 */
public class EPGVersionService {

    private static final String TAG = "EPGVersionService";

    private Context context;

    public EPGVersionService(Context context) {
        this.context = context;
    }

    public void saveEPGVersion(int version) {
        SharedPreferences preferences = context.getSharedPreferences("changhong_epg", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("EPG_VERSION", version);
        editor.commit();
    }

    public int getEPGVersion() {
        SharedPreferences preferences = context.getSharedPreferences("changhong_epg", Context.MODE_PRIVATE);
        return preferences.getInt("EPG_VERSION", -1);
    }

}
