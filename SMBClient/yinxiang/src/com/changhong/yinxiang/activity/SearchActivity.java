package com.changhong.yinxiang.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.yinxiang.R;

public class SearchActivity extends Activity {

	/**
	 * 搜索类型定义
	 */
	private static final String music = "音乐";
	private static final String movie = "电影";
	private static final String[] type_str = { music, movie };
	private String locType = music;
	/**
	 * 控件
	 */
	private EditText search_keywords;
	private Button search_commit;
	private Spinner search_type;

	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		initData();

	}

	private void initView() {
		setContentView(R.layout.search_main);
		search_keywords = (EditText) findViewById(R.id.search_keywords);
		search_commit = (Button) findViewById(R.id.search_commit);
		search_type = (Spinner) findViewById(R.id.search_type);
	}

	private void initData() {
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, type_str);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		search_type.setAdapter(adapter);
		search_type.setOnItemSelectedListener(new SpinnerSelectedListener());

		search_commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 发送命令到机顶盒执行搜索功能
				String key = search_keywords.getText().toString();
				if (!TextUtils.isEmpty(key)) {
					StringBuffer sb=new StringBuffer();
					sb.append("search:");
					sb.append("|");
					sb.append(locType);
					sb.append("|");
					sb.append(key);
					ClientSendCommandService.msg = sb.toString();
					ClientSendCommandService.handler.sendEmptyMessage(1);
				}else {
					Toast.makeText(SearchActivity.this,"搜索内容不能为空！", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private class SpinnerSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			locType = type_str[position];
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
