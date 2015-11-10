package com.changhong.xiami.data;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.xiami.sdk.XiamiSDK;
import com.xiami.sdk.entities.OnlineAlbum;
import com.xiami.sdk.entities.OnlineCollect;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.SceneSongs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JsonUtil {

    private static JsonUtil instance;
    private Gson mGson;
    /**
	 * XiamiSDK
	 */
	public static XiamiSDK mXiamiSDK = null;
	public static final String KEY = "825bdc1bf1ff6bc01cd6619403f1a072";
	public static final String SECRET = "7ede04a287d0f92c366880ba515293fd";
    
    
    private JsonUtil(Context con) {
		mXiamiSDK = new XiamiSDK(con, KEY, SECRET);
        mGson = new Gson();
    }


    public static JsonUtil getInstance(Context con) {
        if (instance == null) {
            instance = new JsonUtil(con);
        }
        return instance;
    }

    public boolean isResponseValid(XiamiApiResponse response) {
        if (response == null) return false;
        int state = response.getState();
        if (state == 0) {
            JsonElement element = response.getData();
            return !(element == null || element.isJsonNull());
        } else {
            return false;
        }
    }
    
    
    
    public List<SceneInfor> getGenreList	(JsonElement element){
    	
        if(null == element)return null;

		    List<SceneInfor> dataList = new ArrayList<SceneInfor>();
		
			JsonObject obj = element.getAsJsonObject();
			element = obj.get("genre_list");
			if (element.isJsonArray()) {

				JsonArray array = element.getAsJsonArray();
				int size = array.size();
				for (int i = 0; i < size; i++) {
					JsonObject itemObj = array.get(i).getAsJsonObject();
	
					SceneInfor sceneInfor = new SceneInfor();
					sceneInfor.setSceneName(itemObj.get("title").getAsString());
					sceneInfor.setSceneLogo(itemObj.get("logo").getAsString());
					sceneInfor.setSceneID(itemObj.get("radio_id").getAsInt());	
					sceneInfor.setMusicType(itemObj.get("type").getAsInt());			

					dataList.add(sceneInfor);					
					Log.e("SceneInfor  is  ",i+","+ itemObj.toString());
				}
		}
		return dataList;
   }

   public List<SceneSongs> getSceneSongs(JsonElement element){
    	
        if(null == element)return null;

		    List<SceneSongs> dataList = new ArrayList<SceneSongs>();
		
			JsonObject obj = element.getAsJsonObject();
			element = obj.get("list");
			if (element.isJsonArray()) {

				JsonArray array = element.getAsJsonArray();
				int size = array.size();
				for (int i = 0; i < size; i++) {
					JsonObject itemObj = array.get(i).getAsJsonObject();
					SceneSongs sceneSongs = new SceneSongs();
					sceneSongs.setTitle(itemObj.get("title").getAsString());
					sceneSongs.setIcon(itemObj.get("icon").getAsString());
					JsonElement songList= itemObj.get("songs");					
					//封装歌曲列表
					List<OnlineSong> songs= getSongList(songList);					
					dataList.add(sceneSongs);
				}
		}
		return dataList;
   }
 
    public List<OnlineCollect> getCollectRecommend	(JsonElement element){
    	
         if(null == element)return null;

		List<OnlineCollect> dataList = new ArrayList<OnlineCollect>();
		
			JsonObject obj = element.getAsJsonObject();
			String total = obj.get("total").getAsString();
			String more = obj.get("more").getAsString();
			element = obj.get("collects");

			if (element.isJsonArray()) {

				JsonArray array = element.getAsJsonArray();
				int size = array.size();
				for (int i = 0; i < size; i++) {
					JsonObject itemObj = array.get(i).getAsJsonObject();
					String collect_logo = itemObj.get("collect_logo").getAsString();
					String author_avatar = itemObj.get("author_avatar")	.getAsString();
					OnlineCollect onlineCollect = new OnlineCollect();
					onlineCollect.setListId(itemObj.get("list_id").getAsInt());
					onlineCollect.setCollectName(itemObj.get("collect_name").getAsString());
					onlineCollect.setCollectLogo(collect_logo);
					onlineCollect.setUserName(itemObj.get("user_name").getAsString());
					onlineCollect.setUserId(itemObj.get("user_id").getAsInt());
					onlineCollect.setAuthorAvatar(author_avatar);
					onlineCollect.setDescription(itemObj.get("description").getAsString());
					onlineCollect.setPlayCount(itemObj.get("play_count").getAsInt());				
					onlineCollect.setCreateTime(itemObj.get("gmt_create").getAsInt());
					onlineCollect.setSongCount(itemObj.get("song_count").getAsInt());
					dataList.add(onlineCollect);
				}
		}
		return dataList;
    }

    
   /**
    * 解析专辑列表
    * @param element
    * @return
    */
    public List<OnlineAlbum> getAlbumList	(JsonElement element){
    	
        if(null == element)return null;

		List<OnlineAlbum> dataList = new ArrayList<OnlineAlbum>();
		
			JsonObject obj = element.getAsJsonObject();
			String total = obj.get("total").getAsString();
			String more = obj.get("more").getAsString();
			element = obj.get("albums");

			if (element.isJsonArray()) {

				JsonArray array = element.getAsJsonArray();
				int size = array.size();
				for (int i = 0; i < size; i++) {
					JsonObject itemObj = array.get(i).getAsJsonObject();
			
					OnlineAlbum onlineAlbum = new OnlineAlbum();
					onlineAlbum.setAlbumId(itemObj.get("album_id").getAsInt());
					onlineAlbum.setAlbumName(itemObj.get("album_name").getAsString());
					onlineAlbum.setArtistId(itemObj.get("artist_id").getAsInt());
					onlineAlbum.setArtistName(itemObj.get("artist_name").getAsString());
					onlineAlbum.setArtistLogo(itemObj.get("artist_logo").getAsString());	
					onlineAlbum.setAlbumCategory(itemObj.get("album_category").getAsString());	
					onlineAlbum.setCompany(itemObj.get("company").getAsString());	
					onlineAlbum.setLanguage(itemObj.get("language").getAsString());	
					onlineAlbum.setDescription(itemObj.get("description").getAsString());	
					onlineAlbum.setCdCount(itemObj.get("cd_count").getAsString());				
					onlineAlbum.setPublishTime(itemObj.get("gmt_publish").getAsInt());
					onlineAlbum.setSongCount(itemObj.get("song_count").getAsInt());
					onlineAlbum.setGrade(itemObj.get("grade").getAsFloat());
					dataList.add(onlineAlbum);
				}
		}
		return dataList;
   }
    
    
    public List<OnlineSong> getSongList(JsonElement element){
    	
        if(null == element)return null;

		List<OnlineSong> dataList = new ArrayList<OnlineSong>();
		JsonArray arraySong = element.getAsJsonArray();
		int size = arraySong.size();
	
		for (int i = 0; i < size; i++) {
			
			JsonObject songObj = arraySong.get(i).getAsJsonObject();
			OnlineSong song = new OnlineSong();
			song.setSongId(songObj.get("song_id").getAsLong());
			song.setArtistId(songObj.get("artist_id").getAsLong());
			song.setSongId(songObj.get("song_id").getAsLong());
			song.setAlbumId(songObj.get("album_id").getAsLong());
			song.setLength(songObj.get("length").getAsInt());
			song.setMusicType(songObj.get("music_type").getAsInt());
			song.setCdSerial(songObj.get("cd_serial").getAsInt());
			song.setSongName(songObj.get("song_name").getAsString());
			song.setArtistName(songObj.get("artist_name").getAsString());
			song.setArtistLogo(songObj.get("artist_logo").getAsString());
			song.setAlbumName(songObj.get("album_name").getAsString());
			song.setLogo(songObj.get("album_logo").getAsString());
			song.setSingers(songObj.get("singers").getAsString());
			dataList.add(song);
		}
		return dataList;
   }
    
    
    
    


	/**
	 * 自解析JSON接口
	 * @param methodCode
	 * @param params
	 * @return
	 */
	public String xiamiRequest(String methodCode,  HashMap<java.lang.String,java.lang.Object> params) throws java.security.NoSuchAlgorithmException,
    java.io.IOException,
    com.xiami.core.exceptions.AuthExpiredException,
    com.xiami.core.exceptions.ResponseErrorException{
		
		Log.e("YDINFOR", "++++++++++++++++xiamiRequest()+++++++++++++++++++++++++++++");
//		mXiamiSDK.enableLog(true);
		String  results=mXiamiSDK.xiamiSDKRequest(methodCode,params);
		return results;
	}
    

	
	
	public XiamiApiResponse  xiamiRespond(String respond) {
		
		Log.e("YDINFOR", "++++++++++++++++xiamiRequest()+++++++++++++++++++++++++++++");
        XiamiApiResponse response = mGson.fromJson(respond, XiamiApiResponse.class);
		return response;
	}

}
