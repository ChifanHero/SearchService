package com.sohungry.search.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class DishListSearchResponse {
	
	private List<DishList> results;
	private Error error;
	public List<DishList> getResults() {
		return results;
	}
	public void setResults(List<DishList> results) {
		this.results = results;
	}
	public Error getError() {
		return error;
	}
	public void setError(Error error) {
		this.error = error;
	}

}
