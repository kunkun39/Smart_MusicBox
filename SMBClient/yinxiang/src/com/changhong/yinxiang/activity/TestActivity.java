package com.changhong.yinxiang.activity;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Date;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.JsonReader;
import android.view.MotionEvent;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.service.NetworkConnectChangedReceiver;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.search.searchHistoryAdapter;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.fragment.YinXiangCategoryFragment;
import com.changhong.yinxiang.fragment.YinXiangFMFragment;
import com.changhong.yinxiang.fragment.YinXiangNetMusicFragment;
import com.changhong.yinxiang.fragment.YinXiangRemoteControlFragment;
import com.changhong.yinxiang.fragment.YinXiangSettingFragment;
import com.changhong.yinxiang.music.MusicEditServer;
import com.changhong.yinxiang.service.AppLogService;
import com.changhong.yinxiang.service.ClientGetCommandService;
import com.changhong.yinxiang.service.ClientLocalThreadRunningService;
import com.changhong.yinxiang.service.UpdateLogService;
import com.changhong.yinxiang.service.UserUpdateService;
import com.changhong.yinxiang.setting.AppHelpDialog;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public class TestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_fm_switch);


		ListView myHistory=(ListView) findViewById(R.id.history_test_infor);
	    myHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {   
					    Toast.makeText(TestActivity.this, "点击成功", Toast.LENGTH_LONG).show();
				}
			});
	    String historyData="后来"+";"+"执迷不悔";
	    searchHistoryAdapter  historyAdapter=new searchHistoryAdapter(TestActivity.this, historyData);
		myHistory.setAdapter(historyAdapter);

	}
}
