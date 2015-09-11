package com.changhong.yinxiang.alarm;

import java.util.ArrayList;
import java.util.List;

import com.changhong.yinxiang.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class AlarmAdapter extends BaseAdapter {

	private List<Alarm> mAlarmList = null;
	private Context context;
	private LayoutInflater inflater;
	
	public AlarmAdapter(Context con){
		this.context=con;
		this.inflater=(LayoutInflater)con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	
	// 初始化设置数据
	public void setData(ArrayList<Alarm> list) {
		mAlarmList.clear();
		this.mAlarmList = list;
		notifyDataSetChanged();
	}

	// 删除闹铃
	public void deleteData(int i) {
		if (mAlarmList != null && mAlarmList.size() > i) {
			mAlarmList.remove(i);
			notifyDataSetChanged();
		}
	}

	// 增加闹铃
	public void addData(Alarm newAlarm) {
		if (mAlarmList != null && mAlarmList.size() > 0) {
			mAlarmList.add(newAlarm);

		} else {
			mAlarmList = new ArrayList<Alarm>();
			mAlarmList.add(newAlarm);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mAlarmList.size();
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
		if (convertView == null) {
			convertView=inflater.inflate(R.layout.alarm_item, null);
		} else {

		}

		return convertView;
	}

	private final class DataHolder {

		// 时间
		public TextView alarmTime;

		// 星期
		public TextView alarmWeek;

		// 标签
		public TextView alarmTag;
		// 启用按钮
		public Switch alarmStart;

	}

}
