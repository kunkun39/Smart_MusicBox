package com.changhong.yinxiang.fragment;

import android.os.SystemClock;

import com.baidu.voicerecognition.android.Candidate;
import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.changhong.baidu.BaiDuVoiceChannelControlDialog;
import com.changhong.baidu.BaiDuVoiceConfiguration;
import com.changhong.common.domain.AppInfo;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import android.os.Bundle;
import android.os.Handler;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.yinxiang.R;
import com.changhong.yinxiang.remotecontrol.TVInputDialogFragment;
import com.changhong.yinxiang.utils.YuYingWordsUtils;
import com.changhong.yinxiang.view.AudioControlDialog;
import com.changhong.yinxiang.view.DYControlDialog;
import com.changhong.yinxiang.view.LightsControlDialog;
import com.changhong.yinxiang.view.TVNumInputDialog;
import com.changhong.yinxiang.view.YinXiaoControlDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YinXiangRemoteControlFragment extends TVInputDialogFragment
		implements OnClickListener, OnTouchListener, OnGestureListener {
	private static final String TAG = "TVhelper";
	// private BidirSlidingLayout bidirSlidingLayout;
	/**
	 * control part
	 */
	ImageView img_d = null;
	View img_v = null;

	private GestureDetector detector;

	/**
	 * server ip part
	 */
	// private BoxSelectAdapter adapter = null;
	// public static TextView title = null;
	// private ListView clients = null;
	// private Button list;

	private String LongKeyValue = null;
	private PointF startPoint = new PointF();
	private PointF endPoint = new PointF();

	// 数字键盘
	TVNumInputDialog numInputDialog = null;
	// 音效键盘
	YinXiaoControlDialog yinXiaoControlDialog = null;
	// 灯光键盘
	LightsControlDialog lightsControlDialog = null;
	// 音量
	AudioControlDialog audioControlDialog = null;
	// 低音
	DYControlDialog dyControlDialog = null;
	// 长按键
	Handler mHandler1 = new Handler();
	Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			if (StringUtils.hasLength(LongKeyValue)) {
				MyApplication.vibrator.vibrate(30);
				ClientSendCommandService.msg = LongKeyValue;
				ClientSendCommandService.handler.sendEmptyMessage(1);
			}
			mHandler1.postDelayed(mRunnable, 150);
		}
	};

	ImageView smoothBall;
	private PointF centerPoint = new PointF();
	int width, height;

	/************************************************** 百度语音换台部分 **************************************************/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_remote_control,
				container, false);
		initView(view);
		return view;
	}

	public void initView(View v) {

		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		height = metric.heightPixels; // 屏幕高度（像素）
		float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
		detector = new GestureDetector(this);
		// bidirSlidingLayout = (BidirSlidingLayout)
		// findViewById(R.id.bidir_sliding_layout);
		img_d = (ImageView) v.findViewById(R.id.img_d);
		img_v = v.findViewById(R.id.img_volume);
		// title = (TextView) v.findViewById(R.id.title);
		// clients = (ListView) v.findViewById(R.id.clients);
		// list = (Button) v.findViewById(R.id.btn_list);

		smoothBall = (ImageView) v.findViewById(R.id.ball);
		Button btn_up = (Button) v.findViewById(R.id.up);
		Button btn_down = (Button) v.findViewById(R.id.down);
		Button btn_left = (Button) v.findViewById(R.id.left);
		Button btn_right = (Button) v.findViewById(R.id.right);
		Button btn_center = (Button) v.findViewById(R.id.center);
		ImageView btn_v2 = (ImageView) v.findViewById(R.id.volume2);
		ImageView btn_v1 = (ImageView) v.findViewById(R.id.volume1);
		ImageView btn_v3 = (ImageView) v.findViewById(R.id.volume3);
		ImageView btn_vlight = (ImageView) v.findViewById(R.id.vlight);
		ImageView home = (ImageView) v.findViewById(R.id.btn_home);
		ImageView menu = (ImageView) v.findViewById(R.id.btn_menu);
		ImageView backBtn = (ImageView) v.findViewById(R.id.btn_b);
		Button list = (Button) v.findViewById(R.id.btn_list);
		btn_up.setOnTouchListener(this);
		btn_up.setOnClickListener(this);
		btn_down.setOnTouchListener(this);
		btn_down.setOnClickListener(this);
		btn_left.setOnTouchListener(this);
		btn_left.setOnClickListener(this);
		btn_right.setOnTouchListener(this);
		btn_right.setOnClickListener(this);
		btn_center.setOnTouchListener(this);
		btn_center.setOnClickListener(this);
		btn_v2.setOnTouchListener(this);
		btn_v2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MyApplication.vibrator.vibrate(100);
				if (dyControlDialog != null && !dyControlDialog.isShowing()) {
					dyControlDialog.show();
				}
			}
		});
		btn_v1.setOnTouchListener(this);
		btn_v1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MyApplication.vibrator.vibrate(100);
				if (audioControlDialog != null
						&& !audioControlDialog.isShowing()) {
					audioControlDialog.show();
				}
			}
		});

		btn_v3.setOnTouchListener(this);
		btn_v3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				if (yinXiaoControlDialog != null
						&& !yinXiaoControlDialog.isShowing()) {
					yinXiaoControlDialog.show();
				}
			}
		});
		btn_vlight.setOnTouchListener(this);
		btn_vlight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				if (lightsControlDialog != null
						&& !lightsControlDialog.isShowing()) {
					lightsControlDialog.show();
				}
			}
		});

//		btn_vnum.setOnTouchListener(this);
//		btn_vnum.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				MyApplication.vibrator.vibrate(100);
//				if (numInputDialog != null && !numInputDialog.isShowing()) {
//					numInputDialog.show();
//				}
//			}
//		});

		// back.setOnTouchListener(this);
		home.setOnClickListener(this);
		home.setOnTouchListener(this);
		menu.setOnClickListener(this);
		menu.setOnTouchListener(this);
		backBtn.setOnClickListener(this);
		backBtn.setOnTouchListener(this);
		// back.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// MyApplication.vibrator.vibrate(100);
		// // getActivity().finish();
		// getActivity().onBackPressed();
		// }
		// });
		// power.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// MyApplication.vibrator.vibrate(100);
		// Dialog dialog = new AlertDialog.Builder(getActivity())
		// .setTitle("是否确定关机？")
		// .setPositiveButton("是", new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// ClientSendCommandService.msg = "key:power";
		// ClientSendCommandService.handler.sendEmptyMessage(1);
		// }
		// })
		// .setNegativeButton("否", new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.cancel();
		// }
		// })
		// .create();
		// dialog.show();
		// }
		// });

		// adapter = new BoxSelectAdapter(getActivity(),
		// ClientSendCommandService.serverIpList);
		// clients.setAdapter(adapter);
		// clients.setOnTouchListener(new View.OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// clients.setVisibility(View.GONE);
		// return false;
		// }
		// });
		// clients.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		// adapter.notifyDataSetChanged();
		// ClientSendCommandService.serverIP =
		// ClientSendCommandService.serverIpList.get(arg2);
		// ClientSendCommandService.titletxt=ClientSendCommandService.getCurrentConnectBoxName();
		// title.setText(ClientSendCommandService.getCurrentConnectBoxName());
		// ClientSendCommandService.handler.sendEmptyMessage(2);
		// onUpdate();
		// clients.setVisibility(View.GONE);
		// }
		// });
		//
		// list.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// MyApplication.vibrator.vibrate(100);
		// if (ClientSendCommandService.serverIpList.isEmpty()) {
		// Toast.makeText(getActivity(), "没有发现长虹智能机顶盒，请确认盒子和手机连在同一个路由器上",
		// Toast.LENGTH_LONG).show();
		// } else {
		// clients.setVisibility(View.VISIBLE);
		// }
		// }
		// });

		// bidirSlidingLayout.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// bidirSlidingLayout.closeRightMenu();
		// }
		// });
		if (numInputDialog == null) {
			numInputDialog = new TVNumInputDialog(getActivity());
			numInputDialog.setCanceledOnTouchOutside(false);
			numInputDialog.btn0.setOnClickListener(this);
			numInputDialog.btn1.setOnClickListener(this);
			numInputDialog.btn2.setOnClickListener(this);
			numInputDialog.btn3.setOnClickListener(this);
			numInputDialog.btn4.setOnClickListener(this);
			numInputDialog.btn5.setOnClickListener(this);
			numInputDialog.btn6.setOnClickListener(this);
			numInputDialog.btn7.setOnClickListener(this);
			numInputDialog.btn8.setOnClickListener(this);
			numInputDialog.btn9.setOnClickListener(this);
			numInputDialog.btnOk.setOnClickListener(this);
			numInputDialog.btnCancle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MyApplication.vibrator.vibrate(100);
					if (numInputDialog != null && numInputDialog.isShowing()) {
						numInputDialog.dismiss();
					}
				}
			});
		}
		if (yinXiaoControlDialog == null) {
			yinXiaoControlDialog = new YinXiaoControlDialog(getActivity());
			yinXiaoControlDialog.setCanceledOnTouchOutside(true);
			yinXiaoControlDialog.YX_game.setOnClickListener(this);
			yinXiaoControlDialog.YX_game.setOnTouchListener(this);
			yinXiaoControlDialog.YX_movie.setOnClickListener(this);
			yinXiaoControlDialog.YX_movie.setOnTouchListener(this);
			yinXiaoControlDialog.YX_music.setOnClickListener(this);
			yinXiaoControlDialog.YX_music.setOnTouchListener(this);
			yinXiaoControlDialog.YX_tv.setOnClickListener(this);
			yinXiaoControlDialog.YX_tv.setOnTouchListener(this);
			yinXiaoControlDialog.YX_xt.setOnClickListener(this);
			yinXiaoControlDialog.YX_xt.setOnTouchListener(this);
			yinXiaoControlDialog.YX_yd.setOnClickListener(this);
			yinXiaoControlDialog.YX_yd.setOnTouchListener(this);
		}
		if (lightsControlDialog == null) {
			lightsControlDialog = new LightsControlDialog(getActivity());
			lightsControlDialog.setCanceledOnTouchOutside(true);
			lightsControlDialog.lights_controller.setOnClickListener(this);
			lightsControlDialog.lights_controller.setOnTouchListener(this);
			lightsControlDialog.lights_down.setOnClickListener(this);
			lightsControlDialog.lights_down.setOnTouchListener(this);
			lightsControlDialog.lights_moon.setOnClickListener(this);
			lightsControlDialog.lights_moon.setOnTouchListener(this);
			lightsControlDialog.lights_sun.setOnClickListener(this);
			lightsControlDialog.lights_sun.setOnTouchListener(this);
			lightsControlDialog.lights_up.setOnClickListener(this);
			lightsControlDialog.lights_up.setOnTouchListener(this);
		}
		if (audioControlDialog == null) {
			audioControlDialog = new AudioControlDialog(getActivity());
			audioControlDialog.setCanceledOnTouchOutside(true);
			audioControlDialog.btnAudiodown.setOnClickListener(this);
			audioControlDialog.btnAudiodown.setOnTouchListener(this);
			audioControlDialog.btnAudioup.setOnClickListener(this);
			audioControlDialog.btnAudioup.setOnTouchListener(this);
		}
		if (dyControlDialog == null) {
			dyControlDialog = new DYControlDialog(getActivity());
			dyControlDialog.setCanceledOnTouchOutside(true);
			dyControlDialog.btnDYdown.setOnClickListener(this);
			dyControlDialog.btnDYdown.setOnTouchListener(this);
			dyControlDialog.btnDYup.setOnClickListener(this);
			dyControlDialog.btnDYup.setOnTouchListener(this);
		}
		centerPoint.set((180.25f - 35.5f) * density, (343.25f - 35.5f)* density);

		// 长按触发语音换台功能
		btn_center.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				MyApplication.vibrator.vibrate(100);

				/**
				 * stop first, because last action maybe not finished
				 */
				recognitionClient.stopVoiceRecognition();
				/**
				 * 语音的配置
				 */
				VoiceRecognitionConfig config = BaiDuVoiceConfiguration
						.getVoiceRecognitionConfig();
				/**
				 * 下面发起识别
				 */
				int code = recognitionClient.startVoiceRecognition(
						recogListener, config);
				if (code != VoiceRecognitionClient.START_WORK_RESULT_WORKING) {
					Toast.makeText(getActivity(), "网络连接出错，请重新尝试",
							Toast.LENGTH_LONG).show();
				}

				return true;
			}
		});

		initBaiduConfiguration();
	}

	/***************************************************** 系统方法重载部分 ***********************************************/

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.up:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:up";
			break;
		case R.id.down:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:down";
			break;
		case R.id.left:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:left";
			break;
		case R.id.right:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:right";
			break;
		case R.id.center:
		case R.id.numok:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:ok";
			break;
		case R.id.btn_b:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:back";
			break;
		case R.id.btn_menu:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:menu";
			break;
		case R.id.btn_home:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:home";
			break;
		case R.id.audioup:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:volumeup";
			break;
		case R.id.audiodown:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:volumedown";
			break;
		case R.id.power:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:power";
			break;
		case R.id.num0:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:0";
			break;
		case R.id.num1:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:1";
			break;
		case R.id.num2:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:2";
			break;
		case R.id.num3:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:3";
			break;
		case R.id.num4:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:4";
			break;
		case R.id.num5:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:5";
			break;
		case R.id.num6:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:6";
			break;
		case R.id.num7:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:7";
			break;
		case R.id.num8:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:8";
			break;
		case R.id.num9:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:9";
			break;
		case R.id.yinxiao_movie:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:yxmovie";
			break;
		case R.id.yinxiao_tv:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:yxtv";
			break;
		case R.id.yinxiao_music:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:yxmusic";
			break;
		case R.id.yinxiao_game:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:yxgame";
			break;
		case R.id.yinxiao_yd:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:yxyd";
			break;
		case R.id.yinxiao_xt:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:yxxt";
			break;
		case R.id.lightsup:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:lightsup";
			break;
		case R.id.lightsdown:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:lightsdown";
			break;
		case R.id.lightsmoon:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:lightsmoon";
			break;
		case R.id.lightssun:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:lightssun";
			break;
		case R.id.lightscontrol:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:lightscontrol";
			break;
		case R.id.dydown:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:dydown";
			break;
		case R.id.dyup:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:dyup";
			break;
		default:
			ClientSendCommandService.msg = "";
			break;
		}
		ClientSendCommandService.handler.sendEmptyMessage(1);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			startPoint.set(event.getX(), event.getY());
			// 1秒后如果不移动不弹起按键 就执行 长按键操作
			mHandler1.postDelayed(mRunnable, 500);
			break;
		case MotionEvent.ACTION_MOVE:
			endPoint.set(event.getX(), event.getY());
			float moveX = endPoint.x - startPoint.x;
			float moveY = endPoint.y - startPoint.y;

			// 移动距离过大判定不是长按键取消长按键操作
			if (Math.abs(moveX) > 80 || Math.abs(moveY) > 80) {
				mHandler1.removeCallbacks(mRunnable);
			}

			if (Math.abs(moveX) >= Math.abs(moveY)) {
				if (moveX >= 400) {
					ClientSendCommandService.msg = "key:right";
					moveFocus(moveX);
				}
				if (moveX <= -400) {
					ClientSendCommandService.msg = "key:left";
					moveFocus(moveX);
				}
			} else {
				if (moveY >= 400) {
					ClientSendCommandService.msg = "key:down";
					moveFocus(moveY);
				}
				if (moveY <= -400) {
					ClientSendCommandService.msg = "key:up";
					moveFocus(moveY);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			// 移除长按键操作
			mHandler1.removeCallbacks(mRunnable);
			endPoint.set(event.getX(), event.getY());
			moveX = endPoint.x - startPoint.x;
			moveY = endPoint.y - startPoint.y;

			if (Math.abs(moveX) >= Math.abs(moveY)) {
				if (moveX >= 120) {
					moveBall(centerPoint.x, width, centerPoint.y, centerPoint.y);
					ClientSendCommandService.msg = "key:right";
					moveFocus(moveX);
					MyApplication.vibrator.vibrate(100);
				}
				if (moveX <= -120) {
					moveBall(centerPoint.x, -100f, centerPoint.y, centerPoint.y);
					ClientSendCommandService.msg = "key:left";
					moveFocus(moveX);
					MyApplication.vibrator.vibrate(100);
				}
			} else {
				if (moveY >= 120) {
					moveBall(centerPoint.x, centerPoint.x, centerPoint.y,
							height);
					ClientSendCommandService.msg = "key:down";
					moveFocus(moveY);
					MyApplication.vibrator.vibrate(100);
				}
				if (moveY <= -120) {
					moveBall(centerPoint.x, centerPoint.x, centerPoint.y, 0f);
					ClientSendCommandService.msg = "key:up";
					moveFocus(moveY);
					MyApplication.vibrator.vibrate(100);
				}
			}

			/**
			 * 语音识别对话结束
			 */
			if (v.getId() == R.id.center) {
				recognitionClient.speakFinish();
			}
			break;
		}

		switch (v.getId()) {
		case R.id.up:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "key:up";
				img_d.setImageResource(R.drawable.tv_control_directory_up);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				img_d.setImageResource(R.drawable.tv_control_direction);
			}
			break;
		case R.id.down:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "key:down";
				img_d.setImageResource(R.drawable.tv_control_directory_down);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				img_d.setImageResource(R.drawable.tv_control_direction);
			}
			break;
		case R.id.left:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "key:left";
				img_d.setImageResource(R.drawable.tv_control_directory_left);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				img_d.setImageResource(R.drawable.tv_control_direction);
			}
			break;
		case R.id.right:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "key:right";
				img_d.setImageResource(R.drawable.tv_control_directory_right);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				img_d.setImageResource(R.drawable.tv_control_direction);
			}
			break;
		case R.id.center:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
				img_d.setImageResource(R.drawable.tv_control_directory_center);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				img_d.setImageResource(R.drawable.tv_control_direction);
			}
			break;
		case R.id.volume2:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
//				img_v.setBackgroundResource(R.drawable.tv_control_menu_volum2);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
//				img_v.setBackgroundResource(R.drawable.tv_control_menu);
			}
			break;
		case R.id.volume1:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
//				img_v.setBackgroundResource(R.drawable.tv_control_menu_volum1);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
//				img_v.setBackgroundResource(R.drawable.tv_control_menu);
			}
			break;
		case R.id.volume3:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
//				img_v.setBackgroundResource(R.drawable.tv_control_menu_tv);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
//				img_v.setBackgroundResource(R.drawable.tv_control_menu);
			}
			break;
		case R.id.vlight:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
//				img_v.setBackgroundResource(R.drawable.tv_control_menu_channel);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
//				img_v.setBackgroundResource(R.drawable.tv_control_menu);
			}
			break;
			
//		case R.id.num:
//			if (event.getAction() == MotionEvent.ACTION_DOWN) {
//				LongKeyValue = "";
//				img_v.setBackgroundResource(R.drawable.tv_control_menu_num);
//			} else if (event.getAction() == MotionEvent.ACTION_UP) {
//				img_v.setBackgroundResource(R.drawable.tv_control_menu);
//			}
//			break;
			
			case R.id.btn_menu:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
//				img_v.setBackgroundResource(R.drawable.tv_control_menu_menu);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
//				img_v.setBackgroundResource(R.drawable.tv_control_menu);
			}
			break;
		case R.id.btn_home:
		case R.id.btn_b:
			LongKeyValue = "";
			break;
		case R.id.yinxiao_movie:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
				yinXiaoControlDialog.imgback
						.setBackgroundResource(R.drawable.yinxiaomovie);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				yinXiaoControlDialog.imgback
						.setBackgroundResource(R.drawable.yinxiao);
			}
			break;
		case R.id.yinxiao_tv:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
				yinXiaoControlDialog.imgback
						.setBackgroundResource(R.drawable.yinxiaotv);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				yinXiaoControlDialog.imgback
						.setBackgroundResource(R.drawable.yinxiao);
			}
			break;
		case R.id.yinxiao_music:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
				yinXiaoControlDialog.imgback
						.setBackgroundResource(R.drawable.yinxiaomusic);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				yinXiaoControlDialog.imgback
						.setBackgroundResource(R.drawable.yinxiao);
			}
			break;
		case R.id.yinxiao_game:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
				yinXiaoControlDialog.imgback
						.setBackgroundResource(R.drawable.yixiaogame);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				yinXiaoControlDialog.imgback
						.setBackgroundResource(R.drawable.yinxiao);
			}
			break;
		case R.id.yinxiao_yd:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
				yinXiaoControlDialog.imgback
						.setBackgroundResource(R.drawable.yinxiaoyd);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				yinXiaoControlDialog.imgback
						.setBackgroundResource(R.drawable.yinxiao);
			}
			break;
		case R.id.yinxiao_xt:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
				yinXiaoControlDialog.imgback
						.setBackgroundResource(R.drawable.yinxiaoxt);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				yinXiaoControlDialog.imgback
						.setBackgroundResource(R.drawable.yinxiao);
			}
			break;
		case R.id.lightsdown:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
				lightsControlDialog.imgback
						.setBackgroundResource(R.drawable.lightsdown);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				lightsControlDialog.imgback
						.setBackgroundResource(R.drawable.lights);
			}
			break;
		case R.id.lightscontrol:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
				lightsControlDialog.imgback
						.setBackgroundResource(R.drawable.lightscontroller);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				lightsControlDialog.imgback
						.setBackgroundResource(R.drawable.lights);
			}
			break;
		case R.id.lightsup:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
				lightsControlDialog.imgback
						.setBackgroundResource(R.drawable.lightsup);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				lightsControlDialog.imgback
						.setBackgroundResource(R.drawable.lights);
			}
			break;
		case R.id.lightssun:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
				lightsControlDialog.imgback
						.setBackgroundResource(R.drawable.lightssun);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				lightsControlDialog.imgback
						.setBackgroundResource(R.drawable.lights);
			}
			break;
		case R.id.lightsmoon:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
				lightsControlDialog.imgback
						.setBackgroundResource(R.drawable.lightsmoon);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				lightsControlDialog.imgback
						.setBackgroundResource(R.drawable.lights);
			}
			break;
		case R.id.audiodown:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "key:volumedown";
				audioControlDialog.imgback
						.setBackgroundResource(R.drawable.audiodown);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				audioControlDialog.imgback
						.setBackgroundResource(R.drawable.audio);
			}
			break;
		case R.id.audioup:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "key:volumeup";
				audioControlDialog.imgback
						.setBackgroundResource(R.drawable.audioup);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				audioControlDialog.imgback
						.setBackgroundResource(R.drawable.audio);
			}
			break;
		case R.id.dydown:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "key:dydown";
				dyControlDialog.imgback
						.setBackgroundResource(R.drawable.dydown);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				dyControlDialog.imgback.setBackgroundResource(R.drawable.dy);
			}
			break;
		case R.id.dyup:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "key:dyup";
				dyControlDialog.imgback.setBackgroundResource(R.drawable.dyup);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				dyControlDialog.imgback.setBackgroundResource(R.drawable.dy);
			}
			break;
		default:
			break;
		}
		return false;
	}

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// return this.detector.onTouchEvent(event);
	// }

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Log.e("Fling", "Fling Happened!");
		float xMoveDistenace = e2.getX() - e1.getX();
		float yMoveDistenace = e2.getY() - e1.getY();

		if (Math.abs(xMoveDistenace) > Math.abs(yMoveDistenace)) {
			if (xMoveDistenace > 0 && Math.abs(xMoveDistenace) > 120) {
				moveBall(centerPoint.x, width, centerPoint.y, centerPoint.y);
				ClientSendCommandService.msg = "key:right";
				moveFocus(Math.abs(xMoveDistenace));
				MyApplication.vibrator.vibrate(100);

			} else if (xMoveDistenace < 0 && Math.abs(xMoveDistenace) > 120) {
				moveBall(centerPoint.x, -100f, centerPoint.y, centerPoint.y);
				ClientSendCommandService.msg = "key:left";
				moveFocus(Math.abs(xMoveDistenace));
				MyApplication.vibrator.vibrate(100);

			}
		} else {
			if (yMoveDistenace > 0 && Math.abs(yMoveDistenace) > 120) {
				moveBall(centerPoint.x, centerPoint.x, centerPoint.y, height);
				ClientSendCommandService.msg = "key:down";
				moveFocus(Math.abs(yMoveDistenace));
				MyApplication.vibrator.vibrate(100);

			} else if (yMoveDistenace < 0 && Math.abs(yMoveDistenace) > 120) {
				moveBall(centerPoint.x, centerPoint.x, centerPoint.y, 0f);
				ClientSendCommandService.msg = "key:up";
				moveFocus(Math.abs(yMoveDistenace));
				MyApplication.vibrator.vibrate(100);

			}
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		float xMoveDistenace = e2.getX() - e1.getX();
		float yMoveDistenace = e2.getY() - e1.getY();

		if (Math.abs(xMoveDistenace) > Math.abs(yMoveDistenace)) {
			if (xMoveDistenace > 0 && Math.abs(xMoveDistenace) > 400) {
				ClientSendCommandService.msg = "key:right";
				moveFocus(Math.abs(xMoveDistenace));

			} else if (xMoveDistenace < 0 && Math.abs(xMoveDistenace) > 400) {
				ClientSendCommandService.msg = "key:left";
				moveFocus(Math.abs(xMoveDistenace));

			}
		} else {
			if (yMoveDistenace > 0 && Math.abs(yMoveDistenace) > 400) {
				ClientSendCommandService.msg = "key:down";
				moveFocus(Math.abs(yMoveDistenace));

			} else if (yMoveDistenace < 0 && Math.abs(yMoveDistenace) > 400) {
				ClientSendCommandService.msg = "key:up";
				moveFocus(Math.abs(yMoveDistenace));

			}
		}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	private void moveFocus(float length) {
		ClientSendCommandService.handler.sendEmptyMessage(1);
	}

	private void moveBall(float fromXDelta, float toXDelta, float fromYDelta,float toYDelta) {
		TranslateAnimation animation = new TranslateAnimation(fromXDelta,toXDelta, fromYDelta, toYDelta);
		animation.setDuration(500);
		animation.setInterpolator(new AccelerateInterpolator());
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				smoothBall.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				smoothBall.clearAnimation();
				smoothBall.setVisibility(View.INVISIBLE);
			}
		});
		smoothBall.startAnimation(animation);
	}

	@Override
	public void onResume() {
		// if (ClientSendCommandService.titletxt != null) {
		// title.setText(ClientSendCommandService.titletxt);
		// }
		super.onResume();
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// return true;
	// }

	@Override
	public void onStop() {
		super.onStop();
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// switch (keyCode) {
	// case KeyEvent.KEYCODE_BACK:
	// break;
	// // case KeyEvent.KEYCODE_MENU:
	// // bidirSlidingLayout.clickSideMenu();
	// // return true;
	// default:
	// break;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/********************************************** 语音部分代码 *********************************************************/

	/**
	 * baidu recognition client, void to init many times, so use static here
	 */
	private static VoiceRecognitionClient recognitionClient;

	/**
	 * 识别回调接口
	 */
	private BaiDuVoiceChannelDialogRecogListener recogListener = new BaiDuVoiceChannelDialogRecogListener();

	/**
	 * 初始化百度的配置
	 */
	private void initBaiduConfiguration() {
		if (recognitionClient == null) {
			recognitionClient = VoiceRecognitionClient
					.getInstance(getActivity());
			recognitionClient.setTokenApis(BaiDuVoiceConfiguration.API_KEY,
					BaiDuVoiceConfiguration.SECRET_KEY);
		}
	}

	/**
	 * 百度语音监听器
	 */
	public class BaiDuVoiceChannelDialogRecogListener implements
			VoiceRecognitionClient.VoiceClientStatusChangeListener {
		/**
		 * 正在识别中
		 */
		private boolean isRecognitioning = false;

		private int recognitioningFailedTimes = 0;

		/**
		 * channel match list, integer value stand for match time, compare han
		 * zi one by one
		 */
		private Map<String, Integer> matchChannel = new HashMap<String, Integer>();

		@Override
		public void onClientStatusChange(int status, Object obj) {
			switch (status) {
			// 语音识别实际开始，这是真正开始识别的时间点，需在界面提示用户说话。
			case VoiceRecognitionClient.CLIENT_STATUS_START_RECORDING:
				isRecognitioning = true;
				break;
			// 检测到语音起点
			case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_START:
				break;
			// 已经检测到语音终点，等待网络返回
			case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_END:
				break;
			// 语音识别完成，显示obj中的结果
			case VoiceRecognitionClient.CLIENT_STATUS_FINISH:
				isRecognitioning = false;
				updateRecognitionResult(obj);
				break;
			// 处理连续上屏
			case VoiceRecognitionClient.CLIENT_STATUS_UPDATE_RESULTS:
				break;
			// 用户取消
			case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
				recognitionClient.stopVoiceRecognition();
				break;
			default:
				break;
			}
		}

		@Override
		public void onError(int errorType, int errorCode) {
			Toast.makeText(getActivity(), "抱歉哟，我们不能识别空指令", Toast.LENGTH_LONG)
					.show();
			isRecognitioning = false;
			recognitionClient.stopVoiceRecognition();
		}

		@Override
		public void onNetworkStatusChange(int status, Object obj) {
			// 这里不做任何操作不影响简单识别
		}

		/**
		 * 将识别结果更新到UI上，搜索模式结果类型为List<String>,输入模式结果类型为List<List<Candidate>>
		 */
		private void updateRecognitionResult(Object result) {
			String recognitionResult = "";
			if (result != null && result instanceof List) {
				List results = (List) result;
				if (results.size() > 0) {
					if (results.get(0) instanceof List) {
						List<List<Candidate>> sentences = (List<List<Candidate>>) result;
						StringBuffer sb = new StringBuffer();
						for (List<Candidate> candidates : sentences) {
							if (candidates != null && candidates.size() > 0) {
								sb.append(candidates.get(0).getWord());
							}
						}
						recognitionResult = sb.toString().replace("。", "");
					} else {
						recognitionResult = results.get(0).toString()
								.replace("。", "");
					}
				}
			}

			/**
			 * used for check yuying laucher is successful or not
			 * <p>
			 * we have two flows: 1 - if text start with "打开"，"启动"，"开启" go to
			 * open box app way 2 - else go to switch channel way
			 */
			boolean hasResult = false;
			if (StringUtils.hasLength(recognitionResult)) {
				/******************************************** 处理用户说的话 ********************************************/

				String commands = YuYingWordsUtils
						.isSearchContainsControl(recognitionResult);
				if (StringUtils.hasLength(commands)) {
					// TODO:流程->主页
					String[] command = StringUtils.delimitedListToStringArray(
							commands, "|");
					if (command.length == 2 && command[0].equals("key:dtv")) {
						ClientSendCommandService.msg = command[0];
						ClientSendCommandService.handler.sendEmptyMessage(1);
						SystemClock.sleep(300);
						ClientSendCommandService.msg = command[1];
						ClientSendCommandService.handler.sendEmptyMessage(1);
					} else {
						for (String cmd : command) {
							ClientSendCommandService.msg = cmd;
							ClientSendCommandService.handler
									.sendEmptyMessage(1);
						}
					}

					hasResult = true;
					Toast.makeText(getActivity(),
							"语音的结果为:" + recognitionResult, Toast.LENGTH_LONG)
							.show();

				} else if (YuYingWordsUtils
						.isSearchContainsAppKeywords(recognitionResult)) {
					// TODO:流程->搜索应用
					recognitionResult = YuYingWordsUtils
							.appSearchWordsConvert(recognitionResult);

					/**
					 * search server side all applications
					 */
					if (ClientSendCommandService.serverAppInfo.isEmpty()) {
						ClientSendCommandService.handler.sendEmptyMessage(6);
						// wait for search channel finish
						while (!ClientSendCommandService.searchApplicationFinished) {
							SystemClock.sleep(500);
						}
					}

					/**
					 * compare the matched app, use char compare one by one
					 */
					matchChannel.clear();
					for (int i = 0; i < recognitionResult.length(); i++) {
						for (int j = 0; j < ClientSendCommandService.serverAppInfo
								.size(); j++) {
							AppInfo info = ClientSendCommandService.serverAppInfo
									.get(j);
							String appName = info.appName;
							if (appName.indexOf(recognitionResult.charAt(i)) >= 0) {
								Integer count = matchChannel.get(String
										.valueOf(j));
								if (count == null) {
									matchChannel.put(String.valueOf(j), 1);
								} else {
									matchChannel.put(String.valueOf(j),
											count + 1);
								}
							}
						}
					}

					/**
					 * get best matched result, the value must bigger than 2 1 -
					 * first compare value which is bigger 2 - if value is equal
					 * compare which is shorter 3 - if length is equal compare
					 * which contains the input string
					 */
					int bestCounter = 0;
					String bestPostion = "";
					for (String position : matchChannel.keySet()) {
						Integer value = matchChannel.get(position);

						if (value >= 2) {
							if (value > bestCounter) {
								bestCounter = value;
								bestPostion = position;
							} else if (value == bestCounter) {
								String bestApp = ClientSendCommandService.serverAppInfo
										.get(Integer.valueOf(bestPostion)).appName;
								String newApp = ClientSendCommandService.serverAppInfo
										.get(Integer.valueOf(position)).appName;

								if (newApp.length() < bestApp.length()) {
									bestPostion = position;
								} else if (newApp.length() == bestApp.length()) {
									int bestIndex = bestApp
											.indexOf(recognitionResult);
									int newIndex = newApp
											.indexOf(recognitionResult);
									if (bestIndex < 0 && newIndex >= 0) {
										bestPostion = position;
									}
								}
							}
						}
					}

					/**
					 * send command to the server to decide which one should
					 * open
					 */
					if (StringUtils.hasLength(bestPostion)) {
						AppInfo info = ClientSendCommandService.serverAppInfo
								.get(Integer.valueOf(bestPostion));
						ClientSendCommandService.msg = "app_open:"
								+ info.packageName;
						ClientSendCommandService.handler.sendEmptyMessage(1);
						Log.e(TAG, "message:" + "app_open:" + info.packageName);

						Toast.makeText(
								getActivity(),
								"应用的结果为:" + info.appName + "\n语音的结果为:"
										+ recognitionResult, Toast.LENGTH_LONG)
								.show();
						hasResult = true;
					} else {
						hasResult = false;
					}

				} else {

				}
				/******************************************** 结束处理用户说的话 ******************************************/
			}

			if (!hasResult) {
				recognitioningFailedTimes = recognitioningFailedTimes + 1;
				if (recognitioningFailedTimes == 3) {
					recognitioningFailedTimes = 0;
					BaiDuVoiceChannelControlDialog yuYingHelpDialog = new BaiDuVoiceChannelControlDialog(
							getActivity());
					yuYingHelpDialog.show();
				} else {
					Toast.makeText(getActivity(),
							"抱歉哟，目前还不支持该指令:" + recognitionResult,
							Toast.LENGTH_LONG).show();
				}
			} else {
				recognitioningFailedTimes = 0;
			}
		}
	}
}
