package com.changhong.yinxiang.remotecontrol;


import java.io.File;
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
import com.changhong.yinxiang.view.SwitchButton;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

/**
 * 
 */
public class AudioCtrlAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private List<String> audioCtrlAll;
	private Context context;
	public static final int AUDIO_CTRL_YINXIAO=1;
	public static final int AUDIO_CTRL_LIGHT=2;
	
	/**
	 * YD add 20150806 for fileEdit 音频文件编辑功能
	 */
       Handler mHandler=null;

	public AudioCtrlAdapter(Context context, String  imgs) {
		this.context = context;
		initAudioCtrl(imgs);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	private void initAudioCtrl(String  imgs){
		String[] imgList=imgs.split(";");
		audioCtrlAll=new ArrayList<String>();
		for (int i = 0; i < imgList.length; i++) {
			audioCtrlAll.add(imgList[i]);
		}
		
	}
	

	

	public int getCount() {
		return audioCtrlAll.size();
	}

	public Object getItem(int item) {
		return item;
	}

	public long getItemId(int id) {
		return id;
	}

	// 创建View方法
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView =new ImageView(context);	
		} 
		return convertView;
	}
}
