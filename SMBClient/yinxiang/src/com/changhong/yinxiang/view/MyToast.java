package com.changhong.yinxiang.view;

import com.changhong.yinxiang.R;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

public class MyToast {
	private static Toast mToast = null;

	 private static Handler mHandler = new Handler();
	    private static Runnable r = new Runnable() {
	        public void run() {
	            mToast.cancel();
	        }
	    };
	
	public static void show(Context context, String msg) {
		if (null == mToast) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			TextView textView = (TextView) inflater.inflate(
					R.layout.textview, null);
			mToast = new Toast(context);
			mToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 250);
			mToast.setDuration(Toast.LENGTH_SHORT);
			mToast.setView(textView);
		}
		
		((TextView)mToast.getView()).setText(msg);
         mHandler.postDelayed(r, 500);
		mToast.show();

	}

}
