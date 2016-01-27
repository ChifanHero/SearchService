package com.sohungry.search.finder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.index.query.BaseQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
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
import com.sohungry.search.converter.RestaurantConverter;
import com.sohungry.search.elastic.factory.ElasticsearchRestClientFactory;
import com.sohungry.search.meta.Indices;
import com.sohungry.search.meta.Types;
import com.sohungry.search.model.DistanceUnit;
import com.sohungry.search.model.Location;
import com.sohungry.search.model.Range;
import com.sohungry.search.model.Restaurant;
import com.sohungry.search.model.RestaurantField;
import com.sohungry.search.model.RestaurantSearchRequest;
import com.sohungry.search.model.SortBy;
import com.sohungry.search.model.SortOrder;
import com.sohungry.search.util.HighlightImportanceComparator;
import com.sohungry.search.util.StringUtil;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class RestaurantFinder extends AbstractFinder<Restaurant> {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RestaurantFinder.class);
//	private final static double PROMINENT_SCORE = 3.0;
	
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
	private Range range;
	private boolean debugMode;
	private boolean highlightInField;
	private String language;
	
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
		this.range = builder.range;
		this.debugMode = builder.debugMode;
		this.highlightInField = builder.highlightInField;
		this.language = builder.language;
	}
	
	public static class Builder {
		
		private static final int DEFAULT_OFFSET = 0;
		private static final int DEFAULT_LIMIT = 20;
		private static final SortBy DEFAULT_SORTBY = SortBy.relevance;
		private static final SortOrder DEFAULT_SORT_ORDER = SortOrder.descend;
		private static final float DEFAULT_REL_THRESH = (float) 0.6;
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
		private Range range;
		private boolean debugMode;
		private boolean highlightInField;
		private String language = "zh";
		
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
			if (searchRequest.getOutput() != null && searchRequest.getOutput().getFields() != null) {
				this.returnAllFields = false;
				this.fields = searchRequest.getOutput().getFields();
			} 
			normalizeFields(fields);
			this.userLocation = searchRequest.getUserLocation();
			if (searchRequest.getOutput() != null && searchRequest.getOutput().getParams() != null && searchRequest.getOutput().getParams().getDistanceUnit() != null) {
				this.distanceUnit = searchRequest.getOutput().getParams().getDistanceUnit();
			}
			this.range = searchRequest.getRange();
			this.highlightInField = searchRequest.isHighlightInField();
		}
		
		public RestaurantFinder build() {
			return new RestaurantFinder(this);
		}
		
		private void normalizeFields(List<String> fields) {
			if (returnAllFields) {
				fields.clear();
				for (RestaurantField field : RestaurantField.values()) {
					fields.add(field.name());
				}
			} else {
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
		
	}

	@Override
	public List<Restaurant> find() {
		Search searchQuery = buildSearchQuery();
		try {
			SearchResult result = ElasticsearchRestClientFactory.getRestClient().execute(searchQuery);
			if (result != null && result.getJsonObject() != null && result.getJsonObject().getAsJsonObject("hits") != null) {
				JsonArray hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
				if (hits != null && hits.size() > 0) {
					List<Restaurant> results = convert(hits);
					return results;
				}
			}	
		} catch (IOException e) {
			LOGGER.error("Error during search", e);
		}
		return Collections.emptyList();
	}
	
	@Override
	protected List<Restaurant> convert(JsonArray hits) {
		if (hits == null || !hits.isJsonArray() || hits.size() <= 0) return Collections.emptyList();
		List<Restaurant> results = new ArrayList<Restaurant>();
//		boolean hasProminantResult = false;
		for (int i = 0; i < hits.size(); i++) {
			JsonObject hit = hits.get(i).getAsJsonObject();
			if (hit == null || !hit.isJsonObject()) {
				continue;
			}
			
			if (hit.get("_source") == null) {
				continue;
			}
			double score = 0;
			if (hit.get("_score") != null) {
				score = hit.get("_score").getAsDouble();
			}
//			if (score >= PROMINENT_SCORE) {
//				hasProminantResult = true;
//			}
//			if (hasProminantResult && score < PROMINENT_SCORE) {
//				continue;
//			}
			JsonObject source = hit.get("_source").getAsJsonObject();
			Restaurant restaurant = new RestaurantConverter(this.fields, this.userLocation, this.distanceUnit, this.language).convert(source);	
			if (highlightInField && hit.get("highlight") != null && hit.get("highlight").getAsJsonObject() != null) {
				JsonObject highlightResult = hit.get("highlight").getAsJsonObject();
				String highlightedName = null;
				String highlightedEnglishName = null;
				if (highlightResult.get(RestaurantField.name.name()) != null) {
					JsonArray nameHighlight = highlightResult.get(RestaurantField.name.name()).getAsJsonArray();
					if (nameHighlight != null) {
						JsonElement element = nameHighlight.get(0);
						if (element != null) {
							highlightedName = element.getAsString();
						} 
					}
				} 
				if (highlightResult.get(RestaurantField.english_name.name()) != null) {
					JsonArray englishNameHighlight = highlightResult.get(RestaurantField.english_name.name()).getAsJsonArray();
					if (englishNameHighlight != null) {
						JsonArray nameHighlight = highlightResult.get(RestaurantField.english_name.name()).getAsJsonArray();
						if (nameHighlight != null) {
							
							JsonElement element = nameHighlight.get(0);
							if (element != null) {
								highlightedEnglishName = element.getAsString();
							}
						}
					}
				} 
				if (highlightResult.get("dishes") != null) {
					JsonArray dishHighlight = highlightResult.get("dishes").getAsJsonArray();
					if (dishHighlight != null) {
						List<String> dishes = new ArrayList<String>();
						for (int j = 0; j < dishHighlight.size(); j++) {
							JsonElement element = dishHighlight.get(j);
							if (element != null) {
								String dish = element.getAsString();
								if ("zh".equals(this.language)) {
									if (StringUtil.containsHanScript(dish)) {
										dishes.add(dish);
									}
								} else if ("en".equals(this.language)) {
									if (!StringUtil.containsHanScript(dish)) {
										dishes.add(dish);
									}
								}				
							}
						}
						if (dishes.size() > 0) {
							Collections.sort(dishes, new HighlightImportanceComparator());
							List<String> currentDishes = restaurant.getDishes();
							Iterator<String> iterator = currentDishes.iterator();
							while (iterator.hasNext()) {
								String dish = iterator.next();
								for (int m = 0; m < this.keyword.length(); m++) {
									if (dish.indexOf(this.keyword.charAt(m)) > 0) {
										iterator.remove();
										break;
									}
								}
							}
							currentDishes.addAll(0, dishes);
						}
					}
				} 
				if (highlightResult.get("address") != null) {
					JsonArray addressHighlight = highlightResult.get("address").getAsJsonArray();
					if (addressHighlight != null) {
						JsonArray nameHighlight = highlightResult.get("address").getAsJsonArray();
						if (nameHighlight != null) {
							String highlightedAddress;
							JsonElement element = nameHighlight.get(0);
							if (element != null) {
								highlightedAddress = element.getAsString();
								restaurant.setAddress(highlightedAddress);
							}
						}
					}
				}
				if ("zh".equals(this.language)) {
					String name = restaurant.getName();
					if (highlightedName != null) {
						name = highlightedName;
					}
					if (highlightedEnglishName != null) {
						name = name + " (" + highlightedEnglishName + ")";
					}
					restaurant.setName(name);
				} else if ("en".equals(this.language)) {
					String name = restaurant.getName();
					if (highlightedEnglishName != null) {
						name = highlightedEnglishName;
					}
					if (highlightedName != null) {
						name = name + " (" + highlightedName + ")";
					}
					restaurant.setName(name);
				}
			}
			if (debugMode) {
				restaurant.addDiagInfo("score", String.valueOf(score));
			}
			results.add(restaurant);
		}
		return results;
	}


	@Override
	protected Search buildSearchQuery() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		BaseQueryBuilder query;
		if (keyword != null && !keyword.isEmpty()) {
			
//			query = QueryBuilders.boolQuery();
			query = QueryBuilders.disMaxQuery();
			QueryBuilder nameQuery = QueryBuilders.matchQuery("name", keyword).boost(3);
			QueryBuilder englishNameQuery = QueryBuilders.matchQuery("english_name", keyword).boost(2);
			QueryBuilder dishNameQuery = QueryBuilders.matchQuery("dishes", keyword);
//			QueryBuilder dishNameQuery = QueryBuilders.matchPhraseQuery("dishes", keyword);
			QueryBuilder addressQuery = QueryBuilders.matchQuery("address", keyword);
			((DisMaxQueryBuilder) query).add(nameQuery);
			((DisMaxQueryBuilder) query).add(englishNameQuery);
			((DisMaxQueryBuilder) query).add(dishNameQuery);
			((DisMaxQueryBuilder) query).add(addressQuery);
//			((BoolQueryBuilder) query).should(dismaxQuery);
//			((BoolQueryBuilder) query).should(addressQuery);
		} else {
			query = QueryBuilders.matchAllQuery();
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
		
		if (this.range != null && this.range.getCenter() != null && this.range.getDistance() != null) {
			org.elasticsearch.common.unit.DistanceUnit unit = org.elasticsearch.common.unit.DistanceUnit.MILES;
			if (this.range.getDistance() != null && this.range.getDistance().getUnit() != null && this.range.getDistance().getUnit() == DistanceUnit.km) {
				unit = org.elasticsearch.common.unit.DistanceUnit.KILOMETERS;
			} 
			FilterBuilder geoDistanceFilter = FilterBuilders.geoDistanceFilter("coordinates").distance(range.getDistance().getValue(), unit);
			FilteredQueryBuilder filterreQuery = QueryBuilders.filteredQuery(query, geoDistanceFilter);
			searchSourceBuilder.query(filterreQuery);
		} else {
			searchSourceBuilder.query(query);
		}
		if (sort != null) {
			searchSourceBuilder.sort(sort);
		}
		searchSourceBuilder.from(offset).size(limit).minScore(relevanceScoreThreshold);
		
		if (!returnAllFields) {
			searchSourceBuilder.fields(fields);
		} 
		if (highlightInField) {
			searchSourceBuilder.highlight(new HighlightBuilder().preTags("<b>").postTags("</b>").field(RestaurantField.name.name()).field("dishes").field("address").field(RestaurantField.english_name.name()));
		}
				
		Search search = new Search.Builder(searchSourceBuilder.toString())
		                                // multiple index or types can be added.
		                                .addIndex(Indices.FOOD)
		                                .addType(Types.RESTAURANT)
		                                .build();
		return search;
	}
	
//	private QueryBuilder getCompositeQuery(String fieldName, String keyword, float boost) {
//		if (fieldName == null || keyword == null) return null;
//		QueryBuilder matchQuery = QueryBuilders.matchQuery(fieldName, keyword);
//		QueryBuilder phraseQuery = QueryBuilders.matchPhraseQuery(fieldName, keyword).boost(boost).slop(50);
//		return QueryBuilders.boolQuery().should(matchQuery).should(phraseQuery).minimumNumberShouldMatch(1);
//	}
//	
	

}

