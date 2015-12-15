package com.changhong.tvserver.search;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.changhong.tvserver.R;
import com.changhong.tvserver.utils.MyProgressDialog;
import com.xiami.sdk.XiamiSDK;
import com.xiami.sdk.entities.OnlineArtist;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.OnlineSong.Quality;
import com.xiami.sdk.entities.QueryInfo;
import com.xiami.sdk.entities.SearchSummaryResult;

public class SearchActivity extends Activity {

	private ListView searchSongList;
	private EditText searchKeyWords;
	private ImageView searchSubmit;
	private GridView singerList;
	private TextView  searchResult;

	private String s_KeyWords = null;
	public static final String keyWordsName = "StringKeyWords";
	public static Handler handler;

	/**
	 * 虾米搜索相关组件
	 */
	private XiamiSDK sdk;
	private SearchSummaryResult searchResultSum;
	private SearchSummaryAdapter adapter;
	private SingerAdapter mSingerAdapter;
	private List<OnlineSong> songs;
	private List<OnlineSong> songsfull;
	private List<OnlineArtist> artistList;

	private Pair<QueryInfo, List<OnlineSong>> results;

	private Quality curQuality = OnlineSong.Quality.L;
	/**
	 * 默认搜索结果每页个数
	 */
	private static final int PAGE_SIZE = 10;

	/**
	 * 默认搜索结果页码
	 */
	private static final int PAGE_INDEX = 1;
	
	/*
	 * 动画效果
	 */
	 private LinearLayout songLastSelected, songCurSelected;
	 private ImageView focusView;
	public Animation scaleBigAnim, scaleSmallAnim;
	
	public static final int REFRESH_SINGERLIST = 1000;
	
	private MyProgressDialog myProgress=null;


	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		initView();
		initData();
		initEvent();

	}

	private void initView() {

		setContentView(R.layout.activity_search);
		searchSongList = (ListView) findViewById(R.id.search_songs);
		searchKeyWords = (EditText) findViewById(R.id.search_keywords);
		singerList = (GridView) findViewById(R.id.search_singers);
		searchSubmit = (ImageView) findViewById(R.id.search_submit);
		searchResult=(TextView) findViewById(R.id.search_result);
		
		focusView = (ImageView) findViewById(R.id.image_selected);
		
		sdk = new XiamiSDK(this, SDKUtil.KEY, SDKUtil.SECRET);
		handler = new Handler(getMainLooper());
		adapter = new SearchSummaryAdapter(SearchActivity.this);
		searchSongList.setAdapter(adapter);

		mSingerAdapter = new SingerAdapter(SearchActivity.this, handler);
		singerList.setAdapter(mSingerAdapter);
		
	}

	private void initData() {
		myProgress=new MyProgressDialog(this);
		Intent intent = getIntent();
		s_KeyWords = intent.getStringExtra(keyWordsName);
		if (!TextUtils.isEmpty(s_KeyWords)) {
			searchKeyWords.setText(s_KeyWords);
			// 设置光标放在文本末尾
			searchKeyWords.setSelection(s_KeyWords.length());
			search(s_KeyWords);
		}

		searchSongList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// 获取网络音乐路径，并发送给播放器
				packageData(arg2);

			}
		});
		singerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				OnlineArtist curArtist=artistList.get(arg2);
				songsfull=matchArtistSongs(curArtist.getId());		
				adapter.changeSongs(songsfull);

			}
		});
		
		// 取消item焦点，同时配合android:descendantFocusability="afterDescendants"
		// searchSongList.setItemsCanFocus(true);

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 1:
					String keys = (String) msg.obj;
					searchKeyWords.setText(keys);
					// 设置光标放在文本末尾
					searchKeyWords.setSelection(keys.length());
					break;
				case REFRESH_SINGERLIST:
					break;

				}

			}
		};
		//设置动画效果
		scaleBigAnim = AnimationUtils.loadAnimation(SearchActivity.this,
				R.anim.scale_big);
		scaleSmallAnim = AnimationUtils.loadAnimation(SearchActivity.this,
				R.anim.scale_small);
		searchSongList.setOnItemSelectedListener(itemSelectedListener);
		searchSongList.setOnFocusChangeListener(itemChangeListener);
		
		singerList.setOnItemSelectedListener(itemSelectedListener);
		singerList.setOnFocusChangeListener(itemChangeListener);
	}

	private void packageData(int arg) {
		JSONObject o = new JSONObject();
		JSONArray array = new JSONArray();
		for (int i = arg; i < songsfull.size(); i++) {

			String path = songsfull.get(i).getListenFile();
			String title = songsfull.get(i).getSongName();
			String artist = songsfull.get(i).getArtistName();
			int duration = songsfull.get(i).getLength();
			long songId = songsfull.get(i).getSongId();

			JSONObject music = new JSONObject();
			music.put("id", songId);
			music.put("tempPath", path);
			music.put("title", title);
			music.put("artist", artist);
			music.put("duration", duration);
			array.put(music);
		}
		o.put("musicss", array.toString());
		o.put("musicType", "xiaMi");
		String listPath = "GetMusicList:" + o.toString();
		handleMusicMsgs(listPath);
	}

	private void handleMusicMsgs(String msg) {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.changhong.playlist",
				"com.changhong.playlist.Playlist"));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.putExtra("musicpath", msg);
		startActivity(intent);
	}

	private void initEvent() {
		searchKeyWords.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String text = arg0.toString();
				if (!TextUtils.isEmpty(text)) {
					search(text);
				}
			}
		});
	}

	private void search(final String keyWords) {

		if(null != myProgress){
			myProgress.show();
		}
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				
				results = sdk.searchSongSync(keyWords, PAGE_SIZE, PAGE_INDEX);
				songs = results.second;
				
				
				if (null == songsfull) {
					songsfull = new ArrayList<OnlineSong>();
				} else {
					songsfull.clear();
				}
				
				initArtistList();
				
				for (int i = 0; i < songs.size(); i++) {
					OnlineSong detail = sdk.findSongByIdSync(songs.get(i).getSongId(), curQuality);
					songsfull.add(detail);
					addArtist(detail);
				}
				searchSongList.post(new Runnable() {
					@Override
					public void run() {
						myProgress.cancel();
						if (results != null) {
							// 设置歌曲列表
							searchResult.setText("搜索结果：相关歌手"+(artistList.size()-1)+"位、相关歌曲"+songs.size()+"首");
							adapter.changeSongs(songsfull);
							mSingerAdapter.changeSingers(artistList);
						} else if (results.second.size() == 0) {
							Toast.makeText(SearchActivity.this, R.string.no_search_result,	Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(SearchActivity.this,	R.string.error_response, Toast.LENGTH_SHORT)	.show();
						}
					}
				});
			}
		});
		thread.start();
	}

	private void addArtist(OnlineSong song) {
		long artistID = song.getArtistId();
		int size = artistList.size();
		for (int i = 0; i < size; i++) {
			OnlineArtist onlineArtist = artistList.get(i);
			if(artistID == onlineArtist.getId())return;
		}
		//add artist
		OnlineArtist newArtist=new OnlineArtist();
		newArtist.setId(song.getArtistId());
		newArtist.setName(song.getArtistName());
		newArtist.setLogo(song.getArtistLogo());
		artistList.add(newArtist);
	}
	
	
	private void initArtistList(){
		if (null == artistList) {
			artistList = new ArrayList<OnlineArtist>();
		} else {
			artistList.clear();
		}		
		//增加全部艺人
		OnlineArtist all=new OnlineArtist ();
		all.setId(1);
		all.setName("全部");
		artistList.add(all);
	}
	
	/*
	 * 匹配艺人歌曲
	 */
	private List<OnlineSong> matchArtistSongs(long artistID){
		
		List<OnlineSong> songList = new ArrayList<OnlineSong>();
		int size=songs.size();
		for (int i = 0; i < size; i++) {
			OnlineSong song=songs.get(i);
			if(artistID==song.getArtistId() || 1==artistID){
				songList.add(song);
			}
		}
        return songList;
	}
	
	OnFocusChangeListener itemChangeListener =new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if (hasFocus) {
				if (null != songLastSelected) {
					selectedScaleBig(songLastSelected);
				}
			} else {
				if (null != songLastSelected) {
					selectedScaleSmall(songLastSelected);
				}
			}
		}
	};
	
	OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {

			songCurSelected = (LinearLayout) view.findViewById(R.id.layout_item);

			if (null != songLastSelected) {
				selectedScaleSmall(songLastSelected);
			}
			if (null != songCurSelected) {
				songLastSelected = songCurSelected;
				selectedScaleBig(songCurSelected);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};
	
	/**
	 * 选中项添加边框
	 */
	public void selectedScaleBig(LinearLayout selected) {
		Rect imgRect = new Rect();
		FrameLayout.LayoutParams focusItemParams = new FrameLayout.LayoutParams(
				10, 10);
		selected.getGlobalVisibleRect(imgRect);

		focusItemParams.leftMargin = 20;
		focusItemParams.topMargin = imgRect.top -160;
		focusItemParams.width = imgRect.width();
		focusItemParams.height = imgRect.height();

		focusView.setLayoutParams(focusItemParams);
		focusView.setImageResource(R.drawable.playlist_selected);
		focusView.setVisibility(View.VISIBLE);
		focusView.startAnimation(scaleBigAnim);
	}

	/**
	 * 去掉边框
	 */
	public void selectedScaleSmall(View disselected) {
		focusView.setVisibility(View.INVISIBLE);
		focusView.startAnimation(scaleSmallAnim);
		focusView.clearAnimation();
	}

	/**
	 * 系统方法复写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// initData();
	}

}
