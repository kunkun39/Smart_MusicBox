package com.changhong.yinxiang.activity;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Date;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.JsonReader;
import android.view.MotionEvent;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.fragment.YinXiangCategoryFragment;
import com.changhong.yinxiang.fragment.YinXiangFMFragment;
import com.changhong.yinxiang.fragment.YinXiangRemoteControlFragment;
import com.changhong.yinxiang.fragment.YinXiangSettingFragment;
import com.changhong.yinxiang.service.AppLogService;
import com.changhong.yinxiang.service.ClientGetCommandService;
import com.changhong.yinxiang.service.ClientLocalThreadRunningService;
import com.changhong.yinxiang.service.UpdateLogService;
import com.changhong.yinxiang.service.UserUpdateService;
import com.changhong.yinxiang.setting.AppHelpDialog;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public class YinXiangMainActivity extends FragmentActivity {

	private static final String TAG = "YinXiangMainFragmentActivity";

	/************************************************** IP连接部分 *******************************************************/

	public static TextView title = null;
	private BoxSelectAdapter adapter = null;
	private ListView clients = null;
	private Button list;
	/**
	 * message handler
	 */
	private Handler mhandler = null;

	/****************************** 定义fragment ***************************************/
	private FragmentManager fragmentManager;
	private RadioGroup radioGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_yinxiang_root);

		initService();

		initViewAndEvent();

		initUpdateThread();

	}

	private void initService() {
		/**
		 * 启动get command服务
		 */
		Intent service1 = new Intent(YinXiangMainActivity.this,
				ClientGetCommandService.class);
		startService(service1);

		/**
		 * 启动send command服务
		 */
		Intent service2 = new Intent(YinXiangMainActivity.this,
				ClientSendCommandService.class);
		startService(service2);

		/**
		 * 启动手机端本地线程
		 */
		Intent service3 = new Intent(YinXiangMainActivity.this,
				ClientLocalThreadRunningService.class);
		startService(service3);
	}

	private void initViewAndEvent() {
		/**
		 * init all views
		 */
		title = (TextView) findViewById(R.id.title);
		clients = (ListView) findViewById(R.id.clients);
		list = (Button) findViewById(R.id.btn_list);
		Button power = (Button) findViewById(R.id.power);

		/***************************** 初始化fragment ****************************************/
		fragmentManager = getFragmentManager();

		radioGroup = (RadioGroup) findViewById(R.id.yx_rgtab);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {

						FragmentTransaction transaction = fragmentManager.beginTransaction();
						Fragment mFragment = getInstanceByIndex(checkedId);

						// 增加fragment到backstack。
						transaction.replace(R.id.yx_tabcontent, mFragment);
						transaction.addToBackStack(null);
						// transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
						// 提交修改
						transaction.commit();
					}
				});

		// 设置默认进入遥控器控制界面
		((RadioButton) radioGroup.getChildAt(0)).setChecked(true);

		/**
		 * Ip部分
		 */
		adapter = new BoxSelectAdapter(YinXiangMainActivity.this,
				ClientSendCommandService.serverIpList);
		clients.setAdapter(adapter);
		clients.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// clients.setVisibility(View.GONE);
				return false;
			}
		});
		clients.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				adapter.notifyDataSetChanged();
				ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList
						.get(arg2);
				ClientSendCommandService.titletxt = ClientSendCommandService
						.getCurrentConnectBoxName();
				title.setText(ClientSendCommandService.titletxt);
				ClientSendCommandService.handler.sendEmptyMessage(2);
				clients.setVisibility(View.GONE);
			}
		});
		list.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				if (ClientSendCommandService.serverIpList.isEmpty()) {
					Toast.makeText(YinXiangMainActivity.this,
							"没有发现长虹智能机顶盒，请确认盒子和手机连在同一个路由器内", Toast.LENGTH_LONG)
							.show();
				} else {
					clients.setVisibility(View.VISIBLE);
				}
			}
		});

		power.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				Dialog dialog = new AlertDialog.Builder(
						YinXiangMainActivity.this)
						.setTitle("是否确定关机？")
						.setPositiveButton("是",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										ClientSendCommandService.msg = "key:power";
										ClientSendCommandService.handler
												.sendEmptyMessage(1);
									}
								})
						.setNegativeButton("否",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								}).create();
				dialog.show();
			}
		});

		mhandler = new Handler() {

			@Override
			public void handleMessage(Message msg1) {
				switch (msg1.what) {
				case 1:
					break;
				case 2:
					finish();
					break;
				default:
					break;
				}
				super.handleMessage(msg1);
			}
		};

	}

	/******************************************** 系统方法重载部分 *******************************************************/

	@Override
	protected void onResume() {
		super.onResume();
		if (ClientSendCommandService.titletxt != null) {
			title.setText(ClientSendCommandService.titletxt);
			// 增加获取FM频道列表
			ClientSendCommandService.handler.sendEmptyMessage(2);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (ClientSendCommandService.client != null) {
			try {
				ClientSendCommandService.client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (updateReceiver != null) {
			unregisterReceiver(updateReceiver);
			updateReceiver = null;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Log.i(TAG, "KEYCODE_BACK");
			isExitApp();
			return true;
		case KeyEvent.KEYCODE_MENU:
			return true;
		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	private boolean isExitApp() {
		AlertDialog.Builder builder = new Builder(
				YinXiangMainActivity.this);
		builder.setMessage("确认退出应用？");
		builder.setTitle("提示");
		builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ClientSendCommandService.titletxt = "未连接";
				// title.setText(ClientSendCommandService.titletxt);
				mhandler.sendEmptyMessage(2);
				dialog.dismiss();
				System.exit(0);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
		return true;
	}

	/************************************************ 升级部分 ***********************************************************/

	/**
	 * 更新信息
	 */
	private String updateMsgContent;

	/**
	 * 升级信息
	 */
	private String updateVersion;

	/**
	 * 系统更新的服务
	 */
	public void initUpdateThread() {
		/**
		 * 注册广播
		 */
		IntentFilter homefilter = new IntentFilter();
		homefilter.addAction("MAIN_UPDATE_DOWNLOAD");
		homefilter.addAction("MAIN_UPDATE_INSTALL");
		registerReceiver(this.updateReceiver, homefilter);

		/**
		 * 更新的时间检测如果当前更新过了就不用在更新
		 */
		UpdateLogService preferenceService = new UpdateLogService(this);
		String updateDate = preferenceService.getUpdateDate();
		if (!updateDate.equals("")
				&& updateDate.compareTo(DateUtils.to10String(new Date())) >= 0) {
			return;
		} else {
			preferenceService.saveUpdateDate();
		}

		if (UserUpdateService.updateFile.exists()) {
			/**
			 * 本地APK已存在流程
			 */
			fileExistFlow();
		} else {
			/**
			 * 本地文件不存在，从服务器获得更新流程
			 */
			fileNotExistFlow();
		}
	}

	@SuppressLint("NewApi")
	private void fileExistFlow() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 先比较本地下载APK和服务端最新的版本
					PackageManager pm = getPackageManager();
					PackageInfo instalPMInfo = pm.getPackageArchiveInfo(
							UserUpdateService.updateFile.getAbsolutePath()
									.toString(), PackageManager.GET_ACTIVITIES);
					final String updateMsg = getUpdateInfo();
					if (updateMsg != null) {
						JsonReader reader = new JsonReader(new StringReader(
								updateMsg));
						try {
							reader.beginObject();
							while (reader.hasNext()) {
								String name = reader.nextName();
								if (name.equals("version")) {
									updateVersion = reader.nextString();
								} else if (name.equals("updatecontent")) {
									updateMsgContent = reader.nextString();
								} else {
									reader.skipValue();
								}
							}
							reader.endObject();
						} catch (IOException e) {
							e.printStackTrace();
						}

						// 先比较本地程序和服务器的版本
						if (instalPMInfo != null) {
							int installVersionCode = instalPMInfo.versionCode;
							if (updateVersion != null
									&& !updateVersion.equals("")
									&& !updateVersion.equals("null")) {
								if (Integer.parseInt(updateVersion) > installVersionCode) {
									// 有更新弹框提示下载更新
									UserUpdateService.updateFile.delete();
									fileNotExistFlow();
									return;
								}
							}
						} else {
							// 文件包存在，但是又得不到信息，证明下载的文件又问题，重新下载
							UserUpdateService.updateFile.delete();
							fileNotExistFlow();
							return;
						}
					}

					// 在比较本地程序和安装APK的版本
					PackageInfo localPMInfo = pm
							.getPackageInfo(getPackageName(),
									PackageManager.GET_CONFIGURATIONS);
					if (localPMInfo != null) {
						int localVersionCode = localPMInfo.versionCode;
						Log.e(TAG, "安装的versionCode  " + localVersionCode);

						// 获取本地apk的versionCode
						if (instalPMInfo != null) {
							int installVersionCode = instalPMInfo.versionCode;
							Log.e(TAG, "未安装的versionCode  " + installVersionCode);

							if (installVersionCode <= localVersionCode) {
								UserUpdateService.updateFile.delete();
							} else {
								// 弹框提示安装
								Intent intent = new Intent(
										"MAIN_UPDATE_INSTALL");
								YinXiangMainActivity.this
										.sendBroadcast(intent);
							}
						}
					}
				} catch (PackageManager.NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	@SuppressLint("NewApi")
	private void fileNotExistFlow() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 服务器获得最新的版本信息
				String updateMsg = getUpdateInfo();
				if (updateMsg != null) {
					JsonReader reader = new JsonReader(new StringReader(
							updateMsg));
					try {
						reader.beginObject();
						while (reader.hasNext()) {
							String name = reader.nextName();
							if (name.equals("version")) {
								updateVersion = reader.nextString();
							} else if (name.equals("updatecontent")) {
								updateMsgContent = reader.nextString();
							} else {
								reader.skipValue();
							}
						}
						reader.endObject();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// 比较本地的版本和服务器端的版本
					try {
						PackageManager pm = getPackageManager();
						PackageInfo localPMInfo = pm.getPackageInfo(
								getPackageName(),
								PackageManager.GET_CONFIGURATIONS);
						if (localPMInfo != null) {
							int versionCode = localPMInfo.versionCode;
							if (updateVersion != null
									&& !updateVersion.equals("")
									&& !updateVersion.equals("null")) {
								if (Integer.parseInt(updateVersion) <= versionCode) {
									// 本地版本大于等于服务器版本，无更新
								} else {
									// 本地版本小于等于服务器版本有更新弹框提示下载更新
									Intent intent = new Intent(
											"MAIN_UPDATE_DOWNLOAD");
									YinXiangMainActivity.this
											.sendBroadcast(intent);
								}
							}
						}
					} catch (PackageManager.NameNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					// 没得到升级信息，不做处理
				}
			}
		}).start();
	}

	/**
	 * *****************************************服务器获得更新信息部分*********************
	 * ****************************
	 */

	private String getUpdateInfo() {
		String retSrc = null;

		/**
		 * 没有连接网络，提示用户，流程结束
		 */
		if (!NetworkUtils.isWifiConnected(getApplicationContext())) {
			return null;
		}

		/**
		 * 下载最新的版本信息
		 */
		try {
			URI url = URI.create(UserUpdateService.JSON_URL);
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 5000);
			HttpConnectionParams.setSoTimeout(params, 8000);
			HttpClient httpclient = new DefaultHttpClient(params);
			HttpGet httpRequest = new HttpGet(url);
			HttpResponse httpResponse = httpclient.execute(httpRequest);

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				retSrc = EntityUtils
						.toString(httpResponse.getEntity(), "UTF-8");
				Log.i(TAG, "getJsonData get Json  OK !");
			} else {
				Log.e(TAG, "getJsonData  get Json  ERROR !");
				return null;
			}

			retSrc = removeBOM(retSrc);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return retSrc;
	}

	public static final String removeBOM(String data) {
		if (TextUtils.isEmpty(data)) {
			return data;
		}
		if (data.startsWith("\ufeff")) {
			return data.substring(1);
		} else {
			return data;
		}
	}

	/**
	 * *******************************************下载和安装部分***********************
	 * ******************************
	 */

	private BroadcastReceiver updateReceiver = new BroadcastReceiver() {

		public void onReceive(Context mContext, Intent mIntent) {
			/**
			 * 升级文件下载
			 */
			if (mIntent.getAction().equals("MAIN_UPDATE_DOWNLOAD")) {

				// 如果用户不是连接的WIFI网络，直接返回不处理
				if (!NetworkUtils
						.isWifiConnected(YinXiangMainActivity.this)) {
					return;
				}

				// 直接开始下载程序不经过用户确认
				new Thread(new Runnable() {
					@Override
					public void run() {

						HttpURLConnection connection = null;
						try {
							UserUpdateService.downloading = true;

							/**
							 * 设置网络连接
							 */
							URL url = new URL(UserUpdateService.UPDATE_URL);
							connection = (HttpURLConnection) url
									.openConnection();
							connection.setUseCaches(false);
							connection.setRequestMethod("GET");
							connection.setConnectTimeout(10000);

							/**
							 * 开始下载文件
							 */
							if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
								connection.connect();
								InputStream instream = connection
										.getInputStream();
								RandomAccessFile rasf = new RandomAccessFile(
										UserUpdateService.updateFile, "rwd");
								byte[] b = new byte[1024 * 24];
								int length = -1;
								while ((length = instream.read(b)) != -1) {
									rasf.write(b, 0, length);
									Log.d("update file size",
											">>>>>" + rasf.length());
								}
								rasf.close();
								instream.close();

								// 下载完成安装
								Intent install = new Intent(
										"MAIN_UPDATE_INSTALL");
								YinXiangMainActivity.this
										.sendBroadcast(install);
							}

							/**
							 * 下载完成处理
							 */
							UserUpdateService.downloading = false;
						} catch (Exception e) {
							// 异常处理
							e.printStackTrace();
							if (UserUpdateService.updateFile.exists()) {
								UserUpdateService.updateFile.delete();
							}
							UserUpdateService.downloading = false;
						} finally {
							connection.disconnect();
						}
					}
				}).start();

			} else if (mIntent.getAction().equals("MAIN_UPDATE_INSTALL")) {
				// 安装最新的apk文件
				if (!UserUpdateService.updateFile.exists()) {
					return;
				}

				try {
					Runtime.getRuntime().exec(
							"chmod 0777  "
									+ UserUpdateService.updateFile
											.getAbsolutePath().toString());
				} catch (IOException e) {
					e.printStackTrace();
				}

				/**
				 * 如果用户点击的是直接下载，下载后直接更新，如果下载文件已经存在，就询问用户是否安装
				 */

				Builder builer = new Builder(YinXiangMainActivity.this);
				builer.setTitle("已经为您准备好更新");
				builer.setMessage("最新的版本已经下载完成,是否安装更新？");
				builer.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

								// 休息1秒安装
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

								// 安装新的APK
								Uri uri = Uri
										.fromFile(UserUpdateService.updateFile);
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.setDataAndType(uri,
										"application/vnd.android.package-archive");
								startActivity(intent);
							}
						});

				builer.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				AlertDialog dialog = builer.create();
				dialog.show();
			}
		}
	};

	/******************************************* 第一次进入显示帮助对话框 **************************************************/

	/**
	 * 如果用户是第一次进入该程序，则显示该帮助对话框，如果已经登录，不做任何操作
	 */
	private void initHelpDialog() {
		AppLogService service = new AppLogService(this);
		if (!service.isUserAlreadyEntrance()) {
			AppHelpDialog dialog = new AppHelpDialog(this);
			dialog.show();
		}
	}

	/**
	 * 获取fragment实例对象。
	 * @param index 索引值。
	 * @return
	 */
	public Fragment getInstanceByIndex(int index) {
		Fragment fragment = null;
		int fragmentIndex = matchFragmentIndex(index);
		switch (fragmentIndex) {
		case 1: // 遥控器
			fragment = new YinXiangRemoteControlFragment();
			break;
		case 2: // 网络电台
			fragment = new YinXiangFMFragment();
			break;
		case 3: // 一键推送
			fragment = new YinXiangCategoryFragment();
			break;
		case 4: // 设置Fragment
			fragment = new YinXiangSettingFragment();
			break;
		default:
			fragment = new YinXiangRemoteControlFragment();
			break;
		}
		return fragment;
	}

	/**
	 * 根据index匹配fragment的索引。
	 * @param index
	 * @return
	 */
	private int matchFragmentIndex(int index) {
		int reValue = 1;
		int childCount = radioGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			RadioButton child = (RadioButton) radioGroup.getChildAt(i);
			if (null != child && index == child.getId()){
				reValue=i+1;
				break;
			}
		}
		return reValue;
	}

}
