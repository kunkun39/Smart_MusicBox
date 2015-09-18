package com.changhong.yinxiang.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.alarm.Alarm;
import com.changhong.yinxiang.alarm.AlarmAdapter;
import com.changhong.yinxiang.alarm.DeleteAlarmActivity;
import com.changhong.yinxiang.alarm.ResolveAlarmInfor;
import com.changhong.yinxiang.alarm.SetAlarmActvity;
import com.changhong.yinxiang.music.MusicEditServer;
import com.changhong.yinxiang.music.MusicUtils;

public class AlarmMainActivity extends BaseActivity {

	private Button add;
	private Button delete;
	private ListView alarmInfor;
	private static AlarmAdapter adapter;
	public static ArrayList<Alarm> mAlarmList = null;
   private  MusicEditServer mMusicEditServer=null;
	private static String action = "alarmInfor";
	public static Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case YinXiangMusicViewActivity.SHOW_ACTION_RESULT:

				Bundle bund=msg.getData();
				String resAction = (String) bund.get("action");
				if (resAction.equals(action)) {
					String result =bund.getString("result");
					Log.i("mmmm", "AlarmMainActivity-result="+result);
					mAlarmList=ResolveAlarmInfor.strToList(result);
					adapter.setData(mAlarmList);
				}

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
		add = (Button) findViewById(R.id.add);
		delete = (Button) findViewById(R.id.delete);
		alarmInfor = (ListView) findViewById(R.id.alarm_info);
		adapter=new AlarmAdapter(AlarmMainActivity.this);
		alarmInfor.setAdapter(adapter);
		mMusicEditServer = MusicEditServer.creatFileEditServer();

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startSetAlarm();
			}
		});

		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(AlarmMainActivity.this,DeleteAlarmActivity.class);
				startActivity(intent);
			}
		});
		
//		alarmInfor.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				Intent intent=new Intent(AlarmMainActivity.this,SetAlarmActvity.class);
//				intent.putExtra("select", position);
//				startActivityForResult(intent, 0);
//			}
//		});
		getAlarmMsg();
	}

	private void getAlarmMsg() {
		
		
				
		// 触发音响端数据发送
		String ipString = NetworkUtils.getLocalHostIp();
		ClientSendCommandService.msg = Alarm.get + ipString;
		ClientSendCommandService.handler.sendEmptyMessage(1);
		
		// 启动TCP接收线程
		mMusicEditServer.communicationWithServer(handler,
								MusicUtils.ACTION_SOCKET_COMMUNICATION, action);

		
		Log.i("mmmm", "getAlarmMsg:"+ipString);
	}

	private void startSetAlarm() {
		Intent intent = new Intent(AlarmMainActivity.this,
				SetAlarmActvity.class);
		startActivityForResult(intent, 1);
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(data==null)
			return;
		switch (requestCode){
		case 0:
		//更改闹铃信息后的处理
			String str=data.getExtras().getString("alarm");
			Alarm alarm=ResolveAlarmInfor.jsonToAlarm(str);
			adapter.update(alarm);
			break;
			
		case 1:
			//增加闹铃
			String addStr=data.getExtras().getString("alarm");
			Alarm addAlarm=ResolveAlarmInfor.jsonToAlarm(addStr);
			mAlarmList.add(addAlarm);
			adapter.notifyDataSetChanged();
			break;
		}
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mMusicEditServer.close();

		super.onDestroy();
	}

}
