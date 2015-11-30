package com.changhong.yinxiang.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.nanohttpd.HTTPDService;

/**
 * Created by Jack Wang
 */
public class YinXiangSourceActivity extends BaseActivity {

	/************************************************** IP连接部分 *******************************************************/
//
//    public static TextView title = null;
//    private Button listClients;
//    private Button back;
//    private ListView clients = null;
//    private BoxSelectAdapter IpAdapter;

	/************************************************** 菜单部分 *******************************************************/
    Button btnBT=null;
    Button btnAV1=null;
    Button btnAV2=null;
    Button btnHDMI=null;
    Button btnOTT=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	protected void initView() {
		setContentView(R.layout.activity_yinxiang_source);
        /**
         * IP连接部分
         */
        title = (TextView) findViewById(R.id.title);
        back = (ImageView) findViewById(R.id.btn_back);
        clients = (ListView) findViewById(R.id.clients);
        listClients = (Button) findViewById(R.id.btn_list);

		/**
		 * 菜单部分
		 */
        btnBT=(Button)findViewById(R.id.bt);
        btnAV1=(Button)findViewById(R.id.av1);
        btnAV2=(Button)findViewById(R.id.av2);
        btnHDMI=(Button)findViewById(R.id.hdmi);
        btnOTT=(Button)findViewById(R.id.ott);
	}

	protected void initData() {
        super.initData();

		/**
		 * 菜单部分
		 */
        btnBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MyApplication.vibrator.vibrate(100);
				ClientSendCommandService.msg = "source:bt";
				ClientSendCommandService.handler.sendEmptyMessage(1);
			}
		});
        btnAV1.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View arg0) {
        		// TODO Auto-generated method stub
        		MyApplication.vibrator.vibrate(100);
				ClientSendCommandService.msg = "source:av1";
				ClientSendCommandService.handler.sendEmptyMessage(1);
        	}
        });
        btnAV2.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View arg0) {
        		// TODO Auto-generated method stub
        		MyApplication.vibrator.vibrate(100);
				ClientSendCommandService.msg = "source:av2";
				ClientSendCommandService.handler.sendEmptyMessage(1);
        	}
        });
        btnHDMI.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View arg0) {
        		// TODO Auto-generated method stub
        		MyApplication.vibrator.vibrate(100);
				ClientSendCommandService.msg = "source:hdmi";
				ClientSendCommandService.handler.sendEmptyMessage(1);
        	}
        });
        btnOTT.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View arg0) {
        		// TODO Auto-generated method stub
        		MyApplication.vibrator.vibrate(100);
				ClientSendCommandService.msg = "source:ott";
				ClientSendCommandService.handler.sendEmptyMessage(1);
        	}
        });
	}

	/********************************************** 系统发发重载 *********************************************************/

	@Override
	protected void onResume() {
		super.onResume();
        if (ClientSendCommandService.titletxt != null) {
            title.setText(ClientSendCommandService.titletxt);
        }
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

}
