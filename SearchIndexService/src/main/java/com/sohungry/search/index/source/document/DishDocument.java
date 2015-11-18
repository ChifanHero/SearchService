package com.sohungry.search.index.source.document;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.sohungry.search.index.source.document.simplified.SimplifiedMenu;
import com.sohungry.search.index.source.document.simplified.SimplifiedRestaurant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DishDocument {
	
	private String objectId;
	private String name;
	
	@JsonProperty("english_name")
	private String englishName;
	
	private Picture picture;
	
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
	
	private SimplifiedRestaurant fromRestaurant;
	private SimplifiedMenu menu;
	private List<DishList> lists;
	
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
	public Picture getPicture() {
		return picture;
	}
	public void setPicture(Picture picture) {
		this.picture = picture;
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
	public SimplifiedRestaurant getFromRestaurant() {
		return fromRestaurant;
	}
	public void setFromRestaurant(SimplifiedRestaurant fromRestaurant) {
		this.fromRestaurant = fromRestaurant;
	}
	public SimplifiedMenu getMenu() {
		return menu;
	}
	public void setMenu(SimplifiedMenu menu) {
		this.menu = menu;
	}
	public List<DishList> getLists() {
		return lists;
	}
	public void setLists(List<DishList> lists) {
		this.lists = lists;
	}
	

}
