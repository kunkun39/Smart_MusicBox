package com.changhong.xiami.data;

import android.graphics.Bitmap;

public class XiamiDataModel {

	private long id;   //数据id
	private String name;   //名称
	private String imgUrl; //图片URL
	private Bitmap image=null; //图片	
	
	//用于歌手按字符排序
	private String sortLetters;  //显示数据拼音的首字母	
	private String content;  //详情描述
	private Object   otherObj;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Bitmap getImage() {
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}
	
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public Object getOtherObj() {
		return otherObj;
	}
	public void setOtherObj(Object otherObj) {
		this.otherObj = otherObj;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
