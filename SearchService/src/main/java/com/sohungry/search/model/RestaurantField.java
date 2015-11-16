package com.sohungry.search.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public enum RestaurantField {
	
	all,
	basic,
	id,
	name,
	english_name,
	address,
	distance,
	favorite_count,
	like_count,
	dislike_count,
	neutral_count,
	phone,
	hours,
	picture;
	
	public static RestaurantField fromString(String text) {
		if (text != null) {
			for (RestaurantField b : RestaurantField.values()) {
				if (text.equalsIgnoreCase(b.name())) {
					return b;
				}
			}
		}
		return null;
	}

}
