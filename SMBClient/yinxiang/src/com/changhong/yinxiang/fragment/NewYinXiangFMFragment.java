package com.changhong.yinxiang.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.service.NetworkConnectChangedReceiver;
import com.changhong.common.system.MyApplication;
import com.changhong.xiami.activity.AlbumListActivity;
import com.changhong.xiami.data.XMMusicData;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.utils.Configure;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiami.music.api.utils.RequestMethods;
import com.xiami.music.model.Radio;
import com.xiami.music.model.RadioCategory;
import com.xiami.sdk.entities.OnlineAlbum;

public class NewYinXiangFMFragment extends Fragment {

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
	private XMMusicData mXMMusicData = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		if (null == mXMMusicData)
			mXMMusicData = XMMusicData.getInstance(getActivity());
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
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Configure.XIAMI_RADIO_CATEGORIES:
					// 获取电台列表信息
					JsonElement jsonData = (JsonElement) msg.obj;
					handlXiamiResponse(jsonData, msg.what);
					break;
				}

			}
		};
		regFMInforBroadcastRec();

	}

	@Override
	public void onResume() {
		super.onResume();
		// 请求Fm列表信息
		requestFMCategory();

	}

	@Override
	public void onPause() {
		unregisterFMInfor();
		super.onDestroy();
	}

	/**
	 * 请求艺人热门歌曲、专辑
	 * 
	 * @param method
	 */
	private void requestFMCategory() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		mXMMusicData.getJsonData(mHandler,
				RequestMethods.METHOD_RADIO_CATEGORIES_OLD, params);
	}

	private void requestFMList(int categoryID) {
		if (categoryID < 0)
			return;
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("category_id", categoryID);
		params.put("limit", 50);
		params.put("page", 1);
		mXMMusicData.getJsonData(mHandler, RequestMethods.METHOD_RADIO_LIST,
				params);
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

	static int index = 0;

	/**
	 * 处理服务器响应列表请求信息
	 * 
	 * @param jsonData
	 */
	private void handlXiamiResponse(JsonElement jsonData, int type) {
		List<RadioCategory> serverFMInfo = mXMMusicData
				.getRadioCategoryOld(jsonData);
		if (null != serverFMInfo) {
			ClientSendCommandService.serverFMInfo.clear();
			for (int i = 0; i < serverFMInfo.size(); i++) {
				ClientSendCommandService.serverFMInfo.add(serverFMInfo.get(i)	.getTypeName());
			}
			adapter.notifyDataSetChanged();
		} else {
			Toast.makeText(getActivity(), "没有获取到FM信息，请确认网络链接正常",
					Toast.LENGTH_SHORT).show();
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
