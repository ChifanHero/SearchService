package com.sohungry.search.domain.context.builder;

import com.sohungry.search.domain.context.ImmutableApplicationContext;
import com.sohungry.search.domain.context.ImmutableRestaurantRequestContext;
import com.sohungry.search.model.RestaurantSearchRequest;

public class RestaurantRequestContextBuilder {
	
	private RestaurantSearchRequest request;
	private ImmutableApplicationContext appContext;
	
	public RestaurantRequestContextBuilder(RestaurantSearchRequest request, ImmutableApplicationContext applicationContext) {
		this.request = request;
		this.appContext = applicationContext;
	}
	
	public ImmutableRestaurantRequestContext build() {
		ImmutableRestaurantRequestContext requestContext = new ImmutableRestaurantRequestContext();
		requestContext.setAppContext(appContext);
		return null;
	}

}
