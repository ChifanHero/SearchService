package com.sohungry.search.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.sohungry.search.parse.util.ParseValidator;

import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;

public class RestaurantIndexer {
		
	public JestResult indexRestaurant(RestaurantSource source) {
		if (source == null) return null;
		List<RestaurantSource> sources = new ArrayList<RestaurantSource>();
		sources.add(source);
		List<RestaurantDocument> documents = convertSourcesToDocuments(sources);
		if (documents == null || documents.size() == 0) {
			return null;
		} else {
			return indexRestaurantDocument(documents.get(0));
		}
		
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
	
	private List<RestaurantDocument> convertSourcesToDocuments(List<RestaurantSource> sources) {
		if (sources == null || sources.isEmpty()) {
			return Collections.emptyList();
		} else {
			List<RestaurantDocument> documents = new ArrayList<RestaurantDocument>();
			Map<String, RestaurantDocument> imagesToFetch = new HashMap<String, RestaurantDocument>();
			Map<String, RestaurantDocument> restaurantToDoc = new HashMap<String, RestaurantDocument>();
			for (RestaurantSource source : sources) {
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
				if (ParseValidator.isValidPointer(source.getPicture())) {
					imagesToFetch.put(source.getPicture().getObjectId(), document);
				}
				restaurantToDoc.put(source.getObjectId(), document);
				documents.add(document);
			}
			ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClass.image);
			query.whereContainedIn("objectId", imagesToFetch.keySet());
			try {
				List<ParseObject> images = query.find();
				assembleImages(images, imagesToFetch);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			query = ParseQuery.getQuery(ParseClass.DISH);
			ParseQuery<?> innerQuery = ParseQuery.getQuery(ParseClass.RESTAURANT);
			innerQuery.whereContainedIn("objectId", restaurantToDoc.keySet());
			query.whereMatchesQuery("from_restaurant", innerQuery);
			query.limit(500);
			boolean fetchMore = true;
			while (fetchMore) {
				try {
					List<ParseObject> dishes = query.find();
					assembleDishes(dishes, restaurantToDoc);
					if (dishes == null || dishes.size() < 500) {
						fetchMore = false;
					} else {
						query.skip(query.getSkip() + 500);
					}
				} catch (ParseException e) {
					fetchMore = false;
				}	
			}
			return documents;
		}
	}
	
	private void assembleDishes(List<ParseObject> dishes, Map<String, RestaurantDocument> restaurantToDoc) {
		if (dishes == null || dishes.isEmpty() || restaurantToDoc == null || restaurantToDoc.isEmpty()) {
			return;
		}
		for (ParseObject dish : dishes) {
			ParseObject fromRestaurant = dish.getParseObject("from_restaurant");
			if (ParseValidator.isValidObject(fromRestaurant)) {
				RestaurantDocument document = restaurantToDoc.get(fromRestaurant.getObjectId());
				List<String> dishNames = document.getDishes();
				if (dishNames == null) {
					dishNames = new ArrayList<String>();
					document.setDishes(dishNames);
				}
				if (dish.getString("name") != null) {
					dishNames.add(dish.getString("name"));
				}
				if (dish.getString("english_name") != null) {
					dishNames.add(dish.getString("english_name"));
				}
			}
		}
		
	}

	private void assembleImages(List<ParseObject> images, Map<String, RestaurantDocument> imagesToFetch) {
		if (images == null || images.isEmpty() || imagesToFetch == null || imagesToFetch.isEmpty()) {
			return;
		}
		for (ParseObject image : images) {
			ParseFile original = image.getParseFile("original");
			ParseFile thumbnail = image.getParseFile("thumbnail");
			if (original != null || thumbnail != null) {
				RestaurantDocument document = imagesToFetch.get(image.getObjectId());
				Picture picture = new Picture();
				picture.setOriginal(original.getUrl());
				picture.setThumbnail(thumbnail.getUrl());
				document.setPicture(picture);
			}
		}
	}

	public JestResult indexRestaurants(List<RestaurantSource> sources) {
		if (sources == null || sources.size() == 0) {
			return null;
		} else {
			List<RestaurantDocument> documents = convertSourcesToDocuments(sources);
			if (documents == null || documents.size() == 0) {
				return null;
			} else {
				return bulkIndexRestaurantDocument(documents);
			}
		}
	}

	private JestResult bulkIndexRestaurantDocument(List<RestaurantDocument> documents) {
		if (documents == null || documents.size() <= 0) {
			return null;
		} 
		ObjectMapper mapper = new ObjectMapper();
		List<Index> actions = new ArrayList<Index>();
		for (RestaurantDocument document : documents) {
			String doc = null;
			try {
				doc = mapper.writeValueAsString(document);
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
			if (doc != null) {
				Index index = new Index.Builder(doc).build();
				actions.add(index);
			}
		}
		if (actions.size() > 0) {
			Bulk bulk = new Bulk.Builder().defaultIndex(Indices.FOOD).defaultType(Types.RESTAURANT).addAction(actions).build();
			try {
				return ElasticsearchRestClientFactory.getRestClient().execute(bulk);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		return null;
	}

}
