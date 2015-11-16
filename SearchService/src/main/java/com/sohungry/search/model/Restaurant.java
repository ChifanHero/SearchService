package com.sohungry.search.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Restaurant {
	
	private String id;
	private String name;
	
	@JsonProperty("english_name")
	private String englishName;
	private String address;
	private Distance distance;
	
	@JsonProperty("favorite_count")
	private long favoriteCount;
	
	@JsonProperty("like_count")
	private long likeCount;
	
	@JsonProperty("neutral_count")
	private long neutralCount;
	
	@JsonProperty("dislike_count")
	private long dislikeCount;
	private String phone;
	private String hours;
	private Picture picture;
	
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Distance getDistance() {
		return distance;
	}
	public void setDistance(Distance distance) {
		this.distance = distance;
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
	public long getDislikeCount() {
		return dislikeCount;
	}
	public void setDislikeCount(long dislikeCount) {
		this.dislikeCount = dislikeCount;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getHours() {
		return hours;
	}
	public void setHours(String hours) {
		this.hours = hours;
	}
	public Picture getPicture() {
		return picture;
	}
	public void setPicture(Picture picture) {
		this.picture = picture;
	}

}

