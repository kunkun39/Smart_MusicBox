package com.changhong.tvserver.search.aidl;


import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.TextView;


public class VoiceSearchService extends Service{
	
	/**
	 * Service to setData to Notifier
	 */
	@Override
	public IBinder onBind(Intent intent) {
		
		IVoiceSearchVideo.Stub voiceSearch = new IVoiceSearchVideo.Stub() {
			
			@Override
			public void unRegister(String authid) throws RemoteException {}
			
			@Override
			public void setEpgList(String source, String action,
					List<VideoInfo> videoList) throws RemoteException {
				VideoInfoDataServer.getInstance().setData(videoList);				
			}
			
			@Override
			public void registerApplication(String authid) throws RemoteException {}
		};
		return voiceSearch;
	}			
}
