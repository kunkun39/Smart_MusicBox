package com.changhong.yinxiang.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.SearchActivity;
import com.changhong.yinxiang.activity.YinXiangMusicViewActivity;
import com.changhong.yinxiang.activity.YinXiangPictureCategoryActivity;
import com.changhong.yinxiang.activity.YinXiangVedioViewActivity;
import com.changhong.yinxiang.nanohttpd.HTTPDService;


public class YinXiangCategoryFragment extends Fragment {

	/************************************************** IP连接部分 *******************************************************/

//    public static TextView title = null;
//    private Button listClients;
//    private Button back;
//    private ListView clients = null;
//    private BoxSelectAdapter IpAdapter;

	/************************************************** 菜单部分 *******************************************************/
	private ImageView imageTouYing;
	private ImageView vedioTouYing;
	private ImageView musicTouYing;
	private ImageView otherTouYing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 启动Http服务
         */
        Intent http = new Intent(getActivity(), HTTPDService.class);
        getActivity().startService(http);

        initMedia();
	}
	
	
	@Override
   	public View onCreateView(LayoutInflater inflater, ViewGroup container,
   			Bundle savedInstanceState) {
       	View view= inflater.inflate(R.layout.fragment_yinxiang_category, container,	false);
       	

	
       	initView(view);
		initEvent();
   		return  view;
   	}
	
	

    private void initMedia() {
        /**
         * 通知系统媒体去更新媒体库
         */
        String[] types = {"video/3gpp", "video/x-msvideo", "video/mp4", "video/mpeg", "video/quicktime",
                "audio/x-wav", "audio/x-pn-realaudio", "audio/x-ms-wma", "audio/x-ms-wmv", "audio/x-mpeg", "image/jpeg", "image/png"};
        MediaScannerConnection.scanFile(getActivity(), new String[]{Environment.getExternalStorageDirectory().getAbsolutePath()}, types, null);
    }

	private void initView(View v) {
//		setContentView(R.layout.activity_yinxiang_category);
        /**
         * IP连接部分
         */
//        title = (TextView) v.findViewById(R.id.title);
//        back = (Button) v.findViewById(R.id.btn_back);
//        clients = (ListView) v.findViewById(R.id.clients);
//        listClients = (Button) v.findViewById(R.id.btn_list);

		/**
		 * 菜单部分
		 */
		imageTouYing = (ImageView) v.findViewById(R.id.button_image_touying);
		vedioTouYing = (ImageView) v.findViewById(R.id.button_vedio_touying);
		musicTouYing = (ImageView) v.findViewById(R.id.button_music_touying);
		otherTouYing = (ImageView) v.findViewById(R.id.button_other_touying);
		
	}

	private void initEvent() {
        /**
         * IP连接部分
         */
//        IpAdapter = new BoxSelectAdapter(getActivity(),
//                ClientSendCommandService.serverIpList);
//        clients.setAdapter(IpAdapter);
//        clients.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                clients.setVisibility(View.GONE);
//                return false;
//            }
//        });
//        clients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList.get(arg2);
//                ClientSendCommandService.titletxt=ClientSendCommandService.getCurrentConnectBoxName();
//                title.setText(ClientSendCommandService.getCurrentConnectBoxName());
//                ClientSendCommandService.handler.sendEmptyMessage(2);
//                clients.setVisibility(View.GONE);
//            }
//        });
//        listClients.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    MyApplication.vibrator.vibrate(100);
//                    if (ClientSendCommandService.serverIpList.isEmpty()) {
//                        Toast.makeText(getActivity(), "未获取到服务器IP", Toast.LENGTH_LONG).show();
//                    } else {
//                        clients.setVisibility(View.VISIBLE);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MyApplication.vibrator.vibrate(100);
//                getActivity().onBackPressed();
//            }
//        });

		/**
		 * 菜单部分
		 */
		imageTouYing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                Intent intent = new Intent(getActivity(), YinXiangPictureCategoryActivity.class);
                startActivity(intent);
            }
        });
        vedioTouYing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                Intent intent = new Intent(getActivity(), YinXiangVedioViewActivity.class);
                startActivity(intent);
            }
        });
		musicTouYing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                Intent intent = new Intent(getActivity(), YinXiangMusicViewActivity.class);
                startActivity(intent);
            }
        });
        otherTouYing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
//                Toast.makeText(getActivity(), "暂不支持，敬请期待...", 3000).show();
            }
        });

	}

	/********************************************** 系统发发重载 *********************************************************/

	@Override
	public void onResume() {
		super.onResume();
//        if (ClientSendCommandService.titletxt != null) {
//            title.setText(ClientSendCommandService.titletxt);
//        }
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		switch (keyCode) {
//		case KeyEvent.KEYCODE_BACK:
//			finish();
//			break;
//		default:
//			break;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

}
