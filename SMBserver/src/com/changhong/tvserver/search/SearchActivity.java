package com.changhong.tvserver.search;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.changhong.tvserver.R;
import com.xiami.sdk.XiamiSDK;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.SearchSummaryResult;

public class SearchActivity extends Activity{

	private ListView searchResultList;
	private EditText searchKeyWords;
	private String s_KeyWords=null;
	public static final String keyWordsName="StringKeyWords";
	private Handler handler;
	
	
	/**
	 * 虾米搜索相关组件
	 */
	private XiamiSDK sdk;
	private SearchSummaryResult searchResultSum;
	private SearchSummaryAdapter adapter;
	private List<OnlineSong> songs;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		initView();
		initEvent();
		
	}

	private void initView(){
		setContentView(R.layout.activity_search);
		searchResultList=(ListView)findViewById(R.id.search_result);
		searchKeyWords=(EditText)findViewById(R.id.search_keywords);
		
		sdk = new XiamiSDK(this, SDKUtil.KEY, SDKUtil.SECRET);
		handler=new Handler(getMainLooper());
		adapter = new SearchSummaryAdapter(getLayoutInflater());
		searchResultList.setAdapter(adapter);
	}
	
	private void initData(){
		Intent intent=getIntent();
		s_KeyWords=intent.getStringExtra(keyWordsName);
		if(!TextUtils.isEmpty(s_KeyWords)){
			searchKeyWords.setText(s_KeyWords);
			search(s_KeyWords);
		}
		
		searchResultList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//获取网络音乐路径，并发送给播放器
				String path=songs.get(arg2).getListenFile();
				Log.i("mmmm", "SearchActivity-arg2:"+arg2+"|path="+path);
				handleMusicMsgs(path);
			}
		});
	}
	
	private void handleMusicMsgs(String msg){
    	Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.changhong.playlist", "com.changhong.playlist.Playlist"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("musicpath", msg);
        startActivity(intent);
    }
	
	private void initEvent(){
		searchKeyWords.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
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
                SearchSummaryResult result = sdk.searchSummarySync(keyWords, 10);
                searchResultSum = result;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshList();
                    }
                });
            }
        });
        thread.start();
    }
	
	private void refreshList() {
        if (searchResultSum != null) {
            adapter.swapData(searchResultSum);
            songs=searchResultSum.getSongs();
            Log.i("mmmm", "SearchActivity-songs.size:"+songs.size());
        }
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
