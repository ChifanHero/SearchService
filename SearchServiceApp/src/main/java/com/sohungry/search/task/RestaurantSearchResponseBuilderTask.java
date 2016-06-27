package com.sohungry.search.task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sohungry.search.internal.representation.RestaurantInternal;
import com.sohungry.search.internal.representation.RestaurantInternalSearchResponse;
import com.sohungry.search.internal.representation.converter.RestaurantInternalConverter;
import com.sohungry.search.model.Bucket;
import com.sohungry.search.model.RestaurantSearchResponse;
import com.sohungry.search.model.Result;
import com.sohungry.search.model.Source;

import github.familysyan.concurrent.tasks.Task;

public class RestaurantSearchResponseBuilderTask implements Task<RestaurantSearchResponse>{

	@Override
	public RestaurantSearchResponse execute(List<Object> dependencies) {
		RestaurantSearchResponse response = new RestaurantSearchResponse();
		if (dependencies == null) {
			return response;
		}
		RestaurantInternalSearchResponse nativeResponse = null;
		RestaurantInternalSearchResponse googleResponse = null;
		for (Object dependency : dependencies) {
			if (dependency instanceof RestaurantInternalSearchResponse) {
				RestaurantInternalSearchResponse converted = (RestaurantInternalSearchResponse)dependency;
				if (converted.getSource() == Source.self) {
					nativeResponse = converted;
				} else if (converted.getSource() == Source.google) {
					googleResponse = converted;
				}
			}
		}
		Map<String, Bucket> buckets = new LinkedHashMap<String, Bucket>();
		response.setBuckets(buckets);
		if (nativeResponse != null) {
			fillResponseWithNativeSearchResults(response, nativeResponse);
		}
		if (googleResponse != null) {
			fillResponseWithGoogleSearchResults(response, googleResponse);
		}
		markPosition(response.getBuckets());
		return response;
	}

	private void markPosition(Map<String, Bucket> buckets) {
		int position = 0;
		for (Map.Entry<String, Bucket> entry : buckets.entrySet()) {
			Bucket bucket = entry.getValue();
			if (bucket != null) {
				bucket.setPosition(position);
				position++;
			}
		}
	}

	private void fillResponseWithGoogleSearchResults(RestaurantSearchResponse response,
			RestaurantInternalSearchResponse googleResponse) {
		if (response == null || googleResponse == null) {
			return;
		}
		Bucket bucket = new Bucket();
		bucket.setTotal(googleResponse.getTotal());
		List<Result> restaurants = new ArrayList<Result>();
		for (RestaurantInternal internal : googleResponse.getResults()) {
			restaurants.add(RestaurantInternalConverter.convert(internal));
		}
		bucket.setResults(restaurants);
		response.getBuckets().put(Source.google.name(), bucket);
		
	}

	private void fillResponseWithNativeSearchResults(RestaurantSearchResponse response,
			RestaurantInternalSearchResponse nativeResponse) {
		if (response == null || nativeResponse == null) {
			return;
		}
		Bucket bucket = new Bucket();
		bucket.setTotal(nativeResponse.getTotal());
		List<Result> restaurants = new ArrayList<Result>();
		for (RestaurantInternal internal : nativeResponse.getResults()) {
			restaurants.add(RestaurantInternalConverter.convert(internal));
		}
		bucket.setResults(restaurants);
		response.getBuckets().put(Source.self.name(), bucket);
	}

	@Override
	public String getUniqueTaskId() {
		return this.getClass().getName();
	}

}
