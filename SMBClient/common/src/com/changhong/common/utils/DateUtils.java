package com.changhong.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jack Wang
 */
public class DateUtils {

	public static final String DEFAUT_DATE_FORMAT = "yyyy-MM-dd";

	public static final String DEFAUT_TIME_FORMAT = "HH:mm";

	public static String to10String(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				DEFAUT_DATE_FORMAT);
		return simpleDateFormat.format(date);
	}

	public static String getMedioAddDate(long timestamp) {
		Date addDate = new Date(timestamp * 1000);
		return to10String(addDate);
	}

	public static String getTimeShow(long seconds) {
		long minitus = seconds / 60;
		long leftSeconds = seconds % 60;
		return StringUtils.toFixLength(String.valueOf(minitus), 2) + ":"
				+ StringUtils.toFixLength(String.valueOf(leftSeconds), 2);
	}

	/**
	 * monday is 1 and sunday is 7
	 */
	public static int getWeekIndex(int plusDays) {
		Date day = new Date();
		day.setDate(day.getDate() + plusDays);
		int weekIndex = day.getDay();
		if (weekIndex == 0) {
			weekIndex = 7;
		}
		return weekIndex;
	}

	/**
	 * return 周一->周天
	 */
	public static String getWeekIndexName(int plusDays) {
		int weekIndex = getWeekIndex(plusDays);
		if (weekIndex == 0) {
			weekIndex = 7;
		}

		String weekIndexName = "";
		switch (weekIndex) {
		case 1:
			weekIndexName = "周一";
			break;
		case 2:
			weekIndexName = "周二";
			break;
		case 3:
			weekIndexName = "周三";
			break;
		case 4:
			weekIndexName = "周四";
			break;
		case 5:
			weekIndexName = "周五";
			break;
		case 6:
			weekIndexName = "周六";
			break;
		case 7:
			weekIndexName = "周天";
			break;
		default:
			break;
		}
		return weekIndexName;
	}

	/**
	 * format like this 12:00
	 */
	public static String getCurrentTimeStamp() {
		Date current = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				DEFAUT_TIME_FORMAT);
		return simpleDateFormat.format(current);
	}

	public static String getOrderDate(int plusDays) {
		Date day = new Date();
		day.setDate(day.getDate() + plusDays);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				DEFAUT_DATE_FORMAT);
		return simpleDateFormat.format(day);
	}

	public static String getDayOfToday() {
		Date day = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				DEFAUT_DATE_FORMAT + DEFAUT_TIME_FORMAT);
		return simpleDateFormat.format(day);
	}

	/*
	 * add by cym 根据int值获取闹铃在哪几个星期重复
	 */
	public static boolean[] getRepeatWeek(int weekInt) {
		boolean[] weeks = new boolean[7];
		for (int i = 0; i < 7; i++) {
			weeks[i] = ((weekInt & (1 << i)) > 0);
		}
		return weeks;
	}
/*
 * 
 * boolean转int值
 */
	public static int RWArrayToInt(boolean[] weeks) {
		int weekInt = 0;

		if (weeks != null && 7 == weeks.length) {
			for (int i = 0; i < 7; i++) {
				if (weeks[i]) {
					weekInt |= (1 << i);
				} else {
					weekInt &= ~(1 << i);
				}
			}
		}
		return weekInt;
	}
}
