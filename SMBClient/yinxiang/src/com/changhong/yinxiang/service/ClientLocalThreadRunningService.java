package com.changhong.yinxiang.service;

import android.app.*;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.util.Log;
import com.changhong.common.db.sqlite.DatabaseContainer;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.service.EPGVersionService;
import com.changhong.common.system.AppConfig;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.MobilePerformanceUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.utils.WebUtils;
import com.changhong.yinxiang.nanohttpd.HTTPDService;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by Jack Wang
 */
public class ClientLocalThreadRunningService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initThreads();
    }

    private void initThreads() {
        /**
         * 启动HTTPD服务监视器
         */
        new HttpServerMonitorThread(ClientLocalThreadRunningService.this).start();

        /**
         * 性能监控
         */
        new PerformanceMonitorThread(ClientLocalThreadRunningService.this).start();

        /**
         * 启动缓存图片
         */
        new PictureCacheForLocalThread().start();

    }


    /**
     * ******************************************http server monitor  thread******************************************
     */

    /**
     * 说明：进入投影界面的时候，打开此服务, 还有就是用户一直处于投影界面，然而这个时候服务已经关闭，但是用户现在点击投影，所以这里需要先检查有没有HTTP服务， 如果没有就需要开启
     * <p/>
     * <p/>
     * 1 - 这个线程是用来检查是否HTTP服务还在使用 1个小时一次
     * 2 - 如果用户处于投影图片，视频播放，音乐播放时NanoHTTPDService.serverUsing = true的时候，不会停止该服务
     * 3 - 当图片，视频，音乐播放播放结束后，NanoHTTPDService.serverUsing = false，就会停止该服务
     */
    private class HttpServerMonitorThread extends Thread {

        private Context context;

        private HttpServerMonitorThread(Context context) {
            this.context = context;
        }

        public void run() {
            while (true) {
                if (HTTPDService.httpServer != null && !MobilePerformanceUtils.httpServerUsing) {
                    /**
                     * 启动Http服务
                     */
                    Intent http = new Intent(context, HTTPDService.class);
                    stopService(http);
                }
                SystemClock.sleep(1000 * 60 * 60);
            }
        }
    }

    /**
     * ******************************************performance manager thread ********************************************
     */

    /**
     * @link {com.changhong.common.utils.MobilePerformanceUtils}
     */
    private class PerformanceMonitorThread extends Thread {

        private Context context;

        private PerformanceMonitorThread(Context context) {
            this.context = context;
        }

        public void run() {
            while (true) {
                /**
                 * 如果点击屏幕已经操作了三分钟
                 */
                if (MobilePerformanceUtils.sharingRemoteControlling) {
                    Long remoteControlDuringTime = System.currentTimeMillis() - MobilePerformanceUtils.sharingRemoteControlLastHappen;
                    if ((remoteControlDuringTime - 1000 * 60 * 3) > 0) {
                        MobilePerformanceUtils.sharingRemoteControlling = false;
                    }
                }

                MobilePerformanceUtils.closePerformance(context);
                SystemClock.sleep(1000 * 60);
            }
        }
    }

    /**
     * ******************************************picture for local cache thread************************************
     */

    class PictureCacheForLocalThread extends Thread {
        @Override
        public void run() {
            Map<String, List<String>> packageList = new HashMap<String, List<String>>();

            try {
                //sleep for 1 seconds for http server started
                Thread.sleep(1000);

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = ClientLocalThreadRunningService.this.getContentResolver();

                // search images
                Cursor cursor = contentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?", new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED + " desc");

                //put image urls into json object
                while (cursor.moveToNext()) {
                    String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    String[] tokens = StringUtils.delimitedListToStringArray(imagePath, File.separator);
                    String packageName = tokens[tokens.length - 2];

                    //组装相同路径下的package
                    List<String> files = packageList.get(packageName);
                    if (files == null) {
                        files = new ArrayList<String>();
                    }
                    if (AppConfig.MOBILE_CARMERS_PACKAGE.contains(packageName.toLowerCase()) || files.size() < 4) {
                        files.add(imagePath);
                    }
                    packageList.put(packageName, files);
                }
                cursor.close();

                //begin to cache
                Set<String> keys = packageList.keySet();
                if (keys != null) {
                    for (String key : keys) {
                        List<String> images = packageList.get(key);
                        if (images != null) {
                            for (String image : images) {
                                if (!DiskCacheFileManager.isSmallImageExist(image).equals("")) {
                                    MyApplication.preImageLoader.loadImage("file://" + image, MyApplication.viewOptions, null);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
