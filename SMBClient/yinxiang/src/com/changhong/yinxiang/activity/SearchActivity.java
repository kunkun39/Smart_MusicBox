package com.changhong.yinxiang.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.baidu.android.common.logging.Log;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.search.RecommendAdapter;
import com.changhong.search.XMMusicData;
import com.changhong.yinxiang.R;

public class SearchActivity extends Activity {

	/**
	 * 搜索类型定义
	 */
	private static final String music = "music";
	private static final String movie = "movie";
	private static final String[] type_str = { music, movie };
	private static final String[] type_rcn_str = { "音乐", "电影" };
	private String locType = music;
	/**
	 * 控件
	 */
	private EditText search_keywords;
	private ImageView search_commit;
	private Spinner search_type;
	private GridView recommend;

	private ArrayAdapter<String> adapter;

	/**
	 * 推荐专辑名列表
	 */
	private String[] mAlbumName = null;
	
	public Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case 1:
				mAlbumName = XMMusicData.getInstance(SearchActivity.this).getAlbumName();
				Log.i("mmmm", "mAlbumName"+mAlbumName.toString());
				if (mAlbumName != null && mAlbumName.length > 0) {
					setRecommendAlbum();
				}else {
					Toast.makeText(SearchActivity.this, R.string.error_response,
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
				
			
			super.handleMessage(msg);
		}
		
	};

	
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
		search_commit = (ImageView) findViewById(R.id.search_commit);
		search_type = (Spinner) findViewById(R.id.search_type);
		recommend = (GridView) findViewById(R.id.hot_tab);
	}

	private void initData() {
		adapter = new ArrayAdapter<String>(SearchActivity.this,
				android.R.layout.simple_spinner_item, type_rcn_str);
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
					searchKey(key);
				} else {
					Toast.makeText(SearchActivity.this,R.string.error_empty_keyword,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		XMMusicData.getInstance(SearchActivity.this).iniData(handler);
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

	private void setRecommendAlbum() {
		RecommendAdapter  recAdapter=new RecommendAdapter(SearchActivity.this, mAlbumName);
		recommend.setAdapter(recAdapter);
		recommend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.i("mmmm","mAlbumName[position]"+ mAlbumName[position]);
				searchKey(mAlbumName[position]);
			}
		});
	}

	private void searchKey(String key) {
		StringBuffer sb = new StringBuffer();
		sb.append("search:");
		sb.append("|");
		sb.append(locType);
		sb.append("|");
		sb.append(key);
		ClientSendCommandService.msg = sb.toString();
		ClientSendCommandService.handler.sendEmptyMessage(1);
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
