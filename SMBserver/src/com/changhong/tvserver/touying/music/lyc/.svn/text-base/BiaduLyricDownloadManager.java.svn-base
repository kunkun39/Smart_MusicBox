/**
 * Copyright (c) www.longdw.com
 */
package com.changhong.tvserver.touying.music.lyc;

import android.util.Log;
import com.changhong.tvserver.MyApplication;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.*;

/**
 * 歌词下载
 */
public class BiaduLyricDownloadManager {
    private static final String TAG = BiaduLyricDownloadManager.class.getSimpleName();
    public static final String GB2312 = "GB2312";
    public static final String UTF_8 = "utf-8";
    private final int mTimeOut = 10 * 1000;
    private LyricXMLParser mLyricXMLParser = new LyricXMLParser();
    private URL mUrl = null;
    private int mDownloadLyricId = -1;

    /*
     * 根据歌曲名和歌手名取得该歌的XML信息文件 返回歌词保存路径
     */
    public String searchLyricFromWeb(String musicLrcPath, String musicName, String singerName) {
        if (musicLrcPath != null && !"".equals(musicLrcPath)) {
            return fetchLyricContent(musicLrcPath, musicName, singerName);
        }

        // 传进来的如果是汉字，那么就要进行编码转化
        try {
            musicName = URLEncoder.encode(musicName, UTF_8);
            singerName = URLEncoder.encode(singerName, UTF_8);
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }

        // 百度音乐盒的API
        String strUrl = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=" + musicName + "$$" + singerName + "$$$$";

        // 生成URL
        try {
            mUrl = new URL(strUrl);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        try {
            HttpURLConnection httpConn = (HttpURLConnection) mUrl.openConnection();
            httpConn.setReadTimeout(mTimeOut);
            if (httpConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            httpConn.connect();

            // 将百度音乐盒的返回的输入流传递给自定义的XML解析器，解析出歌词的下载ID
            mDownloadLyricId = mLyricXMLParser.parseLyricId(httpConn.getInputStream());
            httpConn.disconnect();
        } catch (IOException e1) {
            Log.i(TAG, "http连接连接IO异常");
            e1.printStackTrace();
            return null;
        } catch (Exception e) {
            Log.i(TAG, "XML解析错误");
            e.printStackTrace();
            return null;
        }
        return fetchLyricContent(musicName, singerName);
    }

    /**
     * 从手机上直接下载歌词
     */
    private String fetchLyricContent(String musicLrcPath, String musicName, String singerName) {
        HttpGet getMethod = new HttpGet(musicLrcPath);
        HttpClient httpClient = new DefaultHttpClient();

        String content = null;
        try {
            HttpResponse response = httpClient.execute(getMethod);
            if (response.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (content != null) {
            // 检查保存的目录是否已经创建

            String folderPath = MyApplication.lrcPath;
            File savefolder = new File(folderPath);
            if (!savefolder.exists()) {
                savefolder.mkdirs();
            }

            String savePath = folderPath + File.separator + musicName + "-" + singerName + ".lrc";
            saveLyric(content.toString(), savePath);
            return savePath;
        }

        return null;

    }

    /**
     * 根据歌词下载ID，获取网络上的歌词文本内容
     */
    private String fetchLyricContent(String musicName, String singerName) {
        if (mDownloadLyricId <= 0) {
            return null;
        }
        BufferedReader br = null;
        StringBuilder content = null;
        String temp = null;
        String lyricURL = "http://box.zhangmen.baidu.com/bdlrc/" + mDownloadLyricId / 100 + "/" + mDownloadLyricId + ".lrc";

        try {
            mUrl = new URL(lyricURL);
        } catch (MalformedURLException e2) {
            e2.printStackTrace();
        }

        // 获取歌词文本，存在字符串类中
        try {
            // 建立网络连接
            br = new BufferedReader(new InputStreamReader(mUrl.openStream(), GB2312));
            if (br != null) {
                content = new StringBuilder();
                // 逐行获取歌词文本
                while ((temp = br.readLine()) != null) {
                    content.append(temp);
                }
                br.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            musicName = URLDecoder.decode(musicName, UTF_8);
            singerName = URLDecoder.decode(singerName, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (content != null) {
            // 检查保存的目录是否已经创建

            String folderPath = MyApplication.lrcPath;
            File savefolder = new File(folderPath);
            if (!savefolder.exists()) {
                savefolder.mkdirs();
            }

            String savePath = folderPath + File.separator + musicName + "-" + singerName + ".lrc";
            saveLyric(content.toString(), savePath);
            return savePath;
        } else {
            return null;
        }
    }

    private void saveLyric(String content, String filePath) {
        File file = new File(filePath);
        try {
            OutputStream outstream = new FileOutputStream(file);
            OutputStreamWriter out = new OutputStreamWriter(outstream);
            out.write(content);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
