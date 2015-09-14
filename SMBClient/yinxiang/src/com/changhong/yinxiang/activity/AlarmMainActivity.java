package com.changhong.yinxiang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.alarm.Alarm;
import com.changhong.yinxiang.alarm.AlarmAdapter;
import com.changhong.yinxiang.alarm.ResolveAlarmInfor;
import com.changhong.yinxiang.alarm.SetAlarmActvity;
import com.changhong.yinxiang.music.MusicEditServer;
import com.changhong.yinxiang.music.MusicUtils;

public class AlarmMainActivity extends BaseActivity {

	private Button add;
	private Button delete;
	private ListView alarmInfor;
	private static AlarmAdapter adapter;

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
					
					adapter.setData(ResolveAlarmInfor.strToList(result));
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

			}
		});
		getAlarmMsg();
	}

	private void getAlarmMsg() {
		// 触发音响端数据发送
		String ipString = NetworkUtils.getLocalHostIp();
		ClientSendCommandService.msg = Alarm.get + ipString;
		ClientSendCommandService.handler.sendEmptyMessage(1);

		// 启动TCP接收线程
		MusicEditServer.creatFileEditServer().communicationWithServer(handler,
				MusicUtils.ACTION_SOCKET_COMMUNICATION, action);
	}

	private void startSetAlarm() {
		Intent intent = new Intent(AlarmMainActivity.this,
				SetAlarmActvity.class);
		startActivity(intent);

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
