package com.changhong.baidu;

import android.app.Dialog;
import android.content.Context;
import android.view.*;
import android.widget.*;
import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;

/**
 * Created by Jack Wang
 */
public class BaiDuVoiceChannelControlDialog extends Dialog {

    private static final String TAG = "BaiDuVoiceChannelControlDialog";

    private Context context;

    /**
     * close button for this dialog
     */
    private TextView iKnowButton;

    public BaiDuVoiceChannelControlDialog(Context context) {
        super(context, R.style.InputTheme);

        this.context = context;

        initView();

        initEvent();
    }

    private void initView() {
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        window.setGravity(Gravity.BOTTOM);

        setContentView(R.layout.yunying_voice_help_dialog);

        iKnowButton = (TextView) findViewById(R.id.dialog_iknow);
    }

    private void initEvent() {
        iKnowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                dismiss();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                dismiss();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
