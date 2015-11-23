package com.sohungry.search.index.source.document;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.sohungry.search.index.source.document.shared.GeoPoint;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DishListDocument {
	
	private String createdAt;
	
	@JsonProperty("favorite_count")
	private long favoriteCount;
	
	@JsonProperty("like_count")
	private long likeCount;
	
	@JsonProperty("member_count")
	private long memberCount;
	
	private String name;
	private String objectId;
	private String updatedAt;
	
	private List<String> dishes;
	private List<String> restaurants;
	
	private List<GeoPoint> locations;
	
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public long getFavoriteCount() {
		return favoriteCount;
	}
	public void setFavoriteCount(long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}
	public long getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(long likeCount) {
		this.likeCount = likeCount;
	}
	public long getMemberCount() {
		return memberCount;
	}
	public void setMemberCount(long memberCount) {
		this.memberCount = memberCount;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	public List<String> getDishes() {
		return dishes;
	}
	public void setDishes(List<String> dishes) {
		this.dishes = dishes;
	}
	public List<String> getRestaurants() {
		return restaurants;
	}
	public void setRestaurants(List<String> restaurants) {
		this.restaurants = restaurants;
	}
	public List<GeoPoint> getLocations() {
		return locations;
	}
	public void setLocations(List<GeoPoint> locations) {
		this.locations = locations;
	}

}
