package com.changhong.yinxiang.fragment;

import com.changhong.yinxiang.R;
import com.changhong.yinxiang.remotecontrol.TVRemoteControlService;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract  class BaseFragment extends Fragment {

	 //定义数组来存放Fragment界面

	//定义ParentActivity
	protected Activity mParentActivity;

	protected Context mContext;
	
	//定义tab选卡文字
	 private String mTextViewArray []={"遥控器","一键推送","网络电台","设置"};
	 
	 //定义当前Fragment标记
	 private String mFlag="";
	 
	 
	 @Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO 自动生成的方法存根
			super.onCreate(savedInstanceState);
			mParentActivity=getActivity();
		}
	 
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
        View view = inflater.inflate(getFragmentLayout(), null);  
		return view;
	}
	
	

	private int getFragmentLayout(){
		
		int layout;	
		if(mFlag.equals(mTextViewArray[0])){
			layout=R.layout.activity_remote_control;	
		}else if(mFlag.equals(mTextViewArray[1])){
			layout=R.layout.activity_yinxiang_category;	
		}else if(mFlag.equals(mTextViewArray[2])){
			layout=R.layout.activity_netmusic;	
		}else if(mFlag.equals(mTextViewArray[3])){
			layout=R.layout.activity_yinxiang_setting;	
		}else{
			layout=R.layout.activity_remote_control;	
		}	
		return layout;
	}
	
	 @Override
		public void onAttach(Activity activity) {
		    mContext=activity.getApplicationContext();
			super.onAttach(activity);		 
		}
	 
	 
	 protected int getWindowWidth() {

		    return mParentActivity.getWindow().getAttributes().width;
		}
	
	 protected void registerReceiver(BroadcastReceiver receiver, IntentFilter filter){
		 mContext.registerReceiver(receiver,filter);
	 }
	 
	 public Context getApplicationContext() {
			// TODO 自动生成的方法存根
			return mContext;
		}
	 
	 

	 protected void bindService(Intent service,ServiceConnection serviceCon2, int bindAutoCreate) {
		       mParentActivity.bindService(service, serviceCon2, bindAutoCreate);
		}
	 
   public abstract  String setFlag(String flag);

	public   boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

}
