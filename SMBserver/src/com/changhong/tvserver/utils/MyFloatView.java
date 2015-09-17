package com.changhong.tvserver.utils;

/**
 * 在屏幕上方实现浮动图标显示。
 */

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class MyFloatView {

	      private static WindowManager wm=null;
	      
	      //浮标显示位置： 左上
	     public final static int DIRECTION_LEFTTOP=1;
	    //浮标显示位置： 左下
	     public final static int DIRECTION_LEFTBOTTOM=2;
	      //浮标显示位置： 右上
	     public final static int DIRECTION_RIGHTTOP=3;
	      //浮标显示位置： 右下
	     public final static int DIRECTION_RIGHTBOTTOM=4;

	      /**
	       * 在屏幕页面上，显示浮动图标
	       * @param context 上下文关联
	       * @param floatView  显示浮动图标view
	       * @param width 浮标的宽
	       * @param height  浮标的高
	       * @param style 显示
	       */
	      public static  void show(Context context, View floatView, int width, int height, int direction){
	           wm=(WindowManager) context.getApplicationContext().getSystemService("window"); 	  
		  		// 指定下载进程显示屏幕下方		          
		  		WindowManager.LayoutParams lp =new WindowManager.LayoutParams() ;
		  		lp.type=2002;
		  		lp.flags|=8;	
		  		lp.x=30;
		  		lp.y=30;	
		  		lp.width = width;
		  		lp.height = height;
		  	
		  		if(DIRECTION_LEFTTOP == direction)lp.gravity = Gravity.LEFT | Gravity.TOP;
		  		else if(DIRECTION_LEFTBOTTOM == direction)lp.gravity = Gravity.BOTTOM  | Gravity.LEFT;
		  		else if(DIRECTION_RIGHTTOP == direction)lp.gravity = Gravity.TOP  | Gravity.RIGHT;
		  		else if(DIRECTION_RIGHTBOTTOM == direction)lp.gravity = Gravity.BOTTOM  | Gravity.RIGHT;	
		  		else lp.gravity = Gravity.CENTER;
		  		lp.alpha=0.6f;
		  		wm.addView(floatView, lp);	  		
	      }
	    
	      
	      /**
	       *从WindowManager 移除 浮动View，需要showFloatView()成对使用。
	       * @param floatView  浮动View
	       */
	      public static  void removeView(View floatView){
	    	  try {
	    		  if(null != wm){
						   wm.removeView(floatView);
						   wm=null;
	    		  }
			} catch (IllegalArgumentException e) {
				    Log.e("MyFloatView::", "removeView  occurred error! ");
				    e.printStackTrace();
			}
	      }
	  
	      
}
