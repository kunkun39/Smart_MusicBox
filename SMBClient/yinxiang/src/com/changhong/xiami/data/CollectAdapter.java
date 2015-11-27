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
import android.widget.ImageView;
import android.widget.TextView;

public class CollectAdapter extends BaseAdapter{

	List<XiamiDataModel> mCollectList = new LinkedList<XiamiDataModel>();
	private Context mContext;
	private int mScreenWidth;
	private int mScreenHeight;
    ImageLoader imageLoader;
	private Handler parentHandler=null;

	public CollectAdapter(Context mContext,Handler parent) {
		this.mContext = mContext;
		this.parentHandler=parent;
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display d = wm.getDefaultDisplay();
		mScreenWidth = d.getWidth();
		mScreenHeight = d.getHeight()-65;
		imageLoader=ImageLoader.getInstance();
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<XiamiDataModel> list){
		mCollectList.clear();
    	mCollectList.addAll(list);
        notifyDataSetInvalidated();      

	}

	public int getCount() {
		return this.mCollectList.size();
	}

	public Object getItem(int position) {
		return mCollectList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final XiamiDataModel dataModel = mCollectList.get(position);
		
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.xiami_collect_list_item, null);	
			AbsListView.LayoutParams param = new AbsListView.LayoutParams( android.view.ViewGroup.LayoutParams.FILL_PARENT,mScreenHeight/4);
			view.setLayoutParams(param);					
			viewHolder.collectName = (TextView) view.findViewById(R.id.collect_title);
			viewHolder.artist = (TextView) view.findViewById(R.id.collect_artist);
			viewHolder.collectDetail= (TextView) view.findViewById(R.id.collect_detail);
			viewHolder.likeCount = (TextView) view.findViewById(R.id.collect_likecount);
			viewHolder.collectLogo=(ImageView) view.findViewById(R.id.collect_logo);
			viewHolder.collectPlay=(ImageView) view.findViewById(R.id.collect_play);	
			viewHolder.artistImg=(ImageView) view.findViewById(R.id.collect_artistimg);	
			
			view.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		viewHolder.collectName.setText(this.mCollectList.get(position).getTitle());	
		viewHolder.artist.setText(this.mCollectList.get(position).getArtist());	
		viewHolder.collectDetail.setText(this.mCollectList.get(position).getDescription());	
		viewHolder.likeCount.setText(this.mCollectList.get(position).getLikeCount()+"");
		String logo=mCollectList.get(position).getLogoUrl();
		String artistImg=mCollectList.get(position).getArtistImgUrl();
		
		imageLoader.displayImage(logo, viewHolder.collectLogo);
		imageLoader.displayImage(artistImg, viewHolder.artistImg);
		viewHolder.collectLogo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(30);
				if(null != parentHandler){					
				    Message msg=parentHandler.obtainMessage();
				    msg.arg1=(int) mCollectList.get(position).getId();
				    msg.what=Configure.XIAMI_PLAY_MUSICS;
				    parentHandler.sendMessage(msg);
				}				
			}
		});
		return view;

	}
	


	final static class ViewHolder {
		
		TextView collectId;
		TextView collectName;
		TextView collectDetail;
		TextView artist;
		TextView likeCount;
		TextView category;
		ImageView collectLogo;
		ImageView   collectPlay;
		ImageView artistImg;


	}

}