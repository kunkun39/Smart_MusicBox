package com.changhong.yinxiang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.yinxiang.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 15-5-12.
 */
public class YinXiangPictureViewActivity extends Activity {
    /**************************************************IP连接部分*******************************************************/

    public static TextView title = null;
    public Button back;
    public Button listClients;
    private ListView clients = null;
    private BoxSelectAdapter ipAdapter;

    /************************************************图片加载部分*******************************************************/

    private GridView listViewlocal;

    /**
     * 传过来需要浏览的图片
     */
    private List<String> imagePaths;

    /**
     * 图片加载的适配器
     */
    private ImageAdapterLocal imageAdapter = new ImageAdapterLocal();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        initView();

        initEvent();
    }

    private void initData() {
        imagePaths = getIntent().getStringArrayListExtra("imagePaths");
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_yinxinag_picture_view);

        title = (TextView) findViewById(R.id.title);
        clients = (ListView) findViewById(R.id.clients);
        listClients = (Button) findViewById(R.id.btn_list);
        back = (Button) findViewById(R.id.btn_back);

        /**
         * 图片容器
         */
        listViewlocal = (GridView) findViewById(R.id.select_picture);
        listViewlocal.setAdapter(imageAdapter);

    }

    private void initEvent() {
        /**
         * IP连接部分
         */
        ipAdapter = new BoxSelectAdapter(YinXiangPictureViewActivity.this, ClientSendCommandService.serverIpList);
        clients.setAdapter(ipAdapter);
        clients.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clients.setVisibility(View.GONE);
                return false;
            }
        });
        clients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList.get(arg2);
                ClientSendCommandService.titletxt=ClientSendCommandService.getCurrentConnectBoxName();
                title.setText(ClientSendCommandService.getCurrentConnectBoxName());
                ClientSendCommandService.handler.sendEmptyMessage(2);
                clients.setVisibility(View.GONE);
            }
        });
        listClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                if (ClientSendCommandService.serverIpList.isEmpty()) {
                    Toast.makeText(YinXiangPictureViewActivity.this, "未获取到服务器IP", Toast.LENGTH_LONG).show();
                } else {
                    clients.setVisibility(View.VISIBLE);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                finish();
            }
        });

        /**
         * 图片部分
         */
        listViewlocal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                MyApplication.vibrator.vibrate(100);
                /**
                 * 显示图片预览效果
                 */
                Intent intent = new Intent();
                intent.setClass(YinXiangPictureViewActivity.this, YinXiangPictureDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putStringArrayList("imagePaths", new ArrayList<String>(imagePaths));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public class ImageAdapterLocal extends BaseAdapter {
        @Override
        public int getCount() {
            return imagePaths.size();
        }

        @Override
        public Object getItem(int position) {
            return imagePaths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null || ((ViewHolder)(view.getTag())).index != position) {
                view = getLayoutInflater().inflate(R.layout.activity_yinxiang_picture_item, parent, false);
                holder = new ViewHolder();

                holder.imageView = (ImageView) view.findViewById(R.id.grid_picture);
                holder.index = position;

                MyApplication.imageLoader.displayImage("file://" + imagePaths.get(position), holder.imageView, MyApplication.viewOptions);
                Log.i("IMAGE_VIEW", imagePaths.get(position));
                view.setTag(holder);
            }

            return view;
        }
    }

    private class ViewHolder {
        ImageView imageView;
        int index;
    }

    /**********************************************系统发发重载*********************************************************/

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
