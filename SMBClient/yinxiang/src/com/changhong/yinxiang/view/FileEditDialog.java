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

public class FileEditDialog extends Dialog {

    /**
     * 
     */
    public ImageView edit_copy, edit_cancle, edit_clock;
    public ImageView imgback;
   private  Context mContext;
	public static String ACTION_FILE_EDIT = "com.changhong.fileEdit";


    public FileEditDialog(Context context) {
    	
        super(context, R.style.InputTheme);
        this.mContext=context;
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.alpha = 0.75f;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        window.setGravity(Gravity.CENTER);
//        window.setWindowAnimations(R.style.dialogWindowAnim);
        setContentView(R.layout.file_edit_dialog);

        //功能按钮定义
        edit_copy = (ImageView) findViewById(R.id.fileedit_copy);
        edit_cancle = (ImageView) findViewById(R.id.fileedit_cancle);
        edit_clock = (ImageView) findViewById(R.id.fileedit_clock);
//        imgback=(ImageView)findViewById(R.id.showyinxiao);
         
    }
    
    
    @Override
    public void show() {
        super.show();
    }
}
