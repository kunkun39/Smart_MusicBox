package com.changhong.xiami.activity;

/**
 * 专辑大全
 */
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.utils.NetworkUtils;
import com.changhong.xiami.artist.CharacterParser;
import com.changhong.xiami.artist.PinyinComparator;
import com.changhong.xiami.artist.SideBar;
import com.changhong.xiami.artist.SideBar.OnTouchingLetterChangedListener;
import com.changhong.xiami.artist.SortAdapter;
import com.changhong.xiami.data.AlbumAdapter;
import com.changhong.xiami.data.SceneAdapter;
import com.changhong.xiami.data.SceneInfor;
import com.changhong.xiami.data.XMMusicData;
import com.changhong.xiami.data.XiamiDataModel;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.changhong.yinxiang.utils.Configure;
import com.xiami.sdk.entities.ArtistRegion;
import com.xiami.sdk.entities.OnlineAlbum;
import com.xiami.sdk.entities.OnlineArtist;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.SceneSongs;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SceneActivity extends BaseActivity {

	private XMMusicData mXMMusicData;
	private GridView mSceneList;
	private SceneAdapter adapter;
	private Handler mHandler;

	private int curPageSize = 0;
	private final int MAX_PAGE_SIZE = 100;

	private List<XiamiDataModel> SourceDataList = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initView() {
		setContentView(R.layout.xiami_album_list);

		mXMMusicData = XMMusicData.getInstance(this);

		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (Button) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);

		mSceneList = (GridView) findViewById(R.id.album_list);
		adapter = new SceneAdapter(this);
		mSceneList.setAdapter(adapter);

	}

	@Override
	protected void initData() {
		super.initData();
		// 长按进入歌手详情
		mSceneList.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent,
							View view, int position, long id) {
						
						XiamiDataModel model = (XiamiDataModel) adapter.getItem(position);	
						SceneSongs  scene=(SceneSongs) model.getOtherObj();
						if(null !=scene){
							    SceneInfor mSceneInfor=new SceneInfor();
							    mSceneInfor.setSceneName(scene.getTitle());
							    mSceneInfor.setSongsList(scene.getSongs());
								Intent intent=new Intent(SceneActivity.this, XiamiMusicListActivity.class);
								intent.putExtra("sceneInfor", mSceneInfor);
								intent.putExtra("musicType", 2);
								startActivity(intent);
						}
					}
				});

		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				
			}
		};
	}

	@Override
	protected void onStart() {
		super.onStart();

		// 获取推荐歌曲
		new Thread(new Runnable() {
			@Override
			public void run() {
				WifiInfo curWifiInfor=NetworkUtils.getCurWifiInfor(SceneActivity.this);			
				String gps=NetworkUtils.getGPSInfor(SceneActivity.this);
				final List<SceneSongs> results = mXMMusicData.getRecommendSceneSongs(gps,curWifiInfor.getSSID(),curWifiInfor.getBSSID());
				
				SourceDataList = filledData(results);					
				mSceneList.post(new Runnable() {
					@Override
					public void run() {
						if(null !=SourceDataList){
						       adapter.updateListView(SourceDataList);
						}else{
                            Toast.makeText(SceneActivity.this,"没有搜索到专辑信息", Toast.LENGTH_SHORT).show();
						}
					}
				});		
			}
		}).start();
	}



	/**
	 * 为ListView填充数据
	 * 
	 * @param datda
	 *            数据
	 * @return SortModel列表
	 */
	private List<XiamiDataModel> filledData(List<SceneSongs> scenes) {

		if(null == scenes)return null;
		
		List<XiamiDataModel> sceneList = new ArrayList<XiamiDataModel>();

		int size = scenes.size();
		for (int i = 0; i < size; i++) {

			SceneSongs scene = (SceneSongs) scenes.get(i);
		    String imgUrl=scene.getIcon();
			String name = scene.getTitle();
			XiamiDataModel sceneModel = new XiamiDataModel();
			sceneModel.setName(name);
			sceneModel.setImgUrl(imgUrl);			
			Bitmap image = mXMMusicData.getBitmapFromUrl(imgUrl);
			sceneModel.setImage(image);
			sceneModel.setOtherObj(scene);	
			
			sceneList.add(sceneModel);
		}
		return sceneList;

	}
	
	
	
	
	/**
	 * 位图资源释放
	 */
	private void  BitmapRecycle(){
		int size=SourceDataList.size();
		for (int i = 0; i < size; i++) {
			Bitmap bit = SourceDataList.get(i).getImage();
			if(bit != null && !bit.isRecycled()) {
			    bit.recycle();
			}
		}		
	}
	
		
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BitmapRecycle();

	}
	
	
	class MyOnlineAlbum extends OnlineAlbum{
		
		public  String getArtistLogo(){			
			     return getArtistLogo();
		}
		
		public  String getAlbumCategory(){			
		     return this.getAlbumCategory();
	}
		
	}

}