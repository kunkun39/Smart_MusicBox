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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.changhong.tvserver.R;
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

	public void removeSong(OnlineSong song) {
		mSongList.remove(song);
		notifyDataSetChanged();
	}

	public void addSongFirst(OnlineSong song) {
		mSongList.addFirst(song);
		notifyDataSetChanged();
	}

	public void addSongLast(OnlineSong song) {
		mSongList.addLast(song);
		notifyDataSetChanged();
	}

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
		return getItem(position).getSongId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.search_list_item, parent,
					false);
			viewHolder = new ViewHolder(convertView);
		} else {
			viewHolder = ViewHolder.getFromView(convertView);
		}
		viewHolder.title.setText(mSongList.get(position).getSongName());
		viewHolder.subtitle.setText(mSongList.get(position).getSingers());
		viewHolder.play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				packageData(position);
			}
		});
		return convertView;
	}

	private static class ViewHolder {
		TextView title;
		TextView subtitle;
		ImageView play;

		public ViewHolder(View view) {
			title = (TextView) view.findViewById(R.id.title);
			subtitle = (TextView) view.findViewById(R.id.subtitle);
			play=(ImageView)view.findViewById(R.id.play);
			view.setTag(this);
		}

		public static ViewHolder getFromView(View view) {
			Object tag = view.getTag();
			if (tag instanceof ViewHolder) {
				return (ViewHolder) tag;
			} else {
				return new ViewHolder(view);
			}
		}

//		public void render(int position) {
//			title.setText(mSongList.get(position).getSongName());
//			subtitle.setText(mSongList.get(position).getSingers());
//		}
	}
	
	private void packageData(int arg) {
		JSONObject o = new JSONObject();
		JSONArray array = new JSONArray();
		for (int i = arg; i < mSongList.size(); i++) {
			

			String path = mSongList.get(i).getListenFile();
			String title = mSongList.get(i).getSongName();
			String artist = mSongList.get(i).getArtistName();
			int duration = mSongList.get(i).getLength();

			JSONObject music = new JSONObject();
			music.put("tempPath", path);
			music.put("title", title);
			music.put("artist", artist);
			music.put("duration", duration);
			array.put(music);
		}
			o.put("musicss", array.toString());
		

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
}
