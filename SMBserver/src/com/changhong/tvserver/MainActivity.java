package com.changhong.tvserver;


import com.changhong.tvserver.utils.StringUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "YinXiangServer";
	
    private EditText chboxName;

    private Button chboxSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		chboxName = (EditText) findViewById(R.id.ch_box_name);
        chboxSave = (Button) findViewById(R.id.ch_box_save);

        TVSocketControllerService.CH_BOX_NAME = getBoxName();
        chboxName.setText(TVSocketControllerService.CH_BOX_NAME);
		
        Intent intent = new Intent(MainActivity.this, TVSocketControllerService.class);
        startService(intent);
        
        
      //YD add 20150726 client状态监控服务
//        Intent  mIntent = new Intent(MainActivity.this, com.changhong.tvserver.smartctrl.ClientOnLineMonitorService.class);
//        startService(mIntent);
		
		chboxSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = chboxName.getText().toString();
                if (!StringUtils.hasLength(content)) {
                    Toast.makeText(MainActivity.this, "请输入智能音箱的标识!", 3000).show();
                } else {
                    try {
                        if (!TVSocketControllerService.CH_BOX_NAME.equals(content)) {
                            content = content.trim();
                            saveBoxName(content);
                            TVSocketControllerService.CH_BOX_NAME = content;
                        }
                        Toast.makeText(MainActivity.this, "保存成功!", 3000).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "保存失败!", 3000).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
	
	private void saveBoxName(String name) {
        SharedPreferences preferences = MainActivity.this.getSharedPreferences("changhong_box_name", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("CH_BOX_NAME", name);
        editor.commit();
    }

    private String getBoxName() {
        SharedPreferences preferences = MainActivity.this.getSharedPreferences("changhong_box_name", Context.MODE_PRIVATE);
        return preferences.getString("CH_BOX_NAME", "音    箱");
    }
}
