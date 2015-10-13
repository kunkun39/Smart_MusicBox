package com.changhong.tvserver.fedit;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import android.content.Context;
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
	 * 
	 * @param handler
	 * @param communicationType
	 * @param params
	 */
	public void communicationWithClient(Handler handler, int communicationType,
			Map<String, Object> params) {

		mParentHandler = handler;
		if (null != mMsgHandler) {// 发送消息给子线程
			System.out.println("now start to send   msg to  MsgHandler ");

			Message sendMsg = mMsgHandler.obtainMessage();
			sendMsg.what = communicationType;
			sendMsg.obj = params;
			mMsgHandler.sendMessage(sendMsg);
		}
	}

	public String gotFileNameByUrl(String url) {
		String fileName = "";
		int fileSeparator = url.lastIndexOf(File.separator);
		if (fileSeparator > 0) {
			fileName = url.substring(fileSeparator + 1);
		}
		return fileName;
	}

	public String gotShortFileName(String url) {
		String fileName = gotFileNameByUrl(url);
		;
		if (fileName.length() >= 8)
			fileName = fileName.substring(0, 8) + "···";
		return fileName;
	}

	/********************************************************** fileEdit task *******************************************************************/

	class CommunicationThread implements Runnable {

		int communicationType;
		Map<String, Object> mParams;

		public void run() {

			// 创建handler之前先初始化Looper
			Looper.prepare();

			mMsgHandler = new Handler() {

				public void handleMessage(Message msg) {

					System.out.println("got parent  msg  and  communicationType is "+communicationType);

					
					communicationType = msg.what;
					mParams = (Map<String, Object>) msg.obj;

					// 接收来之通讯线程的消息
					switch (communicationType) {

					// Http通讯方式，根据URL执行文件下载任务
					case Configure.ACTION_HTTP_DOWNLOAD:
						String downLoadResult;
						String fileUrl = (String) mParams
								.get(Configure.FILE_URL);
						String editType = (String) mParams
								.get(Configure.EDIT_TYPE);
						if (fileUrl.toLowerCase().startsWith("http://")
								|| fileUrl.toLowerCase().startsWith("https://")) {
							try {
								
								mFileUtil	.checkMaxFileItemExceedAndProcess("music");
								String fileName = mFileUtil.getFileName(fileUrl);
								downLoadResult = HttpDownloader.download(fileUrl, "music", fileName);
								

								// 通讯结果回复给主线程
								if (null != mParentHandler) {
									Message respondMsg = mParentHandler
											.obtainMessage();
									Bundle bundle = new Bundle();
									// 当前文件类型: music;
									if (fileName.length() >= 8)
										fileName = fileName.substring(0, 8)
												+ "···";
									String result;
									if (downLoadResult
											.equals(Configure.ACTION_SUCCESS)) {
										result = fileName + ",下载成功";
									} else if (downLoadResult
											.equals(Configure.FILE_EXIST)) {
										result = fileName + ",文件已存在";
									} else if (downLoadResult
											.equals(Configure.ACTION_FAILED)) {
										result = "下载失败,请检查网络！";
									} else {
										result = fileName + ",文件超大";
									}

									// 获取本地媒体文件路径：
									bundle.putString(Configure.EDIT_TYPE,	editType);
									bundle.putString(Configure.MSG_RESPOND,result);
									bundle.putString(	Configure.FILE_URL,mFileUtil
													.convertHttpURLToLocalFile(fileUrl));
									respondMsg.setData(bundle);
									respondMsg.what = 2;
									mParentHandler.sendMessage(respondMsg);
								}
								Log.e(TAG, "finish download file " + fileUrl);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						break;

					// 执行socket通讯 发送信息： 机顶盒------> 手机。
					case Configure.ACTION_SOCKET_COMMUNICATION:

						System.out	.println("++++++++++++++++++++++start  postMassageBySocket+++++++++++++++++++++++");
						String sendmsg = (String) mParams.get(Configure.MSG_SEND);
						String clientIp = (String) mParams	.get(Configure.IP_ADD);
						postMassageBySocket(clientIp, sendmsg);
						System.out	.println("++++++++++++++++++++++end  postMassageBySocket+++++++++++++++++++++++");

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
	 * 
	 * @param clientIp
	 *            手机端的IP地址
	 * @param sendMsg
	 *            发送信息
	 * @return
	 */
	private String postMassageBySocket(String clientIp, String sendMsg) {

		String result = "error";
		Socket client = null;
		PrintWriter socketoutput = null;
		try {

			// 新建一个socket
			client = new Socket(clientIp, Configure.SOCKET_PORT);
			System.out	.println("++++++++++++++++++++++create  newSocket+++++++++++++++++++++++");
			if(null != client){
				
					// 从Socket获取一个输出对象，以便把sendMsg输入的数据发给客户端
					socketoutput = new PrintWriter(client.getOutputStream(), true);
					socketoutput.println(sendMsg);// 发送给服务器
					socketoutput.flush();// 清空缓存
					result = "OK";
					Log.i("mmmm", "Socket" +clientIp);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (null != socketoutput) {
					socketoutput.close();
					socketoutput=null;
				}
				if (null != client) {
					client.close();
					client=null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public void updateMediaStoreAudio(Context context, String fileName) {
		mFileUtil.updateGallery(context, fileName);
	}

	public void updateMediaStoreAudio(Context context, String oldFile,
			String newFile) {
		mFileUtil.updateGallery(context, oldFile, newFile);
	}

	// 销毁线程池,该方法保证在所有任务都完成的情况下才销毁所有线程，否则等待任务完成才销毁
	public void destroy() {

	}

}
