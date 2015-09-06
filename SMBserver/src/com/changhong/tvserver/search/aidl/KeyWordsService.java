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
import android.os.IBinder;
import android.os.RemoteException;


public class KeyWordsService extends Service{
	
	// uselessness
	public static final String BOARDCAST_INTENT_ACTION = "mallservice.updateepg";
	 
	IKeyWords keywordsService;
	List<VideoInfo> videoInfos = new ArrayList<VideoInfo>();	
	
	@Override
	public IBinder onBind(Intent intent) {
		
		IKeyWords.Stub keywords = new IKeyWords.Stub() {

			@Override
			public void SetKeyWords(String searchText, KeyWords keyWords)
					throws RemoteException {
				
			}			
		};
		return keywords;
	}				
}
