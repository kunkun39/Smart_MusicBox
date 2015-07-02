package com.changhong.yinxiang.remotecontrol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import java.util.Map;
import java.util.Queue;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class TCPSocketController extends SocketController {
	Map<String,TCPThread> mThreadMap = new HashMap<String, TCPThread>();
	
	TCPSocketController(Context context, Handler handle) {
		super(context, handle);
		// TODO Auto-generated constructor stub
	}

	
	public void sendContent(String data)
	{		
		super.sendContent(data);
		
		new SentConentThread().start();	
	}
	
	class SentConentThread extends Thread
	{
		@Override
		public void run() 
		{
			if(mRemoteInfo.getIp() == null)
				return ;
			
			DatagramPacket packet = null;
			while((packet = mRemoteInfo.getPackage()) != null)
			{			
					TCPThread thread = mThreadMap.get(packet.getAddress().getHostAddress());
					if (thread != null)
						thread.addTask(packet);							
			}
		}
	}

    
    protected void clear()
    {
    	super.clear();
    	
    	if (mThreadMap != null) 
		{
			Collection<TCPThread> collections = mThreadMap.values();
			for(TCPThread thread : collections) {				
				thread.close();
			}
			mThreadMap.clear();
		}	
    	mThreadMap = null;
    }
    
    protected void onIpObtained(String ip)
	{
    	if (mThreadMap == null
				|| mThreadMap.containsKey(ip))
			return ;
    	
    	if (mThreadMap.containsKey(ip)
    			&& mThreadMap.get(ip).isAlive())
    		return ;
    	
		TCPThread thread = new TCPThread(ip);
		mThreadMap.put(ip, thread);
		thread.start();
	}
	
	protected void onIpRemoved(String ip)
	{
		if (mThreadMap == null
				|| !mThreadMap.containsKey(ip))
			return ;
		
		TCPThread thread = mThreadMap.get(ip);
		if (thread != null
			&& thread.isAlive() == true
			&& thread.isInterrupted() == false) 
		{
			thread.close();
		}
		
		mThreadMap.remove(ip);
	}
	
	class TCPThread extends Thread
	{
		private String mIp = null;
		private Socket mSocket = null;		
		private Queue<DatagramPacket> mDataPackets = null;
		private boolean mIsExit = false;
		Thread  mThread = null;
		Thread mSendThread = null,mGetThread = null;
		
		
		public TCPThread(String ip)
		{
			mIp = ip;
			mDataPackets = new LinkedList<DatagramPacket>();
			mThread = this;
		}
		
		public void addTask(DatagramPacket packet)
		{
			if (mIp == null
				|| !(mIp.contains(packet.getAddress().getHostAddress()))) 
			return ;
			
			if (mDataPackets == null) 
				return ;
			
			mDataPackets.offer(packet);
			synchronized (mSendThread)
			{
				if (mSendThread != null) 
				{
					mSendThread.notifyAll();
				}
			}
			
		}
		
		public void close()
		{			
			mIsExit = true;
			if (mDataPackets != null) 
			{
				mDataPackets.clear();				
			}
			mDataPackets = null;
			
			if(mSocket != null)
			{
				try {
					mSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();					
				}
			}
			
						
		}
		
		@Override
		public void run() 
		{			
																										
			while(!mIsExit)
			{
				try {
					
					
					try
					{
						mSocket = new Socket(InetAddress.getByName(mIp), CONTENT_PORT);
						mSocket.setKeepAlive(true);
						mSocket.setTcpNoDelay(true);
					}
					catch(SocketException e)
					{
						e.printStackTrace();
						continue ;
					}					 
						
						
					if( mSocket != null
							&& mSocket.isConnected()
							&& !mSocket.isClosed())
					{
						if (mSendThread == null) 
						{
							mSendThread = new Thread(){
								@Override
								public void run()
								{	
									try {
										
										while(!mIsExit)
										{
											if (mSocket == null
													|| mSocket.isClosed() == true
													|| mSocket.isConnected() == false) 
												{
													break ;
												}
											doSendData();
											
											synchronized (mSendThread)
											{
												try {
													mSendThread.wait();
												}catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												
											}
										}
									}							
									catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();										
									} 
									finally
									{
										synchronized (mThread) 
										{
											mThread.notifyAll();
										}
									}
								}
							};
							mSendThread.start();
						}
						
						if (mGetThread == null) 
						{
							mGetThread = new Thread(){
								@Override
								public void run()
								{
									try {
										while(!mIsExit)
										{	
											if (mSocket == null
													|| mSocket.isClosed() == true
													|| mSocket.isConnected() == false) 
												{
													break ;
												}
											doGetData();											
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();										
									}
									finally
									{
										synchronized (mThread)
										{
											mThread.notifyAll();
										}
									}
								}
							};
							mGetThread.start();
						}
						
						synchronized (mThread) 
						{
							mThread.wait();
						}
					}
					
				}catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally
				{
					
					try {
						
						if(mSocket != null)
							mSocket.close();												
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				
			} 
			
			close();	
		}
		
		protected void doSendData() throws IOException,SocketException
		{				
			DatagramPacket pack = null;	
			DataOutputStream stream = new DataOutputStream(mSocket.getOutputStream());
			
			if(mDataPackets == null
					|| stream == null)				
				return ;
			
			while ((pack = mDataPackets.poll()) != null) 
			{							
				stream.write(pack.getData(), pack.getOffset(), pack.getLength());
				stream.flush();
			}	
			
			//stream.close();
		}
		
		protected void doGetData() throws IOException,SocketException
		{				
			byte[] pack = new byte[1024];
			DataInputStream stream = new DataInputStream(mSocket.getInputStream());
			int len = 0;

			
			if(0 < (len = stream.read(pack)))
			{
				String content = new String(pack, 0, len);
				if (mHandle != null) {
					
					try {
						mHandle.sendMessage(mHandle.obtainMessage(EVENT_IM,content));
						
					} catch (Exception e) {
						// TODO: handle exception
						Log.e(TAG, e.getMessage());
					}
				}
			}
			//stream.close();
		}
	}
	
}
