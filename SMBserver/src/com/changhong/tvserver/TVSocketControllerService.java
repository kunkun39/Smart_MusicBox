package com.changhong.tvserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.LcdManager;
import android.os.Message;
import android.os.SystemClock;
import android.util.JsonReader;
import android.util.Log;

import com.changhong.tvserver.alarm.ClockCommonData;
import com.changhong.tvserver.autoctrl.ClientOnLineMonitorService;
import com.changhong.tvserver.fedit.FileDowLoadTask;
import com.changhong.tvserver.fedit.MusicEdit;
import com.changhong.tvserver.search.Commonmethod;
import com.changhong.tvserver.search.MallListActivity;
import com.changhong.tvserver.search.SearchActivity;
import com.changhong.tvserver.touying.image.ImageShowPlayingActivity;
import com.changhong.tvserver.touying.music.MusicViewPlayingActivity;
import com.changhong.tvserver.touying.video.VideoViewPlayingActivity;
import com.changhong.tvserver.utils.NetworkUtils;
import com.changhong.tvserver.utils.StringUtils;
import com.chome.virtualkey.virtualkey;

public class TVSocketControllerService extends Service implements ServerSocketInterface{
	private static final String TAG = "TVSocketControlService";

	virtualkey t = new virtualkey();

	/**
	 * heart internal time which stand for server send info to clients for this
	 * value
	 */
	private static final int TIME = 1000;

	/**
	 * server ip
	 */
	private String ip = null;

	/**
	 * heart
	 */
	private static final int heartPort = 9004;
	/**
	 * handle for this service
	 */
	private Handler handler = null;

	// private String DeviceModel = null;
	private String serverInfo = null;

	/**
	 * message for client send
	 */
	private String msg1 = null;

	/**
	 * the tag which stand for music or video stop play 1 - for video 2 - for
	 * music
	 */
	public static int STOP_PLAY_TAG = 0;

	// 音乐文件编辑
	MusicEdit mMusicEdit = null;

	/**
	 * 推送的视频列表
	 */
	public static List<String> vedios = new ArrayList<String>();

	//前面板液晶显示支持类，屏蔽该类，可跨平台使用。
	LcdManager mLcdManager = null;

	public static String CH_BOX_NAME = "影  院";

	// YD add 20150726 接收ClientOnLineMonitorService发送过来的自动控制命令广播。
	private AutoCtrlCommandReceiver autoCtrlReceiver = null;
	private UpdateInforReceiver updateInforReceiver = null;
	private static String ACTION_UPDATE_FMINFOR = "com.changhong.FmStatus";

	/*
	 * TCP server
	 */
	private ServerSocket mServerSocket;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences preferences = this.getSharedPreferences("changhong_box_name", Context.MODE_PRIVATE);
		CH_BOX_NAME = preferences.getString("CH_BOX_NAME", "音    箱");
		if (mLcdManager == null) {
			mLcdManager = (LcdManager) getSystemService(Context.LCDDISPLAY_SERVICE);
		}
		initFM();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					switch (msg.what) {
					case 1:
						if (msg1.equals("key:up")) {
							Log.e(TAG, "key:up");
							t.vkey_input(103, 1);
						} else if (msg1.equals("key:down")) {
							Log.e(TAG, "key:down");
							t.vkey_input(108, 1);
						} else if (msg1.equals("key:left")) {
							Log.e(TAG, "key:left");
							t.vkey_input(105, 1);
						} else if (msg1.equals("key:right")) {
							Log.e(TAG, "key:right");
							t.vkey_input(106, 1);
						} else if (msg1.equals("key:ok")) {
							Log.e(TAG, "key:ok");
							t.vkey_input(28, 1);
						} else if (msg1.equals("key:back")) {
							Log.e(TAG, "key:back");
							t.vkey_input(1, 1);
						} else if (msg1.equals("key:menu")) {
							Log.e(TAG, "key:menu");
							t.vkey_input(125, 1);
						} else if (msg1.equals("key:home")) {
							Log.e(TAG, "key:home");
							t.vkey_input(102, 1);
						} else if (msg1.equals("key:volumeup")) {
							Log.e(TAG, "key:volumeup");
							t.vkey_input(104, 1);
						} else if (msg1.equals("key:volumedown")) {
							Log.e(TAG, "key:volumedown");
							t.vkey_input(109, 1);
						} else if (msg1.equals("key:power")) {
							Log.e(TAG, "key:power");
							// t.vkey_input(0x7f01, 1);
							t.vkey_input(116, 1);
						} else if (msg1.equals("key:mute")) {
							Log.e(TAG, "key:mute");
							t.vkey_input(113, 1);
						} else if (msg1.equals("key:0")) {
							Log.e(TAG, "key:0");
							t.vkey_input(11, 1);
						} else if (msg1.equals("key:1")) {
							Log.e(TAG, "key:1");
							t.vkey_input(2, 1);
						} else if (msg1.equals("key:2")) {
							Log.e(TAG, "key:2");
							t.vkey_input(3, 1);
						} else if (msg1.equals("key:3")) {
							Log.e(TAG, "key:3");
							t.vkey_input(4, 1);
						} else if (msg1.equals("key:4")) {
							Log.e(TAG, "key:4");
							t.vkey_input(5, 1);
						} else if (msg1.equals("key:5")) {
							Log.e(TAG, "key:5");
							t.vkey_input(6, 1);
						} else if (msg1.equals("key:6")) {
							Log.e(TAG, "key:6");
							t.vkey_input(7, 1);
						} else if (msg1.equals("key:7")) {
							Log.e(TAG, "key:7");
							t.vkey_input(8, 1);
						} else if (msg1.equals("key:8")) {
							Log.e(TAG, "key:8");
							t.vkey_input(9, 1);
						} else if (msg1.equals("key:9")) {
							Log.e(TAG, "key:9");
							t.vkey_input(10, 1);
						} else if (msg1.equals("key:yxmovie")) {
							Log.e(TAG, "key:yxmovie");
							t.vkey_input(59, 1);
						} else if (msg1.equals("key:yxtv")) {
							Log.e(TAG, "key:yxtv");
							t.vkey_input(60, 1);
						} else if (msg1.equals("key:yxmusic")) {
							Log.e(TAG, "key:yxmusic");
							t.vkey_input(61, 1);
						} else if (msg1.equals("key:yxgame")) {
							Log.e(TAG, "key:yxgame");
							t.vkey_input(62, 1);
						} else if (msg1.equals("key:yxyd")) {
							Log.e(TAG, "key:yxyd");
							t.vkey_input(63, 1);
						} else if (msg1.equals("key:yxxt")) {
							Log.e(TAG, "key:yxxt");
							t.vkey_input(64, 1);
						} else if (msg1.equals("key:lightsup")) {
							Log.e(TAG, "key:lightsup");
							t.vkey_input(67, 1);
						} else if (msg1.equals("key:lightsdown")) {
							Log.e(TAG, "key:lightsdown");
							t.vkey_input(65, 1);
						} else if (msg1.equals("key:lightsmoon")) {
							Log.e(TAG, "key:lightsmoon");
							t.vkey_input(88, 1);
						} else if (msg1.equals("key:lightssun")) {
							Log.e(TAG, "key:lightssun");
							t.vkey_input(68, 1);
						} else if (msg1.equals("key:lightscontrol")) {
							Log.e(TAG, "key:lightscontrol");
							t.vkey_input(66, 1);
						} else if (msg1.equals("key:dyup")) {
							Log.e(TAG, "key:dyup");
							t.vkey_input(0x18f, 1);
						} else if (msg1.equals("key:dydown")) {
							Log.e(TAG, "key:dydown");
							t.vkey_input(0x191, 1);
						} else if (msg1.equals("key:music")) {
							Log.e(TAG, "key:music");
							t.vkey_input(0x190, 1);
						} else if (msg1.equals("key:xiami")) {
							// 启动虾米音乐
							Log.e(TAG, "key:xiami");
							// t.vkey_input(0x190, 1);
							startXiaMiMusic();

						} else if (msg1.equals("key:autoctrl_on")) {
							t.vkey_input(116, 1);
							t.vkey_input(0x190, 1);

						} else if (msg1.equals("key:autoctrl_off")) {
							t.vkey_input(116, 1);
						}
						// 选择输入源部�?
						else if (msg1.equals("source:av1")) {
							mLcdManager.lcdDsaCmdSend((byte) 0x60);
							Intent intent = new Intent();
							intent.setAction("com.changhong.action.InputSource");
							intent.putExtra("source_id", 1001);
							sendBroadcast(intent);
						} else if (msg1.equals("source:bt")) {
							mLcdManager.lcdDsaCmdSend((byte) 0x61);
							Intent intent = new Intent();
							intent.setAction("com.changhong.action.InputSource");
							intent.putExtra("source_id", 1004);
							sendBroadcast(intent);
						} else if (msg1.equals("source:hdmi")) {
							mLcdManager.lcdDsaCmdSend((byte) 0x62);
							Intent intent = new Intent();
							intent.setAction("com.changhong.action.InputSource");
							intent.putExtra("source_id", 1002);
							sendBroadcast(intent);
						} else if (msg1.equals("source:ott")) {
							mLcdManager.lcdDsaCmdSend((byte) 0x63);
							Intent intent = new Intent();
							intent.setAction("com.changhong.action.InputSource");
							intent.putExtra("source_id", 1003);
							sendBroadcast(intent);
						} else if (msg1.equals("source:av2")) {
							mLcdManager.lcdDsaCmdSend((byte) 0x64);
							Intent intent = new Intent();
							intent.setAction("com.changhong.action.InputSource");
							intent.putExtra("source_id", 1005);
							sendBroadcast(intent);
						}
						// 投影歌曲部分
						else if (msg1.startsWith("GetMusicList:")) {
							handleMusicMsgs(msg1);
						}
						// else if (msg1.contains("music_play")) {
						// Log.e(TAG, msg1);
						// Intent intent = new
						// Intent(TVSocketControllerService.this,
						// MusicViewPlayingActivity.class);
						// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						// intent.setData(Uri.parse(msg1));
						// startActivity(intent);
						// } else if (msg1.equals("music:start")) {
						// if (MusicViewPlayingActivity.mEventHandler != null) {
						// Log.e(TAG, msg1);
						// MusicViewPlayingActivity.mEventHandler.sendEmptyMessage(1);
						// }
						// } else if (msg1.equals("music:stop")) {
						// if (MusicViewPlayingActivity.mEventHandler != null) {
						// Log.e(TAG, msg1);
						// MusicViewPlayingActivity.mEventHandler.sendEmptyMessage(2);
						// }
						// } else if (msg1.startsWith("music:seekto:")) {
						// if (MusicViewPlayingActivity.mEventHandler != null) {
						// Log.e(TAG, msg1);
						// Message message = new Message();
						// message.what = 3;
						// message.obj = msg1;
						// MusicViewPlayingActivity.mEventHandler.sendMessage(message);
						// }
						// //投影视屏部分
						// }
						else if (msg1.startsWith("GetVideoList:")) {
							handleVedioMsgs(msg1);
						}
						// else if (msg1.substring(0, 4).equals("http")) {
						// Log.e(TAG, msg1);
						// Intent intent = new
						// Intent(TVSocketControllerService.this,
						// TCMediaPlayer.class);
						// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						// intent.setData(Uri.parse(msg1));
						// startActivity(intent);
						// } else if (msg1.equals("vedio:start")) {
						// if (TCMediaPlayer.mEventHandler != null) {
						// Log.e(TAG, msg1);
						// TCMediaPlayer.mEventHandler.sendEmptyMessage(1);
						// }
						// } else if (msg1.equals("vedio:stop")) {
						// if (TCMediaPlayer.mEventHandler != null) {
						// Log.e(TAG, msg1);
						// TCMediaPlayer.mEventHandler.sendEmptyMessage(2);
						// }
						// } else if (msg1.startsWith("vedio:seekto:")) {
						// if (TCMediaPlayer.mEventHandler != null) {
						// Log.e(TAG, msg1);
						// Message message = new Message();
						// message.what = 3;
						// message.obj = msg1;
						// TCMediaPlayer.mEventHandler.sendMessage(message);
						// }
						//
						// }
						// 投影图片部分
						else if (msg1.contains("urls")) {
							Log.e(TAG, msg1);
							handleTouYingPicMsg(msg1);
						} else if (msg1.equals("rotation:left")) {
							if (ImageShowPlayingActivity.handler != null) {
								Log.e(TAG, msg1);
								ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
								ActivityManager.RunningTaskInfo info = manager
										.getRunningTasks(1).get(0);
								String shortClassName = info.topActivity
										.getClassName(); // 类名
								if ("com.changhong.tvserver.touying.image.ImageShowPlayingActivity"
										.equals(shortClassName)) {
									ImageShowPlayingActivity.handler
											.sendEmptyMessage(2);
								}
							}
						} else if (msg1.equals("rotation:right")) {
							if (ImageShowPlayingActivity.handler != null) {
								Log.e(TAG, msg1);
								ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
								ActivityManager.RunningTaskInfo info = manager
										.getRunningTasks(1).get(0);
								String shortClassName = info.topActivity
										.getClassName(); // 类名
								Log.e(TAG, "rotation:" + shortClassName);
								if ("com.changhong.tvserver.touying.image.ImageShowPlayingActivity"
										.equals(shortClassName)) {
									ImageShowPlayingActivity.handler
											.sendEmptyMessage(3);
								}
							}
						} else if (msg1.startsWith("room_pointer_down:")) {
							if (ImageShowPlayingActivity.handler != null) {
								Log.e(TAG, "Location:" + msg1);
								ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
								ActivityManager.RunningTaskInfo info = manager
										.getRunningTasks(1).get(0);
								String shortClassName = info.topActivity
										.getClassName(); // 类名
								if ("com.changhong.tvserver.touying.image.ImageShowPlayingActivity"
										.equals(shortClassName)) {
									Message message = new Message();
									message.what = 4;
									message.obj = msg1;
									ImageShowPlayingActivity.handler
											.sendMessage(message);
								}
							}
						} else if (msg1.startsWith("room_action_move:")) {
							if (ImageShowPlayingActivity.handler != null) {
								Log.e(TAG, "Location:" + msg1);
								ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
								ActivityManager.RunningTaskInfo info = manager
										.getRunningTasks(1).get(0);
								String shortClassName = info.topActivity
										.getClassName(); // 类名
								if ("com.changhong.tvserver.touying.image.ImageShowPlayingActivity"
										.equals(shortClassName)) {
									Message message = new Message();
									message.what = 5;
									message.obj = msg1;
									ImageShowPlayingActivity.handler
											.sendMessage(message);
								}
							}
						} else if (msg1.startsWith("room_action_up:")) {
							if (ImageShowPlayingActivity.handler != null) {
								Log.e(TAG, "Location:" + msg1);
								ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
								ActivityManager.RunningTaskInfo info = manager
										.getRunningTasks(1).get(0);
								String shortClassName = info.topActivity
										.getClassName(); // 类名
								if ("com.changhong.tvserver.touying.image.ImageShowPlayingActivity"
										.equals(shortClassName)) {
									ImageShowPlayingActivity.handler
											.sendEmptyMessage(6);
								}
							}
						} else if (msg1.startsWith("room_pointer_up:")) {
							if (ImageShowPlayingActivity.handler != null) {
								Log.e(TAG, "Location:" + msg1);
								ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
								ActivityManager.RunningTaskInfo info = manager
										.getRunningTasks(1).get(0);
								String shortClassName = info.topActivity
										.getClassName(); // 类名
								if ("com.changhong.tvserver.touying.image.ImageShowPlayingActivity"
										.equals(shortClassName)) {
									ImageShowPlayingActivity.handler
											.sendEmptyMessage(7);
								}
							}
						} else if (msg1.equals("key:dtv")) {
							Intent mIntent = getPackageManager()
									.getLaunchIntentForPackage(
											"Com.smarttv_doggle_newui");
							mIntent.putExtra("forceresume", true);
							try {
								startActivity(mIntent);
							} catch (Exception e) {
								Log.i(TAG,
										"startActivity Com.smarttv_doggle_newui  err ! ");
							}
						} else if (msg1.startsWith("app_open:")) {
							Log.e(TAG, "Location:" + msg1);
							openYuYingApplication(msg1);
						} else if (msg1.equals("finish")) {
							Intent intent = new Intent("FinishActivity");
							sendBroadcast(intent);
						} else if (msg1.startsWith("fm:")) {

							Log.e("YDINFOR::",
									"++++++++++++++++++++++++++++++++start to FM++++++++++++++++++++++++++++++");
							Intent intent = new Intent("com.changhong.fmname");
							intent.putExtra("fmname", msg1.substring(3));
							sendBroadcast(intent);

							// 判断当前是否主页,不是，启动主页
							if (!Commonmethod.isActivityForeground(
									TVSocketControllerService.this,
									"com.changhong.doplauncher.Launcher")) {
								t.vkey_input(102, 1);
							}
							Log.e("YDINFOR::",
									"++++++++++++++++++++++++++++++++sendBroadcast(FM) end++++++++++++++++++++++++++++++");
						}

						// 搜索音乐视频部分
						else if (msg1.startsWith("search:")) {
							handleSearchMsg(msg1);
						}
						// 增加文件编辑
						else if (msg1.contains("fileEdit")) {

							handleFileEditMsg(msg1);

							// 获取闹铃设置信息
						} else if (msg1.startsWith("getAlarmMsg:")) {
//							Log.i("mmmm", TAG + ":" + msg1);
							handleAlarm(msg1);
						} else if (msg1.contains("autoctrl")) {
							Log.i("mmmm", TAG + ":" + msg1);
							handleAutoCtrl(msg1);
						}
						break;

					default:

						break;
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
				super.handleMessage(msg);
			}
		};

		new send_heart_thread().start();
		new get_command().start();
		new TCPReceive().start();

		// YD add 20150726 注册自动控制监控发送的广播
		autoCtrlReceiver = new AutoCtrlCommandReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ClientOnLineMonitorService.ACTION_AUTOCTRL_COMMAND);
		registerReceiver(autoCtrlReceiver, filter);

		updateInforReceiver = new UpdateInforReceiver();
		IntentFilter fmFilter = new IntentFilter();
		fmFilter.addAction(ACTION_UPDATE_FMINFOR);
		registerReceiver(updateInforReceiver, fmFilter);

	}

	/************************************************* send heart part **************************************************/

	/**
	 * 服务端发送客户端心跳
	 * <p>
	 * DatagramSocket:一开始就创建�? DatagramPacket:接收一个创建一�? 这样免得发生阻塞
	 */
	private class send_heart_thread extends Thread {

		public void run() {
			DatagramSocket dgSocket = null;
			try {
				dgSocket = new DatagramSocket();
				DatagramPacket dgPacket = null;

				while (true) {
					try {
						ip = NetworkUtils.getLocalIpAddress();

						if (StringUtils.hasLength(ip) && !ip.equals("0.0.0.0")) {
							serverInfo = CH_BOX_NAME;
							/**
							 * 添加服务端网络信息到心跳
							 */
							serverInfo = serverInfo + "|"
									+ MyApplication.networkStatus.name();

							if (STOP_PLAY_TAG > 0) {
								/**
								 * video or music stop play tag
								 */
								serverInfo = serverInfo + "|play_stop|"
										+ STOP_PLAY_TAG;
								STOP_PLAY_TAG = 0;

							} else {
								/**
								 * 发送当前视频播放的操作信息, 错误之后并不影响心跳机制
								 */
								try {
									ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
									ActivityManager.RunningTaskInfo info = manager
											.getRunningTasks(1).get(0);
									String shortClassName = info.topActivity
											.getClassName(); // 类名
									if ("com.changhong.tvserver.touying.video.VideoViewPlayingActivity"
											.equals(shortClassName)) {
										serverInfo = serverInfo
												+ "|vedio_play|"
												+ VideoViewPlayingActivity.playVeidoKey
												+ "|"
												+ VideoViewPlayingActivity.mVV
														.getCurrentPosition()
												+ "|"
												+ VideoViewPlayingActivity.mVV
														.isPlaying();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

								/**
								 * 发送当前音乐播放的操作信息, 错误之后并不影响心跳机制
								 */
								try {
									ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
									ActivityManager.RunningTaskInfo info = manager
											.getRunningTasks(1).get(0);
									String shortClassName = info.topActivity
											.getClassName(); // 类名
									if ("com.changhong.tvserver.touying.music.MusicViewPlayingActivity"
											.equals(shortClassName)) {
										serverInfo = serverInfo
												+ "|music_play|"
												+ MusicViewPlayingActivity.playVeidoKey
												+ "|"
												+ MusicViewPlayingActivity.mVV
														.getCurrentPosition()
												+ "|"
												+ MusicViewPlayingActivity.mVV
														.isPlaying();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

							/**
							 * 发送心�?
							 */
							Log.i(TAG, ">>>" + serverInfo);
							byte[] b = serverInfo.getBytes();
							dgPacket = new DatagramPacket(b, b.length,
									InetAddress.getByName("255.255.255.255"),
									heartPort);
							dgSocket.send(dgPacket);
						} else {
							Log.e(TAG, "ip>>>not get the ip");
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						dgPacket = null;
					}

					/**
                     *
                     */
					SystemClock.sleep(TIME);
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} finally {
				try {
					if (dgSocket != null) {
						dgSocket.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/************************************************* get command part *************************************************/

	/**
	 * 服务端接收客户端发来的socket
	 * <p>
	 * DatagramSocket:一开始就创建�? DatagramPacket:接收一个创建一�? 这样免得发生阻塞
	 */
	private class get_command extends Thread {
		public void run() {
			DatagramSocket dgSocket = null;

			try {
				dgSocket = new DatagramSocket(9002);
				DatagramPacket dgPacket = null;
				Log.i("mmmm", "TVSocketControllerService>get_command");
				while (true) {
					try {
						byte[] by = new byte[1024];
						dgPacket = new DatagramPacket(by, by.length);
						dgSocket.receive(dgPacket);

						String command = new String(by, 0, dgPacket.getLength());
						if (!command.equals("")) {
							Log.w(TAG, command);
							msg1 = command;
							handler.sendEmptyMessage(1);
						}

					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						dgPacket = null;
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();

			} finally {
				try {
					if (dgSocket != null) {
						dgSocket.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/******************************************** handle client message part *******************************************/

	private void handleTouYingPicMsg(String JsonMsg) {
		List<String> Pics = new ArrayList<String>();
		Pics.clear();
		String clientIP = "";

		if (JsonMsg != null && !JsonMsg.equals("")) {
			JsonReader reader = new JsonReader(new StringReader(JsonMsg));
			try {
				reader.beginObject();
				while (reader.hasNext()) {
					String name = reader.nextName();
					Log.e(TAG, "nextname:" + name);
					if (name.equals("urls")) {
						reader.beginArray();
						while (reader.hasNext()) {
							String picAddress = reader.nextString();
							Log.i(TAG, "nextaddress:" + picAddress);
							Pics.add(picAddress);
						}
						reader.endArray();
					} else if (name.equals("client_ip")) {
						clientIP = reader.nextString();
						Log.i(TAG, "clientaddress:" + clientIP);
					} else {
						reader.skipValue();
					}
				}
				reader.endObject();
				reader.close();
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "error load json message");
		}

		if (!Pics.isEmpty()) {
			String[] urls = new String[Pics.size()];
			for (int i = 0; i < Pics.size(); i++) {
				urls[i] = Pics.get(i);
			}

			try {
				ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
				ActivityManager.RunningTaskInfo info = manager.getRunningTasks(
						1).get(0);
				String shortClassName = info.topActivity.getClassName(); // 类名
				if ("com.changhong.tvserver.touying.image.ImageShowPlayingActivity"
						.equals(shortClassName)) {
					Message msg = new Message();
					msg.what = 100;
					msg.obj = urls;
					ImageShowPlayingActivity.handler.sendMessage(msg);
				} else {
					Intent intent = new Intent(TVSocketControllerService.this,
							ImageShowPlayingActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra(ImageShowPlayingActivity.EXTRA_IMAGE_URLS,
							urls);
					intent.putExtra(ImageShowPlayingActivity.CLIENT_IP,
							clientIP);
					intent.putExtra(ImageShowPlayingActivity.EXTRA_IMAGE_INDEX,
							0);
					startActivity(intent);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Log.e(TAG, "no picture url");
		}
	}

	/********************************************************* YD add 20150806 for fileEdit **********************************************************************************/
	private void handleFileEditMsg(String msg) {
		List<String> files = new ArrayList<String>();
		files.clear();
		String clientIP = "";

		if (msg == null || msg.equals(""))
			return;

		// 请求音响端音乐文件信息

		JsonReader reader = new JsonReader(new StringReader(msg));
		try {
			reader.beginObject();
			while (reader.hasNext()) {
				String name = reader.nextName();
				Log.e(TAG, "nextname:" + name);
				if (name.equals("fileEdit")) {
					reader.beginArray();
					while (reader.hasNext()) {
						String fileInfor = reader.nextString();
						Log.i(TAG, "nextaddress:" + fileInfor);
						files.add(fileInfor);
					}
					reader.endArray();
				} else if (name.equals("client_ip")) {
					clientIP = reader.nextString();
					Log.i(TAG, "clientaddress:" + clientIP);
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}

		if (!files.isEmpty()) {

			String editType = files.get(0);
			String parameter1 = files.get(1);
			String parameter2 = files.get(2);

			if (editType.equals("copyToYinXiang")
					|| editType.equals("clockRing")) {

				FileDowLoadTask.creatFileDownLoad(this).startDownLoad(editType,
						parameter1, parameter2);

			} else {

				if (null == mMusicEdit) {
					mMusicEdit = new MusicEdit();
				}
				mMusicEdit.doFileEdit(this, clientIP, editType, parameter1);
			}
		} else {
			Log.e(TAG, "no picture url");
		}
	}

	/**
	 * 
	 */
	private void handleAutoCtrl(String msg) {
		List<String> files = new ArrayList<String>();
		files.clear();
		String clientIP = "";
		if (msg == null || msg.equals(""))
			return;

		// 请求音响端音乐文件信息

		JsonReader reader = new JsonReader(new StringReader(msg));
		try {
			reader.beginObject();
			while (reader.hasNext()) {
				String name = reader.nextName();
				Log.e(TAG, "nextname:" + name);
				if (name.equals("autoctrl:")) {
					reader.beginArray();
					while (reader.hasNext()) {
						String fileInfor = reader.nextString();
						files.add(fileInfor);
					}
					reader.endArray();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			reader.close();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}

		if (!files.isEmpty()) {

			String command = files.get(0);
			String parameter = files.get(1);

			// 创建Intent对象
			Intent intent = new Intent();
			// 设置Intent的Action属性
			intent.setAction(ClientOnLineMonitorService.ACTION_UPDATE_AUTOCTRL);
			// 如果只传一个bundle的信息，可以不包bundle，直接放在intent里
			intent.putExtra("cmd", command);
			intent.putExtra("parameter", parameter);
			// 发送广播给ClientOnLineMonitorService
			sendBroadcast(intent);

		} else {
			Log.e(TAG, "no picture url");
		}
	}

	/*
	 * 
	 * 处理闹铃的操作请求
	 */
	private void handleAlarm(String str) {
		if (str == null || str.equals(""))
			return;
		String[] keys = StringUtils.delimitedListToStringArray(str, "|");
		ClockCommonData.getInstance().dealMsg(keys);
	}

	private void startXiaMiMusic() {

		// 发送广播停止当前服务.
		stopBackGroundServer("musicpure");

		// 启动虾米音乐
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.xiami.tv",
				"com.xiami.tv.activities.StartActivity"));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	private void stopBackGroundServer(String app) {
		// 发送广播停止当前服务.
		Intent packageIntent = new Intent("com.changhong.action.start_package");
		packageIntent.putExtra("extra", app);
		sendBroadcast(packageIntent);
	}

	/**
	 * 记录上次打开的应�?
	 */
	private String lastLunchApp = "";

	private void openYuYingApplication(String msg) {

		try {
			/**
			 * open app
			 */
			String[] tokens = StringUtils.delimitedListToStringArray(msg, ":");
			String packageName = tokens[1];
			if (StringUtils.hasLength(packageName)) {
				PackageManager packageManager = getPackageManager();
				Intent intent = new Intent();
				intent = packageManager.getLaunchIntentForPackage(packageName);
				if (intent != null) {
					startActivity(intent);
				}
			}
			/**
			 * close last open app, even last lunch app close by use, it's OK if
			 * (StringUtils.hasLength(lastLunchApp)) { ActivityManager
			 * activityManager =
			 * (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
			 * Method forceStopPackage =
			 * activityManager.getClass().getDeclaredMethod("forceStopPackage",
			 * String.class); forceStopPackage.setAccessible(true);
			 * forceStopPackage.invoke(activityManager, lastLunchApp); }
			 */
			lastLunchApp = packageName;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleVedioMsgs(final String vedioPath) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String msg = null;
				msg = vedioPath.substring(13);
				Log.e(TAG, "vedioPath>>>" + msg);

				if (msg == null) {
					return;
				}

				// get network json data
				String sss = null;
				URL urlAddress = null;
				try {
					urlAddress = new URL(msg);
					HttpURLConnection hurlconn = (HttpURLConnection) urlAddress
							.openConnection();
					hurlconn.setRequestMethod("GET");
					hurlconn.setConnectTimeout(2000);
					hurlconn.setRequestProperty("Charset", "UTF-8");
					hurlconn.setRequestProperty("Connection", "Close");
					if (hurlconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						hurlconn.connect();
						InputStream instream = hurlconn.getInputStream();
						InputStreamReader inreader = new InputStreamReader(
								instream, "UTF-8");
						StringBuffer stringappend = new StringBuffer();
						char[] b = new char[256];
						int length = -1;
						while ((length = inreader.read(b)) != -1) {
							stringappend.append(new String(b, 0, length));
						}
						sss = stringappend.toString();
						inreader.close();
						instream.close();
					} else {
						Log.e(TAG,
								">>>>>>>hurlconn.getResponseCode()!= HttpURLConnection.HTTP_OK 获取vediolist失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.e(TAG, ">>>>>>>>>>>>>>>>>>>>>>");
				}

				vedios.clear();
				try {
					if (StringUtils.hasLength(sss)) {
						JSONObject all = new JSONObject(sss);
						String arrayMsg = all.getString("vedios");
						JSONArray array = new JSONArray(arrayMsg);
						if (array != null) {
							for (int i = 0; i < array.length(); i++) {
								vedios.add(array.getString(i));
							}
						}
					} else {
						Log.e(TAG, "未获取到视频Json");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!vedios.isEmpty()) {

					stopBackGroundServer("TCMediaPlayer");
					Intent intent = new Intent(TVSocketControllerService.this,
							TCMediaPlayer.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					TVSocketControllerService.this.startActivity(intent);
				}
			}
		}).start();
	}

	private void handleMusicMsgs(String msg) {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.changhong.playlist",
				"com.changhong.playlist.Playlist"));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.putExtra("musicpath", msg);
		startActivity(intent);
	}

	// 处理搜索消息
	private void handleSearchMsg(String str) {
		String[] keys = StringUtils.delimitedListToStringArray(str, "|");
		if (keys.length != 2) {
			return;
		}
		// 文字搜索，去掉类型
		String[] tokens = StringUtils.delimitedListToStringArray(keys[1], ";");
		String searchContent = (2 == tokens.length) ? tokens[1] : tokens[0];

		if (keys[1].contains("music") || keys[1].contains("音乐")
				|| keys[1].contains("歌曲")) {
			String musickey = searchContent.replace("音乐", "");
			musickey = musickey.replace("歌曲", "");
			if (Commonmethod.isActivityForeground(this,
					"com.changhong.tvserver.search.SearchActivity")) {
				Message msg = new Message();
				msg.what = 1;
				msg.obj = musickey;
				SearchActivity.handler.sendMessage(msg);
			} else {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.setClass(this,
						com.changhong.tvserver.search.SearchActivity.class);
				intent.putExtra(SearchActivity.keyWordsName, musickey);
				startActivity(intent);
			}
		} else {

			if (Commonmethod.isActivityForeground(this,
					"com.changhong.tvserver.search.MallListActivity")) {
				Message msg = new Message();
				msg.what = 1;
				msg.obj = searchContent;
				MallListActivity.handler.sendMessage(msg);
			} else {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.setClass(this,
						com.changhong.tvserver.search.MallListActivity.class);
				intent.putExtra("command", "movie&tv:" + searchContent);
				startActivity(intent);
			}
		}
	}

	/*
	 * init FM 列表
	 */
	private void initFM() {
		try {
			JSONArray all = new JSONArray();
			ContentResolver contentResolver = MyApplication.getContext()
					.getContentResolver();
			Uri selectUri = Uri
					.parse("content://com.changhong.provider.fmprovider");
			Cursor cursor = contentResolver.query(selectUri, null, null, null,
					null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					String FMname = cursor.getString(cursor
							.getColumnIndex("name"));
					String state = cursor.getString(cursor
							.getColumnIndex("state"));
					JSONObject single = new JSONObject();
					single.put("FMname", FMname);
					single.put("state", state);
					all.put(single);
				}
				cursor.close();
			}

			// //增加控制状态到文件中。
			// JSONObject autoCtrlState = new JSONObject();
			// String
			// autoCtrl=ClientOnLineMonitorService.isAutoControl()?"on":"off";
			// autoCtrlState.put("FMname", "autoCtrl");
			// autoCtrlState.put("state", autoCtrl);
			// all.put(autoCtrlState);

			/**
			 * 输入FM列表到文�?
			 */
			File FMIndexJson = new File(MyApplication.appInfoPath,
					"OttFMInfoJson.json");
			if (FMIndexJson.exists()) {
				FMIndexJson.delete();
			}
			FileWriter fw = new FileWriter(FMIndexJson);
			fw.write(all.toString(), 0, all.toString().length());
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		if (intent != null) {
			int i = intent.getIntExtra("message", 0);
			if (1 == i) {
				initFM();
			}
		}
		return super.onStartCommand(intent, Service.START_REDELIVER_INTENT,
				startId);
	}

	// ---------系统方法

	/***************************************************** YD add 20150726 broadcast 接收clientmonitor 发送过来自动控制命令 ********************************************************/

	private class AutoCtrlCommandReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action
					.equals(ClientOnLineMonitorService.ACTION_AUTOCTRL_COMMAND)) {
				msg1 = intent.getStringExtra("cmd");
				Log.i(TAG, "autoCtrlCommand is " + msg1);
				handler.sendEmptyMessage(1);
			}
		}

	}

	private class UpdateInforReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_UPDATE_FMINFOR)) {
				initFM();
			}
		}
	}

	/*
	 * TCP receiver
	 */

	private class TCPReceive extends Thread {
		BufferedReader in = null;
		String content = "";
		Socket socketclient = null;
		String line = "";
		InputStream is = null;
		byte[] buffer = new byte[1024];
		char[] buf=new char[1024];
		int length = 0;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {

				if (null == mServerSocket) {
					mServerSocket = new ServerSocket(TCP_ALARM_SERVER_PORT, 5);
				}
				mServerSocket.setSoTimeout(0);
				Log.i("mmmm", "tcp start");
				while (true) {
					// 获取音响端发送的socket的对象
					socketclient = null;
					socketclient = mServerSocket.accept();
					Log.i("mmmm", "socketclient_accept" + socketclient);
					  is = socketclient.getInputStream();

					in = new BufferedReader(new InputStreamReader(
							socketclient.getInputStream()));
					content="";				
					while ((line = in.readLine()) != null) {
						content += line;
						if(content.contains(TCP_END)){
							Log.i("mmmm", "content"+content);
							msg1 = content;
							if (content != null) {
								handler.sendEmptyMessage(1);
							}
							content=""; 
						}
					}
					
					// while (true) {
					// if (is.available() < 0) {
					// continue;
					// }
					// length = is.read(buffer);
					// content = new String(buffer, 0, length);
					// // 发送信息给主线程，返回响应结果
					// msg1 = content;
					// handler.sendEmptyMessage(1);
					// Log.i("mm", "--recTCPData--------" + msg1);
					// }
				}

			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();

			} finally {

				try {
					if (null != in) {
						in.close();
						in = null;
					}
					if (null != socketclient) {
						socketclient.close();
						socketclient = null;
					}
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		}
	}

	/***************************************************** YD add 20150726 end ********************************************************/

	@Override
	public void onDestroy() {

		// 取消自动控制广播
		if (null != autoCtrlReceiver) {
			unregisterReceiver(autoCtrlReceiver);
			autoCtrlReceiver = null;
		}
		super.onDestroy();

	}

}
