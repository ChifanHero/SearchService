package com.sohungry.search.elasticsearch.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sohungry.search.internal.representation.Coordinates;
import com.sohungry.search.internal.representation.RestaurantInternal;
import com.sohungry.search.model.Picture;
import com.sohungry.search.model.Source;

public class ElasticRestaurantConverter{


	public static RestaurantInternal convert(JsonObject source) {
		if (source == null || !source.isJsonObject()) {
			return null;
		}

		RestaurantInternal restaurant = new RestaurantInternal();
		if (source.get("address") != null && !source.get("address").isJsonNull()) {
			restaurant.setAddress(source.get("address").getAsString());
		}
		if (source.get("dislike_count") != null && !source.get("dislike_count").isJsonNull()) {
			restaurant.setDislikeCount(source.get("dislike_count").getAsLong());
		}
		if (source.get("coordinates") != null && !source.get("coordinates").isJsonNull()) {
			JsonObject coordinates = source.get("coordinates").getAsJsonObject();
			if (coordinates.get("lat") != null || coordinates.get("lon") != null) {
				Coordinates location = new Coordinates();
				location.setLat(coordinates.get("lat").getAsDouble());
				location.setLon(coordinates.get("lon").getAsDouble());
				restaurant.setCoordinates(location);
			}
		}
		if (source.get("english_name") != null && !source.get("english_name").isJsonNull()) {
			restaurant.setEnglishName(source.get("english_name").getAsString());
		}
		if (source.get("favorite_count") != null && !source.get("favorite_count").isJsonNull()) {
			restaurant.setFavoriteCount(source.get("favorite_count").getAsLong());
		}
		if (source.get("objectId") != null && !source.get("objectId").isJsonNull()) {
			restaurant.setId(source.get("objectId").getAsString());
		}
		if (source.get("like_count") != null && !source.get("like_count").isJsonNull()) {
			restaurant.setLikeCount(source.get("like_count").getAsLong());
		}
		if (source.get("name") != null && !source.get("name").isJsonNull()) {
			restaurant.setName(source.get("name").getAsString());
		}
		if (source.get("neutral_count") != null && !source.get("neutral_count").isJsonNull()) {
			restaurant.setNeutralCount(source.get("neutral_count").getAsLong());
		}
		if (source.get("phone") != null && !source.get("phone").isJsonNull()) {
			restaurant.setPhone(source.get("phone").getAsString());
			
		}
		if (source.get("picture") != null && !source.get("picture").isJsonNull()) {
			JsonObject pic = source.get("picture").getAsJsonObject();
			if (pic != null) {
				Picture picture = new Picture();
				picture.setOriginal(pic.get("original").getAsString());
				picture.setThumbnail(pic.get("thumbnail").getAsString());
				restaurant.setPicture(picture);
			}
		}
		if (source.get("dishes") != null && !source.get("dishes").isJsonNull()) {
			JsonArray array = source.get("dishes").getAsJsonArray();
			List<String> dishes = new ArrayList<String>();
			for (int i = 0; i <array.size(); i++) {
				String dish = array.get(i).getAsString();
				dishes.add(dish);
			}
			restaurant.setDishes(dishes);
		}
		
		if (source.get("highlight") != null && source.get("highlight").getAsJsonObject() != null) {
			JsonObject highlightResult = source.get("highlight").getAsJsonObject();
			Set<Map.Entry<String, JsonElement>> highlightEntries = highlightResult.entrySet();
			if (highlightEntries.size() > 0) {
				Map<String, List<String>> highlight = new HashMap<String, List<String>>();
				for (Map.Entry<String, JsonElement> entry : highlightEntries) {
					JsonArray resultsArray = entry.getValue().getAsJsonArray();
					if (resultsArray != null && resultsArray.size() > 0) {
						List<String> results = new ArrayList<String>();
						Iterator<JsonElement> iterator = resultsArray.iterator();
						while (iterator.hasNext()) {
							String result = iterator.next().getAsString();
							results.add(result);
						}
						highlight.put(entry.getKey(), results);	
					}
				}
				restaurant.setHighlights(highlight);
			}
			
		}
		if (source.get("_source") != null) {
			double score = source.get("_score").getAsDouble();
			restaurant.setScore(score);
		}
		restaurant.setSource(Source.self);
		
		return restaurant;	
	}

}
