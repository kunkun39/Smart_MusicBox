package com.changhong.yinxiang.view;

import com.changhong.yinxiang.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class TVNumInputDialog extends Dialog {

    /**
     * all buttons for number dialog
     */
    public Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnOk, btnCancle;

    public TVNumInputDialog(Context context) {
        super(context, R.style.InputTheme);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.alpha = 0.75f;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        window.setGravity(Gravity.BOTTOM);
//        window.setWindowAnimations(R.style.dialogWindowAnim);
        setContentView(R.layout.control_num_dialog);

        btn0 = (Button) findViewById(R.id.num0);
        btn1 = (Button) findViewById(R.id.num1);
        btn2 = (Button) findViewById(R.id.num2);
        btn3 = (Button) findViewById(R.id.num3);
        btn4 = (Button) findViewById(R.id.num4);
        btn5 = (Button) findViewById(R.id.num5);
        btn6 = (Button) findViewById(R.id.num6);
        btn7 = (Button) findViewById(R.id.num7);
        btn8 = (Button) findViewById(R.id.num8);
        btn9 = (Button) findViewById(R.id.num9);
        btnOk = (Button) findViewById(R.id.numok);
        btnCancle = (Button) findViewById(R.id.numcancle);
    }

    @Override
    public void show() {
        super.show();
    }
}
