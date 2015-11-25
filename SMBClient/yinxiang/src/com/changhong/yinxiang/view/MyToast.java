package com.changhong.yinxiang.view;

import com.changhong.yinxiang.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

public class MyToast {
	private static Toast toast = null;

	public static void show(Context context, String msg) {
		if (null == toast) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			TextView textView = (TextView) inflater.inflate(
					R.layout.textview, null);
			toast = new Toast(context);
			toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 250);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setView(textView);
		}
		((TextView)toast.getView()).setText(msg);
		toast.show();

	}

}
