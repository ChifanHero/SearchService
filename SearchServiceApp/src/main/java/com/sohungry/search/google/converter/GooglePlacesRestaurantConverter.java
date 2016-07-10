package com.sohungry.search.google.converter;

import com.sohungry.search.internal.representation.Coordinates;
import com.sohungry.search.internal.representation.RestaurantInternal;
import com.sohungry.search.model.Source;

import se.walkercrou.places.Place;

public class GooglePlacesRestaurantConverter{

	public static RestaurantInternal convert(Place place) {
		if (place == null) {
			return null;
		}
		RestaurantInternal restaurant = new RestaurantInternal();
		restaurant.setAddress(place.getAddress());
		restaurant.setId(place.getPlaceId());
		restaurant.setName(place.getName());
		restaurant.setPhone(place.getPhoneNumber());
		restaurant.setSource(Source.google);
		Coordinates coordinates = new Coordinates();
		coordinates.setLat(place.getLatitude());
		coordinates.setLon(place.getLongitude());
		restaurant.setCoordinates(coordinates);
		return restaurant;
	}

}
