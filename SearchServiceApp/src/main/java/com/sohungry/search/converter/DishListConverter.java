package com.sohungry.search.converter;

import java.util.List;

import com.google.gson.JsonObject;
import com.sohungry.search.model.DishList;
import com.sohungry.search.model.DishListField;

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
		return dishList;
	}

	
}
