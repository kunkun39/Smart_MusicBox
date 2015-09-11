package com.changhong.yinxiang.music;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;
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
	private boolean checkSetFlag = false;

	/**
	 * YD add 20150806 for fileEdit 音频文件编辑功能
	 */
       Handler mHandler=null;

	public YinXiangMusicAdapter(Context context, Handler handler,String keyWords, String jsonStr) {
		this.context = context;
		this.keyStr = keyWords;
		this.mHandler=handler;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		createAdapterData(jsonStr);
		checkStateMap = new TreeMap<String, String>();
		musicFilter();
		selectMusics.clear();
		
	}
	
	
	
	
	public void createAdapterData(String jsonStr){
		if(null != jsonStr){
			musicsAll = pareJsonToMusicList(jsonStr);
		}else {
			YinXiangMusicProvider provider = new YinXiangMusicProvider(context);
			musicsAll = provider.getList();
		}
	}
	
	
	public void changeAdapterData(String type, YinXiangMusic music ){
		
		if(null == music)return;
		int index=getMusicIndex(music);			
		if(-1 != index  && type.equals(MusicUtils.EDIT_REMOVE)){
			musicsAll.remove(index);
		}else if(-1 != index  && type.equals(MusicUtils.EDIT_RENAME)){
//			YinXiangMusic tempMusic=musicsAll.get(index);
//			tempMusic.setTitle(music.getTitle());
			musicsAll.set(index, music);
		}
		musicFilter();
	}
	
	
	
	private int getMusicIndex(YinXiangMusic music){
		int musicID=music.getId();
		for ( int i = 0; i < musicsAll.size(); i++) {
			YinXiangMusic tempMusic = musicsAll.get(i);
			if (musicID == tempMusic.getId()) {
				return i;
			} 
		}	
		return -1;		
	}
	
	
	public void setMusicsCheckAll(boolean checkAll){
		  selectMusics.clear();
		   if(checkAll){
			   for (int i = 0; i < musicsAct.size(); i++) {
					YinXiangMusic music = musicsAll.get(i);
					selectMusics.add(music);
				}
		   }	
	}
	
	private List<YinXiangMusic>  pareJsonToMusicList(String jsonStr) {
        List<YinXiangMusic> list = new ArrayList<YinXiangMusic>();
		if (null  != jsonStr) {		
			try {

				JSONTokener jsonParser = new JSONTokener(jsonStr);
				// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
				// 如果此时的读取位置在"name" : 了，那么nextValue就是"返回对象了"（String）

				JSONArray msgObject = (JSONArray) jsonParser.nextValue();

				// 获取消息类型
//				JSONArray jsonObjs = msgObject.getJSONArray("musicList");
				for (int i = 0; i < msgObject.length(); i++) {
					JSONObject musicObj = ((JSONObject) msgObject.opt(i));
					int id =musicObj.getInt("id");
                    String title = musicObj.getString("title");
                    String path = musicObj.getString("path");
                    String artist = musicObj.getString("artist");
                    String fileUrl = musicObj.getString("httpUrl");;

                    YinXiangMusic music = new YinXiangMusic(id, title, path, i, artist,i, 0, 4);
                    
                    //增加文件远程访问定位符
                    music.setFileUrl(fileUrl);
                    list.add(music);
				}	
            }catch (JSONException ex) {
    			// 异常处理代码
    			ex.printStackTrace();
    		}
		
        }
		return list;
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
			convertView = inflater.inflate(R.layout.yinixiang_music_list_item,
					null);
			wapper.musicIndex = (TextView) convertView.findViewById(R.id.yinxiang_music_item_index);
			wapper.musicName = (TextView) convertView.findViewById(R.id.yinxiang_music_item_name);
			wapper.fullPath = (TextView) convertView.findViewById(R.id.yinxiang_music_item_path);
			wapper.musicChecked = (CheckBox) convertView.findViewById(R.id.yinxiang_music_item_checked);

			wapper.editBtn = (ImageView) convertView.findViewById(R.id.yinxiang_music_item_edit);

//			wapper.musicImage.setTag(position);
			// 组装view
			convertView.setTag(wapper);
		} else {
			wapper = (DataWapper) convertView.getTag();
		}

		final YinXiangMusic yinXiangMusic = (YinXiangMusic) musicsAct.get(position);

		displayName = yinXiangMusic.getTitle();
		musicPath = yinXiangMusic.getPath();

		wapper.musicIndex.setText(Integer.toString(position+1));
		wapper.musicName.setText(displayName);
		wapper.fullPath.setText(musicPath);
		String musicImagePath = DiskCacheFileManager	.isSmallImageExist(musicPath);
//		if (!musicImagePath.equals("")) {
//			MyApplication.imageLoader.displayImage("file://" + musicImagePath,
//					wapper.musicImage, MyApplication.viewOptions);
//			wapper.musicImage.setScaleType(ImageView.ScaleType.FIT_XY);
//		} else {
//			YinXiangSetImageView.getInstance().setContext(context);
//			YinXiangSetImageView.getInstance().startExecutor(wapper.musicImage,
//					yinXiangMusic);
//		}

		final boolean isChecked = selectMusics.contains(yinXiangMusic);
		wapper.musicChecked.setChecked(isChecked);

		wapper.musicChecked.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox check = (CheckBox) v;
				if (check.isChecked()) {
					if (!selectMusics.contains(yinXiangMusic))
						selectMusics.add(yinXiangMusic);
				} else {
					selectMusics.remove(yinXiangMusic);
				}
//				YinXiangMusicViewActivity.musicSelectedInfo.setText("你共选择了"
//						+ selectMusics.size() + "首歌曲");
			}
		});

		// YD add 20150805 for file edit
		wapper.editBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				
				//发送消息给绑定的activity。
				Message msg=new Message();
				msg.what=1;
				msg.obj=yinXiangMusic;
				mHandler.sendMessage(msg);
			}
		});

		// wapper.musicChecked
		// .setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// // TODO Auto-generated method stub
		// if (isChecked) {
		// if (!selectMusics.contains(yinXiangMusic)) {
		// selectMusics.add(yinXiangMusic);
		// checkStateMap.put(String.valueOf(position), String.valueOf(1));
		// }
		// } else {
		// selectMusics.remove(yinXiangMusic);
		// checkStateMap.remove(String.valueOf(position));
		// }
		// Log.i("mmmm", "size=" + selectMusics.size());
		// YinXiangMusicViewActivity.musicSelectedInfo
		// .setText("你共选择了" + checkStateMap.size()
		// + "首歌曲");
		// }
		// });
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

		// 列表序号
		public TextView musicIndex;

		// 音频的名字
		public TextView musicName;

		// 音频是否被选中
		public CheckBox musicChecked;

		// 音屏的全路径
		public TextView fullPath;

		// YD add 20150805 音频的编辑按钮
		public ImageView editBtn;

	}
}
