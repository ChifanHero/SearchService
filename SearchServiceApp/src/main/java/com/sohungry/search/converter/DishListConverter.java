package com.sohungry.search.converter;

import java.util.List;

import com.google.gson.JsonObject;
import com.sohungry.search.model.DishField;
import com.sohungry.search.model.DishList;
import com.sohungry.search.model.DishListField;
import com.sohungry.search.model.Picture;

public class DishListConverter implements Converter<DishList>{
	
	private List<String> fields;
	
	public DishListConverter(List<String> fields) {
		this.fields = fields;
	}

	@Override
	public DishList convert(JsonObject source) {
		if (source == null || !source.isJsonObject() || fields == null || fields.isEmpty()) {
			return null;
		}
		boolean returnAll = false;
		if (fields == null) {
			returnAll = true;
		} else if (fields.isEmpty()) {
			return new DishList();
		} 
		DishList dishList = new DishList();
		if (returnAll || fields.contains(DishListField.id.name())) {
			if (source.get("objectId") != null && !source.get("objectId").isJsonNull()) {
				dishList.setId(source.get("objectId").getAsString());
			}
		}
		if (returnAll || fields.contains(DishListField.name.name())) {
			if (source.get("name") != null && !source.get("name").isJsonNull()) {
				dishList.setName(source.get("name").getAsString());
			}
		}
		
		if (returnAll || fields.contains(DishListField.member_count.name())) {
			if (source.get("member_count") != null && !source.get("member_count").isJsonNull()) {
				dishList.setMemberCount(source.get("member_count").getAsLong());
			}
		}
		
		if (returnAll || fields.contains(DishListField.favorite_count.name())) {
			if (source.get("favorite_count") != null && !source.get("favorite_count").isJsonNull()) {
				dishList.setFavoriteCount(source.get("favorite_count").getAsLong());
			}
		}
		
		if (returnAll || fields.contains(DishListField.like_count.name())) {
			if (source.get("like_count") != null && !source.get("like_count").isJsonNull()) {
				dishList.setFavoriteCount(source.get("like_count").getAsLong());
			}
		}
		
		if (returnAll || fields.contains(DishField.picture.name())) {
			if (source.get("picture") != null && !source.get("picture").isJsonNull()) {
				JsonObject pic = source.get("picture").getAsJsonObject();
				if (pic != null) {
					Picture picture = new Picture();
					picture.setOriginal(pic.get("original").getAsString());
					picture.setThumbnail(pic.get("thumbnail").getAsString());
					dishList.setPicture(picture);
				}
			}
		}
		return dishList;
	}

	
}
