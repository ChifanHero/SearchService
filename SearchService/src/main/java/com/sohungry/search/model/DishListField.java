package com.sohungry.search.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public enum DishListField {
	
	id,
	name,
	member_count,
	like_count,
	favorite_count,
	picture
	;
	
	public static DishListField fromString(String text) {
		if (text != null) {
			for (DishListField b : DishListField.values()) {
				if (text.equalsIgnoreCase(b.name())) {
					return b;
				}
			}
		}
		return null;
	}

}
