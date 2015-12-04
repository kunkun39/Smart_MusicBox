package com.changhong.tvserver.search;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.changhong.tvserver.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiami.sdk.entities.OnlineArtist;

public class SingerAdapter extends BaseAdapter {
	LinkedList<OnlineArtist> mArtistList = new LinkedList<OnlineArtist>();
	LayoutInflater mInflater;
	Context context;
	Handler mParent;
	private final int MAX_SIZE = 5;

	public SingerAdapter(Context con, Handler parent) {
		this.context = con;
		this.mParent = parent;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void changeSingers(List<OnlineArtist> singers) {
		mArtistList.clear();
		mArtistList.addAll(singers);
		notifyDataSetInvalidated();
	}

	public void addArtist(OnlineArtist artist) {
		mArtistList.addFirst(artist);
		mArtistList.removeLast();
		notifyDataSetChanged();
	}

	public List<OnlineArtist> getArtisrList() {
		return mArtistList;
	}

	@Override
	public int getCount() {
		return mArtistList.size();
	}

	@Override
	public OnlineArtist getItem(int position) {
		return mArtistList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.search_artist_item,parent, false);
			viewHolder.logo = (ImageView) convertView.findViewById(R.id.logo);
			viewHolder.singer = (TextView) convertView.findViewById(R.id.artist);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (position < mArtistList.size()) {
			
			OnlineArtist artist=mArtistList.get(position);
			viewHolder.singer.setText(mArtistList.get(position).getName());
			String logo = mArtistList.get(position).getLogo();
			ImageLoader.getInstance().displayImage(logo, viewHolder.logo);
			viewHolder.logo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mParent.sendEmptyMessage(100);
				}
			});
		}
		return convertView;
	}

	private class ViewHolder {
		TextView singer;
		ImageView logo;
	}

}
