package com.changhong.xiami.artist;

import java.util.Comparator;

import com.changhong.xiami.data.XiamiDataModel;


public class PinyinComparator implements Comparator<XiamiDataModel> {

	public int compare(XiamiDataModel o1, XiamiDataModel o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
