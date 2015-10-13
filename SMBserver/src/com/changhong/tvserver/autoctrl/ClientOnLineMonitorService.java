/**
 * *
 * * 监测局域网内在线客户状态，通过在线客户状态自动控制开机或关闭音响设备
 * *
 */

package com.changhong.tvserver.autoctrl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import com.changhong.tvserver.fedit.Configure;
import com.changhong.tvserver.fedit.FileEditManager;
import com.changhong.tvserver.utils.MySharePreferences;
import com.changhong.tvserver.utils.MySharePreferencesData;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

public class ClientOnLineMonitorService extends Service {

	private static final String TAG = "ClientOnLineMonitorService";

	private   Boolean  debug=true;
	// 自动控制音响设备标志,true自动控制音响设备开机关机，false，不启用自动设置。
	private static Boolean isAutoControl = true;
	
	Boolean  isRunning=true;

	// 保存在线客户端列表
	ArrayList<String> clientOnLineList = new ArrayList<String>();

	public static String ACTION_AUTOCTRL_COMMAND = "com.changhong.autoctrl";
	public static String ACTION_UPDATE_AUTOCTRL= "com.changhong.updateAutoCtrl";

	
	//心跳信息内容
    public static String CH_CLIENT_HEARTBEAT = "client | heartBeat";

    private static  MySharePreferences mySharePreferences;
	/**
	 * 标志客户端心跳线程正在执行
	 */
	private boolean isRun = true;
	
	FileEditManager mFileEditManager;
	private ChangeAutoCtrlReceiver changeCtrlReceiver = null;


	/**
	 * 延迟时间参照
	 */
//	private int counter = 0;
	
	
	long  mTime;
    
    private get_ClientHeartBeat  mClientHeartBeat=null;;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initSmartCtrlService();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    flags = START_STICKY;  
		return super.onStartCommand(intent, flags, startId);
	}

	private void initSmartCtrlService() {

		clientOnLineList.clear();	
        isRun = true;
        mTime=0;     
		/**
		 * 需创建一个新的线程，否则，4.0版本以上会报异常
		 */
        if(null == mClientHeartBeat){
        	mClientHeartBeat=new  get_ClientHeartBeat();
        	mClientHeartBeat.start();
        }
        
        //获取auto状态。
        mySharePreferences= new MySharePreferences(this);
        MySharePreferencesData  shareData= mySharePreferences.InitGetMySharedPreferences();
        setAutoControlFlag(shareData.isAutoCtrl);
        
        changeCtrlReceiver = new ChangeAutoCtrlReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ClientOnLineMonitorService.ACTION_UPDATE_AUTOCTRL);
		registerReceiver(changeCtrlReceiver, filter);
        
        
        
	}

	public  Boolean isAutoControl() {
		boolean copyIsAutoControl;
		synchronized (isAutoControl) {
			copyIsAutoControl=isAutoControl;
		}
		return copyIsAutoControl;
	}

	public  void setAutoControlFlag(Boolean autoControl) {
		synchronized (isAutoControl) {
		       isAutoControl = autoControl;
		       Log.e(TAG, "setAutoControlFlag ="+autoControl);
		}
		mySharePreferences.SaveAutoCtrl(autoControl);
	}
	
	
	public void sendAutoCtrlFlagToClient(String clientIp){
		
		String autoCtrl=isAutoControl()?"auto_on":"auto_off";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(Configure.MSG_SEND, "autoCtrl:"+autoCtrl);
		params.put(Configure.IP_ADD, clientIp);
		FileEditManager.getInstance().communicationWithClient(null,	Configure.ACTION_SOCKET_COMMUNICATION, params);			
		Log.e(TAG, "sendAutoCtrlFlagToClient::  autoCtrl is  "+autoCtrl);
		
	}
	

	private class ChangeAutoCtrlReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action = intent.getAction();
			
			if (action	.equals(ClientOnLineMonitorService.ACTION_UPDATE_AUTOCTRL)) {
				
						String cmd = intent.getStringExtra("cmd");
						String parameter= intent.getStringExtra("parameter");

						if(cmd.equals("requestAutoCtrlFlag")){							
							   sendAutoCtrlFlagToClient(parameter);						   
						}else if(cmd.equals("setAutoCtrl")){
							// 设置自动控制标记
							setAutoControlFlag(parameter.equals("auto_on") ? true : false);
						}					
			}
		}

	}
	

	/************************************************* get client heartBeat *************************************************/

	/**
	 * 服务端接收客户端发来的socket心跳信息，通过心跳监测局域网内在线手机客户端设备状况
	 * 
	 * 
	 * 
	 * DatagramSocket:一开始就创建好 DatagramPacket:接收一个创建一个, 这样免得发生阻塞
	 */

	private class get_ClientHeartBeat extends Thread {

		public void run() {

			DatagramSocket dgSocket = null;

			try {
				
				dgSocket = new DatagramSocket(9008);
				//设置接收超时时间：10秒
				dgSocket.setSoTimeout(10000);
				DatagramPacket dgPacket = null;
				
				while (isRun) {

					if (isAutoControl()) {

						try {
							
							if(debug)
							 Log.e(TAG, "**************************************isAutoControl =true  and   start to receive *******************************************************");
							/**
							 * 接收client发送过来的 心跳包信息
							 */
							byte[] by = new byte[512];
							dgPacket = new DatagramPacket(by, by.length);
							dgSocket.receive(dgPacket);
							
						
							/**
							 * 处理Socket
							 */
							String clientAddress = dgPacket.getAddress().getHostAddress();
							String content = new String(by, 0,	dgPacket.getLength());
							
								
							//检测数据的有效性
							if (checkValidOfData(clientAddress,content)) {
								
								if(debug)
									 Log.e(TAG, "************************************** receive   clientAddress="+clientAddress+"finished*******************************************************");
								
						
								//判断当前通讯的client是否 第一个进入局域网在线客户端。
								if (firstOnLineClient(clientAddress)) {
									if(debug)
										 Log.e(TAG, "**************************************autoStartTVService*******************************************************");
									// 检查音响自动开启功能
									autoStartTVService();
								}								
								mTime=System.currentTimeMillis();					
							}
							
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							dgPacket = null;
						}
						
						
						//自动关机检查
						if (isStopTVService()) {
							if(debug)
								 Log.e(TAG, "**************************************isStopTVService*******************************************************");
							// 局域网内无联网客户端，延迟30分自动关机，
							autoStopTVService();
						}
						
					}									
					//线程休眠1秒
					Thread.sleep(10000);
					
				}
			} catch (Exception e) {
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

	/**
	 * 检验是否为局域网第一个联网设备？
	 */
	private boolean firstOnLineClient(String clientAddress) {
		boolean rValue = false;

		if (!clientOnLineList.contains(clientAddress)  ) {
			clientOnLineList.add(clientAddress);
			if (1 == clientOnLineList.size()  && mTime>0 && mTime <100)
				rValue = true;
		}
		return rValue;
	}

	/**
	 * 系统是否满足自动关机条件
	 */
	private boolean isStopTVService() {
		boolean rValue = false;

		 long curTime=System.currentTimeMillis();
		// 自动关机 , 条件1：局域网内无联网设备，连续时间长达30分
		 if (0 == clientOnLineList.size() && mTime > 100  && (curTime-mTime) > 120*1000) {
			 
			
			// 自动关机 , 条件2：增加时间区限制：早上7:00~9:00
			Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			// 时间格式12小时制，午后时间 +12；
			if (!DateFormat.is24HourFormat(this) && 0 != c.get(Calendar.AM_PM)) {
				hour += 12;
			}
            
			//预设自动关机时间早上7:00~9:00;
			if (hour >= 7 && hour < 20) {
				rValue = true;
			}
			mTime=20;

		} else if (0 != clientOnLineList.size() && (curTime-mTime) > 10000) {
			// 定时10秒清空用户列表
			clientOnLineList.clear();
			mTime=curTime;
		}else{
			if(debug)
				 Log.e(TAG, "**************************************isStopTVService     and    clientOnLineList.size()="+clientOnLineList.size()+"*******************************************************");

		}
		 
		return rValue;
	}
	
	
	/**
	 * 启动自动关机命令
	 */
	private void autoStopTVService() {
         
		if(isRunning){
			String msg = "key:autoctrl_off";
			// 创建Intent对象
			Intent intent = new Intent();
			// 设置Intent的Action属性
			intent.setAction(ACTION_AUTOCTRL_COMMAND);
			// 如果只传一个bundle的信息，可以不包bundle，直接放在intent里
			intent.putExtra("cmd", msg);
			// 发送广播
			sendBroadcast(intent);
		}
		isRunning=!isRunning;

	}

	/**
	 * 检查是否需要自动开机，如是，通过广播发送开机命令。
	 */

	private void autoStartTVService() {

		// 自动控制启动条件2: 时间区
		// 判断时间点是否晚上18:00~22:00之间，如是， 启动开启音响场景设置
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);

		// 时间格式12小时制，午后时间 +12；
		if (!DateFormat.is24HourFormat(this) && 0 != c.get(Calendar.AM_PM)) {
			hour += 12;
		}
   
		if(isRunning)return;
		
		// 检查当前时间是否在触发时间区条件内。
		if (hour >=  10 && hour < 22) {
			// 发送开机命令广播
			String msg = "key:autoctrl_on";
			// 创建Intent对象
			Intent intent = new Intent();
			// 设置Intent的Action属性
			intent.setAction(ACTION_AUTOCTRL_COMMAND);
			// 如果只传一个bundle的信息，可以不包bundle，直接放在intent里
			intent.putExtra("cmd", msg);
			// 发送广播
			sendBroadcast(intent);

			
			isRunning=true;
		}

	}
	
	
	/**
	 * 检查接收到的数据是否有效
	 * @param ipaddress  客户端地址
	 * @param content     数据包的内容："client | heartBeat"
	 * @return  true：可接收的数据包，false，无效包，丢弃。
	 */
	private boolean checkValidOfData(String ipaddress,String content){
		boolean reValue=false;
		
		if(null  != ipaddress && ipaddress.length()>0
				&& null  !=content && content.contains(CH_CLIENT_HEARTBEAT)){			   
	        reValue=true;
		}		
		return reValue;
	}
	
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
		isRun = false;		
       if(null != changeCtrlReceiver){
    	   unregisterReceiver(changeCtrlReceiver);
    	   changeCtrlReceiver=null;
       }
		
	}
}
