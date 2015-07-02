package com.changhong.yinxiang.view;

import com.changhong.yinxiang.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class LightsControlDialog extends Dialog {

    /**
     * all buttons for number dialog
     */
    public Button lights_up, lights_down, lights_moon, lights_sun,lights_controller;
    public ImageView imgback;

    public LightsControlDialog(Context context) {
        super(context, R.style.InputTheme);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.alpha = 0.75f;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        window.setGravity(Gravity.CENTER);
//        window.setWindowAnimations(R.style.dialogWindowAnim);
        setContentView(R.layout.control_lights_dialog);

        lights_up = (Button) findViewById(R.id.lightsup);
        lights_down = (Button) findViewById(R.id.lightsdown);
        lights_moon = (Button) findViewById(R.id.lightsmoon);
        lights_sun = (Button) findViewById(R.id.lightssun);
        lights_controller = (Button) findViewById(R.id.lightscontrol);
        imgback = (ImageView) findViewById(R.id.showlights);
    }

    @Override
    public void show() {
        super.show();
    }
}
