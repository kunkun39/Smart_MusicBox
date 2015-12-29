package com.changhong.yinxiang.fragment;

import java.util.HashMap;

import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.service.NetworkConnectChangedReceiver;
import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;

public class YinXiangFMFragment extends Fragment {

	/**
	 * message handler
	 */
	public static Handler mHandler = null;

	/************************************************** 频道部分 *******************************************************/

	private GridView FMlist = null;
	private FMAdapter adapter = null;

	/********************************************************** play按钮 ***********************************************************/
	private int curPlayingIndex;

	private HashMap<Integer, View> viewMap = null;
	private View mLastView = null;
	private int mLastPosition = -1;
	private ImageView mPlayingImage = null;
	private FMUpdateReceiver fmUpdateReceiver = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 请求Fm列表信息
		if (ClientSendCommandService.handler != null) {
			ClientSendCommandService.handler.sendEmptyMessage(2);
		}
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
		viewMap = new HashMap<Integer, View>();
		FMlist = (GridView) v.findViewById(R.id.fmlist);
		// 取消GridView中Item选中时默认的背景色
		FMlist.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new FMAdapter(getActivity());
		FMlist.setAdapter(adapter);
		FMlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.changeImageMode(position, true);
			}
		});
		setListViewPos(ClientSendCommandService.curFMIndex);

		// mHandler = new Handler() {
		// public void handleMessage(Message msg1) {
		// if (ClientSendCommandService.searchFMFinished) {
		// adapter.notifyDataSetChanged();
		// } else {
		// mHandler.sendMessageDelayed(new Message(), 1000);
		// }
		// }
		// };
		// mHandler.sendEmptyMessage(1);

	}

	@Override
	public void onResume() {
		super.onResume();
		regFMInforBroadcastRec();

	}

	@Override
	public void onPause() {
		unregisterFMInfor();
		super.onDestroy();
	}

	private void setListViewPos(int pos) {

		if (pos < 0)
			return;
		FMlist.setSelection(pos);
	}

	private void regFMInforBroadcastRec() {
		if (null == fmUpdateReceiver) {
			fmUpdateReceiver = new FMUpdateReceiver();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(ClientSendCommandService.ACTION_FMINFOR_UPDATE);
		getActivity().registerReceiver(fmUpdateReceiver, filter);
	}

	private void unregisterFMInfor() {
		if (fmUpdateReceiver != null) {
			getActivity().unregisterReceiver(fmUpdateReceiver);
			fmUpdateReceiver = null;
		}
	}

	private class FMUpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ClientSendCommandService.ACTION_FMINFOR_UPDATE)) {
				adapter.changeImageMode(ClientSendCommandService.curFMIndex,
						false);
				FMlist.setSelection(ClientSendCommandService.curFMIndex);
			}
		}

	}

	/**
	 * FM名称
	 */
	private class FMAdapter extends BaseAdapter {

		private Context mContext;
		private int mCurPosition = -1;

		public FMAdapter(Context context) {
			this.mContext = context;
			mCurPosition = ClientSendCommandService.curFMIndex;
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			/**
			 * VIEW HOLDER的配置
			 */
			final ViewHolder vh;
			if (null == convertView) {
				LayoutInflater minflater = LayoutInflater.from(mContext);
				convertView = minflater
						.inflate(R.layout.activity_fm_item, null);
				vh = new ViewHolder();
				vh.FMname = (TextView) convertView.findViewById(R.id.fmtxt);
				vh.FMpause = (ImageView) convertView.findViewById(R.id.btn_fm);
				vh.FMplay = (ImageView) convertView.findViewById(R.id.fmisplay);

				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			if (ClientSendCommandService.serverFMInfo.size() > 0) {

				vh.FMname.setText(ClientSendCommandService.serverFMInfo
						.get(position));
				vh.id = position;

				if (position == mCurPosition) {
					vh.FMpause.setVisibility(View.INVISIBLE);
					vh.FMplay.setVisibility(View.VISIBLE);
				} else {
					vh.FMpause.setVisibility(View.VISIBLE);
					vh.FMplay.setVisibility(View.INVISIBLE);
				}

				Log.e("YDINFOR:: ", "  position=" + position);
			}
			return convertView;
		}

		// public View getView(final int position, View convertView, ViewGroup
		// parent) {
		// /**
		// * VIEW HOLDER的配置
		// */
		// final ViewHolder vh;
		// if (convertView == null) {
		// LayoutInflater minflater=LayoutInflater.from(mContext);
		// convertView = minflater.inflate(R.layout.activity_fm_item, null);
		// }
		// TextView FMname= (TextView) convertView.findViewById(R.id.fmtxt);
		// ImageView FMplay = (ImageView) convertView.findViewById(R.id.btn_fm);
		// if (ClientSendCommandService.serverFMInfo.size() > 0) {
		//
		// FMname.setText(ClientSendCommandService.serverFMInfo .get(position));
		// FMplay.setTag(position);
		// FMplay.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// MyApplication.vibrator.vibrate(100);
		// String
		// serverFMInfor=ClientSendCommandService.serverFMInfo.get(position);
		// ClientSendCommandService.msg = "fm:" +serverFMInfor;
		// ClientSendCommandService.handler.sendEmptyMessage(1);
		//
		// if (null != mPlayingImage && null !=mAnimation) {
		// if (mAnimation.isRunning())mAnimation.stop();
		// mPlayingImage.setBackgroundResource(R.drawable.fmplay);
		// }
		// int pos=Integer.parseInt(arg0.getTag().toString());
		// //检查是否同一电台
		// if(pos != mLastPosition){
		// arg0.setBackgroundResource(R.anim.playing_anim);
		// mAnimation = (AnimationDrawable) arg0.getBackground();
		// mAnimation.start();
		// mPlayingImage =(ImageView) arg0;
		// mLastPosition=position;
		// }
		// }
		// });
		// }
		// return convertView;
		// }

		public void changeImageMode(int position, boolean isSendCommand) {

			if (isSendCommand) {
				MyApplication.vibrator.vibrate(100);
				String serverFMInfor = ClientSendCommandService.serverFMInfo
						.get(position);
				ClientSendCommandService.msg = "fm:" + serverFMInfor;
				ClientSendCommandService.handler.sendEmptyMessage(1);
			}
			// if (mLastView != null) {
			// holder = (ViewHolder) mLastView.getTag();
			// mLastPosition = holder.id;
			// mLastView = null;
			// if (mAnimation.isRunning())
			// mAnimation.stop();
			// holder.FMplay.setBackgroundResource(R.drawable.fmplay);
			// holder.FMname.setTextColor(mContext.getResources().getColor(R.color.white));
			// }
			// holder = (ViewHolder) view.getTag();
			// if (holder.id == position && position != mLastPosition) {
			// mLastPosition = position;
			// mLastView = view;
			// holder.FMname.setTextColor(mContext.getResources().getColor(
			// R.color.tab_textColor_selected));
			// holder.FMplay.setBackgroundResource(R.anim.playing_anim);
			// mAnimation = (AnimationDrawable) holder.FMplay.getBackground();
			// mAnimation.start();
			// } else if(position == mLastPosition){
			// mLastPosition = -1;
			// }

			adapter.setindex(-1);
			adapter.notifyDataSetChanged();

			if (mLastPosition != position) {
				adapter.setindex(position);
				adapter.notifyDataSetChanged();
				mLastPosition = position;
			} else {
				mLastPosition = -1;
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
