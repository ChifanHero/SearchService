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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sohungry.search.converter.DishListConverter;
import com.sohungry.search.elastic.factory.ElasticsearchRestClientFactory;
import com.sohungry.search.meta.Indices;
import com.sohungry.search.meta.Types;
import com.sohungry.search.model.DishList;
import com.sohungry.search.model.DishListField;
import com.sohungry.search.model.DishListSearchRequest;
import com.sohungry.search.model.DistanceUnit;
import com.sohungry.search.model.Location;
import com.sohungry.search.model.Range;
import com.sohungry.search.model.SortBy;
import com.sohungry.search.model.SortOrder;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class DishListFinder extends AbstractFinder<DishList>{
	
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
	private Range range;
	
	private DishListFinder(Builder builder) {
		
		this.keyword = builder.keyword;
		this.offset = builder.offset;
		this.limit = builder.limit;
		this.sortBy = builder.sortBy;
		this.sortOrder = builder.sortOrder;
		this.relevanceScoreThreshold = builder.relevanceScoreThreshold;
		this.returnAllFields = builder.returnAllFields;
		this.fields = builder.fields;
		this.userLocation = builder.userLocation;
		this.range = builder.range;
		
	}
	
	public static class Builder {
		
		private static final int DEFAULT_OFFSET = 0;
		private static final int DEFAULT_LIMIT = 20;
		private static final SortOrder DEFAULT_SORT_ORDER = SortOrder.DESCEND;
		private static final SortBy DEFAULT_SORT_BY = SortBy.RELEVANCE;
		private static final float DEFAULT_REL_THRESH = (float) 0.3;
		
		private String keyword;
		private int offset = DEFAULT_OFFSET;
		private int limit = DEFAULT_LIMIT;
		private SortBy sortBy = DEFAULT_SORT_BY;
		private SortOrder sortOrder = DEFAULT_SORT_ORDER;
		private float relevanceScoreThreshold = DEFAULT_REL_THRESH;
		private boolean returnAllFields = true;
		private List<String> fields = new ArrayList<String>();
		private Location userLocation;
		private Range range;
		
		public Builder(DishListSearchRequest searchRequest) {
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
			this.range = searchRequest.getRange();
		}
		
		private void normalizeFields(List<String> fields) {
			if (returnAllFields) {
				fields.clear();
				for (DishListField field : DishListField.values()) {
					fields.add(field.name());
				}
			} else {
				if (fields == null || fields.size() <= 0) return;
				Iterator<String> iterator = fields.iterator();
				while (iterator.hasNext()) {
					String field = iterator.next();
					if (DishListField.fromString(field) == null) {
						iterator.remove();
					}
				}
			}
			
		}

		public DishListFinder build() {
			return new DishListFinder(this);
		}
		
	}

	@Override
	public List<DishList> find() {
		Search searchQuery = buildSearchQuery();
		try {
			SearchResult result = ElasticsearchRestClientFactory.getRestClient().execute(searchQuery);
			if (result != null && result.getJsonObject() != null && result.getJsonObject().getAsJsonObject("hits") != null) {
				JsonArray hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
				if (hits != null && hits.size() > 0) {
					List<DishList> results = convert(hits);
					return results;
				}
			}	
		} catch (IOException e) {
			LOGGER.error("Error during search", e);
		}
		return Collections.emptyList();
	}
	
	@Override
	protected List<DishList> convert(JsonArray hits) {
		if (hits == null || !hits.isJsonArray() || hits.size() <= 0) return Collections.emptyList();
		List<DishList> results = new ArrayList<DishList>();
		for (int i = 0; i < hits.size(); i++) {
			JsonObject hit = hits.get(i).getAsJsonObject();
			if (hit == null || !hit.isJsonObject()) {
				continue;
			}
			
			if (hit.get("_source") == null) {
				continue;
			}
			JsonObject source = hit.get("_source").getAsJsonObject();
			DishList dishList = new DishListConverter(fields).convert(source);
			results.add(dishList);
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
			QueryBuilder restaurantsQuery = QueryBuilders.matchQuery("restaurants", keyword);
			QueryBuilder dishesQuery = QueryBuilders.matchQuery("dishes", keyword);
			((DisMaxQueryBuilder) mainQuery).add(nameQuery);
			((DisMaxQueryBuilder) mainQuery).add(restaurantsQuery);
			((DisMaxQueryBuilder) mainQuery).add(dishesQuery);
		} else {
			mainQuery = QueryBuilders.matchAllQuery();
		}
		
		
		
		SortBuilder sort = null;
		if (sortBy == SortBy.DISTANCE && userLocation != null) {
			sort = SortBuilders.geoDistanceSort("coordinates").point(userLocation.getLat(), userLocation.getLon());
			if (sortOrder == SortOrder.DESCEND) {
				sort.order(org.elasticsearch.search.sort.SortOrder.DESC);
			} else {
				sort.order(org.elasticsearch.search.sort.SortOrder.ASC);
			}
		} else if (sortBy == SortBy.HOTNESS) {
			sort = SortBuilders.fieldSort("like_count");
			if (sortOrder == SortOrder.DESCEND) {
				sort.order(org.elasticsearch.search.sort.SortOrder.DESC);
			} else {
				sort.order(org.elasticsearch.search.sort.SortOrder.ASC);
			}
		}
		
		FilterBuilder distanceFilter = createRangeFilter();
		if (distanceFilter != null) {
			BoolFilterBuilder boolFilter = FilterBuilders.boolFilter();
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
		Search search = new Search.Builder(searchSourceBuilder.toString())
		                                .addIndex(Indices.FOOD)
		                                .addType(Types.DISHLIST)
		                                .build();
		return search;
	}

	private FilterBuilder createRangeFilter() {
		if (this.range != null && this.range.getDistance() != null && this.range.getCenter() != null) {
			org.elasticsearch.common.unit.DistanceUnit unit = org.elasticsearch.common.unit.DistanceUnit.MILES;
			if (this.range.getDistance() != null && this.range.getDistance().getUnit() != null && this.range.getDistance().getUnit() == DistanceUnit.KM) {
				unit = org.elasticsearch.common.unit.DistanceUnit.KILOMETERS;
			} 
			FilterBuilder geoDistanceFilter = FilterBuilders.geoDistanceFilter("locations").distance(range.getDistance().getValue(), unit);
			return geoDistanceFilter;
		}
		return null;
	}


}
