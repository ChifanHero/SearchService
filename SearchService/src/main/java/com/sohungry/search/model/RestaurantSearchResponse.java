package com.sohungry.search.model;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class RestaurantSearchResponse {
	
	private List<Restaurant> results;
	private Map<String, Bucket> buckets;
	private Error error;

	public List<Restaurant> getResults() {
		return results;
	}

	public void setResults(List<Restaurant> results) {
		this.results = results;
	}

	public Map<String, Bucket> getBuckets() {
		return buckets;
	}

	public void setBuckets(Map<String, Bucket> buckets) {
		this.buckets = buckets;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

}
