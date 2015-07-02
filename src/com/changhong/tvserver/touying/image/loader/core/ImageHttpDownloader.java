package com.changhong.tvserver.touying.image.loader.core;

import android.net.Uri;
import android.util.Log;
import com.changhong.tvserver.touying.image.loader.cache.DiskCacheFilenameGenerator;
import com.changhong.tvserver.touying.image.loader.exception.ThreadKillException;
import com.changhong.tvserver.touying.image.loader.task.ImageDownloadSubTask;
import com.changhong.tvserver.touying.image.loader.task.ImageDownloadTask;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Jack Wang
 */
public class ImageHttpDownloader {

    private static final String TAG = "ImageHttpDownloader";

    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000;

    public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000;

    private static final int MUTI_THREAD_SIZE_POINT = 1024 * 1024 * 2; //2M

    private static final int BUFFER_SIZE = 32 * 1024; //32K

    public static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

    private static HttpURLConnection createConnection(String url) throws IOException {
        String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
        HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
        conn.setConnectTimeout(DEFAULT_HTTP_CONNECT_TIMEOUT);
        conn.setReadTimeout(DEFAULT_HTTP_READ_TIMEOUT);
        return conn;
    }

    /**
     * this method is used for download image the two flows
     * <p>
     *
     * 1 - if this not original image, download use one thread
     * 2 - if this is original image and file size bigger than 2M, download with two threads, else also one thread
     *
     * @param originalImage the parameter which used for decide this is original image or not
     * @param imageUri http image url
     */
    public static void getStreamFromNetwork(boolean originalImage, String imageUri) throws IOException {
        HttpURLConnection conn = createConnection(imageUri);
        int contentLength = conn.getContentLength();

        if (contentLength <= MUTI_THREAD_SIZE_POINT) {
            gotoSingleThreadDownload(conn, originalImage, imageUri);
        } else {
            Long mainThreadID = Thread.currentThread().getId();
            ImageDownloadTask.subCurrentDownloadThreadCounter.put(String.valueOf(mainThreadID), new AtomicInteger(0));
            gotoTwoThreadDownload(mainThreadID, conn, contentLength, originalImage, imageUri);
        }
    }

    /********************************************single thread download http image************************************/

    private static void gotoSingleThreadDownload(HttpURLConnection conn, boolean originalImage, String imageUri) {
        InputStream imageStream = null;
        File file = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try {
            imageStream = conn.getInputStream();
            file = DiskCacheFilenameGenerator.getDiskCacheFile(imageUri);

            in = new BufferedInputStream(imageStream, BUFFER_SIZE);
            out = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);

            copy(originalImage, in, out);
        } catch (IOException e) {
            e.printStackTrace();
            if (file != null && file.exists()) {
                file.delete();
            }
        } catch (ThreadKillException e) {
            Log.e(TAG, e.toString());
            if (file != null && file.exists()) {
                file.delete();
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static int copy(boolean orignalImage, InputStream in, OutputStream out) throws IOException {
        Long currentThreadID = Thread.currentThread().getId();

        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        while ((bytesRead = in.read(buffer)) != -1) {
            if (orignalImage && currentThreadID.intValue() != ImageDownloadTask.currentDownloadThreadID.intValue()) {
                throw new ThreadKillException("end user already request new picture, kill this thread");
            }
            out.write(buffer, 0, bytesRead);
            byteCount += bytesRead;
        }
        out.flush();
        return byteCount;
    }

    /********************************************two thread download http image****************************************/

    private static void gotoTwoThreadDownload(Long mainThreadID, HttpURLConnection conn, int contentLength, boolean originalImage, String imageUri) {
        File file = null;
        try {
            //make caculation two thread download position
            long firstThreadStart = 0;
            long firstThreadEnd = contentLength / 2;
            long secondThreadStart = contentLength / 2 + 1;
            long secondThreadEnd = contentLength;

            //generate file and begin to download
            file = DiskCacheFilenameGenerator.getDiskCacheFile(imageUri);

            ImageDownloadSubTask firstThread = new ImageDownloadSubTask(mainThreadID, imageUri, file, firstThreadStart, firstThreadEnd);
            firstThread.start();
            ImageDownloadSubTask secondThread = new ImageDownloadSubTask(mainThreadID, imageUri, file, secondThreadStart, secondThreadEnd);
            secondThread.start();

            //check all download thread is finished or not
            while (ImageDownloadTask.subCurrentDownloadThreadCounter.get(String.valueOf(mainThreadID)).intValue() < 2) {
                Thread.sleep(200);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (file != null && file.exists()) {
                file.delete();
            }
        } finally {
            //remove task even if there exist exception happen
            ImageDownloadTask.subCurrentDownloadThreadCounter.remove(String.valueOf(mainThreadID));

            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
