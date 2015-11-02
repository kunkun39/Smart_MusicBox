package com.changhong.xiami.data;

import java.util.LinkedList;
import java.util.List;

import com.changhong.yinxiang.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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
	
	public SceneAdapter(Context mContext) {
		this.mContext = mContext;
		
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display d = wm.getDefaultDisplay();
		mScreenWidth = d.getWidth();
		mScreenHeight = d.getHeight()-65;
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
		return this.mSceneList.size();
	}

	public Object getItem(int position) {
		return mSceneList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final XiamiDataModel mContent = mSceneList.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.xiami_album_list_item, null);	
			AbsListView.LayoutParams param = new AbsListView.LayoutParams( android.view.ViewGroup.LayoutParams.FILL_PARENT,
					mScreenHeight/3);
			view.setLayoutParams(param);			
			viewHolder.sceneName = (TextView) view.findViewById(R.id.ablum_name);
			viewHolder.sceneLogo=(ImageView) view.findViewById(R.id.ablum_logo);
			viewHolder.scenePlay=(ImageView) view.findViewById(R.id.ablum_play);	
			FrameLayout.LayoutParams params=(LayoutParams) viewHolder.scenePlay.getLayoutParams();
			params.gravity=Gravity.BOTTOM|Gravity.RIGHT;
			viewHolder.scenePlay.setLayoutParams(params);
			
		   params=(LayoutParams) viewHolder.sceneName.getLayoutParams();
			params.gravity=Gravity.TOP|Gravity.LEFT;
			viewHolder.sceneName.setLayoutParams(params);
			
			view.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		viewHolder.sceneName.setText(this.mSceneList.get(position).getName());	
		Bitmap logo=this.mSceneList.get(position).getImage();
		if(null != logo)viewHolder.sceneLogo.setImageBitmap(logo);	

		return view;

	}
	


	final static class ViewHolder {
		
		TextView sceneId;
		TextView sceneName;
		ImageView sceneLogo;
		ImageView   scenePlay;

	}

}