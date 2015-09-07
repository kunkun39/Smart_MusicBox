package com.changhong.yinxiang.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.yinxiang.R;

public class AlarmMainActivity extends BaseActivity{

	private Button add;
	private Button delete;
	private ListView alarmInfor;
	public static Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what){
			case 1:
				
				break;
			}
			super.handleMessage(msg);
			
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}


	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.alarm_main);
		add=(Button)findViewById(R.id.add);
		delete=(Button)findViewById(R.id.delete);
		alarmInfor=(ListView)findViewById(R.id.alarm_info);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		}); 
		
		delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		getAlarmMsg();
	}

	private void getAlarmMsg(){
		ClientSendCommandService.msg="getAlarmMsg:|get";
		ClientSendCommandService.handler.sendEmptyMessage(1);
	}
	
	private void startSetAlarm(){
		Intent intent =new Intent();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
