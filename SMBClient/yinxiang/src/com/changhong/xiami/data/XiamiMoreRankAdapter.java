package com.changhong.xiami.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.RankListItem;

public class XiamiMoreRankAdapter extends BaseAdapter {

	private List<RankListItem> myList = new ArrayList<RankListItem>();
	private Context context;
	private LayoutInflater inflater;

	public XiamiMoreRankAdapter(Context context) {
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(List<RankListItem> list) {
		if (null == list) {
			return;
		}
		this.myList = list;
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
		return myList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		DataHolder dataHolder = null;
		if (null == convertView) {
			convertView = inflater
					.inflate(R.layout.xiami_rankdetail_item, null);
			dataHolder = new DataHolder();
			dataHolder.rankTitle = (TextView) convertView
					.findViewById(R.id.rank_detail_title);
			dataHolder.rankImage = (ImageView) convertView
					.findViewById(R.id.rank_detail_logo);
			dataHolder.rankPlay = (ImageView) convertView
					.findViewById(R.id.rank_detail_play);

			dataHolder.rankMusicName1 = (TextView) convertView
					.findViewById(R.id.rank_item_musicname1);
			dataHolder.rankMusicArtist1 = (TextView) convertView
					.findViewById(R.id.rank_item_musicartist1);

			dataHolder.rankMusicName2 = (TextView) convertView
					.findViewById(R.id.rank_item_musicname2);
			dataHolder.rankMusicArtist2 = (TextView) convertView
					.findViewById(R.id.rank_item_musicartist2);

			dataHolder.rankMusicName3 = (TextView) convertView
					.findViewById(R.id.rank_item_musicname3);
			dataHolder.rankMusicArtist3 = (TextView) convertView
					.findViewById(R.id.rank_item_musicartist3);
			convertView.setTag(dataHolder);
		} else {
			dataHolder = (DataHolder) convertView.getTag();
		}
		RankListItem rankItem = myList.get(position);
		List<OnlineSong> songList = rankItem.getSongs();
		dataHolder.rankTitle.setText(rankItem.getTitle());
		MyApplication.imageLoader.displayImage(rankItem.getLogoMiddle(),
				dataHolder.rankImage);
		if (songList.size() > 0) {
			dataHolder.rankMusicName1.setText("1. "+songList.get(0).getSongName()+"  -  "+songList.get(0).getArtistName());
		} 
		if (songList.size() > 1) {
			dataHolder.rankMusicName2.setText("2. "+songList.get(1).getSongName()+"  -  "+songList.get(1).getArtistName());
		} 
		if (songList.size() > 2) {
			dataHolder.rankMusicName3.setText("3. "+songList.get(2).getSongName()+"  -  "+songList.get(2).getArtistName());
		}
		dataHolder.rankPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		return convertView;
	}

	private final class DataHolder {
		public TextView rankTitle;
		public ImageView rankImage;
		public ImageView rankPlay;
		public TextView rankMusicName1;
		public TextView rankMusicArtist1;

		public TextView rankMusicName2;
		public TextView rankMusicArtist2;

		public TextView rankMusicName3;
		public TextView rankMusicArtist3;
	}

}
