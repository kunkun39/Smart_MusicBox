package com.changhong.yinxiang.utils;

import com.xiami.music.api.utils.RequestMethods;

public class Configure {

	public final static int IMAGE_SIZE1 = 80, IMAGE_SIZE2 = 120, IMAGE_SIZE3 = 400;
	
	public final static int XIAMI_RESPOND_SECCESS=1000;
	public final static int XIAMI_RESPOND_FAILED=1001;

	public final static int XIAMI_TODAY_RECOMSONGS=1002;//今日推荐单曲
	public final static int XIAMI_NEW_ALBUMS=1003;//新碟首发
	
	
	public static int getRequestType(String str){
		int i=XIAMI_RESPOND_SECCESS;
		if(str.equals(RequestMethods.METHOD_RECOMMEND_DAILYLIST)){
			i=XIAMI_TODAY_RECOMSONGS;
		}else if(str.equals(RequestMethods.METHOD_COLLECT_RECOMMEND)){
			i=XIAMI_NEW_ALBUMS;
		}
		return i;
	}
}
