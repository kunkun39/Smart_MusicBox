package com.changhong.yinxiang.alarm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.service.SendTCPData;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.WeekButton;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.AlarmMainActivity;
import com.changhong.yinxiang.activity.YinXiangMusicViewActivity;
import com.changhong.yinxiang.music.MusicEditServer;
import com.changhong.yinxiang.music.MusicUtils;

public class SetAlarmActvity extends Activity {

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

	private int curentId=1;// 设置该alarm的ID。
	private int resultCode = 0;// 0为更改，1位新增

	private boolean musicIsEmpty = true;

	private SendTCPData sendTCP = null;
	private String serverIP;

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
		initView();
		initData();
	}

	protected void initView() {
		// TODO Auto-generated method stub
		setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
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

	protected void initData() {
		// TODO Auto-generated method stub
		// 发送获取音乐列表请求，并启动接收线程
		sendFileEditMsg2YinXiang("", MusicUtils.EDIT_REQUEST_MUSICS, "");
		MusicEditServer.creatFileEditServer().communicationWithServer(handler,
				MusicUtils.ACTION_SOCKET_COMMUNICATION,
				MusicUtils.EDIT_REQUEST_MUSICS);

		
		

		Intent intent = getIntent();
		int position = intent.getIntExtra("select", -1);
		if (position < 0) {
			state = State.add;
			resultCode = 1;

			musicIsEmpty = true;

			intAddAlarm();

		} else {
			state = State.update;
			resultCode = 0;

			musicIsEmpty = false;

			initUpdateAlarm(position);
		}

		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				timePicker.setCurrentHour(hourOfDay);
				timePicker.setCurrentMinute(minute);
			}
		});
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

		if (alarm.musicBean != null && !alarm.musicBean.isEmpty()) {
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
		for (int i = 0; i < 5; i++) {
			myWBList[i].setFlag(true);
		}
	}

	// 修改数据完成
	private void updateAlarm() {

		if (null != alarm) {
			timePicker.clearFocus();
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

//		// 计算当前闹铃的ID值是多少,从1开始，看那个数字没有使用就用那个
//		if (AlarmMainActivity.mAlarmList != null
//				&& AlarmMainActivity.mAlarmList.size() > 0) {
//			length = AlarmMainActivity.mAlarmList.size();
//			for (int a = 1; a < length + 2; a++) {
//				boolean idflag=true;
//				for (int i = 0; i < length; i++) {
//					int cache = AlarmMainActivity.mAlarmList.get(i).getId();
//					if(a==cache){
//						idflag=false;
//					}
//				}
//				if(idflag){
//					curentId=a;
//					break;
//				}
//			}
//		}
		
		//计算当前闹铃的ID，看最大ID是多少，则当前ID为最大ID加1(因为音响端的策略是如此，若不这样处理，在添加后立即修改，发过去的ID不能与添加的ID匹配)
		if (AlarmMainActivity.mAlarmList != null
				&& AlarmMainActivity.mAlarmList.size() > 0) {
			length = AlarmMainActivity.mAlarmList.size();
			int aID=0;
			for(int a=0;a<length;a++){
				aID=AlarmMainActivity.mAlarmList.get(a).getId();
				if(curentId<aID){
					curentId=aID;
				}
			}
			curentId+=1;
		}
		alarm.setId(curentId);
		alarm.setHour(timePicker.getCurrentHour());
		alarm.setMinutes(timePicker.getCurrentMinute());
		alarm.setLabel(tag.getText().toString());
		alarm.setEnabled(true);

		for (int i = 0; i < myWBList.length; i++) {
			alarm.daysOfWeek.set(i, myWBList[i].getFlag());
		}

		ArrayList<MusicBean> musics = new ArrayList<MusicBean>();

		if (currentState != null && currentState.length > 0) {
			for (int j = 0; j < currentState.length; j++) {
				if (currentState[j]) {
					MusicBean cacheMusic = musicListAll.get(j);
					cacheMusic.setmId(curentId);
					musics.add(cacheMusic);
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
			array.put(1, "{}");
		
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
			Log.i("mmmm", "sendObj" + sendObj.toString());
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
		musicIsEmpty = false;
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
									setAlarmMusics(dialog);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									cancelDismiss(dialog, true);
								}
							}).show();
		}
	}

	private void setAlarmMusics(DialogInterface dialog) {

		boolean emptyFlag = false;
		musicIsEmpty=false;
		// 判断音乐列表是否为空
		for (int b = 0; b < currentState.length; b++) {
			if (currentState[b]) {
				emptyFlag = true;
			}
		}
		if (!emptyFlag) {
			musicIsEmpty = true;
			Toast.makeText(this, "音乐列表不能为空，请添加对应的音乐文件", Toast.LENGTH_SHORT)
					.show();
			curMusic.setText("");
			cancelDismiss(dialog, false);
		} else {

			// 增加判断音乐文件是否有效功能
			for (int k = 0; k < musicListInit.size(); k++) {
				boolean flag = false;// 代表该首音乐是否是有效音乐，true代表音乐文件存在，false代表音乐文件不存在
				MusicBean music = musicListInit.get(k);
				String name = music.getTitle();
				for (int a = 0; a < musicListAll.size(); a++) {
					if (name.equals(musicListAll.get(a).getTitle())) {
						flag = true;
					}
				}
				if (!flag) {
					musicListInit.remove(music);
					k = k - 1;
				}
			}

			for (int i = 0; i < currentState.length; i++) {
				if (updateContent[i] != 1) {
					continue;
				}
				if (currentState[i]) {

					int musicListSize = musicListInit.size();
					if (0 == musicListSize) {
						MusicBean music = musicListAll.get(i);
						music.setmId(alarm.id);
						musicListInit.add(music);
					} else {
						for (int j = 0; j < musicListSize; j++) {
							if (musicListAll.get(i).getTitle()
									.equals(musicListInit.get(j).getTitle())) {
								break;
							} else if (j == (musicListSize - 1)) {
								MusicBean music = musicListAll.get(i);
								music.setmId(alarm.id);
								musicListInit.add(music);

							}
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
			// 设置音乐名字，用设置的音乐列表中的第一首歌曲作为名字
			if (musicListInit != null && musicListInit.size() > 0) {
				String name = musicListInit.get(0).getTitle();
				curMusic.setText(name);
			} else {
				curMusic.setText("");
			}
			cancelDismiss(dialog, true);
		}
	}

	// 回传数据给闹铃主界面，并且发送给音响。
	private void dealResultData() {

		if (!musicIsEmpty) {
			String content = null;
			Intent intent = new Intent();
			String str = ResolveAlarmInfor.alarmToStr(alarm);
			intent.putExtra("alarm", str);
			SetAlarmActvity.this.setResult(resultCode, intent);
			// 考虑是否用TCP发送数据回音响端???
			switch (state) {
			case update:
//				ClientSendCommandService.msg = Alarm.update + curentId + "|"
//						+ str;
				 content = Alarm.update + curentId + "|" + str;
				break;
			case add:

//				ClientSendCommandService.msg = Alarm.insert + str;
				 content = Alarm.insert  + str;
				break;
			default:
				break;
			}
			Message msg=ClientSendCommandService.handler.obtainMessage();
			msg.what=7;
			msg.obj=content;
			ClientSendCommandService.handler.sendMessage(msg);
		} else {
			Toast.makeText(this, "音乐列表不能为空，请添加对应的音乐文件,此次操作无效!",
					Toast.LENGTH_SHORT).show();
		}
		finish();

	}

	// 设置点击dialog按钮后是否关闭该对话框，false不关闭，true关闭
	private void cancelDismiss(DialogInterface dialog, boolean flag) {
		Field field = null;
		try {
			field = dialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			if (field != null) {
				field.setAccessible(true);
				field.set(dialog, flag);
				dialog.dismiss();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	
}
