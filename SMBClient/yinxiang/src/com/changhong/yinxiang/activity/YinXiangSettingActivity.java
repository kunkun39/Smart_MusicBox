package com.changhong.yinxiang.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;
import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.service.UserUpdateService;
import com.changhong.yinxiang.setting.AppHelpDialog;

/**
 * Created by Jack Wang
 */
public class YinXiangSettingActivity extends Activity {

	private static final String LOG_TAG = "SettingActivity";

	/**
	 * 返回到主菜单按钮
	 */
	private Button settingReturn;
	/**
	 * 升级按钮, 下载的进度条, 系统升级的服务
	 */
	private TextView updateInfo;
	private LinearLayout updateBtn;
	private ProgressDialog m_pDialog;
	private UserUpdateService updateService;

	/**
	 * 系统评分、系统帮助
	 */
	private AppHelpDialog appHelpDialog;

	/**
	 * 消息处理
	 */
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initialViews();

		initialEvents();

		initData();
	}

	private void initialViews() {
		// 初始化主界面
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_yinxiang_setting);
		// 初始化按钮和事件
		settingReturn = (Button) findViewById(R.id.btn_back);
		updateInfo = (TextView) findViewById(R.id.update_info);
		updateBtn = (LinearLayout) findViewById(R.id.update_info_btn);



		/**
		 * 进度条初始化
		 */
		m_pDialog = new ProgressDialog(YinXiangSettingActivity.this);
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		m_pDialog.setMessage("正在下载更新");
		m_pDialog.setIndeterminate(false);
		m_pDialog.setCancelable(false);
	}

	private void initialEvents() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int what = msg.arg1;
				switch (what) {
				case 100:
					Toast.makeText(getApplicationContext(), "恭喜您，已经是最新版本了",
							Toast.LENGTH_SHORT).show();
					break;
				case 200:
					Toast.makeText(getApplicationContext(), "你没连接网络，请查看设置",
							Toast.LENGTH_SHORT).show();
					break;
				case 300:
					// 缺省
					break;
				case 400:
					if (m_pDialog != null && m_pDialog.isShowing()) {
						m_pDialog.dismiss();
					}
					break;
				case 500:
					if (m_pDialog != null && m_pDialog.isShowing()) {
						m_pDialog.dismiss();
					}
					Toast.makeText(getApplicationContext(), "网络连接异常，请稍后重试",
							Toast.LENGTH_SHORT).show();
					break;
				case 600:
					Toast.makeText(getApplicationContext(), "网络连接异常，请稍后重试",
							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};


		settingReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				finish();
			}
		});

		updateBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				startUpdate();
			}
		});
	}

	private void initData() {
		updateInfo.setText("当前系统版本:" + getCurrentSystemVersion());

		/**
		 * 注册广播
		 */
		updateService = new UserUpdateService(YinXiangSettingActivity.this, handler, m_pDialog);
		IntentFilter homefilter = new IntentFilter();
		homefilter.addAction("SETTING_UPDATE_DOWNLOAD");
		homefilter.addAction("SETTING_UPDATE_INSTALL");
		registerReceiver(updateService.updateReceiver, homefilter);
	}

	private String getCurrentSystemVersion() {
		try {
			return this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
			return "1.0";
		}
	}

	/**
	 * *********************************************升级部分************************
	 * **********************************
	 */

	private void startUpdate() {
		updateService.initUpdateThread();
	}

	/**
	 * *************************************************系统方法重载******************
	 * ********************************
	 */

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (updateService.updateReceiver != null) {
			unregisterReceiver(updateService.updateReceiver);
			updateService.updateReceiver = null;
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

}
