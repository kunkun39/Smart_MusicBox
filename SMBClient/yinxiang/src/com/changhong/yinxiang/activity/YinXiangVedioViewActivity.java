package com.changhong.yinxiang.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.nanohttpd.HTTPDService;
import com.changhong.yinxiang.vedio.YinXiangVedioAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 15-5-11.
 */
public class YinXiangVedioViewActivity extends BaseActivity {

  

    /**************************************************视频部分*******************************************************/

    /**
     * Image List adapter
     */
    private YinXiangVedioAdapter vedioAdapter;
    /**
     * 视频浏览部分
     */
    private ListView vedioListView;

    /**
     * 视频推送按钮
     */
    private Button vedioSend;

    /**
     * 视频已经选择INFO
     */
    public static TextView vedioSelectedInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initView() {
        setContentView(R.layout.activity_yinxiang_vedio_view);

        /**
         * IP连接部分
         */
        title = (TextView) findViewById(R.id.title);
        back = (Button) findViewById(R.id.btn_back);
        clients = (ListView) findViewById(R.id.clients);
        listClients = (Button) findViewById(R.id.btn_list);

        /**
         * 视频部分
         */
        vedioListView = (ListView) findViewById(R.id.yinxiang_vedio_list_view);
        vedioAdapter = new YinXiangVedioAdapter(this);
        vedioListView.setAdapter(vedioAdapter);

        vedioSend = (Button)findViewById(R.id.yinxing_vedio_tuisong);
        vedioSelectedInfo = (TextView)findViewById(R.id.yinxing_vedio_tuisong_info);
    }

    protected void initData() {
        super.initData();
        /**
         * 视频部分
         */
        vedioSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (NetworkUtils.isWifiConnected(YinXiangVedioViewActivity.this)) {
                        if (!StringUtils.hasLength(ClientSendCommandService.serverIP)) {
                            Toast.makeText(YinXiangVedioViewActivity.this, "手机未连接电视，请确认后再投影", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        MyApplication.vibrator.vibrate(100);

                        /**
                         * 转换投影图片的路径
                         */
                        if(YinXiangVedioAdapter.selectVedioPaths.isEmpty()){
                        	Toast.makeText(YinXiangVedioViewActivity.this, "请选择推送的视频", Toast.LENGTH_LONG).show();
                        }else{
//                        	if(YinXiangVedioAdapter.selectVedioPaths.size()>=4){
//                        		Toast.makeText(YinXiangVedioViewActivity.this, "暂时最多支持推送4个视频", Toast.LENGTH_LONG).show();
//                        	}else{
                        		List<String> convertSelectedVedioPaths = new ArrayList<String>();
                                for (String selectVedioPath : YinXiangVedioAdapter.selectVedioPaths) {
                                    String tempPath = "";
                                    if (selectVedioPath.startsWith(HTTPDService.defaultHttpServerPath)) {
                                        tempPath =convertFileUrlToHttpURL( selectVedioPath.replace(HTTPDService.defaultHttpServerPath, ""));
                                    
                                    } else {
                                        for (String otherHttpServerPath : HTTPDService.otherHttpServerPaths) {
                                            if (selectVedioPath.startsWith(otherHttpServerPath)) {
                                                tempPath =convertFileUrlToHttpURL( selectVedioPath.replace(HTTPDService.defaultHttpServerPath, ""));
                                            }
                                        }
                                    }
                                    convertSelectedVedioPaths.add(tempPath);
                                }

                                /**
                                 * 准备发送投影的数据
                                 */
                                String ipAddress = NetworkUtils.getLocalHostIp();
                                String httpAddress = "http://" + ipAddress + ":" + HTTPDService.HTTP_PORT;
                                JSONObject o = new JSONObject();
                                JSONArray array = new JSONArray();
                                for (String convertSelectedVedioPath : convertSelectedVedioPaths) {
                                    array.put(httpAddress + convertSelectedVedioPath);
                                }
                                o.put("vedios", array.toString());
                                
                                File jsonFile=new File(HTTPDService.defaultHttpServerPath+"/VideoList.json");
                                if(jsonFile.exists()){
                                	jsonFile.delete();
                                }
                                jsonFile.createNewFile();
                                
                                FileWriter fw = new FileWriter(jsonFile);
                                fw.write(o.toString(), 0, o.toString().length());
                                fw.flush();
                                fw.close();
                                
                                //发送播放地址
                                ClientSendCommandService.msg = "GetVideoList:"+httpAddress+"/VideoList.json";
                                ClientSendCommandService.handler.sendEmptyMessage(4);
//                        	}
                        }
                    } else {
                        Toast.makeText(YinXiangVedioViewActivity.this, "请链接无线网络", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(YinXiangVedioViewActivity.this, "视频获取失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
	 * 特殊字符转换
	 * 
	 * @param url
	 *            url字符串
	 * @return
	 */
	public String convertFileUrlToHttpURL(String url) {
		if (null != url && url.length() > 0) {
			return url.replace("%", "%25").replace(" ", "%20")
					.replace("+", "%2B").replace("#", "%23")
					.replace("&", "%26").replace("=", "%3D")
					.replace("?", "%3F").replace("^", "%5E");
		}
		return url;
	}
    
    
    /**********************************************系统发发重载*********************************************************/

   
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
