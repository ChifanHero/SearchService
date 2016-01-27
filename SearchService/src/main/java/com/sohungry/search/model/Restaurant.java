package com.sohungry.search.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Restaurant {
	
	private String id;
	private String name;
	
//	@JsonProperty("english_name")
//	private String englishName;
	private String address;
	private Distance distance;
	
	@JsonProperty("favorite_count")
	private Long favoriteCount;
	
	@JsonProperty("like_count")
	private Long likeCount;
	
	@JsonProperty("neutral_count")
	private Long neutralCount;
	
	@JsonProperty("dislike_count")
	private Long dislikeCount;
	private String phone;
	private String hours;
	private Picture picture;
//	private Map<String, List<String>> highlight;
	private List<String> dishes;
	private Map<String, List<String>> diagInfo;
	
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
//	public String getEnglishName() {
//		return englishName;
//	}
//	public void setEnglishName(String englishName) {
//		this.englishName = englishName;
//	}
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
	public Long getNeutralCount() {
		return neutralCount;
	}
	public void setNeutralCount(Long neutralCount) {
		this.neutralCount = neutralCount;
	}
	public Long getDislikeCount() {
		return dislikeCount;
	}
	public void setDislikeCount(Long dislikeCount) {
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
public List<String> getDishes() {
		return dishes;
	}
	public void setDishes(List<String> dishes) {
		this.dishes = dishes;
	}
	//	public Map<String, List<String>> getHighlight() {
//		return highlight;
//	}
//	public void setHighlight(Map<String, List<String>> highlight) {
//		this.highlight = highlight;
//	}
	public Map<String, List<String>> getDiagInfo() {
		return diagInfo;
	}
	public void setDiagInfo(Map<String, List<String>> diagInfo) {
		this.diagInfo = diagInfo;
	}
	public void addDiagInfo(String name, String value) {
		if (this.diagInfo == null) {
			this.diagInfo = new HashMap<String, List<String>>();
		}
		if (this.diagInfo.get(name) == null) {
			List<String> values = new ArrayList<String>();
			this.diagInfo.put(name, values);
		}
		this.diagInfo.get(name).add(value);
	}

}

