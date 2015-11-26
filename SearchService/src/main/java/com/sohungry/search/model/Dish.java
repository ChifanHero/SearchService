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
	private Long favoriteCount;
	
	@JsonProperty("like_count")
	private Long likeCount;
	
	@JsonProperty("dislike_count")
	private Long dislikeCount;
	
	@JsonProperty("neutral_count")
	private Long neutralCount;
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
	public Long getFavoriteCount() {
		return favoriteCount;
	}
	public void setFavoriteCount(Long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}
	public Long getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(Long likeCount) {
		this.likeCount = likeCount;
	}
	public Long getDislikeCount() {
		return dislikeCount;
	}
	public void setDislikeCount(Long dislikeCount) {
		this.dislikeCount = dislikeCount;
	}
	public Long getNeutralCount() {
		return neutralCount;
	}
	public void setNeutralCount(Long neutralCount) {
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
