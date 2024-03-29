package com.sohungry.search.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class RestaurantSearchRequest {
	
	private String keyword;
	private Integer offset;
	private Integer limit;
	
	@JsonProperty("sort_by")
	private SortBy sortBy;
	
	@JsonProperty("sort_order")
	private SortOrder sortOrder;
	private TuningParams parameters;
	private Output output;
	
	@JsonProperty("user_location")
	private Location userLocation;
	private Range range;
	
	private Filters filters;
	
	@JsonProperty("highlight_in_field")
	private boolean highlightInField;
	
	private Source source;
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public SortBy getSortBy() {
		return sortBy;
	}
	public void setSortBy(SortBy sortBy) {
		this.sortBy = sortBy;
	}
	public SortOrder getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}
	public TuningParams getParameters() {
		return parameters;
	}
	public void setParameters(TuningParams parameters) {
		this.parameters = parameters;
	}
	public Output getOutput() {
		return output;
	}
	public void setOutput(Output output) {
		this.output = output;
	}
	public Location getUserLocation() {
		return userLocation;
	}
	public void setUserLocation(Location userLocation) {
		this.userLocation = userLocation;
	}
	public Range getRange() {
		return range;
	}
	public void setRange(Range range) {
		this.range = range;
	}
	public Filters getFilters() {
		return filters;
	}
	public void setFilters(Filters filters) {
		this.filters = filters;
	}
	public boolean isHighlightInField() {
		return highlightInField;
	}
	public void setHighlightInField(boolean highlight) {
		this.highlightInField = highlight;
	}
	public Source getSource() {
		return source;
	}
	public void setSource(Source source) {
		this.source = source;
	}

}
