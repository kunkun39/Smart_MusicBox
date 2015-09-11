package com.changhong.yinxiang.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.BoxSelectAdapter;
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
				vh.FMplay.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						MyApplication.vibrator.vibrate(100);
						ClientSendCommandService.msg = "fm:"	+ ClientSendCommandService.serverFMInfo	.get(position);
						ClientSendCommandService.handler.sendEmptyMessage(1);

						if (null != mPlayingBtn) {
							mAnimation = (AnimationDrawable) mPlayingBtn	.getBackground();
							if (mAnimation.isRunning())mAnimation.stop();
							mPlayingBtn.setBackgroundResource(R.drawable.fmplay);
						}
						mPlayingBtn =( (ViewHolder)arg0.getTag()).FMplay;
						mPlayingBtn.setBackgroundResource(R.anim.playing_anim);
						mAnimation = (AnimationDrawable) mPlayingBtn	.getBackground();
						mAnimation.start();
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
