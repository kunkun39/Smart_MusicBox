package com.changhong.xiami.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.HorizontalListView;
import com.changhong.xiami.data.TodaySRecommendAdapter;
import com.changhong.xiami.data.XMMusicData;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.changhong.yinxiang.activity.SearchActivity;
import com.changhong.yinxiang.utils.Configure;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiami.sdk.entities.OnlineAlbum;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.utils.ImageUtil;

public class XiamiMainActivity extends BaseActivity {

	/*
	 * 控件申明
	 */
	// 更多信息
	private Button moreToday, moreAlbum, moreRank, moreConcert;

	// 今日歌曲�?
	private HorizontalListView horListView;
	private ImageView xiamiMainSearch, randomMusic;

	private List<OnlineSong> todayRecomList = null;
	private TodaySRecommendAdapter TRAdapter = null;

	// 新碟首发
	private ImageView albumMsg1, playAlbum1, albumMsg2, playAlbum2, albumMsg3,
			playAlbum3;
	private TextView albumTitle1, albumTitle2, albumTitle3;

	private List<OnlineAlbum> promotionAlbums = null;

	// 排行�?
	private ImageView rankHY, rankOM;
	private ImageView playRankHY, playRankAll;

	// 音乐�?
	private ImageView concertAlbum, concertScene, concertArtist,
			concertCollection;

	private XMMusicData XMData;

	/*
	 * 
	 */
	private List<OnlineSong> playList = new ArrayList<OnlineSong>();

	/*
	 * 
	 * 申明handler的各种action
	 */
	private final static int SHOW_TODAY_RECOMMEND_MUSIC = 1;

	private Handler handler = new Handler() {
		JsonElement jsonElement = null;
		JsonObject obj = null;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			jsonElement = (JsonElement) msg.obj;

			switch (msg.what) {
			case Configure.XIAMI_TODAY_RECOMSONGS:
				obj = jsonElement.getAsJsonObject();
				jsonElement = obj.get("songs");
				todayRecomList = XMData.getSongList(jsonElement);
				TRAdapter.setData(todayRecomList);

				break;
			case Configure.XIAMI_PROMOTION_ALBUMS:

				promotionAlbums = XMData.albumsElementToList(jsonElement);
				showNewAlbums();
				break;
			}
		}

	};

	private OnClickListener myClick = new OnClickListener() {
		Intent intent = null;

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.xiami_recommend_today_more:
				intent = new Intent(XiamiMainActivity.this,
						XiamiMusicListActivity.class);
				intent.putExtra("musicType", Configure.XIAMI_TODAY_RECOMSONGS);
				startActivity(intent);
				break;
			case R.id.xiami_promotion_album_more:
				intent = new Intent(XiamiMainActivity.this,
						AlbumListActivity.class);
				startActivity(intent);
				break;
			case R.id.xiami_rank_more:
				intent = new Intent(XiamiMainActivity.this,
						XiamiMoreRankActivity.class);
				startActivity(intent);
				break;
			case R.id.xiami_concert_more:
				break;
			case R.id.xiami_recommend_today:
				break;
			case R.id.xiami_search:
				intent = new Intent(XiamiMainActivity.this,
						SearchActivity.class);
				startActivity(intent);
				break;
			case R.id.xiami_random_songs:
				// 随便听听快捷方式
				ClientSendCommandService.msg = "key:music";
				ClientSendCommandService.handler.sendEmptyMessage(1);
				break;
			case R.id.xiami_new_album1:
				dealPromotionAlbums(intent, 0);

				break;
			case R.id.xiami_new_album1_play:
				break;
			case R.id.xiami_new_album2:
				dealPromotionAlbums(intent, 1);
				break;
			case R.id.xiami_new_album2_play:
				break;
			case R.id.xiami_new_album3:
				dealPromotionAlbums(intent, 2);
				break;
			case R.id.xiami_new_album3_play:
				break;
			case R.id.xiami_hyrank_image:
				dealRank(1);
				break;
			case R.id.xiami_hyrank_play:
				dealRank(2);
				break;
			case R.id.xiami_allrank_image:
				dealRank(3);
				break;
			case R.id.xiami_allrank_play:
				dealRank(4);
				break;
			case R.id.xiami_concert_album:
				intent = new Intent(XiamiMainActivity.this,
						AlbumListActivity.class);
				startActivity(intent);
				break;
			case R.id.xiami_concert_scene:
				intent = new Intent(XiamiMainActivity.this, SceneActivity.class);
				startActivity(intent);
				break;
			case R.id.xiami_concert_artist:
				intent = new Intent(XiamiMainActivity.this,
						ArtistListActivity.class);
				startActivity(intent);
				break;
			case R.id.xiami_concert_collection:
				intent = new Intent(XiamiMainActivity.this,
						CollectActivity.class);
				startActivity(intent);
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
		moreAlbum = (Button) findViewById(R.id.xiami_promotion_album_more);
		moreRank = (Button) findViewById(R.id.xiami_rank_more);
		moreConcert = (Button) findViewById(R.id.xiami_concert_more);

		horListView = (HorizontalListView) findViewById(R.id.xiami_recommend_today);
		xiamiMainSearch = (ImageView) findViewById(R.id.xiami_search);
		randomMusic = (ImageView) findViewById(R.id.xiami_random_songs);

		albumMsg1 = (ImageView) findViewById(R.id.xiami_new_album1);
		playAlbum1 = (ImageView) findViewById(R.id.xiami_new_album1_play);
		albumTitle1 = (TextView) findViewById(R.id.xiami_new_album1_title);
		albumMsg2 = (ImageView) findViewById(R.id.xiami_new_album2);
		playAlbum2 = (ImageView) findViewById(R.id.xiami_new_album2_play);
		albumTitle2 = (TextView) findViewById(R.id.xiami_new_album2_title);
		albumMsg3 = (ImageView) findViewById(R.id.xiami_new_album3);
		playAlbum3 = (ImageView) findViewById(R.id.xiami_new_album3_play);
		albumTitle3 = (TextView) findViewById(R.id.xiami_new_album3_title);

		rankHY = (ImageView) findViewById(R.id.xiami_hyrank_image);
		rankOM = (ImageView) findViewById(R.id.xiami_allrank_image);
		playRankHY = (ImageView) findViewById(R.id.xiami_hyrank_play);
		playRankAll = (ImageView) findViewById(R.id.xiami_allrank_play);

		concertAlbum = (ImageView) findViewById(R.id.xiami_concert_album);
		concertScene = (ImageView) findViewById(R.id.xiami_concert_scene);
		concertArtist = (ImageView) findViewById(R.id.xiami_concert_artist);
		concertCollection = (ImageView) findViewById(R.id.xiami_concert_collection);

		TRAdapter = new TodaySRecommendAdapter(XiamiMainActivity.this);
		horListView.setAdapter(TRAdapter);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		super.initData();
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
		playRankAll.setOnClickListener(myClick);

		concertAlbum.setOnClickListener(myClick);
		concertScene.setOnClickListener(myClick);
		concertArtist.setOnClickListener(myClick);
		concertCollection.setOnClickListener(myClick);

		horListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// 播放点击歌曲以后的所有今日推荐歌�?待完�?
				play(position, todayRecomList);
			}
		});

		XMData = XMMusicData.getInstance(XiamiMainActivity.this);
		getXMData();

	}

	/*
	 * 
	 * 获取基础数据信息
	 */

	private void getXMData() {

		XMData.getTodayRecom(handler, 10);
		XMData.getPromotionALbums(handler, 1, 3);
	}

	/*
	 * 
	 * 显示新碟首发
	 */
	private void showNewAlbums() {
		OnlineAlbum album1, album2, album3;
		album1 = promotionAlbums.get(0);
		album2 = promotionAlbums.get(1);
		album3 = promotionAlbums.get(2);

		MyApplication.imageLoader.displayImage(
				ImageUtil.transferImgUrl(album1.getArtistLogo(), 330),
				albumMsg1);
		MyApplication.imageLoader.displayImage(
				ImageUtil.transferImgUrl(album2.getArtistLogo(), 330),
				albumMsg2);
		MyApplication.imageLoader.displayImage(
				ImageUtil.transferImgUrl(album3.getArtistLogo(), 330),
				albumMsg3);

		albumTitle1.setText(album1.getAlbumName() + "\n"
				+ album1.getArtistName());
		albumTitle2.setText(album2.getAlbumName() + "\n"
				+ album2.getArtistName());
		albumTitle3.setText(album3.getAlbumName() + "\n"
				+ album3.getArtistName());
	}

	private void dealPromotionAlbums(Intent intent, int index) {
		if (null == promotionAlbums || promotionAlbums.size() < 3) {
			return;
		}
		intent = new Intent(XiamiMainActivity.this,
				XiamiMusicListActivity.class);
		intent.putExtra("musicType", Configure.MUSIC_TYPE_COLLECT);
		intent.putExtra("albumID", promotionAlbums.get(index).getAlbumId());
		startActivity(intent);
	}

	private void dealRank(int i) {
		Intent intent = null;
		if (1 == i) {
			intent = new Intent(XiamiMainActivity.this,
					XiamiMusicListActivity.class);
			intent.putExtra("musicType", Configure.XIAMI_RANK_HUAYU);
			startActivity(intent);
		} else if (2 == i) {

		} else if (3 == i) {
			intent = new Intent(XiamiMainActivity.this,
					XiamiMusicListActivity.class);
			intent.putExtra("musicType", Configure.XIAMI_RANK_ALL);
			startActivity(intent);
		} else if (4 == i) {

		}
	}

	/*
	 * 
	 * 播放专辑音乐
	 */
	private void play(final int position, final List<OnlineSong> list) {
		if (null == list && list.size() < position) {
			return;
		}
		if (!playList.isEmpty()) {
			playList.clear();
		}
		for (int i = position; i < list.size(); i++) {
			playList.add(list.get(i));
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				playList = XMData.getDetailList(playList);
				XMData.sendMusics(XiamiMainActivity.this, playList);
			}
		}).start();
	}

	/*
	 * ==========================================================================
	 * == 系统方法重载
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
