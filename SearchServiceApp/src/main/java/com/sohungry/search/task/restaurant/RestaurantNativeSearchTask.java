package com.sohungry.search.task.restaurant;

import java.util.List;

import com.sohungry.search.domain.context.RestaurantRequestContext;
import com.sohungry.search.elasticsearch.ElasticSearchExecutor;
import com.sohungry.search.elasticsearch.converter.ElasticRestaurantInternalResponseBuilder;
import com.sohungry.search.elasticsearch.query.RestaurantElasticQueryBuilder;
import com.sohungry.search.internal.representation.RestaurantInternalSearchResponse;

import github.familysyan.concurrent.tasks.Task;
import io.searchbox.core.SearchResult;

public class RestaurantNativeSearchTask implements Task<RestaurantInternalSearchResponse>{
	
	private RestaurantRequestContext requestContext;
	
	public RestaurantNativeSearchTask(RestaurantRequestContext requestContext) {
		this.requestContext = requestContext;
	}

	@Override
	public RestaurantInternalSearchResponse execute(List<Object> arg0) {
		ElasticSearchExecutor executor = new ElasticSearchExecutor();
		RestaurantElasticQueryBuilder queryBuilder = new RestaurantElasticQueryBuilder(requestContext);
		SearchResult searchResult = executor.execute(queryBuilder);
		ElasticRestaurantInternalResponseBuilder responseBuilder = new ElasticRestaurantInternalResponseBuilder();
		RestaurantInternalSearchResponse searchResponse = responseBuilder.build(searchResult);
		return searchResponse;
	}

	@Override
	public String getUniqueTaskId() {
		return "native_search_task";
	}

	@Override
	public void failedToComplete() {
		throw new RuntimeException("Native search task timed out");
	}

	@Override
	public long getTimeout() {
		return 2000;
	}

}
