package com.changhong.yinxiang.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;

import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.service.UserUpdateService;
import com.changhong.yinxiang.setting.AppHelpDialog;


public class YinXiangSettingFragment extends Fragment {

	private static final String LOG_TAG = "SettingActivity";

	/**
	 * 返回到主菜单按钮
	 */
//	private Button settingReturn;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}
	
	
	@Override
   	public View onCreateView(LayoutInflater inflater, ViewGroup container,
   			Bundle savedInstanceState) {
       	View view= inflater.inflate(R.layout.fragment_yinxiang_setting, container,	false);
       	initialViews(view);
		initialEvents();
		initData();
		
		return  view;
   	}
    
	
	

	private void initialViews(View v) {
		// 初始化主界面
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.activity_yinxiang_setting);
		// 初始化按钮和事件
//		settingReturn = (Button) v.findViewById(R.id.btn_back);
		updateInfo = (TextView) v.findViewById(R.id.update_info);
		updateBtn = (LinearLayout) v.findViewById(R.id.update_info_btn);



		/**
		 * 进度条初始化
		 */
		m_pDialog = new ProgressDialog(getActivity());
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
					Toast.makeText(getActivity(), "恭喜您，已经是最新版本了",
							Toast.LENGTH_SHORT).show();
					break;
				case 200:
					Toast.makeText(getActivity(), "你没连接网络，请查看设置",
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
					Toast.makeText(getActivity(), "网络连接异常，请稍后重试",
							Toast.LENGTH_SHORT).show();
					break;
				case 600:
					Toast.makeText(getActivity(), "网络连接异常，请稍后重试",
							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};


//		settingReturn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				MyApplication.vibrator.vibrate(100);
//                getActivity().onBackPressed();
//			}
//		});

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
		updateService = new UserUpdateService(getActivity(), handler, m_pDialog);
		IntentFilter homefilter = new IntentFilter();
		homefilter.addAction("SETTING_UPDATE_DOWNLOAD");
		homefilter.addAction("SETTING_UPDATE_INSTALL");
		getActivity().registerReceiver(updateService.updateReceiver, homefilter);
	}

	private String getCurrentSystemVersion() {
		try {
			return getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
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
	public void onDestroy() {
		super.onDestroy();
		if (updateService.updateReceiver != null) {
			getActivity().unregisterReceiver(updateService.updateReceiver);
			updateService.updateReceiver = null;
		}
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		switch (keyCode) {
//		case KeyEvent.KEYCODE_BACK:
//			finish();
//			break;
//		default:
//			break;
//		}
//		return super.onKeyDown(keyCode, event);
//	}



}
