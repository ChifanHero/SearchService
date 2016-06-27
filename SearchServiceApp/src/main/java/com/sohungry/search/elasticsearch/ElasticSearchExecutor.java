package com.sohungry.search.elasticsearch;

import java.io.IOException;

import com.sohungry.search.elastic.factory.ElasticsearchRestClientFactory;
import com.sohungry.search.elasticsearch.query.ElasticQueryBuilder;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class ElasticSearchExecutor {
	
	public SearchResult execute(ElasticQueryBuilder queryBuilder) {
		Search searchQuery = queryBuilder.buildQuery();
		SearchResult result = null;
		try {
			result = ElasticsearchRestClientFactory.getRestClient().execute(searchQuery);
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
	}

}
