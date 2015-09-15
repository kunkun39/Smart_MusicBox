package com.changhong.yinxiang.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class MySharePreferences {

	Context mContext;
	android.content.SharedPreferences.Editor mEditor;
	SharedPreferences mSettings;

	public MySharePreferences(Context context) {
		
		mSettings = null;
		mEditor = null;
		mContext = context;
		InitSharedPreferences();
	}

	private  void InitSharedPreferences() {
		mSettings = mContext.getSharedPreferences("SmartAudio", 0);
		mEditor = mSettings.edit();
	}
	
	
	public MySharePreferencesData InitGetMySharedPreferences() {
		MySharePreferencesData mysharepreferencesdata = new MySharePreferencesData();
		mysharepreferencesdata.musicHistory = mSettings.getString("search_musicHistory", null);
		mysharepreferencesdata.vedioHistory = mSettings.getString("search_vedioHistory", null);

		return mysharepreferencesdata;
	}
	

	public void SaveMySharePreferences(MySharePreferencesData mysharepreferencesdata) {
		
		mEditor.putString("search_musicHistory", mysharepreferencesdata.musicHistory);
		mEditor.putString("search_vedioHistory", mysharepreferencesdata.vedioHistory);

		mEditor.commit();
	}

}
