package com.changhong.yinxiang.alarm;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.yinxiang.R;

public class AlarmAdapter extends BaseAdapter {

	public static List<Alarm> mAlarmList = null;
	private Context context;
	private LayoutInflater inflater;

	public AlarmAdapter(Context con) {
		this.context = con;
		this.inflater = (LayoutInflater) con
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(null==mAlarmList){
			mAlarmList=new ArrayList<Alarm>();
		}

	}

	// 初始化设置数据
	public void setData(ArrayList<Alarm> list) {
		mAlarmList.clear();
		AlarmAdapter.mAlarmList = list;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DataHolder dataHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.alarm_item, null);
			dataHolder = new DataHolder();
			dataHolder.alarmLabel = (TextView) convertView
					.findViewById(R.id.alarm_label);
			dataHolder.alarmTime = (TextView) convertView
					.findViewById(R.id.alarm_time);
			dataHolder.alarmWeeks = (TextView) convertView
					.findViewById(R.id.alarm_weeks);
			dataHolder.alarmEnable = (Switch) convertView
					.findViewById(R.id.enable);
			dataHolder.update=(LinearLayout)convertView.findViewById(R.id.update_alarm);
			
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
		//设置闹铃启用按钮的监听器
		dataHolder.alarmEnable.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					ClientSendCommandService.msg = Alarm.update + 1;
				}else{
					ClientSendCommandService.msg = Alarm.update + 0;
				}
				ClientSendCommandService.handler.sendEmptyMessage(1);
			}
		});
		
		//改变闹铃信息
		dataHolder.update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(context,SetAlarmActvity.class);
				intent.putExtra("select", position);
				context.startActivity(intent);
			}
		});
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
		public Switch alarmEnable;
		
		//
		public LinearLayout update;

	}
	

}
