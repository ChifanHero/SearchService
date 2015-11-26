package com.sohungry.search.finder;

import java.util.List;

import com.google.gson.JsonArray;

import io.searchbox.core.Search;

public abstract class AbstractFinder<T> {
	
	public abstract List<T> find();
	
	abstract List<T> convert(JsonArray hits);
	
	abstract Search buildSearchQuery();

}
