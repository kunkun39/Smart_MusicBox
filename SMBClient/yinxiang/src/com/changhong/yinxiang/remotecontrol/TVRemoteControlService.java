package com.changhong.yinxiang.remotecontrol;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import com.changhong.common.service.ClientSocketInterface;

public class TVRemoteControlService extends Service implements ClientSocketInterface
{

	Handler mHandler=null;
	Messenger mMessager = null,cMessenger = null;
	SocketController mSocket = null;
	
	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		mHandler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what)
				{				
					case EVENT_IMLAUNCH:
					{
						cMessenger = msg.replyTo;
					}
					break;
					case EVENT_IMCANCEL:
					{
						cMessenger = null;
					}
					break;
					case EVENT_IMLIKEDHINT:
					{
						mSocket.sendContent(packageContent(STR_IM_SHOWTIPS));
					}
					break;
					case EVENT_IMFININPUT:
					{						
						if (msg.getData() == null ||
								msg.getData().getString(STR_IM_FINISHINPUT) == null) {
							break;
						}
						
						mSocket.sendContent(packageContentWithTag(STR_IM_CONTENT
								,msg.getData().getString(STR_IM_FINISHINPUT)));				
					}
					break;
					case EVENT_IMHIDE:
					{
						mSocket.sendContent(packageContent(STR_IM_HIDE));
					}
					break;
					case EVENT_IMCOMMITE:
					{
						mSocket.sendContent(packageContent(STR_IM_COMMITE));
					}
					break;
					case EVENT_IMDELETE:
					{
						mSocket.sendContent(packageContent(STR_IM_DELCHAR));
					}
					break;
					case EVENT_IM:
					{
						setInputMethodState((String)msg.obj);
					}
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
			
		};
		mSocket = new UDPSocketController(this,mHandler);
		mMessager = new Messenger(mHandler);
				
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		
		return mMessager.getBinder();
		
	}
	
	@Override
	public boolean onUnbind(Intent intent)
	{
		return super.onUnbind(intent);
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(mSocket != null)
			mSocket.clear();
		mHandler = null;
		mMessager = null;
		cMessenger = null;
		
		super.onDestroy();
	}
	
	public static void update()
	{
		SocketController.setIsDirty(true);
	}
	
    void setInputMethodState(String state)
    {
    	if(state == null)
    		return ;
    	int status = 0; 
    	
    	int begin = state.indexOf(DEVIDE_TOKEN);
    	int last = state.indexOf(DEVIDE_TOKEN, begin + 1);
    	int nextbeg = state.indexOf(DEVIDE_MEG);
    	Object value = null;
    	int paramCount = 1;
    	if (begin != last 
    			&& last != -1
    			&& begin != 0
    			&& (last < nextbeg)) {
    		paramCount = 2;
		}
    	else {
    		paramCount = 1;			
		}
    	
    	if (state.contains(STR_IM_SHOW)) 
    	{
    		status = EVENT_IMSHOW;
		}
    	else if (state.contains(STR_IM_HIDE))
    	{
    		status = EVENT_IMHIDE;    		
    	}
    	else if (state.contains(STR_IM_CONTENT)) {
    		status = EVENT_IMDATA_GET;			
		}
    	else if (state.contains(STR_IM_USEDEFAULT))
    	{
    		status = EVENT_IMDEFAULT;    	
    	}
    	else if (state.contains(STR_IM_CHANGHONG))
    	{
    		status = EVENT_IMCHANGHONG;    		
    	}
    	else 
    	{
			return ;
		}
    	
    	switch (paramCount) {
		case 1:
			setInputMethodState(status);
			break;
		case 2:
		{
			value = state.substring(last+1,nextbeg).trim();//Integer.parseInt();
    		setInputMethodState(status,value);
		}
		break;

		default:
			break;
		}
    	
    	if (nextbeg != -1) {
    		String nextState = state.substring(state.indexOf(DEVIDE_MEG) + 1);
        	if (nextState != null
        			&& nextState.length() > 1) {
        		setInputMethodState(nextState);
    		}
		}
    	
    	
    	
    }
    
    private void setInputMethodState(int state)
    {
    	if (cMessenger != null) {
    		try {
    			Message msg = mHandler.obtainMessage();
        		msg.what = state;
        		cMessenger.send(msg);
				
			} catch (Exception e) {
				// TODO: handle exception
				Log.d(TAG, e.getMessage());
			}
    		
		}
    	/*
    	else {
    		handler.sendEmptyMessage(state);
		}
		*/
    	 
    }
    
    private void setInputMethodState(int state,Object arg)
    {
    	if (cMessenger != null) {
    		try {    			
        		cMessenger.send(mHandler.obtainMessage(state,arg));
				
			} catch (Exception e) {
				// TODO: handle exception
				Log.d(TAG, e.getMessage());
			}
    		
		}
    	/*
    	else {
    		handler.sendMessage(mHandler.obtainMessage(state, arg, 0));
    	}
    	*/
    }
    
	private static String packageContent(String Content)
	{
		return TAG + DEVIDE_TOKEN + Content + DEVIDE_MEG;
	}
	
	private static String packageContentWithTag(String tag,String Content)
	{
		return tag + DEVIDE_TOKEN + Content + DEVIDE_MEG;
	}
	
	
	
}
