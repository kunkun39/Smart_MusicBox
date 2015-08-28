package com.changhong.tvserver.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class FileUtil {

	private String SDCARDPATH = "";
	/**
	 * 文本文件类型
	 */
	public static final String FILE_PATH = "file";
	/**
	 * 音乐文件目录名
	 */
	public static final String MUSIC_PATH = "music";
	/**
	 * 视频文件目录名
	 */
	public static final String VEDIO_PATH = "video";

	// 文件读取Buffer size
	private static final int BUFFER_SIZE = 4 * 1024; // 4K

	public final static int MAX_FILE_ITEM_SIZE = 10;

	public final static int DELETE_ITEM_SIZE = 2;

	/**
	 * 获取sdcard根目录
	 * 
	 * @return SDcard根目录路径
	 */
	public String getSDCARDPATH() {
		return SDCARDPATH;
	}

	public FileUtil() {
		// 得到手机存储器目录---因为各个厂商的手机SDcard可能不一样。
		SDCARDPATH = Environment.getExternalStorageDirectory() + File.separator;
	}

	/**
	 * 在SDcard上创建文件
	 * 
	 * @param fileName
	 * @return File
	 */
	public File creatSDFile(String fileName) {
		File file = new File(SDCARDPATH + fileName);
		return file;
	}

	/**
	 * 在SDcard上创建目录
	 * 
	 * @param dirName
	 */
	public void createSDDir(String dirName) {
		File file = new File(SDCARDPATH + dirName);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param fileName
	 * @return boolean
	 */
	public boolean isFileExist(String fileType, String fileName) {

		String path = MUSIC_PATH;
		// 根据文件类型修改保存路径
		if (fileType.contains(FILE_PATH))
			path = FILE_PATH;
		else if (fileType.contains(VEDIO_PATH))
			path = VEDIO_PATH;

		fileName = path + File.separator + fileName;

		File file = new File(SDCARDPATH + fileName);
		return file.exists();
	}

	/**
	 * @param path
	 *            存放目录
	 * @param fileName
	 *            文件名字
	 * @param input
	 *            数据来源
	 * @return
	 */
	public File writeToSDCard(String type, String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		// 默认状态下，路径为音乐文件
		String path = MUSIC_PATH;
		try {

			// 根据文件类型修改保存路径
			if (type.contains(FILE_PATH))
				path = FILE_PATH;
			else if (type.contains(VEDIO_PATH))
				path = VEDIO_PATH;

			// 创建文件夹
			createSDDir(path);

			// 添加文件分隔符
			path = path + File.separator;
			// 创建文件
			file = creatSDFile(path + fileName);
			output = new FileOutputStream(file);
			// 以4K为单位，每次写4k
			byte buffer[] = new byte[BUFFER_SIZE];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			// 清除缓存
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭流
				if (null != output) {
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 从SDcard中删除指定的文件
	 * 
	 * @param filePath
	 *            文件路径
	 */
	public String removeFileFromSDCard(String filePath) {
		boolean reValue = false;
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
			reValue = true;
		}
		return reValue?Configure.ACTION_SUCCESS:Configure.ACTION_FAILED;
	}

	/**
	 * 重命名文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @param newName
	 *            新的文件名
	 * @return true=重命名成功； false=重命名失败
	 */
	public String reNameFile(String filePath, String newName) {
		boolean reValue = false;
		File file = new File(filePath);
		if (file.exists()) {
			String newFilePath = getNewFilePath(filePath, newName);
			File newFile = new File(newFilePath);
			reValue = file.renameTo(newFile);
		}
		return reValue?Configure.ACTION_SUCCESS:Configure.ACTION_FAILED;
	}

	/**
	 * 根据新的文件名，重新获取文件路径
	 * 
	 * @param filePath
	 *            文件路径
	 * @return
	 */
	public String getNewFilePath(String filePath, String newName) {
		String newPath = "";
		
		//参数有效性检查
		if(null == filePath || filePath.length() <=0 )return newPath;
		if(null == newName || newName.length() <=0 )return newPath;

		//获取文件文件名，并替换。
		int startIndex = filePath.lastIndexOf(File.separator);
		int endIndex = filePath.indexOf(".");
		if (startIndex > 0 && endIndex > (startIndex + 1)) {
			String oldName = filePath.substring(startIndex + 1, endIndex);
			if (null != oldName && oldName.length() > 0) {
				newPath = filePath.replace(oldName, newName);
			}
		}
		return newPath;
	}

	/**
	 * 检查文件是否超过设定文件个数，超过，则删除2个文件
	 * 
	 * @param fileType
	 *            文件类型
	 */
	public void checkMaxFileItemExceedAndProcess(String fileType) {

		String path = MUSIC_PATH;
		// 根据文件类型修改保存路径
		if (fileType.contains(FILE_PATH))
			path = FILE_PATH;
		else if (fileType.contains(VEDIO_PATH))
			path = VEDIO_PATH;

		File fileList = new File(getSDCARDPATH() + File.separator + path);
		String[] list = fileList.list();
		if (list != null && list.length > MAX_FILE_ITEM_SIZE) {
			Log.e("FILE_DELETE", "now small picture number is  " + list.length);

			int alreadyDeleteNumber = 0;
			File[] files = fileList.listFiles();
			Arrays.sort(files, new FileComparator());

			for (File file : files) {
				try {
					if (!file.isDirectory()) {
						file.delete();
						alreadyDeleteNumber++;
						if (alreadyDeleteNumber >= DELETE_ITEM_SIZE) {
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}

	

	// filename是我们的文件全名，包括后缀哦
	private void updateGallery(Context context, String filename) {
		MediaScannerConnection.scanFile(context, new String[] { filename },
				null, new MediaScannerConnection.OnScanCompletedListener() {
					@Override
					public void onScanCompleted(String path, Uri uri) {
						// TODO 自动生成的方法存根
						Log.i("ExternalStorage", "Scanned " + path + ":");
						Log.i("ExternalStorage", "-> uri=" + uri);
					}
				});
	}
	
	static class FileComparator implements Comparator<File> {
		@Override
		public int compare(File f1, File f2) {
			Long last1 = f1.lastModified();
			Long last2 = f2.lastModified();
			return last1.compareTo(last2);
		}
	}

}
