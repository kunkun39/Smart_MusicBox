package com.changhong.yinxiang.fragment;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.remotecontrol.AudioCtrlAdapter;
import com.changhong.yinxiang.remotecontrol.TVInputDialogFragment;
import com.changhong.yinxiang.utils.Configure;
import com.changhong.yinxiang.view.MyToast;

public class YinXiangRemoteControlFragment extends Fragment
		implements OnClickListener, OnTouchListener, OnGestureListener {
	private static final String TAG = "yinXiang";
	/**
	 * control part
	 */
	ImageView img_d = null;
	View img_v = null;

	private GestureDetector detector;
	RelativeLayout ctrlMain;
	LinearLayout ctrlAudio, volumeBar;
	SeekBar dyAndVolControl;
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
	GridView yinXiaoControl, lightsControl;
	// 音量控制条
	Handler audioHandler = null;

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
		img_d = (ImageView) v.findViewById(R.id.img_d);
		smoothBall = (ImageView) v.findViewById(R.id.ball);
		Button btn_up = (Button) v.findViewById(R.id.up);
		Button btn_down = (Button) v.findViewById(R.id.down);
		Button btn_left = (Button) v.findViewById(R.id.left);
		Button btn_right = (Button) v.findViewById(R.id.right);
		Button btn_center = (Button) v.findViewById(R.id.center);
		ImageView home = (ImageView) v.findViewById(R.id.btn_home);
		ImageView menu = (ImageView) v.findViewById(R.id.btn_menu);
		ImageView backBtn = (ImageView) v.findViewById(R.id.btn_b);
		Button list = (Button) v.findViewById(R.id.btn_list);

		// 功能切换键
		CheckBox funSwitch = (CheckBox) v.findViewById(R.id.function_switch);
		// 音效控件
		ctrlMain = (RelativeLayout) v.findViewById(R.id.control_main);
		ctrlAudio = (LinearLayout) v.findViewById(R.id.control_audio);
		volumeBar = (LinearLayout) v.findViewById(R.id.control_volume);

		Button lights_Center = (Button) v.findViewById(R.id.lightscontrol);
		yinXiaoControl = (GridView) v.findViewById(R.id.audio_yinxiao);
		lightsControl = (GridView) v.findViewById(R.id.audio_light);
		dyAndVolControl = (SeekBar) v.findViewById(R.id.volume_value);

		yinXiaoControl.setSelector(new ColorDrawable(Color.TRANSPARENT));
		lightsControl.setSelector(new ColorDrawable(Color.TRANSPARENT));
		AudioCtrlAdapter adapter = new AudioCtrlAdapter(getActivity(), 6,
				(int) (width * 0.2f), (int) (height * 0.135f));
		yinXiaoControl.setAdapter(adapter);
		adapter = new AudioCtrlAdapter(getActivity(), 4, (int) (width * 0.27f),
				(int) (height * 0.16));
		lightsControl.setAdapter(adapter);

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

		ctrlMain.setOnTouchListener(this);
		ctrlMain.setOnClickListener(this);

		// 功能切换键
		funSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					//更多功能设置
					ctrlMain.setVisibility(View.GONE);
					ctrlAudio.setVisibility(View.VISIBLE);
				} else {
					//常用功能设置
					ctrlMain.setVisibility(View.VISIBLE);
					ctrlAudio.setVisibility(View.GONE);
				}
			}
		});

		home.setOnClickListener(this);
		home.setOnTouchListener(this);
		menu.setOnClickListener(this);
		menu.setOnTouchListener(this);
		backBtn.setOnClickListener(this);
		backBtn.setOnTouchListener(this);

		// 灯光控制
		lights_Center.setOnClickListener(this);
		lights_Center.setOnTouchListener(this);
		lightsControl.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MyApplication.vibrator.vibrate(100);
				int resID = matchLightResID(position);
				if (resID > 0) {
					lightsControl.setBackgroundResource(resID);
					String cmdStr = Configure.getAudioSettingCMD(resID);
					ClientSendCommandService.msg = "key:" + cmdStr;
					ClientSendCommandService.handler.sendEmptyMessage(1);
				}
			}
		});

		// 音效控制
		yinXiaoControl.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MyApplication.vibrator.vibrate(100);
				int resID = matchYinxiaoResID(position);
				if (resID > 0) {
					yinXiaoControl.setBackgroundResource(resID);
					String cmdStr = Configure.getAudioSettingCMD(resID);
					ClientSendCommandService.msg = "key:" + cmdStr;
					ClientSendCommandService.handler.sendEmptyMessage(1);
				}
			}
		});
		centerPoint.set((180.25f - 35.5f) * density, (343.25f - 35.5f)
				* density);

		audioHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:// 定时关闭音量控制条
					if (View.VISIBLE == volumeBar.getVisibility()) {
						volumeBar.setVisibility(View.GONE);
					}
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}

		};
	}

	/***************************************************** 系统方法重载部分 ***********************************************/

	@Override
	public void onClick(View v) {
		
		if (View.VISIBLE == volumeBar.getVisibility()) return;
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
		case R.id.power:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:power";
			break;

		case R.id.lightscontrol:
			MyApplication.vibrator.vibrate(100);
			ClientSendCommandService.msg = "key:lightscontrol";
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
			break;
		case MotionEvent.ACTION_UP:

			// 移除长按键操作
			mHandler1.removeCallbacks(mRunnable);
			endPoint.set(event.getX(), event.getY());
			moveX = endPoint.x - startPoint.x;
			moveY = endPoint.y - startPoint.y;

			if (Math.abs(moveX) >= Math.abs(moveY)) {
				if (moveX >= 120) {
					ClientSendCommandService.msg = "key:volumeup";
					setVolumeAndDy("key:volumeup");
					moveFocus(moveX);
//					v.setId(0);
					MyApplication.vibrator.vibrate(100);
				}
				if (moveX <= -120) {
					ClientSendCommandService.msg = "key:volumedown";
					setVolumeAndDy("key:volumedown");
					moveFocus(moveX);
//					v.setId(0);
					MyApplication.vibrator.vibrate(100);
				}
			} else {
				if (moveY >= 120) {
					ClientSendCommandService.msg = "key:dydown";
					setVolumeAndDy("key:dydown");
					moveFocus(moveY);
//					v.setId(0);
					MyApplication.vibrator.vibrate(100);
				}
				if (moveY <= -120) {
					ClientSendCommandService.msg = "key:dyup";
					setVolumeAndDy("key:dyup");
					moveFocus(moveY);
//					v.setId(0);
					MyApplication.vibrator.vibrate(100);
				}
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

		case R.id.btn_menu:
		case R.id.btn_home:
		case R.id.btn_b:
			LongKeyValue = "";
			break;

		case R.id.lightscontrol:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				LongKeyValue = "";
				lightsControl
						.setBackgroundResource(R.drawable.lightscontroller);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				lightsControl
						.setBackgroundResource(R.drawable.lightscontroller);
			}
			break;

		case R.id.function_switch:
			// 功能切换按钮
			if (View.GONE == ctrlMain.getVisibility()) {
				ctrlMain.setVisibility(View.VISIBLE);
				ctrlAudio.setVisibility(View.GONE);
			} else {
				ctrlMain.setVisibility(View.GONE);
				ctrlAudio.setVisibility(View.VISIBLE);
			}
			break;
		default:
			break;
		}
	
		return false;
	}

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
				ClientSendCommandService.msg = "key:volumeup";
				setVolumeAndDy("key:volumeup");
				moveFocus(Math.abs(xMoveDistenace));
				MyApplication.vibrator.vibrate(100);

			} else if (xMoveDistenace < 0 && Math.abs(xMoveDistenace) > 120) {
				ClientSendCommandService.msg = "key:volumedown";
				setVolumeAndDy("key:volumedown");
				moveFocus(Math.abs(xMoveDistenace));
				MyApplication.vibrator.vibrate(100);

			}
		} else {
			if (yMoveDistenace > 0 && Math.abs(yMoveDistenace) > 120) {
				ClientSendCommandService.msg = "key:dydown";
				setVolumeAndDy("key:dydown");
				moveFocus(Math.abs(yMoveDistenace));
				MyApplication.vibrator.vibrate(100);

			} else if (yMoveDistenace < 0 && Math.abs(yMoveDistenace) > 120) {
				ClientSendCommandService.msg = "key:dyup";
				setVolumeAndDy("key:dyup");
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
				ClientSendCommandService.msg = "key:volumeup";
				moveFocus(Math.abs(xMoveDistenace));

			} else if (xMoveDistenace < 0 && Math.abs(xMoveDistenace) > 400) {
				ClientSendCommandService.msg = "key:volumedown";
				moveFocus(Math.abs(xMoveDistenace));

			}
		} else {
			if (yMoveDistenace > 0 && Math.abs(yMoveDistenace) > 400) {
				ClientSendCommandService.msg = "key:dydown";
				moveFocus(Math.abs(yMoveDistenace));

			} else if (yMoveDistenace < 0 && Math.abs(yMoveDistenace) > 400) {
				ClientSendCommandService.msg = "key:dyup";
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

	private void moveBall(float fromXDelta, float toXDelta, float fromYDelta,
			float toYDelta) {
		TranslateAnimation animation = new TranslateAnimation(fromXDelta,
				toXDelta, fromYDelta, toYDelta);
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

	/**
	 * 匹配音效资源ID
	 */

	private int matchYinxiaoResID(int position) {
		int resID = -1;
		if (position < Configure.yinxiao_resID.length) {
			resID = Configure.yinxiao_resID[position];
		}
		return resID;
	}

	/**
	 * 匹配灯光资源ID
	 */

	private int matchLightResID(int position) {
		int resID = -1;
		if (position < Configure.light_resID.length) {
			resID = Configure.light_resID[position];
		}
		return resID;
	}

	/**
	 * 音量 && 低音设置
	 * 
	 * @param cmd
	 *            -1=降低 +1=提高
	 */
	private void setVolumeAndDy(String cmd) {

		//	限定只有在遥控器主界面下才能设置
		if(View.VISIBLE  ==  ctrlAudio.getVisibility())return;
		
		String notice = "";
		// 显示音量设置条
		if (View.GONE == volumeBar.getVisibility()) {
			volumeBar.setVisibility(View.VISIBLE);
		}
		audioHandler.sendEmptyMessageDelayed(1, 5000);

		// 设置音量条的值
		int curValue = dyAndVolControl.getProgress();
		if (cmd.equals("key:volumedown")) {
			curValue -= 2;
			notice = "音量  减";
		} else if (cmd.equals("key:dydown")) {
			curValue -= 2;
			notice = "低音  减";
		} else if (cmd.equals("key:volumeup")) {
			curValue += 2;
			notice = "音量  加";
		} else if (cmd.equals("key:dyup")) {
			curValue += 2;
			notice = "低音  加";
		}
		dyAndVolControl.setProgress(curValue);
		MyToast.show(getActivity(), notice);
	}

}
