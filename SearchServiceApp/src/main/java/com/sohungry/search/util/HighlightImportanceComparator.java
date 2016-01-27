package com.sohungry.search.util;

import java.util.Comparator;

public class HighlightImportanceComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		if (o1 == null && o2 == null) {
			return 0;
		} else if (o1 == null && o2 != null) {
			return 1;
		} else if (o1 != null && o2 == null) {
			return -1;
		}
		int count1 = HighlightUtil.getNumberOfHighlightTags(o1);
		int count2 = HighlightUtil.getNumberOfHighlightTags(o2);
		if (count1 > count2) {
			return -1;
		} else if (count1 == count2) {
			if (o1.length() > o2.length()) {
				return 1;
			} else if (o1.length() < o2.length()) {
				return -1;
			} else {
				return 0;
			}
		} else {
			return 1;
		}
	}

}
