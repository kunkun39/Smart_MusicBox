package com.changhong.xiami.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.changhong.xiami.data.XiamiMoreRankAdapter;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.changhong.yinxiang.utils.Configure;
import com.google.gson.JsonElement;
import com.xiami.sdk.entities.RankListItem;

public class XiamiMoreRankActivity extends BaseActivity {

	private ListView rankList;
	private XiamiMoreRankAdapter adapter;
	private List<RankListItem> rankTypeList=new ArrayList<RankListItem>();

	private Handler mhandler = new Handler() {
		JsonElement element = null;
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case Configure.XIAMI_RANK_LIST:
				element = (JsonElement) msg.obj;
				rankTypeList=mXMMusicData.getRankListItem(element);
				adapter.setData(rankTypeList);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.xiami_rank_detail);

		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (Button) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);

		rankList = (ListView) findViewById(R.id.rank_detail_list);
		adapter = new XiamiMoreRankAdapter(XiamiMoreRankActivity.this);
		rankList.setAdapter(adapter);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();

		mXMMusicData.getRankType(mhandler);
		
		rankList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(XiamiMoreRankActivity.this,XiamiMusicListActivity.class);
				intent.putExtra("musicType", Configure.XIAMI_RANK_LIST);
				intent.putExtra("rankTitle", rankTypeList.get(position).getTitle());
				intent.putExtra("rankType", rankTypeList.get(position).getType());
				startActivity(intent);
			}
		});
	}

}
