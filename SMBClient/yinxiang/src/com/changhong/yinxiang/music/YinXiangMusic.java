package com.changhong.yinxiang.music;

import java.io.Serializable;

/**
 * Created by Jack Wang
 */
public class YinXiangMusic implements Serializable {

    private int id;// 歌曲ID

    private String title; // 歌曲名称

    private String path;// 歌曲路径

    private int albumId;//专辑ID 

    private String artist;// 歌手名称

    private int artistId;

    private int duration;// 歌曲时长

    private long createTime;
    


    public YinXiangMusic(int id, String title, String path, int albumId, String artist, int artistId, int duration, long createTime) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.albumId = albumId;
        this.artist = artist;
        this.artistId = artistId;
        this.duration = duration;
        this.createTime = createTime;
    }

    public YinXiangMusic(){
    	
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
