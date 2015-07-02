package com.changhong.tvserver;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class TCMediaPlayer extends Activity implements OnPreparedListener,
														OnCompletionListener,
														OnErrorListener,
														OnInfoListener
{
	private final String TAG = "TCMediaPlayer";
	public static int progress=0;
	private SurfaceView surfaceView;
	private SeekBar skbProgress;
	private Player player;
	LinearLayout banner=null;
	ImageView buffingImageView = null;
	/**
     * 播放地址索引值
     */
	private int index = 0;
//    private String mVideoSource = null;
	
    /**
     * 播放的信息处理
     */
    public static EventHandler mEventHandler;
    private HandlerThread mHandlerThread;
    
    /**
     * 播放状态
     */
    private enum PLAYER_STATUS {
        PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
    }

    private final Object SYNC_Playing = new Object();
    private final int EVENT_PLAY = 0;
    private final int EVENT_START = 1;
    private final int EVENT_STOP = 2;
    private final int EVENT_SEEKTO = 3;
    private final int EVENT_FINISH = 4;
    private WakeLock mWakeLock = null;
    private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;

    /**
     * 记录播放位置
     */
    private int mLastPos = 0;
    private int stat = -1;
	
	/*
	 * 提示语
	 */
	public static String websiteDataWrong = "数据获取失败,退出播放！"; 
	public static String noPlayUrl = "播放地址为空,退出播放！"; 
	public static String noNetwork = "无可用的网络连接，退出播放！";
	public static String moiveUnavaiable = "该时间节目尚未播放";
	public static String playError = "播放出错，退出播放！";
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.media_player);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
		
		surfaceView = (SurfaceView) this.findViewById(R.id.mplay_video_surface);
		buffingImageView = (ImageView)findViewById(R.id.buffing);
		
		skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
		skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
		//显示进度条
		buffingImageView.setVisibility(View.INVISIBLE);
//		Uri uriPath = getIntent().getData();
//        if (null != uriPath) {
//            String scheme = uriPath.getScheme();
//            if (null != scheme) {
//                mVideoSource = uriPath.toString();
//            } else {
//                mVideoSource = uriPath.getPath();
//            }
//        }
		player = new Player(TCMediaPlayer.this,surfaceView, skbProgress,buffingImageView);
		
		/**
         * 开启后台事件处理线程
         */
        mHandlerThread = new HandlerThread("event handler thread", Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mEventHandler = new EventHandler(mHandlerThread.getLooper());
		
	}

	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener 
	{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) 
		{
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) 
		{
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar)
		{
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		switch(keyCode){
		case KeyEvent.KEYCODE_DPAD_LEFT:
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			break;
		case KeyEvent.KEYCODE_BACK:
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
    protected void onPause() {
        super.onPause();
        stat = 1;
        Log.v(TAG, "onPause");
        /**
         * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
         */
        if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
            mLastPos = player.mediaPlayer.getCurrentPosition();
//            player.mediaPlayer.stopPlayback();
        }
        onDestroy();
    }

	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (null != mWakeLock && (!mWakeLock.isHeld())) {
            mWakeLock.acquire();
        }
		/**
         * 发起一次播放任务,当然您不一定要在这发起
         */
        mEventHandler.sendEmptyMessage(EVENT_PLAY);
	}
	
	@Override
    protected void onStop() {
        super.onStop();

        mHandlerThread.quit();
        Log.v(TAG, "onStop");

        finish();
    }

	public void onDestroy() {
		super.onDestroy();
		/**
         * 结束后台事件处理线程
         */
        mHandlerThread.quit();
        Log.v(TAG, "onDestroy");
	}

//	protected boolean isNetworkConnected(){
//		ConnectivityManager mConnectivityManager = (ConnectivityManager)TCMediaPlayer.
//				this.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
//		if(mNetworkInfo!=null){
//			return mNetworkInfo.isAvailable();
//		}
//		else {	
//			return false;
//		}
//	}
	
	/**
     * *******************************************播放器设置部分*******************************************************
     */


    public class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_PLAY:
                    /**
                     * 如果已经播放了，等待上一次播放结束
                     */
                    if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
                        synchronized (SYNC_Playing) {
                            try {
                                SYNC_Playing.wait();
                                Log.v(TAG, "wait player status to idle");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    /**
                     * 设置播放url
                     */
                    Log.v(TAG, TVSocketControllerService.vedios.get(index));
					player.playUrl(TVSocketControllerService.vedios.get(index));
					/**
                     * 设置监听
                     */
//					player.mediaPlayer.setOnPreparedListener(TCMediaPlayer.this);
//					player.mediaPlayer.setOnCompletionListener(TCMediaPlayer.this);
//					player.mediaPlayer.setOnErrorListener(TCMediaPlayer.this);
//					player.mediaPlayer.setOnInfoListener(TCMediaPlayer.this);
                    /**
                     * 续播，如果需要如此
                     */
                    if (mLastPos > 0) {
                    	player.mediaPlayer.seekTo(mLastPos);
                        mLastPos = 0;
                    }
                    /**
                     * 显示或者隐藏缓冲提示
                     */
//                    player.showCacheInfo(true);
                    /**
                     * 开始播放
                     */
//                    player.mediaPlayer.start();

                    mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
                    break;
                case EVENT_START:
                	player.mediaPlayer.start();
                    break;
                case EVENT_STOP:
                	player.mediaPlayer.pause();
                    break;
                case EVENT_SEEKTO:
                    String message = (String) msg.obj;
                    int currentPosition = Integer.valueOf(message.split(":")[2]);
                    player.mediaPlayer.seekTo(currentPosition);
                    break;
                case EVENT_FINISH:
                	if(index<TVSocketControllerService.vedios.size()-1){
                		//播放下一个视频
                		index++;
                		/**
                         * 设置播放url
                         */
                        Log.v(TAG, TVSocketControllerService.vedios.get(index));
    					player.playUrl(TVSocketControllerService.vedios.get(index));
                	}else{
                		finish();
                	}
                	break;
                default:
                    break;
            }
        }
    }

	@Override
	public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

        switch (arg1) {
            /**
             * 开始缓冲
             */
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                break;
            /**
             * 结束缓冲
             */
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                break;
            default:
                break;
        }
        return false;
    
	}

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
        Log.v(TAG, "onError");
        synchronized (SYNC_Playing) {
            SYNC_Playing.notify();
        }
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        return true;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub

        Log.v(TAG, "onCompletion");

        /**
         * play complete, notify boardcast send command to client
         */
        TVSocketControllerService.STOP_PLAY_TAG = 1;

        synchronized (SYNC_Playing) {
            SYNC_Playing.notify();
        }
        if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
            /**
             * auto play finished stat = -1
             */
            if (stat == -1) {
                finish();
            }
        }
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
    
		
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onPrepared");
        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
	}
	
}
