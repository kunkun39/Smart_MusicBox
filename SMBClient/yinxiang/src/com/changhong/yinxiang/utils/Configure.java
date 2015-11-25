package com.changhong.yinxiang.utils;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;
import com.xiami.music.api.utils.RequestMethods;

public class Configure {

	public final static int IMAGE_SIZE1 = 80, IMAGE_SIZE2 = 120,
			IMAGE_SIZE3 = 400;

	public final static int XIAMI_RESPOND_SECCESS = 1000;
	public final static int XIAMI_RESPOND_FAILED = 1001;

	public final static int XIAMI_TODAY_RECOMSONGS = 1002;// 今日推荐单曲
	public final static int XIAMI_PROMOTION_ALBUMS = 1003;// 新碟首发
	public final static int MUSIC_TYPE_COLLECT = 1004;// 专辑歌曲列表
	public final static int XIAMI_RANK_HUAYU = 1005;// 华语排行榜
	public final static int XIAMI_RANK_ALL = 1006;// 全部歌曲排行榜
	public final static int XIAMI_RANK_LIST = 1007;// 榜单列表
	public final static int XIAMI_RANK_DETAIL = 1008;// 指定榜单类型
	public final static int XIAMI_ALBUM_DETAIL = 1009;// 指定指定专辑

	public final static int XIAMI_ARTIST_WORDBOOK = 1014;// 艺人大全
	public final static int XIAMI_RECOMMEND_PROMOTIONS_ARTISTS = 1015;// 推荐艺人
	public final static int XIAMI_ARTIST_DETAIL = 1016;// 艺人详细信息
	public final static int XIAMI_ARTIST_HOTSONGS = 1017;// 热门歌曲

	public final static int XIAMI_COLLECT_RECOMMEND = 1018;// 精选集推荐
	public final static int XIAMI_NEW_ALBUMS = 1019;// 新碟上架-音乐会-专辑

	public final static int[] yinxiao_resID = { R.drawable.yinxiaomovie,
			R.drawable.yinxiaotv, R.drawable.yinxiaomusic,
			R.drawable.yinxiaogame, R.drawable.yinxiaoyd, R.drawable.yinxiaoxt };
	public final static int[] light_resID = { R.drawable.lightsup,
			R.drawable.lightsdown, R.drawable.lightssun, R.drawable.lightsmoon };

	public static String getAudioSettingCMD(int resID) {
		String re_value = "";
		if (R.drawable.yinxiaomovie == resID) {
			re_value = "yxmovie";
		} else if (R.drawable.yinxiaotv == resID) {
			re_value = "yxtv";
		} else if (R.drawable.yinxiaomusic == resID) {
			re_value = "yxmusic";
		} else if (R.drawable.yinxiaogame == resID) {
			re_value = "yxgame";
		} else if (R.drawable.yinxiaoyd == resID) {
			re_value = "yxyd";
		} else if (R.drawable.yinxiaoxt == resID) {
			re_value = "yxxt";
		} else if (R.drawable.lightsup == resID) {
			re_value = "lightsup";
		} else if (R.drawable.lightsdown == resID) {
			re_value = "lightsdown";
		} else if (R.drawable.lightssun == resID) {
			re_value = "lightssun";
		} else if (R.drawable.lightsmoon == resID) {
			re_value = "lightsmoon";
		}
		return re_value;
	}

	/*
	 * 服务器请求参数类型RequestMethods
	 */
	public final static String RequestMethods_PROMOTION_ALBUMS = "rank.promotion-albums";// 新碟首发

	public static int getRequestType(String str) {
		int i = XIAMI_RESPOND_SECCESS;
		if (str.equals(RequestMethods.METHOD_RECOMMEND_DAILYLIST)) {
			i = XIAMI_TODAY_RECOMSONGS;
		} else if (str.equals(RequestMethods_PROMOTION_ALBUMS)) {
			i = XIAMI_PROMOTION_ALBUMS;
		} else if (str.equals(RequestMethods.METHOD_ARTIST_WORDBOOK)) {
			i = XIAMI_ARTIST_WORDBOOK;
		} else if (str.equals(RequestMethods.METHOD_RECOMMEND_PROMOTIONS_ARTISTS)) {
			i = XIAMI_RECOMMEND_PROMOTIONS_ARTISTS;
		} else if (str.equals(RequestMethods.METHOD_ARTIST_DETAIL)) {
			i = XIAMI_ARTIST_DETAIL;
		} else if (str.equals(Configure.RequestMethods_PROMOTION_ALBUMS)) {
			i = XIAMI_PROMOTION_ALBUMS;
		} else if (str.equals(RequestMethods.METHOD_RANK_DETAIL)) {
			i = XIAMI_RANK_DETAIL;
		} else if (str.equals(RequestMethods.METHOD_RANK_LIST)) {
			i = XIAMI_RANK_LIST;
		} else if (str.equals(RequestMethods.METHOD_ARTIST_HOTSONGS)) {
			i = XIAMI_ARTIST_HOTSONGS;
		} else if (str.equals(RequestMethods.METHOD_COLLECT_RECOMMEND)) {
			i = XIAMI_COLLECT_RECOMMEND;
		} else if (str.equals(RequestMethods.METHOD_ALBUMS_DETAIL)) {
			i = XIAMI_ALBUM_DETAIL;
		} else if (str.equals(RequestMethods.METHOD_RANK_NEWALBUM)) {
			i = XIAMI_NEW_ALBUMS;
		}

		return i;
	}
}
