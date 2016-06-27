package com.sohungry.search.google.converter;

import java.util.ArrayList;
import java.util.List;

import com.sohungry.search.internal.representation.RestaurantInternal;
import com.sohungry.search.internal.representation.RestaurantInternalSearchResponse;
import com.sohungry.search.model.Source;

import se.walkercrou.places.Place;

public class GooglePlacesRestaurantInternalResponseBuilder implements GooglePlacesInternalResponseBuilder<RestaurantInternalSearchResponse> {

	@Override
	public RestaurantInternalSearchResponse build(List<Place> places) {
		RestaurantInternalSearchResponse response = new RestaurantInternalSearchResponse();
		if (places == null || places.isEmpty()) {
			response.setTotal(0);
		} else {
			response.setTotal(places.size());
			List<RestaurantInternal> results = new ArrayList<RestaurantInternal>();
			for (Place place : places) {
				RestaurantInternal restaurant = GooglePlacesRestaurantConverter.convert(place); 
				results.add(restaurant);
			}
			response.setResults(results);
		}
		response.setSource(Source.google);
		return response;
	}

}
