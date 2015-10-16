package com.changhong.search;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;

import com.xiami.sdk.XiamiSDK;
import com.xiami.sdk.entities.OnlineAlbum;
import com.xiami.sdk.entities.QueryInfo;
import com.xiami.sdk.entities.RankType;

public class XMMusicData {

	/**
	 * XiamiSDK
	 */
	private static XiamiSDK mXiamiSDK = null;
	public static final String KEY = "825bdc1bf1ff6bc01cd6619403f1a072";
	public static final String SECRET = "7ede04a287d0f92c366880ba515293fd";
	
	/**
     * 排行榜类型
     */
    private List<RankType> RANK_LIST_TYPE = new ArrayList<RankType>();
    

    /**
     * 排行榜标题
     */
    private List<String> RANK_LIST_TITLE = new ArrayList<String>();

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
	private static XMMusicData xMMusicData = null;

	public static XMMusicData getInstance(Context con) {
		if (null == xMMusicData) {
			xMMusicData = new XMMusicData();
			mXiamiSDK = new XiamiSDK(con, KEY, SECRET);
		}
		return xMMusicData;
	}

	
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
	
//	public void get
}
