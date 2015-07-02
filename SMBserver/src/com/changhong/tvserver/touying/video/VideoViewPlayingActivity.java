package com.changhong.tvserver.touying.video;

import com.changhong.tvserver.TVSocketControllerService;
import com.changhong.tvserver.R;
import com.baidu.cyberplayer.core.BMediaController;
import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class VideoViewPlayingActivity extends Activity implements OnPreparedListener,
        OnCompletionListener,
        OnErrorListener,
        OnInfoListener,
        OnPlayingBufferCacheListener {

    private final String TAG = "VideoViewPlayingActivity";

    /**
     * 百度音乐需要设置的KEY
     */
    private String AK = "GcB3uqcVvjzEtbsV8lxBDQ8d";
    private String SK = "jshPsRMEXDTah1rqYO6qLilGkuFrFYKG";

    /**
     * 播放地址
     */
    private String mVideoSource = null;
    public static String playVeidoKey = null;

    /**
     * 播控控件的设置
     */
    public static BVideoView mVV = null;
    private BMediaController mVVCtl = null;
    private RelativeLayout mViewHolder = null;
    private LinearLayout mControllerHolder = null;
    private boolean mIsHwDecode = false;

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
    private WakeLock mWakeLock = null;
    private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
    private int stat = -1;

    /**
     * 记录播放位置
     */
    private int mLastPos = 0;

    /**
     * *******************************************初始化和包房*******************************************************
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vedio_player);
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);

        mIsHwDecode = getIntent().getBooleanExtra("isHW", false);
        Uri uriPath = getIntent().getData();
        if (null != uriPath) {
            String scheme = uriPath.getScheme();
            if (null != scheme) {
                mVideoSource = uriPath.toString();
            } else {
                mVideoSource = uriPath.getPath();
            }
        }
        String[] tokens = mVideoSource.split("/");
        playVeidoKey = tokens[tokens.length - 1];

        /**
         * 初始化UI
         */
        initUI();

        /**
         * 开启后台事件处理线程
         */
        mHandlerThread = new HandlerThread("event handler thread", Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mEventHandler = new EventHandler(mHandlerThread.getLooper());
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        mViewHolder = (RelativeLayout) findViewById(R.id.view_holder);
        mControllerHolder = (LinearLayout) findViewById(R.id.controller_holder);

        /**
         * 设置ak及sk的前16位
         */
        BVideoView.setAKSK(AK, SK);

        /**
         *创建BVideoView和BMediaController
         */
        mVV = new BVideoView(this);
        mVVCtl = new BMediaController(this);
        mViewHolder.addView(mVV);
        mControllerHolder.addView(mVVCtl);

        /**
         * 注册listener
         */
        mVV.setOnPreparedListener(this);
        mVV.setOnCompletionListener(this);
        mVV.setOnErrorListener(this);
        mVV.setOnInfoListener(this);
        mVVCtl.setPreNextListener(mPreListener, mNextListener);

        /**
         * 关联BMediaController
         */
        mVV.setMediaController(mVVCtl);
        /**
         * 设置解码模式
         */
        mVV.setDecodeMode(BVideoView.DECODE_HW);

        /**
         * 设置视频的播放方向
         */
    }

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
                    Log.v(TAG, mVideoSource);
                    mVV.setVideoPath(mVideoSource);
                    //mVV.setCacheBufferSize(1*1024*1024);
                    /**
                     * 续播，如果需要如此
                     */
                    if (mLastPos > 0) {
                        mVV.seekTo(mLastPos);
                        mLastPos = 0;
                    }
                    /**
                     * 显示或者隐藏缓冲提示
                     */
                    mVV.showCacheInfo(true);
                    /**
                     * 开始播放
                     */
                    mVV.start();

                    mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
                    break;
                case EVENT_START:
                    mVV.resume();
                    break;
                case EVENT_STOP:
                    mVV.pause();
                    break;
                case EVENT_SEEKTO:
                    String message = (String) msg.obj;
                    int currentPosition = Integer.valueOf(message.split(":")[2]);
                    mVV.seekTo(currentPosition);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 实现切换示例
     */
    private View.OnClickListener mPreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.v(TAG, "pre btn clicked");
        }
    };

    private View.OnClickListener mNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.v(TAG, "next btn clicked");
        }
    };

    /**
     * *******************************************系统方法重载部分*******************************************************
     */

    @Override
    protected void onPause() {
        super.onPause();
        stat = 1;
        Log.v(TAG, "onPause");
        /**
         * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
         */
        if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
            mLastPos = mVV.getCurrentPosition();
            mVV.stopPlayback();
        }
        onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();


        Log.v(TAG, "onResume");
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

    @SuppressLint("NewApi")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 结束后台事件处理线程
         */
        mHandlerThread.quit();
        Log.v(TAG, "onDestroy");
    }

    @Override
    public boolean onInfo(int what, int extra) {
        switch (what) {
            /**
             * 开始缓冲
             */
            case BVideoView.MEDIA_INFO_BUFFERING_START:
                break;
            /**
             * 结束缓冲
             */
            case BVideoView.MEDIA_INFO_BUFFERING_END:
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 当前缓冲的百分比， 可以配合onInfo中的开始缓冲和结束缓冲来显示百分比到界面
     */
    @Override
    public void onPlayingBufferCache(int percent) {

    }

    /**
     * 播放出错
     */
    @Override
    public boolean onError(int what, int extra) {
        Log.v(TAG, "onError");
        synchronized (SYNC_Playing) {
            SYNC_Playing.notify();
        }
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        return true;
    }

    /**
     * 播放完成
     */
    @Override
    public void onCompletion() {
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

    /**
     * 播放准备就绪
     */
    @Override
    public void onPrepared() {
        Log.v(TAG, "onPrepared");
        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
    }

}
