package com.sohungry.search.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Dish {
	
	private String id;
	private String name;
	
	@JsonProperty("english_name")
	private String englishName;
	
	@JsonProperty("farovite_count")
	private long favoriteCount;
	
	@JsonProperty("like_count")
	private long likeCount;
	
	@JsonProperty("dislike_count")
	private long dislikeCount;
	
	@JsonProperty("neutral_count")
	private long neutralCount;
	private Picture picture;
	
	@JsonProperty("from_restaurant")
	private Restaurant fromRestaurant;
	
	@JsonProperty("related_lists")
	private List<DishList> relatedLists;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public Picture getPicture() {
		return picture;
	}
	public void setPicture(Picture picture) {
		this.picture = picture;
	}
	public Restaurant getFromRestaurant() {
		return fromRestaurant;
	}
	public void setFromRestaurant(Restaurant fromRestaurant) {
		this.fromRestaurant = fromRestaurant;
	}
	public List<DishList> getRelatedLists() {
		return relatedLists;
	}
	public void setRelatedLists(List<DishList> relatedLists) {
		this.relatedLists = relatedLists;
	}

}
