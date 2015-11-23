package com.sohungry.search.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class DishSearchResponse {
	
	private List<Dish> results;
	private Error error;
	
	public List<Dish> getResults() {
		return results;
	}
	public void setResults(List<Dish> results) {
		this.results = results;
	}
	public Error getError() {
		return error;
	}
	public void setError(Error error) {
		this.error = error;
	}

}
