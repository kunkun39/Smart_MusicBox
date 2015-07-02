package com.changhong.yinxiang.view;

import com.changhong.yinxiang.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class DYControlDialog extends Dialog {

    /**
     * all buttons for number dialog
     */
    public Button btnDYdown, btnDYup;
    public ImageView imgback;

    public DYControlDialog(Context context) {
        super(context, R.style.InputTheme);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.alpha = 0.75f;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        window.setGravity(Gravity.CENTER);
//        window.setWindowAnimations(R.style.dialogWindowAnim);
        setContentView(R.layout.control_dy_dialog);

        btnDYdown = (Button) findViewById(R.id.dydown);
        btnDYup = (Button) findViewById(R.id.dyup);
        imgback = (ImageView) findViewById(R.id.showdy);
    }

    @Override
    public void show() {
        super.show();
    }
}
