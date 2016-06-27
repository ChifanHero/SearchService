package com.sohungry.search.task;

import java.util.List;

import com.sohungry.search.internal.representation.RestaurantInternalSearchResponse;

import github.familysyan.concurrent.tasks.Task;

public class NativeSearchTask implements Task<RestaurantInternalSearchResponse>{

	@Override
	public RestaurantInternalSearchResponse execute(List<Object> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUniqueTaskId() {
		return "native_search_task";
	}

}
