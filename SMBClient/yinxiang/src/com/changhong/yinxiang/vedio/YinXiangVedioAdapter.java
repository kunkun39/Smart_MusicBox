package com.changhong.yinxiang.vedio;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.YinXiangVedioViewActivity;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

/**
 * Created by Administrator on 15-5-11.
 */
public class YinXiangVedioAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private List<?> vedios;


	public static List<String> selectVedioPaths = new ArrayList<String>();

	/**
	 * 线程池获取图片
	 * 
	 * @param context
	 * @param keyWords
	 */
	private ExecutorService executorService = Executors.newFixedThreadPool(5);

	public YinXiangVedioAdapter(Context context) {
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		YinXaingVedioProvider provider = new YinXaingVedioProvider(context);
		vedios = provider.getList();

		selectVedioPaths.clear();
	}

	public int getCount() {
		return vedios.size();
	}

	public Object getItem(int item) {
		return item;
	}

	public long getItemId(int id) {
		return id;
	}

	// 创建View方法
	public View getView(int position, View convertView, ViewGroup parent) {
		DataWapper wapper = null;

		if (convertView == null) {
			wapper = new DataWapper();
			// 获得view
			convertView = inflater.inflate(R.layout.yinixiang_vedio_list_item,
					null);
			wapper.vedioImage = (ImageView) convertView
					.findViewById(R.id.yinxiang_vedio_item_image);
			wapper.vedioName = (TextView) convertView
					.findViewById(R.id.yinxiang_vedio_item_name);
			wapper.fullPath = (TextView) convertView
					.findViewById(R.id.yinxiang_vedio_item_path);
			wapper.vedioChecked = (CheckBox) convertView
					.findViewById(R.id.yinxiang_vedio_item_checked);

			// 组装view
			convertView.setTag(wapper);
		} else {
			wapper = (DataWapper) convertView.getTag();
		}

		YinXiangVedio yinXiangVedio = (YinXiangVedio) vedios.get(position);

		String displayName = yinXiangVedio.getDisplayName();
		String totalTime = DateUtils
				.getTimeShow(yinXiangVedio.getDuration() / 1000);
		final String vedioPath = yinXiangVedio.getPath();

		wapper.vedioName.setText(displayName + "\n" + totalTime);
		wapper.fullPath.setText(vedioPath);

		String vedioImagePath = DiskCacheFileManager
				.isSmallImageExist(vedioPath);
		if (!vedioImagePath.equals("")) {
			MyApplication.imageLoader.displayImage("file://" + vedioImagePath,
					wapper.vedioImage, MyApplication.viewOptions);
			wapper.vedioImage.setScaleType(ImageView.ScaleType.FIT_XY);
		} else {
			synchronizImageLoad(wapper.vedioImage, vedioPath);
		}

		final boolean isChecked = selectVedioPaths.contains(vedioPath);
		wapper.vedioChecked.setChecked(isChecked);
		wapper.vedioChecked.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox check=(CheckBox)v;
				if (check.isChecked()) {
					selectVedioPaths.add(vedioPath);
				} else {
					selectVedioPaths.remove(vedioPath);
				}
				YinXiangVedioViewActivity.vedioSelectedInfo
						.setText("你共选择了" + selectVedioPaths.size()
								+ "部视频");
			}
		});

		return convertView;
	}

	private void synchronizImageLoad(final ImageView imageView,
			final String path) {
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path,
						MediaStore.Images.Thumbnails.MINI_KIND);

				if (bitmap != null && imageView != null) {
					imageView.setImageBitmap(bitmap);
					imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				}
				DiskCacheFileManager.saveSmallImage(bitmap, path);
			}
		});

	}


	private final class DataWapper {

		// 视频的图标
		public ImageView vedioImage;

		// 视频的名字
		public TextView vedioName;

		// 视频是否被选中
		public CheckBox vedioChecked;

		// 视屏的全路径
		public TextView fullPath;

	}
}
