package com.changhong.yinxiang.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.changhong.yinxiang.R;

public class FileDownLoadDialog extends Dialog {

    /**
     * 
     */
    public ImageView progress, copy_from,copy_to;
	public static String ACTION_FILE_EDIT = "com.changhong.FileCopyingDialog";
    final String COPY_MOBILE_TO_YINXIANG="yinxiang";
    final String  COPY_YINXIANG_TO_MOBILE="mobile";
    AnimationDrawable anim=null;

    public FileDownLoadDialog(Context context) {
    
        super(context, R.style.fileEditTheme);

        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        window.setGravity(Gravity.BOTTOM);
        setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失  
        setCancelable(false);// 设置是否可以通过点击Back键取消  
        setContentView(R.layout.dialog_file_copying);

        //功能按钮定义
        progress = (ImageView) findViewById(R.id.filecopy_progress);
        copy_from = (ImageView) findViewById(R.id.filecopy_from);
        copy_to = (ImageView) findViewById(R.id.filecopy_to);  
              
    
    }

    public void close(){
    	if(null != anim && anim.isRunning()){
    		anim.stop();
    	}
    }
    
    public void show(String type) {
    	
    	if(type.equals(COPY_YINXIANG_TO_MOBILE)){
           	copy_from.setBackgroundResource(R.drawable.tv);
           	copy_to.setBackgroundResource(R.drawable.mobile);
           }
    	    //将动画资源文件设置为ImageView的背景  
         progress.setImageResource(R.anim.copying_anim); 
 		 //获取ImageView背景,此时已被编译成AnimationDrawable 
         anim = (AnimationDrawable) progress.getDrawable(); 
         
         //判断动画是否运行
         if(!anim.isRunning()){
     		  //开始执行动画  
                anim.start();   
         }  
        super.show();
    }
}
