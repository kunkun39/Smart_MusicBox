package com.changhong.yinxiang.setting;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.*;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;

/**
 * Ma Ren
 */

public class AppHelpDialog extends Dialog {

	private LinearLayout remote_control_help;
	private LinearLayout voice_control_help;
	private HelpDetailsDialog hdd;

	public AppHelpDialog(final Context context) {
		super(context, R.style.Translucent_NoTitle);
		setContentView(R.layout.setting_sys_help_dialog);

		Window window = this.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);
		window.setGravity(Gravity.BOTTOM);

		ImageButton helpButton = (ImageButton) findViewById(R.id.cancel_help);
		remote_control_help = (LinearLayout) findViewById(R.id.remote_control_help);
		voice_control_help = (LinearLayout) findViewById(R.id.voice_control_help);

		helpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				dismiss();
			}
		});

		remote_control_help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
				startDetailsDialog(context, context.getString(R.string.rch_name), context.getString(R.string.rch_content));
			}
		});

		voice_control_help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
				startDetailsDialog(context, context.getString(R.string.vch_name), context.getString(R.string.vch_content));
			}
		});
	}
	
	private void startDetailsDialog(Context context,String name,String content){
		if (null==hdd){
			hdd=new HelpDetailsDialog(context);
		}
		hdd.setParameter(name, content);
		Log.i("mmmm","content==" +name+content);
		hdd.show();
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
