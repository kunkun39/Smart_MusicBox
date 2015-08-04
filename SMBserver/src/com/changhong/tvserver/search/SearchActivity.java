package com.changhong.tvserver.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ListView;

import com.changhong.tvserver.R;
import com.xiami.sdk.XiamiSDK;
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
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		initView();
		
		
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
