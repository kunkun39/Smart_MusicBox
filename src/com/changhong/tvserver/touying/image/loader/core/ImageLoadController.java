package com.changhong.tvserver.touying.image.loader.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import com.changhong.tvserver.utils.StringUtils;
import com.changhong.tvserver.touying.image.loader.cache.DiskCache;
import com.changhong.tvserver.touying.image.loader.task.ImageDisplayTask;
import com.changhong.tvserver.touying.image.loader.task.ImageDownloadTask;
import com.changhong.tvserver.touying.image.loader.task.TaskExecutorFactory;

import java.io.File;
import java.util.concurrent.Executor;

/**
 * Created by Jack Wang
 */
public class ImageLoadController {

    private static final String TAG = "ImageLoadController";

    /**
     * image loader controller instance
     */
    private static ImageLoadController instance;

    /**
     * disk cache provider
     */
    private DiskCache diskCache;

    /**
     * image data download task executor
     */
    private Executor downloadTaskExecutor ;

    public static ImageLoadController getInstance() {
        if (instance == null) {
            synchronized (ImageLoadController.class) {
                if (instance == null) {
                    instance = new ImageLoadController();
                }
            }
        }
        return instance;
    }

    public void initConfiguration(ImageLoaderConfigure configuration) {
        configuration.initAllEmptyFields();
        this.diskCache = configuration.getDiskCache();
        this.downloadTaskExecutor = TaskExecutorFactory.createdDownloadExecutor();
    }

    /****************************************************image load part**********************************************/

    /**
     * this is entrence for image get
     * <p>
     *
     * @param handler activity handle which used for handle message afterwords
     * @param downloadID handle message id for download image by http
     * @param displayID handle message id for display image after local already download image data or exist
     * @param imageUri the url for http image
     */
    public void getImage(Handler handler, int downloadID, int displayID, String imageUri) {
        /**
         * according to image url check is cached on disk
         */
        File cachedFile = diskCache.getDiskCachedFile(imageUri);

        /**
         * according cache file exist decide go to which way
         */
        if (cachedFile == null) {
            handler.sendEmptyMessage(downloadID);
        } else {
            Message msg = new Message();
            msg.what = displayID;
            msg.obj = "show";
            handler.sendMessage(msg);
        }
    }

    /**
     * this method will do two things
     *
     * 1 - check the max number for disk file number is exceed, if exceed, delete oldest fifty
     * 1 - image download task execute
     */
    public void gotoDownloadWay(Handler handler, int displayID, boolean orignalImage, String imageUri) {
        if (StringUtils.hasLength(imageUri)) {
            ImageDownloadTask task = new ImageDownloadTask(handler, displayID, orignalImage, imageUri);
            downloadTaskExecutor.execute(task);
        }
    }

    /**
     * image view for display
     */
    public static void gotoDisplayWay(Handler handler, int finishID, ImageView imageView, boolean hasSmallImage, boolean showAnimation, String imageUri) {
        try {
            if (StringUtils.hasLength(imageUri)) {
                ImageDisplayTask task = new ImageDisplayTask(handler, finishID, imageView, hasSmallImage, showAnimation, imageUri);
                task.run();
                Log.i(TAG, "finish display image " + imageUri);
            }
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(finishID);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
    }

}
