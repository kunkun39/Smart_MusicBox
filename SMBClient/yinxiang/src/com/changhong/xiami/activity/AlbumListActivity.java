package com.changhong.xiami.activity;

/**
 * 专辑大全
 */
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.changhong.xiami.artist.CharacterParser;
import com.changhong.xiami.artist.PinyinComparator;
import com.changhong.xiami.artist.SideBar;
import com.changhong.xiami.artist.SideBar.OnTouchingLetterChangedListener;
import com.changhong.xiami.artist.SortAdapter;
import com.changhong.xiami.data.AlbumAdapter;
import com.changhong.xiami.data.XMMusicData;
import com.changhong.xiami.data.XiamiDataModel;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.changhong.yinxiang.utils.Configure;
import com.xiami.sdk.entities.ArtistRegion;
import com.xiami.sdk.entities.OnlineAlbum;
import com.xiami.sdk.entities.OnlineArtist;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumListActivity extends BaseActivity {

	private XMMusicData mXMMusicData;
	private ListView mAlbumList;
	private AlbumAdapter adapter;
	private Handler mHandler;

	private int curPageSize = 0;
	private final int MAX_PAGE_SIZE = 100;

	private List<XiamiDataModel> SourceDataList = null;

	public static final int HIGH_LIGHTED_LETTER = 0x01;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initView() {
		setContentView(R.layout.xiami_singer_list);

		mXMMusicData = XMMusicData.getInstance(this);

		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (Button) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);

		mAlbumList = (ListView) findViewById(R.id.singer_list);
		adapter = new AlbumAdapter(this);
		mAlbumList.setAdapter(adapter);

	}

	@Override
	protected void initData() {
		super.initData();
		// 长按进入歌手详情
		mAlbumList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent,
							View view, int position, long id) {
						
						XiamiDataModel model = (XiamiDataModel) adapter.getItem(position);			
						long artistID=model.getId();
						if(artistID>0){
								Intent intent=new Intent(AlbumListActivity.this, ArtistDetailActivity.class);
								intent.putExtra("artistID", artistID);
								intent.putExtra("artistName", model.getName());		
								startActivity(intent);
						}
					}
				});
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HIGH_LIGHTED_LETTER:
                    String s=(String) msg.obj;
				
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
				final List<OnlineAlbum> results = mXMMusicData	.getWeekHotAlbumsSync(MAX_PAGE_SIZE, 1);
				SourceDataList = filledData(results);					
				mAlbumList.post(new Runnable() {
					@Override
					public void run() {
						if(null !=SourceDataList){
						       adapter.updateListView(SourceDataList);
						}else{
                            Toast.makeText(AlbumListActivity.this,"没有搜索到专辑信息", Toast.LENGTH_SHORT).show();
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
	private List<XiamiDataModel> filledData(List<OnlineAlbum> albums) {

		List<XiamiDataModel> albumList = new ArrayList<XiamiDataModel>();

		int size = albums.size();
		for (int i = 0; i < size; i++) {

			OnlineAlbum album = albums.get(i);
			String name = album.getAlbumName();
			XiamiDataModel albumModel = new XiamiDataModel();
			albumModel.setId(album.getAlbumId());
			albumModel.setName(album.getAlbumName());
			albumModel.setImgUrl(album.getImageUrl(Configure.IMAGE_SIZE2));
			Bitmap image = mXMMusicData.getBitmapFromUrl(albumModel.getImgUrl());
			albumModel.setImage(image);		
			albumList.add(albumModel);
		}
		return albumList;

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

}