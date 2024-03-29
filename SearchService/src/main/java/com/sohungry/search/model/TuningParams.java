package com.sohungry.search.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class TuningParams {
	
	@JsonProperty("relevance_score_threshold")
	private Float relevanceScoreThreshold;

	public Float getRelevanceScoreThreshold() {
		return relevanceScoreThreshold;
	}

	public void setRelevanceScoreThreshold(Float relevanceScoreThreshold) {
		this.relevanceScoreThreshold = relevanceScoreThreshold;
	}

}
