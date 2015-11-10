package com.changhong.xiami.activity;

/*
 * 显示对应专辑ID的歌曲列表
 * 传入参数的名字为 albumID
 * BY CYM
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.common.logging.Log;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.xiami.activity.CollectActivity.FindCollectTask;
import com.changhong.xiami.data.JsonUtil;
import com.changhong.xiami.data.MusicsListAdapter;
import com.changhong.xiami.data.RequestDataTask;
import com.changhong.xiami.data.SceneInfor;
import com.changhong.xiami.data.XMMusicData;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiami.music.api.utils.RequestMethods;
import com.xiami.sdk.entities.LanguageType;
import com.xiami.sdk.entities.OnlineAlbum;
import com.xiami.sdk.entities.OnlineCollect;
import com.xiami.sdk.entities.OnlineSong;

public class XiamiMusicListActivity extends BaseActivity {

	private  XMMusicData mXMMusicData;
	private JsonUtil mJsonUtil;
	private TextView albumName;
	private ListView musicsList;
	private MusicsListAdapter adapter;
	private List<OnlineSong> songsList;
	private OnlineAlbum album;
	private long albumID=0;	
	private int albumIndex = 0;
    private final int MUSIC_TYPE_ALBUM=1;
    private final int MUSIC_TYPE_SCENE=2;
    private final int MUSIC_TYPE_COLLECT=3;

    private final int MUSIC_LIST_UPDATE=6;

    private int curMusicType=1;
    private  String curTitle;
	private Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MUSIC_LIST_UPDATE:  //更新音乐列表
				albumName.setText(curTitle);
				adapter.setData(songsList);
				break;
			case MUSIC_TYPE_ALBUM: //专辑音乐类型
				albumID=getIntent().getIntExtra("albumID", 0);
				getAlbumList();			
				break;
				
		case MUSIC_TYPE_COLLECT: //精选集音乐类型
				albumID=getIntent().getIntExtra("list_id", 0);
				getAlbumList();			
				break;	
				
			case MUSIC_TYPE_SCENE: //场景音乐
				SceneInfor sceneInfor=(SceneInfor) getIntent().getSerializableExtra("sceneInfor");
				albumID=sceneInfor.getSceneID();
				curTitle=sceneInfor.getSceneName();
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("id", 1);
				params.put("type", 1);
				params.put("limit", 20);
				params.put("page", 1);
				FindSongTask findSongByIdTask = new FindSongTask(getApplicationContext(),"tag.song	");
				findSongByIdTask.execute(params);
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
	}

	protected void initView() {
		setContentView(R.layout.xiami_music_list);
		
		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (Button) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);
		
		
		albumName = (TextView) findViewById(R.id.ablum_name);
		musicsList = (ListView) findViewById(R.id.musics_list);
		adapter = new MusicsListAdapter(XiamiMusicListActivity.this);
		musicsList.setAdapter(adapter);
	}

	protected void initData() {
		super.initData();
		
		mXMMusicData=XMMusicData.getInstance(this);
		mJsonUtil=JsonUtil.getInstance();
		
		// 启动activity的时候传进参数名为"musicsAlbum"的专辑。		
		curMusicType=getIntent().getIntExtra("musicType", 1);	
       
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});			
	   mhandler.sendEmptyMessage(curMusicType);
	}

	private void getAlbumList() {
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				//测试代码
				if(curMusicType == MUSIC_TYPE_ALBUM){
				ArrayList<OnlineAlbum> albumList = (ArrayList<OnlineAlbum>) mXMMusicData.getNewAlbums(
						LanguageType.huayu, 10, 1);
				album = albumList.get(albumIndex);
				albumID=album.getAlbumId();
				//根据ID获取专辑相信信息，带歌曲列表
				album = XMMusicData.getInstance(XiamiMusicListActivity.this)	.getDetailAlbum(albumID);
				songsList=album.getSongs();
				curTitle=album.getAlbumName();
				
				}else if(curMusicType == MUSIC_TYPE_SCENE){
					OnlineCollect mOnlineCollect= mXMMusicData.getCollectDetailSync((int)albumID);
					songsList=mOnlineCollect.getSongs();
					curTitle=mOnlineCollect.getCollectName();
				}else if(curMusicType == MUSIC_TYPE_COLLECT){
					OnlineCollect mOnlineCollect= mXMMusicData.getCollectDetailSync((int)albumID);
					songsList=mOnlineCollect.getSongs();
					curTitle=mOnlineCollect.getCollectName();
				}
				//根据ID获取专辑相信信息，带歌曲列表				
				mhandler.sendEmptyMessage(MUSIC_LIST_UPDATE);

			}
		}).start();

		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	
	
	class FindSongTask extends RequestDataTask {


		public FindSongTask(Context context, String method) {
			super(mXMMusicData, context,	method,method);
		}

		@Override
		public void postInBackground(JsonElement response) {
		}

		@Override
		protected void onPostExecute(JsonElement jsonData) {
			super.onPostExecute(jsonData);
			
			if(null ==jsonData)return;
			
			if(curMusicType == MUSIC_TYPE_SCENE){
		        	JsonObject songObj = jsonData.getAsJsonObject();
		        	jsonData=songObj.get("songs");
			}

			songsList= mJsonUtil.getSongList(jsonData);
			musicsList.post(new Runnable() {
				@Override
				public void run() {
					if (null != songsList) {
						mhandler.sendEmptyMessage(MUSIC_LIST_UPDATE);
					} else {
						Toast.makeText(XiamiMusicListActivity.this, "没有搜索到专辑信息",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}

}
