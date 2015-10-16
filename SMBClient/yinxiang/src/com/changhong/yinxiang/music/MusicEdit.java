package com.changhong.yinxiang.music;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.YinXiangMusicViewActivity;
import com.changhong.yinxiang.nanohttpd.HttpDownloader;
import com.changhong.yinxiang.utils.FileUtil;
import com.changhong.yinxiang.view.FileDownLoadDialog;
import com.changhong.yinxiang.view.FileEditDialog;
import com.changhong.yinxiang.view.MyProgressDialog;

public class MusicEdit {
	
	// 文件编辑对话框
	FileEditDialog fileEditDialog = null;	
	// 文件下载提示框
	FileDownLoadDialog  fileDownLoadDialog=null;
	// 文件删除提示对话框：
	Dialog cancleDialog = null;
	// 文件删除提示对话框：
	Dialog reNameDialog = null;
	
	//远程处理进度条
	MyProgressDialog MProgressDialog =null;

	EditText mEditText = null;
	FileUtil mFileUtil = null;

	Context mContext;
	YinXiangMusic mEditMusic;
	int curStorageDev;
	Handler parentHandler;

	public MusicEdit(Context context, Handler handler) {
		this.mContext = context;
		this.parentHandler = handler;
		mEditText = new EditText(mContext);
		mEditText.setHint("新的文件名：");
		mFileUtil = new FileUtil();
		createFileEditDialog();
	}

	public void createFileEditDialog() {

		if (null ==fileEditDialog) {

			fileEditDialog = new FileEditDialog(mContext);
			fileEditDialog.setCanceledOnTouchOutside(true);
			// 设置盒子端闹铃铃声
			fileEditDialog.edit_clock
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							MyApplication.vibrator.vibrate(100);
//							parentHandler.sendEmptyMessage(2);
							Toast.makeText(mContext, "功能建设中······", Toast.LENGTH_SHORT).show();
						}
					});

			// 删除文件
			fileEditDialog.edit_remove
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							MyApplication.vibrator.vibrate(100);
							String fileName = mEditMusic.getTitle();
							removeFile(fileName);
							// parentHandler.sendEmptyMessage(5);

						}
					});
			// copy设备从手机到音响指定文件目录
			fileEditDialog.edit_copy1
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							MyApplication.vibrator.vibrate(100);
							String musicPath = mEditMusic.getPath();
							parentHandler.sendEmptyMessage(3);
						}
					});

			fileEditDialog.edit_rename
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							MyApplication.vibrator.vibrate(100);
							String fileName = mEditMusic.getTitle();
							reNameFile(fileName);
							// parentHandler.sendEmptyMessage(4);

						}
					});
			
			//如果是音响设备，修改edit_copy1背景图
		}
		
		if(null == fileDownLoadDialog){
			  fileDownLoadDialog=new FileDownLoadDialog(mContext);
		}
		
		if(null == MProgressDialog){
			    MProgressDialog=new MyProgressDialog(mContext);
		}
		
	}

	public void showEditDialog(YinXiangMusic music, int storageDev) {
		this.mEditMusic = music;
		this.curStorageDev = storageDev;
		if (fileEditDialog != null && !fileEditDialog.isShowing()) {
			fileEditDialog.show(curStorageDev);
		}
	}
	
	
	public void showProgressDialog(String msg ) {
		
		if(!StringUtils.hasLength(msg))return;
		
		if(msg.startsWith("fileEdit:")){
			if (fileDownLoadDialog != null && !fileDownLoadDialog.isShowing()) {
				fileDownLoadDialog.show(msg.substring(10));
			}
		}else{
			if (MProgressDialog != null && !MProgressDialog.isShowing()) {
				MProgressDialog.show(msg);
			}
		}
	}
	
   
	public void closeProgressDialog( ) {
		
		if (fileDownLoadDialog != null && fileDownLoadDialog.isShowing()) {
			fileDownLoadDialog.close();
			fileDownLoadDialog.cancel();
		}
		if (MProgressDialog != null && MProgressDialog.isShowing()) {
			MProgressDialog.cancel();
		}
	}
	

   
	/**
	 * 删除指定文件对话框
	 * 
	 * @param filePath  文件路径
	 */
	private void removeFile(String fileName) {
		if (null == cancleDialog) {
			AlertDialog.Builder builder = new Builder(mContext);
			cancleDialog = builder
					.setPositiveButton("确认", new OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							cancleDialog.dismiss();
							// 获取焦点文件路径
							String removeFilePath = mEditMusic.getPath();
							
							// 删除文件 本地文件=1、远程文件=2
							if (1 == curStorageDev) {
								
								//本地文件删除，成功，则，更新媒体库文件
								if (mFileUtil.removeFileFromSDCard(removeFilePath)) {
									upDateMediaStoreFile(removeFilePath);
								} else {
									removeFilePath = "";
								}
							}
							Message msg = parentHandler.obtainMessage();
							msg.what = YinXiangMusicViewActivity.FILE_EDIT_REMOVE;
							msg.obj = removeFilePath;
							parentHandler.sendMessage(msg);
						}
					}).setNegativeButton("取消", new OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							cancleDialog.dismiss();
						}
					}).create();
		}
		cancleDialog.setTitle("是否删除该歌曲  ==> " + fileName);
		cancleDialog.show();
	}

	/**
	 * 重命名编辑框
	 */
	private void reNameFile(String fileName) {
		if (null == reNameDialog) {
			AlertDialog.Builder builder = new Builder(mContext);
			reNameDialog = builder.setView(mEditText)
					.setPositiveButton("确认", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO 自动生成的方法存根
							String newName = mEditText.getText().toString();
							String newFilePath="";
							if (StringUtils.hasLength(newName)) {
								reNameDialog.dismiss();
								// 获取焦点文件
								String filePath = mEditMusic.getPath();
								newFilePath=mFileUtil.getNewFilePath(filePath, newName);
								if (1 == curStorageDev) {							
									// 本地重命名文件，成功，则，更新媒体库记录。
									if (mFileUtil.reNameFile(	filePath, newName)) {		
										upDateMediaStoreFile(filePath,newFilePath);
									} else {									
										newName = "";
									}
								}
								Message msg = parentHandler.obtainMessage();
								msg.what = YinXiangMusicViewActivity.FILE_EDIT_RENAME;
								Bundle bundle=new Bundle();
								bundle.putString("newName", newName);
								bundle.putString("newFilePath", newFilePath);
								msg.setData(bundle);
								parentHandler.sendMessage(msg);
							}
						}
					}).setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO 自动生成的方法存根
							reNameDialog.dismiss();
						}
					}).create();
		}
		reNameDialog.setTitle("歌曲:" + fileName + "，更名为 ==>");
		reNameDialog.show();
	}

	/**
	 * 媒体库文件更新、删除。
	 * 
	 * @param fileUrl  文件定位符
	 */
	public void upDateMediaStoreFile(String fileUrl) {
	   
		if (fileUrl.toLowerCase().startsWith("http://") || fileUrl.toLowerCase().startsWith("https://")) {
			fileUrl=convertHttpURLToFileUrl(fileUrl);
			fileUrl=mFileUtil.convertHttpUrlToLocalFilePath(fileUrl);
		}
	   mFileUtil.updateGallery(mContext, fileUrl);      
	}
	
	/**
	 * MediaStoreFile 中增加一个音乐文件记录
	 * @param music
	 */
	public void upDateMediaStoreFile(YinXiangMusic music) {
		
		if(null == music)return;
		
		String fileUrl=music.getFileUrl();
		if (fileUrl.toLowerCase().startsWith("http://") || fileUrl.toLowerCase().startsWith("https://")) {
			fileUrl=convertHttpURLToFileUrl(fileUrl);
			fileUrl=mFileUtil.convertHttpUrlToLocalFilePath(fileUrl);
			music.setPath(fileUrl);
		}	
	   mFileUtil.updateGallery(mContext, music);      
	}
	

	public void upDateMediaStoreFile(String oldFile, String newFile) {
	   mFileUtil.updateGallery(mContext, oldFile,newFile);
       
	}
	
	
	public String getFileUrlByName(String fileUrl,String newName){		  
		return mFileUtil.getNewFilePath(fileUrl, newName);
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

	
	
	
}
