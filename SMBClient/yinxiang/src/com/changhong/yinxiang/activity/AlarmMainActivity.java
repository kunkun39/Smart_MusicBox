package com.changhong.yinxiang.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.changhong.yinxiang.view.MyProgressDialog;

public class AlarmMainActivity extends BaseActivity {

	private Button add;
	private Button delete;
	private ListView alarmInfor;
	private static AlarmAdapter adapter;
	public static ArrayList<Alarm> mAlarmList = null;
	private MusicEditServer mMusicEditServer = null;
	private static String action = "alarmInfor";
	private MyProgressDialog myProgressDialog = null;// 远程数据获取进度条

	public static final int SEND_GET_REQUEST = 1001;
	public static final int SEND_TCP_COMMOND = 1002;

	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case YinXiangMusicViewActivity.SHOW_ACTION_RESULT:

				Bundle bund = msg.getData();
				String resAction = (String) bund.get("action");
				if (resAction.equals(action)) {
					String result = bund.getString("result");
					Log.i("mmmm", "AlarmMainActivity-result=" + result);
					mAlarmList = ResolveAlarmInfor.strToList(result);
					adapter.setData(mAlarmList);
					setClickable(true);
					cancelMyDialog();
				}
				break;

			case SEND_GET_REQUEST:

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

	protected void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.alarm_main);
		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (Button) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);
		
		// 启动TCP接收线程
		mMusicEditServer = MusicEditServer.creatFileEditServer();
		mMusicEditServer.communicationWithServer(handler,
				MusicUtils.ACTION_SOCKET_COMMUNICATION, action);
		
		add = (Button) findViewById(R.id.add);
		delete = (Button) findViewById(R.id.delete);
		alarmInfor = (ListView) findViewById(R.id.alarm_info);
		adapter = new AlarmAdapter(AlarmMainActivity.this);
		alarmInfor.setAdapter(adapter);
		setClickable(false);
	}

	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();
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
				Intent intent = new Intent(AlarmMainActivity.this,
						DeleteAlarmActivity.class);
				startActivityForResult(intent, 2);
			}
		});

		// alarmInfor.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// // TODO Auto-generated method stub
		// Intent intent=new
		// Intent(AlarmMainActivity.this,SetAlarmActvity.class);
		// intent.putExtra("select", position);
		// startActivityForResult(intent, 0);
		// }
		// });

		getAlarmMsg();
		if (null == ClientSendCommandService.serverIP) {
			Toast.makeText(AlarmMainActivity.this, "局域网内未发现设备，请检查设备是否开启！",
					Toast.LENGTH_SHORT).show();
			adapter.clearData();
		} else {
			showMyDialog();
		}
	}

	private void showMyDialog() {
		if (null == myProgressDialog) {
			myProgressDialog = new MyProgressDialog(AlarmMainActivity.this);
		}
		if (!myProgressDialog.isShowing()) {
			myProgressDialog.show("远程数据加载中....");
		}
	}

	private void cancelMyDialog() {
		if (myProgressDialog != null && myProgressDialog.isShowing()) {
			myProgressDialog.dismiss();
		}
	}

	public void setClickable(boolean flag) {
		add.setEnabled(flag);
		delete.setEnabled(flag);
	}

	private void getAlarmMsg() {

		if (ClientSendCommandService.handler != null) {
			// 触发音响端数据发送
			String ipString = NetworkUtils.getLocalHostIp();
			ClientSendCommandService.msg = Alarm.get + ipString;
			ClientSendCommandService.handler.sendEmptyMessage(1);

			Log.i("mmmm", "getAlarmMsg:" + ipString);
		}
	}

	private void startSetAlarm() {
		Intent intent = new Intent(AlarmMainActivity.this,
				SetAlarmActvity.class);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (data == null)
			return;
		switch (requestCode) {
		case 0:
			// 更改闹铃信息后的处理
			String str = data.getExtras().getString("alarm");
			Alarm alarm = ResolveAlarmInfor.jsonToAlarm(str);
			adapter.update(alarm);
			break;

		case 1:
			// 增加闹铃
			String addStr = data.getExtras().getString("alarm");
			Alarm addAlarm = ResolveAlarmInfor.jsonToAlarm(addStr);
			mAlarmList.add(addAlarm);
			adapter.notifyDataSetChanged();
			break;
		case 2:
			adapter.setData(mAlarmList);
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// if (myProgressDialog != null && myProgressDialog.isShowing()) {
			// myProgressDialog.dismiss();
			// }
			break;
		case KeyEvent.KEYCODE_MENU:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (NetworkUtils.isWifiConnected(this)) {
			adapter.setData(mAlarmList);
		} else {
			adapter.clearData();
		}

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mMusicEditServer.close();

		ClientSendCommandService.stopTCP();
		super.onDestroy();
	}

}
