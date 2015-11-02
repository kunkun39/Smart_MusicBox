package com.changhong.xiami.data;

import java.io.Serializable;
import java.util.List;

import com.xiami.sdk.entities.OnlineSong;


public class SceneInfor implements  Serializable{
	
	private  String  sceneName;
	public String getSceneName() {
		return sceneName;
	}
	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}
	public List<OnlineSong> getSongsList() {
		return songsList;
	}
	public void setSongsList(List<OnlineSong> songsList) {
		this.songsList = songsList;
	}
	private List<OnlineSong> songsList;
	
}
