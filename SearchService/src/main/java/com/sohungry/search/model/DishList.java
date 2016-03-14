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
public class DishList {
	
	private String id;
	private String name;
	
	@JsonProperty("member_count")
	private Long memberCount;
	
	private List<String> dishes;
	
	@JsonProperty("farovite_count")
	private Long favoriteCount;
	
	@JsonProperty("like_count")
	private Long likeCount;

	private Picture picture;
	
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

	public Long getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(Long memberCount) {
		this.memberCount = memberCount;
	}
	
	public List<String> getDishes() {
		return dishes;
	}

	public void setDishes(List<String> dishes) {
		this.dishes = dishes;
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

	public Picture getPicture() {
		return picture;
	}

	public void setPicture(Picture picture) {
		this.picture = picture;
	}

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
