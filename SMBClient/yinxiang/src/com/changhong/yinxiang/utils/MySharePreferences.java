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
		mysharepreferencesdata.searchHistory = mSettings.getString("search_History", null);

		return mysharepreferencesdata;
	}
	

	public void SaveMySharePreferences(MySharePreferencesData mysharepreferencesdata) {
		
		mEditor.putString("search_History", mysharepreferencesdata.searchHistory);
		mEditor.commit();
	}

}
