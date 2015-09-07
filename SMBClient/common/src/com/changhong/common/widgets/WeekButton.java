package com.changhong.common.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class WeekButton extends ImageView implements OnClickListener{


	public WeekButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public WeekButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	private boolean flag = false;
	private int press_up=0;
	private int press_down=0;
	/**
	 *  监听接口
	 */
	private OnChangedListener listener;

	public WeekButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void initBt(int press_up, int press_down, boolean flag) {
		this.flag = flag;
		this.press_up = press_up;
		this.press_down = press_down;
		if (flag) {
			setBackgroundResource(press_down);
		} else {
			setBackgroundResource(press_up);
		}
		setOnClickListener(this);
	}


	public boolean getFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
		if (flag) {
			setBackgroundResource(press_down);
		} else {
			setBackgroundResource(press_up);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (flag) {
			setBackgroundResource(press_up);
			flag=false;
		} else {
			setBackgroundResource(press_down);
			flag=true;
		}
		listener.OnChanged(WeekButton.this, flag);
	}

	
	/**
	 * 为WiperSwitch设置一个监听，供外部调用的方法
	 * 
	 * @param listener
	 */
	public void setOnChangedListener(OnChangedListener listener) {
		this.listener = listener;
	}
	/**
	 * 回调接口
	 * 
	 * @author len
	 * 
	 */
	public interface OnChangedListener {
		public void OnChanged(WeekButton wb, boolean checkState);
	}
}
