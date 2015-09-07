package com.changhong.yinxiang.activity;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	protected abstract void initView();
	protected abstract void initData();
	
}
