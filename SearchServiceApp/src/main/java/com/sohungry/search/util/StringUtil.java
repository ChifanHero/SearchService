package com.sohungry.search.util;

public class StringUtil {
	
	public static boolean isLettersOnly(String name) {
	    return name.matches("[a-zA-Z]+");
	}
	
	public static boolean containsHanScript(String s) {
	    return s.codePoints().anyMatch(
	            codepoint ->
	            Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN);
	}
	
	public static String removeChineseCharacterFromString(String s) {
//		String s = "小米体验版 latin string 01234567890";
		s = s.replaceAll("[^\\x00-\\x7F]", "");
		return s;
	}
	
	public static void main(String[] args) {
		String s = "2855 Stevens Creek Blvd, Santa Clara, CA 95050美国";
		System.out.println(removeChineseCharacterFromString(s));
	}

}
