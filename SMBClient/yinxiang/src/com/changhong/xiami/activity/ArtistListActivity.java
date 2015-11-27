package com.changhong.xiami.activity;

/**
 * 艺人大全
 */
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.xiami.data.CharacterParser;
import com.changhong.xiami.data.PinyinComparator;
import com.changhong.xiami.data.SideBar;
import com.changhong.xiami.data.SortAdapter;
import com.changhong.xiami.data.XMMusicData;
import com.changhong.xiami.data.XiamiDataModel;
import com.changhong.xiami.data.SideBar.OnTouchingLetterChangedListener;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.changhong.yinxiang.utils.Configure;
import com.google.gson.JsonElement;
import com.xiami.music.api.utils.RequestMethods;
import com.xiami.sdk.entities.ArtistRegion;
import com.xiami.sdk.entities.OnlineAlbum;
import com.xiami.sdk.entities.OnlineArtist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ArtistListActivity extends BaseActivity {

	private XMMusicData mXMMusicData;
	private ListView mArtistList;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private Handler mHandler;
	// 艺人类型
	private RadioGroup radioGroup;
	private int curPageSize = 0;
	private final int MAX_PAGE_SIZE = 50;
	private int curArtistType;
	private int  handleCount;

   String[] artistCategory={"chinese_M","chinese_F","chinese_B","english_M","english_F","english_B","korea_M","korea_F","korea_B"," japanese_M"," japanese_F"," japanese_B"};
   
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<XiamiDataModel> SourceDataList = null;
	private JsonElement hotArtists=null;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	public static final int HIGH_LIGHTED_LETTER = 0x01;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initView() {
		setContentView(R.layout.xiami_singer_list);

		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		mXMMusicData = XMMusicData.getInstance(this);

		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (ImageView) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);

		mArtistList = (ListView) findViewById(R.id.singer_list);
		sideBar = (SideBar) findViewById(R.id.singer_sidebar);
		radioGroup=(RadioGroup) findViewById(R.id.singer_category);
		adapter = new SortAdapter(this);
		mArtistList.setAdapter(adapter);
		SourceDataList = new ArrayList<XiamiDataModel>();


	}

	@Override
	protected void initData() {
		super.initData();
		// 长按进入歌手详情
		mArtistList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent,
							View view, int position, long id) {
						
						XiamiDataModel model = (XiamiDataModel) adapter.getItem(position);			
						long artistID=model.getId();
						if(artistID>0){
								Intent intent=new Intent(ArtistListActivity.this, ArtistDetailActivity.class);
//								intent.putExtra("artistID", artistID);
//								intent.putExtra("artistName", model.getTitle());
								intent.putExtra("XiamiDataModel",(Serializable)model);
								startActivity(intent);
						}
					}
				});
		mArtistList.setOnScrollListener(new OnScrollListener() {

			private int _start_index;
			private int _end_index;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				// 异步加载图片
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					Message msg=new Message();
					msg.what=HIGH_LIGHTED_LETTER;
					msg.obj=adapter.getLetterByPosition(_start_index);
					mHandler.sendMessage(msg);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 设置当前屏幕显示的起始index和结束index
				_start_index = firstVisibleItem;
				_end_index = firstVisibleItem + visibleItemCount;
				if (_end_index >= totalItemCount) {
					_end_index = totalItemCount - 1;
				}

			}
		});

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					mArtistList.setSelection(position);
				}
			}
		});
		
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// 设备变化
				curArtistType= matchArtistTypeIndex(checkedId);
				SourceDataList.clear();
				if(null == hotArtists){
						requestHotArtist();	
				}else{
						handleXiamiResponse(hotArtists,true);
						requestArtistBook();
				}
			}

		});

     ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
		
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case HIGH_LIGHTED_LETTER:
                    String s=(String) msg.obj;
					sideBar.matchChoose(s);
					sideBar.invalidate();
					break;
				case Configure.XIAMI_RECOMMEND_PROMOTIONS_ARTISTS://推荐艺人
					hotArtists = (JsonElement) msg.obj;
					handleXiamiResponse(hotArtists,true);
					requestArtistBook();
					break;							
				case Configure.XIAMI_ARTIST_WORDBOOK:
					JsonElement  jsonData = (JsonElement) msg.obj;
					handleCount--;
					handleXiamiResponse(jsonData,false);
					break;
				case Configure.XIAMI_RESPOND_FAILED:
					int errorCode=msg.arg1;
					Toast.makeText(ArtistListActivity.this, errorCode,	Toast.LENGTH_SHORT).show();
					break;	
				}
			}
		};
		
		
	
	}

	@Override
	protected void onStart() {
		super.onStart();	
		requestHotArtist();		
	}



	/**
	 * 为ListView填充数据
	 * 
	 * @param datda
	 *            数据
	 * @return SortModel列表
	 */
	private void filledData(List<OnlineArtist> artists,boolean hot) {

		 String artistArea="";
		int size = artists.size();
		for (int i = 0; i < size; i++) {

			OnlineArtist artist = artists.get(i);
			artistArea=artist.getArea();
			String singer = artist.getName();
			XiamiDataModel sortModel = new XiamiDataModel();
			sortModel.setId(artist.getId());
			sortModel.setTitle(singer);
			sortModel.setLikeCount(artist.getCountLikes());
			sortModel.setArtistImgUrl(artist.getImageUrl());
			
			if(hot){				
				    sortModel.setSortLetters("!");		
				    Log.e("艺人区域", "区域"+artistArea);
				    if(!artistIsInCurArea(artistArea))continue;
				    
			}else{
					// 汉字转换成拼音
					String pinyin = characterParser.getSelling(singer);
					String sortString = pinyin.substring(0, 1).toUpperCase();
		            
					// 正则表达式，判断首字母是否是英文字母
					if (sortString.matches("[A-Z]")) {
						sortModel.setSortLetters(sortString.toUpperCase());
					} else {
						sortModel.setSortLetters("#");
					}
			}
			SourceDataList.add(sortModel);
		}		
	}
	
	
	private void requestHotArtist() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("limit", 100);
		handleCount=0;
		mXMMusicData.getJsonData(mHandler,RequestMethods.METHOD_RECOMMEND_PROMOTIONS_ARTISTS, params);
	}
	
	
	private void requestArtistBook( ) {
		String[] categorys=artistCategory;
		
		if(2 == curArtistType){//艺人大全
			categorys=new String[]{"chinese_M","chinese_F","chinese_B"};
		}else if(3 == curArtistType){//欧美
			categorys=new String[]{"english_M","english_F","english_B"};
		}else if(4 == curArtistType){//日本
			categorys=new String[]{"japanese_M","japanese_F","japanese_B"};
		}else if(5 == curArtistType){//韩国
			categorys=new String[]{"korea_M","korea_F","korea_B"};
		}else if(6 == curArtistType){//
			categorys=new String[]{"musician"};
		}
		
		int size=categorys.length;
		HashMap[]  paramList=new HashMap[size];
		for (int i = 0; i < size; i++) {		
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("type", categorys[i]);
			params.put("limit", MAX_PAGE_SIZE);
			params.put("page", 1);
			paramList[i]=params;
		}
		handleCount=categorys.length;
		mXMMusicData.getJsonData(mHandler,RequestMethods.METHOD_ARTIST_WORDBOOK, paramList);
	}

	/**
	 * 根据index匹配艺人类型的索引。
	 * 
	 * @param index
	 * @return
	 */
	private int matchArtistTypeIndex(int index) {
		int reValue = 1;
		int childCount = radioGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			RadioButton child = (RadioButton) radioGroup.getChildAt(i);
			if (null != child && index == child.getId()) {
				reValue = i + 1;
				break;
			}
		}
		return reValue;
	}

	
	
	
	/**
	 * 位图资源释放
	 */
	private void  BitmapRecycle(){
		int size=SourceDataList.size();
		for (int i = 0; i < size; i++) {
			Bitmap bit = SourceDataList.get(i).getLogoImg();
			if(bit != null && !bit.isRecycled()) {
			    bit.recycle();
			}
		}
		
	}
	
	
	private void handleXiamiResponse(JsonElement jsonData,boolean hot) {
		if(null ==jsonData)return;
		
		final List<OnlineArtist> results = mXMMusicData	.getArtistList(jsonData);
		filledData(results,hot);		
		if(handleCount>0 || SourceDataList.size()<=0)return;
		
		// 根据a-z进行排序源数据
		Collections.sort(SourceDataList, pinyinComparator);
	
		//高亮显示第一个歌手Letter
		Message msg=new Message();
		msg.what=HIGH_LIGHTED_LETTER;
		msg.obj=SourceDataList.get(0).getSortLetters();
		if("!".equals(msg.obj))msg.obj="热";
		
		mHandler.sendMessage(msg);
		
		mArtistList.post(new Runnable() {
			@Override
			public void run() {
				if(null !=SourceDataList){
				       adapter.updateListView(SourceDataList);
				}else{
                    Toast.makeText(ArtistListActivity.this,"没有搜索到艺人信息", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
  private boolean artistIsInCurArea(String artistArea){
	  
	  if(1==curArtistType){	  
	    	 return true;
	  }else if(2==curArtistType &&( artistArea.contains("大陆") || artistArea.contains("香港") ||  artistArea.contains("台湾"))){
	    	 return true;
	    }else  if(3==curArtistType && (artistArea.contains("english")
	    		|| artistArea.contains("美国") ||  artistArea.contains("欧美") || artistArea.contains("英国"))){
	    	 return true;

	    }else  if(4==curArtistType && (artistArea.contains("korea") || artistArea.contains("韩国"))){
	    	 return true;

	    }else  if(5==curArtistType && (artistArea.contains("japanese") || artistArea.contains("日本"))){
	    	 return true;

	    }else  if(6==curArtistType && artistArea.contains("other")){
	    	 return true;
	    }
	  return false;
  }
	
		
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BitmapRecycle();

	}

}