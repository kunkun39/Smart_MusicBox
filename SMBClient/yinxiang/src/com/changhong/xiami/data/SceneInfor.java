package com.changhong.xiami.data;

import java.io.Serializable;
import java.util.List;

import com.xiami.sdk.entities.OnlineSong;


public class SceneInfor implements  Serializable{
	
	private  int  sceneID;
	private  int  musicType;
	private  String  sceneName;
	private  String  sceneLogo;

	private List<OnlineSong> songsList;

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
	public int getSceneID() {
		return sceneID;
	}
	public void setSceneID(int sceneID) {
		this.sceneID = sceneID;
	}
	public int getMusicType() {
		return musicType;
	}
	public void setMusicType(int musicType) {
		this.musicType = musicType;
	}
	public String getSceneLogo() {
		return sceneLogo;
	}
	public void setSceneLogo(String sceneLogo) {
		this.sceneLogo = sceneLogo;
	}
	
}
