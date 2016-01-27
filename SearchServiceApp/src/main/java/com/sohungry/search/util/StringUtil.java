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

}
