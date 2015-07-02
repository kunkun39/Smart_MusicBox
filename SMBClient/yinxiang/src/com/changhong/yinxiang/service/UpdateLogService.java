package com.changhong.yinxiang.service;

import android.content.Context;
import android.content.SharedPreferences;
import com.changhong.common.utils.DateUtils;

import java.util.Date;

/**
 * Created by Jack Wang
 */
public class UpdateLogService {

    private static final String TAG = "UpdateLogService";

    private Context context;

    public UpdateLogService(Context context) {
        this.context = context;
    }

    public void saveUpdateDate() {
        SharedPreferences preferences = context.getSharedPreferences("changhong_setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("UPDATE_DATE", DateUtils.to10String(new Date()));
        editor.commit();
    }

    public String getUpdateDate() {
        SharedPreferences preferences = context.getSharedPreferences("changhong_setting", Context.MODE_PRIVATE);
        return preferences.getString("UPDATE_DATE", "");
    }

    /**
     * 保存更新是否出错
     */
    public void saveDownloadException(boolean exception) {
        SharedPreferences preferences = context.getSharedPreferences("changhong_setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean("DOWNLOAD_EXCEPTION", exception);
        editor.commit();
    }

    /**
     * 判断下载是否有异常
     */
    public boolean isDownloadingException() {
        SharedPreferences preferences = context.getSharedPreferences("changhong_setting", Context.MODE_PRIVATE);
        return preferences.getBoolean("DOWNLOAD_EXCEPTION", false);
    }

    /**
     * 保存线程下载文件的字节数
     */
    public void saveThreadDownloadDataSize(int threadNumber, long alreadyDownloadSize) {
        SharedPreferences preferences = context.getSharedPreferences("changhong_setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putLong("THREAD_NUMBER_" + threadNumber, alreadyDownloadSize);
        editor.commit();
    }

    /**
     * 获得线程下载文件的字节数
     */
    public long getThreadDownloadDataSize(int threadNumber) {
        SharedPreferences preferences = context.getSharedPreferences("changhong_setting", Context.MODE_PRIVATE);
        return preferences.getLong("THREAD_NUMBER_" + threadNumber, 0);
    }

    /**
     * 获得所有线程下载文件的总数
     */
    public long getTotalDownlaodDataSize() {
        SharedPreferences preferences = context.getSharedPreferences("changhong_setting", Context.MODE_PRIVATE);
        long first =  preferences.getLong("THREAD_NUMBER_1", 0);
        long second =  preferences.getLong("THREAD_NUMBER_2", 0);
        return first + second;
    }
}
