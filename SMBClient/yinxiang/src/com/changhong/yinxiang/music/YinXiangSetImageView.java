package com.changhong.yinxiang.music;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class YinXiangSetImageView {

	public static YinXiangSetImageView setImageView;
	private ExecutorService executorService =Executors.newFixedThreadPool(5);
	private Context context;
	
	public static YinXiangSetImageView getInstance(){
		if(null==setImageView){
			setImageView=new YinXiangSetImageView();
		}
		return setImageView;
	}
	public void setContext(Context context){
		this.context=context;
	}
	
	public void startExecutor(final ImageView iv,final YinXiangMusic yxMusic){
		executorService.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Bitmap bitmap = YinXiangMediaUtil.getArtwork(context,
						yxMusic.getId(), yxMusic.getAlbumId(),
						true, false);
				if (bitmap != null && iv != null) {
					iv.setImageBitmap(bitmap);
					iv.setScaleType(ImageView.ScaleType.FIT_XY);
				}
				DiskCacheFileManager.saveSmallImage(bitmap,
						yxMusic.getPath());
			}
		});
	}
}
