package com.changhong.xiami.data;



import java.util.List;

import com.xiami.sdk.entities.OnlineSong;

import android.graphics.Bitmap;

public class XiamiDataModel {

	private long id;   //数据id
	
	private String title;   //名称
	
	private int type;   //音乐类型
	
	private String logoUrl; //logoURL
	
	private Bitmap logoImg=null; //图片	
	
	//用于歌手按字符排序
	private String sortLetters;  //显示数据拼音的首字母	
	
	private String description;  //详情描述
	
	
	//喜爱度
	int  likeCount;
	
	
	//歌手名称
	private String  artist;
	
	//歌手图片URL
	private  String  artistImgUrl;
	
	//歌手图片数据
	private Bitmap artistImg=null; 
	
	//歌曲列表
	private List<OnlineSong> songs=null;
	
	
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Bitmap getLogoImg() {
		return logoImg;
	}
	public void setLogoImg(Bitmap logoImg) {
		this.logoImg = logoImg;
	}
	
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	public String getLogoUrl() {
		return logoUrl;
	}
	public void setLogoUrl(String imgUrl) {
		this.logoUrl = imgUrl;
	}
	public long getId() {
		return id;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getArtistImgUrl() {
		return artistImgUrl;
	}
	public void setArtistImgUrl(String artistImgUrl) {
		this.artistImgUrl = artistImgUrl;
	}
	public List<OnlineSong> getSongs() {
		return songs;
	}
	public void setSongs(List<OnlineSong> songs) {
		this.songs = songs;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Bitmap getArtistImg() {
		return artistImg;
	}
	public void setArtistImg(Bitmap artistImg) {
		this.artistImg = artistImg;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
}
