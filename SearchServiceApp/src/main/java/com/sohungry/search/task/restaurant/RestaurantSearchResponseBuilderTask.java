package com.sohungry.search.task.restaurant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.sohungry.search.domain.context.RestaurantRequestContext;
import com.sohungry.search.internal.representation.RestaurantInternal;
import com.sohungry.search.internal.representation.RestaurantInternalSearchResponse;
import com.sohungry.search.internal.representation.converter.RestaurantInternalConverter;
import com.sohungry.search.model.Bucket;
import com.sohungry.search.model.RestaurantSearchResponse;
import com.sohungry.search.model.Result;
import com.sohungry.search.model.Source;

import github.familysyan.concurrent.tasks.Task;

public class RestaurantSearchResponseBuilderTask implements Task<RestaurantSearchResponse>{
	
	private RestaurantRequestContext requestContext;
	private final static double SCORE_THRESHOLD = 1.0;
	
	public RestaurantSearchResponseBuilderTask(RestaurantRequestContext requestContext) {
		this.requestContext = requestContext;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RestaurantSearchResponse execute(List<Object> dependencies) {
		RestaurantSearchResponse response = new RestaurantSearchResponse();
		if (dependencies == null) {
			return response;
		}
		RestaurantInternalSearchResponse nativeResponse = null;
		RestaurantInternalSearchResponse googleResponse = null;
		if (dependencies.size() == 1 && dependencies.get(0) instanceof List) {
			List<Object> responses = (List<Object>) dependencies.get(0);
			for (Object dependency : responses) {
				if (dependency instanceof RestaurantInternalSearchResponse) {
					RestaurantInternalSearchResponse converted = (RestaurantInternalSearchResponse)dependency;
					if (converted.getSource() == Source.self) {
						nativeResponse = converted;
					} else if (converted.getSource() == Source.google) {
						googleResponse = converted;
					}
				}
			}
		} else if (dependencies.size() == 1 && dependencies.get(0) instanceof RestaurantInternalSearchResponse) {
			nativeResponse = (RestaurantInternalSearchResponse)dependencies.get(0);
		}
		List<Bucket> buckets = new LinkedList<Bucket>();
		response.setBuckets(buckets);
		boolean needSeperateBucket = false;
		if (googleResponse != null && googleResponse.getResults() != null) {
			Bucket googleBucket = createBucket(googleResponse.getResults());
			if (googleBucket != null) {
				needSeperateBucket = true;
				googleBucket.setLabel("以下为google搜索结果");
				buckets.add(googleBucket);
				googleBucket.setSource(Source.google);
			}
		}
		if (needSeperateBucket) {
			List<RestaurantInternal> topResults = new ArrayList<RestaurantInternal>();
			List<RestaurantInternal> otherResults = new ArrayList<RestaurantInternal>(); 
			if (nativeResponse != null && nativeResponse.getResults() != null) {
				for (RestaurantInternal internal : nativeResponse.getResults()) {
					if (internal.getScore() >= SCORE_THRESHOLD) {
						topResults.add(internal);
					} else {
						otherResults.add(internal);
					}
				}
			}
			if (topResults.size() > 0) {
				Bucket topBucket = createBucket(topResults);
				if (topBucket != null) {
					topBucket.setLabel(null);
					buckets.add(0, topBucket);
				}
				topBucket.setSource(Source.self);
			}
			
			if (otherResults.size() > 0) {
				Bucket otherBucket = createBucket(otherResults);
				if (otherBucket != null) {
					otherBucket.setLabel("更多搜索结果");
					buckets.add(otherBucket);
					otherBucket.setSource(Source.self);
				}
				
			}
		} else {
			if (nativeResponse != null && nativeResponse.getResults() != null) {
				Bucket resultsBucket = createBucket(nativeResponse.getResults());
				if (resultsBucket != null) {
					resultsBucket.setLabel(null);
					buckets.add(resultsBucket);
				}
			}
			
		}
		
		return response;
	}

	private Bucket createBucket(List<RestaurantInternal> results) {
		if (results == null || results.size() == 0) {
			return null;
		}
		Bucket bucket = new Bucket();
		List<Result> restaurants = new ArrayList<Result>();
		for (RestaurantInternal internal : results) {
			restaurants.add(new RestaurantInternalConverter(requestContext).convert(internal));
		}
		bucket.setResults(restaurants);
		return bucket;
	}

	@Override
	public String getUniqueTaskId() {
		return this.getClass().getName();
	}

	@Override
	public void failedToComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

}
