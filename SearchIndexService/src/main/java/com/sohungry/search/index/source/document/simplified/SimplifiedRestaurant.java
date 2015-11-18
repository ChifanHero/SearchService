package com.sohungry.search.index.source.document.simplified;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.sohungry.search.index.source.document.GeoPoint;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimplifiedRestaurant {
	
	private String objectId;
	private String name;
	
	@JsonProperty("english_name")
	private String englishName;
	
	private GeoPoint coordinates;
	
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEnglishName() {
		return englishName;
	}
	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}
	public GeoPoint getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(GeoPoint coordinates) {
		this.coordinates = coordinates;
	}

}
