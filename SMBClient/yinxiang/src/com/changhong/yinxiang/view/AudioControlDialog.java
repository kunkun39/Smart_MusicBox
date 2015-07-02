package com.changhong.yinxiang.view;

import com.changhong.yinxiang.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class AudioControlDialog extends Dialog {

    /**
     * all buttons for number dialog
     */
    public Button btnAudiodown, btnAudioup;
    public ImageView imgback;

    public AudioControlDialog(Context context) {
        super(context, R.style.InputTheme);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.alpha = 0.75f;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        window.setGravity(Gravity.CENTER);
//        window.setWindowAnimations(R.style.dialogWindowAnim);
        setContentView(R.layout.control_audio_dialog);

        btnAudiodown = (Button) findViewById(R.id.audiodown);
        btnAudioup = (Button) findViewById(R.id.audioup);
        imgback = (ImageView) findViewById(R.id.showaudios);
    }

    @Override
    public void show() {
        super.show();
    }
}
