package com.changhong.search;

import com.baidu.android.common.logging.Log;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.SearchActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecommendAdapter extends BaseAdapter {

	private String[] str;
	private LayoutInflater inflater;
	private Context con;

	public RecommendAdapter(Context con, String[] str) {
		// TODO Auto-generated method stub
		this.str = str;
		this.con = con;
		this.inflater = (LayoutInflater) con
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Log.i("mmmm", "str:" + str.length + "=0=" + str[0]);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return str.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DataHolder dh;
		if (null == convertView) {
			dh = new DataHolder();
			convertView = inflater.inflate(R.layout.pop_recom_element, null);
			dh.tv = (TextView) convertView.findViewById(R.id.pr_element);
			convertView.setTag(dh);
		} else {
			dh = (DataHolder) convertView.getTag();
		}

		dh.tv.setText(str[position]);

		return convertView;
	}

	private class DataHolder {

		// 推荐的图标
		public TextView tv;
	}
}
