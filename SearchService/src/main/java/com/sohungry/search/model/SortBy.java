package com.sohungry.search.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public enum SortBy {
	
	@JsonProperty("hotness")
	HOTNESS,
	
	@JsonProperty("distance")
	DISTANCE,
	
	@JsonProperty("relevance")
	RELEVANCE

}
