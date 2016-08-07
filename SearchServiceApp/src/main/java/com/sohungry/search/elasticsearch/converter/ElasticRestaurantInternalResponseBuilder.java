package com.sohungry.search.elasticsearch.converter;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sohungry.search.internal.representation.RestaurantInternal;
import com.sohungry.search.internal.representation.RestaurantInternalSearchResponse;
import com.sohungry.search.model.Source;

import io.searchbox.core.SearchResult;

public class ElasticRestaurantInternalResponseBuilder implements ElasticInternalResponseBuilder<RestaurantInternalSearchResponse>{
	
	@Override
	public RestaurantInternalSearchResponse build(SearchResult result) {
		if (result != null && result.getJsonObject() != null && result.getJsonObject().getAsJsonObject("hits") != null) {
			RestaurantInternalSearchResponse response = new RestaurantInternalSearchResponse();
			response.setTotal(result.getTotal());
			JsonArray hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
			if (hits != null && hits.size() > 0) {
				List<RestaurantInternal> results = new ArrayList<RestaurantInternal>();
				for (int i = 0; i < hits.size(); i++) {
					JsonObject hit = hits.get(i).getAsJsonObject();
					if (hit == null || !hit.isJsonObject()) {
						continue;
					}
					
					if (hit.get("_source") == null) {
						continue;
					}
					JsonObject source = hit.get("_source").getAsJsonObject();
					RestaurantInternal restaurant = ElasticRestaurantConverter.convert(source);	
					if (hit.get("_score") != null && !hit.get("_score").isJsonNull()) {
						double score = hit.get("_score").getAsDouble();
						restaurant.setScore(score);
					}
					results.add(restaurant);
				}
				response.setResults(results);
			}
			response.setSource(Source.self);
			return response;
		} 
		return null;
	}

}
