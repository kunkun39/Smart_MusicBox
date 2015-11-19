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
	private String keyStr;
	private String displayName, musicPath;
	private boolean checkSetFlag = false;

	/**
	 * YD add 20150806 for fileEdit 音频文件编辑功能
	 */
       Handler mHandler=null;

	public AudioCtrlAdapter(Context context, List<String>audioCtrl) {
		this.context = context;
		this.audioCtrlAll = audioCtrl;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			convertView = inflater.inflate(R.layout.switch_button,null);	
		} 
		((SwitchButton)convertView).init(audioCtrlAll.get(position));
		return convertView;
	}
}
