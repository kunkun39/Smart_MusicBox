package com.changhong.yinxiang.alarm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;

import com.changhong.common.widgets.WeekButton;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;

public class SetAlarmActvity extends BaseActivity{

	private TimePicker timePicker;
	private Button confirm;
	private WeekButton weekBt1,weekBt2,weekBt3,weekBt4,weekBt5,weekBt6,weekBt7;

	// 进入设置界面后，先设置状态。
	private enum State {
		add, delete, update, get
	}
	//默认状态
	private State state=State.add;
			
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.alarm_setting);
		timePicker=(TimePicker)findViewById(R.id.timePicker);
		timePicker.setIs24HourView(true);
		weekBt1=(WeekButton)findViewById(R.id.week1);
		weekBt2=(WeekButton)findViewById(R.id.week2);
		weekBt3=(WeekButton)findViewById(R.id.week3);
		weekBt4=(WeekButton)findViewById(R.id.week4);
		weekBt5=(WeekButton)findViewById(R.id.week5);
		weekBt6=(WeekButton)findViewById(R.id.week6);
		weekBt7=(WeekButton)findViewById(R.id.week7);
		weekBt1.initBt(R.drawable.alarm1_up, R.drawable.alarm1_down, false);
		weekBt2.initBt(R.drawable.alarm2_up, R.drawable.alarm2_down, false);
		weekBt3.initBt(R.drawable.alarm3_up, R.drawable.alarm3_down, false);
		weekBt4.initBt(R.drawable.alarm4_up, R.drawable.alarm4_down, false);
		weekBt5.initBt(R.drawable.alarm5_up, R.drawable.alarm5_down, false);
		weekBt6.initBt(R.drawable.alarm6_up, R.drawable.alarm6_down, false);
		weekBt7.initBt(R.drawable.alarm7_up, R.drawable.alarm7_down, false);
		
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		Intent intent=getIntent();
		int position=intent.getIntExtra("select", -1);
		if(position<0){
			state=State.add;
			addAlarm();
			
		}else{
			state=State.update;
			updateAlarm(position);
		}
			
	}
	
	//进入修改流程
	private void updateAlarm(int position){
		
	}
	
	//进入添加流程
	private void addAlarm(){
		
	}

}
