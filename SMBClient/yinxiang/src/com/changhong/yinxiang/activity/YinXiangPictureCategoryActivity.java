package com.changhong.yinxiang.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.AppConfig;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.yinxiang.R;

/**
 * Created by Jack Wang
 */
public class YinXiangPictureCategoryActivity extends BaseActivity {
    

    /**************************************************图片部分*********************************************************/

    /**
     * 顺序记录文件夹名称, 初始化容量，避免空间分配造成的性能影响
     */
    private static List<String> packageNames = new ArrayList<String>(30);
    /**
     * 本地文件夹缓存对象
     */
    public static Map<String, List<String>> packageList = new HashMap<String, List<String>>(30);
    /**
     * 文件夹选项适配器
     */
    private PackageAdapterLocal packageAdapter;
    /**
     * 本地文宽夹显示VIEW
     */
    private GridView listPackageView;

    /**
     * 处理主线程消息
     */
    public static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initView() {
        setContentView(R.layout.activity_yinxiang_picture_category);

        /**
         * IP连接部分
         */
        title = (TextView) findViewById(R.id.title);
        back = (ImageView) findViewById(R.id.btn_back);
        clients = (ListView) findViewById(R.id.clients);
        listClients = (Button) findViewById(R.id.btn_list);
        

        /**
         * 图片部分
         */
        listPackageView = (GridView) findViewById(R.id.select_package);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        packageAdapter = new PackageAdapterLocal();
                        listPackageView.setAdapter(packageAdapter);
                        packageAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    protected void initData() {
         super.initData();
        /**
         * 图片部分
         */
        listPackageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                MyApplication.vibrator.vibrate(100);
                String packageName = (String) packageNames.get(position);
                List<String> imagePaths = packageList.get(packageName);

                //跳转到每个文件夹下面的图片
                Intent intent = new Intent();
                intent.setClass(YinXiangPictureCategoryActivity.this, YinXiangPictureViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("imagePaths", new ArrayList<String>(imagePaths));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        
        /**
         * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
         */
        getPackageFromLocal();
    }


    private void getPackageFromLocal() {
        packageNames.clear();
        packageList.clear();

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = YinXiangPictureCategoryActivity.this.getContentResolver();

                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?", new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED + " desc");

                while (mCursor.moveToNext()) {
                    String imagePath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    // 获取该图片的父路径名
                    String[] tokens = StringUtils.delimitedListToStringArray(imagePath, File.separator);
                    String packageName = tokens[tokens.length - 2];

                    //组装相同路径下的package
                    List<String> files = packageList.get(packageName);
                    if (files == null) {
                        files = new ArrayList<String>();
                        packageNames.add(packageName);
                    }
                    files.add(imagePath);
                    packageList.put(packageName, files);
                }
                mCursor.close();

                //把拍照文件夹放到首位，在兄配置中，配置了有哪些默认的拍照文件夹
                List<String> newPackageNames = new ArrayList<String>();
                for (String packageName : packageNames) {
                    if (AppConfig.MOBILE_CARMERS_PACKAGE.contains(packageName.toLowerCase())) {
                        newPackageNames.add(0, packageName);
                    } else {
                        newPackageNames.add(packageName);
                    }
                }
                packageNames = newPackageNames;

                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0);
            }
        }).start();

    }

    /***********************************************图片部分************************************************************/

    public class PackageAdapterLocal extends BaseAdapter {

        @Override
        public int getCount() {
            return packageNames.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final PackageViewHolder holder;
            View view = convertView;

            if (view == null || ((PackageViewHolder)(view.getTag())).index != position) {
                view = getLayoutInflater().inflate(R.layout.activity_yinxiang_category_item, parent, false);
                holder = new PackageViewHolder();

                holder.packageName = (TextView) view.findViewById(R.id.package_name);
                holder.packageFileSize = (TextView) view.findViewById(R.id.package_file_size);
                holder.imageView1 = (ImageView) view.findViewById(R.id.package_picture_1);
                holder.imageView2 = (ImageView) view.findViewById(R.id.package_picture_2);
                holder.imageView3 = (ImageView) view.findViewById(R.id.package_picture_3);
                holder.imageView4 = (ImageView) view.findViewById(R.id.package_picture_4);
                holder.index = position;

                view.setTag(holder);

                //准备数据
                String packageName = packageNames.get(position);
                List<String> images = packageList.get(packageName);
                int size = images.size();

                //设置文字
                holder.packageName.setText(StringUtils.getShortString(packageName, 12));
                holder.packageFileSize.setText(size + "张");

                //设置图片
                try {
                    if (images.size() == 1) {
                        displayImage(holder.imageView1, images.get(0));
                        holder.imageView2.setBackgroundResource(R.drawable.ic_stub); 
                        holder.imageView3.setBackgroundResource(R.drawable.ic_stub); 
                        holder.imageView4.setBackgroundResource(R.drawable.ic_stub); 
                    }

                    if (images.size() == 2) {
                        displayImage(holder.imageView1, images.get(0));
                        displayImage(holder.imageView2, images.get(1));
                        holder.imageView3.setBackgroundResource(R.drawable.ic_stub); 
                        holder.imageView4.setBackgroundResource(R.drawable.ic_stub); 
                    }

                    if (images.size() == 3) {
                        displayImage(holder.imageView1, images.get(0));
                        displayImage(holder.imageView2, images.get(1));
                        displayImage(holder.imageView3, images.get(2));
                        holder.imageView4.setBackgroundResource(R.drawable.ic_stub); 

                    }

                    if (images.size() >= 4) {
                        displayImage(holder.imageView1, images.get(0));
                        displayImage(holder.imageView2, images.get(1));
                        displayImage(holder.imageView3, images.get(2));
                        displayImage(holder.imageView4, images.get(3));
                    }
                } catch (Exception e) {
                }

                view.setTag(holder);
            }

            return view;
        }
    }

    static class PackageViewHolder {

        TextView packageName;

        TextView packageFileSize;

        ImageView imageView1;

        ImageView imageView2;

        ImageView imageView3;

        ImageView imageView4;

        int index;
    }

    private void displayImage(ImageView imageView, String path) {
        MyApplication.imageLoader.displayImage("file://" + path, imageView, MyApplication.viewOptions);
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
