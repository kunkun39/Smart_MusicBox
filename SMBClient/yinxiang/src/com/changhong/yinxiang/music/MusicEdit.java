package com.changhong.yinxiang.music;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.utils.FileUtil;
import com.changhong.yinxiang.view.FileEditDialog;

public class MusicEdit {
	// 文件编辑对话框
	FileEditDialog fileEditDialog = null;
	// 文件删除提示对话框：
	Dialog cancleDialog = null;
	// 文件删除提示对话框：
	Dialog reNameDialog = null;
	EditText mEditText = null;
	FileUtil mFileUtil = null;
	
	Context mContext;
	YinXiangMusic mEditMusic;
	Handler parentHandler;

	public MusicEdit(Context context, Handler handler ) {
		this.mContext=context;
		this.parentHandler=handler;
		mEditText = new EditText(mContext);
		mEditText.setHint("新的文件名：");
		mFileUtil = new FileUtil();
		createFileEditDialog();
	}

	public void createFileEditDialog() {
	
		
		if (fileEditDialog == null) {
			
			fileEditDialog = new FileEditDialog(mContext);
			fileEditDialog.setCanceledOnTouchOutside(true);

			// 设置盒子端闹铃铃声
			fileEditDialog.edit_clock.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							MyApplication.vibrator.vibrate(100);
							parentHandler.sendEmptyMessage(2);
						}
					});

			// 删除文件
			fileEditDialog.edit_cancle
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							MyApplication.vibrator.vibrate(100);
							String fileName = mEditMusic.getTitle();
							removeFile(fileName);
							parentHandler.sendEmptyMessage(5);

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
							parentHandler.sendEmptyMessage(4);

						}
					});
		}
	}

	public void showEditDialog(YinXiangMusic music) {
		this.mEditMusic=music;
		if (fileEditDialog != null && !fileEditDialog.isShowing()) {
			fileEditDialog.show();
		}
	}

	/**
	 * 删除指定文件
	 * 
	 * @param filePath
	 *            文件路径
	 */
	private void removeFile(String fileName) {
		if (null == cancleDialog) {
			AlertDialog.Builder builder = new Builder(
					mContext);
			cancleDialog = builder
					.setPositiveButton("确认", new OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							cancleDialog.dismiss();
							// 获取焦点文件
							String removeFilePath = mEditMusic.getPath();
							// 删除文件
							boolean result = mFileUtil
									.removeFileFromSDCard(removeFilePath);
							if (result) {
								upDateMediaStore(mFileUtil,"remove", removeFilePath,null);			
							}else{
								Toast.makeText(
										mContext,
										"删除文件失败", Toast.LENGTH_LONG).show();
							}
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
			AlertDialog.Builder builder = new Builder(
					mContext);
			reNameDialog = builder.setView(mEditText)
					.setPositiveButton("确认", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO 自动生成的方法存根
							String newName = mEditText.getText().toString();
							if (null != newName && newName.length() > 0) {
								reNameDialog.dismiss();
								// 获取焦点文件
								String filePath = mEditMusic.getPath();
								// 重命名文件
								boolean result = mFileUtil.reNameFile(
										filePath, newName);
								if (result) {                                
									upDateMediaStore(mFileUtil,"reName", filePath,	newName);
								} else {
									Toast.makeText(
											mContext,
											"重命名失败", Toast.LENGTH_LONG)
											.show();
								}
							}
						}
					}).setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
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
	 * @param doAction
	 * @param oldfile
	 * @param newFile
	 */
	private void upDateMediaStore(FileUtil fileUtil, String doAction,
			String oldfile, String newFile) {

		if (null == oldfile || oldfile.length() <= 0 )return;

		/**
		 * 更改Content Provider的文件
		 */
		ContentResolver mContentResolver = mContext.getContentResolver();
		Uri mAudioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		if (doAction.equals("reName") && null != newFile ) {
			ContentValues values = new ContentValues();
			String newFilePath = fileUtil.getNewFilePath(oldfile, newFile);
			if (!newFilePath.equals("")) {
				values.put(MediaStore.Audio.Media.DATA, newFilePath);
				mContentResolver.update(mAudioUri, values,
						MediaStore.Audio.Media.DATA + " = '" + oldfile + "'",null);
			}
		} else if (doAction.equals("remove")) {
			mContentResolver.delete(mAudioUri, MediaStore.Audio.Media.DATA
					+ " = '" + oldfile + "'", null);
		}
	}
	
	
	
}
