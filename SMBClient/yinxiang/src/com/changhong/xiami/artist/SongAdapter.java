package com.changhong.xiami.artist;

import java.util.LinkedList;
import java.util.List;
import com.changhong.yinxiang.R;
import com.xiami.sdk.entities.OnlineSong;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SongAdapter extends BaseAdapter{

	List<OnlineSong> mSongList = new LinkedList<OnlineSong>();
	private Context mContext;
	
	public SongAdapter(Context mContext) {
		this.mContext = mContext;
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<OnlineSong> list){
		mSongList.clear();
    	mSongList.addAll(list);
        notifyDataSetInvalidated();      

	}

	public int getCount() {
		return this.mSongList.size();
	}

	public Object getItem(int position) {
		return mSongList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final OnlineSong mContent = mSongList.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.sort_item, null);
			
			
			viewHolder.songTitle = (TextView) view.findViewById(R.id.item_name);
			viewHolder.songFile = (TextView) view.findViewById(R.id.sort_catalog);
			viewHolder.songId = (TextView) view.findViewById(R.id.item_id);
			viewHolder.songContent = (TextView) view.findViewById(R.id.item_content);
			
			
			ImageView img=(ImageView) view.findViewById(R.id.item_logo);
			ImageView nextImg=(ImageView) view.findViewById(R.id.item_next);
			
			img.setVisibility(View.GONE);			
			nextImg.setImageResource(R.drawable.fmplay);
			viewHolder.songId.setVisibility(View.VISIBLE);
			
			view.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		viewHolder.songId.setText((position+1)+"");	
		viewHolder.songTitle.setText(this.mSongList.get(position).getSongName());	
//		viewHolder.songFile.setText(this.mSongList.get(position).getListenFile());	

		return view;

	}
	


	final static class ViewHolder {
		
		TextView songId;
		TextView songFile;
		TextView songTitle;
		TextView songContent;

	}

}