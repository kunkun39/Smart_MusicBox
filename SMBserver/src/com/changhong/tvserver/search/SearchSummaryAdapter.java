package com.changhong.tvserver.search;

import java.util.LinkedList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.changhong.tvserver.R;
import com.xiami.sdk.entities.OnlineSong;

/**
 * Created by Administrator on 12月17日.
 */
public class SearchSummaryAdapter extends BaseAdapter {
	LinkedList<OnlineSong> mSongList = new LinkedList<OnlineSong>();
	LayoutInflater mInflater;

	public SearchSummaryAdapter(LayoutInflater layoutInflater) {
		mInflater = layoutInflater;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.search_list_item, parent,
					false);
			viewHolder = new ViewHolder(convertView);
		} else {
			viewHolder = ViewHolder.getFromView(convertView);
		}
		viewHolder.render(getItem(position));
		return convertView;
	}

	private static class ViewHolder {
		TextView title;
		TextView subtitle;

		public ViewHolder(View view) {
			title = (TextView) view.findViewById(R.id.title);
			subtitle = (TextView) view.findViewById(R.id.subtitle);
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

		public void render(OnlineSong item) {
			title.setText(item.getSongName());
			subtitle.setText(item.getSingers());
		}
	}
}
