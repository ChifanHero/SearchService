package com.sohungry.search.task;

import java.util.List;

import com.sohungry.search.internal.representation.RestaurantInternalSearchResponse;

import github.familysyan.concurrent.tasks.Task;

public class GoogleSearchTask implements Task<RestaurantInternalSearchResponse>{

	@Override
	public RestaurantInternalSearchResponse execute(List<Object> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUniqueTaskId() {
		return "google_search_task";
	}

}
