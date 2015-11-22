package com.sohungry.search.finder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.index.query.BaseQueryBuilder;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sohungry.search.distance.Coordinates;
import com.sohungry.search.distance.HaversineDistanceCalculator;
import com.sohungry.search.elastic.factory.ElasticsearchRestClientFactory;
import com.sohungry.search.index.Indices;
import com.sohungry.search.index.Types;
import com.sohungry.search.model.Distance;
import com.sohungry.search.model.DistanceUnit;
import com.sohungry.search.model.Location;
import com.sohungry.search.model.Picture;
import com.sohungry.search.model.Restaurant;
import com.sohungry.search.model.RestaurantField;
import com.sohungry.search.model.RestaurantSearchRequest;
import com.sohungry.search.model.SortBy;
import com.sohungry.search.model.SortOrder;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class RestaurantFinder {
	
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
	
	/**
	 * Use new RestaurantFinder.Builder(searchRequest).build() to construct this class
	 */
	private RestaurantFinder(Builder builder) {
		this.keyword = builder.keyword;
		this.offset = builder.offset;
		this.limit = builder.limit;
		this.sortBy = builder.sortBy;
		this.sortOrder = builder.sortOrder;
		this.relevanceScoreThreshold = builder.relevanceScoreThreshold;
		this.returnAllFields = builder.returnAllFields;
		this.fields = builder.fields;
		this.userLocation = builder.userLocation;
		this.distanceUnit = builder.distanceUnit;
	}
	
	public static class Builder {
		
		private static final int DEFAULT_OFFSET = 0;
		private static final int DEFAULT_LIMIT = 20;
		private static final SortBy DEFAULT_SORTBY = SortBy.hotness;
		private static final SortOrder DEFAULT_SORT_ORDER = SortOrder.decrease;
		private static final float DEFAULT_REL_THRESH = (float) 0.3;
		private static final DistanceUnit DEFAULT_DISTANCE_UNIT = DistanceUnit.mi;
		
		private String keyword;
		private int offset = DEFAULT_OFFSET;
		private int limit = DEFAULT_LIMIT;
		private SortBy sortBy = DEFAULT_SORTBY;
		private SortOrder sortOrder = DEFAULT_SORT_ORDER;
		private float relevanceScoreThreshold = DEFAULT_REL_THRESH;
		private boolean returnAllFields = true;
		private List<String> fields = new ArrayList<String>();
		private Location userLocation;
		private DistanceUnit distanceUnit = DEFAULT_DISTANCE_UNIT;
		
		public Builder(RestaurantSearchRequest searchRequest) {
			if (searchRequest == null) {
				throw new RuntimeException("searchRequest cannot be null");
			}
			this.keyword = searchRequest.getKeyword();
			if (searchRequest.getOffset() != null) {
				this.offset = searchRequest.getOffset();
			}
			if (searchRequest.getLimit() != null) {
				this.limit = searchRequest.getLimit();
			}
			if (searchRequest.getSortBy() != null) {
				this.sortBy = searchRequest.getSortBy();
			}
			if (searchRequest.getSortOrder() != null) {
				this.sortOrder = searchRequest.getSortOrder();
			}
			if (searchRequest.getParameters() != null && searchRequest.getParameters().getRelevanceScoreThreshold() != null) {
				this.relevanceScoreThreshold = searchRequest.getParameters().getRelevanceScoreThreshold();
			}
			if (searchRequest.getOutput() != null && searchRequest.getOutput().getFields() == null) {
				this.returnAllFields = false;
				this.fields = searchRequest.getOutput().getFields();
				cleanFields(fields);
			} 
			this.userLocation = searchRequest.getUserLocation();
			if (searchRequest.getOutput() != null && searchRequest.getOutput().getParams() != null && searchRequest.getOutput().getParams().getDistanceUnit() != null) {
				this.distanceUnit = searchRequest.getOutput().getParams().getDistanceUnit();
			} 
		}
		
		public RestaurantFinder build() {
			return new RestaurantFinder(this);
		}
		
		private void cleanFields(List<String> fields) {
			if (fields == null || fields.size() <= 0) return;
			Iterator<String> iterator = fields.iterator();
			while (iterator.hasNext()) {
				String field = iterator.next();
				if (RestaurantField.fromString(field) == null) {
					iterator.remove();
				}
			}
		}
		
	}

	public List<Restaurant> find() {
		Search searchQuery = buildSearchQuery();
		try {
			SearchResult result = ElasticsearchRestClientFactory.getRestClient().execute(searchQuery);
			if (result != null && result.getJsonObject() != null && result.getJsonObject().getAsJsonObject("hits") != null) {
//				List<Hit<RestaurantDocument, Void>> hits = result.getHits(RestaurantDocument.class);
//				if (hits != null && hits.size() > 0) {
//					List<RestaurantDocument> sources = new ArrayList<RestaurantDocument>();
//					for (Hit<RestaurantDocument, Void> hit : hits) {
//						if (hit != null && hit.source.getObjectId() != null) {
//							sources.add(hit.source);
//						}
//					}
//					List<Restaurant> results = convert(sources);
//					return results;
//				}
				JsonArray hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
				if (hits != null && hits.size() > 0) {
					List<Restaurant> results = convert(hits);
					return results;
				}
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	
	private List<Restaurant> convert(JsonArray hits) {
		if (hits == null || !hits.isJsonArray() || hits.size() <= 0) return Collections.emptyList();
		List<Restaurant> results = new ArrayList<Restaurant>();
		for (int i = 0; i < hits.size(); i++) {
			JsonObject hit = hits.get(i).getAsJsonObject();
			if (hit == null || !hit.isJsonObject() || isMetadata(hit)) {
				continue;
			}
			
			if (hit.get("_source") == null) {
				continue;
			}
			JsonObject source = hit.get("_source").getAsJsonObject();
			Restaurant restaurant = new Restaurant();
			if (source.get("address") != null && !source.get("address").isJsonNull()) {
				restaurant.setAddress(source.get("address").getAsString());
			}
			if (source.get("dislike_count") != null && !source.get("dislike_count").isJsonNull()) {
				restaurant.setDislikeCount(source.get("dislike_count").getAsLong());
			}
			if (source.get("coordinates") != null && !source.get("coordinates").isJsonNull()) {
				restaurant.setDistance(getDistance(source.get("coordinates").getAsJsonObject()));
			}
			if (source.get("english_name") != null && !source.get("english_name").isJsonNull()) {
				restaurant.setEnglishName(source.get("english_name").getAsString());
			}
			if (source.get("favorite_count") != null && !source.get("favorite_count").isJsonNull()) {
				restaurant.setFavoriteCount(source.get("favorite_count").getAsLong());
			}
			if (source.get("objectId") != null && !source.get("objectId").isJsonNull()) {
				restaurant.setId(source.get("objectId").getAsString());
			}
			if (source.get("like_count") != null && !source.get("like_count").isJsonNull()) {
				restaurant.setLikeCount(source.get("like_count").getAsLong());
			}
			if (source.get("name") != null && !source.get("name").isJsonNull()) {
				restaurant.setName(source.get("name").getAsString());
			}
			if (source.get("neutral_count") != null && !source.get("neutral_count").isJsonNull()) {
				restaurant.setNeutralCount(source.get("neutral_count").getAsLong());
			}
			if (source.get("phone") != null && !source.get("phone").isJsonNull()) {
				restaurant.setPhone(source.get("phone").getAsString());
				
			}
			if (source.get("picture") != null && !source.get("picture").isJsonNull()) {
				JsonObject pic = source.get("picture").getAsJsonObject();
				if (pic != null) {
					Picture picture = new Picture();
					picture.setOriginal(pic.get("original").getAsString());
					picture.setThumbnail(pic.get("thumbnail").getAsString());
					restaurant.setPicture(picture);
				}
			}			
			results.add(restaurant);
		}
		return results;
	}

	private boolean isMetadata(JsonObject hit) {
		return (hit.get("_id") == null || "_explain".equals(hit.get("_id").getAsString()));
	}
	
	private Distance getDistance(JsonObject coordinates) {
		if (coordinates == null || !coordinates.isJsonObject() || coordinates.get("lat") == null || coordinates.get("lon") == null) 
			return null;
		if ((returnAllFields || fields.contains(RestaurantField.distance)) && userLocation != null) {
			Coordinates pos1 = new Coordinates();
			pos1.setLat(userLocation.getLat());
			pos1.setLon(userLocation.getLon());
			Coordinates pos2 = new Coordinates();
			pos2.setLat(coordinates.get("lat").getAsDouble());
			pos2.setLon(coordinates.get("lon").getAsDouble());
			Double value = null;
			if (distanceUnit == DistanceUnit.mi) {
				value = HaversineDistanceCalculator.getDistanceInMi(pos1, pos2);
			} else {
				value = HaversineDistanceCalculator.getDistanceInKm(pos1, pos2);
			}
			if (value != null) {
				Distance distance = new Distance();
				distance.setValue(value);
				distance.setUnit(distanceUnit);
				return distance;
			}
		}
		return null;
	}

	private Search buildSearchQuery() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		BaseQueryBuilder query;
		if (keyword != null && !keyword.isEmpty()) {
			query = QueryBuilders.disMaxQuery();
			QueryBuilder nameQuery = QueryBuilders.matchQuery("name", keyword);
			QueryBuilder englishNameQuery = QueryBuilders.matchQuery("english_name", keyword);
			QueryBuilder dishNameQuery = QueryBuilders.matchQuery("dishes", keyword);
			((DisMaxQueryBuilder) query).add(nameQuery);
			((DisMaxQueryBuilder) query).add(englishNameQuery);
			((DisMaxQueryBuilder) query).add(dishNameQuery);
		} else {
			query = QueryBuilders.matchAllQuery();
		}
		

		SortBuilder sort = null;
		if (sortBy == SortBy.distance && userLocation != null) {
			sort = SortBuilders.geoDistanceSort("coordinates").point(userLocation.getLat(), userLocation.getLon());
			if (sortOrder == SortOrder.decrease) {
				sort.order(org.elasticsearch.search.sort.SortOrder.DESC);
			} else {
				sort.order(org.elasticsearch.search.sort.SortOrder.ASC);
			}
		} else {
			sort = SortBuilders.fieldSort("like_count");
			if (sortOrder == SortOrder.decrease) {
				sort.order(org.elasticsearch.search.sort.SortOrder.DESC);
			} else {
				sort.order(org.elasticsearch.search.sort.SortOrder.ASC);
			}
		}
		searchSourceBuilder.query(query).sort(sort).from(offset).size(limit).minScore(relevanceScoreThreshold);
		if (!returnAllFields) {
			searchSourceBuilder.fields(fields);
		}

		Search search = new Search.Builder(searchSourceBuilder.toString())
		                                // multiple index or types can be added.
		                                .addIndex(Indices.FOOD)
		                                .addType(Types.RESTAURANT)
		                                .build();
		return search;
	}
	
	

}

//{
//	  "query": {
//	    "match": {
//	      "_all": "木盆莲藕"
//	    }
//	  },
//	  "min_score": 0.3
//	  
//	}

//{
//	  "query": {
//	    "match": {
//	      "_all": "木盆莲藕"
//	    }
//	  },
//	  "fields" : ["name"],
//	  "min_score": 0.3
//	  
//	}

//{  
//	   "query":{  
//	      "match":{  
//	         "_all":"木盆莲藕"
//	      }
//	   },
//	   "min_score":0.3,
//	   "sort": [
//	    {
//	      "_geo_distance": {
//	        "coordinates": { 
//	          "lat":  37.242312,
//	          "lon": -121.76488699999999
//	        },
//	        "order":         "asc",
//	        "unit":          "mi"
//	      }
//	    }
//	  ]
//	}
