package com.changhong.tvserver;

import android.app.Application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.changhong.nanohttpd.NanoHTTPDService;
import com.changhong.tvserver.touying.image.loader.core.ImageLoadController;
import com.changhong.tvserver.touying.image.loader.core.ImageLoaderConfigure;
import com.changhong.tvserver.touying.image.loader.utils.RepositoryUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * application for tv server which used for init some configuration such as image loader or path for cache etc
 */
public class MyApplication extends Application {
    /**
     * Lrc save path
     */
    public static String lrcPath = "/lrc";

    /**
     * private save path
     */
    public static String appInfoPath = "/data/data/";

    /**
     * image tou ying load options
     */
    public static DisplayImageOptions options;

    /**
     * Image download save path
     */
    public static File imageDownloadRootPath;

    /**
     * all status about network
     */
    enum NetworkStatus {
        NET_NULL,
        NET_WIRED,
        NET_WIRELESS_24G,
        NET_WIRELESS_5G
    }

    /**
     * current network status
     */
    public static NetworkStatus networkStatus = NetworkStatus.NET_NULL;

	@Override
	public void onCreate() {
		super.onCreate();
		options = new DisplayImageOptions
				.Builder()
				.showImageForEmptyUri(R.drawable.activity_empty_photo)
				.showImageOnFail(R.drawable.activity_empty_photo)
				.cacheInMemory(false)
				.cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration
				.Builder(getApplicationContext())
				.defaultDisplayImageOptions(options)
				.discCacheSize(50 * 1024 * 1024)//50M
				.discCacheFileCount(100)
				.writeDebugLogs()
				.build();
		ImageLoader.getInstance().init(config);
		appInfoPath=appInfoPath+this.getPackageName();


        /**
         * 创建歌词保存目录和删除原来已经存在的歌词
         */
        String ROOT = StorageUtils.getCacheDirectory(this).getAbsolutePath();
        lrcPath = ROOT + lrcPath;
        File lrcFile = new File(lrcPath);
        if(lrcFile.exists()) {
            File[] files = lrcFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        } else {
            lrcFile.mkdirs();
        }

        /**
        * 设置图片下载的路径
        */
        imageDownloadRootPath = RepositoryUtils.getCacheDirectory(this);
        ImageLoaderConfigure configure = new ImageLoaderConfigure();
        ImageLoadController.getInstance().initConfiguration(configure);

        /**
         * 生成所有application的index文件
         */
        generateApplicationInfoIndexJson(this);

        /**
         * 注册网络变化的received
         */
        registerNetworkStatusReceiver();
        
        /**
         * 启动Http服务
         */
        Intent start = new Intent(this, NanoHTTPDService.class);
        startService(start);
        
    }

    /******************************************get all server application info*****************************************/

    public static void generateApplicationInfoIndexJson(Context context) {
        try {
        	JSONArray all = new JSONArray();
            PackageManager pm = context.getPackageManager();
        	Intent intent = new Intent(Intent.ACTION_MAIN,null);
    		intent.addCategory(Intent.CATEGORY_LAUNCHER);
    		List< ResolveInfo> appList = pm.queryIntentActivities(intent,0);
    		
            for (ResolveInfo packageInfo : appList) {
            	
                /**
                 * all application
                 */
                String packageName = packageInfo.activityInfo.packageName;
                String applicationName = packageInfo.loadLabel(pm).toString();

                JSONObject single = new JSONObject();
                single.put("packageName", packageName);
                single.put("applicationName", applicationName);
                all.put(single);

            }

            /**
             * 输入应用到文件
             */
            File appIndexJson = new File(appInfoPath, "OttAppInfoJson.json");
            if (appIndexJson.exists()) {
                appIndexJson.delete();
            }
            FileWriter fw = new FileWriter(appIndexJson);
            fw.write(all.toString(), 0, all.toString().length());
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**********************************************register receiver for network**************************************/

    private void registerNetworkStatusReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.RSSI_CHANGED");
        filter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        filter.addAction("android.intent.action.TIME_TICK");
        filter.addAction("android.amlogic.settings.WEATHER_INFO");
        filter.addAction("android.changhong.refresh.movie");
        registerReceiver(this.netReceiver, filter);
    }

    private BroadcastReceiver netReceiver = new BroadcastReceiver() {

        public void onReceive(Context paramContext, Intent paramIntent) {
            String action = paramIntent.getAction();
            boolean setNetworkSuccessFul = false;

            if("android.net.conn.CONNECTIVITY_CHANGE".equals(action)
                    || "android.net.wifi.RSSI_CHANGED".equals(action)
                    || "android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)
                    ||	"android.changhong.refresh.movie".equals(action)) {

                //获得网络连接服务
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

                //获取无线连接状, 设置这个值只是为了让系统知道，连接的是无线，NetworkStatus.NET_WIRELESS_24G
                NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                if (NetworkInfo.State.CONNECTED == state) {
                    networkStatus = NetworkStatus.NET_WIRELESS_24G;
                    setNetworkSuccessFul = true;
                }

                //获取网络连接状
                state = connManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).getState();
                if (NetworkInfo.State.CONNECTED == state) {
                    networkStatus = NetworkStatus.NET_WIRED;
                    setNetworkSuccessFul = true;
                }

                if (!setNetworkSuccessFul) {
                    networkStatus = NetworkStatus.NET_NULL;
                }
            }

            Log.e("TVServer_Application", "Current status " + networkStatus.name());
        }
    };

}
