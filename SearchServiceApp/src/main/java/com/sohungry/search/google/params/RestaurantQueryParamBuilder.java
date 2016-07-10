package com.sohungry.search.google.params;

import com.sohungry.search.domain.context.RestaurantRequestContext;
import com.sohungry.search.model.Location;

import se.walkercrou.places.Param;

public class RestaurantQueryParamBuilder implements ParamBuilder{
	
	private String keyword;
	private int limit = 20;
	private Location center;
	private String type = "restaurant";
	private String language;
	
	
	public RestaurantQueryParamBuilder(RestaurantRequestContext requestContext) {
		if (requestContext != null) {
			keyword = requestContext.getKeyword();
			limit = Math.min(20, requestContext.getLimit());
		}
		if (requestContext.getRange() != null) {
			center = requestContext.getRange().getCenter();
		}
		language = requestContext.getAppContext().getLanguage();
		
	}

	@Override
	public Param[] buildParams() {
		Param centerParameter = null;
		Param typeParameter = null;
		Param languageParameter = null;
		if (center != null) {
			centerParameter = new Param(GooglePlacesAPIParameters.LOCATION);
			centerParameter.value(getLocationValue(center));
		}
		if (type != null) {
			typeParameter = new Param(GooglePlacesAPIParameters.TYPE);
			typeParameter.value(type);
		}
		if (language != null) {
			languageParameter = new Param(GooglePlacesAPIParameters.LANGUAGE);
			languageParameter.value(language);
		}
		Param[] params = new Param[]{centerParameter, typeParameter, languageParameter};
		return params;
	}

	private String getLocationValue(Location center) {
		StringBuilder sb = new StringBuilder();
		sb.append(center.getLat());
		sb.append(",");
		sb.append(center.getLon());
		return sb.toString();
	}

	@Override
	public String getKeyword() {
		return keyword;
	}

	@Override
	public int getLimit() {
		return limit;
	}

}
