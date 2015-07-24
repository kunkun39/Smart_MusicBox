package com.changhong.yinxiang.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.Fragment;
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

public class YinXiangNetMusicFragment extends Fragment {
	
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
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_netmusic);
//        initViewAndEvent();
    }
    
    @Override
   	public View onCreateView(LayoutInflater inflater, ViewGroup container,
   			Bundle savedInstanceState) {
       	View view= inflater.inflate(R.layout.activity_remote_control, container,	false);
       	initViewAndEvent(view);
   		return  view;
   	}
    

    private void initViewAndEvent(View v) {
        title = (TextView) v.findViewById(R.id.title);
        clients = (ListView) v.findViewById(R.id.clients);
        list = (Button) v.findViewById(R.id.btn_list);
        back = (Button) v.findViewById(R.id.btn_back);
        qqButton = (Button) v.findViewById(R.id.qqmusic);
        wyButton = (Button) v.findViewById(R.id.wymusic);

        /**
         * IP part
         */
        ipAdapter = new BoxSelectAdapter(getActivity(), ClientSendCommandService.serverIpList);
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
                    Toast.makeText(getActivity(), "没有发现长虹智能机顶盒，请确认盒子和手机连在同一个路由器?", Toast.LENGTH_LONG).show();
                } else {
                    clients.setVisibility(View.VISIBLE);
                }
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                getActivity().onBackPressed();
            }
        });
        
        qqButton.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		// TODO Auto-generated method stub
        		MyApplication.vibrator.vibrate(100);
        		//QQ音乐入口
        		Intent mIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.tencent.qqmusic");
        		try {
        			startActivity(mIntent);
        		} catch (Exception e) {
        			Log.e(TAG, "startActivity com.tencent.qqmusic  err ! ");
        			Toast.makeText(getActivity(), "启动QQ音乐失败，请确定您是否已安装QQ音乐！", Toast.LENGTH_LONG).show();
        		}
        	}
        });
        wyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyApplication.vibrator.vibrate(100);
                //QQ音乐入口
                Intent mIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.netease.cloudmusic");
                try {
                    startActivity(mIntent);
                } catch (Exception e) {
                    Log.e(TAG, "startActivity com.netease.cloudmusic  err ! ");
                    Toast.makeText(getActivity(), "启动网易云音乐失败，请确定您是否已安装网易云音乐！", Toast.LENGTH_LONG).show();
                }
			}
		});
    }

    /**
     * ****************************************************系统方法重载部分********************************************
     */

    @Override
	public void onResume() {
        super.onResume();
        if (ClientSendCommandService.titletxt != null) {
            title.setText(ClientSendCommandService.titletxt);
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK:
//                finish();
//                break;
//            default:
//                break;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


}
