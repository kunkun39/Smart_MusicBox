package com.changhong.xiami.activity;

/**
 * 精选集
 */
import android.content.Context;
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
import com.changhong.xiami.data.CollectAdapter;
import com.changhong.xiami.data.JsonUtil;
import com.changhong.xiami.data.RequestDataTask;
import com.changhong.xiami.data.SceneInfor;
import com.changhong.xiami.data.XMMusicData;
import com.changhong.xiami.data.XiamiDataModel;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.BaseActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiami.music.api.utils.RequestMethods;
import com.xiami.sdk.entities.OnlineCollect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CollectActivity extends BaseActivity {

	private XMMusicData mXMMusicData;
	private JsonUtil mJsonUtil;
	private GridView mCollectList;
	private CollectAdapter adapter;
	private Handler mHandler;
	private int curPageSize = 0;
	private final int MAX_PAGE_SIZE = 20;

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

		mCollectList = (GridView) findViewById(R.id.album_list);
		adapter = new CollectAdapter(this);
		mCollectList.setAdapter(adapter);
		mCollectList.setNumColumns(1);

	}

	@Override
	protected void initData() {
		super.initData();

		SourceDataList = new ArrayList<XiamiDataModel>();
		mJsonUtil=JsonUtil.getInstance();
		// 长按进入歌手详情
		mCollectList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				XiamiDataModel model = (XiamiDataModel) adapter
						.getItem(position);
				if (null != model) {
					int list_id =(int) model.getId();
					Intent intent = new Intent(CollectActivity.this,XiamiMusicListActivity.class);
					intent.putExtra("list_id", list_id);
					intent.putExtra("musicType", 3);
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
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		//
		// final List<OnlineCollect> results =
		// mXMMusicData.getNewCollect(MAX_PAGE_SIZE,1);
		//
		// SourceDataList = filledData(results);
		// mCollectList.post(new Runnable() {
		// @Override
		// public void run() {
		// if(null !=SourceDataList){
		// adapter.updateListView(SourceDataList);
		// }else{
		// Toast.makeText(CollectActivity.this,"没有搜索到专辑信息",
		// Toast.LENGTH_SHORT).show();
		// }
		// }
		// });
		// }
		// }).start();

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("limit", MAX_PAGE_SIZE);
		params.put("page", 1);
		FindCollectTask findCollectByIdTask = new FindCollectTask(
				getApplicationContext());
		findCollectByIdTask.execute(params);

	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param data
	 *            数据
	 * @return SortModel列表
	 */
//	private List<XiamiDataModel> filledData(JsonElement jsonData) {
//
//		List<XiamiDataModel> dataList = new ArrayList<XiamiDataModel>();
//		
//		if (jsonData != null) {		
//			JsonObject obj = jsonData.getAsJsonObject();
//			String total = obj.get("total").getAsString();
//			String more = obj.get("more").getAsString();
//			jsonData = obj.get("collects");
//
//			if (jsonData.isJsonArray()) {
//
//				JsonArray array = jsonData.getAsJsonArray();
//				int size = array.size();
//				for (int i = 0; i < size; i++) {
//					JsonObject itemObj = array.get(i).getAsJsonObject();
//
//					String collect_logo = itemObj.get("collect_logo").getAsString();
//					String author_avatar = itemObj.get("author_avatar")	.getAsString();
//
//					XiamiDataModel model = new XiamiDataModel();
//
//					Bitmap image1 = mXMMusicData.readAPIC(collect_logo);
//					Bitmap image2 = mXMMusicData.readAPIC(author_avatar);
//
//					XiamiDataModel collectModel = new XiamiDataModel();
//					collectModel.setId(itemObj.get("list_id").getAsInt());
//					collectModel.setTitle(itemObj.get("collect_name").getAsString());
//					collectModel.setLogoUrl(collect_logo);
//					collectModel.setLogoImg(image1);
//					collectModel.setArtist(itemObj.get("user_name").getAsString());
//					collectModel.setArtistImgUrl(author_avatar);
//					collectModel.setArtistImg(image2);
//					collectModel.setDescription(itemObj.get("description").getAsString());
//					collectModel.setLikeCount(itemObj.get("play_count").getAsInt());
//					dataList.add(collectModel);
//				}
//			}
//		}
//		return dataList;
//	}
	
	private List<XiamiDataModel> filledData(List<OnlineCollect> list) {

		List<XiamiDataModel> dataList = new ArrayList<XiamiDataModel>();
		
		if (list != null) {		
			
				int size = list.size();
				for (int i = 0; i < size; i++) {
					OnlineCollect collect = list.get(i);
					XiamiDataModel collectModel = new XiamiDataModel();
					collectModel.setId(collect.getListId());
					collectModel.setTitle(collect.getCollectName());
					collectModel.setLogoUrl(collect.getCollectLogo());
					collectModel.setArtist(collect.getUserName());
					collectModel.setArtistImgUrl(collect.getAuthorAvatar());
					collectModel.setDescription(collect.getDescription());
					collectModel.setLikeCount(collect.getPlayCount());
					dataList.add(collectModel);
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
			Bitmap logo = SourceDataList.get(i).getLogoImg();
			Bitmap artist = SourceDataList.get(i).getArtistImg();

			if (logo != null && !logo.isRecycled()) {
				logo.recycle();
			}

			if (artist != null && !artist.isRecycled()) {
				artist.recycle();
			}
		}
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		BitmapRecycle();

	}

	class FindCollectTask extends RequestDataTask {

		public FindCollectTask(Context context) {
			super(mXMMusicData, context,	RequestMethods.METHOD_COLLECT_RECOMMEND,
					RequestMethods.METHOD_COLLECT_DETAIL);
		}

		@Override
		public void postInBackground(JsonElement response) {
		}

		@Override
		protected void onPostExecute(JsonElement jsonData) {
			super.onPostExecute(jsonData);
			List<OnlineCollect>onlineCollects= mJsonUtil.getCollectRecommend(jsonData);
			SourceDataList=filledData(onlineCollects);
			mCollectList.post(new Runnable() {
				@Override
				public void run() {
					if (null != SourceDataList) {
						adapter.updateListView(SourceDataList);
					} else {
						Toast.makeText(CollectActivity.this, "没有搜索到专辑信息",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}
}
