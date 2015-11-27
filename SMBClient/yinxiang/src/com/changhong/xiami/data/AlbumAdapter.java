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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumAdapter extends BaseAdapter{

	List<XiamiDataModel> mAlbumList = new LinkedList<XiamiDataModel>();
	private Context mContext;
	private int mScreenWidth;
	private int mScreenHeight;
	private  ImageLoader imageLoader;
	private Handler parentHandler=null;

	public AlbumAdapter(Context mContext,Handler parent) {
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
		mAlbumList.clear();
    	mAlbumList.addAll(list);
        notifyDataSetInvalidated();      

	}

	public int getCount() {
		return this.mAlbumList.size();
	}

	public Object getItem(int position) {
		return mAlbumList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final XiamiDataModel mContent = mAlbumList.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.xiami_album_list_item, null);	
			AbsListView.LayoutParams param = new AbsListView.LayoutParams( android.view.ViewGroup.LayoutParams.FILL_PARENT,
					mScreenHeight/3);
			view.setLayoutParams(param);			
			viewHolder.albumName = (TextView) view.findViewById(R.id.ablum_name);
			viewHolder.albumContent = (TextView) view.findViewById(R.id.ablum_content);
			viewHolder.albumLogo=(ImageView) view.findViewById(R.id.ablum_logo);
			viewHolder.albumPlay=(ImageView) view.findViewById(R.id.ablum_play);			
			view.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		viewHolder.albumId=mAlbumList.get(position).getId();
		viewHolder.albumName.setText(mAlbumList.get(position).getTitle());
		viewHolder.albumContent.setText(mAlbumList.get(position).getDescription());	
		String logo=mAlbumList.get(position).getLogoUrl();
		imageLoader.displayImage(logo, viewHolder.albumLogo);
		viewHolder.albumPlay.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(30);
				if(null != parentHandler){
				    Message msg=parentHandler.obtainMessage();
				    msg.arg1=(int) mAlbumList.get(position).getId();
				    msg.what=Configure.XIAMI_PLAY_MUSICS;
				    parentHandler.sendMessage(msg);
				}
				      
			}
		});
		return view;

	}
	


	final static class ViewHolder {
		
		long albumId;
		TextView albumName;
		TextView albumContent;
		ImageView albumLogo;
		ImageView   albumPlay;

	}

}