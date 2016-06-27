package com.sohungry.search.google;

import java.util.List;

import com.sohungry.search.google.params.ParamBuilder;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;

public class GooglePlacesSearchExecutor {
	
	private String apiKey;
	
	public GooglePlacesSearchExecutor(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public List<Place> execute(ParamBuilder paramBuilder) {
		GooglePlaces client = new GooglePlaces(apiKey);
		List<Place> places = client.getPlacesByQuery(paramBuilder.getKeyword(), paramBuilder.getLimit(), paramBuilder.buildParams());
		return places;
	}

}
