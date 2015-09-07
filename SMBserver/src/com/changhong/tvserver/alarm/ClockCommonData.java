package com.changhong.tvserver.alarm;

import java.util.ArrayList;
import java.util.List;

import com.changhong.tvserver.MyApplication;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class ClockCommonData {

	public static ClockCommonData myData;
	private Cursor clockCursor;
	private List<Alarm> alarms = null;
	
	
	public static ClockCommonData getInstance(){
		if(null==myData){
			myData=new ClockCommonData();
		}
		return myData;
	}
	
	public void dealMsg(String[] keys){
		if (keys[1].equals("get")) {
			refreshAlarmsData();
			Log.i("mmmm", "alarms"+alarms);
		} else if (keys[1].equals("delete")) {

		} else if (keys[1].equals("insert")) {

		}else if (keys[1].equals("update")) {

		}
	}
	
	
	public List<Alarm> getAlarmsData(Context con){
		refreshAlarmsData();
		Log.i("mmmm", "alarms"+alarms);
		return alarms;
	}
	
	private void refreshAlarmsData() {
		// TODO Auto-generated method stub
		clockCursor = Alarms.getAlarmsCursor(MyApplication.getContext().getContentResolver());
		alarms = new ArrayList<Alarm>();
		clockCursor.moveToFirst();
		Alarm alarm;
		Log.i("mmmm", "refreshAlarmsData alarm size:>>" + clockCursor.getCount());
		if (clockCursor.getCount() > 0) {
			alarm = new Alarm(clockCursor);
			alarms.add(alarm);
			while (!clockCursor.isLast()) {
				clockCursor.moveToNext();
				alarm = new Alarm(clockCursor);
				alarms.add(alarm);
			}
		}
	}
}
