package com.changhong.yinxiang.view;

import com.changhong.yinxiang.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class YinXiaoControlDialog extends Dialog {

    /**
     * all buttons for number dialog
     */
    public Button YX_movie, YX_tv, YX_music, YX_game, YX_yd, YX_xt;
    public ImageView imgback;

    public YinXiaoControlDialog(Context context) {
        super(context, R.style.InputTheme);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.alpha = 0.75f;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        window.setGravity(Gravity.CENTER);
//        window.setWindowAnimations(R.style.dialogWindowAnim);
        setContentView(R.layout.control_yinxiao_dialog);

        YX_movie = (Button) findViewById(R.id.yinxiao_movie);
        YX_tv = (Button) findViewById(R.id.yinxiao_tv);
        YX_music = (Button) findViewById(R.id.yinxiao_music);
        YX_game = (Button) findViewById(R.id.yinxiao_game);
        YX_yd = (Button) findViewById(R.id.yinxiao_yd);
        YX_xt = (Button) findViewById(R.id.yinxiao_xt);
        imgback=(ImageView)findViewById(R.id.showyinxiao);
    }

    @Override
    public void show() {
        super.show();
    }
}
