package com.changhong.tvserver.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.changhong.tvserver.R;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.search.aidl.KeyWords;
import com.search.aidl.KeyWordsUtil;
import com.search.aidl.VideoInfo;
import com.search.aidl.VideoInfoDataServer;
public class MallListActivity extends FragmentActivity{

	public static final String TAG = "movie&tv";
	
	/**
	 * DataObserver of Mall's Video
	 */
	DataObserver mDataObserver = new DataObserver();	

	/**
	 * GridVies's 
	 */
	GridView mVideoView;	
	GridAdapter mAdapter;
	
	/**
	 *  Video List Storage
	 */
	List<VideoInfo> mVideoInfos = new ArrayList<VideoInfo>();
	
	/**
	 *  Message handler
	 */
	public static Handler handler = null;
	
	public static Handler msgHandler = null;

	
	/**
	 *  Complex Search Dialog
	 */
	View dialogView = null;
	
	/**
	 * Search EditBox
	 */
	TextView mNameView = null;
	
/** ======================================================================================================
 *  Option
 */	
	DisplayImageOptions options = new DisplayImageOptions
			.Builder()
			.showImageForEmptyUri(R.drawable.activity_empty_photo)
			.showImageOnFail(R.drawable.activity_empty_photo)
			.showImageOnLoading(R.drawable.activity_empty_photo)
			.cacheInMemory(false)
			.cacheOnDisc(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .resetViewBeforeLoading(true)            
			.build();

/** ======================================================================================================
 *  Public Function
 */	
	public void playVideo(VideoInfo video)
	{
		Intent intent = new Intent();		
		intent.setClassName("com.changhong.tvmall", "com.changhong.ivideo.activity.DetailActivity");
		intent.putExtra("POSTER_TAG", video.getVideoId());
		intent.putExtra("POSTER_CODE_TAG", video.getPrivatecode());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
	}
	
	public void requiestSearch(String searchText)
	{
		Intent intent = new Intent();
		intent.setAction("com.changhong.searchservice.searchvideo");
		intent.putExtra("keytext", searchText);		
		intent.putExtra("serviceaction", "com.search.aidl.VoiceSearchService");	
		//intent.putExtra("keyWords", value)
		this.sendBroadcast(intent);		
		Log.e("MallListActivity", "+++++++++++++++++++++start to requiestSearch++++++++++++++++++++++++ ");
		
	}
	
	public void requiestSearch(KeyWords keyWords)
	{		
		Intent intent = new Intent();
		intent.setAction("com.changhong.searchservice.searchvideo");
		intent.putExtra("serviceaction", "com.search.aidl.VoiceSearchService");	
		intent.putExtra("keyWords", keyWords);
		this.sendBroadcast(intent);		
	}
/** ======================================================================================================
 *  Override
 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_mall_list);
		initView();
		initEvent();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		paserIntent(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDataObserver.exit();	
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:{
			
			if (dialogView == null) {
				dialogView = LayoutInflater.from(this).inflate(R.layout.mall_search_view, null);
			}	
			else {
				ViewGroup viewGroup = (ViewGroup) dialogView.getParent();
				if (viewGroup != null) {
					viewGroup.removeView(dialogView);
				}
			}			

			Spinner areaSpinner = (Spinner)dialogView.findViewById(R.id.area_content);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, KeyWordsUtil.getAreaList());
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			areaSpinner.setAdapter(adapter);
			
			Spinner kindSpinner = (Spinner)dialogView.findViewById(R.id.kind_content);
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, KeyWordsUtil.getMainKindList());
			kindSpinner.setAdapter(adapter);
			
			Spinner categorySpinner = (Spinner)dialogView.findViewById(R.id.category_content);
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, KeyWordsUtil.getCategoryList());
			categorySpinner.setAdapter(adapter);
			
			
			final AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setView(dialogView, 0, 0, 0, 0);
			
			Button confirmBtn = (Button)dialogView.findViewById(R.id.mall_search_comfirm);
			confirmBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final KeyWords keyWords = new KeyWords();		
					
					EditText personContent = (EditText)dialogView.findViewById(R.id.persion_content);
					EditText nameContent = (EditText)dialogView.findViewById(R.id.name_content);
					Spinner areaSpinner = (Spinner)dialogView.findViewById(R.id.area_content);
					Spinner categorySpinner = (Spinner)dialogView.findViewById(R.id.category_content);
					Spinner kindSpinner = (Spinner)dialogView.findViewById(R.id.kind_content);
					EditText yearSpinner = (EditText)dialogView.findViewById(R.id.year_content);
					keyWords.setName(String.valueOf(nameContent.getText()));
					keyWords.setPerson(String.valueOf(personContent.getText()));
					
					if (areaSpinner.getSelectedItemPosition() != 0) {
						keyWords.setArea(KeyWordsUtil.getAreaList().get(areaSpinner.getSelectedItemPosition()));
					}
					
					if (kindSpinner.getSelectedItemPosition() != 0) {
						keyWords.setName(keyWords.getName() + (KeyWordsUtil.getMainKindList().get(kindSpinner.getSelectedItemPosition())));
					}
					
					if (categorySpinner.getSelectedItemPosition() != 0) {
						keyWords.setCategory(KeyWordsUtil.getCategoryList().get(categorySpinner.getSelectedItemPosition()));
					}
					
					keyWords.setYear(String.valueOf(yearSpinner.getText()));
					requiestSearch(keyWords);
					dialog.dismiss();		
					
				}
			});					
			dialog.show();
			dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		}return true;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
/** ======================================================================================================
 *  Private Functions
 */	
	void initView()
	{
		mNameView = (TextView)findViewById(R.id.mall_list_name);		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);				
		mVideoView = (GridView)findViewById(R.id.mall_list_gridview);	
		mAdapter = new GridAdapter();		
		mVideoView.setAdapter(mAdapter);			
		paserIntent(getIntent());	
		handler = new Handler(getMainLooper());
		
		handler= new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 1:
					String command=(String) msg.obj;
					if(command != null && !command.isEmpty())	{
					    	mVideoInfos.clear();
						   mAdapter.notifyDataSetChanged();
							requiestSearch(command);
							mNameView.setText(command);
					}
					break;
				}

			}
		};
	}
	
	void initEvent()	{
		// search keywords
		mNameView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_ENTER:
					if (mNameView.getText() == null) {
						break;
					}					
					String command = String.valueOf(mNameView.getText());
					if (command.length() > 0) {
						requiestSearch(command);
					}
					break;

				default:
					break;
				}
				return false;
			}
		});
		
		// 
		mVideoView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {				
				playVideo((VideoInfo) mVideoView.getAdapter().getItem(index));
			}
		});
		
		// List
		ListView listView = (ListView)findViewById(R.id.mall_category_list);		
		listView.setAdapter(new ArrayAdapter<String>(this, R.layout.mall_list_item, KeyWordsUtil.getCategoryList()));
		listView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				KeyWords words = new KeyWords();
				String name = String.valueOf(((TextView)findViewById(R.id.mall_list_name)).getText());
				if (name.length() > 0) {
					words.setName(name);
				}				
				words.setCategory(KeyWordsUtil.getCategoryList().get(position));
				requiestSearch(words);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {;}
		});
		
		// Search button
		ImageButton  searchButton = (ImageButton)findViewById(R.id.mall_list_name_search);
		searchButton.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				String name = String.valueOf(((TextView)findViewById(R.id.mall_list_name)).getText());
				if (name.length() > 0) {
					requiestSearch(name);
				}
			}
		});
	}
	
	void paserIntent(Intent intent)
	{
		if (intent != null) {
			String command = intent.getStringExtra("command");
			if(command != null && !command.isEmpty())	{
				command = command.substring(TAG.length() + 1);
				if (!command.isEmpty()) {
					requiestSearch(command);
					mNameView.setText(command);
				}
			}
		}
	}
	

	
	
/** ======================================================================================================
 * 	
 * DataObserver
 *
 */
	class DataObserver implements Observer
	{
		DataObserver()
		{
			VideoInfoDataServer.getInstance().addObserver(this);
		}
		
		void exit()
		{
			VideoInfoDataServer.getInstance().deleteObserver(this);
		}
		
		@Override
		public void update(Observable observable, final Object data) {
			if (handler != null) {
				handler.post(new Runnable() {					
					@Override
					public void run() {
						mVideoInfos.clear();
						mVideoInfos.addAll((Collection<? extends VideoInfo>) data);
						mAdapter.notifyDataSetChanged();
					}
				});
			}
		}
	}
	
/** ======================================================================================================
 * 	
 * GridAdapter
 *
 */		
	class GridAdapter extends BaseAdapter
	{
		@Override
		public int getCount() {
			return mVideoInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return mVideoInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final VideoInfo videoInfo =  mVideoInfos.get(position);
			ImageView imageView = null;
			TextView nameView = null;	
			
			if ((position % 5) == 0) {
				for(int i = 1; i < 11 ; i ++)
				{					
					if (position + i < mVideoInfos.size()) {
						ImageLoader.getInstance().loadImage(mVideoInfos.get(position + i).getmImageUrl(),options, null);
					}					
				}
			}
			
			if (convertView == null) {
				
				convertView = LayoutInflater.from(MallListActivity.this).inflate(R.layout.mall_gridlist_item, null);
				imageView = (ImageView)convertView.findViewById(R.id.mall_list_item_image);
				nameView = (TextView)convertView.findViewById(R.id.mall_list_item_name);				
				
				PageDataHoder pageDataHoder = new PageDataHoder();
				pageDataHoder.imageView = imageView;
				pageDataHoder.nameView = nameView;
				
				convertView.setTag(pageDataHoder);	
			}else{
				PageDataHoder pageDataHoder = (PageDataHoder)convertView.getTag();
				imageView = pageDataHoder.imageView;
				nameView = pageDataHoder.nameView;
			}			
			ImageLoader.getInstance().displayImage(videoInfo.getmImageUrl(), imageView,options);
			convertView.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (v == null) {
						return ;
					}
					
					ImageView view = (ImageView)v.findViewById(R.id.mall_list_item_image);
					if (hasFocus) {
						view.setBackgroundResource(R.drawable.mall_search_foucsed);
					}
					else {
						view.setBackgroundResource(R.drawable.mall_search_nofoucsed);
					}					
				}
			});
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					playVideo(videoInfo);
				}
			});
			Log.d("DATALIST:", "videoType:" + videoInfo.getVideoType() + ",ACTION:" + videoInfo.getAction());
			nameView.setText(videoInfo.getVideoName());		
			return convertView;
		}			
	}
	
	class PageDataHoder
	{
		ImageView imageView;
		TextView nameView;
		TextView typeView;		
	}
}
