package com.changhong.xiami.activity;

/**
 * 艺人大全
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
import com.changhong.xiami.data.XMMusicData;
import com.changhong.xiami.data.XiamiDataModel;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.changhong.yinxiang.utils.Configure;
import com.xiami.sdk.entities.ArtistRegion;
import com.xiami.sdk.entities.OnlineArtist;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtistListActivity extends BaseActivity {

	private XMMusicData mXMMusicData;
	private ListView mArtistList;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private Handler mHandler;

	private int curPageSize = 0;
	private final int MAX_PAGE_SIZE = 100;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<XiamiDataModel> SourceDataList = null;

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
		back = (Button) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);

		mArtistList = (ListView) findViewById(R.id.singer_list);
		sideBar = (SideBar) findViewById(R.id.singer_sidebar);
		adapter = new SortAdapter(this);
		mArtistList.setAdapter(adapter);

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
								intent.putExtra("artistID", artistID);
								intent.putExtra("artistName", model.getName());		
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
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HIGH_LIGHTED_LETTER:
                    String s=(String) msg.obj;
					sideBar.matchChoose(s);
					sideBar.invalidate();
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
				final List<OnlineArtist> results = mXMMusicData	.fetchArtistListSync(ArtistRegion.chinese_M, MAX_PAGE_SIZE, 1);
				SourceDataList = filledData(results);
			
				// 根据a-z进行排序源数据
				Collections.sort(SourceDataList, pinyinComparator);
			
				//高亮显示第一个歌手Letter
				Message msg=new Message();
				msg.what=HIGH_LIGHTED_LETTER;
				msg.obj=SourceDataList.get(0).getSortLetters();
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
		}).start();
	}



	/**
	 * 为ListView填充数据
	 * 
	 * @param datda
	 *            数据
	 * @return SortModel列表
	 */
	private List<XiamiDataModel> filledData(List<OnlineArtist> artists) {

		List<XiamiDataModel> sortList = new ArrayList<XiamiDataModel>();

		int size = artists.size();
		for (int i = 0; i < size; i++) {

			OnlineArtist artist = artists.get(i);
			String singer = artist.getName();
			XiamiDataModel sortModel = new XiamiDataModel();
			sortModel.setId(artist.getId());
			sortModel.setName(singer);
			sortModel.setImgUrl(artist.getImageUrl(Configure.IMAGE_SIZE1));
			Bitmap image = mXMMusicData.getBitmapFromUrl(sortModel.getImgUrl());
			sortModel.setImage(image);
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(singer);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}
			sortList.add(sortModel);
		}
		return sortList;

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