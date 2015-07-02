package com.changhong.tvserver.touying.music.lyc;

import android.util.Log;
import com.changhong.tvserver.MyApplication;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by Jack Wang
 */
public class MiniLyricDownloadManager {

    private static final String TAG = "MiniLyricDownload";

    public static final String UTF_8 = "utf-8";

    public String searchLyricFromWeb(String musicName, String artist) {
        try {
            String musicLrcResponse = searchMusicLrcExist(musicName, artist);

            if (musicLrcResponse != null && !musicLrcResponse.equals("")) {
                JSONObject musicLrcJson = new JSONObject(musicLrcResponse);
                int count = musicLrcJson.getInt("count");
                if (count > 0) {
                    JSONArray array = musicLrcJson.getJSONArray("result");
                    JSONObject o = array.getJSONObject(0);
                    String lrcURL = o.getString("lrc");

                    if (lrcURL != null && !lrcURL.equals("")) {
                        return getAndSaveLrc(lrcURL, musicName, artist);
                    }
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "http连接连接IO异常");
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public String searchMusicLrcExist(String musicName, String singer) throws Exception {
        try {
            musicName = URLEncoder.encode(musicName, UTF_8);
            singer = URLEncoder.encode(singer, UTF_8);
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }

        String musicSrarchURL = "http://geci.me/api/lyric/" + musicName + "/" + singer;
        HttpGet getMethod = new HttpGet(musicSrarchURL);
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
        return content;
    }

    public String getAndSaveLrc(String lrcURL, String musicName, String singer) throws Exception {
        HttpGet getMethod = new HttpGet(lrcURL);
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

        try {
            musicName = URLDecoder.decode(musicName, UTF_8);
            singer = URLDecoder.decode(singer, UTF_8);
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

            String savePath = folderPath + File.separator + musicName + "-" + singer + ".lrc";
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
