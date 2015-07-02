package com.changhong.yinxiang.remotecontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import android.content.Context;
import android.os.Handler;

import android.util.Log;

public class UDPSocketController extends SocketController {		
	
	GetContent mGetContent = null;
	
	public UDPSocketController(Context context,Handler handle) 
	{
		// TODO Auto-generated method stub
		super(context,handle);
		mGetContent = new GetContent();
		mGetContent.start();
		
	}

	
	private class GetContent extends Thread
	{
		DatagramSocket dgSocket = null;
		
		public void run() 
		{
			
			while(!mIsExit)
			{
				try 
				{
					dgSocket = new DatagramSocket(CONTENT_PORT);
					dgSocket.setReuseAddress(true);
					byte[] data = new byte[1024];
					DatagramPacket dgPacket = new DatagramPacket(data, data.length);
					
					while(true)
					{		
						if (mIsExit) {
							break ;
						}
						
						
						try {
							dgSocket.receive(dgPacket);
							
							try {
								if(mHandle != null)
									mHandle.sendMessage(mHandle.obtainMessage(EVENT_IM, new String(dgPacket.getData(), 0, dgPacket.getLength())));
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							
						} catch (IOException e) {
							// TODO: handle exception
							e.printStackTrace();
						}
												
						
					}				
					
				} 
				catch(SocketException e)
				{
					e.printStackTrace();
				}			
				catch (Exception e) 
				{
					// TODO: handle exception
					e.printStackTrace();
				}
				finally
				{
					try {
						if (dgSocket != null) 
							dgSocket.close();	
					} catch (Exception e2) {
						// TODO: handle exception
						e2.printStackTrace();
					}
					
				}
			}
		}
		
		public void close()
		{
			try {
				if (dgSocket != null) 
					dgSocket.close();	
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}
	}
	
	class ThreadContentSend extends Thread
	{
		@Override
        public void run() {
            DatagramSocket clientSocket = null;
            DatagramPacket dgPacket = null;            

            try {
                /**
                 * 鍙戦�佹秷鎭�
                 */
            	Log.d("RemoteSocketServer", "ThreadContentSend in");
                if (mRemoteInfo != null
                	&& mRemoteInfo.getIp() != null
					&& mRemoteInfo.getIp().length() > 0) 
                {
                	clientSocket = new DatagramSocket();
                	while ((dgPacket = mRemoteInfo.getPackage()) != null) {
                		try {
                			if (mIsExit) {
        						break;
        					}
                			clientSocket.send(dgPacket);	
						} catch (Exception e) {
							// TODO: handle exception
							Log.d(TAG, e.getMessage());
						}
                		
                		
                									
					}  
                	
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (clientSocket != null) {
                	try {
                		clientSocket.close();
					} catch (Exception e2) {
						// TODO: handle exception
						e2.printStackTrace();
					}
                    
                    clientSocket = null;
                }
            }
            Log.d("RemoteSocketServer", "ThreadContentSend out");
        }
	}
	
	@Override
	protected void sendContent(String data)
	{
		super.sendContent(data);
		new ThreadContentSend().start();		
	}	
	
	protected void clear()
	{
						
		super.clear();
		if (mGetContent != null)
		{
			try {
				mGetContent.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		}
								
	}

	@Override
	protected void onIpObtained(String ip) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onIpRemoved(String ip) {
		// TODO Auto-generated method stub
		
	}
}
