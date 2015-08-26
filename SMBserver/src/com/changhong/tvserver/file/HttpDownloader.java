package com.changhong.tvserver.file;

import android.net.Uri;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpDownloader {

	private static final String TAG = "FileHttpDownloader";

	public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000;

	public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000;

	private static final int MUTI_THREAD_SIZE_POINT = 1024 * 1024 * 6; // 6M

	public static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

	/**
	 * 根据URL创建http链接。
	 * @param url 链接URL
	 * @return Http链接
	 * @throws IOException
	 */
	private static HttpURLConnection createConnection(String url)
			throws IOException {
		String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
		HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
		conn.setConnectTimeout(DEFAULT_HTTP_CONNECT_TIMEOUT);
		conn.setReadTimeout(DEFAULT_HTTP_READ_TIMEOUT);
		return conn;
	}

   /**
    * 文件下载
    * @param fileUri 下载URL
    * @param fileType 文件类型：音乐、视频、文本
    * @param fileName 文件名称
    * @return  downLoadOK=下载成功； downloadError=下载失败； fileExist=文件存在。
    */
	public static String download(String fileUri, String fileType,	String fileName) {

		String result = "downLoadOK";
		InputStream inputStream = null;
		HttpURLConnection conn = null;
		try {

			conn = createConnection(fileUri);
			int contentLength = conn.getContentLength();

			// 下载文件最大值限定=6M
			if (contentLength <= MUTI_THREAD_SIZE_POINT) {
				inputStream = conn.getInputStream();
				FileUtil fileUtils = new FileUtil();

				// 判断文件是否存在
				if (fileUtils.isFileExist(fileType, fileName)) {
					result = "fileExist";
				} else {
					File fileResult = fileUtils.writeToSDCard(fileType,fileName, inputStream);
					// 如果fileResult=null,下载失败。
					if (null == fileResult) {
						result = "downloadError";
					}
				}
			}
		} catch (IOException e) {
			result = "downloadError";
			e.printStackTrace();
		} finally {
			try {

				if (inputStream != null) {
					inputStream.close();
				}
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
