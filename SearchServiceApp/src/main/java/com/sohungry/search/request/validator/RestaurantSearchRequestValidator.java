package com.sohungry.search.request.validator;

import com.sohungry.search.model.Range;
import com.sohungry.search.model.RestaurantSearchRequest;
import com.sohungry.search.model.SortBy;
import com.sohungry.search.model.Error;
import com.sohungry.search.model.ErrorSeverity;

public class RestaurantSearchRequestValidator {
	
	private Error error;
	private RestaurantSearchRequest searchRequest;
	
	public RestaurantSearchRequestValidator(RestaurantSearchRequest searchRequest) {
		this.searchRequest = searchRequest;
	}
	
	public boolean validate() {
		if (searchRequest.getKeyword() == null && !hasValidRange()) {
			error.setSeverity(ErrorSeverity.error);
			error.setMessage("keyword or valid range must be provided. A valid range must have center defined.");
			return false;
		}
		if (searchRequest.getSortBy() != null && searchRequest.getSortBy() == SortBy.distance && searchRequest.getUserLocation() == null) {
			error.setSeverity(ErrorSeverity.error);
			error.setMessage("To sort by distance, user location must be provided");
			return false;
		}
		return true;
	}
	
	public Error getError() {
		return error;
	}
	
	private boolean hasValidRange() {
		if (searchRequest.getRange() != null) {
			Range range = searchRequest.getRange();
			if (range.getCenter() == null) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
		
	}

}
