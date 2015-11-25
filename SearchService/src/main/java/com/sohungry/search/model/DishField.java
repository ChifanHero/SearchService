package com.sohungry.search.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public enum DishField {
	
	id,
	name,
	favorite_count,
	like_count,
	dislike_count,
	neutral_count,
	picture,
	from_restaurant,
	related_lists;
	
	public static DishField fromString(String text) {
		if (text != null) {
			for (DishField b : DishField.values()) {
				if (text.equalsIgnoreCase(b.name())) {
					return b;
				}
			}
		}
		return null;
	}

}
