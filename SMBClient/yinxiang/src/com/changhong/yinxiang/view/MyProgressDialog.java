package com.changhong.yinxiang.view;

import com.changhong.yinxiang.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MyProgressDialog extends Dialog {

   private  Context mContext;

  private TextView notice;
    public MyProgressDialog(Context context) {
    	
        super(context,R.style.fileEditTheme);
        this.mContext=context;
        Window window = this.getWindow();
//        WindowManager.LayoutParams wlp = window.getAttributes();
////        wlp.alpha = 0.1f;
//        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        window.setAttributes(wlp);
        window.setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失  
        setCancelable(true);// 设置是否可以通过点击Back键取消  
        setContentView(R.layout.my_progressbar);  
        notice=(TextView) findViewById(R.id.progress_notice);
    }
     
    @Override
    public void show() {
        super.show();
    }
    
    public void show(String msg){
    	notice.setText(msg);
    	this.show();
    }
}
