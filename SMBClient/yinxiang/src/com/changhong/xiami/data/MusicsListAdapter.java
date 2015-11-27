package com.changhong.xiami.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;
import com.xiami.sdk.entities.OnlineSong;

public class MusicsListAdapter extends BaseAdapter {
	private ArrayList<OnlineSong> myList = new ArrayList<OnlineSong>();
	private LayoutInflater layout;
	private Context context;

	public MusicsListAdapter(Context con) {
		this.context = con;
		this.layout = (LayoutInflater) con
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (null == myList) {
			myList = new ArrayList<OnlineSong>();
		}
	}

	public void setData(List<OnlineSong> list) {
		this.myList = (ArrayList<OnlineSong>)list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return myList != null ? myList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DataHolder holder = null;
		if (null == convertView) {
			convertView = layout.inflate(R.layout.xiami_music_list_item, null);
			holder = new DataHolder();
			holder.index = (TextView) convertView
					.findViewById(R.id.xiami_listitem_index);
			holder.title = (TextView) convertView
					.findViewById(R.id.xiami_music_item_title);
			holder.artist = (TextView) convertView
					.findViewById(R.id.xiami_music_item_artist_duration);
			holder.play = (ImageButton) convertView
					.findViewById(R.id.xiami_music_list_play);
			holder.musicImage=(ImageView)convertView.findViewById(R.id.xiami_listitem_music_image);
			convertView.setTag(holder);
		} else {
			holder = (DataHolder) convertView.getTag();
		}
		OnlineSong song=myList.get(position);
		holder.index.setText(String.valueOf(position + 1));
		holder.title.setText(song.getSongName());
		holder.artist.setText(song.getArtistName());
		MyApplication.imageLoader.displayImage(song.getImageUrl(), holder.musicImage);

		return convertView;
	}

	private final class DataHolder {
		public TextView index;
		public TextView title;
		public TextView artist;
		public ImageButton play;
		public ImageView musicImage;
	}

}
