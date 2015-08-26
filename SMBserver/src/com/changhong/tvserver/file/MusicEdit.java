package com.changhong.tvserver.file;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import com.changhong.tvserver.touying.music.MusicInfor;
import com.changhong.tvserver.touying.music.MusicProvider;
import com.changhong.tvserver.utils.StringUtils;

public class MusicEdit {

	List<MusicInfor> musicList=null;
	FileUtil mFileUtil ;
	FileEditManager mFileEditManager;
	
	public MusicEdit(){
		   mFileUtil = new FileUtil() ;
		   mFileEditManager=FileEditManager.getInstance();
	}
	

	public void doFileEdit(Context context, String clientIp, String editType,String fileUrl) {
		
		String doResult = "";
		if (!matchEditTypeAndMsg(editType,fileUrl))return;

		if (editType.equals("reName")) {
			String[] tokens=fileUrl.split(";");
			doResult = mFileUtil.reNameFile(tokens[0], tokens[1]);
		} else if (editType.equals("cancle")) {
			doResult = mFileUtil.removeFileFromSDCard(fileUrl);
		} else if (editType.equals("requestMusicList")) {
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
					JSONObject musicObj = new JSONObject();
					musicObj.put("id", id);
					musicObj.put("path", tempPath);
					musicObj.put("title", title);
					musicObj.put("artist", artist);
					musicObj.put("duration", duration);
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
	private boolean matchEditTypeAndMsg(String editType,String fileUrl){
		
		boolean reValue=false;		
	    if (editType.equals("cancle") && StringUtils.hasLength(fileUrl)) {
			     reValue=true;
		}else if (editType.equals("reName") && StringUtils.hasLength(fileUrl)) {
			String[] tokens=fileUrl.split(";");
			if(null != tokens  && tokens.length >1){
				reValue=true;
			}
		} else if(editType.equals("requestMusicList")){
			reValue=true;

		}	
		return reValue;
	}

}
