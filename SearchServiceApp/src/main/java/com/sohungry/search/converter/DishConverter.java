package com.sohungry.search.converter;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sohungry.search.model.Dish;
import com.sohungry.search.model.DishField;
import com.sohungry.search.model.DishList;
import com.sohungry.search.model.DishListField;
import com.sohungry.search.model.DistanceUnit;
import com.sohungry.search.model.Location;
import com.sohungry.search.model.Picture;
import com.sohungry.search.model.Restaurant;
import com.sohungry.search.model.RestaurantField;

public class DishConverter implements Converter<Dish>{
	
	private List<String> fields;
	private Location userLocation;
	private DistanceUnit distanceUnit;
	private String language;
	
	public DishConverter(List<String> fields) {
		this(fields, null, null, null);
	}
	
	public DishConverter(List<String> fields, Location userLocation, DistanceUnit distanceUnit, String language) {
		this.fields = fields;
		this.userLocation = userLocation;
		this.distanceUnit = distanceUnit;
		this.language = language;
	}

	@Override
	public Dish convert(JsonObject source) {
		if (source == null || !source.isJsonObject() || fields == null || fields.isEmpty()) {
			return null;
		}
		boolean returnAll = false;
		if (fields == null) {
			returnAll = true;
		} else if (fields.isEmpty()) {
			return new Dish();
		} 
		Dish dish = new Dish();
		if (returnAll || fields.contains(DishField.dislike_count.name())) {
			if (source.get("dislike_count") != null && !source.get("dislike_count").isJsonNull()) {
				dish.setDislikeCount(source.get("dislike_count").getAsLong());
			}
		}
		
		if (returnAll || fields.contains(DishField.english_name.name())) {
			if (source.get("english_name") != null && !source.get("english_name").isJsonNull()) {
				if ("en".equals(language)) {
					dish.setName(source.get("english_name").getAsString());
				} 
			}
		}
		
		if (returnAll || fields.contains(DishField.favorite_count.name())) {
			if (source.get("favorite_count") != null && !source.get("favorite_count").isJsonNull()) {
				dish.setFavoriteCount(source.get("favorite_count").getAsLong());
			}
		}
		
		if (returnAll || fields.contains(DishField.id.name())) {
			if (source.get("objectId") != null && !source.get("objectId").isJsonNull()) {
				dish.setId(source.get("objectId").getAsString());
			}
		}
		
		if (returnAll || fields.contains(DishField.like_count.name())) {
			if (source.get("like_count") != null && !source.get("like_count").isJsonNull()) {
				dish.setLikeCount(source.get("like_count").getAsLong());
			}
		}
		
		if (returnAll || fields.contains(DishField.name.name())) {
			if (source.get("name") != null && !source.get("name").isJsonNull()) {
				if ("zh".equals(language)) {
					dish.setName(source.get("name").getAsString());
				} 
			}
		}
		
		if (returnAll || fields.contains(DishField.neutral_count.name())) {
			if (source.get("neutral_count") != null && !source.get("neutral_count").isJsonNull()) {
				dish.setNeutralCount(source.get("neutral_count").getAsLong());
			}
		}
		
		if (returnAll || fields.contains(DishField.picture.name())) {
			if (source.get("picture") != null && !source.get("picture").isJsonNull()) {
				JsonObject pic = source.get("picture").getAsJsonObject();
				if (pic != null) {
					Picture picture = new Picture();
					picture.setOriginal(pic.get("original").getAsString());
					picture.setThumbnail(pic.get("thumbnail").getAsString());
					dish.setPicture(picture);
				}
			}
		}
		
		if (returnAll || fields.contains(DishField.from_restaurant.name())) {
			if (source.get("from_restaurant") != null && !source.get("from_restaurant").isJsonNull()) {
				JsonObject restaurant = source.get("from_restaurant").getAsJsonObject();
				List<String> restaurantFields = new ArrayList<String>();
				restaurantFields.add(RestaurantField.id.name());
				restaurantFields.add(RestaurantField.name.name());
				restaurantFields.add(RestaurantField.english_name.name());
				restaurantFields.add(RestaurantField.distance.name());
				Restaurant fromRestaurant = new RestaurantConverter(restaurantFields, this.userLocation, this.distanceUnit, this.language).convert(restaurant);
				dish.setFromRestaurant(fromRestaurant);
			}
		}
		
		if (returnAll || fields.contains(DishField.related_lists.name())) {
			if (source.get("lists") != null && !source.get("lists").isJsonNull()) {
				JsonArray lists = source.get("lists").getAsJsonArray();
				if (lists.size() > 0) {
					List<DishList> dishLists = new ArrayList<DishList>();
					List<String> dishListFields = new ArrayList<String>();
					dishListFields.add(DishListField.id.name());
					dishListFields.add(DishListField.name.name());
					for (int j = 0; j < lists.size(); j++) {
						JsonObject list = lists.get(j).getAsJsonObject();
						DishList dishList = new DishListConverter(dishListFields).convert(list);
						if (dishList != null) {
							dishLists.add(dishList);
						}
					}
					dish.setRelatedLists(dishLists);
				}
			}
		}
		
		return dish;
	}

	

}
