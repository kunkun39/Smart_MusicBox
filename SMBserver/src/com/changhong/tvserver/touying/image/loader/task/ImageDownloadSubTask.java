package com.changhong.tvserver.touying.image.loader.task;

import android.net.Uri;
import com.changhong.tvserver.touying.image.loader.core.ImageHttpDownloader;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Jack Wang
 */
public class ImageDownloadSubTask extends Thread {

    /**
     * indicate thread number
     */
    private Long mainThreadID;

    /**
     * Http download image url
     */
    private String imageUrl;

    /**
     * download local file
     */
    private RandomAccessFile accessFile;

    /**
     * start download file position
     */
    private long downloadStartByte;

    /**
     * end download file position
     */
    private long downloadEndByte;

    public ImageDownloadSubTask(Long mainThreadID, String imageUrl, File file, long start, long end) {
        this.mainThreadID = mainThreadID;
        try {
            this.accessFile = new RandomAccessFile(file, "rwd");
        } catch (Exception e) {
            this.accessFile = null;
        }
        this.imageUrl = imageUrl;
        this.downloadStartByte = start;
        this.downloadEndByte = end;
    }

    @Override
    public void run() {
        if (accessFile == null) {
            return;
        }

        /**
         * setting the connection
         */
        HttpURLConnection downloadConnection = null;
        InputStream instream = null;
        try {
            String encodedUrl = Uri.encode(imageUrl, ImageHttpDownloader.ALLOWED_URI_CHARS);
            URL url = new URL(encodedUrl);
            downloadConnection = (HttpURLConnection) url.openConnection();
            downloadConnection.setConnectTimeout(20000);
            downloadConnection.setRequestMethod("GET");
            downloadConnection.setRequestProperty("Range", "bytes=" + downloadStartByte + "-" + downloadEndByte);
            downloadConnection.setRequestProperty("Accept", "image/gif,image/x-xbitmap,application/msword,*/*");
            downloadConnection.setRequestProperty("Connection", "Keep-Alive");

            /**
             * begin to downloading
             */
            if (downloadConnection.getResponseCode() == HttpURLConnection.HTTP_OK || downloadConnection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                downloadConnection.connect();
                instream = downloadConnection.getInputStream();
                accessFile.seek(downloadStartByte);

                byte[] b = new byte[1024 * 24];
                int length = -1;
                while ((length = instream.read(b)) != -1) {
                    accessFile.write(b, 0, length);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (accessFile != null) {
                    accessFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (instream != null) {
                    instream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (downloadConnection != null) {
                    downloadConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //set the finishe tag
            AtomicInteger counter = ImageDownloadTask.subCurrentDownloadThreadCounter.get(String.valueOf(mainThreadID));
            counter.getAndIncrement();
        }
    }

}
