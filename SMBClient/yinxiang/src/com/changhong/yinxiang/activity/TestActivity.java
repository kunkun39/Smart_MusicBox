package com.changhong.yinxiang.activity;


import java.util.HashMap;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;

public class TestActivity extends Activity {
	
	FMAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_main);
 

		GridView myList=(GridView) findViewById(R.id.testlist);
		
		myList.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new FMAdapter(this);
		myList.setAdapter(adapter);
		myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {   
					    Toast.makeText(TestActivity.this, "点击成功", Toast.LENGTH_LONG).show();
					    adapter.changeImageMode(view,position);
					    
				}
			});
		
		myList.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
//			    adapter.changeImageMode(view,position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		myList.setSelection(20);

	}
	
	
	private int mLastPosition=-1;

	/**
	 * FM名称
	 */
	private class FMAdapter extends BaseAdapter {

		private  Context  mContext;
		private View mLastView = null;
		private ImageView  mPlayingImage=null;
		private boolean showBar=false;
		private int mCurPosition=20;

      
		public FMAdapter(Context context) {
			this.mContext = context;
			initData();
		}
		@Override
		public int getCount() {
			return ClientSendCommandService.serverFMInfo.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return ClientSendCommandService.serverFMInfo.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		
		public void setindex(int pos) {
			mCurPosition = pos;
		}
		
		
//		private HashMap<Integer, View> viewMap=new HashMap<Integer, View>() ;
		@Override
//		public View getView( int position, View convertView,	ViewGroup parent) {
//			/**
//			 * VIEW HOLDER的配置
//			 */
//			final ViewHolder vh;
//			if(!viewMap.containsKey(position)){  
//			    LayoutInflater minflater=LayoutInflater.from(mContext);
//				convertView = minflater.inflate(R.layout.activity_fm_item, null);
//				vh = new ViewHolder();
//				vh.FMname = (TextView) convertView.findViewById(R.id.fmtxt);
//				vh.FMplay = (ImageView) convertView.findViewById(R.id.btn_fm);
//				convertView.setTag(vh);
//				viewMap.put(position, convertView);
//			} else {
//	            convertView = viewMap.get(position);  
//				vh = (ViewHolder) convertView.getTag();
//			}
//			
//			
//			if (ClientSendCommandService.serverFMInfo.size() > 0) {
//
//				vh.FMname.setText(ClientSendCommandService.serverFMInfo	.get(position));
//				vh.id=position;
//				
//				
//				
//				
//                Log.e("YDINFOR:: ","  position="+position);             
//			}
//			return convertView;
//		}
		
		
		public View getView( int position, View convertView,	ViewGroup parent) {
			/**
			 * VIEW HOLDER的配置
			 */
			final ViewHolder vh;
			if(null == convertView){  
			    LayoutInflater minflater=LayoutInflater.from(mContext);
				convertView = minflater.inflate(R.layout.activity_fm_item, null);
				vh = new ViewHolder();
				vh.FMname = (TextView) convertView.findViewById(R.id.fmtxt);
				vh.FMpause = (ImageView) convertView.findViewById(R.id.btn_fm);
				vh.FMplay = (ImageView) convertView.findViewById(R.id.fmisplay);

				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			
			
			if (ClientSendCommandService.serverFMInfo.size() > 0) {

				vh.FMname.setText(ClientSendCommandService.serverFMInfo	.get(position));
				vh.id=position;
					
				if (position ==mCurPosition) {
					vh.FMpause.setVisibility(View.INVISIBLE);
					vh.FMplay.setVisibility(View.VISIBLE);
				} else {
					vh.FMpause.setVisibility(View.VISIBLE);
					vh.FMplay.setVisibility(View.INVISIBLE);
				}
				
                Log.e("YDINFOR:: ","  position="+position);             
			}
			return convertView;
		}
		
		private void initData(){
			ClientSendCommandService.serverFMInfo.clear();
			
			for (int i = 0; i < 200; i++) {
				ClientSendCommandService.serverFMInfo.add("测试电台"+i);
			}
			
		}
	
		
		private AnimationDrawable mAnimation = null;

		public void changeImageMode(View view,int position) { 
			
			MyApplication.vibrator.vibrate(100);
			
//			ViewHolder holder;
//	        if(mLastView != null ) {  
//	        	holder = (ViewHolder) mLastView.getTag();  
//	        	mLastPosition=holder.id;
//				if (mAnimation.isRunning())mAnimation.stop();
//				holder.FMplay.setBackgroundResource(R.drawable.fmplay);	 
//				holder.FMname.setTextColor(mContext.getResources().getColor(R.color.white));
//	        } 	        
//	        holder = (ViewHolder) view.getTag();  
//	        if(holder.id == position && mLastPosition !=position){
//		            mLastPosition = position;  
//			        mLastView = view;  
//					holder.FMname.setTextColor(mContext.getResources().getColor(R.color.tab_textColor_selected));
//			        holder.FMplay.setBackgroundResource(R.anim.playing_anim);
//			        mAnimation = (AnimationDrawable) holder.FMplay.getBackground();
//					mAnimation.start();	 
//	        }else{       
//	        	mLastPosition=-1;
//	        }
			
			adapter.setindex(-1);
			adapter.notifyDataSetChanged();
			
			if(mLastPosition != position){
				adapter.setindex(position);
				adapter.notifyDataSetChanged();
				mLastPosition=position;
			}else{
				mLastPosition=-1;
			}
			
	    }  
		
		

		public final class ViewHolder {
			public int id;
			public TextView FMname;
			public ImageView FMpause;
			public ImageView FMplay;

		}
	}
}
