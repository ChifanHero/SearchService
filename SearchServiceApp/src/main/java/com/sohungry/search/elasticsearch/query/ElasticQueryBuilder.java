package com.sohungry.search.elasticsearch.query;

import io.searchbox.core.Search;

public interface ElasticQueryBuilder {
	
	public Search buildQuery(); 

}
