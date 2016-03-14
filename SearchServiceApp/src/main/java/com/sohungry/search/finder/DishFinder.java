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
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sohungry.search.converter.DishConverter;
import com.sohungry.search.elastic.factory.ElasticsearchRestClientFactory;
import com.sohungry.search.meta.Indices;
import com.sohungry.search.meta.Types;
import com.sohungry.search.model.Dish;
import com.sohungry.search.model.DishField;
import com.sohungry.search.model.DishSearchRequest;
import com.sohungry.search.model.DistanceUnit;
import com.sohungry.search.model.Location;
import com.sohungry.search.model.Range;
import com.sohungry.search.model.SortBy;
import com.sohungry.search.model.SortOrder;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class DishFinder extends AbstractFinder<Dish> {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DishFinder.class);
	
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
	private boolean debugMode;
	private boolean highlightInField;
	private String language = "zh";
	
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
		this.debugMode = builder.debugMode;
		this.highlightInField = builder.highlightInField;
		this.language = builder.language;
		
	}

	public static class Builder {

		private static final int DEFAULT_OFFSET = 0;
		private static final int DEFAULT_LIMIT = 20;
		private static final SortOrder DEFAULT_SORT_ORDER = SortOrder.descend;
		private static final SortBy DEFAULT_SORT_BY = SortBy.relevance;
		private static final float DEFAULT_REL_THRESH = (float) 1.0;
		private static final DistanceUnit DEFAULT_DISTANCE_UNIT = DistanceUnit.mi;
		
		private String keyword;
		private int offset = DEFAULT_OFFSET;
		private int limit = DEFAULT_LIMIT;
		private SortBy sortBy = DEFAULT_SORT_BY;
		private SortOrder sortOrder = DEFAULT_SORT_ORDER;
		private float relevanceScoreThreshold = DEFAULT_REL_THRESH;
		private boolean returnAllFields = true;
		private List<String> fields = new ArrayList<String>();
		private Location userLocation;
		private DistanceUnit distanceUnit = DEFAULT_DISTANCE_UNIT;
		private String restaurantId;
		private String menuId;
		private Range range;
		private boolean debugMode;
		private boolean highlightInField;
		private String language = "zh";

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
			if (searchRequest.getOutput() != null && searchRequest.getOutput().getFields() != null) {
				this.returnAllFields = false;
				this.fields = searchRequest.getOutput().getFields();
			} 
			normalizeFields(fields);
			
			this.userLocation = searchRequest.getUserLocation();
			if (searchRequest.getOutput() != null && searchRequest.getOutput().getParams() != null && searchRequest.getOutput().getParams().getDistanceUnit() != null) {
				this.distanceUnit = searchRequest.getOutput().getParams().getDistanceUnit();
			} 
			this.restaurantId = searchRequest.getRestaurantId();
			this.menuId = searchRequest.getMenuId();
			this.range = searchRequest.getRange();
			this.highlightInField = searchRequest.isHighlightInField();
		}

		private void normalizeFields(List<String> fields) {
			if (returnAllFields) {
				fields.clear();
				for (DishField field : DishField.values()) {
					fields.add(field.name());
				}
			} else {
				if (fields == null || fields.size() <= 0) return;
				Iterator<String> iterator = fields.iterator();
				while (iterator.hasNext()) {
					String field = iterator.next();
					if (DishField.fromString(field) == null) {
						iterator.remove();
					}
				}
			}
			
		}

		public boolean isDebugMode() {
			return debugMode;
		}

		public Builder setDebugMode(boolean debugMode) {
			this.debugMode = debugMode;
			return this;
		}

		public String getLanguage() {
			return language;
		}

		public Builder setLanguage(String language) {
			this.language = language;
			return this;
		}

		public DishFinder build() {
			return new DishFinder(this);
		}

	}

	@Override
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
			LOGGER.error("Error during search", e);
		}
		return Collections.emptyList();
	}

	@Override
	protected List<Dish> convert(JsonArray hits) {
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
			Dish dish = new DishConverter(fields, userLocation, distanceUnit, this.language).convert(source);		
			if (highlightInField && hit.get("highlight") != null && hit.get("highlight").getAsJsonObject() != null) {
				JsonObject highlightResult = hit.get("highlight").getAsJsonObject();
				String highlightedName = null;
				String highlightedEnglishName = null;
				if (highlightResult.get("name") != null) {
					JsonArray nameHighlight = highlightResult.get("name").getAsJsonArray();
					if (nameHighlight != null) {
						JsonElement element = nameHighlight.get(0);
						if (element != null) {
							highlightedName = element.getAsString();
						} 
					}
				} 
				if (highlightResult.get("english_name") != null) {
					JsonArray englishNameHighlight = highlightResult.get("english_name").getAsJsonArray();
					if (englishNameHighlight != null) {
						JsonArray nameHighlight = highlightResult.get("english_name").getAsJsonArray();
						if (nameHighlight != null) {
							
							JsonElement element = nameHighlight.get(0);
							if (element != null) {
								highlightedEnglishName = element.getAsString();
							}
						}
					}
				} 
				if ("zh".equals(this.language)) {
					String name = dish.getName();
					if (highlightedName != null) {
						name = highlightedName;
					}
					if (highlightedEnglishName != null) {
						name = name + " (" + highlightedEnglishName + ")";
					}
					dish.setName(name);
				} else if ("en".equals(this.language)) {
					String name = dish.getName();
					if (highlightedEnglishName != null) {
						name = highlightedEnglishName;
					}
					if (highlightedName != null) {
						name = name + " (" + highlightedName + ")";
					}
					dish.setName(name);
				}
			}
			results.add(dish);
			if (debugMode) {
				double score = 0;
				if (hit.get("_score") != null) {
					score = hit.get("_score").getAsDouble();
				}
				dish.addDiagInfo("score", String.valueOf(score));
			}
		}
		return results;
	}

	@Override
	protected Search buildSearchQuery() {
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
			if (sortOrder == SortOrder.descend) {
				sort.order(org.elasticsearch.search.sort.SortOrder.DESC);
			} else {
				sort.order(org.elasticsearch.search.sort.SortOrder.ASC);
			}
		} else if (sortBy == SortBy.hotness) {
			sort = SortBuilders.fieldSort("like_count");
			if (sortOrder == SortOrder.descend) {
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
			searchSourceBuilder.query(filteredQuery);
		} else {
			searchSourceBuilder.query(mainQuery);
		}
		if (sort != null) {
			searchSourceBuilder.sort(sort);
		}
		searchSourceBuilder.from(offset).size(limit).minScore(relevanceScoreThreshold);
		if (!returnAllFields) {
			searchSourceBuilder.fields(fields);
		}
		if (highlightInField) {
			searchSourceBuilder.highlight(new HighlightBuilder().preTags("<b>").postTags("</b>").field("name").field("english_name"));
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
		if ((this.range != null && this.range.getDistance() != null && this.range.getCenter() != null) || this.userLocation != null ) {
			org.elasticsearch.common.unit.DistanceUnit unit = org.elasticsearch.common.unit.DistanceUnit.MILES;
			Location center = null;
			double distance = 50;
			if (this.range != null && this.range.getDistance() != null && this.range.getCenter() != null) {
				if (this.range.getDistance() != null && this.range.getDistance().getUnit() != null && this.range.getDistance().getUnit() == DistanceUnit.km) {
					unit = org.elasticsearch.common.unit.DistanceUnit.KILOMETERS;
				}
				center = this.range.getCenter();
				distance = range.getDistance().getValue();
			} else {
				center = this.userLocation;
			}
			 
			FilterBuilder geoDistanceFilter = FilterBuilders.geoDistanceFilter("from_restaurant.coordinates").distance(distance, unit).lat(center.getLat()).lon(center.getLon());
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
}
