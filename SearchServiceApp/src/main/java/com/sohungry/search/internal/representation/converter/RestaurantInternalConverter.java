package com.sohungry.search.internal.representation.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sohungry.search.distance.HaversineDistanceCalculator;
import com.sohungry.search.domain.context.RestaurantRequestContext;
import com.sohungry.search.internal.representation.Coordinates;
import com.sohungry.search.internal.representation.RestaurantInternal;
import com.sohungry.search.model.Distance;
import com.sohungry.search.model.DistanceUnit;
import com.sohungry.search.model.Location;
import com.sohungry.search.model.Restaurant;
import com.sohungry.search.model.RestaurantField;
import com.sohungry.search.util.StringUtil;

public class RestaurantInternalConverter {
	
	private RestaurantRequestContext requestContext;
	private boolean returnAllFields;
	private List<String> fields;
	private boolean debugMode;
	private String language;
	
	public RestaurantInternalConverter(RestaurantRequestContext requestContext) {
		this.requestContext = requestContext;
		this.returnAllFields = requestContext.isReturnAllFields();
		this.fields = requestContext.getFields();
		this.debugMode = requestContext.getAppContext().isDebugMode();
		this.language = requestContext.getAppContext().getLanguage();
	}
	
	public Restaurant convert(RestaurantInternal internal) {
		Restaurant restaurant = new Restaurant();
		if (returnAllFields || fields.contains(RestaurantField.address.name())) {
			restaurant.setAddress(internal.getAddress());
		}
		if (debugMode) {
			Map<String, List<String>> diagInfo = new HashMap<>();
			List<String> values = new ArrayList<>();
			values.add(String.valueOf(internal.getScore()));
			diagInfo.put("relevanceScore", values);
			restaurant.setDiagInfo(diagInfo);
		}
		if (returnAllFields || fields.contains(RestaurantField.dishes.name())) {
			restaurant.setDishes(internal.getDishes());
			restaurant.setDishes(getDishes(internal.getDishes()));
		}
		if (returnAllFields || fields.contains(RestaurantField.dislike_count.name())) {
			restaurant.setDislikeCount(internal.getDislikeCount());
		}
		if (returnAllFields || fields.contains(RestaurantField.distance.name()) && requestContext.getUserLocation() != null) {
			restaurant.setDistance(getDistance(requestContext.getUserLocation(), internal.getCoordinates(), requestContext.getDistanceUnit()));
		}
		if (returnAllFields || fields.contains(RestaurantField.favorite_count.name())) {
			restaurant.setFavoriteCount(internal.getFavoriteCount());
		}
		if (returnAllFields || fields.contains(RestaurantField.rating.name())) {
			restaurant.setRating(internal.getRating());
		}
		if (returnAllFields || fields.contains(RestaurantField.id.name())) {
			restaurant.setId(internal.getId());
		}
		if (returnAllFields || fields.contains(RestaurantField.like_count.name())) {
			restaurant.setLikeCount(internal.getLikeCount());
		}
		if (returnAllFields || fields.contains(RestaurantField.name.name())) {
			if ("en".equals(language)) {
				restaurant.setName(internal.getEnglishName());
			} else {
				restaurant.setName(internal.getName());
			}
		}
		if (returnAllFields || fields.contains(RestaurantField.neutral_count.name())) {
			restaurant.setNeutralCount(internal.getNeutralCount());
		}
		if (returnAllFields || fields.contains(RestaurantField.phone.name())) {
			restaurant.setPhone(internal.getPhone());
		}
		if (returnAllFields || fields.contains(RestaurantField.picture.name())) {
			restaurant.setPicture(internal.getPicture());
		}
		return restaurant;
	}

	private List<String> getDishes(List<String> dishes) {
		if (dishes == null || dishes.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> results = new ArrayList<String>();
		for (String dish : dishes) {
			if ("en".equals(language)) {
				if (!StringUtil.containsHanScript(dish)) {
					if (results.size() < 20) {
						results.add(dish);
					}
				}
			} else if ("zh".equals(language)) {
				if (StringUtil.containsHanScript(dish)) {
					if (results.size() < 20) {
						results.add(dish);
					}
				}
			}
		}
		return results;
		
	}

	private Distance getDistance(Location userLocation, Coordinates coordinates, DistanceUnit distanceUnit) {
		if (userLocation != null && coordinates != null) {
			com.sohungry.search.distance.Coordinates pos1 = new com.sohungry.search.distance.Coordinates();
			pos1.setLat(userLocation.getLat());
			pos1.setLon(userLocation.getLon());
			com.sohungry.search.distance.Coordinates pos2 = new com.sohungry.search.distance.Coordinates();
			pos2.setLat(coordinates.getLat());
			pos2.setLon(coordinates.getLon());
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
