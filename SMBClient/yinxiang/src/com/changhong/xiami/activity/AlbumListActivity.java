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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.changhong.xiami.data.AlbumAdapter;
import com.changhong.xiami.data.XiamiDataModel;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.changhong.yinxiang.utils.Configure;
import com.google.gson.JsonElement;
import com.xiami.music.api.utils.RequestMethods;
import com.xiami.sdk.entities.OnlineAlbum;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AlbumListActivity extends BaseActivity {

	private GridView mAlbumList;
	private AlbumAdapter adapter;
	private Handler mHandler;

	private int curPageSize = 0;
	private final int MAX_PAGE_SIZE = 50;

	private List<XiamiDataModel> SourceDataList = null;

	public static final int HIGH_LIGHTED_LETTER = 0x01;
	  /**
     * 专辑发行日期格式
     */
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

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
		back = (Button) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);

		mAlbumList = (GridView) findViewById(R.id.album_list);
		adapter = new AlbumAdapter(this);
		mAlbumList.setAdapter(adapter);

	}

	@Override
	protected void initData() {
		super.initData();

		// 长按进入歌手详情
		mAlbumList.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent,
							View view, int position, long id) {
						
						XiamiDataModel model = (XiamiDataModel) adapter.getItem(position);			
						long albumID=model.getId();
						if(albumID>0){
								Intent intent=new Intent(AlbumListActivity.this, XiamiMusicListActivity.class);
								intent.putExtra("albumID", albumID);
								startActivity(intent);
						}
					}
				});

		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Configure.XIAMI_RESPOND_SECCESS:
					JsonElement jsonData = (JsonElement) msg.obj;
					handlXiamiResponse(jsonData);
					break;
				case Configure.XIAMI_RESPOND_FAILED:
					int errorCode=msg.arg1;
					Toast.makeText(AlbumListActivity.this, errorCode,	Toast.LENGTH_SHORT).show();
					break;					
				}
			}
		};
	}

	@Override
	protected void onStart() {
		super.onStart();

//		// 获取推荐歌曲
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				final List<OnlineAlbum> results = mXMMusicData.getWeekHotAlbumsSync(MAX_PAGE_SIZE, 1);
//				
//				SourceDataList = filledData(results);					
//				mAlbumList.post(new Runnable() {
//					@Override
//					public void run() {
//						if(null !=SourceDataList){
//						       adapter.updateListView(SourceDataList);
//						}else{
//                            Toast.makeText(AlbumListActivity.this,"没有搜索到专辑信息", Toast.LENGTH_SHORT).show();
//						}
//					}
//				});		
//			}
//		}).start();
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("type", "all");
		params.put("limit", MAX_PAGE_SIZE);
		params.put("page", 1);
		mXMMusicData.getJsonData(mHandler,RequestMethods.METHOD_RANK_NEWALBUM, params);
	}



	/**
	 * 为ListView填充数据
	 * 
	 * @param datda
	 *            数据
	 * @return SortModel列表
	 */
	private List<XiamiDataModel> filledData(List<OnlineAlbum> albums) {

		if(null == albums)return null;
		
		List<XiamiDataModel> albumList = new ArrayList<XiamiDataModel>();

		int size = albums.size();
		for (int i = 0; i < size; i++) {

		    OnlineAlbum album = (OnlineAlbum) albums.get(i);
		    long albumID=album.getAlbumId();
		    String imgUrl=album.getArtistLogo();
			String name = album.getAlbumName();
            Date mIssueTime = new Date(album.getPublishTime()*1000);
			String content=album.getArtistName()+"\n"+album.getAlbumCategory()+" "+mFormat.format(mIssueTime);
		
		   		    
			XiamiDataModel albumModel = new XiamiDataModel();
			albumModel.setId(albumID);
			albumModel.setTitle(name);
			albumModel.setLogoUrl(imgUrl);			
			albumModel.setDescription(content);						
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
			Bitmap bit = SourceDataList.get(i).getLogoImg();
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
	
	private void handlXiamiResponse(JsonElement jsonData) {
		if (jsonData != null) {
			List<OnlineAlbum>OnlineAlbums= mXMMusicData.getAlbumList(jsonData);
			SourceDataList=filledData(OnlineAlbums);
			mAlbumList.post(new Runnable() {
				@Override
				public void run() {
					if (null != SourceDataList) {
						adapter.updateListView(SourceDataList);
					} else {
						Toast.makeText(AlbumListActivity.this, "没有搜索到专辑信息",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		} else {
			Toast.makeText(getApplicationContext(), R.string.error_response,Toast.LENGTH_SHORT).show();
		}

	}
}