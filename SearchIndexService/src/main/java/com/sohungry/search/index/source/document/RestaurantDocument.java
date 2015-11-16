package com.sohungry.search.index.source.document;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantDocument {
	private String objectId;
	private String name;
	
	@JsonProperty("english_name")
	private String englishName;
	
	private String address;
	private GeoPoint coordinates;
	private String phone;
	private Picture picture;
	private List<String> dishes;
	
	@JsonProperty("like_count")
	private long likeCount;
	
	@JsonProperty("dislike_count")
	private long dislikeCount;
	
	@JsonProperty("neutral_count")
	private long neutralCount;
	
	@JsonProperty("favorite_count")
	private long favoriteCount;
	
	private String createdAt;
	private String updatedAt;
	
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public GeoPoint getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(GeoPoint coordinates) {
		this.coordinates = coordinates;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Picture getPicture() {
		return picture;
	}
	public void setPicture(Picture picture) {
		this.picture = picture;
	}
	public List<String> getDishes() {
		return dishes;
	}
	public void setDishes(List<String> dishes) {
		this.dishes = dishes;
	}
	public long getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(long likeCount) {
		this.likeCount = likeCount;
	}
	public long getDislikeCount() {
		return dislikeCount;
	}
	public void setDislikeCount(long dislikeCount) {
		this.dislikeCount = dislikeCount;
	}
	public long getNeutralCount() {
		return neutralCount;
	}
	public void setNeutralCount(long neutralCount) {
		this.neutralCount = neutralCount;
	}
	public long getFavoriteCount() {
		return favoriteCount;
	}
	public void setFavoriteCount(long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

}
