package com.sohungry.search.util;

import org.springframework.util.StringUtils;

public class HighlightUtil {
	
	public static int getNumberOfHighlightTags(String s) {
		if (s == null || s.isEmpty() ) {
			return 0;
		}
		int count = StringUtils.countOccurrencesOf(s, "<");
		return count;
	}

}
