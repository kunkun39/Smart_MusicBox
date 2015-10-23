package com.changhong.xiami.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.changhong.xiami.data.MusicsListAdapter;
import com.changhong.yinxiang.R;
import com.xiami.sdk.entities.OnlineAlbum;

public class XiamiMusicListActivity extends Activity{

	private Button back;
	private TextView albumName;
	private ListView musicsList;
	private MusicsListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	
	private void initView(){
		setContentView(R.layout.xiami_music_list);
		back=(Button)findViewById(R.id.btn_back);
		albumName=(TextView)findViewById(R.id.ablum_name);
		musicsList=(ListView)findViewById(R.id.musics_list);
		adapter=new MusicsListAdapter(XiamiMusicListActivity.this);
		musicsList.setAdapter(adapter);
	}
	
	private void initData(){
		//启动activity的时候传进参数名为"musicsAlbum"的专辑。
		Intent intent=getIntent();
		OnlineAlbum album=(OnlineAlbum)intent.getSerializableExtra("musicsAlbum");
		if(album!=null){
		adapter.setData(album.getSongs());
		albumName.setText(album.getAlbumName());
		}
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	
	
	
	
	
	
	
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
	
}
