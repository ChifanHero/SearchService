package com.sohungry.search.domain.context;

import java.util.List;

import com.sohungry.search.model.DistanceUnit;
import com.sohungry.search.model.Location;
import com.sohungry.search.model.Range;
import com.sohungry.search.model.SortBy;
import com.sohungry.search.model.SortOrder;

public final class ImmutableRestaurantRequestContext extends RequestContext{
	
	private String keyword;
	private Integer offset;
	private Integer limit;
	private SortBy sortBy;
	private SortOrder sortOrder;
	private float relevanceScoreThreshold;
	private boolean returnAllFields;
	private List<String> fields;
	private Location userLocation;
	private DistanceUnit distanceUnit;
	private Range range;
	private boolean highlightInField;
	
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
	public float getRelevanceScoreThreshold() {
		return relevanceScoreThreshold;
	}
	public void setRelevanceScoreThreshold(float relevanceScoreThreshold) {
		this.relevanceScoreThreshold = relevanceScoreThreshold;
	}
	public boolean isReturnAllFields() {
		return returnAllFields;
	}
	public void setReturnAllFields(boolean returnAllFields) {
		this.returnAllFields = returnAllFields;
	}
	public List<String> getFields() {
		return fields;
	}
	public void setFields(List<String> fields) {
		this.fields = fields;
	}
	public Location getUserLocation() {
		return userLocation;
	}
	public void setUserLocation(Location userLocation) {
		this.userLocation = userLocation;
	}
	public DistanceUnit getDistanceUnit() {
		return distanceUnit;
	}
	public void setDistanceUnit(DistanceUnit distanceUnit) {
		this.distanceUnit = distanceUnit;
	}
	public Range getRange() {
		return range;
	}
	public void setRange(Range range) {
		this.range = range;
	}
	public boolean isHighlightInField() {
		return highlightInField;
	}
	public void setHighlightInField(boolean highlightInField) {
		this.highlightInField = highlightInField;
	}


}
