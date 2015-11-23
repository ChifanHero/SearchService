package com.sohungry.search.index.source;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.sohungry.search.index.source.shared.Pointer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class DishSource {
	
	private String createdAt;
	
	@JsonProperty("from_restaurant")
	private Pointer fromRestaurant;
	private Pointer menu;
	private String name;
	
	@JsonProperty("english_name")
	private String englishName;
	
	private String objectId;
	private String updatedAt;
	
	@JsonProperty("dislike_count")
	private long dislikeCount;
	
	@JsonProperty("favorite_count")
	private long favoriteCount;
	
	@JsonProperty("like_count")
	private long likeCount;
	
	@JsonProperty("neutral_count")
	private long neutralCount;
	
	private Pointer picture;

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public Pointer getFromRestaurant() {
		return fromRestaurant;
	}

	public void setFromRestaurant(Pointer fromRestaurant) {
		this.fromRestaurant = fromRestaurant;
	}

	public Pointer getMenu() {
		return menu;
	}

	public void setMenu(Pointer menu) {
		this.menu = menu;
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

	public long getDislikeCount() {
		return dislikeCount;
	}

	public void setDislikeCount(long dislikeCount) {
		this.dislikeCount = dislikeCount;
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

	public long getNeutralCount() {
		return neutralCount;
	}

	public void setNeutralCount(long neutralCount) {
		this.neutralCount = neutralCount;
	}

	public Pointer getPicture() {
		return picture;
	}

	public void setPicture(Pointer picture) {
		this.picture = picture;
	}

}
