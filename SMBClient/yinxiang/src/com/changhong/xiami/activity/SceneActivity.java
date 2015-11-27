package com.changhong.xiami.activity;

/**
 * 场景展示
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.utils.NetworkUtils;
import com.changhong.xiami.data.RequestDataTask;
import com.changhong.xiami.data.SceneAdapter;
import com.changhong.xiami.data.SceneInfor;
import com.changhong.xiami.data.XMMusicData;
import com.changhong.xiami.data.XMPlayMusics;
import com.changhong.xiami.data.XiamiApiResponse;
import com.changhong.xiami.data.XiamiDataModel;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.changhong.yinxiang.utils.Configure;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.xiami.music.api.utils.RequestMethods;
import com.xiami.sdk.entities.OnlineCollect;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.SceneSongs;
import com.xiami.sdk.entities.TokenInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SceneActivity extends BaseActivity {

	private GridView mSceneList;
	private SceneAdapter adapter;
	private Handler mHandler;
	private List<XiamiDataModel> SourceDataList = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initView() {
		setContentView(R.layout.xiami_album_list);

		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (ImageView) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);

		mSceneList = (GridView) findViewById(R.id.album_list);
	

		// 修改layout
		mSceneList.setVerticalSpacing(20);
		mSceneList.setHorizontalSpacing(20);
	}

	@Override
	protected void initData() {
		super.initData();
		// 长按进入歌手详情
		mSceneList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				XiamiDataModel model = (XiamiDataModel) adapter
						.getItem(position);
				if (null != model) {
				
					Intent intent = new Intent(SceneActivity.this,	XiamiMusicListActivity.class);
					intent.putExtra("sceneID", model.getId());
					intent.putExtra("sceneName", model.getTitle());
					intent.putExtra("musicType", Configure.XIAMI_SCENE_DETAIL);
					startActivity(intent);
				}
			}
		});

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Configure.XIAMI_SCENE_LIST:
					JsonElement jsonData = (JsonElement) msg.obj;
					handlXiamiResponse(jsonData);
					break;
				case Configure.XIAMI_RESPOND_FAILED:
					int errorCode=msg.arg1;
					Toast.makeText(SceneActivity.this, errorCode,	Toast.LENGTH_SHORT).show();
					break;	
				case Configure.XIAMI_SCENE_DETAIL:
	                //获取专辑歌曲
					jsonData = (JsonElement) msg.obj;
					jsonData = jsonData.getAsJsonObject().get("songs");
					List<OnlineSong> songs=mXMMusicData.getSongList(jsonData);	
					XMPlayMusics.getInstance(SceneActivity.this).playMusics(songs);
					break;
				case Configure.XIAMI_PLAY_MUSICS:
	                //播放音乐列表
					int  sceneID=msg.arg1;
					mXMMusicData.getSceneDetail(this, sceneID);
					break;
				}
			}
		};
		
		adapter = new SceneAdapter(this,mHandler);
		mSceneList.setAdapter(adapter);
	}

	@Override
	protected void onStart() {
		super.onStart();

		HashMap<String, Object> params = new HashMap<String, Object>();
		mXMMusicData.getJsonData(mHandler, "radio.scene", params);		
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param datda
	 *            数据
	 * @return SortModel列表
	 */
	private List<XiamiDataModel> filledData(List<SceneInfor> list) {

		List<XiamiDataModel> dataList = new ArrayList<XiamiDataModel>();

		if (list != null) {

			int size = list.size();
			for (int i = 0; i < size; i++) {
				SceneInfor scene = list.get(i);
				XiamiDataModel sceneModel = new XiamiDataModel();
				sceneModel.setId(scene.getSceneID());
				sceneModel.setTitle(scene.getSceneName());
				sceneModel.setType(scene.getMusicType());
				sceneModel.setLogoUrl(scene.getSceneLogo());
				dataList.add(sceneModel);
			}
		}
		return dataList;
	}

	/**
	 * 位图资源释放
	 */
	private void BitmapRecycle() {
		int size = SourceDataList.size();
		for (int i = 0; i < size; i++) {
			Bitmap bit = SourceDataList.get(i).getLogoImg();
			if (bit != null && !bit.isRecycled()) {
				bit.recycle();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BitmapRecycle();

	}

	private void handlXiamiResponse(JsonElement jsonData) {

		if (jsonData != null) {
			List<SceneInfor> scenes = mXMMusicData.getSceneList(jsonData);
			SourceDataList = filledData(scenes);
			mSceneList.post(new Runnable() {
				@Override
				public void run() {
					if (null != SourceDataList) {
						adapter.updateListView(SourceDataList);
					} else {
						Toast.makeText(SceneActivity.this, "没有搜索到专辑信息",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		} else {
			Toast.makeText(getApplicationContext(), R.string.error_response,
					Toast.LENGTH_SHORT).show();
		}

	}

}
