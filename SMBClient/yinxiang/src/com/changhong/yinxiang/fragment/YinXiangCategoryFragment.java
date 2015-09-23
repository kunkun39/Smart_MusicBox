package com.changhong.yinxiang.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.SearchActivity;
import com.changhong.yinxiang.activity.YinXiangMusicViewActivity;
import com.changhong.yinxiang.activity.YinXiangPictureCategoryActivity;
import com.changhong.yinxiang.activity.YinXiangVedioViewActivity;
import com.changhong.yinxiang.nanohttpd.HTTPDService;


public class YinXiangCategoryFragment extends Fragment {


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
            }
        });

	}

	@Override
	public void onResume() {
		super.onResume();
	}

}
