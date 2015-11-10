package com.changhong.yinxiang.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.search.RecommendAdapter;
import com.changhong.search.searchHistoryAdapter;
import com.changhong.xiami.data.XMMusicData;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.utils.MySharePreferences;
import com.changhong.yinxiang.utils.MySharePreferencesData;

public class SearchActivity extends BaseActivity {

	private static final String TAG = "SearchActivity::";

	/************************************************** IP连接部分 *******************************************************/

//	public static TextView title = null;
//	private Button listClients;
//	private Button back;
//	private ListView clients = null;
//	private BoxSelectAdapter IpAdapter;
	/**
	 * 搜索类型定义
	 */
	private static final String music = "music";
	private static final String movie= "movie";
	private static final String tv= "tv";
	private static final String[] type_str = { music, movie, tv};
	private static final String[] type_rcn_str = { "音乐", "影视" };
	private String locType = music;

	/**
	 * 控件
	 */
	private EditText search_keywords;
	private ImageView search_commit,search_tv,search_movie;
	private RadioGroup search_type;
	private GridView recommend;
   private LinearLayout hotSearch,videoType;
	private ArrayAdapter<String> adapter;	
	private MySharePreferences mSharePreferences=null;
	private ListView myHistory;
	private TextView  noHistoryList;
	private  Button  clearHistory;
	String musicRecords,vedioRecords;

	/**
	 * 推荐专辑名列表
	 */
	private String[] mAlbumName = null;
	
	public Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case 1:
				mAlbumName = (String[]) msg.obj;
				Log.i("mmmm", "mAlbumName"+mAlbumName.toString());
				if (mAlbumName != null && mAlbumName.length > 0) {
					setRecommendAlbum();
				}else {
					 Toast.makeText(SearchActivity.this, R.string.error_response, Toast.LENGTH_SHORT).show();
				}
				break;
			}			
			super.handleMessage(msg);
		}
		
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initSearchHistory();
		super.onCreate(savedInstanceState);
	
	}

	
	/**
	 * 初始化搜索历史记录，并分别对musicRecords和vedioRecords赋值
	 */
	private void initSearchHistory() {
		//获取搜索历史记录
			mSharePreferences=new MySharePreferences(this); 
			MySharePreferencesData shareData=mSharePreferences.InitGetMySharedPreferences();
			if(null != shareData.searchHistory){
				int index=shareData.searchHistory.indexOf("vedio:");
				musicRecords=shareData.searchHistory.substring("music:".length(),index);
				vedioRecords=shareData.searchHistory.substring(index+"vedio:".length());
				
				Log.e(TAG, "SearchHistory  is  "+shareData.searchHistory);

			}
			if(!StringUtils.hasLength(musicRecords))musicRecords="";
			if(!StringUtils.hasLength(vedioRecords))vedioRecords="";

	}

	protected void initView() {
		setContentView(R.layout.search_main);
		
		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (Button) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);
		search_keywords = (EditText) findViewById(R.id.search_keywords);
		search_commit = (ImageView) findViewById(R.id.search_commit);
		search_type = (RadioGroup) findViewById(R.id.search_type);
		recommend = (GridView) findViewById(R.id.hot_tab);
		hotSearch=(LinearLayout) findViewById(R.id.hot_search);
		videoType=(LinearLayout) findViewById(R.id.vedio_type);
		myHistory=(ListView) findViewById(R.id.history_search_infor);	
		noHistoryList=(TextView) findViewById(R.id.history_search_null);	
		clearHistory=(Button) findViewById(R.id.clear_search_history);	
	    search_movie = (ImageView) findViewById(R.id.vedio_type_movie);
	    search_tv = (ImageView) findViewById(R.id.vedio_type_tv);		
	    locType = music;
	}

	protected void initData() {		
        super.initData();
		search_commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				// 发送命令到机顶盒执行搜索功能
				String key = search_keywords.getText().toString();
				if (!TextUtils.isEmpty(key)) {
					searchKey(key);	
					//保存新增的搜索记录
					updateSearchHistory(key);
					
				} else {
					Toast.makeText(SearchActivity.this,R.string.error_empty_keyword,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		search_movie.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				// 发送命令到机顶盒执行搜索功能
				locType = type_str[1];
				search_movie.setBackgroundColor(SearchActivity.this.getResources().getColor(R.color.tab_textColor_selected));
				search_tv.setBackgroundColor(0);

			}
		});
		search_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				// 发送命令到机顶盒执行搜索功能
				locType = type_str[2];
				search_tv.setBackgroundColor(SearchActivity.this.getResources().getColor(R.color.tab_textColor_selected));
				search_movie.setBackgroundColor(0);
			}
		});
		search_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// 设备变化
				int childCount = search_type.getChildCount();
				for (int i = 0; i < childCount; i++) {
					RadioButton child = (RadioButton) search_type.getChildAt(i);
					if (null != child && checkedId == child.getId()) {
						locType = type_str[i];
						notifySearchTypeChange();
					}
				}
			}
		});
		
		clearHistory.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (searchHistoryAdapter.selectHistorys.isEmpty()) {
					Toast.makeText(SearchActivity.this,"请选择删除的记录", Toast.LENGTH_LONG).show();
				} else {
					String  tmpStr="";
					 for (int i = 0; i < searchHistoryAdapter.selectHistorys.size(); i++) {
							String name=searchHistoryAdapter.selectHistorys.get(i);	
							if(locType.equals(music) ){
								tmpStr=musicRecords=musicRecords.replace(name+";", "");
							}else{
								tmpStr=vedioRecords=vedioRecords.replace(name+";", "");
							}
					}
					 searchHistoryAdapter adapter=(searchHistoryAdapter) myHistory.getAdapter();
					 adapter.setHistoryList(tmpStr);
					 adapter.notifyDataSetChanged();
				}
			}			
		});		
		
		myHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {   
				     searchHistoryAdapter adapter=(searchHistoryAdapter) myHistory.getAdapter();
				     String record=adapter.getItemLable(view);	
				     search_keywords.setText(record);
			}
		});
		
		
		setSearchHistory();		
       ((RadioButton) search_type.getChildAt(0)).setChecked(true);
//	   XMMusicData.getInstance(SearchActivity.this).getAlbumName(handler);
	}


	private void setRecommendAlbum() {
		RecommendAdapter  recAdapter=new RecommendAdapter(SearchActivity.this, mAlbumName);
		recommend.setAdapter(recAdapter);
		//取消GridView中Item选中时默认的背景色
		recommend.setSelector(new ColorDrawable(Color.TRANSPARENT));
		recommend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.i("mmmm","mAlbumName[position]"+ mAlbumName[position]);
				searchKey(mAlbumName[position]);
			    search_keywords.setText(mAlbumName[position]);
				updateSearchHistory(mAlbumName[position]);


			}
		});
	}
	
	
	/**
	 * 设置当前搜索类型历史记录的Adapter
	 */
	private void setSearchHistory() {
		
		String historyData=locType.equals(music)?musicRecords:vedioRecords;
		if(!StringUtils.hasLength(historyData)){
			noHistoryList.setVisibility(View.VISIBLE);
			myHistory.setVisibility(View.GONE);
			return;	
		}
		noHistoryList.setVisibility(View.GONE);
		myHistory.setVisibility(View.VISIBLE);		
		searchHistoryAdapter  historyAdapter=new searchHistoryAdapter(SearchActivity.this, historyData);
		myHistory.setAdapter(historyAdapter);
	}
	
	
	private void updateSearchHistory(String key){
		String newRecords=null;
		//保存新增的搜索记录
		if(locType.equals(music)){						
			if(musicRecords.contains(key))musicRecords=musicRecords.replace(key+";", "");		
			newRecords=musicRecords=key+";"+musicRecords;
		}else{					
			if(vedioRecords.contains(key))vedioRecords=vedioRecords.replace(key+";", "");		
			newRecords=vedioRecords=key+";"+vedioRecords;
		}		
		 searchHistoryAdapter historyAdapter=(searchHistoryAdapter) myHistory.getAdapter();
		 if(null==historyAdapter){
			    setSearchHistory();
		 }else{
		         historyAdapter.setHistoryList(newRecords);
				 historyAdapter.notifyDataSetChanged();
		 }
	}
	
	
   /**
    * 保存搜索记录到MySharePreferences中
    */
	private void saveSearchHistory() {
		 MySharePreferencesData shareData=new MySharePreferencesData();  
		 shareData.searchHistory=formateHistoryRecords();	
		mSharePreferences.SaveMySharePreferences(shareData);	
		Log.e(TAG, "saveSearchHistory  is  "+shareData.searchHistory);
	}
	
	private String formateHistoryRecords(){
		 String[] musicTokens=musicRecords.split(";");
		 String[] vedioTokens=vedioRecords.split(";");
		 musicRecords="";
		 vedioRecords="";
		 for (int i = 0; i < 3; i++) {
			 if(i<musicTokens.length && StringUtils.hasLength(musicTokens[i]))musicRecords+=musicTokens[i]+";";
			 if(i<vedioTokens.length  && StringUtils.hasLength(vedioTokens[i]))vedioRecords+=vedioTokens[i]+";";
		}	
		return  "music:"+musicRecords+"vedio:"+vedioRecords;	
	}
	
		
	
	private void searchKey(String key) {
		
		if(locType.equals(tv)){
			key="电视剧"+key;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("search:");
		sb.append("|");
		sb.append(locType);
		sb.append(";");
		sb.append(key);
		
		ClientSendCommandService.msg = sb.toString();
		ClientSendCommandService.handler.sendEmptyMessage(1);
	}
	
	private void notifySearchTypeChange(){
		
		 if(locType.equals(music)){				
			      hotSearch.setVisibility(View.VISIBLE);	
				  videoType.setVisibility(View.GONE);
		}else {
			  //设置电影为焦点
		      search_movie.requestFocus();
		      search_movie.setFocusable(true);
		      
			  videoType.setVisibility(View.VISIBLE);
		      hotSearch.setVisibility(View.GONE);	
		    
		}	
		//更新历史记录:
		 setSearchHistory();
	}



	@Override
	protected void onDestroy() {
		//保存历史记录
		saveSearchHistory();
		super.onDestroy();
	}
  

}
