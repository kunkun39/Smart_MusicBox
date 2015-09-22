package com.changhong.tvserver.search;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.changhong.tvserver.R;
import com.xiami.sdk.XiamiSDK;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.OnlineSong.Quality;
import com.xiami.sdk.entities.QueryInfo;
import com.xiami.sdk.entities.SearchSummaryResult;

public class SearchActivity extends Activity {

	private ListView searchResultList;
	private EditText searchKeyWords;
	private String s_KeyWords = null;
	public static final String keyWordsName = "StringKeyWords";
	public static Handler handler;

	/**
	 * 虾米搜索相关组件
	 */
	private XiamiSDK sdk;
	private SearchSummaryResult searchResultSum;
	private SearchSummaryAdapter adapter;
	private List<OnlineSong> songs;
	private List<OnlineSong> songsfull;
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

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		initView();
		initEvent();

	}

	private void initView() {
		setContentView(R.layout.activity_search);
		searchResultList = (ListView) findViewById(R.id.search_result);
		searchKeyWords = (EditText) findViewById(R.id.search_keywords);

		sdk = new XiamiSDK(this, SDKUtil.KEY, SDKUtil.SECRET);
		handler = new Handler(getMainLooper());
		adapter = new SearchSummaryAdapter(SearchActivity.this);
		searchResultList.setAdapter(adapter);
	}

	private void initData() {
		Intent intent = getIntent();
		s_KeyWords = intent.getStringExtra(keyWordsName);
		if (!TextUtils.isEmpty(s_KeyWords)) {
			searchKeyWords.setText(s_KeyWords);
			// search(s_KeyWords);
		}

		searchResultList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// 获取网络音乐路径，并发送给播放器
				packageData(arg2);

			}
		});
		//取消item焦点，同时配合android:descendantFocusability="afterDescendants"
//		searchResultList.setItemsCanFocus(true);

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 1:
					String keys=(String) msg.obj;
					searchKeyWords.setText(keys);
					break;

				}

			}
		};
	}

	private void packageData(int arg) {
		JSONObject o = new JSONObject();
		JSONArray array = new JSONArray();
		for (int i = arg; i < songsfull.size(); i++) {

			String path = songsfull.get(i).getListenFile();
			String title = songsfull.get(i).getSongName();
			String artist = songsfull.get(i).getArtistName();
			int duration = songsfull.get(i).getLength();

			JSONObject music = new JSONObject();
			music.put("tempPath", path);
			music.put("title", title);
			music.put("artist", artist);
			music.put("duration", duration);
			array.put(music);
		}
		o.put("musicss", array.toString());

		// String listFileAddress =
		// Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"MusicList.json";
		// File jsonFile = new File(listFileAddress);
		// if (jsonFile.exists()) {
		// jsonFile.delete();
		// }
		// try {
		// jsonFile.createNewFile();
		// FileWriter fw = new FileWriter(jsonFile);
		// fw.write(o.toString(), 0, o.toString().length());
		// fw.flush();
		// fw.close();
		// } catch (IOException e) {
		// Toast.makeText(SearchActivity.this, "音频列表保存失败", Toast.LENGTH_SHORT)
		// .show();
		// e.printStackTrace();
		//
		// }

		String listPath = "GetMusicList:" + o.toString();
		handleMusicMsgs(listPath);
	}

	private void handleMusicMsgs(String msg) {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.changhong.playlist",
				"com.changhong.playlist.Playlist"));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
				for (int i = 0; i < songs.size(); i++) {
					songsfull.add(sdk.findSongByIdSync(
							songs.get(i).getSongId(), curQuality));
				}
				searchResultList.post(new Runnable() {
					@Override
					public void run() {
						if (results != null) {
							// 设置歌曲列表
							adapter.changeSongs(songsfull);
						} else if (results.second.size() == 0) {
							Toast.makeText(SearchActivity.this,
									R.string.no_search_result,
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(SearchActivity.this,
									R.string.error_response, Toast.LENGTH_SHORT)
									.show();
						}
					}
				});
			}
		});
		thread.start();
	}

	/**
	 * 系统方法复写
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initData();
	}

}
