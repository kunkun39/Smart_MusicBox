package com.changhong.yinxiang.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;

public class YinXiangFMFragment extends Fragment {

	/**
	 * message handler
	 */
	public static Handler mHandler = null;

	/************************************************** IP连接部分 *******************************************************/
   
//	private BoxSelectAdapter ipAdapter = null;
//	public static TextView title = null;
//	private ListView clients = null;
//	private Button list = null;
//	private Button back = null;

	/************************************************** 频道部分 *******************************************************/

	private GridView FMlist = null;
	private FMAdapter adapter = null;

	/********************************************************** play按钮 ***********************************************************/
	private ImageView mPlayingBtn = null;
	private AnimationDrawable mAnimation = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// // 请求Fm列表信息
		 ClientSendCommandService.handler.sendEmptyMessage(2);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_fm_switch, container,
				false);
		initViewAndEvent(view);
		return view;
	}

	private void initViewAndEvent(View v) {
		FMlist = (GridView) v.findViewById(R.id.fmlist);
		//取消GridView中Item选中时默认的背景色
		FMlist.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new FMAdapter(getActivity());
		FMlist.setAdapter(adapter);
		
		mHandler=new Handler(){
			  public void handleMessage(Message msg1) {
				        if(ClientSendCommandService.searchFMFinished){
				        	adapter.notifyDataSetChanged();				        	
				        }else{
				    		mHandler.sendMessageDelayed(new Message(), 1000);
				        }
			  }
		};		
		mHandler.sendEmptyMessage(1);
	}


	@Override
	public void onResume() {
		super.onResume();
	
	}

	/**
	 * FM名称
	 */
	private class FMAdapter extends BaseAdapter {

		private LayoutInflater minflater;

		public FMAdapter(Context context) {
			this.minflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return ClientSendCommandService.serverFMInfo.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,	ViewGroup parent) {
			/**
			 * VIEW HOLDER的配置
			 */
			final ViewHolder vh;
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = minflater.inflate(R.layout.activity_fm_item, null);
				vh.FMname = (TextView) convertView.findViewById(R.id.fmtxt);
				vh.FMplay = (ImageView) convertView.findViewById(R.id.btn_fm);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			
			if (ClientSendCommandService.serverFMInfo.size() > 0) {

				vh.FMname.setText(ClientSendCommandService.serverFMInfo	.get(position));
				vh.FMplay.setTag(vh);
				vh.FMplay.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						MyApplication.vibrator.vibrate(100);
						String serverFMInfor=ClientSendCommandService.serverFMInfo.get(position);
						ClientSendCommandService.msg = "fm:"	+serverFMInfor;
						ClientSendCommandService.handler.sendEmptyMessage(1);

						if (null != mPlayingBtn && null !=mAnimation) {
							if (mAnimation.isRunning())mAnimation.stop();
							mPlayingBtn.setBackgroundResource(R.drawable.fmplay);
						}
						
						//检查是否同一电台
						if(!arg0.equals(mPlayingBtn)){					
							ViewHolder vh = (ViewHolder) arg0.getTag();
							String  fmName=(String) vh.FMname.getText();
							if(fmName.equals(serverFMInfor)){
								arg0.setBackgroundResource(R.anim.playing_anim);
								mAnimation = (AnimationDrawable) arg0.getBackground();
								mAnimation.start();
								mPlayingBtn =(ImageView) arg0;
							}
						}
						

					}
				});
			}
			return convertView;
		}
		
	
		
		
		
		
		

		public final class ViewHolder {
			public TextView FMname;
			public ImageView FMplay;
		}
	}

}
