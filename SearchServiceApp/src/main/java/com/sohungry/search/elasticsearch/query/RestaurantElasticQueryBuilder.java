package com.sohungry.search.elasticsearch.query;

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

import com.sohungry.search.domain.context.RestaurantRequestContext;
import com.sohungry.search.meta.Indices;
import com.sohungry.search.meta.Types;
import com.sohungry.search.model.DistanceUnit;
import com.sohungry.search.model.Location;
import com.sohungry.search.model.Range;
import com.sohungry.search.model.RestaurantField;
import com.sohungry.search.model.SortBy;
import com.sohungry.search.model.SortOrder;

import io.searchbox.core.Search;

public class RestaurantElasticQueryBuilder implements ElasticQueryBuilder{

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
	private boolean highlightInField;
	private float minRating;
	
	public RestaurantElasticQueryBuilder(RestaurantRequestContext requestContext) {
		if (requestContext != null) {
			this.keyword = requestContext.getKeyword();
			this.offset = requestContext.getOffset();
			this.limit = requestContext.getLimit();
			this.sortBy = requestContext.getSortBy();
			this.sortOrder = requestContext.getSortOrder();
			this.relevanceScoreThreshold = requestContext.getRelevanceScoreThreshold();
			this.returnAllFields = requestContext.isReturnAllFields();
			this.fields = requestContext.getFields();
			this.range = requestContext.getRange();
			this.userLocation = requestContext.getUserLocation();
			this.minRating = requestContext.getMinRating();
		}
	}

	@Override
	public Search buildQuery() {
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
			sort = SortBuilders.fieldSort("rating_total");
			if (sortOrder == SortOrder.descend) {
				sort.order(org.elasticsearch.search.sort.SortOrder.DESC);
			} else {
				sort.order(org.elasticsearch.search.sort.SortOrder.ASC);
			}
		} else if (sortBy == SortBy.rating) {
			sort = SortBuilders.fieldSort("rating");
			if (sortOrder == SortOrder.descend) {
				sort.order(org.elasticsearch.search.sort.SortOrder.DESC);
			} else {
				sort.order(org.elasticsearch.search.sort.SortOrder.ASC);
			}
		}
		
		if (needFilteredQuery()) {	
			BoolFilterBuilder boolFilter = FilterBuilders.boolFilter();
			if (needRangeFilter()) {
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
				FilterBuilder geoDistanceFilter = FilterBuilders.geoDistanceFilter("coordinates").distance(distance, unit).lat(center.getLat()).lon(center.getLon());
				boolFilter.must(geoDistanceFilter);
			}
			if (needRatingFilter()) {
				FilterBuilder ratingFilter = FilterBuilders.rangeFilter("rating").gt(this.minRating);
				boolFilter.must(ratingFilter);
			}
			FilteredQueryBuilder filterreQuery = QueryBuilders.filteredQuery(query, boolFilter);
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
	
	private boolean needFilteredQuery() {
		return needRangeFilter() || needRatingFilter();
	}
	
	private boolean needRangeFilter() {
		return (this.range != null && this.range.getDistance() != null && this.range.getCenter() != null) || this.userLocation != null;
	}
	
	private boolean needRatingFilter() {
		return this.minRating > 0f;
	}


}
