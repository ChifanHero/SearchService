package com.sohungry.search.converter;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.JsonObject;
import com.sohungry.search.distance.Coordinates;
import com.sohungry.search.distance.HaversineDistanceCalculator;
import com.sohungry.search.model.Distance;
import com.sohungry.search.model.DistanceUnit;
import com.sohungry.search.model.Location;
import com.sohungry.search.model.Picture;
import com.sohungry.search.model.Restaurant;
import com.sohungry.search.model.RestaurantField;

public class RestaurantConverter implements Converter<Restaurant>{
	
	private List<String> fields;
	private Location userLocation;
	private DistanceUnit distanceUnit;
	
	public RestaurantConverter(List<String> fields) {
		this(fields, null, null);
	}
	
	public RestaurantConverter(List<String> fields, Location userLocation, DistanceUnit distanceUnit) {
		this.fields = fields;
		this.userLocation = userLocation;
		this.distanceUnit = distanceUnit;
	}

	@Override
	public Restaurant convert(JsonObject source) {
		if (source == null || !source.isJsonObject() || fields == null || fields.isEmpty()) {
			return null;
		}
		boolean returnAll = false;
		if (fields == null) {
			returnAll = true;
		} else if (fields.isEmpty()) {
			return new Restaurant();
		} 
		Restaurant restaurant = new Restaurant();
		if (returnAll || fields.contains(RestaurantField.address.name())) {
			if (source.get("address") != null && !source.get("address").isJsonNull()) {
				restaurant.setAddress(source.get("address").getAsString());
			}
		}
		
		if (returnAll || fields.contains(RestaurantField.dislike_count.name())) {
			if (source.get("dislike_count") != null && !source.get("dislike_count").isJsonNull()) {
				restaurant.setDislikeCount(source.get("dislike_count").getAsLong());
			}
		}
		
		if (returnAll || fields.contains(RestaurantField.distance.name())) {
			if (source.get("coordinates") != null && !source.get("coordinates").isJsonNull()) {
				restaurant.setDistance(getDistance(source.get("coordinates").getAsJsonObject()));
			}
		}
		
		if (returnAll || fields.contains(RestaurantField.english_name.name())) {
			if (source.get("english_name") != null && !source.get("english_name").isJsonNull()) {
				restaurant.setEnglishName(source.get("english_name").getAsString());
			}
		}
		
		if (returnAll || fields.contains(RestaurantField.favorite_count.name())) {
			if (source.get("favorite_count") != null && !source.get("favorite_count").isJsonNull()) {
				restaurant.setFavoriteCount(source.get("favorite_count").getAsLong());
			}
		}
		
		if (returnAll || fields.contains(RestaurantField.id.name())) {
			if (source.get("objectId") != null && !source.get("objectId").isJsonNull()) {
				restaurant.setId(source.get("objectId").getAsString());
			}
		}
		
		if (returnAll || fields.contains(RestaurantField.like_count.name())) {
			if (source.get("like_count") != null && !source.get("like_count").isJsonNull()) {
				restaurant.setLikeCount(source.get("like_count").getAsLong());
			}
		}
		
		if (returnAll || fields.contains(RestaurantField.name.name())) {
			if (source.get("name") != null && !source.get("name").isJsonNull()) {
				restaurant.setName(source.get("name").getAsString());
			}
		}
		
		if (returnAll || fields.contains(RestaurantField.neutral_count.name())) {
			if (source.get("neutral_count") != null && !source.get("neutral_count").isJsonNull()) {
				restaurant.setNeutralCount(source.get("neutral_count").getAsLong());
			}
		}
		
		if (returnAll || fields.contains(RestaurantField.phone.name())) {
			if (source.get("phone") != null && !source.get("phone").isJsonNull()) {
				restaurant.setPhone(source.get("phone").getAsString());
				
			}
		}
		
		if (returnAll || fields.contains(RestaurantField.picture.name())) {
			if (source.get("picture") != null && !source.get("picture").isJsonNull()) {
				JsonObject pic = source.get("picture").getAsJsonObject();
				if (pic != null) {
					Picture picture = new Picture();
					picture.setOriginal(pic.get("original").getAsString());
					picture.setThumbnail(pic.get("thumbnail").getAsString());
					restaurant.setPicture(picture);
				}
			}
		}
		
		return restaurant;	
	}
	
	private Distance getDistance(JsonObject coordinates) {
		if (coordinates == null || !coordinates.isJsonObject() || coordinates.get("lat") == null || coordinates.get("lon") == null) 
			return null;
		if (userLocation != null) {
			Coordinates pos1 = new Coordinates();
			pos1.setLat(userLocation.getLat());
			pos1.setLon(userLocation.getLon());
			Coordinates pos2 = new Coordinates();
			pos2.setLat(coordinates.get("lat").getAsDouble());
			pos2.setLon(coordinates.get("lon").getAsDouble());
			Double value = null;
			if (distanceUnit == DistanceUnit.mi) {
				value = HaversineDistanceCalculator.getDistanceInMi(pos1, pos2);
			} else {
				value = HaversineDistanceCalculator.getDistanceInKm(pos1, pos2);
			}
			if (value != null) {
				Distance distance = new Distance();
				distance.setValue(getScale2DoubleValue(value));
				distance.setUnit(distanceUnit);
				return distance;
			}
		}
		return null;
	}
	
	private Double getScale2DoubleValue(Double original) {
		if (original == null) return null;
		Double toBeTruncated = new Double(original);
		Double truncatedDouble=new BigDecimal(toBeTruncated ).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		return truncatedDouble;
	}

	

}
