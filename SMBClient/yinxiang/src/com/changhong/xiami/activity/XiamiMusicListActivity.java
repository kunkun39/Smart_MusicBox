package com.changhong.xiami.activity;

/*
 * 显示对应专辑ID的歌曲列表
 * 传入参数的名字为 albumID
 * BY CYM
 */
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.changhong.common.utils.StringUtils;
import com.changhong.xiami.data.MusicsListAdapter;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.changhong.yinxiang.utils.Configure;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiami.sdk.entities.OnlineSong;

public class XiamiMusicListActivity extends BaseActivity {

	private TextView albumName;
	private ListView musicsList;
	private MusicsListAdapter adapter;
	private List<OnlineSong> songsList;
	private List<OnlineSong> playList = new ArrayList<OnlineSong>();
	private long albumID = 0;
	private final int MUSIC_TYPE_SCENE = 2;
	private int curMusicType = 1;
	private String curTitle;
	private Handler mhandler = new Handler() {
    private 	JsonElement element = null;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {

			case Configure.XIAMI_RESPOND_SECCESS:
				JsonElement jsonData = (JsonElement) msg.obj;
				handlXiamiResponse(jsonData);
				break;
			case Configure.XIAMI_RESPOND_FAILED:
				int errorCode = msg.arg1;
				Toast.makeText(XiamiMusicListActivity.this, errorCode,
						Toast.LENGTH_SHORT).show();
				break;

			case Configure.XIAMI_PLAY_MUSICS: // 更新音乐列表99
				albumName.setText(curTitle);
				adapter.setData(songsList);
				break;
			case Configure.XIAMI_COLLECT_DETAIL:
			case Configure.XIAMI_ALBUM_DETAIL: // 专辑音乐类型1
			case Configure.XIAMI_SCENE_DETAIL: // 场景音乐
			case Configure.XIAMI_ARTIST_HOTSONGS: //艺人热歌榜

				element=(JsonElement) msg.obj;
				getAlbumList(element);
				break;

			case Configure.MUSIC_TYPE_COLLECT: // 精选集音乐类型3
				albumID = getIntent().getIntExtra("list_id", 0);
				// getAlbumList();
				break;

			case Configure.XIAMI_TODAY_RECOMSONGS:// 今日推荐歌曲列表4
				element = (JsonElement) msg.obj;
				JsonObject jsonObj = element.getAsJsonObject();

				element = jsonObj.get("songs");
				curTitle = getString(R.string.recommend_dailylist);
				songsList = mXMMusicData.getSongList(element);
				mhandler.sendEmptyMessage(Configure.XIAMI_PLAY_MUSICS);
				break;
			case Configure.XIAMI_RANK_DETAIL:
				element = (JsonElement) msg.obj;
				showRankMusics(element);
				break;

			case Configure.XIAMI_RANK_ALL:
				element = (JsonElement) msg.obj;
				showRankMusics(element);
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
		back = (ImageView) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);

		albumName = (TextView) findViewById(R.id.ablum_name);
		musicsList = (ListView) findViewById(R.id.musics_list);
		adapter = new MusicsListAdapter(XiamiMusicListActivity.this);
		musicsList.setAdapter(adapter);
	}

	protected void initData() {
		super.initData();
		// 启动activity的时候传进参数名为"musicsAlbum"的专辑。
		curMusicType = getIntent().getIntExtra("musicType", 1);

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		musicsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.playMusics(position);
			}
		});

		if (Configure.XIAMI_TODAY_RECOMSONGS == curMusicType) {
			mXMMusicData.getTodayRecom(mhandler, 20);
		} else if (Configure.XIAMI_RANK_HUAYU == curMusicType) {
			curTitle = getString(R.string.rank_huayu);
			mXMMusicData.getHuayuRank(mhandler);
		} else if (Configure.XIAMI_RANK_ALL == curMusicType) {
			curTitle = getString(R.string.rank_top);
			mXMMusicData.getALLRank(mhandler);
		} else if (Configure.XIAMI_RANK_LIST == curMusicType) {
			Intent intent = getIntent();
			curTitle = intent.getStringExtra("rankTitle");
			String rankType = intent.getStringExtra("rankType");
			mXMMusicData.getTheRank(mhandler, rankType);
		}else if (Configure.XIAMI_ALBUM_DETAIL == curMusicType) {
			albumID=getIntent().getLongExtra("albumID", 0);
			curTitle=getIntent().getStringExtra("albumName");
			if(0==albumID){
				return;
			}
			mXMMusicData.getTheAlbum(mhandler, albumID);
		}else if (Configure.XIAMI_COLLECT_DETAIL == curMusicType) {
			albumID=getIntent().getLongExtra("listID", 0);
			curTitle=getIntent().getStringExtra("listName");
			if(0==albumID){
				return;
			}
			mXMMusicData.getCollectDetail(mhandler, albumID);
		}else if (Configure.XIAMI_ARTIST_HOTSONGS == curMusicType) {
			albumID=getIntent().getLongExtra("listID", 0);
			curTitle=getIntent().getStringExtra("listName");
			if(0==albumID){
				return;
			}
			mXMMusicData.getArtistHotSongs(mhandler, albumID);
		}else if (Configure.XIAMI_SCENE_DETAIL == curMusicType) {
			albumID=getIntent().getLongExtra("sceneID", 0);
			curTitle=getIntent().getStringExtra("sceneName");
			if(0==albumID){
				return;
			}
			mXMMusicData.getSceneDetail(mhandler, albumID);
		}else {
			mhandler.sendEmptyMessage(curMusicType);
		}
		
		//设置标题
		if(StringUtils.hasLength(curTitle)){				
			albumName.setText(curTitle);
		}
	}

	private void getAlbumList(JsonElement element) {
		songsList=mXMMusicData.getTheAlbumSongs(element);	
		mhandler.sendEmptyMessage(Configure.XIAMI_PLAY_MUSICS);
		}

	private void showRankMusics(JsonElement element) {

		songsList = mXMMusicData.getRankSongList(element);
		mhandler.sendEmptyMessage(Configure.XIAMI_PLAY_MUSICS);
	}

	private void handlXiamiResponse(JsonElement jsonData) {

		if (jsonData == null)
			return;

		if (curMusicType == MUSIC_TYPE_SCENE) {
			JsonObject songObj = jsonData.getAsJsonObject();
			jsonData = songObj.get("songs");
		}

		songsList = mXMMusicData.getSongList(jsonData);
		musicsList.post(new Runnable() {
			@Override
			public void run() {
				if (null != songsList) {
					mhandler.sendEmptyMessage(Configure.XIAMI_PLAY_MUSICS);
				} else {
					Toast.makeText(XiamiMusicListActivity.this, "没有搜索到专辑信息",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
