package com.changhong.tvserver.search.aidl;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class VideoInfoDataServer extends Observable {
	/**
	 * Singleton Instance to Notify data to listener
	 */
	private static VideoInfoDataServer mDataServer = null;
	
	private List<VideoInfo> mDataList = new ArrayList<VideoInfo>();
	
	private VideoInfoDataServer() {};
	
	public static synchronized VideoInfoDataServer getInstance()
	{
		if (mDataServer == null) {
			mDataServer = new VideoInfoDataServer();
		}		
		return mDataServer;
	}
	
	public void setData(List<VideoInfo> list)
	{
		if(list == null
				|| list.size() <=0 )
			return ;
		
		mDataList.clear();
		mDataList.addAll(list);
		
		setChanged();
		notifyObservers(mDataList);
	}
	
	public List<VideoInfo> getData()
	{
		return mDataList;
	}
	
	public void addItem(VideoInfo videoInfo)
	{
		if (videoInfo == null) {
			return;
		}		
		mDataList.add(videoInfo);
		setChanged();
		notifyObservers(mDataList);
	}
	
	public void removeItem(VideoInfo videoInfo)
	{
		if (videoInfo == null) {
			return;
		}	
		mDataList.remove(videoInfo);
		setChanged();
		notifyObservers(mDataList);
	}
}
