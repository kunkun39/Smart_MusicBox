package com.changhong.yinxiang.music;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.changhong.yinxiang.vedio.AbstructProvider;

/**
 * Created by Jack Wang
 */
public class YinXiangMusicProvider implements AbstructProvider {

    public static final int FILTER_SIZE = 1 * 1024 * 1024;// 1MB

    public static final int FILTER_DURATION = 1 * 60 * 1000;// 1分钟

    private Context context;

    public YinXiangMusicProvider(Context context) {
        this.context = context;
    }

    @Override
    public List<YinXiangMusic> getList() {
        List<YinXiangMusic> list = null;
        if (context != null) {
            StringBuffer select = new StringBuffer(" 1=1 ");
            // 查询语句：检索出.mp3为后缀名，时长大于1分钟，文件大小大于1MB的媒体文件
            select.append(" and " + MediaStore.Audio.Media.SIZE + " > " + FILTER_SIZE);
            select.append(" and " + MediaStore.Audio.Media.DURATION + " > " + FILTER_DURATION);
            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, select.toString(), null, MediaStore.Audio.Media.ARTIST_KEY);
            if (cursor != null) {
                list = new ArrayList<YinXiangMusic>();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    int artistId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    long createTime = cursor .getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED));
                    YinXiangMusic music = new YinXiangMusic(id, title, path, albumId, artist,artistId, duration, createTime);
                    list.add(music);
                }
                cursor.close();
            }
        }
        return list;
    }

    public Map<String, List<YinXiangMusic>> getMapStructure(List<?> list) {
        Map<String, List<YinXiangMusic>> model = new HashMap<String, List<YinXiangMusic>>();
        if (list != null) {
            for (Object o : list) {
                YinXiangMusic music = (YinXiangMusic)o;
                String artist = music.getArtist();

                List<YinXiangMusic> musics = null;
                if (model.containsKey(artist)) {
                    musics = model.get(artist);
                } else {
                    musics = new ArrayList<YinXiangMusic>();
                }
                musics.add(music);
                model.put(artist, musics);
            }
        }
        return model;
    }

    public List<String> getMusicList(Map<String, List<YinXiangMusic>> model) {
        List<String> vedioList = new ArrayList<String>();
        for (String key : model.keySet()) {
            if (model.get(key).size() > 1) {
                vedioList.add(0, key);
            } else {
                vedioList.add(key);
            }
        }
        return vedioList;
    }
    
    

	private String getShowName(String filePath){
		
		String fileName="";
	    int startIndex=filePath.lastIndexOf(File.separator);
	    int endIndex=filePath.lastIndexOf(".");	
	    if(startIndex>0  && endIndex>(startIndex+1)){
			fileName=filePath.substring(startIndex+1,endIndex);
		}		
		return fileName;
	}
}
