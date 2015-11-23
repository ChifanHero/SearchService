package com.sohungry.search.parse.util;

import org.parse4j.ParseObject;

import com.sohungry.search.index.source.shared.Pointer;

public class ParseValidator {
	
	public static boolean isValidPointer(Pointer pointer) {
		if (pointer == null) {
			return false;
		} else {
			if (pointer.getClassName() != null && pointer.getObjectId() != null) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public static boolean isValidObject(ParseObject object) {
		if (object == null) {
			return false;
		} else {
			if (object.getClassName() != null && object.getObjectId() != null) {
				return true;
			} else {
				return false;
			}
		}
	}

}
