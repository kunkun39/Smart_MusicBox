package com.changhong.tvserver;


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

@SuppressLint("HandlerLeak")
public class Player implements OnInfoListener,OnBufferingUpdateListener,
		OnCompletionListener, MediaPlayer.OnPreparedListener,
		SurfaceHolder.Callback 
{
	private String TAG = "ZJSM";
	private int videoWidth;
	private int videoHeight;
	public MediaPlayer mediaPlayer;
	private SurfaceHolder surfaceHolder;
	private SeekBar skbProgress;
	private Timer mTimer = new Timer();
	private boolean bPlaying = false;
	private Context mContext=null;
	private ImageView mBuffingImageView=null;
	
	@SuppressWarnings("deprecation")
	public Player(Context context,SurfaceView surfaceView,SeekBar skbProgress,ImageView buffImageView)
	{
		this.skbProgress = skbProgress;
		this.mBuffingImageView = buffImageView;
		this.mContext = context;
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		mTimer.schedule(mTimerTask, 0, 1000);
	}
	
	Handler buffImageHandler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what==0){
				Log.i(TAG,"缓冲开始显示");
				mBuffingImageView.setVisibility(View.VISIBLE);
			}
			if(msg.what==1){
				Log.i(TAG,"缓冲结束显示");
				mBuffingImageView.setVisibility(View.INVISIBLE);
			}
		}
	};
	
	//update progress
	TimerTask mTimerTask = new TimerTask() 
	{
		@Override
		public void run() 
		{
			if(mediaPlayer == null)
			{
				return;
			}
			
			bPlaying = false;
			
			try
			{
				bPlaying = mediaPlayer.isPlaying();
			}
			catch(IllegalStateException e)
			{
				e.printStackTrace();
			}
			//不是直播地址才更新进度条
			if (bPlaying && skbProgress.isPressed() == false) 
			{
				handleProgress.sendEmptyMessage(0);
			}
		}
	};
	
	Handler handleProgress = new Handler() 
	{
		public void handleMessage(Message msg) 
		{
			if(mediaPlayer!=null){
				int position = mediaPlayer.getCurrentPosition();
				int duration = mediaPlayer.getDuration();
				
				if (duration > 0) 
				{
					long pos = skbProgress.getMax() * position / duration;
					skbProgress.setProgress((int) pos);
				}
			}
		};
	};
	
	public void play()
	{
		mediaPlayer.start();
	}
	
	public void playUrl(String videoUrl)
	{
		try 
		{
			if(mediaPlayer==null){
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setOnBufferingUpdateListener(this);
				mediaPlayer.setOnPreparedListener(this);
				mediaPlayer.setOnCompletionListener(this);
				mediaPlayer.setOnInfoListener(this);
			}
			mediaPlayer.reset();
			mediaPlayer.setDataSource(videoUrl);
			mediaPlayer.prepare();//prepare֮���Զ�����
			//mediaPlayer.start();
		} 
		catch (IllegalArgumentException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IllegalStateException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (NullPointerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void pause()
	{
		mediaPlayer.pause();
	}
	
	public void stop()
	{
		if (mediaPlayer != null) 
		{ 
			mediaPlayer.stop();
        } 
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
	{
		Log.e(TAG, "surface changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) 
	{
		try 
		{
			if(mediaPlayer==null){
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setOnBufferingUpdateListener(this);
				mediaPlayer.setOnPreparedListener(this);
				mediaPlayer.setOnCompletionListener(this);
				mediaPlayer.setOnInfoListener(this);
			}
			mediaPlayer.setDisplay(surfaceHolder);
		} 
		catch (Exception e) 
		{
			Log.e(TAG, "error", e);
		}
		
		Log.e(TAG, "surface created");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) 
	{
		Log.e(TAG, "surface destroyed");
		
		if (mediaPlayer != null) 
		{ 
			mediaPlayer.stop();
            mediaPlayer.release(); 
            mediaPlayer = null; 
        } 
	}

	
	@Override
	/**
	 * ͨ��onPrepared����
	 */
	public void onPrepared(MediaPlayer arg0) 
	{
		videoWidth = mediaPlayer.getVideoWidth();
		videoHeight = mediaPlayer.getVideoHeight();
		if (videoHeight != 0 && videoWidth != 0) 
		{
			arg0.start();
		}
		
		Log.e(TAG, "onPrepared");
	}

	@Override
	public void onCompletion(MediaPlayer arg0)
	{
		// TODO Auto-generated method stub
		Log.e(TAG, "onCompletion");
		TCMediaPlayer.mEventHandler.sendEmptyMessage(4);
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) 
	{
		skbProgress.setSecondaryProgress(bufferingProgress);
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		switch (what) {
			case MediaPlayer.MEDIA_INFO_BUFFERING_START:
				buffImageHandler.sendEmptyMessage(0);
			break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_END:
				buffImageHandler.sendEmptyMessage(1);
			break;
		}
			return false;
	}

}
