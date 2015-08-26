package com.changhong.yinxiang.activity;

import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.music.MusicEdit;
import com.changhong.yinxiang.music.MusicEditServer;
import com.changhong.yinxiang.music.YinXiangMusic;
import com.changhong.yinxiang.music.YinXiangMusicAdapter;
import com.changhong.yinxiang.nanohttpd.HTTPDService;


public class YinXiangMusicViewActivity extends Activity {

	/************************************************** IP连接部分 *******************************************************/

	public static TextView title = null;
	private Button listClients;
	private Button back;
	private ListView clients = null;
	private BoxSelectAdapter IpAdapter;

	/************************************************** 音频部分 *******************************************************/
	/**
	 * 从上个Activity传过来的musics
	 */
	private static List<?> musics;

	private static Map<String, List<YinXiangMusic>> model;

	/**
	 * 演唱者
	 */
	private TextView musicSinger;
	private String singerName;
	private static List<String> musicList;

	/**
	 * Image List adapter
	 */
	private YinXiangMusicAdapter musicAdapter;
	/**
	 * 音频浏览部分
	 */
	private ListView musicListView;

	/**
	 * 音频推送按钮
	 */
	private Button musicSend;

	/**
	 * 音频已经选择INFO
	 */
	// public static TextView musicSelectedInfo;

	/**
	 * YD add 20150806 for fileEdit 音频文件编辑功能
	 */
	MusicEdit mFileEdit = null;
	YinXiangMusic mEditMusic = null;

	// 设备类型
	private RadioGroup radioGroup;
	private int curStorage;
	// 存储设备1——手机
	final int STORAGE_MOBLIE = 1;
	// 存储设备2——音响设备
	final int STORAGE_AUDIOEQUIPMENT = 2;
	// 存储设备3——USB
	final int STORAGE_USB = 3;
		
	public static final int  FILE_EDIT_DIALOG=1;
	public static final int  FILE_EDIT_CLOCK=2;
	public static final int  FILE_EDIT_COPY=3;
	public static final int  FILE_EDIT_RENAME=4;
	public static final int  FILE_EDIT_CANCLE=5;

	
	//请求音响设备的音乐文件
	public static final int  REQUEST_AUDIOEQUIPMENT_MUSIC=6;
	public static final int  SHOW_AUDIOEQUIPMENT_MUSICLIST=7;
	public static final int  SHOW_ACTION_RESULT=8;

	
	MusicEditServer mMusicEditServer=null;
	String keyStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView();
		initEvent();
	}

	private void initView() {
		setContentView(R.layout.activity_yinxiang_music_view);

		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (Button) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);

		/**
		 * 音频部分
		 */
		musicListView = (ListView) findViewById(R.id.yinxiang_music_list_view);
		Intent intent = getIntent();
		keyStr = intent.getStringExtra("KeyWords");

		musicAdapter = new YinXiangMusicAdapter(this, mHandle, keyStr, null);
		musicListView.setAdapter(musicAdapter);

		musicSend = (Button) findViewById(R.id.yinxing_music_tuisong);
		// musicSelectedInfo =
		// (TextView)findViewById(R.id.yinxing_music_tuisong_info);

		radioGroup = (RadioGroup) findViewById(R.id.music_rgtab);
		curStorage = STORAGE_MOBLIE;
		mFileEdit = new MusicEdit(this,mHandle);
		mMusicEditServer= MusicEditServer.creatFileEditServer();

	}

	private void initEvent() {

		/**
		 * IP连接部分
		 */
		IpAdapter = new BoxSelectAdapter(YinXiangMusicViewActivity.this,
				ClientSendCommandService.serverIpList);
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
				title.setText(ClientSendCommandService	.getCurrentConnectBoxName());
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
						Toast.makeText(YinXiangMusicViewActivity.this,
								"未获取到服务器IP", Toast.LENGTH_LONG).show();
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

		/**
		 * 音频发送部分
		 */
		musicSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (NetworkUtils
							.isWifiConnected(YinXiangMusicViewActivity.this)) {
						if (!StringUtils
								.hasLength(ClientSendCommandService.serverIP)) {
							Toast.makeText(YinXiangMusicViewActivity.this,
									"手机未连接音箱，请确认后再推送", Toast.LENGTH_SHORT)
									.show();
							return;
						}
						MyApplication.vibrator.vibrate(100);

						/**
						 * 转换音乐的路径,准备发送音乐的数据
						 */

						if (YinXiangMusicAdapter.selectMusics.isEmpty()) {
							Toast.makeText(YinXiangMusicViewActivity.this,
									"请选择推送的歌曲", Toast.LENGTH_LONG).show();
						} else {
							// if(YinXiangMusicAdapter.selectMusics.size()>=4){
							// Toast.makeText(YinXiangMusicViewActivity.this,
							// "暂时最多支持推送4首歌曲", Toast.LENGTH_LONG).show();
							// }else{
							String ipAddress = NetworkUtils.getLocalHostIp();
							String httpAddress = "http://" + ipAddress + ":"
									+ HTTPDService.HTTP_PORT;
							JSONObject o = new JSONObject();
							JSONArray array = new JSONArray();

							for (YinXiangMusic selectMusic : YinXiangMusicAdapter.selectMusics) {
								String tempPath = selectMusic.getPath();
								String title = selectMusic.getTitle();
								String artist = selectMusic.getArtist();
								int duration = selectMusic.getDuration();

								if (tempPath
										.startsWith(HTTPDService.defaultHttpServerPath)) {
									tempPath = tempPath.replace(
											HTTPDService.defaultHttpServerPath,
											"").replace(" ", "%20");
								} else {
									for (String otherHttpServerPath : HTTPDService.otherHttpServerPaths) {
										if (tempPath
												.startsWith(otherHttpServerPath)) {
											tempPath = tempPath.replace(
													otherHttpServerPath, "")
													.replace(" ", "%20");
										}
									}
								}
								JSONObject music = new JSONObject();
								music.put("tempPath", httpAddress + tempPath);
								music.put("title", title);
								music.put("artist", artist);
								music.put("duration", duration);
								array.put(music);
							}
							o.put("musicss", array.toString());

							File jsonFile = new File(
									HTTPDService.defaultHttpServerPath
											+ "/MusicList.json");
							if (jsonFile.exists()) {
								jsonFile.delete();
							}
							jsonFile.createNewFile();

							FileWriter fw = new FileWriter(jsonFile);
							fw.write(o.toString(), 0, o.toString().length());
							fw.flush();
							fw.close();

							// 发送播放地址
							ClientSendCommandService.msg = "GetMusicList:"	+ httpAddress + "/MusicList.json";
							ClientSendCommandService.handler	.sendEmptyMessage(4);
							// }
						}
					} else {
						Toast.makeText(YinXiangMusicViewActivity.this,
								"请链接无线网络", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(YinXiangMusicViewActivity.this, "音频获取失败",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// YD add 20150810 for fileEdit
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// 设备变化
						curStorage = matchFragmentIndex(checkedId);
						//当前设备为音响，请求音响音乐文件。
						if(STORAGE_AUDIOEQUIPMENT  ==  curStorage){
							mHandle.sendEmptyMessage(REQUEST_AUDIOEQUIPMENT_MUSIC);
						}else{					  
							musicAdapter = new YinXiangMusicAdapter(YinXiangMusicViewActivity.this, mHandle, keyStr, null);
							musicListView.setAdapter(musicAdapter);
						}
					}
				});
		((RadioButton) radioGroup.getChildAt(curStorage - 1)).setChecked(true);

	}

	/**
	 * 根据index匹配fragment的索引。
	 * 
	 * @param index
	 * @return
	 */
	private int matchFragmentIndex(int index) {
		int reValue = 1;
		int childCount = radioGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			RadioButton child = (RadioButton) radioGroup.getChildAt(i);
			if (null != child && index == child.getId()) {
				reValue = i + 1;
				break;
			}
		}
		return reValue;
	}

	/************************************************************** YD add 20150806 发送文件编辑信息到音响端 *******************************************************************************/

	private void sendFileEditMsg2YinXiang(String musicPath, String editType, String param) {

		if (!NetworkUtils.isWifiConnected(this)) {
			Toast.makeText(this, "请链接无线网络", Toast.LENGTH_SHORT).show();
			return;
		}

		if (!StringUtils.hasLength(ClientSendCommandService.serverIP)) {
			Toast.makeText(this, "手机未连接机顶盒，请检查网络", Toast.LENGTH_SHORT).show();
			return;
		}

		/**
		 * 封装发送信息。
		 */
		try {
			MyApplication.vibrator.vibrate(100);

			// 获取IP和外部存储路径
			String ipAddress = NetworkUtils.getLocalHostIp();
			String httpAddress = "http://" + ipAddress + ":" + HTTPDService.HTTP_PORT;

			String newMusicPath = null;
			if (musicPath.startsWith(HTTPDService.defaultHttpServerPath)) {
				newMusicPath = convertFileUrlToHttpURL(musicPath.replace(HTTPDService.defaultHttpServerPath, ""));

			} else {
				for (String otherHttpServerPath : HTTPDService.otherHttpServerPaths) {
					if (musicPath.startsWith(otherHttpServerPath)) {
						newMusicPath = convertFileUrlToHttpURL(musicPath.replace(otherHttpServerPath, ""));
					}
				}
			}

			String tmpHttpAddress = httpAddress + newMusicPath;

			// 判断URL是否符合规范，如果不符合规范，就1重命名文件
			try {
				URI.create(tmpHttpAddress);
			} catch (Exception e) {
				try {

					/**
					 * 创建新的文件
					 */
					if(!musicPath.equals("")){
							File illegalFile = new File(musicPath);
							String fullpath = illegalFile.getAbsolutePath();
							String filename = illegalFile.getName();
							String filepath = fullpath.replace(File.separator+ filename, "");
							String[] tokens = StringUtils.delimitedListToStringArray(filename, ".");
							String filenameSuffix = tokens[tokens.length - 1];
							String newFile = filepath + File.separator	+ StringUtils.getRandomString(15) + "."	+ filenameSuffix;
							Runtime.getRuntime().exec("mv " + fullpath + " " + newFile);
							tmpHttpAddress = httpAddress + newFile;
						
							/**
							 * 更改Content Provider的文件
							 */						
							ContentResolver mContentResolver = getContentResolver();
							Uri mAudioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
							ContentValues values = new ContentValues();
							values.put(MediaStore.Audio.Media.DATA, newFile);
							mContentResolver.update(mAudioUri, values,MediaStore.Audio.Media.DATA + " = '" + fullpath
											+ "'", null);
					}

				} catch (Exception e1) {
					e.printStackTrace();
					Toast.makeText(this, "对不起，音乐文件获取有误，不能正常操作！",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}

			// 封装文件为json格式
			JSONObject sendObj = new JSONObject();
			JSONArray array = new JSONArray();
		
			// music urls		
			// 文件编辑类型： copy、clock
			array.put(0, editType);
		
			//第二个参数：如reName：则发送新的文件名。否则，赋值文件httpAddress
			if(editType.equals("reName")){
		     	array.put(1, param);	
			}else{
		     	array.put(1, tmpHttpAddress);
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

	
	private Handler mHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			String musicPath;
			// 显示文件编辑对话框
			switch(msg.what){
			
            case REQUEST_AUDIOEQUIPMENT_MUSIC:
				
				if(STORAGE_AUDIOEQUIPMENT== curStorage){
						//发送播放地址
					    sendFileEditMsg2YinXiang("", "requestMusicList","");
						mMusicEditServer.accept(mHandle,"requestMusicList");
						
				}
				break;		
				
            case SHOW_ACTION_RESULT:
            	
				if(STORAGE_AUDIOEQUIPMENT== curStorage){
					  //机顶盒返回信息
				      String respond=(String) msg.obj;	      
				      if(respond.contains("OK") || respond.contains("success")){
				    	  respond="操作成功";
				      }else {
				    	  respond="操作失败";
				      }
			    	     Toast.makeText(YinXiangMusicViewActivity.this, respond, Toast.LENGTH_SHORT).show();

				}
			     break;	
            case SHOW_AUDIOEQUIPMENT_MUSICLIST:
				
				if(STORAGE_AUDIOEQUIPMENT== curStorage){
					    String musicList=(String) msg.obj;
						musicAdapter = new YinXiangMusicAdapter(YinXiangMusicViewActivity.this, mHandle, keyStr, musicList);
						musicListView.setAdapter(musicAdapter);
				}
				break;	
			//显示文件编辑对话框
			case FILE_EDIT_DIALOG:
					// 1、获取当前焦点音乐文件信息
					mEditMusic = (YinXiangMusic) msg.obj;
					// 2、显示文件编辑对话框
					if (mFileEdit != null) {
						mFileEdit.showEditDialog(mEditMusic);
					}
				break;				
			//文件拷贝：手机----->音响
			case FILE_EDIT_COPY:
				if(STORAGE_MOBLIE== curStorage){
						 musicPath = mEditMusic.getPath();
						sendFileEditMsg2YinXiang(musicPath, "copyToAudio","");
				}
				break;
		    //设置音响端音乐闹铃-并同步拷贝文件到音响
			case FILE_EDIT_CLOCK:
				
				if(STORAGE_MOBLIE== curStorage){
					    musicPath = mEditMusic.getPath();
						sendFileEditMsg2YinXiang(musicPath, "clockRing","");		
				}
				break;
				
			//实现远程音响文件重命名。
			case FILE_EDIT_RENAME:
				
				if(STORAGE_AUDIOEQUIPMENT== curStorage){
				    musicPath = mEditMusic.getPath();
				    String newName=(String) msg.obj;
					sendFileEditMsg2YinXiang(musicPath, "reName",newName);		
					mMusicEditServer.accept(mHandle,"reName");
				}
				break;
				
		   //实现远程音响文件删除。
			case FILE_EDIT_CANCLE:
				if(STORAGE_AUDIOEQUIPMENT== curStorage){
					    musicPath = mEditMusic.getPath();
					   sendFileEditMsg2YinXiang(musicPath, "cancle","");	
						mMusicEditServer.accept(mHandle,"cancle");
				}
				break;		
				default:
					break;
			}
		}
	};

	/**
	 * 特殊字符转换
	 * 
	 * @param url
	 *            url字符串
	 * @return
	 */
	public String convertFileUrlToHttpURL(String url) {
		if (null != url && url.length() > 0) {
			return url.replace("%", "%25").replace(" ", "%20")
					.replace("+", "%2B").replace("#", "%23")
					.replace("&", "%26").replace("=", "%3D")
					.replace("?", "%3F").replace("^", "%5E");
		}
		return url;
	}

	/********************************************** 系统发发重载 *********************************************************/

	@Override
	protected void onResume() {
		super.onResume();
		if (ClientSendCommandService.titletxt != null) {
			title.setText(ClientSendCommandService.titletxt);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMusicEditServer.close();
	}	
	
	
	
	
}
