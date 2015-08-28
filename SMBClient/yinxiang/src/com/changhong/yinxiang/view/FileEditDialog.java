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
    public ImageView edit_copy1, edit_copy2,edit_remove, edit_clock,edit_rename;
    public ImageView imgback;
   private  Context mContext;
	public static String ACTION_FILE_EDIT = "com.changhong.fileEdit";


    public FileEditDialog(Context context) {
    	
        super(context, R.style.InputTheme);
        this.mContext=context;
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        window.setGravity(Gravity.BOTTOM);
//        window.setWindowAnimations(R.style.dialogWindowAnim);
        setContentView(R.layout.file_edit_dialog);

        //功能按钮定义
        edit_copy1 = (ImageView) findViewById(R.id.fileedit_copy1);
        edit_copy2 = (ImageView) findViewById(R.id.fileedit_copy2);
        edit_remove = (ImageView) findViewById(R.id.fileedit_cancle);
        edit_clock = (ImageView) findViewById(R.id.fileedit_clock);
        edit_rename=(ImageView)findViewById(R.id.fileedit_rename);
         
    }
    
    
    @Override
    public void show() {
        super.show();
    }
}
