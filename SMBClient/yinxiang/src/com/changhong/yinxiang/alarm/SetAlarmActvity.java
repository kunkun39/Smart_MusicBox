package com.changhong.yinxiang.alarm;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.WeekButton;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.changhong.yinxiang.music.MusicUtils;

public class SetAlarmActvity extends BaseActivity {

	private TimePicker timePicker;
	private Button confirm, cancel;
	private WeekButton weekBt1, weekBt2, weekBt3, weekBt4, weekBt5, weekBt6,
			weekBt7;
	private EditText tag;
	private TextView curMusic;
	private LinearLayout musics;

	private Alarm alarm;
	private WeekButton myWBList[] = null;

	// 进入设置界面后，先设置状态。
	private enum State {
		add, delete, update, get
	}

	// 默认状态
	private State state = State.add;

	private OnClickListener myClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.confirm:
				switch (state) {
				case add:
					addAlarm();
					break;
				case update:
					updateAlarm();
				default:
					break;
				}
				break;
			case R.id.cancel:

				break;

			case R.id.musics:

				break;
			}
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
		setContentView(R.layout.alarm_setting);
		timePicker = (TimePicker) findViewById(R.id.timePicker);
		timePicker.setIs24HourView(true);
		weekBt1 = (WeekButton) findViewById(R.id.week1);
		weekBt2 = (WeekButton) findViewById(R.id.week2);
		weekBt3 = (WeekButton) findViewById(R.id.week3);
		weekBt4 = (WeekButton) findViewById(R.id.week4);
		weekBt5 = (WeekButton) findViewById(R.id.week5);
		weekBt6 = (WeekButton) findViewById(R.id.week6);
		weekBt7 = (WeekButton) findViewById(R.id.week7);
		weekBt1.initBt(R.drawable.alarm1_up, R.drawable.alarm1_down, false);
		weekBt2.initBt(R.drawable.alarm2_up, R.drawable.alarm2_down, false);
		weekBt3.initBt(R.drawable.alarm3_up, R.drawable.alarm3_down, false);
		weekBt4.initBt(R.drawable.alarm4_up, R.drawable.alarm4_down, false);
		weekBt5.initBt(R.drawable.alarm5_up, R.drawable.alarm5_down, false);
		weekBt6.initBt(R.drawable.alarm6_up, R.drawable.alarm6_down, false);
		weekBt7.initBt(R.drawable.alarm7_up, R.drawable.alarm7_down, false);
		myWBList = getMyWBList();

		confirm = (Button) findViewById(R.id.confirm);
		cancel = (Button) findViewById(R.id.cancel);
		tag = (EditText) findViewById(R.id.tag);
		musics = (LinearLayout) findViewById(R.id.musics);
		curMusic = (TextView) findViewById(R.id.cur_music);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		int position = intent.getIntExtra("select", -1);
		if (position < 0) {
			state = State.add;
			addAlarm();

		} else {
			state = State.update;
			initUpdateAlarm(position);
		}

	}

	private WeekButton[] getMyWBList() {
		if (null == myWBList) {
			myWBList = new WeekButton[7];
			myWBList[0] = weekBt1;
			myWBList[1] = weekBt2;
			myWBList[2] = weekBt3;
			myWBList[3] = weekBt4;
			myWBList[4] = weekBt5;
			myWBList[5] = weekBt6;
			myWBList[6] = weekBt7;

		}
		return myWBList;
	}

	// 初始化修改流程数据
	private void initUpdateAlarm(int position) {
		alarm = AlarmAdapter.mAlarmList.get(position);
		timePicker.setCurrentHour(alarm.hour);
		timePicker.setCurrentMinute(alarm.minutes);

		// 设置星期的焦点
		boolean weeks[] = alarm.daysOfWeek.getBooleanArray();
		if (weeks != null && 7 == weeks.length) {
			for (int i = 0; i < weeks.length; i++) {
				myWBList[i].setFlag(weeks[i]);
			}
		}

		String name = alarm.musicBean.get(0).getTitle();
		curMusic.setText(name);

		tag.setText(alarm.getLabel());

	}

	// 修改数据完成
	private void updateAlarm() {
		if (null != alarm) {
			alarm.hour = timePicker.getCurrentHour();
			alarm.minutes = timePicker.getCurrentMinute();
			
			//设置星期重复
			for (int i = 0; i < myWBList.length; i++) {
				boolean flag=myWBList[i].getFlag();
				alarm.daysOfWeek.set(i, flag);
			}
			
			//设置音乐信息
			
		}

	}

	// 进入添加流程
	private void addAlarm() {

	}
	
	
//	private void sendFileEditMsg2YinXiang(String musicPath, String editType,
//			String param) {
//
//		if (!NetworkUtils.isWifiConnected(this)) {
//			Toast.makeText(this, "请链接无线网络", Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		if (!StringUtils.hasLength(ClientSendCommandService.serverIP)) {
//			Toast.makeText(this, "手机未连接机顶盒，请检查网络", Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		// 显示操作等待进度提示
//		if (null != mFileEdit && !editType.equals(MusicUtils.EDIT_COPYTO_YINXIANG)) {
//			String msg = editType.equals(MusicUtils.EDIT_REQUEST_MUSICS) ? "音响文件获取中": "操作正在进行,请稍后";
//			mFileEdit.showProgressDialog(msg);
//		}
//
//		try {
//			
//			String tmpHttpAddress = musicPath;
//			// 获取IP和外部存储路径
//			String ipAddress = NetworkUtils.getLocalHostIp();
//			if (STORAGE_MOBILE == curStorage) {
//				tmpHttpAddress = getHttpAddressOfFile(ipAddress, musicPath);
//			}
//			// 封装文件为json格式
//			JSONObject sendObj = new JSONObject();
//			JSONArray array = new JSONArray();
//
//			// music urls
//			// 文件编辑类型： copy、clock
//			array.put(0, editType);
//			array.put(1, musicPath);
//
//			// 第二个参数：如reName：则发送新的文件名。否则，赋值文件httpAddress
//			if (StringUtils.hasLength(param)) {
//				array.put(2, param);
//			} else {
//				array.put(2, tmpHttpAddress);
//			}
//
//			sendObj.put("fileEdit", array);
//			// client ip
//			sendObj.put("client_ip", ipAddress);
//
//			// 发送播放地址
//			ClientSendCommandService.msg = sendObj.toString();
//			ClientSendCommandService.handler.sendEmptyMessage(4);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			Toast.makeText(this, "音乐文件获取失败", Toast.LENGTH_SHORT).show();
//			mFileEdit.closeProgressDialog();
//		}
//	}

}
