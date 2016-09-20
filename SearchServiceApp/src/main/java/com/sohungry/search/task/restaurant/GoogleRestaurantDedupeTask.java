package com.sohungry.search.task.restaurant;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.sohungry.search.distance.HaversineDistanceCalculator;
import com.sohungry.search.domain.context.RestaurantRequestContext;
import com.sohungry.search.internal.representation.Coordinates;
import com.sohungry.search.internal.representation.RestaurantInternal;
import com.sohungry.search.internal.representation.RestaurantInternalSearchResponse;
import com.sohungry.search.model.Distance;
import com.sohungry.search.model.DistanceUnit;
import com.sohungry.search.model.Location;
import com.sohungry.search.model.Source;
import com.sohungry.search.util.StringUtil;

import github.familysyan.concurrent.tasks.Task;

public class GoogleRestaurantDedupeTask implements Task<List<RestaurantInternalSearchResponse>>{

	private RestaurantRequestContext requestContext;
	
	public GoogleRestaurantDedupeTask(RestaurantRequestContext requestContext) {
		this.requestContext = requestContext;
	}
	
	@Override
	public List<RestaurantInternalSearchResponse> execute(List<Object> dependencies) {
		if (dependencies == null || dependencies.size() != 2) {
			return Collections.emptyList();
		}
		RestaurantInternalSearchResponse nativeResponse = null;
		RestaurantInternalSearchResponse googleResponse = null;
		for (Object response : dependencies) {
			if (response instanceof RestaurantInternalSearchResponse) {
				RestaurantInternalSearchResponse converted = (RestaurantInternalSearchResponse)response;
				if (converted.getSource() == Source.self) {
					nativeResponse = converted;
				} else if (converted.getSource() == Source.google) {
					googleResponse = converted;
				}
			}
		}
		if (nativeResponse == null && googleResponse != null) {
			return Arrays.asList(new RestaurantInternalSearchResponse[]{googleResponse});
		} else if (googleResponse == null && nativeResponse != null) {
			return Arrays.asList(new RestaurantInternalSearchResponse[]{nativeResponse});
		} else if (nativeResponse != null && googleResponse != null) {
			dedupeGoogleResponse(nativeResponse, googleResponse);
			return Arrays.asList(new RestaurantInternalSearchResponse[]{nativeResponse, googleResponse});
		} else {
			return Collections.emptyList();
		}
		
		
	}

	private void dedupeGoogleResponse(RestaurantInternalSearchResponse nativeResponse,
			RestaurantInternalSearchResponse googleResponse) {
		List<RestaurantInternal> nativeResults = nativeResponse.getResults();
		List<RestaurantInternal> googleResults = googleResponse.getResults();
		if (googleResults == null || googleResults.isEmpty()) {
			return;
		}
		Iterator<RestaurantInternal> iterator = googleResults.iterator();
		while (iterator.hasNext()) {
			RestaurantInternal restaurant = iterator.next();
			if (!isWithinRange(restaurant.getCoordinates())) {
				iterator.remove();
				continue;
			}
			if (nativeResults == null || nativeResults.isEmpty()) {
				continue;
			}
			for (RestaurantInternal nativeResult : nativeResults) {
				String name1 = nativeResult.getName() != null? nativeResult.getName() : nativeResult.getEnglishName();
				String englishName1 = nativeResult.getEnglishName();
				String name2 = restaurant.getName() != null? restaurant.getName() : restaurant.getEnglishName();
				if (isCloseEnough(nativeResult.getCoordinates(), restaurant.getCoordinates()) && (isNameSimilarEnough(name1, name2) || isNameSimilarEnough(englishName1, name2))) {
					iterator.remove();
					break;
				}
			}
		}
//		Set<String> nativeResultsIds = new HashSet<String>();
//		Set<String> nativeResultsNames = new HashSet<String>();
//		if (nativeResults != null) {
//			for (RestaurantInternal nativeResult : nativeResults) {
//				String id = getCoordinateIdentifier(nativeResult);
//				if (id != null) {
//					nativeResultsIds.add(id);
//				}
//			}
//		}
//		List<RestaurantInternal> googleResults = googleResponse.getResults();
//		if (googleResults != null && googleResults.size() > 0) {
//			Iterator<RestaurantInternal> iterator = googleResults.iterator();
//			while(iterator.hasNext()) {
//				RestaurantInternal googleResult = iterator.next();
//				String id = getCoordinateIdentifier(googleResult);
//				if (nativeResultsIds.contains(id)) {
//					iterator.remove();
//				}
//			}
//		}
	}

//	private String getCoordinateIdentifier(RestaurantInternal restaurant) {
//		if (restaurant == null) {
//			return null;
//		}
//		Coordinates coordinates = restaurant.getCoordinates();
//		if (coordinates == null || coordinates.getLat() == 0.0d || coordinates.getLon() == 0.0d) {
//			return null;
//		}
//		Double lat = new Double(coordinates.getLat());
//		Double newLat =new BigDecimal(lat).setScale(3, BigDecimal.ROUND_DOWN).doubleValue();
//		Double lon = new Double(coordinates.getLon());
//		Double newLon =new BigDecimal(lon).setScale(3, BigDecimal.ROUND_DOWN).doubleValue();
//		StringBuilder sb = new StringBuilder();
//		sb.append(newLat);
//		sb.append("|");
//		sb.append(newLon);
//		return sb.toString();
//	}
	
	private boolean isCloseEnough(Coordinates coordinates1, Coordinates coordinates2) {
		Double distance = HaversineDistanceCalculator.getDistanceInKm(coordinates1.getLat(), coordinates1.getLon(), coordinates2.getLat(), coordinates2.getLon());
		return  distance <= 0.10;
	}
	
	private boolean isNameSimilarEnough(String name1, String name2) {
		double score = StringUtil.getRelevanceScore(name1, name2);
		return  score >= 0.5;
	}
	
	private boolean isWithinRange(Coordinates coordinates) {
		if (requestContext == null || requestContext.getRange() == null || requestContext.getRange().getCenter() == null) {
			return true;
		}
		Location center = requestContext.getRange().getCenter();
		Distance distance = requestContext.getRange().getDistance();
		double distanceValue = distance.getValue();
		if (distance.getUnit() == DistanceUnit.mi) {
			distanceValue = distanceValue * 1.6;
		}
		Coordinates centCoordinates = new Coordinates();
		centCoordinates.setLat(center.getLat());
		centCoordinates.setLon(center.getLon());
		Double realDistance = HaversineDistanceCalculator.getDistanceInKm(coordinates.getLat(), coordinates.getLon(), centCoordinates.getLat(), centCoordinates.getLon());
		return realDistance <= distanceValue;
	}

	@Override
	public String getUniqueTaskId() {
		return "GoogleRestaurantDedupeTask";
	}

	@Override
	public void failedToComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

}
