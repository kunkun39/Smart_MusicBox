package com.changhong.tvserver.utils;


import com.changhong.tvserver.R;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;


public class Radar extends ImageView {
	

	Animation operatingAnim;
	
	
	public Radar(Context context) {
		super(context);
		operatingAnim = AnimationUtils.loadAnimation(context, R.anim.rate);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
	}
	
	public void startScan(){
		
		if (operatingAnim != null) {
			startAnimation(operatingAnim);
		}

	}
	

	
	public void StopScan(){
		clearAnimation();
	}
	
	
	public void ReStartScan(){
	}
}
