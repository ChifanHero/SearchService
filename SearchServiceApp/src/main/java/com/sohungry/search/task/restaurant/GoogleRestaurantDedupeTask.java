package com.sohungry.search.task.restaurant;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.sohungry.search.distance.HaversineDistanceCalculator;
import com.sohungry.search.internal.representation.Coordinates;
import com.sohungry.search.internal.representation.RestaurantInternal;
import com.sohungry.search.internal.representation.RestaurantInternalSearchResponse;
import com.sohungry.search.model.Source;
import com.sohungry.search.util.StringUtil;

import github.familysyan.concurrent.tasks.Task;

public class GoogleRestaurantDedupeTask implements Task<List<RestaurantInternalSearchResponse>>{

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
		Iterator<RestaurantInternal> iterator = googleResults.iterator();
		while (iterator.hasNext()) {
			RestaurantInternal restaurant = iterator.next();
			for (RestaurantInternal nativeResult : nativeResults) {
				String name1 = nativeResult.getName() != null? nativeResult.getName() : nativeResult.getEnglishName();
				String name2 = restaurant.getName() != null? restaurant.getName() : restaurant.getEnglishName();
				if (isCloseEnough(nativeResult.getCoordinates(), restaurant.getCoordinates()) && isNameSimilarEnough(name1, name2)) {
					iterator.remove();
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
		return HaversineDistanceCalculator.getDistanceInKm(coordinates1.getLat(), coordinates1.getLon(), coordinates2.getLat(), coordinates2.getLon()) <= 0.01;
	}
	
	private boolean isNameSimilarEnough(String name1, String name2) {
		return StringUtil.getRelevanceScore(name1, name2) >= 0.5;
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
