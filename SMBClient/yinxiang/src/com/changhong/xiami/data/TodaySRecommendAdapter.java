package com.changhong.xiami.data;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import com.changhong.yinxiang.R;
import com.xiami.sdk.entities.OnlineSong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class TodaySRecommendAdapter extends BaseAdapter{
	
	private List<OnlineSong> myList=new ArrayList<OnlineSong>();
	private Context context;
	private LayoutInflater inflater;
	
	
	public TodaySRecommendAdapter(Context con) {
		// TODO Auto-generated constructor stub
		this.context=con;
		inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setData(ArrayList<OnlineSong> list){
		this.myList=list;
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
		DataHolder dataHolder;
		if(null==convertView){
			convertView=inflater.inflate(R.layout.xiami_todayrecom_item, null);
			dataHolder=new DataHolder();
			dataHolder.todayReText=(TextView)convertView.findViewById(R.id.today_recom_text);
			dataHolder.picture=(ImageView)convertView.findViewById(R.id.today_recom_image);
			convertView.setTag(dataHolder);
		}else{
			dataHolder=(DataHolder) convertView.getTag();
		}
		
		
		
		
		
		return convertView;
	}
	
	
	private final class DataHolder {
		public TextView todayReText;
		public ImageView picture;
	}

}
