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

public class CopyOfXMMusicData {

	/**
	 * XiamiSDK
	 */
	public static XiamiSDK mXiamiSDK = null;
	public static final String KEY = "825bdc1bf1ff6bc01cd6619403f1a072";
	public static final String SECRET = "7ede04a287d0f92c366880ba515293fd";
	
	/**
     * 排行榜类型
     */
    public static List<RankType> RANK_LIST_TYPE = new ArrayList<RankType>();
    

    /**
     * 排行榜标题
     */
    public static List<String> RANK_LIST_TITLE = new ArrayList<String>();

	/**
	 * 每页容量
	 */
	private static final int PAGE_SIZE = 10;

	/**
	 * 所取页码
	 */
	private static final int PAGE_INDEX = 1;

	/**
	 * 推荐专辑列表
	 */
	private List<OnlineAlbum> mAlbums=null;

	/**
	 * 推荐专辑名列表
	 */
	private String[] mAlbumName = null;
	/**
	 * 单例的musicData类
	 */
	private static CopyOfXMMusicData xMMusicData = null;

	public static CopyOfXMMusicData getInstance(Context con) {
		if (null == xMMusicData) {
			xMMusicData = new CopyOfXMMusicData();
			
			initRankSong();
		}
		if(null==mXiamiSDK){
			mXiamiSDK = new XiamiSDK(con, KEY, SECRET);
		}
		return xMMusicData;
	}

	/*
	 * 获取本周热门专辑（同步执行）
	 */
	public void getAlbumName(final Handler handler){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Pair<QueryInfo, List<OnlineAlbum>> results = mXiamiSDK.getWeekHotAlbumsSync(PAGE_SIZE, PAGE_INDEX);
				if (null == results) {
					return;
				}
				mAlbums = results.second;
				// 显示播放音乐信息
				if (mAlbums != null) {
					mAlbumName = new String[mAlbums.size()];
					for (int i = 0; i < mAlbums.size(); i++) {
						mAlbumName[i] = mAlbums.get(i).getAlbumName();
					}
				}
				//通知搜索界面获取推荐专辑名
				Message msg=handler.obtainMessage();
				msg.what=1;
				msg.obj=mAlbumName;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	/*
	 * 
	 * 获取今日推荐歌曲列表
	 */
	public List<OnlineSong> getRecommendSong(int pageSize,int pageIndex){
		Pair<QueryInfo, List<OnlineSong>> results=mXiamiSDK.getRecommendSongsSync(pageSize, pageIndex);
		List<OnlineSong> songs=results.second;
		return songs;
	}
	
	/*
	 * 
	 * 获取新碟首发专辑
	 */
	public List<OnlineAlbum> getNewAlbums(LanguageType lang,int pageSize,int pageIndex){
		Pair<QueryInfo, List<OnlineAlbum>> results=mXiamiSDK.getNewAlbumsSync(lang, pageSize, pageIndex);
		List<OnlineAlbum> albums=results.second;
		return albums;
	}
	/*
	 * 获取专辑列表详情
	 */
	public OnlineAlbum getDetailAlbum(long id){
		OnlineAlbum album;
		album=mXiamiSDK.getAlbumsDetailSync(id);
		return album;
	}
	
	
	/*
	 * 
	 * 获取本周热门专辑
	 */
	public List<OnlineAlbum> getWeekHotAlbumsSync(int pageSize,int pageIndex){
		Pair<QueryInfo, List<OnlineAlbum>> results=mXiamiSDK.getWeekHotAlbumsSync(pageSize, pageIndex);
		if(null != results)return results.second;
		return null;
	}
	
	
	
	/*
	 * 
	 * 获取专辑详情
	 */
	public OnlineAlbum getAlbumsDetailSync(long albumid){
		OnlineAlbum result=mXiamiSDK.getAlbumsDetailSync(albumid);
		if(null != result)return result;
		return null;
	}
	
	
	
	/*
	 * 初始化排行榜类型
	 */
	public static void initRankSong(){
//		 	RANK_LIST_TITLE.add("虾米音乐榜（全部）");   RANK_LIST_TYPE.add(RankType.music_all);
//	        RANK_LIST_TITLE.add("虾米音乐榜（欧美）");   RANK_LIST_TYPE.add(RankType.music_oumei);
//	        RANK_LIST_TITLE.add("虾米音乐榜（华语）");   RANK_LIST_TYPE.add(RankType.music_huayu);
//	        RANK_LIST_TITLE.add("虾米新歌榜（全部）");   RANK_LIST_TYPE.add(RankType.newmusic_all);
//	        RANK_LIST_TITLE.add("虾米新歌榜（欧美）");   RANK_LIST_TYPE.add(RankType.newmusic_oumei);
//	        RANK_LIST_TITLE.add("虾米新歌榜（华语）");   RANK_LIST_TYPE.add(RankType.newmusic_huayu);
//	        RANK_LIST_TITLE.add("Hito中文排行榜");   RANK_LIST_TYPE.add(RankType.hito);
//	        RANK_LIST_TITLE.add("香港劲歌金榜");   RANK_LIST_TYPE.add(RankType.jingge);
//	        RANK_LIST_TITLE.add("英国UK单曲榜");   RANK_LIST_TYPE.add(RankType.uk);
//	        RANK_LIST_TITLE.add("虾米原创榜");   RANK_LIST_TYPE.add(RankType.music_original);
//	        RANK_LIST_TITLE.add("虾米Demo榜");   RANK_LIST_TYPE.add(RankType.music_demo);
	}
	
	/*
	 * 获取指定类型排行榜的音乐列表
	 */
	public List<OnlineSong> getRankSong(RankType type){
		List<OnlineSong> results = mXiamiSDK.getRankSongsSync(type);
		return results;
	}
	
	
	
	/*
	 * 获取热门艺人列表（同步执行）
	 */
	public List<OnlineArtist> getHotArtists(int pageSize){
		 List<OnlineArtist> results=mXiamiSDK.getHotArtistsSync(pageSize);
		 return results;
	}
	

	/**
	 * 获取艺人大全
	 * @param region  区域
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public List<OnlineArtist> fetchArtistListSync(ArtistRegion region, int pageSize, int pageIndex) {
		 List<OnlineArtist> results=null;
		 ArtistBook artistBook=mXiamiSDK.fetchArtistBookSync(region, pageSize, pageIndex);
		 if(null != artistBook) results=artistBook.getArtists();
		return results;
	}
	
	/**
	 * 获取艺人详细信息
	 * @param artistId 艺人Id号
	 * @return
	 */
	public OnlineArtist 	fetchArtistDetailSync(long artistId) {
		   
		OnlineArtist result=mXiamiSDK.fetchArtistDetailSync(artistId);
		return result;
	}
	
	
	public List<OnlineSong> fetchSongsByArtistIdSync(long artistId) {
		   
		List<OnlineSong> results=mXiamiSDK.fetchSongsByArtistIdSync(artistId);
		return results;
	}

	
	/**
	 * 获取艺人图片
	 * @param artist
	 * @param size
	 * @return
	 */
	public Bitmap getArtistImage(String imgUrl) {
		 Bitmap  mBitmap= getBitmapFromUrl(imgUrl);
		return mBitmap;
	}
	
	
	
	public List<SceneSongs>  getRecommendSceneSongs(String gps, String ssid, String bssid){
		
		Log.e("YDINFOR", "++++++++++++++++getRecommendSceneSongs()+++++++++++++++++++++++++++++");
//		mXiamiSDK.enableLog(true);
		List<SceneSongs> results=mXiamiSDK.getRecommendSceneSongs(gps,ssid,bssid);
		return results;
	}
	
	/*
	 * 根据场景推荐歌曲
	 * 
	 */
	public List<OnlineSong> getRecommendSong(String gps,int limit){
		List<OnlineSong> results=mXiamiSDK.getRecommendWeatherSongs(gps,limit);
		return results;
	}
	
	/*
	 * 
	 * 获取最新精选集接口
	 */
	public List<OnlineCollect> getNewCollect(int pageSize,int pageIndex){
		Pair<QueryInfo, List<OnlineCollect>>  results=mXiamiSDK.getNewCollectSync(pageSize, pageIndex);
		List<OnlineCollect> collect=results.second;
		return collect;
	}
	
   /**	
    * 获取精选集详情
    * @param collectID
    * @return
    */
	public OnlineCollect getCollectDetailSync(long collectID){
		OnlineCollect  results=mXiamiSDK.getCollectDetailSync(collectID);
		return results;
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
		mXiamiSDK.enableLog(true);
		String  results=mXiamiSDK.xiamiSDKRequest(methodCode,params);
		return results;
	}
	
	
	 public Bitmap readAPIC(String imgUrl) {
		    Log.e("YDINFOR", "++++++++++++++++readAPIC()+++++++++++++++++++++++++++++");
			mXiamiSDK.enableLog(true);
		    return mXiamiSDK.readAPIC(imgUrl, new BitmapFactory.Options());		   
	 }
	
	
	  /**
     * 到Url去下载回传Bitmap
     * @param imgUrl
     * @return
     */
    public Bitmap getBitmapFromUrl(String imgUrl) {
                URL url;
                Bitmap bitmap = null;
                BufferedInputStream bis=null;
                try {
                        url = new URL(imgUrl);
                        InputStream is = url.openConnection().getInputStream();
                        bis = new BufferedInputStream(is);
                        bitmap = BitmapFactory.decodeStream(bis);
                        bitmap=compressBmpFromBmp(bitmap);
                } catch (MalformedURLException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }finally{
                	
                    try {
                    	
                    	if(null != bis){
                    		bis.close();
                            bis=null;
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
		int length=baos.toByteArray().length;
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;
	}


    
    
}
