package com.changhong.tvserver.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


public class FileEditManager {

	public static final String TAG = "FileEditManager";

	private static FileEditManager instance;

	private Handler mParentHandler, mMsgHandler;
	private Thread mMsgThread;
	private String clientIpAdd;
	private FileUtil mFileUtil = null;
	

	
	private FileEditManager() {
		init();
	}

	public static FileEditManager getInstance() {
		if (null == instance) {
			instance = new FileEditManager();
		}
		return instance;
	}

	public void init() {

		mFileUtil = new FileUtil();
		// 启动通讯线程
		CommunicationThread commThread = new CommunicationThread();
		mMsgThread = new Thread(commThread);
		mMsgThread.start();
		System.out.println("mMsgThread is " + mMsgThread.getName());
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 客户端与服务器通讯
	 * @param handler
	 * @param communicationType
	 * @param params
	 */
	public void communicationWithClient(Handler handler,  int communicationType,  Map<String, Object> params) {

		mParentHandler = handler;
		if (null != mMsgHandler) {// 发送消息给子线程
			Message sendMsg = mMsgHandler.obtainMessage();
			sendMsg.what = communicationType;
			sendMsg.obj=params;
			mMsgHandler.sendMessage(sendMsg);
		}
	}

	
	private String gotFileNameByUrl(String url) {
		String fileName = "";
		int fileSeparator = url.lastIndexOf(File.separator);
		if (fileSeparator > 0) {
			fileName = url.substring(fileSeparator);
		}
		return fileName;
	}

	/********************************************************** fileEdit   task *******************************************************************/

	class CommunicationThread implements Runnable {

		int communicationType;
		Map<String, Object> mParams;

		public void run() {

			// 创建handler之前先初始化Looper
			Looper.prepare();

			mMsgHandler = new Handler() {

				public void handleMessage(Message msg) {

					communicationType = msg.what;
					mParams=(Map<String, Object>) msg.obj;

					// 接收来之通讯线程的消息
					switch (communicationType) {
                     
					//Http通讯方式，根据URL执行文件下载任务
					case Configure.ACTION_HTTP_DOWNLOAD:						
						String downLoadResult;
						String fileUrl = (String) mParams.get("fileUrl");
						String fileName = (String) mParams.get("fileName");
						String fileType = "music";
						if (fileUrl.toLowerCase().startsWith("http://") || fileUrl.toLowerCase().startsWith("https://")) {
							try {
								downLoadResult = HttpDownloader.download(fileUrl, fileType, fileName);
								mFileUtil	.checkMaxFileItemExceedAndProcess(fileType);
								Bundle bundle = new Bundle();
								// 当前文件类型: music;
								bundle.putString(Configure.FILE_NAME, fileName);
								bundle.putString(Configure.FILE_URL, fileUrl);
								msg.setData(bundle);
								Message respondMsg = mParentHandler.obtainMessage();
								respondMsg.what = 2;
								if (downLoadResult.equals(Configure.ACTION_SUCCESS)) {
									respondMsg.what = 3;
								} else if (downLoadResult.equals(Configure.FILE_EXIST)) {
									respondMsg.what = 4;
								}
								mParentHandler.sendMessage(respondMsg);
								Log.e(TAG, "finish download file " + fileUrl);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}				
						break;
						
				     //执行socket通讯  发送信息： 机顶盒------> 手机。
					case Configure.ACTION_SOCKET_COMMUNICATION:
						
						String sendmsg = (String) mParams.get(Configure.MSG_SEND); 
						String clientIp=(String) mParams.get(Configure.IP_ADD); 
						postMassageBySocket(clientIp,sendmsg);						
						break;
					default:
						break;
					}
					// super.handleMessage(msg);
				}
			};
			Looper.loop();
		}
	}
	
	
	
	
	
	/**
	 * socket 通讯，机顶盒====手机
	 * @param clientIp 手机端的IP地址
	 * @param sendMsg 发送信息
	 * @return
	 */
	private  String  postMassageBySocket(String clientIp, String sendMsg){
		
		String result = "OK";
		Socket client = null;
		PrintWriter socketoutput = null;
		try {


			// 新建一个socket
			System.out	.println("++++++++++++++++++++++create  newSocket+++++++++++++++++++++++");
			client = new Socket(clientIp, Configure.SOCKET_PORT);
			// 从Socket获取一个输出对象，以便把sendMsg输入的数据发给客户端
			socketoutput = new PrintWriter(client.getOutputStream(), true);
			socketoutput.println(sendMsg);// 发送给服务器
			socketoutput.flush();// 清空缓存
			
			Thread.sleep(5000);

		} catch (Exception e) {
			result = "error";
			e.printStackTrace();
			System.out	.println("++++++++++++++++++++++create  newSocket error+++++++++++++++++++++++");

		} finally {

			try {
				if (null != socketoutput) {
					socketoutput.close();
				}
				if (null != client) {
					client.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	

	// 销毁线程池,该方法保证在所有任务都完成的情况下才销毁所有线程，否则等待任务完成才销毁
	public void destroy() {
		
	}

}
