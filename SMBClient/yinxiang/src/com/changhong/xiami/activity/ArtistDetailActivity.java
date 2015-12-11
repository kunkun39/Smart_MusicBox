package com.changhong.xiami.activity;

/**
 * 艺人详细信息
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.changhong.xiami.data.AlbumAdapter;
import com.changhong.xiami.data.SongAdapter;
import com.changhong.xiami.data.XMMusicData;
import com.changhong.xiami.data.XMPlayMusics;
import com.changhong.xiami.data.XiamiDataModel;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.changhong.yinxiang.utils.Configure;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiami.music.api.utils.RequestMethods;
import com.xiami.sdk.entities.OnlineAlbum;
import com.xiami.sdk.entities.OnlineArtist;
import com.xiami.sdk.entities.OnlineSong;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ArtistDetailActivity extends BaseActivity implements
		OnClickListener {

	private XMMusicData mXMMusicData;
	private ListView mSongList;
	private GridView mAlbumList;
	private TextView singer_name, singer_countLikes, songCount, infor_detail;
	private ImageView singerLogo, songMore, albumMore, inforMore;
	private SongAdapter adapter;
	private AlbumAdapter albumAdapter;
	private RelativeLayout layout1;

	

	private Handler mHandler;
	private final int MAX_PAGE_SIZE = 100;
	long curArtistID = -1;
	private XiamiDataModel model;
	private int page = 1;

	public static final int REFRESH_LOGO = 0x01;
	public static final int REFRESH_SONG_COUNT = 0x02;

	OnlineArtist curArtist=null;
	List<OnlineSong> songs = null;
	List<XiamiDataModel> albums = null;
	/**
	 * 专辑发行日期格式
	 */
	private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initView() {
		setContentView(R.layout.xiami_singer_container);
		model = (XiamiDataModel) getIntent().getSerializableExtra("XiamiDataModel");
		curArtistID = model.getId();
		mXMMusicData = XMMusicData.getInstance(this);

		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (ImageView) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);

		layout1=(RelativeLayout) findViewById(R.id.layout1);
		singerLogo = (ImageView) findViewById(R.id.singer_logo);
		mSongList = (ListView) findViewById(R.id.artist_hotsongs);
		singer_name = (TextView) findViewById(R.id.singer_name);
		singer_countLikes = (TextView) findViewById(R.id.singer_countlikes);
		songCount = (TextView) findViewById(R.id.hotsong_title);
		songMore = (ImageView) findViewById(R.id.song_more);

		infor_detail = (TextView) findViewById(R.id.infor_detail);
		albumMore = (ImageView) findViewById(R.id.album_more);
		// 信息更多详情
		inforMore = (ImageView) findViewById(R.id.infor_more);
		// 专辑
		mAlbumList = (GridView) findViewById(R.id.artist_albums);
		albumAdapter = new AlbumAdapter(this, mHandler);
		mAlbumList.setAdapter(albumAdapter);
		page = 1;
		
		
		
	}

	@Override
	protected void initData() {

		super.initData();

		singer_name.setText(model.getTitle());
		int likeCount = model.getLikeCount();
		if (likeCount > 10000)
			singer_countLikes.setText(likeCount / 1000 + "万");
		else
			singer_countLikes.setText(likeCount + "");

		songMore.setOnClickListener(this);
		albumMore.setOnClickListener(this);
		inforMore.setOnClickListener(this);
		//热门歌曲播放
		mSongList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				List<OnlineSong> songList = mXMMusicData.getSortSongs(songs,
						position);
				XMPlayMusics.getInstance(ArtistDetailActivity.this).playMusics(
						songList);
			}
		});
		//艺人专辑播放
		mAlbumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//进入专辑
				XiamiDataModel model = (XiamiDataModel) albumAdapter.getItem(position);
				long albumID = model.getId();
				if (albumID > 0) {
					Intent intent = new Intent(ArtistDetailActivity.this,	XiamiMusicListActivity.class);
					intent.putExtra("musicType", Configure.XIAMI_ALBUM_DETAIL);
					intent.putExtra("albumName", model.getTitle());				
					intent.putExtra("albumID", albumID);
					startActivity(intent);
				}
			}
		});
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case REFRESH_LOGO:
					String logo = model.getArtistImgUrl();
					logo = mXMMusicData.transferImgUrl(logo,Configure.IMAGE_SIZE5);
					ImageLoader.getInstance().displayImage(logo, singerLogo);
					break;

				case Configure.XIAMI_ARTIST_HOTSONGS:
					JsonElement jsonData = (JsonElement) msg.obj;
					handleXiamiResponse(jsonData,
							Configure.XIAMI_ARTIST_HOTSONGS);
					break;
				case Configure.XIAMI_ARTIST_ALBUMS:
					jsonData = (JsonElement) msg.obj;
					handleXiamiResponse(jsonData, Configure.XIAMI_ARTIST_ALBUMS);
					break;
				case Configure.XIAMI_ARTIST_DETAIL:
					jsonData = (JsonElement) msg.obj;
					handleXiamiResponse(jsonData, Configure.XIAMI_ARTIST_DETAIL);
					break;
				case Configure.XIAMI_RESPOND_FAILED:
					int errorCode = msg.arg1;
					Toast.makeText(ArtistDetailActivity.this, errorCode,
							Toast.LENGTH_SHORT).show();
					break;
				case Configure.XIAMI_ALBUM_DETAIL:
	                //获取专辑歌曲
					jsonData = (JsonElement) msg.obj;
					List<OnlineSong> songs=mXMMusicData.getTheAlbumSongs(jsonData);	
					XMPlayMusics.getInstance(ArtistDetailActivity.this).playMusics(songs);
					break;
				case Configure.XIAMI_PLAY_MUSICS:
	                //播放音乐列表
					int  albumID=msg.arg1;
					mXMMusicData.getTheAlbum(this, albumID);
				}
			}
		};
		mHandler.sendEmptyMessage(REFRESH_LOGO);
		albumAdapter = new AlbumAdapter(this, mHandler);
		mAlbumList.setAdapter(albumAdapter);
		setViewLayoutParams();
	
	}

	@Override
	protected void onStart() {
		super.onStart();
		page = 1;
		requestArtistInfor(RequestMethods.METHOD_ARTIST_HOTSONGS);
	}

	private void handleXiamiResponse(JsonElement jsonData, int method) {
		if (null == jsonData)
			return;

		JsonObject obj = jsonData.getAsJsonObject();

		if (Configure.XIAMI_ARTIST_HOTSONGS == method) {// 艺人热歌
			int total = mXMMusicData.getJsonObjectValueInt(obj, "total");
			songs = mXMMusicData.getTheAlbumSongs(jsonData);
			if (null != songs) {
				songCount.setText("热门歌曲   (" + total + "首)");
				mSongList.post(new Runnable() {
					@Override
					public void run() {
						adapter.updateListView(songs);
					}
				});
				requestArtistInfor(RequestMethods.METHOD_ARTIST_ALBUMS);
			}
		} else if (Configure.XIAMI_ARTIST_ALBUMS == method) {// 艺人专辑
			List<OnlineAlbum> onlineAlbums = mXMMusicData
					.getAlbumList(jsonData);
			albums = filledData(onlineAlbums);
			mAlbumList.post(new Runnable() {
				@Override
				public void run() {
					if (null != albums)
						albumAdapter.updateListView(albums,2);
				}
			});
			requestArtistDetail();
		}else if (Configure.XIAMI_ARTIST_DETAIL == method) {// 艺人专辑
			curArtist = mXMMusicData.getArtistDetail(jsonData);
			String artistDetail="标签："+curArtist.getCategory()+"\n"+"档案："+curArtist.getDescription();
			infor_detail.setText(artistDetail);
		}
	}

	/**
	 * 请求艺人热门歌曲、专辑
	 * @param method
	 */
	private void requestArtistInfor(String method) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("artist_id", curArtistID);
		params.put("limit", MAX_PAGE_SIZE);
		params.put("page", page);
		mXMMusicData.getJsonData(mHandler, method, params);
	}
	
	
	/**
	 * 请求艺人个人信息详情
	 * @param method
	 */
	private void requestArtistDetail() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("artist_id", curArtistID);
		params.put("full_des", true);
		mXMMusicData.getJsonData(mHandler, RequestMethods.METHOD_ARTIST_DETAIL, params);
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param datda
	 *            数据
	 * @return SortModel列表
	 */
	private List<XiamiDataModel> filledData(List<OnlineAlbum> albums) {

		if (null == albums)
			return null;

		List<XiamiDataModel> albumList = new ArrayList<XiamiDataModel>();

		int size = albums.size();
		for (int i = 0; i < size; i++) {

			OnlineAlbum album = (OnlineAlbum) albums.get(i);
			long albumID = album.getAlbumId();
			String imgUrl = album.getArtistLogo();
			String name = album.getAlbumName();
			Date mIssueTime = new Date(album.getPublishTime() * 1000);
			String content = album.getArtistName() + "\n"
					+ album.getAlbumCategory() + " "
					+ mFormat.format(mIssueTime);

			XiamiDataModel albumModel = new XiamiDataModel();
			albumModel.setId(albumID);
			albumModel.setTitle(name);
			albumModel.setLogoUrl(imgUrl);
			albumModel.setDescription(content);
			albumList.add(albumModel);
		}
		return albumList;
	}
	
	/**
	 * 重新布局View的Layout
	 */
	private void setViewLayoutParams(){
		RelativeLayout.LayoutParams param=(LayoutParams) singerLogo.getLayoutParams();
		param.height=param.width;
		singerLogo.setLayoutParams(param);
		param=(LayoutParams) mSongList.getLayoutParams();
		adapter = new SongAdapter(this,param.width,param.height);
		mSongList.setAdapter(adapter);
	    param=(LayoutParams) mAlbumList.getLayoutParams();
	    param.height=(int) ((mScreemHeight-65)*0.67f);
	    mAlbumList.setLayoutParams(param);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.song_more:
			if(null != curArtist){
				Intent intent = new Intent(ArtistDetailActivity.this,XiamiMusicListActivity.class);
				intent.putExtra("musicType",Configure.XIAMI_ARTIST_HOTSONGS);
				intent.putExtra("listID",curArtist.getId());
				intent.putExtra("listName","艺人热歌榜");
				startActivity(intent);
			}
			break;
		case R.id.album_more:
			if(null != curArtist){
				Intent intent = new Intent(ArtistDetailActivity.this,AlbumListActivity.class);
				intent.putExtra("albumList",Configure.XIAMI_ARTIST_ALBUMS);
				intent.putExtra("artist_id",curArtist.getId());
				startActivity(intent);
			}
			break;
		case R.id.infor_more:
			if(null != curArtist){
				Intent intent = new Intent(ArtistDetailActivity.this,ArtistResumeActivity.class);
				intent.putExtra("artistResume",curArtist.getDescription());
				startActivity(intent);
			}
			break;
		default:
			break;
		}
	}

}