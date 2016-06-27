package com.sohungry.search.google.params;

import se.walkercrou.places.Param;

public interface ParamBuilder {
	
	public Param[] buildParams();
	
	public String getKeyword();
	
	public int getLimit();

}
