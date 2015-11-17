package com.changhong.xiami.activity;

/**
 * 艺人详细信息
 */
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.xiami.data.CharacterParser;
import com.changhong.xiami.data.PinyinComparator;
import com.changhong.xiami.data.SideBar;
import com.changhong.xiami.data.SongAdapter;
import com.changhong.xiami.data.SortAdapter;
import com.changhong.xiami.data.XMMusicData;
import com.changhong.xiami.data.XiamiDataModel;
import com.changhong.xiami.data.SideBar.OnTouchingLetterChangedListener;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.changhong.yinxiang.utils.Configure;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiami.music.api.utils.RequestMethods;
import com.xiami.sdk.entities.ArtistRegion;
import com.xiami.sdk.entities.OnlineArtist;
import com.xiami.sdk.entities.OnlineSong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ArtistDetailActivity extends BaseActivity {

	private XMMusicData mXMMusicData;
	private ListView mSongList;
	private TextView singer_name,singer_countLikes;
	private ImageView  singerLogo;
	private SongAdapter adapter;
	private Handler mHandler;
	private final int MAX_PAGE_SIZE = 100;
	long  curArtistID=-1;
	Bitmap  singerImg=null;
	private XiamiDataModel model;


	public static final int REFRESH_SINGER = 0x01;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initView() {
		setContentView(R.layout.xiami_singer_infor);

		model=(XiamiDataModel) getIntent().getSerializableExtra("XiamiDataModel");
		curArtistID=model.getId();			
		mXMMusicData = XMMusicData.getInstance(this);

		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (Button) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);
		
		
		singerLogo=(ImageView) findViewById(R.id.singer_logo);
		mSongList = (ListView) findViewById(R.id.song_list);
		singer_name=(TextView) findViewById(R.id.singer_name);
		singer_countLikes=(TextView) findViewById(R.id.singer_countlikes);		
		adapter = new SongAdapter(this);
		mSongList.setAdapter(adapter);

	}

	@Override
	protected void initData() {
		
		super.initData();
		
		singer_name.setText(model.getArtist());
		int likeCount=model.getLikeCount();
		if(likeCount>10000)	singer_countLikes.setText(likeCount/1000+" 万粉丝");
		else singer_countLikes.setText(likeCount+" 粉丝");
		ImageLoader.getInstance().displayImage(model.getArtistImgUrl(), singerLogo);
		
		// 长按进入歌手详情
		mSongList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						OnlineArtist artist = (OnlineArtist) parent
								.getItemAtPosition(position);
						Toast.makeText(ArtistDetailActivity.this,
								artist.toString(), Toast.LENGTH_SHORT).show();
						return true;
					}
				});
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case REFRESH_SINGER:
					int countLikes=msg.arg1;
					singer_countLikes.setText(countLikes+"");
					singerLogo.setImageBitmap(singerImg);
					break;
					
				case Configure.XIAMI_ARTIST_DETAIL:
					JsonElement jsonData = (JsonElement) msg.obj;
					handleXiamiResponse(jsonData);
					break;
				case Configure.XIAMI_RESPOND_FAILED:
					int errorCode=msg.arg1;
					Toast.makeText(ArtistDetailActivity.this, errorCode,	Toast.LENGTH_SHORT).show();
					break;	
				}
			}
		};
	}

	@Override
	protected void onStart() {
		super.onStart();		
//		HashMap<String, Object> params = new HashMap<String, Object>();
//		params.put("artist_id", curArtistID);
//		params.put("full_des", true);
//		mXMMusicData.getJsonData(mHandler,RequestMethods.METHOD_ARTIST_DETAIL, params);
		requestHotSongsByArtistId();
	}

	private void requestHotSongsByArtistId() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("artist_id", curArtistID);
		params.put("limit", 100);
		params.put("page", 1);	
		mXMMusicData.getJsonData(mHandler,RequestMethods.METHOD_ARTIST_HOTSONGS, params);
	}
	

	
	private void handleXiamiResponse(JsonElement jsonData) {
		if(null ==jsonData)return;
		
//		OnlineArtist  mOnlineArtist= mXMMusicData.getArtistDetail(jsonData);
//		String imgUrl=mOnlineArtist.getImageUrl();
		JsonObject obj = jsonData.getAsJsonObject();
		jsonData =obj.get("songs");

		final List<OnlineSong> songs   =   mXMMusicData.getSongList(jsonData);
		
		//显示歌手背景
//		Message msg=new Message();
//		msg.what=REFRESH_SINGER;
//		msg.arg1=mOnlineArtist.getCountLikes();
//		mHandler.sendMessage(msg);
		
		mSongList.post(new Runnable() {
			@Override
			public void run() {
				adapter.updateListView(songs);
			}
		});
		
	}
	
	
	
	/**
	 * 位图资源释放
	 */
	private void  BitmapRecycle(){
		if(singerImg != null && !singerImg.isRecycled()) {
			singerImg.recycle();
			singerImg=null;
		}	
	}
	
	
	
	
	
		
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BitmapRecycle();

	}

}