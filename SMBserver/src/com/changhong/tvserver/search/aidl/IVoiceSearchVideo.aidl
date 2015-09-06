package com.changhong.tvserver.search.aidl;
import com.changhong.tvserver.search.aidl.VideoInfo;
interface IVoiceSearchVideo{
	void registerApplication(String authid);
	void setEpgList(in String source,in String action, in List<VideoInfo> videoList);
	void unRegister(String authid);
}