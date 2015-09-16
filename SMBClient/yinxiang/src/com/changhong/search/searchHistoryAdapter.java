package com.changhong.search;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.changhong.common.utils.StringUtils;
import com.changhong.yinxiang.R;

public class searchHistoryAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private Context con;	
	private List<String> historysAct = new ArrayList<String>();
	public static List<String> selectHistorys = new ArrayList<String>();


	public searchHistoryAdapter(Context con, String str) {
		this.con = con;
		this.inflater = (LayoutInflater) con	.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setHistoryList(str);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return historysAct.size();
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
			convertView = inflater.inflate(R.layout.search_history_list_item, null);
			dh.name = (TextView) convertView.findViewById(R.id.search_history_item_name);
			dh.removeChecked = (CheckBox) convertView.findViewById(R.id.search_history_item_checked);
			convertView.setTag(dh);
		} else {
			dh = (DataHolder) convertView.getTag();
		}

		String history=historysAct.get(position);
		
		if(StringUtils.hasLength(history))dh.name.setText(history);
		else convertView.setVisibility(View.GONE);
		
		final boolean isChecked = selectHistorys.contains(history);
		dh.removeChecked.setChecked(isChecked);
		dh.removeChecked.setTag(history);
		dh.removeChecked.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox check = (CheckBox) v;
				String name=(String) check.getTag();
				if (check.isChecked()) {
					if (!selectHistorys.contains(name))
						selectHistorys.add(name);
				} else {
					   selectHistorys.remove(name);
				}
			}
		});

		return convertView;
	}
	
	
	public void setHistoryList(String str) {
		     historysAct.clear();
		     selectHistorys.clear();
		     String[] tokens=str.split(";");
		     for (int i = 0; i < tokens.length; i++) {
		    	 if(i<3)historysAct.add(tokens[i]);
			}		
	}
	

	private class DataHolder {

		// 推荐的图�?
		public TextView name;	
		// 删除是否被选中
		public CheckBox removeChecked;
	
	}
}
