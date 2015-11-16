package com.changhong.xiami.data;

import java.util.Comparator;



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
