package com.sohungry.search.domain.context.builder;

import com.sohungry.search.domain.context.ApplicationContext;
import com.sohungry.search.domain.context.RestaurantRequestContext;
import com.sohungry.search.model.Distance;
import com.sohungry.search.model.DistanceUnit;
import com.sohungry.search.model.Location;
import com.sohungry.search.model.Range;
import com.sohungry.search.model.RestaurantSearchRequest;
import com.sohungry.search.model.SortBy;
import com.sohungry.search.model.SortOrder;
import com.sohungry.search.model.Source;

public class RestaurantRequestContextBuilder {
	
	private RestaurantSearchRequest request;
	private ApplicationContext appContext;
	
	public RestaurantRequestContextBuilder(RestaurantSearchRequest request, ApplicationContext applicationContext) {
		this.request = request;
		this.appContext = applicationContext;
	}
	
	public RestaurantRequestContext build() {
		RestaurantRequestContext requestContext = new RestaurantRequestContext();
		requestContext.setAppContext(appContext);
		setDistanceUnit(requestContext);
		setFields(requestContext);
		setHighlightInField(requestContext);
		setKeyword(requestContext);
		setLimit(requestContext);
		setOffset(requestContext);
		setRange(requestContext);
		setRelevanceScoreThreshold(requestContext);
		setSortBy(requestContext);
		setSortOrder(requestContext);
		setUserLocation(requestContext);
		setSource(requestContext);
		return requestContext;
	}

	private void setSource(RestaurantRequestContext requestContext) {
		Source source = request.getSource();
		if (source != null) {
			requestContext.setSource(source);
		} else {
			requestContext.setSource(Source.all);
		}
	}

	private void setUserLocation(RestaurantRequestContext requestContext) {
		Location location = request.getUserLocation();
		if (location != null) {
			requestContext.setUserLocation(location);
		} 
	}

	private void setSortOrder(RestaurantRequestContext requestContext) {
		SortOrder sortOrder = request.getSortOrder();
		if (sortOrder != null) {
			requestContext.setSortOrder(sortOrder);
		} else {
			requestContext.setSortOrder(SortOrder.descend);
		}
	}

	private void setSortBy(RestaurantRequestContext requestContext) {
		SortBy sortBy = request.getSortBy();
		if (sortBy != null) {
			requestContext.setSortBy(sortBy);
		} else {
			requestContext.setSortBy(SortBy.relevance);
		}
	}

	private void setRelevanceScoreThreshold(RestaurantRequestContext requestContext) {
		float threshold = requestContext.getRelevanceScoreThreshold();
		if (threshold > 0) {
			requestContext.setRelevanceScoreThreshold(threshold);
		} else {
			requestContext.setRelevanceScoreThreshold(0.2f);
		}
		
	}

	private void setRange(RestaurantRequestContext requestContext) {
		Range range = request.getRange();
		if (range != null && range.getCenter() != null) {
			if (range.getDistance() == null) {
				Distance distance = new Distance();
				distance.setUnit(DistanceUnit.mi);
				distance.setValue(50);
				range.setDistance(distance);
			} else {
				if (range.getDistance().getUnit() == null) {
					range.getDistance().setUnit(DistanceUnit.mi);
				}
				if (range.getDistance().getValue() <= 0) {
					range.getDistance().setValue(50);
				}
			}
			requestContext.setRange(range);
		} 
	}

	private void setOffset(RestaurantRequestContext requestContext) {
		Integer offset = request.getOffset();
		if (offset != null && offset >= 0) {
			requestContext.setOffset(offset);
		} else {
			requestContext.setOffset(0);
		}
	}

	private void setLimit(RestaurantRequestContext requestContext) {
		Integer limit = request.getLimit();
		if (limit != null && limit >= 0) {
			requestContext.setLimit(limit);
		} else {
			requestContext.setLimit(50);
		}
		
	}

	private void setKeyword(RestaurantRequestContext requestContext) {
		requestContext.setKeyword(request.getKeyword());
	}

	private void setHighlightInField(RestaurantRequestContext requestContext) {
		requestContext.setHighlightInField(request.isHighlightInField());
	}

	private void setFields(RestaurantRequestContext requestContext) {
		if (request.getOutput() != null && request.getOutput().getFields() != null) {
			requestContext.setFields(request.getOutput().getFields());
		} else {
			requestContext.setReturnAllFields(true);
		}
	}

	private void setDistanceUnit(RestaurantRequestContext requestContext) {
		DistanceUnit unit = null;
		if (request.getOutput() != null && request.getOutput().getParams() != null && request.getOutput().getParams().getDistanceUnit() != null) {
			unit = request.getOutput().getParams().getDistanceUnit();
		} else {
			unit = DistanceUnit.mi;
		}
		requestContext.setDistanceUnit(unit);
	}

}
