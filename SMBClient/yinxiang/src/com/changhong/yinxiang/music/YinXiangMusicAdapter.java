package com.changhong.yinxiang.music;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.serializer.MapSerializer;
import com.baidu.android.common.logging.Log;
import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.YinXiangMusicViewActivity;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

/**
 * Created by Administrator on 15-5-11.
 */
public class YinXiangMusicAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private List<YinXiangMusic> musicsAll;
	private List<YinXiangMusic> musicsAct = new ArrayList<YinXiangMusic>();
	private Map<String, String> checkStateMap;
	
	public static List<YinXiangMusic> selectMusics = new ArrayList<YinXiangMusic>();
	private Context context;
	private String keyStr;
	private String displayName, musicPath;
	private boolean checkSetFlag=false;

	public YinXiangMusicAdapter(Context context, String keyWords) {
		this.context = context;
		this.keyStr = keyWords;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		YinXiangMusicProvider provider = new YinXiangMusicProvider(context);
		musicsAll = provider.getList();
		checkStateMap=new TreeMap<String, String>();
		musicFilter();
		selectMusics.clear();
	}

	public int getCount() {
		return musicsAct.size();
	}

	public Object getItem(int item) {
		return item;
	}

	public long getItemId(int id) {
		return id;
	}

	// 创建View方法
	public View getView(final int position, View convertView, ViewGroup parent) {
		DataWapper wapper = null;
		if (convertView == null) {
			wapper = new DataWapper();

			// 获得view
			convertView = inflater.inflate(R.layout.yinixiang_vedio_list_item,
					null);
			wapper.musicImage = (ImageView) convertView
					.findViewById(R.id.yinxiang_vedio_item_image);
			wapper.musicName = (TextView) convertView
					.findViewById(R.id.yinxiang_vedio_item_name);
			wapper.fullPath = (TextView) convertView
					.findViewById(R.id.yinxiang_vedio_item_path);
			wapper.musicChecked = (CheckBox) convertView
					.findViewById(R.id.yinxiang_vedio_item_checked);
			
			wapper.musicImage.setTag(position);
			// 组装view
			convertView.setTag(wapper);
		} else {
			wapper = (DataWapper) convertView.getTag();
		}

		final YinXiangMusic yinXiangMusic = (YinXiangMusic) musicsAct
				.get(position);

		displayName = yinXiangMusic.getTitle();
		musicPath = yinXiangMusic.getPath();

		wapper.musicName.setText(displayName);
		wapper.fullPath.setText(musicPath);
		String musicImagePath = DiskCacheFileManager
				.isSmallImageExist(musicPath);
		if (!musicImagePath.equals("")) {
			MyApplication.imageLoader.displayImage("file://" + musicImagePath,
					wapper.musicImage, MyApplication.viewOptions);
			wapper.musicImage.setScaleType(ImageView.ScaleType.FIT_XY);
		} else {
			YinXiangSetImageView.getInstance().setContext(context);
			YinXiangSetImageView.getInstance().startExecutor(wapper.musicImage,
					yinXiangMusic);
		}

		final boolean isChecked = selectMusics.contains(yinXiangMusic);
		wapper.musicChecked.setChecked(isChecked);
		
		wapper.musicChecked.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox check=(CheckBox)v;
				if(check.isChecked()){
					if (!selectMusics.contains(yinXiangMusic)) 
						selectMusics.add(yinXiangMusic);
				}else{
					selectMusics.remove(yinXiangMusic);
				}
				YinXiangMusicViewActivity.musicSelectedInfo
				.setText("你共选择了" + selectMusics.size()
						+ "首歌曲");
			}
		});
//		wapper.musicChecked
//				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView,
//							boolean isChecked) {
//						// TODO Auto-generated method stub
//						if (isChecked) {
//							if (!selectMusics.contains(yinXiangMusic)) {
//								selectMusics.add(yinXiangMusic);
//								checkStateMap.put(String.valueOf(position), String.valueOf(1));
//							}
//						} else {
//								selectMusics.remove(yinXiangMusic);
//								checkStateMap.remove(String.valueOf(position));
//						}
//						Log.i("mmmm", "size=" + selectMusics.size());
//						YinXiangMusicViewActivity.musicSelectedInfo
//								.setText("你共选择了" + checkStateMap.size()
//										+ "首歌曲");
//					}
//				});
		return convertView;
	}

	private void musicFilter() {
		for (int i = 0; i < musicsAll.size(); i++) {
			YinXiangMusic music = musicsAll.get(i);
			if (TextUtils.isEmpty(keyStr)) {
				musicsAct = musicsAll;
				break;
			} else if (music.getTitle().contains(keyStr)) {
				musicsAct.add(music);
			}
		}
	}

	private final class DataWapper {

		// 音频的图标
		public ImageView musicImage;

		// 音频的名字
		public TextView musicName;

		// 音频是否被选中
		public CheckBox musicChecked;

		// 音屏的全路径
		public TextView fullPath;

	}
}
