package com.changhong.yinxiang.activity;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.service.NetworkConnectChangedReceiver;
import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.xiami.data.JsonUtil;
import com.changhong.xiami.data.XMMusicData;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseActivity extends Activity{

	
	/*
	 * wifi监听
	 */
	private IntentFilter wifiFilter = null;
	private NetworkConnectChangedReceiver networkConnectChange = null;

	/************************************************** IP连接部分 *******************************************************/

	public static TextView title = null;
	protected Button listClients;
	protected Button back;
	protected ListView clients = null;
	protected BoxSelectAdapter IpAdapter;
	
	//json数据解析
	protected XMMusicData mXMMusicData;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}


	
	protected  void initData(){
		
		mXMMusicData=XMMusicData.getInstance(this);
		/**
		 * IP连接部分
		 */
		IpAdapter = new BoxSelectAdapter(BaseActivity.this,ClientSendCommandService.serverIpList);
		clients.setAdapter(IpAdapter);
		clients.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				clients.setVisibility(View.GONE);
				return false;
			}
		});
		clients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList
						.get(arg2);
				ClientSendCommandService.titletxt = ClientSendCommandService
						.getCurrentConnectBoxName();
				title.setText(ClientSendCommandService.getCurrentConnectBoxName());
				ClientSendCommandService.handler.sendEmptyMessage(2);
				clients.setVisibility(View.GONE);
			}
		});
		listClients.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					MyApplication.vibrator.vibrate(100);
					if (ClientSendCommandService.serverIpList.isEmpty()) {
						Toast.makeText(BaseActivity.this,"未获取到服务器IP", Toast.LENGTH_LONG).show();
					} else {
						clients.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				finish();
			}
		});
	}
	
	
	
	
	private void regWifiBroadcastRec() {
		wifiFilter = new IntentFilter();
		if (null == networkConnectChange) {
			networkConnectChange = new NetworkConnectChangedReceiver();
		}
		wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		wifiFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(networkConnectChange, wifiFilter);
	}

	private void unregisterWifiBroad() {
		if (networkConnectChange != null) {
			unregisterReceiver(networkConnectChange);
		}
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		if (ClientSendCommandService.titletxt != null) {
			title.setText(ClientSendCommandService.titletxt);
		}
//		regWifiBroadcastRec();
	}


	@Override
	protected void onPause() {
//		unregisterWifiBroad();
		super.onPause();
	}
	protected abstract void initView();

	
}
