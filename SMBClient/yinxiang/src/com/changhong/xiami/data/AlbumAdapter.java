package com.changhong.xiami.data;

import java.util.LinkedList;
import java.util.List;

import com.changhong.yinxiang.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumAdapter extends BaseAdapter{

	List<XiamiDataModel> mAlbumList = new LinkedList<XiamiDataModel>();
	private Context mContext;
	
	public AlbumAdapter(Context mContext) {
		this.mContext = mContext;
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
			viewHolder.albumName = (TextView) view.findViewById(R.id.ablum_name);
			viewHolder.albumContent = (TextView) view.findViewById(R.id.ablum_content);
			viewHolder.albumLogo=(ImageView) view.findViewById(R.id.ablum_logo);
			viewHolder.albumPlay=(ImageButton) view.findViewById(R.id.ablum_play);			
			view.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		viewHolder.albumName.setText(this.mAlbumList.get(position).getName());	
		viewHolder.albumContent.setText(this.mAlbumList.get(position).getContent());	
		viewHolder.albumLogo.setImageBitmap(this.mAlbumList.get(position).getImage());	

		return view;

	}
	


	final static class ViewHolder {
		
		TextView albumId;
		TextView albumName;
		TextView albumContent;
		ImageView albumLogo;
		ImageButton   albumPlay;

	}

}