package com.changhong.yinxiang.music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.changhong.yinxiang.activity.YinXiangMusicViewActivity;
import com.changhong.yinxiang.nanohttpd.HttpDownloader;
import com.changhong.yinxiang.utils.FileUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class MusicEditServer {

	private static final String Tag = "MusicEditServer::";
	private static MusicEditServer intance;
	private ServerSocket mServerSocket;
	private int SOCKET_PORT= 9009;
	private Handler mParentHandler;
	private Handler mMsgHandler;

	//socketServer接收服务器：
	private  Thread  mSocketCommunication=null;

	private MusicEditServer() {
		initMusicEditServer();
	}

	public static MusicEditServer creatFileEditServer() {
		if (null == intance) {
			intance = new MusicEditServer();
		}
		return intance;
	}

	/**
	 * 初始化全局变量，启动那个服务器
	 */
	private void initMusicEditServer() {
		
		//启动接收线程:
		clientCommunicationThread commThread = new clientCommunicationThread();
		mSocketCommunication = new Thread(commThread);
		mSocketCommunication.start();
		
		try {			
			mServerSocket = new ServerSocket(SOCKET_PORT);			
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	/**
	 * 接收音响端返回的信息
	 * 
	 * @return
	 */
	public void communicationWithServer(Handler handler, int communicationType,String param) {

		mParentHandler = handler;
		// 发送消息给子线程
		if (null != mMsgHandler) {
			Message sendMsg = mMsgHandler.obtainMessage();
			sendMsg.what=communicationType;
			sendMsg.obj=param;
			mMsgHandler.sendMessage(sendMsg);
		}
	}

	
	
	public void close() {
		try {		
			mServerSocket.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	
	/********************************************************** clientCommunicationThread   *******************************************************************/

	private class clientCommunicationThread implements Runnable {

		int communicationType;
		@Override
		public void run() {
		
			// 创建handler之前先初始化Looper
			Looper.prepare();

			mMsgHandler = new Handler() {

				public void handleMessage(Message msg) {

					communicationType = msg.what;

					// 接收来之通讯线程的消息
					switch (communicationType) {
					
					case MusicUtils.ACTION_HTTP_DOWNLOAD:
								String downLoadResult;
								String fileUrl = (String) msg.obj;
								String fileType = "music";
								if (fileUrl.toLowerCase().startsWith("http://") || fileUrl.toLowerCase().startsWith("https://")) {
									try {
										downLoadResult = HttpDownloader.download(fileUrl, fileType);
										Message respondMsg = mParentHandler.obtainMessage();
										respondMsg.obj=fileUrl;
										respondMsg.what = 2;
										if (downLoadResult.equals(MusicUtils.ACTION_SUCCESS)) {
											respondMsg.what = 3;
										} else if (downLoadResult.equals(MusicUtils.FILE_EXIST)) {
											respondMsg.what = 4;
										}
										mParentHandler.sendMessage(respondMsg);
										Log.e(Tag, "finish download file " + fileUrl);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}	
						break;
					case MusicUtils.ACTION_SOCKET_COMMUNICATION:
						    String editType=(String) msg.obj;
							socketAccept(editType);
					break;
				}
				}		
			};
			
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}

			Looper.loop();
	}
		
		
		
		
		
		
		
		private  void socketAccept( String editType){
			BufferedReader in = null;
			String content = "";
			Socket socketclient = null;
			try {
				// 设置接收延迟时间
				mServerSocket.setSoTimeout(30000);
				// 获取音响端发送的socket的对象
				socketclient = mServerSocket.accept();
				in = new BufferedReader(new InputStreamReader(socketclient.getInputStream()));
				// 接收从音响送来的数据
				String line = "";
				while ((line = in.readLine()) != null) {
					content += line;
				}
				System.out.println("recieve Infor::" + content);
				// 发送信息给主线程，更新YinXiangMusicViewActivityUI
				Message newMsg = mParentHandler.obtainMessage();
				if(editType.equals("requestMusicList")){
					 newMsg.what = YinXiangMusicViewActivity.SHOW_AUDIOEQUIPMENT_MUSICLIST;
					 newMsg.obj = getRespondMsg(content);							 
				}else{
				     newMsg.what = YinXiangMusicViewActivity.SHOW_ACTION_RESULT;
				     Bundle bundle=new Bundle();
				     bundle.putString("action", editType);
				     bundle.putString("result", content);
				     newMsg.setData(bundle);
				}
				mParentHandler.sendMessage(newMsg);

			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				
				System.out.println("mServerSocket.accept（） error:::::::::::::::::::::::::::::::::::::::::");

				e.printStackTrace();
				System.out.println("mServerSocket.accept（） error  end:::::::::::::::::::::::::::::::::::::::::");

			} finally {

				try {
					if (null != in) {
						in.close();
					}
					if (null != socketclient) {
						socketclient.close();
						socketclient=null;
					}						
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		}
		
		
		/**
		 * 封装发送数据为json 格式
		 * 
		 * @param context
		 * @param editType
		 * @param content
		 * @return
		 */
		private String getRespondMsg(String jsonStr) {

			String msgRespond="";
			try {

				JSONTokener jsonParser = new JSONTokener(jsonStr);

				// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
				// 如果此时的读取位置在"name" : 了，那么nextValue就是"返回对象了"（String）

				JSONObject msgObject = (JSONObject) jsonParser.nextValue();
				// 获取消息类型
			   String  msgAction = msgObject.getString("msgAction");
			   JSONArray array=msgObject.getJSONArray("msgRespond");
			   
			   if(msgAction.equals("requestMusicList")){
				   msgRespond=array.toString();			
			   }else{
				    
				   //文件操作结果返回
				     if(array.length()>0){
				    	 msgObject=array.getJSONObject(0);
				    	 msgRespond=msgObject.getString("doResult");
				     }
			   }
			   
			}catch (JSONException ex) {
					// 异常处理代码
					ex.printStackTrace();
			} 
			return msgRespond;
		}
		
		
		
	}

	
	
}
