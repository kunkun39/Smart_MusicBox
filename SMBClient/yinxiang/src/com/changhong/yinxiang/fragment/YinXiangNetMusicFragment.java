package com.changhong.yinxiang.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.xiami.activity.AlbumListActivity;
import com.changhong.xiami.activity.ArtistListActivity;
import com.changhong.xiami.activity.SceneActivity;
import com.changhong.xiami.activity.XiamiMusicListActivity;

import com.changhong.yinxiang.R;

public class YinXiangNetMusicFragment extends Fragment{
	
	private final static String TAG = "YinXiangNetMusicActivity";

    /**
     * message handler
     */
    public static Handler mHandler = null;

    private ImageView qqButton,wyButton,randomButton,xiamiButton;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }
    
    
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_netmusic, container,	false);
        initViewAndEvent(view);
		return view;
	}

    private void initViewAndEvent(View view ) {
      
        qqButton = (ImageView) view.findViewById(R.id.netmusic_qq);
        wyButton = (ImageView) view.findViewById(R.id.netmusic_wy);
        randomButton = (ImageView) view.findViewById(R.id.netmusic_random);
        xiamiButton = (ImageView) view.findViewById(R.id.netmusic_xm);

        qqButton.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		// TODO Auto-generated method stub
        		MyApplication.vibrator.vibrate(100);
        		//QQ音乐入口
//        		Intent mIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.tencent.qqmusic");
//        		try {
//        			startActivity(mIntent);
//        		} catch (Exception e) {
//        			Log.e(TAG, "startActivity com.tencent.qqmusic  err ! ");
//        			Toast.makeText(getActivity(), "启动QQ音乐失败，请确定您是否已安装QQ音乐！", Toast.LENGTH_LONG).show();
//        		}
        		 
        		//测试代码
        		Intent intent=new Intent(getActivity(),XiamiMusicListActivity.class);
        		startActivity(intent);
        		
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
        
     randomButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyApplication.vibrator.vibrate(100);
                //随便听听快捷方式
				 ClientSendCommandService.msg = "key:music";
	             ClientSendCommandService.handler.sendEmptyMessage(1);
			}
		});
 
       xiamiButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MyApplication.vibrator.vibrate(100);
			
//			 ClientSendCommandService.msg = "key:xiami";
//             ClientSendCommandService.handler.sendEmptyMessage(1);
			
             startXiaMiMusic();
		}
	});
    }
  
    
    
   private void startXiaMiMusic(){		
		//启动虾米音乐
		Intent intent = new Intent(getActivity(),SceneActivity.class);
		startActivity(intent);
	}

}
