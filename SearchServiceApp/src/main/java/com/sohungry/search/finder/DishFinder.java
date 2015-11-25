package com.sohungry.search.finder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.index.query.BaseQueryBuilder;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
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
import com.sohungry.search.model.Dish;
import com.sohungry.search.model.DishList;
import com.sohungry.search.model.DishSearchRequest;
import com.sohungry.search.model.Distance;
import com.sohungry.search.model.DistanceUnit;
import com.sohungry.search.model.Location;
import com.sohungry.search.model.Picture;
import com.sohungry.search.model.Range;
import com.sohungry.search.model.Restaurant;
import com.sohungry.search.model.RestaurantField;
import com.sohungry.search.model.SortBy;
import com.sohungry.search.model.SortOrder;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class DishFinder {
	
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
	private String restaurantId;
	private String menuId;
	private Range range;
	
	private DishFinder (Builder builder) {
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
		this.restaurantId = builder.restaurantId;
		this.menuId = builder.menuId;
		this.range = builder.range;
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
		private String restaurantId;
		private String menuId;
		private Range range;

		public Builder(DishSearchRequest searchRequest) {
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
			this.restaurantId = searchRequest.getRestaurantId();
			this.menuId = searchRequest.getMenuId();
			this.range = searchRequest.getRange();
			
		}

		private void cleanFields(List<String> fields2) {
			if (fields == null || fields.size() <= 0) return;
			Iterator<String> iterator = fields.iterator();
			while (iterator.hasNext()) {
				String field = iterator.next();
				if (RestaurantField.fromString(field) == null) {
					iterator.remove();
				}
			}
		}

		public DishFinder build() {
			return new DishFinder(this);
		}

	}

	public List<Dish> find() {
		Search searchQuery = buildSearchQuery();
		try {
			SearchResult result = ElasticsearchRestClientFactory.getRestClient().execute(searchQuery);
			if (result != null && result.getJsonObject() != null && result.getJsonObject().getAsJsonObject("hits") != null) {
				JsonArray hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
				if (hits != null && hits.size() > 0) {
					List<Dish> results = convert(hits);
					return results;
				}
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	private List<Dish> convert(JsonArray hits) {
		if (hits == null || !hits.isJsonArray() || hits.size() <= 0) return Collections.emptyList();
		List<Dish> results = new ArrayList<Dish>();
		for (int i = 0; i < hits.size(); i++) {
			JsonObject hit = hits.get(i).getAsJsonObject();
			if (hit == null || !hit.isJsonObject()) {
				continue;
			}
			
			if (hit.get("_source") == null) {
				continue;
			}
			JsonObject source = hit.get("_source").getAsJsonObject();
			Dish dish = new Dish();
			if (source.get("dislike_count") != null && !source.get("dislike_count").isJsonNull()) {
				dish.setDislikeCount(source.get("dislike_count").getAsLong());
			}
			if (source.get("english_name") != null && !source.get("english_name").isJsonNull()) {
				dish.setEnglishName(source.get("english_name").getAsString());
			}
			if (source.get("favorite_count") != null && !source.get("favorite_count").isJsonNull()) {
				dish.setFavoriteCount(source.get("favorite_count").getAsLong());
			}
			if (source.get("objectId") != null && !source.get("objectId").isJsonNull()) {
				dish.setId(source.get("objectId").getAsString());
			}
			if (source.get("like_count") != null && !source.get("like_count").isJsonNull()) {
				dish.setLikeCount(source.get("like_count").getAsLong());
			}
			if (source.get("name") != null && !source.get("name").isJsonNull()) {
				dish.setName(source.get("name").getAsString());
			}
			if (source.get("neutral_count") != null && !source.get("neutral_count").isJsonNull()) {
				dish.setNeutralCount(source.get("neutral_count").getAsLong());
			}
			if (source.get("picture") != null && !source.get("picture").isJsonNull()) {
				JsonObject pic = source.get("picture").getAsJsonObject();
				if (pic != null) {
					Picture picture = new Picture();
					picture.setOriginal(pic.get("original").getAsString());
					picture.setThumbnail(pic.get("thumbnail").getAsString());
					dish.setPicture(picture);
				}
			}
			if (source.get("from_restaurant") != null && !source.get("from_restaurant").isJsonNull()) {
				JsonObject restaurant = source.get("from_restaurant").getAsJsonObject();
				if (restaurant != null) {
					Restaurant fromRestaurant = new Restaurant();
					if (restaurant.get("name") != null && !restaurant.get("name").isJsonNull()) {
						fromRestaurant.setName(restaurant.get("name").getAsString());
					}
					if (restaurant.get("english_name") != null && !restaurant.get("english_name").isJsonNull()) {
						fromRestaurant.setEnglishName(restaurant.get("english_name").getAsString());
					}
					if (restaurant.get("coordinates") != null && !restaurant.get("coordinates").isJsonNull()) {
						fromRestaurant.setDistance(getDistance(restaurant.get("coordinates").getAsJsonObject()));
					}
					dish.setFromRestaurant(fromRestaurant);
				}
			}
			if (source.get("lists") != null && !source.get("lists").isJsonNull()) {
				JsonArray lists = source.get("lists").getAsJsonArray();
				if (lists.size() > 0) {
					List<DishList> dishLists = new ArrayList<DishList>();
					for (int j = 0; i < lists.size(); i++) {
						DishList dishList = new DishList();
						JsonObject list = lists.get(j).getAsJsonObject();
						if (list.get("objectId") != null && !list.get("objectId").isJsonNull()) {
							dishList.setId(list.get("objectId").getAsString());
						}
						if (list.get("name") != null && !list.get("name").isJsonNull()) {
							dishList.setName(list.get("name").getAsString());
						}
						dishLists.add(dishList);
					}
					dish.setRelatedLists(dishLists);
				}
			}
			results.add(dish);
		}
		return results;
	}

	private Search buildSearchQuery() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		BaseQueryBuilder mainQuery;
		if (keyword != null && !keyword.isEmpty()) {
			mainQuery = QueryBuilders.disMaxQuery();
			QueryBuilder nameQuery = QueryBuilders.matchQuery("name", keyword);
			QueryBuilder englishNameQuery = QueryBuilders.matchQuery("english_name", keyword);
			QueryBuilder nestedRestaurantQuery = createNestedRestaurantQuery();
			QueryBuilder nestedDishListQuery = createNestedDishListQuery();
			((DisMaxQueryBuilder) mainQuery).add(nameQuery);
			((DisMaxQueryBuilder) mainQuery).add(englishNameQuery);
			((DisMaxQueryBuilder) mainQuery).add(nestedRestaurantQuery);
			((DisMaxQueryBuilder) mainQuery).add(nestedDishListQuery);
		} else {
			mainQuery = QueryBuilders.matchAllQuery();
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
		
		FilterBuilder restaurantFilter = createRestaurantFilter();
		FilterBuilder menuFilter = createMenuFilter();
		FilterBuilder distanceFilter = createRangeFilter();
		if (restaurantFilter != null || menuFilter != null || distanceFilter != null) {
			BoolFilterBuilder boolFilter = FilterBuilders.boolFilter();
			if (restaurantFilter != null) {
				boolFilter.must(restaurantFilter);
			}
			if (menuFilter != null) {
				boolFilter.must(menuFilter);
			}
			if (distanceFilter != null) {
				boolFilter.must(distanceFilter);
			}
			FilteredQueryBuilder filteredQuery = QueryBuilders.filteredQuery(mainQuery, boolFilter);
			searchSourceBuilder.query(filteredQuery).sort(sort).from(offset).size(limit).minScore(relevanceScoreThreshold);
		} else {
			searchSourceBuilder.query(mainQuery).sort(sort).from(offset).size(limit).minScore(relevanceScoreThreshold);
		}
		if (!returnAllFields) {
			searchSourceBuilder.fields(fields);
		}
		Search search = new Search.Builder(searchSourceBuilder.toString())
		                                // multiple index or types can be added.
		                                .addIndex(Indices.FOOD)
		                                .addType(Types.DISH)
		                                .build();
		return search;
	}

	private QueryBuilder createNestedRestaurantQuery() {
		DisMaxQueryBuilder query = QueryBuilders.disMaxQuery();
		QueryBuilder nameQuery = QueryBuilders.matchQuery("from_restaurant.name", keyword);
		QueryBuilder englishQuery = QueryBuilders.matchQuery("from_restaurant.english_name", keyword);
		query.add(nameQuery).add(englishQuery);
		QueryBuilder nestedRestaurantQuery = QueryBuilders.nestedQuery("from_restaurant", query);
		return nestedRestaurantQuery;
	}

	private QueryBuilder createNestedDishListQuery() {
		QueryBuilder query = QueryBuilders.matchQuery("lists.name", keyword);
		QueryBuilder nestedListsQuery = QueryBuilders.nestedQuery("lists", query);
		return nestedListsQuery;
	}

	private FilterBuilder createRangeFilter() {
		if (this.range != null && this.range.getDistance() != null && this.range.getCenter() != null) {
			org.elasticsearch.common.unit.DistanceUnit unit = org.elasticsearch.common.unit.DistanceUnit.MILES;
			if (this.range.getDistance() != null && this.range.getDistance().getUnit() != null && this.range.getDistance().getUnit() == DistanceUnit.km) {
				unit = org.elasticsearch.common.unit.DistanceUnit.KILOMETERS;
			} 
			FilterBuilder geoDistanceFilter = FilterBuilders.geoDistanceFilter("from_restaurant.coordinates").distance(range.getDistance().getValue(), unit);
			FilterBuilder nestedRestFilter = FilterBuilders.nestedFilter("from_restaurant", geoDistanceFilter);
			return nestedRestFilter;
		}
		return null;
	}

	private FilterBuilder createMenuFilter() {
		if (this.menuId != null) {
			FilterBuilder menuFilter = FilterBuilders.termFilter("menu.objectId", this.menuId);
			FilterBuilder nestedMenuFilter = FilterBuilders.nestedFilter("menu", menuFilter);
			return nestedMenuFilter;
		}
		return null;
	}

	private FilterBuilder createRestaurantFilter() {
		if (this.restaurantId != null) {
			FilterBuilder restaurantFilter = FilterBuilders.termFilter("from_restaurant.objectId", this.restaurantId);
			FilterBuilder nestedRestFilter = FilterBuilders.nestedFilter("from_restaurant", restaurantFilter);
			return nestedRestFilter;
		}
		return null;
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

}
