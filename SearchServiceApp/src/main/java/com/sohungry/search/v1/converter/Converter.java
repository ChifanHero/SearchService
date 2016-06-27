package com.sohungry.search.v1.converter;

import com.google.gson.JsonObject;

public interface Converter<T> {
	
	T convert(JsonObject source);

}
