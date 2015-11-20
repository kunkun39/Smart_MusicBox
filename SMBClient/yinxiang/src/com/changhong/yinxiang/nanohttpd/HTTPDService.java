package com.changhong.yinxiang.nanohttpd;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack Wang
 */
public class HTTPDService extends Service {

    public final static int HTTP_PORT = 8888;
//    public final static int HTTP_PORT = 8899;

    /**
     * 默认的手机存储目录
     */
    public static String defaultHttpServerPath = null;

    /**
     * 手机所有的外设目录，包括手机的内存卡和多个SDCARD
     */
    public static List<String> otherHttpServerPaths = new ArrayList<String>();

    public static HTTPD httpServer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * 获得所有的虚拟目录
         */
        StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        try {
            String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths", null).invoke(sm, null);
            for (int i = 0; i < paths.length; i++) {
                String directory = paths[i];
                if (new File(directory).canRead() && new File(directory).canWrite()) {
                    Log.e("HTTPD", "start wwwroot with this path");
                    otherHttpServerPaths.add(directory);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 设置HTTPD所有的虚拟目录和根目录
         */
        defaultHttpServerPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File wwwroot = new File(defaultHttpServerPath).getAbsoluteFile();

        otherHttpServerPaths.remove(defaultHttpServerPath);
        List<File> otherHttpdPaths = new ArrayList<File>();
        for (String other : otherHttpServerPaths) {
            otherHttpdPaths.add(new File(other).getAbsoluteFile());
        }

        try {
            httpServer = new HTTPD(HTTP_PORT, wwwroot, otherHttpdPaths);
            Log.e("HTTPD", "start http server");
        } catch (IOException ioe) {
            Log.e("HTTPD", "Couldn't start server:\n" + ioe);
            System.exit(-1);
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        if (httpServer != null) {
            httpServer.stop();
            httpServer = null;
            Log.e("HTTPD", "stop http server");
        }

        super.onDestroy();
    }
}
