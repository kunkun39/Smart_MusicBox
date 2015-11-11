package com.changhong.xiami.data;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiami.sdk.XiamiSDK;
import com.xiami.sdk.entities.ArtistBook;
import com.xiami.sdk.entities.ArtistRegion;
import com.xiami.sdk.entities.LanguageType;
import com.xiami.sdk.entities.OnlineAlbum;
import com.xiami.sdk.entities.OnlineArtist;
import com.xiami.sdk.entities.OnlineCollect;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.QueryInfo;
import com.xiami.sdk.entities.RankType;
import com.xiami.sdk.entities.SceneSongs;

public class XMMusicData {

	/**
	 * XiamiSDK
	 */
	public static XiamiSDK mXiamiSDK = null;
	public static final String KEY = "825bdc1bf1ff6bc01cd6619403f1a072";
	public static final String SECRET = "7ede04a287d0f92c366880ba515293fd";

	/**
	 * 单例的musicData类
	 */
	private static XMMusicData xMMusicData = null;
	private Gson mGson;

	private XMMusicData(Context con) {
		mXiamiSDK = new XiamiSDK(con, KEY, SECRET);
		mGson = new Gson();
	}

	public static XMMusicData getInstance(Context con) {

		if (null == xMMusicData) {
			xMMusicData = new XMMusicData(con);
		}
		return xMMusicData;
	}

	/**
	 * 自解析JSON接口
	 * 
	 * @param methodCode
	 * @param params
	 * @return
	 */
	public void getJsonData(Handler handler, String method,HashMap<String, Object> params) {

		Log.e("YDINFOR",
				"++++++++++++++++xiamiRequest()+++++++++++++++++++++++++++++");
		RequestDataTask requestDataTask = new RequestDataTask(this, handler, method);
		requestDataTask.execute(params);
	}

	/**
	 * 判断数据是否有效
	 * 
	 * @param response
	 * @return
	 */
	public boolean isResponseValid(XiamiApiResponse response) {
		if (response == null)
			return false;
		int state = response.getState();
		if (state == 0) {
			JsonElement element = response.getData();
			return !(element == null || element.isJsonNull());
		} else {
			return false;
		}
	}

	
	
	/**
	 * 获取音乐风格列表（场景音乐）
	 * @param element
	 * @return
	 */
	public List<SceneInfor> getGenreList(JsonElement element) {

		if (null == element)
			return null;

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
				Log.e("SceneInfor  is  ", i + "," + itemObj.toString());
			}
		}
		return dataList;
	}

	
	/**
	 * 获取场景音乐
	 * @param element
	 * @return
	 */
	public List<SceneSongs> getSceneSongs(JsonElement element) {

		if (null == element)
			return null;

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
				JsonElement songList = itemObj.get("songs");
				// 封装歌曲列表
				List<OnlineSong> songs = getSongList(songList);
				dataList.add(sceneSongs);
			}
		}
		return dataList;
	}

	
	
	/**
	 * 获取精选辑列表
	 * @param element
	 * @return
	 */
	public List<OnlineCollect> getCollectRecommend(JsonElement element) {

		if (null == element)
			return null;

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
				String author_avatar = itemObj.get("author_avatar")
						.getAsString();
				OnlineCollect onlineCollect = new OnlineCollect();
				onlineCollect.setListId(itemObj.get("list_id").getAsInt());
				onlineCollect.setCollectName(itemObj.get("collect_name")
						.getAsString());
				onlineCollect.setCollectLogo(collect_logo);
				onlineCollect.setUserName(itemObj.get("user_name")
						.getAsString());
				onlineCollect.setUserId(itemObj.get("user_id").getAsInt());
				onlineCollect.setAuthorAvatar(author_avatar);
				onlineCollect.setDescription(itemObj.get("description")
						.getAsString());
				onlineCollect
						.setPlayCount(itemObj.get("play_count").getAsInt());
				onlineCollect.setCreateTime(itemObj.get("gmt_create")
						.getAsInt());
				onlineCollect
						.setSongCount(itemObj.get("song_count").getAsInt());
				dataList.add(onlineCollect);
			}
		}
		return dataList;
	}

	/**
	 * 解析专辑列表
	 * 
	 * @param element  JSON数据
	 * @return  专辑列表
	 */
	public List<OnlineAlbum> getAlbumList(JsonElement element) {

		if (null == element)
			return null;

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
				onlineAlbum.setAlbumName(itemObj.get("album_name")
						.getAsString());
				onlineAlbum.setArtistId(itemObj.get("artist_id").getAsInt());
				onlineAlbum.setArtistName(itemObj.get("artist_name")
						.getAsString());
				onlineAlbum.setArtistLogo(itemObj.get("artist_logo")
						.getAsString());
				onlineAlbum.setAlbumCategory(itemObj.get("album_category")
						.getAsString());
				onlineAlbum.setCompany(itemObj.get("company").getAsString());
				onlineAlbum.setLanguage(itemObj.get("language").getAsString());
				onlineAlbum.setDescription(itemObj.get("description")
						.getAsString());
				onlineAlbum.setCdCount(itemObj.get("cd_count").getAsString());
				onlineAlbum.setPublishTime(itemObj.get("gmt_publish")
						.getAsInt());
				onlineAlbum.setSongCount(itemObj.get("song_count").getAsInt());
				onlineAlbum.setGrade(itemObj.get("grade").getAsFloat());
				dataList.add(onlineAlbum);
			}
		}
		return dataList;
	}

	
	
	/**
	 * 解析歌曲集
	 * @param element JSON数据
	 * @return  歌曲集
	 */
	public List<OnlineSong> getSongList(JsonElement element) {

		if (null == element)return null;

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

	

	public List<OnlineArtist> getArtistList(JsonElement element) {

		if (null == element)
			return null;

		List<OnlineArtist> dataList = new ArrayList<OnlineArtist>();

		JsonObject obj = element.getAsJsonObject();
		String total = obj.get("total").getAsString();
		String more = obj.get("more").getAsString();
		element = obj.get("artists");

		if (element.isJsonArray()) {

			JsonArray array = element.getAsJsonArray();
			int size = array.size();
			for (int i = 0; i < size; i++) {
				JsonObject itemObj = array.get(i).getAsJsonObject();

				OnlineArtist onlineArtist = new OnlineArtist();
				onlineArtist.setId(itemObj.get("artist_id").getAsInt());
				onlineArtist.setName(itemObj.get("artist_name").getAsString());
				onlineArtist.setLogo(itemObj.get("artist_logo").getAsString());				
				onlineArtist.setCategory(itemObj.get("category").getAsInt());
				onlineArtist.setEnglish_name(itemObj.get("english_name").getAsString());
				onlineArtist.setGender(itemObj.get("gender").getAsString());
				onlineArtist.setDescription(itemObj.get("description").getAsString());
				onlineArtist.setArea(itemObj.get("area").getAsString());		
				onlineArtist.setCountLikes(itemObj.get("count_likes").getAsInt());
				onlineArtist.setRecommends(itemObj.get("recommends").getAsInt());
				
				dataList.add(onlineArtist);
			}
		}
		return dataList;
	}

	
	
	
	/**
	 * 请求数据接口（自解析Json方式）
	 * 
	 * @param methodCode  方法名称
	 * @param params  应用传入参数
	 * @return  
	 */
	public String xiamiRequest(String methodCode,
			HashMap<java.lang.String, java.lang.Object> params)
			throws java.security.NoSuchAlgorithmException, java.io.IOException,
			com.xiami.core.exceptions.AuthExpiredException,
			com.xiami.core.exceptions.ResponseErrorException {

		Log.e("YDINFOR",	"++++++++++++++++xiamiRequest()+++++++++++++++++++++++++++++");
		// mXiamiSDK.enableLog(true);
		String results = mXiamiSDK.xiamiSDKRequest(methodCode, params);
		return results;
	}

	
	/**
	 * 
	 * @param respond
	 * @return
	 */
	public XiamiApiResponse getXiamiResponse(String respond) {

		Log.e("YDINFOR",
				"++++++++++++++++xiamiRequest()+++++++++++++++++++++++++++++");
		XiamiApiResponse response = mGson.fromJson(respond,
				XiamiApiResponse.class);
		return response;
	}

	/**
	 * 到Url去下载回传Bitmap
	 * 
	 * @param imgUrl
	 * @return
	 */
	public Bitmap getBitmapFromUrl(String imgUrl) {
		URL url;
		Bitmap bitmap = null;
		BufferedInputStream bis = null;
		try {
			url = new URL(imgUrl);
			InputStream is = url.openConnection().getInputStream();
			bis = new BufferedInputStream(is);
			bitmap = BitmapFactory.decodeStream(bis);
			bitmap = compressBmpFromBmp(bitmap);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {

				if (null != bis) {
					bis.close();
					bis = null;
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	private Bitmap compressBmpFromBmp(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 50, baos);
		int length = baos.toByteArray().length;
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;
	}

	public OnlineArtist getArtistDetail(JsonElement jsonData) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
