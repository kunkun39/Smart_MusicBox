package com.changhong.tvserver.fedit;

/**
 * 文件下载时，屏幕右下角显示提示信息。
 */

import java.util.HashMap;
import java.util.Map;

import com.changhong.tvserver.R;
import com.changhong.tvserver.utils.MyFloatView;
import com.changhong.tvserver.utils.MySharePreferences;
import com.changhong.tvserver.utils.MySharePreferencesData;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FileDowLoadTask {

	private  Handler handler;

	// 定义View控件
	private LinearLayout download_main=null;
	private TextView downLoadTitle = null;
	private TextView downLoadResult = null;
	private ImageView downLoadProgress = null;
	
	private static FileDowLoadTask downLoad=null;

	// 执行文件下载
	final int ACTION_FILE_DOWNLOAD = 1;

	// 文件下载完成
	final int FILE_DOWNLOAD_OK = 2;

	// 文件下载失败
	final int FILE_DOWNLOAD_ERROR = 3;
	
	// 文件已存在 
	final int FILE_IS_EXIST = 4;
	
	// 退出进程显示
	final int ACTION_EXIT = 5;
	
	//定义一个下载控制器对象
	private FileEditManager mFileEditController;
	
	//定义一个浮动小窗口。显示下载进程。
	View mFloatView ;
	
	Context mContext;
	
	
	public static FileDowLoadTask creatFileDownLoad(Context context){
		  if(null == downLoad){
			  downLoad=new FileDowLoadTask(context);
		  }
		  return downLoad;
	}
	private  FileDowLoadTask(Context context) {

	  this.mContext=context;
	  
	  //创建浮动窗口
	    creatFloatView();		
		mFileEditController=FileEditManager.getInstance();

		/**
		 * handler   处理下载相关任务
		 */
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
			
				String eidtType = msg.getData().getString(Configure.EDIT_TYPE);
				String fileUrl = msg.getData().getString(Configure.FILE_URL);
				String  fileName =msg.getData().getString(Configure.FILE_NAME);
;
				switch (msg.what) {

				// 创建新的线程下载音乐文件
				case ACTION_FILE_DOWNLOAD:				
					
					eidtType=convertHttpURLToFileUrl(eidtType);
					fileUrl=convertHttpURLToFileUrl(fileUrl);

					if (null != eidtType && null != fileUrl) {
						// 设置下载标题：
						setDownLoadTitle(eidtType, fileUrl );
						Map<String, Object>params=new HashMap<String,Object>();
						params.put(Configure.EDIT_TYPE, eidtType);
						params.put(Configure.FILE_URL, fileUrl);
						mFileEditController.communicationWithClient(handler,Configure.ACTION_HTTP_DOWNLOAD, params);
					}
					break;
				case FILE_DOWNLOAD_OK:
					
					//如果是闹铃音乐，通知闹铃设置，文件路径	
					if(null != eidtType && eidtType.equals("clock")){
						SaveClockRing(fileUrl);
					}
					
					//更新默认媒体数据库音乐文件索引
					mFileEditController.updateMediaStoreAudio(mContext,fileUrl);
					// 文件现在成功提示
					showResult(fileName+",下载成功");
				
					break;
				case FILE_DOWNLOAD_ERROR:
					// 文件现在失败提示
					showResult(fileName+",下载失败");
				case FILE_IS_EXIST:
					// 文件已存在提示
					showResult(fileName+",已存在");						
					break;
				case ACTION_EXIT:
					// 退出文件下载
					downLoadProgress.clearAnimation();
					MyFloatView.removeView(mFloatView);
					break;
				default:
					break;
				}
			}
		};			
	}
	
	
	/**
	 * 创建浮动窗口，初始化子View控件。
	 */
	private void creatFloatView(){
		
		LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFloatView=inflater.inflate(R.layout.download_progress, null);
		// 初始化View
		download_main=(LinearLayout) mFloatView.findViewById(R.id.download_main);
		downLoadTitle = (TextView) mFloatView.findViewById(R.id.download_title);
		downLoadProgress = (ImageView) mFloatView.findViewById(R.id.download_flag);
		downLoadResult = (TextView) mFloatView.findViewById(R.id.download_result);

//		 //将动画资源文件设置为ImageView的背景  
//		downLoadProgress.setImageResource(R.anim.downloading_anim); 
//		 //获取ImageView背景,此时已被编译成AnimationDrawable 
//        AnimationDrawable anim = (AnimationDrawable) downLoadProgress.getDrawable(); 
//        
//        //判断动画是否运行
//        if(!anim.isRunning()){
//    		  //开始执行动画  
//               anim.start();   
//        }
//        MyFloatView.removeView(floatView)
//		// 指定下载进程显示屏幕下方       
//        MyFloatView.show(mContext, mFloatView,300 , 130, MyFloatView.DIRECTION_RIGHTBOTTOM);
	}

	
	/**
	 * 根据文件类型、文件名实时更新标题。
	 * @param fileType 文件类型
	 * @param fileUrl  文件URL
	 */
	private void setDownLoadTitle(String fileType , String fileUrl) {
       
		if (null == fileType)
			return;

		Resources res=mContext.getResources();
		if (fileType.contains("clock")) {
			
			downLoadTitle.setText(res.getString(R.string.clock_setting));
			
		} else if (fileType.contains("music")) {
			String title=mFileEditController.gotShortFileName(fileUrl);
			downLoadTitle.setText(title+=",正在下载中");
		} else {
			String title=mFileEditController.gotShortFileName(fileUrl);
			downLoadTitle.setText(title+=",正在下载中");
		}

	}

	
	/**
	 * 文件下载结束，显示提示信息
	 * @param result 下载成功，下载失败，文件存在。
	 */
	private void showResult(String result) {
		
		//屏蔽状态显示
		download_main.setVisibility(View.GONE);
		downLoadTitle.setVisibility(View.GONE);
		//设置下载结果
		downLoadResult.setText(result);
		downLoadResult.setVisibility(View.VISIBLE);		
		//设定显示5秒后，退出下载提示，返回。
		handler.sendEmptyMessageDelayed(ACTION_EXIT, 5000);
	}
	
	
	/**
	 *启动文件下载任务
	 * @param fileType 编辑文件类型：音乐、闹铃、视频、文本。
	 * @param fileUrl 下载文件URL
	 */
	public void startDownLoad(String fileType, String fileUrl){
		
		showFloatView();			
		Message msg = new Message();
        msg.what = 1;
        Bundle bundle = new Bundle();
        //当前文件类型: music;
        bundle.putString(Configure.EDIT_TYPE, fileType);
        bundle.putString(Configure.FILE_URL, fileUrl);
        msg.setData(bundle);
        handler.sendMessage(msg);
	}
	
	
	
	private void showFloatView(){
		
		//屏蔽状态显示
		download_main.setVisibility(View.VISIBLE);
		downLoadTitle.setVisibility(View.VISIBLE);
		//设置下载结果
		downLoadResult.setText("");
		downLoadResult.setVisibility(View.GONE);		
		 //将动画资源文件设置为ImageView的背景  
		downLoadProgress.setImageResource(R.anim.downloading_anim); 
		 //获取ImageView背景,此时已被编译成AnimationDrawable 
        AnimationDrawable anim = (AnimationDrawable) downLoadProgress.getDrawable();        
        //判断动画是否运行
        if(!anim.isRunning()){
    		  //开始执行动画  
               anim.start();   
        }
        MyFloatView.removeView(mFloatView);
		// 指定下载进程显示屏幕下方       
        MyFloatView.show(mContext, mFloatView,300 , 130, MyFloatView.DIRECTION_RIGHTBOTTOM);
	}
	
	/**
	 * 特殊字符还原
	 * @param url  url字符串
	 * @return
	 */
	public  String convertHttpURLToFileUrl(String url) {
        if (null !=url && url.length()>0) {
//            return url.replace("%", "%25").replace(" ", "%20").replace("+", "%2B").replace("#", "%23").replace("&", "%26").replace("=", "%3D").replace("?", "%3F").replace("^", "%5E");
            return url.replace("%25", "%").replace("%20"," ").replace("%2B","+").replace( "%23","#").replace( "%26","&").replace("%3D","=").replace("%3F","?").replace("%5E","^");

        }
        return url;
    }

	
	
	/**
	 * 保存闹铃铃声
	 * @param fileUrl 闹铃铃声资源定位符。
	 */
	private void SaveClockRing(String fileUrl ) {

		MySharePreferences mysharepreferences1 = new MySharePreferences(mContext);
		MySharePreferencesData mysharepreferencesdata = new MySharePreferencesData();
		mysharepreferencesdata.clockRing =fileUrl;
		mysharepreferences1.SaveMySharePreferences(mysharepreferencesdata);	
	}


	
	
	/**
	 * 下载资源释放
	 */
	protected void destroy() {
		//释放下载线程资源
		mFileEditController.destroy();
	}
	
	
	
	

}
