package com.changhong.xiami.data;

import java.util.LinkedList;
import java.util.List;
import com.changhong.yinxiang.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiami.music.model.Image;
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
	private   String singer,logo;
	private Context mContext;
	
	public SongAdapter(Context mContext) {
		this.mContext = mContext;
	}
	
	/**
	 * 当ListView数据发生变化�?调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<OnlineSong> list,String singer,String logo){
		
		this.singer=singer;
		this.logo=logo;
		
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
			view = LayoutInflater.from(mContext).inflate(R.layout.songs_item, null);		
			viewHolder.songTitle = (TextView) view.findViewById(R.id.song_name);
			viewHolder.songArtist = (TextView) view.findViewById(R.id.song_artist);
			viewHolder.singerLogo = (ImageView) view.findViewById(R.id.song_logo);	
			viewHolder.songArtist.setText(singer);	
			ImageLoader.getInstance().displayImage(logo, viewHolder.singerLogo );
			
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		viewHolder.songTitle.setText(this.mSongList.get(position).getSongName());	

		return view;

	}
	


	final static class ViewHolder {
		
		TextView songArtist;
		TextView songTitle;
        ImageView  singerLogo;
	}

}