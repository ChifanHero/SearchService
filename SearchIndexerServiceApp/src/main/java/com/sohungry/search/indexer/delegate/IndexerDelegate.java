package com.sohungry.search.indexer.delegate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.parse4j.ParseException;
import org.parse4j.ParseFile;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;

import com.sohungry.search.elastic.factory.ElasticsearchRestClientFactory;
import com.sohungry.search.index.Indices;
import com.sohungry.search.index.Types;
import com.sohungry.search.index.source.RestaurantSource;
import com.sohungry.search.index.source.document.GeoPoint;
import com.sohungry.search.index.source.document.Picture;
import com.sohungry.search.index.source.document.RestaurantDocument;
import com.sohungry.search.parse.config.ParseClass;

import io.searchbox.client.JestResult;
import io.searchbox.core.Index;

public class IndexerDelegate {
		
	public JestResult indexRestaurant(RestaurantSource source) {
		RestaurantDocument document = new RestaurantDocument();
		document.setObjectId(source.getObjectId());
		document.setName(source.getName());
		document.setEnglishName(source.getEnglishName());
		document.setAddress(source.getAddress());
		document.setPhone(source.getPhone());
		document.setCreatedAt(source.getCreatedAt());
		document.setUpdatedAt(source.getUpdatedAt());
		document.setLikeCount(source.getLikeCount());
		document.setDislikeCount(source.getDislikeCount());
		document.setNeutralCount(source.getNeutralCount());
		if (source.getCoordinates() != null && source.getCoordinates().getLatitude() != null && source.getCoordinates().getLongitude() != null) {
			GeoPoint coordinates = new GeoPoint();
			coordinates.setLat(source.getCoordinates().getLatitude());
			coordinates.setLon(source.getCoordinates().getLongitude());
			document.setCoordinates(coordinates);
		}
		ParseQuery<ParseObject> query = ParseQuery.getQuery(source.getPicture().getClassName());
		try {
			ParseObject pictureObj = query.get(source.getPicture().getObjectId());
			ParseFile original = pictureObj.getParseFile("original");
			ParseFile thumbnail = pictureObj.getParseFile("thumbnail");
			if (original != null || thumbnail != null) {
				Picture picture = new Picture();
				picture.setOriginal(original.getUrl());
				picture.setThumbnail(thumbnail.getUrl());
				document.setPicture(picture);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		query = ParseQuery.getQuery(ParseClass.DISH);
		ParseQuery<?> innerQuery = ParseQuery.getQuery(ParseClass.RESTAURANT);
		innerQuery.whereEqualTo("objectId", source.getObjectId());
		query.whereMatchesQuery("from_restaurant", innerQuery);
		query.limit(1000);
		try {
			List<ParseObject> dishes = query.find();
			if (dishes != null && dishes.size() > 0) {
				List<String> dishNames = new ArrayList<String>();
				for (ParseObject dish : dishes) {
					if (dish.getString("name") != null) {
						dishNames.add(dish.getString("name"));
					}
					if (dish.getString("english_name") != null) {
						dishNames.add(dish.getString("english_name"));
					}
				}
				document.setDishes(dishNames);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return indexRestaurantDocument(document);
	}
	
	private JestResult indexRestaurantDocument(RestaurantDocument document) {
		if (document == null) return null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			String doc = mapper.writeValueAsString(document);
			Index index = new Index.Builder(doc).index(Indices.FOOD).type(Types.RESTAURANT).build();
			return ElasticsearchRestClientFactory.getRestClient().execute(index);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

	public void indexRestaurants() {
		
	}

}
