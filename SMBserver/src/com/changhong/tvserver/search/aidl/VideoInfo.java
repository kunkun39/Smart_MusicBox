package com.changhong.tvserver.search.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoInfo implements Parcelable{
	String mImageUrl;
	String videoName;
	String videoId;
	String privatecode;
	String videoType = "";
	String action = "";
	
	public String getVideoType() {
		return videoType;
	}

	public void setVideoType(String videoType) {
		this.videoType = videoType;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public VideoInfo(String image_url, String video_name, String video_id,String privatecode,String videoType){
		mImageUrl = image_url;
		videoName = video_name;
		videoId = video_id;
		this.privatecode = privatecode;
		this.videoType = videoType;
		
	}
	private VideoInfo(String image_url, String video_name, String video_id,String privatecode,String action,String videoType){
		mImageUrl = image_url;
		videoName = video_name;
		videoId = video_id;
		this.privatecode = privatecode;
		this.videoType = videoType;
	}
	
	public String getPrivatecode() {
		return privatecode;
	}

	public void setPrivatecode(String privatecode) {
		this.privatecode = privatecode;
	}

	public String getmImageUrl() {
		return mImageUrl;
	}
	public void setmImageUrl(String mImageUrl) {
		this.mImageUrl = mImageUrl;
	}
	public String getVideoName() {
		return videoName;
	}
	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}
	public String getVideoId() {
		return videoId;
	}
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(mImageUrl);
		dest.writeString(videoName);
		dest.writeString(videoId);
		dest.writeString(privatecode);
		dest.writeString(action);
		dest.writeString(videoType);
	}
	
	public static final Parcelable.Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
		
		@Override
		public VideoInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new VideoInfo[size];
		}
		
		@Override
		public VideoInfo createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new VideoInfo(source.readString(),source.readString(),source.readString(),source.readString(),source.readString(),source.readString());
		}
	};
}
