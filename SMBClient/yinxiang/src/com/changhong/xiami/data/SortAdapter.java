package com.changhong.xiami.data;

import java.util.LinkedList;
import java.util.List;
import com.baidu.android.common.logging.Log;
import com.changhong.common.utils.StringUtils;
import com.changhong.yinxiang.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SortAdapter extends BaseAdapter implements SectionIndexer {

	List<XiamiDataModel> mSingerList = new LinkedList<XiamiDataModel>();
	private Context mContext;

	public SortAdapter(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<XiamiDataModel> list) {
		SingerListClear();
		mSingerList.addAll(list);
		Log.e("YDINFOR::", "start to update   sortAdapter......... ");
		notifyDataSetInvalidated();

	}

	public int getCount() {
		return this.mSingerList.size();
	}

	public Object getItem(int position) {
		return mSingerList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	private void SingerListClear() {

		int count = mSingerList.size();
		for (int i = 0; i < count; i++) {
			Bitmap bit = mSingerList.get(i).getLogoImg();
			if (bit != null && !bit.isRecycled()) {
				bit.recycle();
			}
		}
		mSingerList.clear();
	}

   @Override
	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final XiamiDataModel mContent = mSingerList.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.sort_item,
					null);
			viewHolder.title = (TextView) view.findViewById(R.id.item_name);
			viewHolder.letter = (TextView) view	.findViewById(R.id.sort_catalog);
			viewHolder.likeCount = (TextView) view.findViewById(R.id.item_content);
			viewHolder.img = (ImageView) view.findViewById(R.id.item_logo);
			viewHolder.divideLine= view.findViewById(R.id.sort_divideline);
			
			view.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			 String sortLetter=mContent.getSortLetters();
			 sortLetter=sortLetter.replace("!", "热门");
			viewHolder.letter.setText(sortLetter);
			viewHolder.letter.setVisibility(View.VISIBLE);
			viewHolder.divideLine.setVisibility(View.VISIBLE);


		} else {
			viewHolder.letter.setVisibility(View.GONE);
			viewHolder.divideLine.setVisibility(View.GONE);

		}

		XiamiDataModel model=mSingerList.get(position);
		viewHolder.title.setText(this.mSingerList.get(position).getTitle());
		
		int likeCount=mSingerList.get(position).getLikeCount();
		if(likeCount/10000 >0){
		         viewHolder.likeCount.setText(likeCount/10000+" 万粉丝");
		}else{
	         viewHolder.likeCount.setText(likeCount+" 粉丝");
	    }

		String logo=mSingerList.get(position).getArtistImgUrl();
		if(StringUtils.hasLength(logo)){
			ImageLoader.getInstance().displayImage(logo, viewHolder.img);
		}
		return view;

	}

	final static class ViewHolder {
		TextView letter;
		TextView title;
		TextView likeCount;	
		ImageView img;
		View divideLine;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return mSingerList.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = mSingerList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 根据View获取其第一次出现该首字母的位置
	 */
	public String getLetterByPosition(int position) {

		if (position < getCount()) {
			String sortStr = mSingerList.get(position).getSortLetters();
			return sortStr;

		}
		return null;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}