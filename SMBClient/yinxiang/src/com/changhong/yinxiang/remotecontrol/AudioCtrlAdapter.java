package com.changhong.yinxiang.remotecontrol;


import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 */
public class AudioCtrlAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private Context context;
	int itemCount;
	/**
	 * YD add 20150806 for fileEdit 音频文件编辑功能
	 */
       Handler mHandler=null;
       int itemWidth,itemHight;
       

	public AudioCtrlAdapter(Context context, int  itemNum,int width,int height) {
		this.context = context;
		itemCount=itemNum;
		itemWidth=width;
		itemHight=height;
	}
	


	

	public int getCount() {
		return itemCount;
	}

	public Object getItem(int item) {
		return item;
	}

	public long getItemId(int id) {
		return id;
	}

	// 创建View方法
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {		
			convertView =new TextView(context);
			AbsListView.LayoutParams param = new AbsListView.LayoutParams( itemWidth,itemHight);
			convertView.setLayoutParams(param);			
		} 
		return convertView;
	}
}
