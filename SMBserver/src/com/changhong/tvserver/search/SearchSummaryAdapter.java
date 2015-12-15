package com.changhong.tvserver.search;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.changhong.tvserver.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiami.sdk.entities.OnlineSong;

/**
 * Created by Administrator on 12月17日.
 */
public class SearchSummaryAdapter extends BaseAdapter {
	LinkedList<OnlineSong> mSongList = new LinkedList<OnlineSong>();
	LayoutInflater mInflater;
	Context context;

	public SearchSummaryAdapter(Context con) {
		this.context=con;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void changeSongs(List<OnlineSong> songs) {
		mSongList.clear();
		mSongList.addAll(songs);
		notifyDataSetInvalidated();
	}
//
//	public void removeSong(OnlineSong song) {
//		mSongList.remove(song);
//		notifyDataSetChanged();
//	}

//	public void addSongFirst(OnlineSong song) {
//		mSongList.addFirst(song);
//		notifyDataSetChanged();
//	}
//
//	public void addSongLast(OnlineSong song) {
//		mSongList.addLast(song);
//		notifyDataSetChanged();
//	}

	public List<OnlineSong> getSongList() {
		return mSongList;
	}

	@Override
	public int getCount() {
		return mSongList.size();
	}

	@Override
	public OnlineSong getItem(int position) {
		return mSongList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.search_list_item,parent, false);	
			viewHolder.songName=(TextView) convertView.findViewById(R.id.song);
			viewHolder.singer=(TextView) convertView.findViewById(R.id.singer);
			viewHolder.index=(TextView) convertView.findViewById(R.id.index);
			viewHolder.logo=(ImageView) convertView.findViewById(R.id.logo);
			viewHolder.playBtn=(ImageView) convertView.findViewById(R.id.play);
			viewHolder.albumName=(TextView) convertView.findViewById(R.id.albumname);
			convertView.setTag(viewHolder);

			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		OnlineSong song=mSongList.get(position);
		viewHolder.songName.setText(mSongList.get(position).getSongName());
		viewHolder.singer.setText(mSongList.get(position).getSingers());	
		viewHolder.albumName.setText(mSongList.get(position).getAlbumName());	
		String logo=mSongList.get(position).getLogo();
		if(null !=logo)ImageLoader.getInstance().displayImage(logo, viewHolder.logo);
		viewHolder.playBtn.setTag(position);
		viewHolder.playBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				int index=(Integer) arg0.getTag();
				packageData(index);
			}
		});		
		return convertView;
	}

	
	
	private void packageData(int arg) {
		JSONObject o = new JSONObject();
		JSONArray array = new JSONArray();
		for (int i = arg; i < mSongList.size(); i++) {
			String path = mSongList.get(i).getListenFile();
			String title = mSongList.get(i).getSongName();
			String artist = mSongList.get(i).getArtistName();
			int duration = mSongList.get(i).getLength();
            long songID=mSongList.get(i).getSongId();
            
			JSONObject music = new JSONObject();
			music.put("id", songID);
			music.put("tempPath", path);
			music.put("title", title);
			music.put("artist", artist);
			music.put("duration", duration);
			array.put(music);
		}
			o.put("musicss", array.toString());
			o.put("musicType", "xiaMi");
		String listPath = "GetMusicList:" + o.toString();
		handleMusicMsgs(listPath);
	}
	
	private void handleMusicMsgs(String msg) {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.changhong.playlist",
				"com.changhong.playlist.Playlist"));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("musicpath", msg);
		context.startActivity(intent);
	}
	
	

	
	
	private  class ViewHolder {
		TextView index;
		TextView songName;
		TextView singer;
		TextView albumName;
		ImageView playBtn;
		ImageView logo;
	}
}
