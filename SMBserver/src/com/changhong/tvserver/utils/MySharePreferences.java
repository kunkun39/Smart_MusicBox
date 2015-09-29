package com.changhong.tvserver.utils;


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
		mysharepreferencesdata.clockRing = mSettings.getString("clockRing", null);
		String autoCtrl = mSettings.getString("isAutoCtrl", "off");
		mysharepreferencesdata.isAutoCtrl =autoCtrl.equals("on")?true:false;
		return mysharepreferencesdata;
	}
	

	public void SaveMySharePreferences(MySharePreferencesData mysharepreferencesdata) {
		
		mEditor.putString("clockRing", mysharepreferencesdata.clockRing);
		String autoCtrl =mysharepreferencesdata.isAutoCtrl?"on":"off";
		mEditor.putString("isAutoCtrl", autoCtrl);
		mEditor.commit();
	}

	
public void SaveAutoCtrl(boolean isAutoCtrl) {
		
		String autoCtrl =isAutoCtrl?"on":"off";
		mEditor.putString("isAutoCtrl", autoCtrl);
		mEditor.commit();
	}
	
}
