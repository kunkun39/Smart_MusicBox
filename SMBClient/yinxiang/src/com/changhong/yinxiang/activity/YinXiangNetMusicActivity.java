package com.changhong.yinxiang.activity;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.yinxiang.R;

public class YinXiangNetMusicActivity extends Activity {
	
	private final static String TAG = "YinXiangNetMusicActivity";

    /**
     * message handler
     */
    public static Handler mHandler = null;

    /**************************************************IP连接部分*******************************************************/

    private BoxSelectAdapter ipAdapter = null;
    public static TextView title = null;
    private ListView clients = null;
    private Button list = null;
    private Button back = null;
    private Button qqButton = null;
    private Button wyButton = null;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_netmusic);
        initViewAndEvent();
    }

    private void initViewAndEvent() {
        title = (TextView) findViewById(R.id.title);
        clients = (ListView) findViewById(R.id.clients);
        list = (Button) findViewById(R.id.btn_list);
        back = (Button) findViewById(R.id.btn_back);
        qqButton = (Button) findViewById(R.id.qqmusic);
        wyButton = (Button) findViewById(R.id.wymusic);

        /**
         * IP part
         */
        ipAdapter = new BoxSelectAdapter(YinXiangNetMusicActivity.this, ClientSendCommandService.serverIpList);
        clients.setAdapter(ipAdapter);
        clients.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clients.setVisibility(View.GONE);
                return false;
            }
        });
        clients.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList.get(arg2);
                String boxName = ClientSendCommandService.getCurrentConnectBoxName();
                ClientSendCommandService.titletxt = boxName;
                title.setText(boxName);
                ClientSendCommandService.handler.sendEmptyMessage(2);
                clients.setVisibility(View.GONE);
            }
        });
        list.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClientSendCommandService.serverIpList.isEmpty()) {
                    Toast.makeText(YinXiangNetMusicActivity.this, "没有发现长虹智能机顶盒，请确认盒子和手机连在同一个路由器?", Toast.LENGTH_LONG).show();
                } else {
                    clients.setVisibility(View.VISIBLE);
                }
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                finish();
            }
        });
        
        qqButton.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		// TODO Auto-generated method stub
        		MyApplication.vibrator.vibrate(100);
        		//QQ音乐入口
        		Intent mIntent = getPackageManager().getLaunchIntentForPackage("com.tencent.qqmusic");
        		try {
        			startActivity(mIntent);
        		} catch (Exception e) {
        			Log.e(TAG, "startActivity com.tencent.qqmusic  err ! ");
        			Toast.makeText(YinXiangNetMusicActivity.this, "启动QQ音乐失败，请确定您是否已安装QQ音乐！", Toast.LENGTH_LONG).show();
        		}
        	}
        });
        wyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyApplication.vibrator.vibrate(100);
                //QQ音乐入口
                Intent mIntent = getPackageManager().getLaunchIntentForPackage("com.netease.cloudmusic");
                try {
                    startActivity(mIntent);
                } catch (Exception e) {
                    Log.e(TAG, "startActivity com.netease.cloudmusic  err ! ");
                    Toast.makeText(YinXiangNetMusicActivity.this, "启动网易云音乐失败，请确定您是否已安装网易云音乐！", Toast.LENGTH_LONG).show();
                }
			}
		});
    }

    /**
     * ****************************************************系统方法重载部分********************************************
     */

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
