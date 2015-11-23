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
import org.parse4j.ParseGeoPoint;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;

import com.sohungry.search.elastic.factory.ElasticsearchRestClientFactory;
import com.sohungry.search.index.Indices;
import com.sohungry.search.index.Types;
import com.sohungry.search.index.source.DishListSource;
import com.sohungry.search.index.source.document.DishListDocument;
import com.sohungry.search.index.source.document.shared.GeoPoint;
import com.sohungry.search.parse.config.ParseClass;

import io.searchbox.client.JestResult;
import io.searchbox.core.Index;

public class DishListIndexer {

	public JestResult indexDishList(DishListSource source) {
		if (source == null) return null;
		List<DishListSource> sources = new ArrayList<DishListSource>();
		sources.add(source);
		List<DishListDocument> documents = convertSourcesToDocuments(sources);
		if (documents == null || documents.size() == 0) {
			return null;
		} else {
			return indexDishListDocument(documents.get(0));
		}
	}

	private JestResult indexDishListDocument(DishListDocument dishListDocument) {
		if (dishListDocument == null) return null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			String doc = mapper.writeValueAsString(dishListDocument);
			Index index = new Index.Builder(doc).index(Indices.FOOD).type(Types.LIST).build();
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

	private List<DishListDocument> convertSourcesToDocuments(List<DishListSource> sources) {
		if (sources == null || sources.isEmpty()) {
			return Collections.emptyList();
		} else {
			List<DishListDocument> documents = new ArrayList<DishListDocument>();
//			Map<String, RestaurantDocument> imagesToFetch = new HashMap<String, RestaurantDocument>();
			Map<String, DishListDocument> listToDoc = new HashMap<String, DishListDocument>();
			for (DishListSource source : sources) {
				DishListDocument document = new DishListDocument();
				document.setObjectId(source.getObjectId());
				document.setName(source.getName());
				document.setCreatedAt(source.getCreatedAt());
				document.setUpdatedAt(source.getUpdatedAt());
				document.setLikeCount(source.getLikeCount());
				listToDoc.put(source.getObjectId(), document);
				documents.add(document);
			}
			ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClass.LIST_MEMBER);
			ParseQuery<?> innerQuery = ParseQuery.getQuery(ParseClass.DISH_LIST);
			innerQuery.whereContainedIn("objectId", listToDoc.keySet());
			query.whereMatchesQuery("list", innerQuery);
			query.limit(500);
			boolean fetchMore = true;
			while (fetchMore) {
				try {
					List<ParseObject> listMembers = query.find();
					assembleLists(listMembers, listToDoc);
					if (listMembers == null || listMembers.size() < 500) {
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

	private void assembleLists(List<ParseObject> listMembers, Map<String, DishListDocument> listToDoc) {
		if (listMembers == null || listMembers.size() <= 0 || listToDoc == null || listToDoc.size() <= 0) return;
		for (ParseObject listMember : listMembers) {
			ParseObject dish = listMember.getParseObject("dish");
			ParseObject list = listMember.getParseObject("list");
			ParseObject fromRestaurant = null;
			try {
				dish.fetchIfNeeded();
				fromRestaurant = dish.getParseObject("from_restaurant");
				fromRestaurant.fetchIfNeeded();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (dish == null || list == null) {
				continue;
			} else {
				DishListDocument document = listToDoc.get(list.getObjectId());
				fillDishNames(dish, document);
				if (fromRestaurant != null) {
					fillRestaurantNames(fromRestaurant, document);
					fillLocations(fromRestaurant, document);
				}
				
			}
		}
	}

	private void fillLocations(ParseObject fromRestaurant, DishListDocument document) {
		List<GeoPoint> locations = document.getLocations();
		if (locations == null) {
			locations = new ArrayList<GeoPoint>();
			document.setLocations(locations);
		}
		if (fromRestaurant.getParseGeoPoint("coordinates") != null) {
			ParseGeoPoint coordinates = fromRestaurant.getParseGeoPoint("coordinates");
			GeoPoint location = new GeoPoint();
			location.setLat(coordinates.getLatitude());
			location.setLon(coordinates.getLongitude());
			locations.add(location);
		}
		
	}

	private void fillRestaurantNames(ParseObject fromRestaurant, DishListDocument document) {
		List<String> restaurants = document.getRestaurants();
		if (restaurants == null) {
			restaurants = new ArrayList<String>();
			document.setRestaurants(restaurants);
		}
		String name = fromRestaurant.getString("name");
		String englishName = fromRestaurant.getString("english_name");
		restaurants.add(name);
		restaurants.add(englishName);
	}

	private void fillDishNames(ParseObject dish, DishListDocument document) {
		List<String> dishes = document.getDishes();
		if (dishes == null) {
			dishes = new ArrayList<String>();
			document.setDishes(dishes);
		}
		String dishName = dish.getString("name");
		String englishName = dish.getString("english_name");
		dishes.add(dishName);
		dishes.add(englishName);
	}

	public JestResult indexDishLists(List<DishListSource> sources) {
		if (sources == null || sources.size() == 0) {
			return null;
		} else {
			List<DishListDocument> documents = convertSourcesToDocuments(sources);
			if (documents == null || documents.size() == 0) {
				return null;
			} else {
				return bulkIndexDishListDocument(documents);
			}
		}
	}

	private JestResult bulkIndexDishListDocument(List<DishListDocument> documents) {
		// TODO Auto-generated method stub
		return null;
	}

}
