package com.changhong.xiami.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.HorizontalListView;
import com.changhong.xiami.data.TodaySRecommendAdapter;
import com.changhong.xiami.data.XMMusicData;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.changhong.yinxiang.utils.Configure;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiami.sdk.entities.LanguageType;
import com.xiami.sdk.entities.OnlineAlbum;
import com.xiami.sdk.entities.OnlineSong;

public class XiamiMainActivity extends BaseActivity {

	/*
	 * 控件申明
	 */
	// 更多信息
	private Button moreToday, moreAlbum, moreRank, moreConcert;

	// 今日歌曲栏
	private HorizontalListView horListView;
	private ImageView xiamiMainSearch, randomMusic;
	
	private List<OnlineSong> todayRecomList=null;
	private TodaySRecommendAdapter TRAdapter=null;

	// 新碟首发
	private ImageView albumMsg1, playAlbum1,albumMsg2, playAlbum2,albumMsg3, playAlbum3;
	private TextView albumTitle1,albumTitle2,albumTitle3;
	
	private List<OnlineAlbum> newAlbums=null;

	// 排行榜
	private ImageView rankHY, rankOM;
	private ImageView playRankHY, playRankOM;

	// 音乐会
	private ImageView concertAlbum, concertScene, concertArtist,
			concertCollection;

	private XMMusicData XMData;
	
	/*
	 * 
	 * 申明handler的各种action
	 * 
	 */
	private final static int SHOW_TODAY_RECOMMEND_MUSIC=1;
	
	
	
	private Handler handler=new Handler(){
		JsonElement jsonData=null;
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			jsonData=(JsonElement) msg.obj;
			switch(msg.what){
			case Configure.XIAMI_TODAY_RECOMSONGS:
//				JsonObject 
				todayRecomList=XMData.getSongList(jsonData);
				TRAdapter.setData(todayRecomList);
				
				break;
			case Configure.XIAMI_NEW_ALBUMS:
				newAlbums=XMData.getAlbumList(jsonData);
				showNewAlbums();
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
				Intent todayIntent=new Intent(XiamiMainActivity.this,XiamiMusicListActivity.class);
				todayIntent.putExtra("musicType", 4);
				startActivity(todayIntent);
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
			case R.id.xiami_hyrank_image:
				break;
			case R.id.xiami_omrank_image:
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
		xiamiMainSearch = (ImageView) findViewById(R.id.xiami_search);
		randomMusic = (ImageView) findViewById(R.id.xiami_random_songs);

		albumMsg1 = (ImageView) findViewById(R.id.xiami_new_album1);
		playAlbum1 = (ImageView) findViewById(R.id.xiami_new_album1_play);
		albumTitle1=(TextView)findViewById(R.id.xiami_new_album1_title);
		albumMsg2 = (ImageView) findViewById(R.id.xiami_new_album2);
		playAlbum2 = (ImageView) findViewById(R.id.xiami_new_album2_play);
		albumTitle2=(TextView)findViewById(R.id.xiami_new_album2_title);
		albumMsg3 = (ImageView) findViewById(R.id.xiami_new_album3);
		playAlbum3 = (ImageView) findViewById(R.id.xiami_new_album3_play);
		albumTitle3=(TextView)findViewById(R.id.xiami_new_album3_title);

		rankHY = (ImageView) findViewById(R.id.xiami_hyrank_image);
		rankOM = (ImageView) findViewById(R.id.xiami_omrank_image);
		playRankHY = (ImageView) findViewById(R.id.xiami_hyrank_play);
		playRankOM = (ImageView) findViewById(R.id.xiami_omrank_play);

		concertAlbum = (ImageView) findViewById(R.id.xiami_concert_album);
		concertScene = (ImageView) findViewById(R.id.xiami_concert_scene);
		concertArtist = (ImageView) findViewById(R.id.xiami_concert_artist);
		concertCollection = (ImageView) findViewById(R.id.xiami_concert_collection);
		
		
		TRAdapter=new TodaySRecommendAdapter(XiamiMainActivity.this);
		horListView.setAdapter(TRAdapter);
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
		
		XMData=XMMusicData.getInstance(XiamiMainActivity.this);
		getXMData();
		
		
	}
	/*
	 * 
	 * 获取基础数据信息
	 */

	private void getXMData(){
		
		XMData.getTodayRecom(handler, 10);
		XMData.getNewALbums(handler, 1, 3);
	} 
	
	/*
	 * 
	 * 显示新碟首发
	 */
	private void showNewAlbums(){
		OnlineAlbum album1,album2,album3;
		album1=newAlbums.get(0);
		album2=newAlbums.get(1);
		album3=newAlbums.get(2);
		
		MyApplication.imageLoader.displayImage(album1.getImageUrl(300), albumMsg1);
		MyApplication.imageLoader.displayImage(album2.getImageUrl(300), albumMsg2);
		MyApplication.imageLoader.displayImage(album3.getImageUrl(300), albumMsg3);
		
		albumTitle1.setText(album1.getAlbumName()+"\n"+album1.getArtistName());
		albumTitle2.setText(album2.getAlbumName()+"\n"+album2.getArtistName());
		albumTitle3.setText(album3.getAlbumName()+"\n"+album3.getArtistName());
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
