package com.changhong.xiami.data;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;

import com.xiami.sdk.entities.OnlineSong;

public class XMPlayMusics {
	
	private ExecutorService executor=Executors.newFixedThreadPool(2);
	
	private XMMusicData mXMMData=null;
	private static XMPlayMusics mXMPlayer=null;
	private Context context;
	private List<OnlineSong> playList=null;
	
	public XMPlayMusics(Context con){
		this.context=con;
		if(null==mXMMData){
			mXMMData=XMMusicData.getInstance(con);
		}
	}
			
	public static  XMPlayMusics getInstance(Context con){
		if(null==mXMPlayer){
			mXMPlayer=new XMPlayMusics(con);
		}
		return mXMPlayer;
	}
	
	public void playMusics(final List<OnlineSong> list){
		executor.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				playList = mXMMData.getDetailList(list);
				mXMMData.sendMusics(context,
						playList);
			}
		});
	}
}
