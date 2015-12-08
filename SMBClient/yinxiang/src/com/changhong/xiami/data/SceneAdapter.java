package com.changhong.xiami.data;

import java.util.LinkedList;
import java.util.List;

import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.utils.Configure;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SceneAdapter extends BaseAdapter{

	List<XiamiDataModel> mSceneList = new LinkedList<XiamiDataModel>();
	private Context mContext;
	private int mScreenWidth;
	private int mScreenHeight;
	private ImageLoader imageLoader;
	private Handler parentHandler=null;

	public SceneAdapter(Context mContext,Handler parent) {
		this.mContext = mContext;
		this.parentHandler=parent;
		
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display d = wm.getDefaultDisplay();
		mScreenWidth = d.getWidth()-60;
		mScreenHeight = d.getHeight()-220;
		imageLoader=ImageLoader.getInstance();

	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<XiamiDataModel> list){
		mSceneList.clear();
    	mSceneList.addAll(list);
        notifyDataSetInvalidated();      

	}

	public int getCount() {
		return mSceneList.size();
	}

	public Object getItem(int position) {
		return mSceneList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.xiami_album_list_item, null);	
			AbsListView.LayoutParams param = new AbsListView.LayoutParams( mScreenWidth/2,mScreenHeight/3);
			view.setLayoutParams(param);			
			viewHolder.sceneName = (TextView) view.findViewById(R.id.ablum_name2);
			viewHolder.sceneLogo=(ImageView) view.findViewById(R.id.ablum_logo);
			viewHolder.scenePlay=(ImageView) view.findViewById(R.id.ablum_play);	
		
			
			TextView detail=(TextView) view.findViewById(R.id.ablum_content);
			TextView name=(TextView) view.findViewById(R.id.ablum_name1);
			detail.setVisibility(View.GONE);			
			name.setVisibility(View.GONE);
			viewHolder.sceneName.setVisibility(View.VISIBLE);
			viewHolder.sceneName.getBackground().setAlpha(0);
			view.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		String logo=mSceneList.get(position).getLogoUrl();
		ImageLoader.getInstance().displayImage(logo, viewHolder.sceneLogo);
		viewHolder.sceneName.setText(mSceneList.get(position).getTitle());
		
		viewHolder.scenePlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyApplication.vibrator.vibrate(30);
				if(null != parentHandler){
				    Message msg=parentHandler.obtainMessage();
				    msg.arg1=(int) mSceneList.get(position).getId();
				    msg.what=Configure.XIAMI_PLAY_MUSICS;
				    parentHandler.sendMessage(msg);
				}
			}
		});
		
		return view;

	}
	


	final static class ViewHolder {
		
		TextView sceneId;
		TextView sceneName;
		ImageView sceneLogo;
		ImageView   scenePlay;

	}

}