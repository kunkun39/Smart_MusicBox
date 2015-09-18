package com.changhong.yinxiang.alarm;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.AlarmMainActivity;
import com.changhong.yinxiang.activity.BaseActivity;

public class DeleteAlarmActivity extends BaseActivity{

	private CheckBox checkAll;
	private Button confirm,cancel;
	private ListView list;
	private AlarmDeleteAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.alarm_delete_main);
		checkAll=(CheckBox)findViewById(R.id.checkAll);
		confirm=(Button)findViewById(R.id.confirm_delete);
		cancel=(Button)findViewById(R.id.cancel_delete);
		list=(ListView)findViewById(R.id.list);
		adapter=new AlarmDeleteAdapter(this);
		list.setAdapter(adapter);
		adapter.setData(AlarmMainActivity.mAlarmList);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
		
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				ArrayList<Alarm> selectAlarm=AlarmDeleteAdapter.selectAlarm;
				String data=null;
				if(selectAlarm.size()>0){
					for(int i=0;i<selectAlarm.size();i++){
						data=data+selectAlarm.get(i).getId()+"|";
					}
					ClientSendCommandService.msg=Alarm.delete+data;
					ClientSendCommandService.handler.sendEmptyMessage(1);
					
				}
				finish();
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		checkAll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox check = (CheckBox) v;
				if (check.isChecked()) {
					adapter.setMusicsCheckAll(true);
				} else {
					adapter.setMusicsCheckAll(false);
				}
				adapter.notifyDataSetChanged();
			}
		});
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
