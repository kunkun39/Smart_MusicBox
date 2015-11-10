package com.changhong.xiami.data;

import java.util.LinkedList;
import java.util.List;

import com.changhong.yinxiang.R;
import com.nostra13.universalimageloader.core.ImageLoader;

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
	private ImageLoader imageLoader;
	private int MAX_ITEM=6;
	private int[] resID={R.drawable.scene1,R.drawable.scene2,R.drawable.scene3,
			R.drawable.scene4,R.drawable.scene5,R.drawable.scene6};

	public SceneAdapter(Context mContext) {
		this.mContext = mContext;
		
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
		return MAX_ITEM;
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
			AbsListView.LayoutParams param = new AbsListView.LayoutParams( mScreenWidth/2,		mScreenHeight/3);
			view.setLayoutParams(param);			
			viewHolder.sceneName = (TextView) view.findViewById(R.id.ablum_name);
			viewHolder.sceneLogo=(ImageView) view.findViewById(R.id.ablum_logo);
			viewHolder.scenePlay=(ImageView) view.findViewById(R.id.ablum_play);	
		
			
			TextView detail=(TextView) view.findViewById(R.id.ablum_content);
			detail.setVisibility(View.GONE);
			viewHolder.sceneName.setVisibility(View.GONE);
			
			view.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
//		if(position<mSceneList.size()){
//		    String logo=mSceneList.get(position).getLogoUrl();
//			ImageLoader.getInstance().displayImage(logo, viewHolder.sceneLogo);
//		}else{
//	     	viewHolder.sceneLogo.setImageResource(resID[position]);
//		}
//		
     	viewHolder.sceneLogo.setImageResource(resID[position]);


		return view;

	}
	


	final static class ViewHolder {
		
		TextView sceneId;
		TextView sceneName;
		ImageView sceneLogo;
		ImageView   scenePlay;

	}

}