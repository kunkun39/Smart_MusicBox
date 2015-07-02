package com.changhong.yinxiang.service;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jack Wang
 */
public class UpdateFileDownloadThread extends Thread {

    private Context context;

    /**
     * 表明第几个线程
     */
    private int threadNumber;

    /**
     * 该线程下载的开始位置
     */
    private long downloadStartByte;

    /**
     * 线程下载的结束位置
     */
    private long downloadEndByte;

    public UpdateFileDownloadThread(Context context, int threadNumber, long start, long end) {
        this.context = context;
        this.threadNumber = threadNumber;
        this.downloadStartByte = start;
        this.downloadEndByte = end;
    }

    @Override
    public void run() {
        /**
         * 如果该线程已经下载过，就用日志服务中获得已经下载的位置
         */
        UpdateLogService preferenceService = new UpdateLogService(context);
        long alreadyDownloadSize = preferenceService.getThreadDownloadDataSize(threadNumber);
        if (alreadyDownloadSize > 0) {
            downloadStartByte = alreadyDownloadSize + downloadStartByte;
        }

        /**
         * 设置连接请求
         */
        HttpURLConnection downloadConnection = null;
        InputStream instream = null;
        RandomAccessFile rasf = null;
        try {
            URL url = new URL(UserUpdateService.UPDATE_URL);
            downloadConnection = (HttpURLConnection) url.openConnection();
            downloadConnection.setConnectTimeout(20000);
            downloadConnection.setRequestMethod("GET");
            downloadConnection.setRequestProperty("Range", "bytes=" + downloadStartByte + "-" + downloadEndByte);
            downloadConnection.setRequestProperty("Accept","image/gif,image/x-xbitmap,application/msword,*/*");
            downloadConnection.setRequestProperty("Connection", "Keep-Alive");

            /**
             * 开始下载文件
             */
            if (downloadConnection.getResponseCode() == HttpURLConnection.HTTP_OK || downloadConnection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                downloadConnection.connect();
                instream = downloadConnection.getInputStream();
                rasf = new RandomAccessFile(UserUpdateService.updateFile, "rwd");
                rasf.seek(downloadStartByte);

                byte[] b = new byte[1024 * 24];
                int length = -1;
                while ((length = instream.read(b)) != -1) {
                    rasf.write(b, 0, length);
                    alreadyDownloadSize = alreadyDownloadSize + length;

                    /**
                     * 不听保存当前线程文件下载
                     */
                    Log.d("file write size", ">>>>>" + alreadyDownloadSize);
                    preferenceService.saveThreadDownloadDataSize(threadNumber, alreadyDownloadSize);
                }

                //下载完成，重置当前线程下载的状态
                preferenceService.saveDownloadException(false);

                //关闭流文件, 并重新设置已下载的文件大小为0
                rasf.close();
                instream.close();

                //标记标记线程状态为TRUE
                if (threadNumber == 1) {
                    UserUpdateService.THREAD_ONE_FINISHED  = true;
                } else if (threadNumber == 2) {
                    UserUpdateService.THREAD_TWO_FINISHED  = true;
                }
            }
        } catch (Exception e) {
            //异常处理
            preferenceService.saveDownloadException(true);
            UserUpdateService.THREAD_DOWNLOAD_EXCEPTION  = true;
        } finally {
            try {
                if (downloadConnection != null) {
                    downloadConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
