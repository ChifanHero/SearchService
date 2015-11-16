package com.sohungry.search.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Distance {
	
	private double value;
	private DistanceUnit unit;
	
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public DistanceUnit getUnit() {
		return unit;
	}
	public void setUnit(DistanceUnit unit) {
		this.unit = unit;
	}

}
