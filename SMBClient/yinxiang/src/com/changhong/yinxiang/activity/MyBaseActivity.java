package com.changhong.yinxiang.activity;

import android.app.Activity;
import android.os.Bundle;

public abstract class MyBaseActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}
	
	 public abstract void initView();
	
	 public abstract void initData();
	
}
