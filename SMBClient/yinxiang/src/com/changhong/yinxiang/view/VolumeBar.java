package com.changhong.yinxiang.view;


import com.changhong.yinxiang.utils.FloatUtility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

public class VolumeBar extends View{
	
	private final String TAG = "Ruler";

	private Bitmap mVolumeBg = null;//背景
	private Drawable mBgScale = null;//刻度	
	private Bitmap mPointer = null;//指针
	private Paint mFrePaint = null;
	private float mPointPosition = 0;	
	private float mStartX = 0;
	private float mMoveScale=0;

		
//	private static final float SCALE_UNIT = 1.74f;
//	private static final float MAX_SCALE_VOLUME =31;
//	private static final float START_SCALE = 41;
//	private static final float MAX_SCALE = 540;	
//	private static final float STANTARD_WIDTH = 720;	
//	private static final float STANTARD_HEIGHT = 1280;
	
	private static final float SCALE_UNIT = 1.835f;
	private static final float MAX_SCALE_VOLUME =31;
	private static final float START_SCALE = 10;
	private static final float MAX_SCALE = 569;	
	private static final float STANTARD_WIDTH = 720;	
	private static final float STANTARD_HEIGHT = 1280;
	
	int screenWidth, screenHeight;
	private boolean ischang=false;

	
	
	public VolumeBar(Context context) {
		super(context);
		
//		screenWidth = Environment.getInstance().getScreenWidth();
//		screenHeight = Environment.getInstance().getScreenHeight();
		
		setFocusable(true);		
		mFrePaint = new Paint();
		mFrePaint.setAntiAlias(true);
		mFrePaint.setColor(0xFFFFFFFF);
		mFrePaint.setTextScaleX(1.8f);	
        initalSrc("volume_scale","volume_point");

	}
	
	
	
	
	public void initalSrc(String bgscale,  String point){
		int resId = getResources().getIdentifier((new StringBuilder(String.valueOf(bgscale))).toString(), "drawable", getContext().getPackageName());
		Drawable drawable = getResources().getDrawable(resId);		
		mVolumeBg=drawableToBitmap(drawable,0);
		resId = getResources().getIdentifier((new StringBuilder(String.valueOf(point))).toString(), "drawable", getContext().getPackageName());
		drawable= getResources().getDrawable(resId);
		mPointer=drawableToBitmap(drawable,2);
		
	}

	int scaleY=0;

	@Override
	public void onDraw(Canvas canvas) {
		//背景刻度表
				int startY=(int) (0.039 * screenHeight);
				float KeduPosition = FloatUtility.mulitiply(mPointPosition,FloatUtility.divide(screenWidth, STANTARD_WIDTH,2));	
				float scaleOff = 0.032f * screenWidth;
				
				//背景
				canvas.drawBitmap(mVolumeBg, 0, startY , null);			
				//绘制指针
				canvas.drawBitmap(mPointer, KeduPosition-scaleOff, scaleY+startY-5, null);	
	}
    
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width= (int) (0.79f * screenWidth);
		int height= (int) (0.027f * screenHeight);	
		setMeasuredDimension(width, height);
	}
	
	
	public Bitmap drawableToBitmap(Drawable drawable,int type) {
		

		int W= (int) (0.79f*screenWidth);
		int H= (int) (0.0125f*screenHeight);
		
	    if(1==type){//刻度指针
			 W=H=  (int) (0.0278f*screenWidth);
		}
		android.graphics.Bitmap.Config config;
		Bitmap bitmap;
		Canvas canvas;
		if (drawable.getOpacity() != -1)
			config = android.graphics.Bitmap.Config.ARGB_8888;
		else
			config = android.graphics.Bitmap.Config.RGB_565;	
		bitmap = Bitmap.createBitmap(W, H, config);
		canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, W, H);
		drawable.draw(canvas);
		return bitmap;
	}
	
	
	public Bitmap drawableToBitmap(Drawable drawable,int width,int height) {
				
		android.graphics.Bitmap.Config config;
		Bitmap bitmap;
		Canvas canvas;
		if (drawable.getOpacity() != -1)
			config = android.graphics.Bitmap.Config.ARGB_8888;
		else
			config = android.graphics.Bitmap.Config.RGB_565;
		
		bitmap = Bitmap.createBitmap(width, height, config);
		canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}
	
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			mStartX = event.getX();			
			break;
			
		case MotionEvent.ACTION_MOVE:	
			mMoveScale=event.getX() - mStartX ;					
			break;
		case MotionEvent.ACTION_UP:	
			
			if(Math.abs(mMoveScale) >= SCALE_UNIT){
				
				if(mMoveScale>0)mPointPosition+=SCALE_UNIT;
				else mPointPosition-=SCALE_UNIT;
				
//				mPointPosition+=(int)mMoveScale;
				
				getCurKeDu();
				invalidate();
		        performClick();
			}				
			mStartX=mMoveScale=0;
			break;
			

		default:
			break;
		}
		
		return true;
	}
	
	private float getMaxScale(){
		return MAX_SCALE_VOLUME;
	}
	

	
	public int getCurKeDu(){
		
		int unitCount;		
		int kedu = 0;
		float mScale=mPointPosition-START_SCALE;
	    if(mScale < 0){
	    	mScale=0;
	    	mPointPosition=START_SCALE;
	    }else if(mScale > MAX_SCALE){
	    	mPointPosition=MAX_SCALE+START_SCALE;
	    	mScale=MAX_SCALE;;
	    }
	    unitCount = Math.abs((int)FloatUtility.divide(mScale, SCALE_UNIT,2));
		mPointPosition=(int)(unitCount*SCALE_UNIT+START_SCALE);
		kedu =(int) FloatUtility.divide(unitCount, 10f);		
		setKeDu(kedu);		
		return kedu;
	}
	
	public void setKeDu(float kedu){
		if(kedu<0)kedu=0;
		else if(kedu>MAX_SCALE_VOLUME)kedu=MAX_SCALE_VOLUME;
		float unitCount_VOLUME = (kedu) /0.1f;
		mPointPosition = unitCount_VOLUME * SCALE_UNIT +START_SCALE;
	}
	
	public void smoothScrollToKeDu(float kedu){		
		float unitCount_VOLUME = (kedu ) /0.1f;
		mPointPosition =unitCount_VOLUME * SCALE_UNIT +START_SCALE;
		invalidate();
	}
	
}
