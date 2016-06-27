package com.sohungry.search.internal.representation;

import java.util.List;

import com.sohungry.search.model.Source;

public class RestaurantInternalSearchResponse {
	
	private long total;
	private List<RestaurantInternal> results;
	private Source source;
	
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List<RestaurantInternal> getResults() {
		return results;
	}
	public void setResults(List<RestaurantInternal> results) {
		this.results = results;
	}
	public Source getSource() {
		return source;
	}
	public void setSource(Source source) {
		this.source = source;
	}

}
