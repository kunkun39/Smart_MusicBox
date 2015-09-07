package com.changhong.tvserver.fedit;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.changhong.nanohttpd.NanoHTTPDService;
import com.changhong.tvserver.touying.music.MusicInfor;
import com.changhong.tvserver.touying.music.MusicProvider;
import com.changhong.tvserver.utils.NetworkUtils;
import com.changhong.tvserver.utils.StringUtils;

public class MusicEdit {

	List<MusicInfor> musicList=null;
	FileUtil mFileUtil ;
	FileEditManager mFileEditManager;

	
	
	public MusicEdit(){
		   mFileUtil = new FileUtil() ;
		   mFileEditManager=FileEditManager.getInstance();
	}
	

	public void doFileEdit(Context context, String clientIp, String editType,String fileUrl,String param) {
		
		String doResult = "";
		if (!matchEditTypeAndMsg(editType,fileUrl,param))return;

		if (editType.equals(Configure.EDIT_RENAME)) {
			doResult = mFileUtil.reNameFile(fileUrl, param);
		} else if (editType.equals(Configure.EDIT_REMOVE)) {
			doResult = mFileUtil.removeFileFromSDCard(fileUrl);
		} else if (editType.equals(Configure.EDIT_REQUEST_MUSICS)) {
			// 获取媒体库文件
			MusicProvider provider = new MusicProvider(context);
			musicList = provider.getList();
		}

		//将信息封装成Json格式
		String jsonStr = formateDataToJson(editType, fileUrl, doResult);
		if (StringUtils.hasLength(jsonStr)) {
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Configure.MSG_SEND, jsonStr);
			params.put(Configure.IP_ADD, clientIp);
			params.put(Configure.EDIT_TYPE, editType);
			mFileEditManager.communicationWithClient(null,	Configure.ACTION_SOCKET_COMMUNICATION, params);
			
            //文件修改成功，更新媒体库文件
			if(doResult.equals(Configure.ACTION_SUCCESS)){
				      String newFile=mFileUtil.getNewFilePath(fileUrl, param);
			         upDateMediaStore(context,editType,fileUrl,newFile);
            }
		}
	}

	/**
	 * 封装发送数据为json 格式
	 * 
	 * @param context
	 * @param editType
	 * @param content
	 * @return
	 */
	private String formateDataToJson(String editType, String fileUrl,
			String doResult) {

		String jsonStr = "";
		try {
			JSONObject formateObj = new JSONObject();
			JSONArray array = new JSONArray();

			// 请求音乐列表.
			if (editType.equals("requestMusicList")) {

				if (null == musicList)return jsonStr;

				for (MusicInfor music : musicList) {
					String tempPath = music.getPath();
					String title = music.getTitle();
					String artist = music.getArtist();
					int duration = music.getDuration();
					int id = music.getId();
					String httpurl=convertFilePathToHttpURL(tempPath);
					JSONObject musicObj = new JSONObject();
					musicObj.put("id", id);
					musicObj.put("path", tempPath);
					musicObj.put("title", title);
					musicObj.put("artist", artist);
					musicObj.put("duration", duration);
					musicObj.put("httpUrl", httpurl);
					array.put(musicObj);
				}
			} else {

				JSONObject resultObj = new JSONObject();
				resultObj.put("path", fileUrl);
				resultObj.put("doResult", doResult);
				array.put(resultObj);
			}
			// 编辑类型
			formateObj.put("msgAction", editType);
			formateObj.put("msgRespond", array);
			jsonStr = formateObj.toString();

		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return jsonStr;
	}
	
	
	/**
	 * 通过类型f检查发送信息是否匹配。
	 * @param editType 文件编辑类型
	 * @param fileUrl 文件路径
	 * @return
	 */
	private boolean matchEditTypeAndMsg(String editType,String fileUrl, String param){
		
		boolean reValue=false;		
	    if (editType.equals(Configure.EDIT_REMOVE) && StringUtils.hasLength(fileUrl)) {
			     reValue=true;
		}else if (editType.equals(Configure.EDIT_RENAME) && StringUtils.hasLength(fileUrl)) {
			if(StringUtils.hasLength(param)){
				reValue=true;
			}
		} else if(editType.equals(Configure.EDIT_REQUEST_MUSICS) ){
			reValue=true;

		}	
		return reValue;
	}
	
	
	
	private  String convertFilePathToHttpURL(String filePath){
		
		// 获取IP和外部存储路径
		String ipAddress = NetworkUtils.getLocalIpAddress();
		String httpAddress = "http://" + ipAddress + ":" + NanoHTTPDService.HTTP_PORT;

		String newMusicPath = null;
		if (filePath.startsWith(NanoHTTPDService.defaultHttpServerPath)) {
			newMusicPath = convertFileUrlToHttpURL(filePath.replace(NanoHTTPDService.defaultHttpServerPath, ""));

		} else {
			for (String otherHttpServerPath : NanoHTTPDService.otherHttpServerPaths) {
				if (filePath.startsWith(otherHttpServerPath)) {
					newMusicPath = convertFileUrlToHttpURL(filePath.replace(otherHttpServerPath, ""));
				}
			}
		}

		String tmpHttpAddress = httpAddress + newMusicPath;
		// 判断URL是否符合规范，如果不符合规范，就1重命名文件
		try {
			URI.create(tmpHttpAddress);
		} catch (Exception e) {
			tmpHttpAddress=filePath;
		}		
		return tmpHttpAddress;
	}
	
	
	/**
	 * 特殊字符转换
	 * 
	 * @param url
	 *            url字符串
	 * @return
	 */
	public String convertFileUrlToHttpURL(String url) {
		if (null != url && url.length() > 0) {
			return url.replace("%", "%25").replace(" ", "%20")
					.replace("+", "%2B").replace("#", "%23")
					.replace("&", "%26").replace("=", "%3D")
					.replace("?", "%3F").replace("^", "%5E");
		}
		return url;
	}
	
	private void upDateMediaStore(Context context,String doAction,String fileUrl, String newFile) {
		
		/**
		 * remove、rename 成功，则，更新MediaStore的文件
		 */
		if (!StringUtils.hasLength(fileUrl) || !doAction.equals(Configure.EDIT_REMOVE)
				|| !doAction.equals(Configure.EDIT_RENAME))return;
		
		Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		scanIntent.setData(Uri.fromFile(new File(fileUrl)));
		context.sendBroadcast(scanIntent);
		
		if(doAction.equals(Configure.EDIT_RENAME) && StringUtils.hasLength(newFile)){
		    scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			scanIntent.setData(Uri.fromFile(new File(newFile)));
			context.sendBroadcast(scanIntent);
		}		
	}

}
