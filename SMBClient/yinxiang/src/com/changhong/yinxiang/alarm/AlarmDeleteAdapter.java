package com.changhong.yinxiang.alarm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.music.YinXiangMusic;

public class AlarmDeleteAdapter extends BaseAdapter {

	private List<Alarm> mAlarmList = null;
	private Context context;
	private LayoutInflater inflater;
	
	public static ArrayList<Alarm> selectAlarm = new ArrayList<Alarm>();
	
	
	public AlarmDeleteAdapter(Context con) {
		this.context = con;
		this.inflater = (LayoutInflater) con
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (null == mAlarmList) {
			mAlarmList = new ArrayList<Alarm>();
		}

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
	public void setMusicsCheckAll(boolean checkAll){
		selectAlarm.clear();
		   if(checkAll){
			   for (int i = 0; i < mAlarmList.size(); i++) {
				   Alarm alarm = mAlarmList.get(i);
					selectAlarm.add(alarm);
				}
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DataHolder dataHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.alarm_delete_item, null);
			dataHolder = new DataHolder();
			dataHolder.alarmLabel = (TextView) convertView
					.findViewById(R.id.alarm_delete_label);
			dataHolder.alarmTime = (TextView) convertView
					.findViewById(R.id.alarm_delete_time);
			dataHolder.alarmWeeks = (TextView) convertView
					.findViewById(R.id.alarm_delete_weeks);
			dataHolder.check = (CheckBox) convertView
					.findViewById(R.id.alarm_delete_check);

			convertView.setTag(dataHolder);
		} else {
			dataHolder = (DataHolder) convertView.getTag();
		}
		// 设置闹铃标签
		Alarm alarm = mAlarmList.get(position);
		if (alarm.label != null && alarm.label.length() != 0) {
			dataHolder.alarmLabel.setText(alarm.label);
			dataHolder.alarmLabel.setVisibility(View.VISIBLE);
		} else {
			dataHolder.alarmLabel.setVisibility(View.GONE);
		}
		// 设置闹铃时间
		String hour = alarm.hour > 9 ? "" + alarm.hour : ("0" + alarm.hour);
		String minutes = alarm.minutes > 9 ? "" + alarm.minutes
				: ("0" + alarm.minutes);
		String time = hour + ":" + minutes;
		dataHolder.alarmTime.setText(time);

		// 设置闹铃重复星期
		String daysOfWeek = alarm.daysOfWeek.toString(context, false);
		if (daysOfWeek != null && daysOfWeek.length() != 0) {
			dataHolder.alarmWeeks.setText(daysOfWeek);
			dataHolder.alarmWeeks.setVisibility(View.VISIBLE);
		} else {
			dataHolder.alarmWeeks.setVisibility(View.GONE);
		}

		// 改变闹铃信息
		dataHolder.check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Alarm alarm=mAlarmList.get(position);
				if(isChecked){
					if(!selectAlarm.contains(alarm))
					selectAlarm.add(alarm);
				}else{
					selectAlarm.remove(alarm);
				}
			}
		});
		
		if(selectAlarm.contains(alarm)){
			dataHolder.check.setChecked(true);
		}else{
			dataHolder.check.setChecked(false);
		}
		return convertView;
	}

	private final class DataHolder {

		// 时间
		public TextView alarmTime;

		// 星期
		public TextView alarmWeeks;

		// 标签
		public TextView alarmLabel;
		// 启用按钮
		public CheckBox check;


	}

}
