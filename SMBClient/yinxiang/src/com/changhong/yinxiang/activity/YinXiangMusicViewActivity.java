package com.changhong.yinxiang.activity;

import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
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
import com.changhong.yinxiang.music.MusicUtils;
import com.changhong.yinxiang.music.YinXiangMusic;
import com.changhong.yinxiang.music.YinXiangMusicAdapter;
import com.changhong.yinxiang.nanohttpd.HTTPDService;

public class YinXiangMusicViewActivity extends BaseActivity {

	/************************************************** IP连接部分 *******************************************************/

//	public static TextView title = null;
//	private Button listClients;
//	private Button back;
//	private ListView clients = null;
//	private BoxSelectAdapter IpAdapter;

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
	private ImageView musicSend;

	/**
	 * 全选按钮
	 */
	private CheckBox checkAll;

	/**
	 * 音频已经选择INFO
	 */
	// public static TextView musicSelectedInfo;

	/**
	 * YD add 20150806 for fileEdit 音频文件编辑功能
	 */
	MusicEdit mFileEdit = null;
	YinXiangMusic mEditMusic = null;
	
	String  saveNewName=null;

	// 设备类型
	private RadioGroup radioGroup;
	private int curStorage;
	// 存储设备1——手机
	public final int STORAGE_MOBILE = 1;
	// 存储设备2——音响设备
	public final int STORAGE_YINXIANG = 2;
	// 存储设备3——USB
	public final int STORAGE_USB = 3;

	public static final int FILE_EDIT_DIALOG = 1;
	public static final int FILE_EDIT_CLOCK = 2;
	public static final int FILE_EDIT_COPY = 3;
	public static final int FILE_EDIT_RENAME = 4;
	public static final int FILE_EDIT_REMOVE = 5;
	
	public static final int COMMUNICATION_ERROR = 1000;

	// 请求音响设备的音乐文件
	public static final int REQUEST_AUDIOEQUIPMENT_MUSIC = 6;
	public static final int SHOW_AUDIOEQUIPMENT_MUSICLIST = 7;
	public static final int SHOW_ACTION_RESULT = 8;

	MusicEditServer mMusicEditServer = null;
	String keyStr;

	private String remoteMusics = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void initView() {
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

		// musicAdapter = new YinXiangMusicAdapter(this, mHandle, keyStr, null);
		// musicListView.setAdapter(musicAdapter);

		musicSend = (ImageView) findViewById(R.id.yinxing_music_tuisong);
		// musicSelectedInfo =
		// (TextView)findViewById(R.id.yinxing_music_tuisong_info);
		checkAll = (CheckBox) findViewById(R.id.yinxing_music_checkall);
		radioGroup = (RadioGroup) findViewById(R.id.music_rgtab);
		curStorage = STORAGE_MOBILE;
		mFileEdit = new MusicEdit(this, mHandle);
		mMusicEditServer = MusicEditServer.creatFileEditServer();


	}

	protected void initData() {
        super.initData();		
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
								String tempPath = selectMusic.getPath().trim();
								String title = selectMusic.getTitle().trim();
								String artist = selectMusic.getArtist().trim();
								int duration = selectMusic.getDuration();
								JSONObject music = new JSONObject();

								if (STORAGE_MOBILE == curStorage) {
								
										if (tempPath.startsWith(HTTPDService.defaultHttpServerPath)) {
											tempPath = tempPath.replace(
													HTTPDService.defaultHttpServerPath,
													"").replace(" ", "%20");
										} else {	for (String otherHttpServerPath : HTTPDService.otherHttpServerPaths) {
												if (tempPath.startsWith(otherHttpServerPath)) {
													tempPath = tempPath.replace(
															otherHttpServerPath, "")
															.replace(" ", "%20");
												}
											}
										}
										tempPath=httpAddress + tempPath;
								}
								music.put("tempPath",  tempPath);
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
							ClientSendCommandService.msg = "GetMusicList:"
									+ httpAddress + "/MusicList.json";
							ClientSendCommandService.handler
									.sendEmptyMessage(4);
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

		// 全选/取消全选
		checkAll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CheckBox check = (CheckBox) v;
				if (check.isChecked()) {
					musicAdapter.setMusicsCheckAll(true);
				} else {
					musicAdapter.setMusicsCheckAll(false);
				}
				musicAdapter.notifyDataSetChanged();
				;
			}
		});

		// YD add 20150810 for fileEdit
		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// 设备变化
						curStorage = matchFragmentIndex(checkedId);
						setMusicAdapter();

					}
				});

		((RadioButton) radioGroup.getChildAt(curStorage - 1)).setChecked(true);

	}

	private void setMusicAdapter() {

		// 当前设备为音响，请求音响音乐文件。
		if (STORAGE_YINXIANG == curStorage && remoteMusics.length() < 10) {
			mHandle.sendEmptyMessage(REQUEST_AUDIOEQUIPMENT_MUSIC);
			return;
		}
		String tempStr = (STORAGE_YINXIANG == curStorage) ? remoteMusics : null;
		musicAdapter = new YinXiangMusicAdapter(YinXiangMusicViewActivity.this,
				mHandle, keyStr, tempStr);
		musicListView.setAdapter(musicAdapter);
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

		// 显示操作等待进度提示
		if (null != mFileEdit && !editType.equals(MusicUtils.EDIT_COPYTO_YINXIANG)) {
			String msg = editType.equals(MusicUtils.EDIT_REQUEST_MUSICS) ? "音响文件获取中": "操作正在进行,请稍后";
			mFileEdit.showProgressDialog(msg);
		}

		try {
			
			String tmpHttpAddress = musicPath;
			// 获取IP和外部存储路径
			String ipAddress = NetworkUtils.getLocalHostIp();
			if (STORAGE_MOBILE == curStorage) {
				tmpHttpAddress = getHttpAddressOfFile(ipAddress, musicPath);
			}
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
			mFileEdit.closeProgressDialog();
		}
	}

	
	/**
	 * 获取文件远程访问地址
	 * @param ipAddress 本地ip地址
	 * @param filePath  文件路径
	 * @return 远程访问地址
	 */
	private String getHttpAddressOfFile(String ipAddress, String filePath) {

		String mHttpAddress = "";

		String httpAddress = "http://" + ipAddress + ":"+ HTTPDService.HTTP_PORT;

		String newMusicPath = null;
		if (filePath.startsWith(HTTPDService.defaultHttpServerPath)) {
			newMusicPath = convertFileUrlToHttpURL(filePath.replace(
					HTTPDService.defaultHttpServerPath, ""));

		} else {
			for (String otherHttpServerPath : HTTPDService.otherHttpServerPaths) {
				if (filePath.startsWith(otherHttpServerPath)) {
					newMusicPath = convertFileUrlToHttpURL(filePath.replace(
							otherHttpServerPath, ""));
				}
			}
		}

		mHttpAddress = httpAddress + newMusicPath;
		// 判断URL是否符合规范，如果不符合规范，就1重命名文件
		try {
			URI.create(mHttpAddress);

		} catch (Exception e) {
			try {

				/**
				 * 创建新的文件
				 */
//				if (!filePath.equals("")) {
//					File illegalFile = new File(filePath);
//					String fullpath = illegalFile.getAbsolutePath();
//					String filename = illegalFile.getName();
//					String filepath = fullpath.replace(File.separator+ filename, "");
//					String[] tokens = StringUtils.delimitedListToStringArray(	filename, ".");
//					String filenameSuffix = tokens[tokens.length - 1];
//					String newFile = filepath + File.separator	+ StringUtils.getRandomString(15) + "."+ filenameSuffix;
//					Runtime.getRuntime().exec("mv " + fullpath + " " + newFile);
//					mHttpAddress = httpAddress + newFile;
//
//				}
			} catch (Exception e1) {
				e.printStackTrace();
				Toast.makeText(this, "对不起，音乐文件获取有误，不能正常操作！", Toast.LENGTH_SHORT)
						.show();
			}
		}
		return mHttpAddress;

	}

	private Handler mHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			String musicPath;
			// 显示文件编辑对话框
			switch (msg.what) {

			case REQUEST_AUDIOEQUIPMENT_MUSIC:

				if (STORAGE_YINXIANG == curStorage) {
					// 发送播放地址
					sendFileEditMsg2YinXiang("",MusicUtils.EDIT_REQUEST_MUSICS, "");
					mMusicEditServer.communicationWithServer(mHandle,
							MusicUtils.ACTION_SOCKET_COMMUNICATION,
							MusicUtils.EDIT_REQUEST_MUSICS);

				}
				break;

			case SHOW_ACTION_RESULT:

				if (STORAGE_YINXIANG == curStorage) {
					// 机顶盒返回信息
					String result = (String) msg.getData().get("result");
					String action = (String) msg.getData().get("action");

					if (result.contains("success")) {						
						//远程重命名成功，更新数据
						if(	action.equals(MusicUtils.EDIT_RENAME) && StringUtils.hasLength(saveNewName) ){
							String oldName=mEditMusic.getTitle();
							String path=mEditMusic.getPath();
							String fileUrl=mEditMusic.getFileUrl();
							path=path.replace(oldName, saveNewName);
							fileUrl=fileUrl.replace(oldName, saveNewName);
							mEditMusic.setTitle(saveNewName);
							mEditMusic.setPath(path);
							mEditMusic.setFileUrl(fileUrl);
							saveNewName=null;
						}
						musicAdapter.changeAdapterData(action, mEditMusic);
						musicAdapter.notifyDataSetChanged();	
						remoteMusics="";
						result = "远程操作成功";
					} else if (result.contains("failed")) {
						result = "远程操作失败";
					} else if (result.contains("文件拷贝成功")) {
						// 拷贝成功，更新媒体库记录
						String fileUrl=mEditMusic.getFileUrl();
						mFileEdit.upDateMediaStoreFile(fileUrl);
					}

					Toast.makeText(YinXiangMusicViewActivity.this, result,
							Toast.LENGTH_SHORT).show();

					// 如文件下载提示框显示状态，关闭文件下载提示框
					// 关闭进度条
					if (null != mFileEdit) {
						mFileEdit.closeProgressDialog();
					}
					
					

				}
				break;
			case SHOW_AUDIOEQUIPMENT_MUSICLIST:

				if (STORAGE_YINXIANG == curStorage) {
					remoteMusics = (String) msg.obj;
					musicAdapter = new YinXiangMusicAdapter(	YinXiangMusicViewActivity.this, mHandle, keyStr,	remoteMusics);
					musicListView.setAdapter(musicAdapter);
					// musicAdapter.createAdapterData(musicList);
					// musicAdapter.notifyDataSetChanged();
				}
				// 关闭进度条
				if (null != mFileEdit) {
					mFileEdit.closeProgressDialog();
				}
				break;
			// 显示文件编辑对话框
			case FILE_EDIT_DIALOG:
				// 1、获取当前焦点音乐文件信息
				mEditMusic = (YinXiangMusic) msg.obj;
				// 2、显示文件编辑对话框
				if (mFileEdit != null) {
					mFileEdit.showEditDialog(mEditMusic, curStorage);
				}
				break;
			// 文件拷贝：手机----->音响
			case FILE_EDIT_COPY:

				if (STORAGE_MOBILE == curStorage) {
					musicPath = mEditMusic.getPath();
					sendFileEditMsg2YinXiang(musicPath,	MusicUtils.EDIT_COPYTO_YINXIANG, "");
				} else if (STORAGE_YINXIANG == curStorage) {

					// 获取远程文件访问定位符，实现音响到手机文件COPY
					musicPath = mEditMusic.getFileUrl();
					mMusicEditServer.communicationWithServer(mHandle,MusicUtils.ACTION_HTTP_DOWNLOAD, musicPath);
					mFileEdit.showProgressDialog("fileEdit:yinxiang");
				}

				break;
			// 设置音响端音乐闹铃-并同步拷贝文件到音响
			case FILE_EDIT_CLOCK:
				musicPath = mEditMusic.getPath();
				sendFileEditMsg2YinXiang(musicPath, MusicUtils.EDIT_CLOCK,"");
				break;

			// 实现远程音响文件重命名。
			case FILE_EDIT_RENAME:

				musicPath = mEditMusic.getPath();
				String newName = (String) msg.getData().get("newName");
				String newFilePath= (String) msg.getData().get("newFilePath");
				saveNewName=null;
				if (STORAGE_YINXIANG == curStorage) {				
					saveNewName=newName;
					sendFileEditMsg2YinXiang(musicPath, MusicUtils.EDIT_RENAME,newName);
					mMusicEditServer.communicationWithServer(mHandle,
							MusicUtils.ACTION_SOCKET_COMMUNICATION,
							MusicUtils.EDIT_RENAME);

				} else if (STORAGE_MOBILE == curStorage) {

					String result = "重命名失败";
					if (StringUtils.hasLength(newName)) {
						mEditMusic.setTitle(newName);
						mEditMusic.setPath(newFilePath);
						musicAdapter.changeAdapterData(MusicUtils.EDIT_RENAME,	mEditMusic);
						musicAdapter.notifyDataSetChanged();
						result = "重命名成功";
					}
					// 提示操作结果
					Toast.makeText(YinXiangMusicViewActivity.this, result,Toast.LENGTH_SHORT).show();
				}
				break;

			// 实现远程音响文件删除。
			case FILE_EDIT_REMOVE:
				String removeFile = (String) msg.obj;

				if (STORAGE_YINXIANG == curStorage) {
					musicPath = mEditMusic.getPath();
					sendFileEditMsg2YinXiang(musicPath, MusicUtils.EDIT_REMOVE,"");
					mMusicEditServer.communicationWithServer(mHandle,
							MusicUtils.ACTION_SOCKET_COMMUNICATION,
							MusicUtils.EDIT_REMOVE);

				} else if (STORAGE_MOBILE == curStorage) {
					String result = "文件删除失败";
					if (StringUtils.hasLength(removeFile)) {
						musicAdapter.changeAdapterData(MusicUtils.EDIT_REMOVE,	mEditMusic);
						musicAdapter.notifyDataSetChanged();
						result = "文件删除成功";
					}
					// 提示操作结果
					Toast.makeText(YinXiangMusicViewActivity.this, result,Toast.LENGTH_SHORT).show();
				}
				break;
			case COMMUNICATION_ERROR:
				// 关闭进度条
				if (null != mFileEdit) {
					mFileEdit.closeProgressDialog();
				}
				Toast.makeText(YinXiangMusicViewActivity.this, "通讯失败",Toast.LENGTH_SHORT).show();
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
		setMusicAdapter();
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
	protected void onPause() {
		mMusicEditServer.close();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	
	private class NewNameConfig{
		 String  newName;
		 String  newFilePath;
		 String  newFileURL;
		 
		 public void init(){
			 newName="";
			 newFilePath="";
		     newFileURL="";
		 }
		 
		 public boolean isValid(){
			 if(StringUtils.hasLength(newName) 
					 &&StringUtils.hasLength(newName) 
					 &&StringUtils.hasLength(newName) )
				 return true;
			 return false;
		 }
	}

}
