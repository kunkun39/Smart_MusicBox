package com.changhong.tvserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import android.util.Log;

import com.changhong.tvserver.alarm.ClockCommonData;
import com.changhong.tvserver.utils.StringUtils;

public class RecTCPRun implements Runnable {

	private static RecTCPRun recTCPRun = null;;
	private boolean isRecPlaying = false;
	private ClockCommonData handleData = null;
	private final int TCPPort = 9010;
	private ServerSocket mServerSocket;


	public static RecTCPRun getInstace() {

		if (null == recTCPRun) {
			recTCPRun = new RecTCPRun();
		}
		return recTCPRun;
	}

	public void startPlaying() {
		if (isRecPlaying){
			Log.i("mm", "RecTCPRunnable-startPlaying1");
			return;}
		Log.i("mm", "RecTCPRunnable-startPlaying2");
		isRecPlaying = true;
		new Thread(this).start();
	}

	public boolean isRecPlaying() {
		return isRecPlaying;
	}

	public void stopRecTcp() {
		isRecPlaying = false;
	}

	@Override
	public void run() {
		BufferedReader in = null;
		String content = "";
		Socket socketclient = null;
		// TODO Auto-generated method stub
		try {
			if(null == mServerSocket){
				mServerSocket = new ServerSocket(TCPPort);			
			}
			// 设置接收延迟时间
//			mServerSocket.setSoTimeout(30000);
			// 获取音响端发送的socket的对象
			socketclient = mServerSocket.accept();
			in = new BufferedReader(new InputStreamReader(socketclient.getInputStream()));
			// 接收从音响送来的数据
			String line = "";
			while ((line = in.readLine()) != null) {
				content += line;
			}
			System.out.println("recieve Infor::" + content);
		}catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		handleData = ClockCommonData.getInstance();
		String keys[]=StringUtils.delimitedListToStringArray(content, "|");
		handleData.dealMsg(keys);
		try {
			if (null != in) {
				in.close();
			}
			if (null != socketclient) {
				socketclient.close();
				socketclient=null;
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isRecPlaying=false;
	}
	
	public void stopPlaying() {
		isRecPlaying = false;

	}
}
