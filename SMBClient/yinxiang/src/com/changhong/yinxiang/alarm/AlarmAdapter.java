package com.changhong.yinxiang.alarm;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class AlarmAdapter extends BaseAdapter {

	private List<AlarmInfor> mAlarmList = null;

	// 初始化设置数据
	public void setData(ArrayList<AlarmInfor> list) {
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
	public void addData(AlarmInfor newAlarm) {
		if (mAlarmList != null && mAlarmList.size() > 0) {
			mAlarmList.add(newAlarm);

		} else {
			mAlarmList = new ArrayList<AlarmInfor>();
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
