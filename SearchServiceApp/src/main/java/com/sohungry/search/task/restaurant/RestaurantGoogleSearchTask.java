package com.sohungry.search.task.restaurant;

import java.util.List;

import com.sohungry.search.domain.context.RestaurantRequestContext;
import com.sohungry.search.google.GooglePlacesSearchExecutor;
import com.sohungry.search.google.converter.GooglePlacesRestaurantInternalResponseBuilder;
import com.sohungry.search.google.params.RestaurantQueryParamBuilder;
import com.sohungry.search.internal.representation.RestaurantInternalSearchResponse;

import github.familysyan.concurrent.tasks.Task;
import se.walkercrou.places.Place;

public class RestaurantGoogleSearchTask implements Task<RestaurantInternalSearchResponse>{
	
	private RestaurantRequestContext requestContext;
	private final static String API_KEY = "AIzaSyBv3gtDERygNxP2lk7fwoPMcNCPfuGZdW0";
	
	public RestaurantGoogleSearchTask(RestaurantRequestContext requestContext) {
		this.requestContext = requestContext;
	}

	@Override
	public RestaurantInternalSearchResponse execute(List<Object> arg0) {
		GooglePlacesSearchExecutor searchExecutor = new GooglePlacesSearchExecutor(API_KEY);
		RestaurantQueryParamBuilder paramBuilder = new RestaurantQueryParamBuilder(requestContext);
		List<Place> places = null;
		try {
			places = searchExecutor.execute(paramBuilder);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		GooglePlacesRestaurantInternalResponseBuilder responseBuilder = new GooglePlacesRestaurantInternalResponseBuilder();
		RestaurantInternalSearchResponse searchResponse = responseBuilder.build(places);
		return searchResponse;
	}

	@Override
	public String getUniqueTaskId() {
		return "google_search_task";
	}

}
