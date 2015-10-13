package com.changhong.yinxiang.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.AlarmMainActivity;
import com.changhong.yinxiang.music.MusicEditServer;
import com.changhong.yinxiang.music.MusicUtils;
import com.changhong.yinxiang.service.UserUpdateService;
import com.changhong.yinxiang.setting.AppHelpDialog;
import com.changhong.yinxiang.view.MyProgressDialog;


public class YinXiangSettingFragment extends Fragment {

	private static final String LOG_TAG = "SettingActivity";
	
	public static final int ACTION_AUTOCTRL_REQUEST_STATUS = 700;
	public static final int ACTION_AUTOCTRL_SEND_COMMAND= 800;
	public static final int ACTION_AUTOCTRL_UPDATE_STATUS = 900;
	public static final int ACTION_AUTOCTRL_ERROR = 1000;

	/**
	 * 返回到主菜单按钮
	 */
//	private Button settingReturn;
	/**
	 * 升级按钮, 下载的进度条, 系统升级的服务
	 */
	private TextView updateInfo;
	private LinearLayout updateBtn;
	private LinearLayout alarmBtn;
	private CheckBox autoCtrlBtn;
	private ProgressDialog m_pDialog;
	private UserUpdateService updateService;

	/**
	 * 系统评分、系统帮助
	 */
	private AppHelpDialog appHelpDialog;

	MusicEditServer mSystemFileServer = null;

	//远程处理进度条
	MyProgressDialog MProgressDialog =null;
	/**
	 * 消息处理
	 */
	private Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		//获取系统信息
	
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
		alarmBtn= (LinearLayout) v.findViewById(R.id.alarm_setting);
		autoCtrlBtn= (CheckBox) v.findViewById(R.id.autoctrl_setting);

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
					
				case ACTION_AUTOCTRL_REQUEST_STATUS:
					sendAutoCtrlMsg2YinXiang("requestAutoCtrlFlag","");
					mSystemFileServer.communicationWithServer(handler, MusicUtils.ACTION_SOCKET_COMMUNICATION, "getAutoCtrl");
					break;	
				case ACTION_AUTOCTRL_SEND_COMMAND:
					String autoSet=ClientSendCommandService.isAutoCtrl?"auto_on":"auto_off";
					sendAutoCtrlMsg2YinXiang("setAutoCtrl",autoSet);
					break;
				case ACTION_AUTOCTRL_UPDATE_STATUS:
					
					if(null != MProgressDialog && MProgressDialog.isShowing())	MProgressDialog.cancel();
					String autoCtrl=(String) msg.obj;
					
					if(null != autoCtrl){
					     boolean isAutoCtrl=(autoCtrl.contains("auto_on"))?true:false;
					     ClientSendCommandService.isAutoCtrl=isAutoCtrl;
				    	autoCtrlBtn.setChecked(isAutoCtrl);	
					}
					break;	
				case ACTION_AUTOCTRL_ERROR:		
					if(null != MProgressDialog && MProgressDialog.isShowing())	MProgressDialog.cancel();

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
		
		alarmBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyApplication.vibrator.vibrate(100);
				startSetAlarm();
			}
		});
		
	//增加自动控制开关按钮
		autoCtrlBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyApplication.vibrator.vibrate(100);
				CheckBox check = (CheckBox) v;
				ClientSendCommandService.isAutoCtrl=check.isChecked();
				Message msg=new Message();
				msg.arg1=ACTION_AUTOCTRL_SEND_COMMAND;
	             handler.sendMessage(msg);	             
			}
		});	
		requestAutoCtrlFlag();
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
	
	
	private void startSetAlarm(){
		Intent intent =new Intent(getActivity(), AlarmMainActivity.class);
		startActivity(intent);
	}

	/**
	 * *********************************************升级部分************************
	 * **********************************
	 */

	private void startUpdate() {
		updateService.initUpdateThread();
	}

	
	
	
	
	
	
	private void sendAutoCtrlMsg2YinXiang(String command, String param) {

		if (!NetworkUtils.isWifiConnected(getActivity())) {
			Toast.makeText(getActivity(), "请链接无线网络", Toast.LENGTH_SHORT).show();
			return;
		}

		if (!StringUtils.hasLength(ClientSendCommandService.serverIP)) {
			Toast.makeText(getActivity(), "手机未连接机顶盒，请检查网络", Toast.LENGTH_SHORT).show();
			return;
		}

		// 显示操作等待进度提示
		if (command.equals("requestAutoCtrlFlag")) {
			if(null == MProgressDialog){
			    MProgressDialog=new MyProgressDialog(getActivity());
			}		
			MProgressDialog.show("系统信息获取中，请稍后");
		}

		try {
			
			// 获取IP和外部存储路径
			String ipAddress = NetworkUtils.getLocalHostIp();
		
			// 封装文件为json格式
			JSONObject sendObj = new JSONObject();
			JSONArray array = new JSONArray();

			// music urls
			// 文件编辑类型： copy、clock
			array.put(0, command);
			// 第二个参数：如reName：则发送新的文件名。否则，赋值文件httpAddress
			if (StringUtils.hasLength(param)) {
				array.put(1, param);
			} else {
				array.put(1, ipAddress);
			}

			sendObj.put("autoctrl:", array);
			// 发送播放地址
			ClientSendCommandService.msg = sendObj.toString();
			ClientSendCommandService.handler.sendEmptyMessage(4);

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getActivity(), "音乐文件获取失败", Toast.LENGTH_SHORT).show();
			if(null != MProgressDialog)	MProgressDialog.cancel();
		}
	}

	
	private void requestAutoCtrlFlag(){
		sendAutoCtrlMsg2YinXiang("requestAutoCtrlFlag","");
		if(null == mSystemFileServer){
			    mSystemFileServer=MusicEditServer.creatFileEditServer();
		}
		mSystemFileServer.communicationWithServer(handler, MusicUtils.ACTION_SOCKET_COMMUNICATION, "requestAutoCtrlFlag");
		
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
