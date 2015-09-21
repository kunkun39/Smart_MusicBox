package com.changhong.yinxiang.alarm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.service.SendTCPData;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.WeekButton;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.AlarmMainActivity;
import com.changhong.yinxiang.activity.BaseActivity;
import com.changhong.yinxiang.activity.YinXiangMusicViewActivity;
import com.changhong.yinxiang.music.MusicEditServer;
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

	private List<MusicBean> musicListAll = new ArrayList<MusicBean>();
	private ArrayList<MusicBean> musicListInit = new ArrayList<MusicBean>();
	private boolean currentState[];
	private int updateContent[];// 记录操作过的数据。0未操作，非0操作过。

	private int curentId;// 设置该alarm的ID。
	private int resultCode = 0;// 0为更改，1位新增

	private SendTCPData sendTCP = null;

	// 进入设置界面后，先设置状态。
	private enum State {
		add, delete, update, get
	}

	// 默认状态
	private State state = State.add;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case YinXiangMusicViewActivity.SHOW_AUDIOEQUIPMENT_MUSICLIST:
				String str = (String) msg.obj;
				musicListAll = pareJsonToMusicList(str);
				currentState = new boolean[musicListAll.size()];
				break;

			}
		}

	};

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
					break;
				default:
					break;
				}
				dealResultData();
				finish();
				break;
			case R.id.cancel:
				finish();
				break;

			case R.id.musics:
				showMusics();
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

		confirm.setOnClickListener(myClickListener);
		cancel.setOnClickListener(myClickListener);
		musics.setOnClickListener(myClickListener);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		// 发送获取音乐列表请求，并启动接收线程
		sendFileEditMsg2YinXiang("", MusicUtils.EDIT_REQUEST_MUSICS, "");
		MusicEditServer.creatFileEditServer().communicationWithServer(handler,
				MusicUtils.ACTION_SOCKET_COMMUNICATION,
				MusicUtils.EDIT_REQUEST_MUSICS);

		String serverIP = ClientSendCommandService.serverIP;
		if (serverIP != null) {
			sendTCP = SendTCPData.getInstace();
			sendTCP.setDesIP(serverIP);
			sendTCP.startPlaying();
		}

		Intent intent = getIntent();
		int position = intent.getIntExtra("select", -1);
		if (position < 0) {
			state = State.add;
			resultCode = 1;
			intAddAlarm();

		} else {
			state = State.update;
			resultCode = 0;
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
		alarm = AlarmMainActivity.mAlarmList.get(position);
		timePicker.setCurrentHour(alarm.hour);
		timePicker.setCurrentMinute(alarm.minutes);

		// 设置星期的焦点
		boolean weeks[] = alarm.daysOfWeek.getBooleanArray();
		if (weeks != null && 7 == weeks.length) {
			for (int i = 0; i < weeks.length; i++) {
				myWBList[i].setFlag(weeks[i]);
			}
		}

		if (alarm.musicBean != null) {
			String name = alarm.musicBean.get(0).getTitle();
			curMusic.setText(name);
		}
		tag.setText(alarm.getLabel());

	}

	// 初始化进入添加流程
	private void intAddAlarm() {
		if (null == alarm) {
			alarm = new Alarm();
		}
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);

		timePicker.setCurrentHour(hour);
		timePicker.setCurrentMinute(minute);
		for (int i = 1; i < 6; i++) {
			myWBList[i].setFlag(true);
		}
	}

	// 修改数据完成
	private void updateAlarm() {
		if (null != alarm) {
			alarm.hour = timePicker.getCurrentHour();
			alarm.minutes = timePicker.getCurrentMinute();
			curentId = alarm.getId();
			// 设置星期重复
			for (int i = 0; i < myWBList.length; i++) {
				boolean flag = myWBList[i].getFlag();
				alarm.daysOfWeek.set(i, flag);
			}

		}

	}

	// 进入添加流程
	private void addAlarm() {
		int length = 0;
		int id=0;
		if (AlarmMainActivity.mAlarmList != null) {
			length = AlarmMainActivity.mAlarmList.size();
			id = AlarmMainActivity.mAlarmList.get(length - 1).getId();
		}
		curentId = id + 1;

		alarm.setId(curentId);
		alarm.setHour(timePicker.getCurrentHour());
		alarm.setMinutes(timePicker.getCurrentMinute());
		alarm.setEnabled(true);

		for (int i = 0; i < myWBList.length; i++) {
			alarm.daysOfWeek.set(i, myWBList[i].getFlag());
		}
		alarm.setLabel(tag.getText().toString());

		ArrayList<MusicBean> musics = new ArrayList<MusicBean>();

		if (currentState != null && currentState.length > 0) {
			for (int j = 0; j < currentState.length; j++) {
				if (currentState[j]) {
					musics.add(musicListAll.get(j));
					musics.get(j).setmId(curentId);
				}
			}
		}
		alarm.setMusicBean(musics);
	}

	private void sendFileEditMsg2YinXiang(String musicPath, String editType,
			String param) {

		if (!NetworkUtils.isWifiConnected(this)) {
			Toast.makeText(this, "请链接无线网络", Toast.LENGTH_SHORT).show();
			return;
		}

		if (!StringUtils.hasLength(ClientSendCommandService.serverIP)) {
			Toast.makeText(this, "手机未连接机顶盒，请检查网络", Toast.LENGTH_SHORT).show();
			return;
		}

		try {

			String tmpHttpAddress = musicPath;
			// 获取IP和外部存储路径
			String ipAddress = NetworkUtils.getLocalHostIp();
			// 封装文件为json格式
			JSONObject sendObj = new JSONObject();
			JSONArray array = new JSONArray();

			// music urls
			// 文件编辑类型： copy、clock
			array.put(0, editType);
			array.put(1, musicPath);

			// 第二个参数：如reName：则发送新的文件名。否则，赋值文件httpAddress
			if (StringUtils.hasLength(param)) {
				array.put(2, param);
			} else {
				array.put(2, tmpHttpAddress);
			}

			sendObj.put("fileEdit", array);
			// client ip
			sendObj.put("client_ip", ipAddress);

			// 发送播放地址
			ClientSendCommandService.msg = sendObj.toString();
			ClientSendCommandService.handler.sendEmptyMessage(4);

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "音乐文件获取失败", Toast.LENGTH_SHORT).show();
		}
	}

	// 将收到的音乐信息json转换成list
	private ArrayList<MusicBean> pareJsonToMusicList(String jsonStr) {
		ArrayList<MusicBean> list = new ArrayList<MusicBean>();
		if (null != jsonStr) {
			try {

				JSONTokener jsonParser = new JSONTokener(jsonStr);
				// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
				// 如果此时的读取位置在"name" : 了，那么nextValue就是"返回对象了"（String）

				JSONArray msgObject = (JSONArray) jsonParser.nextValue();

				// 获取消息类型
				// JSONArray jsonObjs = msgObject.getJSONArray("musicList");
				for (int i = 0; i < msgObject.length(); i++) {
					JSONObject musicObj = ((JSONObject) msgObject.opt(i));
					int id = musicObj.getInt("id");
					String title = musicObj.getString("title");
					String path = musicObj.getString("path");
					String artist = musicObj.getString("artist");
					String fileUrl = musicObj.getString("httpUrl");

					MusicBean music = new MusicBean();
					music.setTitle(title);
					music.setUrl(path);
					music.setArtist(artist);
					music.setId(id);
					music.setmId(curentId);
					// 增加文件远程访问定位符
					list.add(music);
				}
			} catch (JSONException ex) {
				// 异常处理代码
				ex.printStackTrace();
			}

		}
		return list;
	}

	// 显示音乐列表
	private void showMusics() {
		if (musicListAll.size() > 0) {
			String names[] = new String[musicListAll.size()];
			for (int i = 0; i < musicListAll.size(); i++) {
				names[i] = musicListAll.get(i).getTitle();
			}
			musicListInit = alarm.getMusicBean();
			// 设置复选框初始选中状态

			updateContent = new int[musicListAll.size()];
			for (int i = 0; i < currentState.length; i++) {
				currentState[i] = false;
				updateContent[i] = 0;
				for (int j = 0; j < musicListInit.size(); j++) {
					if (musicListAll.get(i).getTitle()
							.equals(musicListInit.get(j).getTitle())) {
						currentState[i] = true;
						break;
					}
				}

			}

			new AlertDialog.Builder(this)
					.setTitle("复选框")
					.setMultiChoiceItems(names, currentState,
							new OnMultiChoiceClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which, boolean isChecked) {
									// TODO Auto-generated method stub
									currentState[which] = isChecked;
									updateContent[which] = 1;
								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									// 设置复选框监听器
									setAlarmMusics();
								}
							}).setNegativeButton("取消", null).show();
		}
	}

	private void setAlarmMusics() {
		for (int i = 0; i < currentState.length; i++) {
			if (updateContent[i] != 1) {
				continue;
			}
			if (currentState[i]) {
				for (int j = 0; j < musicListInit.size(); j++) {
					if (musicListAll.get(i).getTitle()
							.equals(musicListInit.get(j).getTitle())) {
						break;
					} else if (j == (musicListInit.size() - 1)) {
						MusicBean music = musicListAll.get(i);
						music.setmId(alarm.id);
						musicListInit.add(music);
					}
				}

			} else {
				for (int j = 0; j < musicListInit.size(); j++) {
					if (musicListAll.get(i).getTitle()
							.equals(musicListInit.get(j).getTitle())) {
						musicListInit.remove(j);
					}
				}
			}
		}
		if (alarm != null) {
			alarm.setMusicBean(musicListInit);
		}
	}

	// 回传数据给闹铃主界面，并且发送给音响。

	private void dealResultData() {
		String content = null;
		Intent intent = new Intent();
		String str = ResolveAlarmInfor.alarmToStr(alarm);
		intent.putExtra("alarm", str);
		SetAlarmActvity.this.setResult(resultCode, intent);
		String ip = ClientSendCommandService.serverIP;
		// 考虑是否用TCP发送数据回音响端???
		switch (state) {
		case update:
			ClientSendCommandService.msg = Alarm.update + curentId + "|" + str;
			// content = Alarm.update + curentId + "|" + str;
			break;
		case add:

			ClientSendCommandService.msg = Alarm.insert + str;
			// ClientSendCommandService.handler.sendEmptyMessage(1);
			// content = Alarm.update + curentId + "|" + str;
			break;
		default:
			break;
		}
		// if (content != null && ip != null) {
		// sendTCP.addData(content, ip);
		// }
		ClientSendCommandService.handler.sendEmptyMessage(1);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (sendTCP != null) {
			sendTCP.stopPlaying();
		}
		super.onDestroy();
	}

}
