package com.search.aidl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.R.string;
import android.hardware.Camera.Area;

public class KeyWordsUtil {

	/**
	 * 
	 * String person;
		String area;
		String category;
		String modifier;
		String name;
		String year;
	 */
	static final String ALL = "不限";
	static final String OTHER = "其他";

	// area	
	static final String CHINA = "大陆";
	static final String AMERICAN = "美国";
	static final String KOREA = "韩国";
	static final String JAPAN = "日本";
    static final String HONGKONG = "香港";
    static final String TAIWAN = "台湾";
    static final String BRAITAIN = "英国";
    static final String FRANCE = "法国";
    static final String THAILAND = "泰国";
    static final String INDIA = "印度";
    static final String CANADA = "加拿大";
    static final String GERMANY = "德国";
    static final String ITALY = "意大利";
    static final String SPANISH = "西班牙";
    static final String AUSTRALIA = "澳大利亚";
    static final String RUSSIAN = "俄罗斯";
    static final String IRELAND = "爱尔兰";
    static final String SINGAPORE = "新加坡";	
    
    static final List<String> AREA = new ArrayList<String>(
    		Arrays.asList(
    			ALL,
    			"综艺",
    			"电视剧",
    			CHINA,
    			AMERICAN,
    			KOREA,
    			JAPAN,
    		    HONGKONG,
    		    TAIWAN,
    		    BRAITAIN,
    		    FRANCE,
    		    THAILAND,
    		    INDIA,
    		    CANADA,
    		    GERMANY,
    		    ITALY,
    		    SPANISH,
    		    AUSTRALIA,
    		    RUSSIAN,
    		    IRELAND,
    		    SINGAPORE,
    			OTHER)
    		);
    // main category
    static final String MOVIE = "电影";
    static final String TV_PLAY = "电视剧";
    static final String VARIETY = "综艺";
    static final String CHILD ="少儿";
    static final String SPORTS = "体育";
	
    static final List<String> MAINKIND = new ArrayList<String>(
    		Arrays.asList(
    			ALL,
    			MOVIE,
    			TV_PLAY,
    			VARIETY,
    			CHILD,
    			SPORTS,    			
    			OTHER)
    );
    
	// category

	static final String COMEDY = "喜剧";
	static final String HORROR = "恐怖";
	static final String LOVE = "爱情";
	static final String ACTION = "动作";
	static final String ETHICAL = "伦理";
	static final String SCI_FI = "科幻";
	static final String WAR = "战争";
	static final String CRIME = "犯罪";
	static final String THRILLER = "惊悚";
	static final String ANIME = "动画";
	static final String FEATURE = "剧情";
	static final String COSTUME = "古装";
	static final String FANTASY = "奇幻";
	static final String KUNGFU = "武侠";
	static final String ADVERNTURE = "冒险";
	static final String SUSPENCE = "悬疑";
	static final String SPORT = "运动";
	static final String MUSIC = "英语";
	static final String DANCE = "歌舞";
	static final String HISTORY = "历史";
	static final String SEXY = "情色";
	static final String ART = "文艺";
	static final String CLASSIC = "经典";
		
	static final List<String> CATEGORY = new ArrayList<String>(
    		Arrays.asList(
    			ALL,
    			COMEDY,
    			HORROR,
    			LOVE,
    			ACTION,
    			ETHICAL,
    			SCI_FI,
    			WAR,
    			CRIME,
    			THRILLER,
    			ANIME,
    			FEATURE,
    			COSTUME,
    			FANTASY,
    			KUNGFU,
    			ADVERNTURE,
    			SUSPENCE,
    			SPORT,
    			MUSIC,
    			DANCE,
    			HISTORY,
    			SEXY,
    			ART,
    			CLASSIC,
    			OTHER)
    );
	
	
	// AREA of MALL VIDEO
	public static List<String> getAreaList()
	{
		return AREA;
	}
	
	public static List<String> getMainKindList()
	{
		return MAINKIND;
	}
	
	// CATEGORY of MALL VIDEO
	public static List<String> getCategoryList()
	{
		return CATEGORY;
	}	
}
