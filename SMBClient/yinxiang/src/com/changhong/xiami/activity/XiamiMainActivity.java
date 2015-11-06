package com.changhong.xiami.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changhong.common.widgets.HorizontalListView;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;

public class XiamiMainActivity extends BaseActivity {

	/*
	 * 控件申明
	 */
	// 更多信息
	private Button moreToday, moreAlbum, moreRank, moreConcert;

	// 今日歌曲栏
	private HorizontalListView horListView;
	private ImageButton xiamiMainSearch, randomMusic;

	// 新碟首发
	private ImageView albumMsg1, playAlbum1,albumMsg2, playAlbum2,albumMsg3, playAlbum3;

	// 排行榜
	private TextView rankHY, rankOM;
	private ImageView playRankHY, playRankOM;

	// 音乐会
	private ImageView concertAlbum, concertScene, concertArtist,
			concertCollection;

	
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case 1:
				break;
				
			}
		}
		
	};
	
	private OnClickListener myClick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.xiami_recommend_today_more:
				break;
			case R.id.xiami_recommend_album_more:
				break;
			case R.id.xiami_rank_more:
				break;
			case R.id.xiami_concert_more:
				break;
			case R.id.xiami_recommend_today:
				break;
			case R.id.xiami_search:
				break;
			case R.id.xiami_random_songs:
				break;
			case R.id.xiami_new_album1:
				break;
			case R.id.xiami_new_album1_play:
				break;
			case R.id.xiami_hyrank_text:
				break;
			case R.id.xiami_omrank_text:
				break;
			case R.id.xiami_hyrank_play:
				break;
			case R.id.xiami_omrank_play:
				break;
			case R.id.xiami_concert_album:
				break;
			case R.id.xiami_concert_scene:
				break;
			case R.id.xiami_concert_artist:
				break;
			case R.id.xiami_concert_collection:
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
	public void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.xiami_main_layout);
		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (Button) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);

		/*
		 * 本类控件
		 */

		moreToday = (Button) findViewById(R.id.xiami_recommend_today_more);
		moreAlbum = (Button) findViewById(R.id.xiami_recommend_album_more);
		moreRank = (Button) findViewById(R.id.xiami_rank_more);
		moreConcert = (Button) findViewById(R.id.xiami_concert_more);

		horListView = (HorizontalListView) findViewById(R.id.xiami_recommend_today);
		xiamiMainSearch = (ImageButton) findViewById(R.id.xiami_search);
		randomMusic = (ImageButton) findViewById(R.id.xiami_random_songs);

		albumMsg1 = (ImageView) findViewById(R.id.xiami_new_album1);
		playAlbum1 = (ImageView) findViewById(R.id.xiami_new_album1_play);
		albumMsg2 = (ImageView) findViewById(R.id.xiami_new_album2);
		playAlbum2 = (ImageView) findViewById(R.id.xiami_new_album2_play);
		albumMsg3 = (ImageView) findViewById(R.id.xiami_new_album3);
		playAlbum3 = (ImageView) findViewById(R.id.xiami_new_album3_play);

		rankHY = (TextView) findViewById(R.id.xiami_hyrank_text);
		rankOM = (TextView) findViewById(R.id.xiami_omrank_text);
		playRankHY = (ImageView) findViewById(R.id.xiami_hyrank_play);
		playRankOM = (ImageView) findViewById(R.id.xiami_omrank_play);

		concertAlbum = (ImageView) findViewById(R.id.xiami_concert_album);
		concertScene = (ImageView) findViewById(R.id.xiami_concert_scene);
		concertArtist = (ImageView) findViewById(R.id.xiami_concert_artist);
		concertCollection = (ImageView) findViewById(R.id.xiami_concert_collection);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		moreToday.setOnClickListener(myClick);
		moreAlbum.setOnClickListener(myClick);
		moreRank.setOnClickListener(myClick);
		moreConcert.setOnClickListener(myClick);
		
		xiamiMainSearch.setOnClickListener(myClick);
		randomMusic.setOnClickListener(myClick);
		
		albumMsg1.setOnClickListener(myClick);
		playAlbum1.setOnClickListener(myClick);
		albumMsg2.setOnClickListener(myClick);
		playAlbum2.setOnClickListener(myClick);
		albumMsg3.setOnClickListener(myClick);
		playAlbum3.setOnClickListener(myClick);
		
		rankHY.setOnClickListener(myClick);
		rankOM.setOnClickListener(myClick);
		playRankHY.setOnClickListener(myClick);
		playRankOM.setOnClickListener(myClick);
		
		concertAlbum.setOnClickListener(myClick);
		concertScene.setOnClickListener(myClick);
		concertArtist.setOnClickListener(myClick);
		concertCollection.setOnClickListener(myClick);
		
		
		
		
		
	}

	/*
	 * 
	 * 显示今日推荐歌曲信息
	 */
	private void showTodaySongs(){
		
		
	}
	
	/*============================================================================
	 * 系统方法重载
	 */

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
