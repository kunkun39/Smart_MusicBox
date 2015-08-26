package com.changhong.yinxiang.music;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.serializer.MapSerializer;
import com.baidu.android.common.logging.Log;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.AppConfig;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.YinXiangMusicViewActivity;
import com.changhong.yinxiang.activity.YinXiangPictureCategoryActivity;
import com.changhong.yinxiang.activity.YinXiangPictureDetailsActivity;
import com.changhong.yinxiang.nanohttpd.HTTPDService;
import com.changhong.yinxiang.view.FileEditDialog;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

/**
 * Created by Administrator on 15-5-11.
 */
public class CopyOfYinXiangMusicAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private List<YinXiangMusic> musicsAll;
	private List<YinXiangMusic> musicsAct = new ArrayList<YinXiangMusic>();
	private Map<String, String> checkStateMap;

	public static List<YinXiangMusic> selectMusics = new ArrayList<YinXiangMusic>();
	private Context context;
	private String keyStr;
	private String displayName, musicPath;
	private boolean checkSetFlag = false;

	/**
	 * YD add 20150806 for fileEdit 音频文件编辑
	 */
	// 文件编辑对话框
	FileEditDialog fileEditDialog = null;

	public CopyOfYinXiangMusicAdapter(Context context, String keyWords) {
		this.context = context;
		this.keyStr = keyWords;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		YinXiangMusicProvider provider = new YinXiangMusicProvider(context);
		musicsAll = provider.getList();
		checkStateMap = new TreeMap<String, String>();
		musicFilter();
		selectMusics.clear();

	}

	public int getCount() {
		return musicsAct.size();
	}

	public Object getItem(int item) {
		return item;
	}

	public long getItemId(int id) {
		return id;
	}

	// 创建View方法
	public View getView(final int position, View convertView, ViewGroup parent) {
		DataWapper wapper = null;
		if (convertView == null) {
			wapper = new DataWapper();

			// 获得view
			convertView = inflater.inflate(R.layout.yinixiang_vedio_list_item,
					null);
			wapper.musicImage = (ImageView) convertView
					.findViewById(R.id.yinxiang_vedio_item_image);
			wapper.musicName = (TextView) convertView
					.findViewById(R.id.yinxiang_vedio_item_name);
			wapper.fullPath = (TextView) convertView
					.findViewById(R.id.yinxiang_vedio_item_path);
			wapper.musicChecked = (CheckBox) convertView
					.findViewById(R.id.yinxiang_vedio_item_checked);

			wapper.editBtn = (ImageView) convertView
					.findViewById(R.id.yinxiang_vedio_item_edit);

			wapper.musicImage.setTag(position);
			// 组装view
			convertView.setTag(wapper);
		} else {
			wapper = (DataWapper) convertView.getTag();
		}

		final YinXiangMusic yinXiangMusic = (YinXiangMusic) musicsAct
				.get(position);

		displayName = yinXiangMusic.getTitle();
		musicPath = yinXiangMusic.getPath();

		wapper.musicName.setText(displayName);
		wapper.fullPath.setText(musicPath);
		String musicImagePath = DiskCacheFileManager
				.isSmallImageExist(musicPath);
		if (!musicImagePath.equals("")) {
			MyApplication.imageLoader.displayImage("file://" + musicImagePath,
					wapper.musicImage, MyApplication.viewOptions);
			wapper.musicImage.setScaleType(ImageView.ScaleType.FIT_XY);
		} else {
			YinXiangSetImageView.getInstance().setContext(context);
			YinXiangSetImageView.getInstance().startExecutor(wapper.musicImage,
					yinXiangMusic);
		}

		final boolean isChecked = selectMusics.contains(yinXiangMusic);
		wapper.musicChecked.setChecked(isChecked);

		wapper.musicChecked.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox check = (CheckBox) v;
				if (check.isChecked()) {
					if (!selectMusics.contains(yinXiangMusic))
						selectMusics.add(yinXiangMusic);
				} else {
					selectMusics.remove(yinXiangMusic);
				}
//				YinXiangMusicViewActivity.musicSelectedInfo.setText("你共选择了"
//						+ selectMusics.size() + "首歌曲");
			}
		});

		// YD add 20150805 for file edit
		wapper.editBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				if (fileEditDialog != null && !fileEditDialog.isShowing()) {
					fileEditDialog.show();
				}
			}
		});

		// wapper.musicChecked
		// .setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// // TODO Auto-generated method stub
		// if (isChecked) {
		// if (!selectMusics.contains(yinXiangMusic)) {
		// selectMusics.add(yinXiangMusic);
		// checkStateMap.put(String.valueOf(position), String.valueOf(1));
		// }
		// } else {
		// selectMusics.remove(yinXiangMusic);
		// checkStateMap.remove(String.valueOf(position));
		// }
		// Log.i("mmmm", "size=" + selectMusics.size());
		// YinXiangMusicViewActivity.musicSelectedInfo
		// .setText("你共选择了" + checkStateMap.size()
		// + "首歌曲");
		// }
		// });
		return convertView;
	}

	private void musicFilter() {
		for (int i = 0; i < musicsAll.size(); i++) {
			YinXiangMusic music = musicsAll.get(i);
			if (TextUtils.isEmpty(keyStr)) {
				musicsAct = musicsAll;
				break;
			} else if (music.getTitle().contains(keyStr)) {
				musicsAct.add(music);
			}
		}
	}

	private void createFileEditDialog() {
		if (fileEditDialog == null) {
			fileEditDialog = new FileEditDialog(context);
			fileEditDialog.setCanceledOnTouchOutside(true);
		
			// 设置盒子端闹铃铃声
			fileEditDialog.edit_clock	.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Toast.makeText(context, "闹铃设置成功", Toast.LENGTH_SHORT).show();
						}
					});

			fileEditDialog.edit_cancle
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});

			fileEditDialog.edit_copy1
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

						}
					});
		}
	}
	
	
	/**************************************************************YD add  20150806 发送文件编辑信息到音响端*******************************************************************************/
	
	private void sendFileMSG2Audio(String musicPath){
      
        if (!NetworkUtils.isWifiConnected(context)) {
            Toast.makeText(context, "请链接无线网络", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!StringUtils.hasLength(ClientSendCommandService.serverIP)) {
            Toast.makeText(context, "手机未连接机顶盒，请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         * 封装发送信息。
         */
        try {
            MyApplication.vibrator.vibrate(100);

            //获取IP和外部存储路径
            String  ipAddress = NetworkUtils.getLocalHostIp();
            String httpAddress = "http://" + ipAddress + ":" + HTTPDService.HTTP_PORT;
            
            
            String newMusicPath = null;
            if (musicPath.startsWith(HTTPDService.defaultHttpServerPath)) {
            	newMusicPath = musicPath.replace(HTTPDService.defaultHttpServerPath, "").replace(" ", "%20");
            } else {
                for (String otherHttpServerPath : HTTPDService.otherHttpServerPaths) {
                    if (musicPath.startsWith(otherHttpServerPath)) {
                    	newMusicPath = musicPath.replace(otherHttpServerPath, "").replace(" ", "%20");
                    }
                }
            }

            String tmpHttpAddress = httpAddress + newMusicPath;

            
            //判断URL是否符合规范，如果不符合规范，就1重命名文件
            try {
                URI.create(tmpHttpAddress);
            } catch (Exception e) {
                try {
                    /**
                     * 创建信的文件
                     */
                    File illegeFile = new File(musicPath);
                    String fullpath = illegeFile.getAbsolutePath();
                    String filename = illegeFile.getName();
                    String filepath = fullpath.replace(File.separator + filename, "");
                    String[] tokens = StringUtils.delimitedListToStringArray(filename, ".");
                    String filenameSuffix = tokens[tokens.length - 1];

                    String newFile = filepath + File.separator + StringUtils.getRandomString(15) + "." + filenameSuffix;
                    Runtime.getRuntime().exec("mv " + fullpath + " " + newFile);


                  

                    /**
                     * 更改Content Provider的文件
                     */
                    ContentResolver mContentResolver = context.getContentResolver();
                    Uri mAudioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Audio.Media.DATA, newFile);
                    mContentResolver.update(mAudioUri, values, MediaStore.Images.Media.DATA + " = '" + fullpath + "'", null);
                    
                } catch (Exception e1) {
                    e.printStackTrace();
                    Toast.makeText(context, "对不起，音乐文件获取有误，不能正常操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            //封装文件为json格式
            JSONObject o = new JSONObject();
            JSONArray array = new JSONArray();
            //music urls
            array.put(0, tmpHttpAddress);
            File musicFile = new File(musicPath);
            array.put(1, tmpHttpAddress);
            o.put("urls", array);
            //client ip
            o.put("client_ip", ipAddress);

            //发送播放地址
            ClientSendCommandService.msg = o.toString();
            ClientSendCommandService.handler.sendEmptyMessage(4);
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "音乐文件获取失败", Toast.LENGTH_SHORT).show();
        }
    }
	
	
	
	
	

	private final class DataWapper {

		// 音频的图标
		public ImageView musicImage;

		// 音频的名字
		public TextView musicName;

		// 音频是否被选中
		public CheckBox musicChecked;

		// 音屏的全路径
		public TextView fullPath;

		// YD add 20150805 音频的编辑按钮
		public ImageView editBtn;

	}
}
