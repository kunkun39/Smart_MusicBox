package com.changhong.yinxiang.activity;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
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

public class YinXiangFMActivity extends Activity {

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

    /**************************************************频道部分*******************************************************/

    private GridView FMlist = null;
    private FMAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fm_switch);
        initViewAndEvent();
    }

    private void initViewAndEvent() {
    	FMlist = (GridView) findViewById(R.id.fmlist);
        title = (TextView) findViewById(R.id.title);
        clients = (ListView) findViewById(R.id.clients);
        list = (Button) findViewById(R.id.btn_list);
        back = (Button) findViewById(R.id.btn_back);

        /**
         * IP part
         */
        ipAdapter = new BoxSelectAdapter(YinXiangFMActivity.this, ClientSendCommandService.serverIpList);
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
                    Toast.makeText(YinXiangFMActivity.this, "没有发现长虹智能机顶盒，请确认盒子和手机连在同一个路由器?", Toast.LENGTH_LONG).show();
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
        adapter=new FMAdapter(YinXiangFMActivity.this);
        FMlist.setAdapter(adapter);
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

    /**
     * FM名称
     */
    private class FMAdapter extends BaseAdapter {

        private LayoutInflater minflater;

        public FMAdapter(Context context) {
            this.minflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return ClientSendCommandService.serverFMInfo.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            /**
             * VIEW HOLDER的配置
             */
            final ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = minflater.inflate(R.layout.activity_fm_item, null);
                vh.FMname = (TextView) convertView.findViewById(R.id.fmtxt);
                vh.FMplay = (Button) convertView.findViewById(R.id.btn_fm);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            vh.FMname.setText(ClientSendCommandService.serverFMInfo.get(position));
            vh.FMplay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					MyApplication.vibrator.vibrate(100);
					ClientSendCommandService.msg = "fm:"+ClientSendCommandService.serverFMInfo.get(position);
					ClientSendCommandService.handler.sendEmptyMessage(1);
				}
			});
            return convertView;
        }

        public final class ViewHolder {
            public TextView FMname;
            public Button FMplay;
        }
    }

}
