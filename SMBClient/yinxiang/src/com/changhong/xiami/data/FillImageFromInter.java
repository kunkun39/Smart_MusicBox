package com.changhong.xiami.data;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;

public class FillImageFromInter extends Thread {

	private View iv;
	private ScheduledExecutorService scheES;
	private String url;
	private Handler hd;
	private int current=0;
	private int slidePeriod=4;//时间间隔，单位秒
	private int defaultBitmap=0;//默认的显示的图片ID
	private int animatiomTime=200;//动画持续时间，单位毫秒


	public FillImageFromInter(ImageView iv,String url,int defaultBitmap) {
		// TODO Auto-generated constructor stub
		this.iv = iv;
		this.url=url;
		this.defaultBitmap=defaultBitmap;
	}


	public void  starPlay(){
		scheES=Executors.newSingleThreadScheduledExecutor();
		scheES.scheduleAtFixedRate(new SlideShow(), 0, slidePeriod, TimeUnit.SECONDS);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		if(null==url||0==url.length()){
			if(defaultBitmap!=0)
			iv.setBackgroundResource(defaultBitmap);//设置默认图片
			return;
		}
		hd = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				setAnimation();
//				((ImageView)iv).setBackgroundDrawable(Drawable.createFromPath(url.get(current)));
			}

		};
		starPlay();
	}
	
	private void setAnimation() {
		AnimationSet animationset = new AnimationSet(true);
		AlphaAnimation alphaAnimation=new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(animatiomTime);
		animationset.addAnimation(alphaAnimation);
		iv.startAnimation(animationset);
	}

	private class SlideShow implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
//			current=(current+1)%bitmapsPath.size();
			hd.obtainMessage().sendToTarget();
		}
	}
}
