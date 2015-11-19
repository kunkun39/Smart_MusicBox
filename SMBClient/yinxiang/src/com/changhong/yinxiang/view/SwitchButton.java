

package com.changhong.yinxiang.view;

import com.changhong.common.utils.StringUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.ImageView;

public class SwitchButton extends ImageView{

    private String imageName;
    private boolean switchStatus;
    
    public SwitchButton(Context context){
        super(context);
        switchStatus=false;
    }
    
    public SwitchButton(Context context, AttributeSet attrs) {
    	super(context, attrs, 0);
        switchStatus=false;
    }
    
    
    public void init(String imgName){
	     this.imageName=imgName;
  }
	
    
    public boolean getSwitchStatus(){
        return switchStatus;
    }

   
	 public void onDraw(Canvas canvas){
		    if(StringUtils.hasLength(imageName)){
			        String resName=imageName;
			        if(switchStatus)resName=resName+"_focus";	               
			        int resId = getResources().getIdentifier((new StringBuilder(String.valueOf(resName))).toString(), "drawable", getContext().getPackageName());       
					setImageResource(resId); 			
		    }
			
	        super.onDraw(canvas);
			
	    }
	
    public boolean onTouchEvent(MotionEvent motionevent){
    	
        super.onTouchEvent(motionevent);
       
        if(motionevent.getAction() == MotionEvent.ACTION_DOWN){
        	
        	 switchStatus=!switchStatus;
             invalidate();
        }
        else if(motionevent.getAction() == MotionEvent.ACTION_UP){         
	        postInvalidate();
        }
        return true;
    }
}
