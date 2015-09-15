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
import com.changhong.yinxiang.R;

public class searchHistoryAdapter extends BaseAdapter {

	private String str;
	private LayoutInflater inflater;
	private Context con;	
	private List<String> historysAct = new ArrayList<String>();
	public static List<Integer> selectHistorys = new ArrayList<Integer>();


	public searchHistoryAdapter(Context con, String str) {
		this.str = str;
		this.con = con;
		this.inflater = (LayoutInflater) con	.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return historysAct.size()>3?3:historysAct.size();
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
		dh.name.setText(historysAct.get(position));
		final boolean isChecked = selectHistorys.contains(history);
		dh.removeChecked.setChecked(isChecked);
		dh.removeChecked.setId(position);
		dh.removeChecked.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox check = (CheckBox) v;
				int id=check.getId();
				if (check.isChecked()) {
					if (!selectHistorys.contains(check))
						selectHistorys.add(id);
				} else {
					   selectHistorys.remove(id);
				}
			}
		});

		return convertView;
	}
	
	
	private void pareStrToHistoryList(String str) {
		     historysAct.clear();
		     String[] tokens=str.split(";");
		     for (int i = 0; i < tokens.length; i++) {
		    	 historysAct.add(tokens[i]);
			}		
	}
	

	private class DataHolder {

		// 推荐的图标
		public TextView name;	
		// 删除是否被选中
		public CheckBox removeChecked;
	
	}
}
