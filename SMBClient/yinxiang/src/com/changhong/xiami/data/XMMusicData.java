package com.changhong.xiami.data;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.yinxiang.nanohttpd.HTTPDService;
import com.changhong.yinxiang.utils.Configure;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.xiami.music.api.utils.RequestMethods;
import com.xiami.sdk.XiamiSDK;
import com.xiami.sdk.entities.ArtistBook;
import com.xiami.sdk.entities.ArtistRegion;
import com.xiami.sdk.entities.LanguageType;
import com.xiami.sdk.entities.OnlineAlbum;
import com.xiami.sdk.entities.OnlineArtist;
import com.xiami.sdk.entities.OnlineCollect;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.QueryInfo;
import com.xiami.sdk.entities.RankListItem;
import com.xiami.sdk.entities.RankType;
import com.xiami.sdk.entities.SceneSongs;
import com.xiami.sdk.entities.OnlineSong.Quality;
import com.xiami.sdk.utils.Encryptor;
import com.xiami.sdk.utils.ImageUtil;

public class XMMusicData {

	/**
	 * XiamiSDK
	 */
	public static XiamiSDK mXiamiSDK = null;
	public static final String KEY = "825bdc1bf1ff6bc01cd6619403f1a072";
	public static final String SECRET = "7ede04a287d0f92c366880ba515293fd";

	/*
	 * music
	 */
	/**
	 * 单例的musicData�?
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
	 *            数组
	 * @return
	 */
	public void getJsonData(Handler handler, String method,
			HashMap<String, Object>[] params) {

		Log.e("YDINFOR",
				"++++++++++++++++xiamiRequest()+++++++++++++++++++++++++++++");
		RequestDataTask requestDataTask = new RequestDataTask(this, handler,
				method);
		requestDataTask.execute(params);
	}

	/**
	 * 
	 * @param handler
	 * @param method
	 * @param params
	 */
	public void getJsonData(Handler handler, String method,
			HashMap<String, Object> params) {

		Log.e("YDINFOR",
				"++++++++++++++++xiamiRequest()+++++++++++++++++++++++++++++");
		RequestDataTask requestDataTask = new RequestDataTask(this, handler,
				method);
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
	 * 
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
	 * 
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
	 * 获取场景分类
	 * 
	 * @param element
	 * @return
	 */
	public List<SceneInfor> getSceneList(JsonElement element) {

		if (null == element)
			return null;

		List<SceneInfor> dataList = new ArrayList<SceneInfor>();

		JsonObject obj = element.getAsJsonObject();
		element = obj.get("list");

		if (element.isJsonArray()) {

			JsonArray array = element.getAsJsonArray();
			int size = array.size();
			for (int i = 0; i < size; i++) {
				JsonObject itemObj = array.get(i).getAsJsonObject();

				SceneInfor scene = new SceneInfor();
				scene.setSceneID(getJsonObjectValueInt(itemObj, "radio_id"));
				scene.setSceneName(getJsonObjectValue(itemObj, "title"));
				scene.setSceneLogo(getJsonObjectValue(itemObj, "logo"));
				scene.setMusicType(getJsonObjectValueInt(itemObj, "radio_type"));
				String tag = getJsonObjectValue(itemObj, "tag");
				// 封装歌曲列表
				dataList.add(scene);
			}
		}
		return dataList;
	}

	/*
	 * 
	 * 获取今日推荐歌曲列表
	 */
	public void getTodayRecom(Handler handler, int limit) {

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("limit", limit);
		RequestDataTask requestDataTask = new RequestDataTask(this, handler,
				RequestMethods.METHOD_RECOMMEND_DAILYLIST);
		requestDataTask.execute(params);

	}

	/*
	 * 获取新碟首发专辑列表
	 */
	public void getPromotionALbums(Handler handler, int page, int limit) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("type", RankType.music_all);
		params.put("page", page);
		params.put("limit", limit);
		RequestDataTask requestDataTask = new RequestDataTask(this, handler,
				Configure.RequestMethods_PROMOTION_ALBUMS);
		requestDataTask.execute(params);
	}

	/*
	 * 获取华语排行榜歌�?
	 */
	public void getHuayuRank(Handler handler) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("type", RankType.hito);
		params.put("time", 0);
		RequestDataTask requestDataTask = new RequestDataTask(this, handler,
				RequestMethods.METHOD_RANK_DETAIL);
		requestDataTask.execute(params);
	}

	/*
	 * 获取全部排行榜歌�?
	 */
	public void getALLRank(Handler handler) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("type", RankType.music_all);
		params.put("time", 0);
		RequestDataTask requestDataTask = new RequestDataTask(this, handler,
				RequestMethods.METHOD_RANK_DETAIL);
		requestDataTask.execute(params);
	}

	/*
	 * 获取排行�?榜单列表
	 */
	public void getRankType(Handler handler) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("", "");
		RequestDataTask requestDataTask = new RequestDataTask(this, handler,
				RequestMethods.METHOD_RANK_LIST);
		requestDataTask.execute(params);
	}

	/*
	 * 获取指定类型榜单的歌曲列�?
	 */
	public void getTheRank(Handler handler, String type) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		params.put("time", 0);
		RequestDataTask requestDataTask = new RequestDataTask(this, handler,
				RequestMethods.METHOD_RANK_DETAIL);
		requestDataTask.execute(params);
	}

	/*
	 * 获取指定专辑ID的歌曲列表
	 */
	public void getTheAlbum(Handler handler, long id) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("album_id", id);
		params.put("full_des", false);
		RequestDataTask requestDataTask = new RequestDataTask(this, handler,
				RequestMethods.METHOD_ALBUMS_DETAIL);
		requestDataTask.execute(params);
	}

	/**
	 * 获取指定精选集ID的详细信息
	 */
	public void getCollectDetail(Handler handler, long id) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("list_id", id);
		params.put("full_des", false);
		RequestDataTask requestDataTask = new RequestDataTask(this, handler,
				RequestMethods.METHOD_COLLECT_DETAIL);
		requestDataTask.execute(params);
	}
	
	
	
	
	
	
	/**
	 * 获取指定场景音乐
	 */
	public void getSceneDetail(Handler handler, long id) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("limit", 100);
		RequestDataTask requestDataTask = new RequestDataTask(this, handler,
				RequestMethods.METHOD_RADIO_DETAIL);
		requestDataTask.execute(params);
	}

	/*
	 * =======================数据解析部分==============================================
	 */

	/**
	 * 获取精选辑列表
	 * 
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
	 * @param element
	 *            JSON数据
	 * @return 专辑列表
	 */
	public List<OnlineAlbum> getAlbumList(JsonElement element) {

		if (null == element)
			return null;

		List<OnlineAlbum> dataList = new ArrayList<OnlineAlbum>();

		JsonObject obj = element.getAsJsonObject();
		String total = obj.get("total").getAsString();
		String more = obj.get("more").getAsString();
		element = obj.get("albums");

		dataList = albumsElementToList(element);
		return dataList;
	}

	/*
	 * 
	 * 
	 */
	public List<OnlineAlbum> albumsElementToList(JsonElement element) {

		List<OnlineAlbum> dataList = new ArrayList<OnlineAlbum>();
		if (element.isJsonArray()) {

			JsonArray array = element.getAsJsonArray();
			int size = array.size();
			for (int i = 0; i < size; i++) {
				JsonObject itemObj = array.get(i).getAsJsonObject();
				OnlineAlbum onlineAlbum = resolveAlbum(itemObj);
				dataList.add(onlineAlbum);
			}
		}

		return dataList;
	}

	public OnlineAlbum resolveAlbum(JsonObject obj) {
		String albumImage = null;
		OnlineAlbum album = new OnlineAlbum();
		album.setAlbumId(obj.get("album_id").getAsInt());
		album.setAlbumName(obj.get("album_name").getAsString());
		album.setArtistId(obj.get("artist_id").getAsInt());
		album.setArtistName(obj.get("artist_name").getAsString());

		// 当有专辑图片时，显示专辑图片，反之则显示艺术家图�?
		albumImage = obj.get("album_logo").getAsString();
		if (!StringUtils.hasLength(albumImage)) {
			albumImage = obj.get("artist_logo").getAsString();
		}
		album.setArtistLogo(albumImage);

		album.setAlbumCategory(obj.get("album_category").getAsString());
		album.setCompany(obj.get("company").getAsString());
		album.setLanguage(obj.get("language").getAsString());
		album.setDescription(obj.get("description").getAsString());
		album.setCdCount(obj.get("cd_count").getAsString());
		album.setPublishTime(obj.get("gmt_publish").getAsInt());
		album.setSongCount(obj.get("song_count").getAsInt());
		album.setGrade(obj.get("grade").getAsFloat());

		return album;
	}

	/**
	 * 解析歌曲�?
	 * 
	 * @param element
	 *            JSON数据
	 * @return 歌曲�?
	 */
	public List<OnlineSong> getSongList(JsonElement element) {

		if (null == element)
			return null;

		List<OnlineSong> dataList = new ArrayList<OnlineSong>();
		JsonArray arraySong = element.getAsJsonArray();
		int size = arraySong.size();

		for (int i = 0; i < size; i++) {
			JsonObject songObj = arraySong.get(i).getAsJsonObject();
			OnlineSong song = new OnlineSong();
			song.setSongId(getJsonObjectValueLong(songObj, "song_id"));
			song.setArtistId(getJsonObjectValueLong(songObj, "artist_id"));
			song.setAlbumId(getJsonObjectValueLong(songObj, "album_id"));
			song.setAlbumName(getJsonObjectValue(songObj, "album_name"));
			song.setLogo(getJsonObjectValue(songObj, "album_logo"));
			song.setLength(getJsonObjectValueInt(songObj, "length"));
			song.setMusicType(getJsonObjectValueInt(songObj, "music_type"));
			song.setCdSerial(getJsonObjectValueInt(songObj, "cd_serial"));
			song.setSongName(getJsonObjectValue(songObj, "song_name"));
			song.setArtistName(getJsonObjectValue(songObj, "artist_name"));
			song.setArtistLogo(getJsonObjectValue(songObj, "artist_logo"));
			song.setSingers(getJsonObjectValue(songObj, "singers"));
			dataList.add(song);
		}
		return dataList;
	}

	public List<OnlineArtist> getArtistList(JsonElement element) {

		if (null == element)
			return null;

		List<OnlineArtist> dataList = new ArrayList<OnlineArtist>();

		JsonObject obj = element.getAsJsonObject();
		String total = getJsonObjectValue(obj, "total");
		String more = getJsonObjectValue(obj, "more");
		JsonArray array = getJsonObjectArray(obj, "artists");
		if (null != array) {
			int size = array.size();
			for (int i = 0; i < size; i++) {
				JsonObject itemObj = getJsonObject(array.get(i));
				if (null == itemObj)
					continue;

				OnlineArtist onlineArtist = new OnlineArtist();
				onlineArtist.setId(getJsonObjectValueInt(itemObj, "artist_id"));
				onlineArtist
						.setName(getJsonObjectValue(itemObj, "artist_name"));
				onlineArtist
						.setLogo(getJsonObjectValue(itemObj, "artist_logo"));
				onlineArtist.setCategory(getJsonObjectValueInt(itemObj,
						"artist_id"));
				onlineArtist.setEnglish_name(getJsonObjectValue(itemObj,
						"english_name"));
				onlineArtist.setGender(getJsonObjectValue(itemObj, "gender"));
				onlineArtist.setDescription(getJsonObjectValue(itemObj,
						"description"));
				onlineArtist.setArea(getJsonObjectValue(itemObj, "area"));
				onlineArtist.setCountLikes(getJsonObjectValueInt(itemObj,
						"count_likes"));
				onlineArtist.setRecommends(getJsonObjectValueInt(itemObj,
						"recommends"));

				dataList.add(onlineArtist);
			}
		}
		return dataList;
	}

	/*
	 * 获取排行榜歌曲列�?
	 */
	public List<OnlineSong> getRankSongList(JsonElement element) {
		if (null == element)
			return null;

		List<OnlineSong> dataList = new ArrayList<OnlineSong>();
		JsonObject jsonObj = element.getAsJsonObject();
		element = jsonObj.get("songs");

		dataList = getSongList(element);
		return dataList;
	}

	/*
	 * 
	 * 获取榜单列表数据
	 */
	public List<RankListItem> getRankListItem(JsonElement element) {
		List<RankListItem> dataList = new ArrayList<RankListItem>();
		JsonArray arrayItems = element.getAsJsonArray();

		int size = arrayItems.size();
		for (int i = 0; i < size; i++) {
			JsonObject itemObj = arrayItems.get(i).getAsJsonObject();
			JsonArray itemContent = itemObj.getAsJsonArray("items");
			for (int j = 0; j < itemContent.size(); j++) {
				JsonObject rankContent = itemContent.get(j).getAsJsonObject();
				RankListItem rankListItem = new RankListItem();
				rankListItem.setCycleType(rankContent.get("cycle_type")
						.getAsString());
				rankListItem.setLogo(rankContent.get("logo").getAsString());
				rankListItem.setLogoMiddle(rankContent.get("logo_middle")
						.getAsString());
				rankListItem.setSongs(getSongList(rankContent.get("songs")));
				rankListItem.setTitle(rankContent.get("title").getAsString());
				rankListItem.setType(rankContent.get("type").getAsString());
				rankListItem.setUpdateDate(rankContent.get("update_date")
						.getAsString());
				dataList.add(rankListItem);
			}
		}

		// for (int j = 0; j < size; j++) {
		//
		// JsonObject songObj = array.get(j).getAsJsonObject();
		// RankListItem rankItem = new RankListItem();
		// rankItem.setCycleType(songObj.get("cycle_type").getAsString());
		// rankItem.setLogo(songObj.get("logo").getAsString());
		// rankItem.setLogoMiddle(songObj.get("logo_middle").getAsString());
		// rankItem.setSongs(getSongList(songObj.get("songs")));
		// rankItem.setTitle(songObj.get("title").getAsString());
		// rankItem.setType(songObj.get("type").getAsString());
		// rankItem.setUpdateDate(songObj.get("update_date").getAsString());
		//
		//
		// dataList.add(rankItem);
		// }

		return dataList;
	}

	/*
	 * 获取指定专辑ID的歌曲列表
	 */
	public List<OnlineSong> getTheAlbumSongs(JsonElement element) {
		List<OnlineSong> songList = new ArrayList<OnlineSong>();
		JsonObject obj = element.getAsJsonObject();
		element = obj.get("songs");
		songList = getSongList(element);
		return songList;
	}

	/**
	 * 获取艺人详细信息
	 * 
	 * @param jsonData
	 * @return
	 */
	public OnlineArtist getArtistDetail(JsonElement element) {

		if (null == element)
			return null;
		;

		JsonObject itemObj = element.getAsJsonObject();

		OnlineArtist onlineArtist = new OnlineArtist();
		onlineArtist.setId(getJsonObjectValueInt(itemObj, "artist_id"));
		onlineArtist.setName(getJsonObjectValue(itemObj, "artist_name"));
		onlineArtist.setLogo(getJsonObjectValue(itemObj, "artist_logo"));
		onlineArtist.setCategory(getJsonObjectValueInt(itemObj, "artist_id"));
		onlineArtist
				.setEnglish_name(getJsonObjectValue(itemObj, "english_name"));
		onlineArtist.setGender(getJsonObjectValue(itemObj, "gender"));
		onlineArtist.setDescription(getJsonObjectValue(itemObj, "description"));
		onlineArtist.setArea(getJsonObjectValue(itemObj, "area"));
		onlineArtist
				.setCountLikes(getJsonObjectValueInt(itemObj, "count_likes"));
		onlineArtist
				.setRecommends(getJsonObjectValueInt(itemObj, "recommends"));

		return onlineArtist;
	}

	/**
	 * 请求数据接口（自解析Json方式�?
	 * 
	 * @param methodCode
	 *            方法名称
	 * @param params
	 *            应用传入参数
	 * @return
	 */
	public String xiamiRequest(String methodCode,
			HashMap<java.lang.String, java.lang.Object> params)
			throws java.security.NoSuchAlgorithmException, java.io.IOException,
			com.xiami.core.exceptions.AuthExpiredException,
			com.xiami.core.exceptions.ResponseErrorException {

		Log.e("YDINFOR",
				"++++++++++++++++xiamiRequest()+++++++++++++++++++++++++++++");
//		 mXiamiSDK.enableLog(true);
		String results = mXiamiSDK.xiamiSDKRequest(methodCode, params);
		return results;
	}

	/**
	 * 根据给定控件尺寸size，向上兼容返回一个合适的图片尺寸
	 * 
	 * @param url
	 * @param size
	 * @return
	 */
	public String transferImgUrl(String url, int size) {
		return ImageUtil.transferImgUrl(url, size);
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

	public int getJsonObjectValueInt(JsonObject jsonObj, String key) {

		int rValue = 0;

		try {
			rValue = jsonObj.get(key).getAsInt();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}

		return rValue;
	}

	private long getJsonObjectValueLong(JsonObject jsonObj, String key) {

		long rValue = 0;

		try {
			rValue = jsonObj.get(key).getAsLong();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}

		return rValue;
	}

	private float getJsonObjectValueFloat(JsonObject jsonObj, String key) {

		float rValue = 0;
		try {
			rValue = jsonObj.get(key).getAsFloat();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
		return rValue;
	}

	private String getJsonObjectValue(JsonObject jsonObj, String key) {

		String rValue = "";
		try {
			rValue = jsonObj.get(key).getAsString();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
		return rValue;
	}

	public boolean getJsonObjectValueBool(JsonObject jsonObj, String key) {

		boolean rValue = false;
		try {
			rValue = jsonObj.get(key).getAsBoolean();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
		return rValue;

	}

	private JsonArray getJsonObjectArray(JsonObject jsonObj, String key) {

		JsonArray rValue = null;
		try {
			JsonElement element = jsonObj.get(key);
			if (element.isJsonArray()) {
				rValue = element.getAsJsonArray();
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
		return rValue;
	}

	public JsonObject getJsonObject(JsonElement element) {

		JsonObject rValue = null;
		try {
			if (element.isJsonObject()) {
				rValue = element.getAsJsonObject();
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
		return rValue;

	}

	/*
	 * 
	 * 发送音乐列表到音响端
	 */
	public void sendMusics(Context context, List<OnlineSong> list) {

		if (NetworkUtils.isWifiConnected(context)) {
			if (!StringUtils.hasLength(ClientSendCommandService.serverIP)) {
				Toast.makeText(context, "手机未连接音箱，请确认后再推送", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			MyApplication.vibrator.vibrate(100);

			String ipAddress = NetworkUtils.getLocalHostIp();
			String httpAddress = "http://" + ipAddress + ":"
					+ HTTPDService.HTTP_PORT;

			try {
				JSONObject o = new JSONObject();
				JSONArray array = new JSONArray();

				for (OnlineSong onlineSong : list) {
					String tempPath =onlineSong.getListenFile();
					tempPath=(null==tempPath)?"null":tempPath;
//					String tempPath = Encryptor.decryptUrl(onlineSong.getListenFile());// 解密播放地址
					String title = onlineSong.getSongName().trim();
					String artist = onlineSong.getArtistName().trim();
					int duration = onlineSong.getLength();
					JSONObject music = new JSONObject();
                    
					music.put("tempPath", tempPath);
					music.put("title", title);
					music.put("artist", artist);
					music.put("duration", duration);
					array.put(music);

				}
				o.put("musicss", array.toString());
				o.put("musicType", "xiaMi");
				File jsonFile = new File(HTTPDService.defaultHttpServerPath
						+ "/MusicList.json");
				if (jsonFile.exists()) {
					jsonFile.delete();
				}
				jsonFile.createNewFile();

				FileWriter fw = new FileWriter(jsonFile);
				fw.write(o.toString(), 0, o.toString().length());
				fw.flush();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(context, "音频获取失败", Toast.LENGTH_SHORT).show();
			}
			// 发送播放地址
			ClientSendCommandService.msg = "GetMusicList:" + httpAddress
					+ "/MusicList.json";
			ClientSendCommandService.handler.sendEmptyMessage(4);
			// }
		} else {
			Toast.makeText(context, "请链接无线网络", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 根据简略信息歌曲列表返回带歌曲地址的详细歌曲列表
	 */

	public List<OnlineSong> getDetailList(List<OnlineSong> list) {
		List<OnlineSong> dataList = new ArrayList<OnlineSong>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				OnlineSong song = new OnlineSong();
				song = mXiamiSDK.findSongByIdSync(list.get(i).getSongId(),
						OnlineSong.Quality.L);
				dataList.add(song);
			}
		}
		return dataList;
	}
	

	
	

	public List<OnlineSong> getSortSongs(List<OnlineSong> songs, int index) {
		if (null == songs || index >= songs.size())
			return null;

		List<OnlineSong> songList = new ArrayList<OnlineSong>();
		int size = songs.size();
		for (int i = index; i < size; i++) {
			OnlineSong song = songs.get(i);
			songList.add(song);
			if ((i + 1) >= songs.size()) {
				i = -1;
				size = index;
			}
		}
		return songList;
	}

}
