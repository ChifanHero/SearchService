package com.sohungry.search.converter;

import com.google.gson.JsonObject;

public interface Converter<T> {
	
	T convert(JsonObject source);

}
