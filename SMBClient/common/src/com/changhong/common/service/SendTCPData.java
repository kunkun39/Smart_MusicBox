package com.changhong.common.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import android.util.Log;

public class SendTCPData implements Runnable, ClientSocketInterface {
	private static boolean isSendPlaying = false;
	private LinkedList<String> sendDataList = null;
	private LinkedList<String> remoteIPList = null;
	private static SendTCPData sendRun = null;
	private String desIP = "0";
	private Socket socket = null;

	// 构造方法
	private SendTCPData() {
		// sendDataList = Collections.synchronizedList(new
		// LinkedList<byte[]>());
		sendDataList = new LinkedList<String>();
		remoteIPList = new LinkedList<String>();
	}

	// 获取实例
	public static SendTCPData getInstace() {
		if (null == sendRun) {
			sendRun = new SendTCPData();
		}
		return sendRun;
	}

	// 添加数据进入sendDataList队列
	public void addData(String data, String ip) {
		synchronized (sendDataList) {

			remoteIPList.addLast(ip);
			sendDataList.addLast(data);

		}
	}

	// 获取SOCKET
	public Socket getSocket() {
		return socket;
	}

	// 启动本接收线程
	public void startPlaying() {
		if (isSendPlaying)
			return;
		isSendPlaying = true;
		new Thread(this).start();
	}

	// 获取sendDataList队列里的第一个元素
	public String getFirstData() {
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
		String str = null;
		synchronized (sendDataList) {
			if (!sendDataList.isEmpty()) {
				str = sendDataList.removeFirst();
			}
		}
		return str;
	}

	private String getFirstIP() {
		String str = null;
		if (!remoteIPList.isEmpty()) {
			try {
				str = remoteIPList.removeFirst();
			} catch (NoSuchElementException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	public String getDesIP() {
		return desIP;
	}

	public void setDesIP(String desIP) {
		this.desIP = desIP;
	}

	@Override
	public void run() {
		if (!isSendPlaying) {
			return;
		}
		// if (socket == null || socket.isClosed()) {
		// try {
		// socket = new Socket(desIP, TCPPort);
		// } catch (UnknownHostException e) {
		// // TODO Auto-generated catch block
		// // 连接失败的时候重发
		//
		// Log.i("mm", "new Socket failed");
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// if (null == socket) {
		// return;
		// }
		// }
		String cache = null;
		String curIP = null;
		PrintWriter socketoutput = null;
		// 替换处
		while (isSendPlaying) {
			
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// 如果目标IP更改则重新new socket
			curIP = getFirstIP();
			cache = getFirstData();
			if (curIP != null && !desIP.equals(curIP)) {
				try {
					desIP = curIP;
					if (socket != null && !socket.isClosed()) {
						socket.close();
					}
					socket = new Socket(desIP, TCP_ALARM_PORT);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (curIP != null && cache!=null) {
				// 将控制包更新进状态map
				cache+=TCP_END;
				try {
					if (null == socketoutput) {
						socketoutput = new PrintWriter(
								socket.getOutputStream(), true);
					}
					socketoutput.println(cache);// 发送给服务器
					socketoutput.flush();// 清空缓存
					Log.i("mmmm", "TCP_cache:" + cache + "|curIP" + curIP);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					stopPlaying();
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// TODO Auto-generated catch block
					Log.i("mm", "Exception");
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		try {
			socketoutput.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendDataList.clear();
		remoteIPList.clear();
		// socket断开后重新发送广播,重新连接

	}

	// 启动TCP接收线程
	/**
	 * @param socket
	 */

	public boolean isSendPlaying() {
		return isSendPlaying;
	}

	public void stopPlaying() {
		isSendPlaying = false;
		sendRun=null;
	}
}
