package com.changhong.xiami.activity;

/**
 * 艺人详细信息
 */
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.changhong.xiami.artist.CharacterParser;
import com.changhong.xiami.artist.PinyinComparator;
import com.changhong.xiami.artist.SideBar;
import com.changhong.xiami.artist.SideBar.OnTouchingLetterChangedListener;
import com.changhong.xiami.artist.SongAdapter;
import com.changhong.xiami.artist.SortAdapter;
import com.changhong.xiami.data.XMMusicData;
import com.changhong.xiami.data.XiamiDataModel;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.changhong.yinxiang.utils.Configure;
import com.xiami.sdk.entities.ArtistRegion;
import com.xiami.sdk.entities.OnlineArtist;
import com.xiami.sdk.entities.OnlineSong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtistDetailActivity extends BaseActivity {

	private XMMusicData mXMMusicData;
	private ListView mSongList;
	private TextView singer_name,singer_countLikes;
	private ImageView  singerLogo;
	private SongAdapter adapter;
	private Handler mHandler;
	private final int MAX_PAGE_SIZE = 100;
	long  curArtistID=-1;
	Bitmap  singerImg=null;


	public static final int REFRESH_SINGER = 0x01;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initView() {
		setContentView(R.layout.xiami_singer_infor);

		curArtistID=getIntent().getExtras().getLong("artistID");		
		String name=getIntent().getStringExtra("artistName");
	
		mXMMusicData = XMMusicData.getInstance(this);

		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (Button) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);
		
		
		singerLogo=(ImageView) findViewById(R.id.singer_logo);
		mSongList = (ListView) findViewById(R.id.song_list);
		singer_name=(TextView) findViewById(R.id.singer_name);
		singer_countLikes=(TextView) findViewById(R.id.singer_countlikes);
		singer_name.setText(name);
		
		adapter = new SongAdapter(this);
		mSongList.setAdapter(adapter);

	}

	@Override
	protected void initData() {
		
		super.initData();
		// 长按进入歌手详情
		mSongList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						OnlineArtist artist = (OnlineArtist) parent
								.getItemAtPosition(position);
						Toast.makeText(ArtistDetailActivity.this,
								artist.toString(), Toast.LENGTH_SHORT).show();
						return true;
					}
				});
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case REFRESH_SINGER:
					int countLikes=msg.arg1;
					singer_countLikes.setText(countLikes+"");
					singerLogo.setImageBitmap(singerImg);
					break;
				}
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
				
				OnlineArtist  mOnlineArtist= mXMMusicData.fetchArtistDetailSync(curArtistID);
				String imgUrl=mOnlineArtist.getImageUrl(Configure.IMAGE_SIZE3);
				singerImg=mXMMusicData.getArtistImage(imgUrl);				
				final List<OnlineSong> songs   =   mXMMusicData.fetchSongsByArtistIdSync(curArtistID);
				
				//显示歌手背景
				Message msg=new Message();
				msg.what=REFRESH_SINGER;
				msg.arg1=mOnlineArtist.getCountLikes();
				mHandler.sendMessage(msg);
				
				mSongList.post(new Runnable() {
					@Override
					public void run() {
						adapter.updateListView(songs);
					}
				});
				
				
			}
		}).start();
	}



	
	
	
	/**
	 * 位图资源释放
	 */
	private void  BitmapRecycle(){
		if(singerImg != null && !singerImg.isRecycled()) {
			singerImg.recycle();
			singerImg=null;
		}	
	}
	
		
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BitmapRecycle();

	}

}