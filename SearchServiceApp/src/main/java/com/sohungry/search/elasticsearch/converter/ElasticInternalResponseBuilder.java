package com.sohungry.search.elasticsearch.converter;

import io.searchbox.core.SearchResult;

public interface ElasticInternalResponseBuilder<E> {
	
	public E build(SearchResult result);

}
