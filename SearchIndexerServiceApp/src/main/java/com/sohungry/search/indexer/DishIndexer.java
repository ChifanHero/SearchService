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
import com.sohungry.search.index.source.DishSource;
import com.sohungry.search.index.source.document.DishDocument;
import com.sohungry.search.index.source.document.shared.GeoPoint;
import com.sohungry.search.index.source.document.shared.Picture;
import com.sohungry.search.index.source.document.simplified.SimplifiedDishList;
import com.sohungry.search.index.source.document.simplified.SimplifiedMenu;
import com.sohungry.search.index.source.document.simplified.SimplifiedRestaurant;
import com.sohungry.search.parse.config.ParseClass;
import com.sohungry.search.parse.util.ParseValidator;

import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;

public class DishIndexer {

	public JestResult indexDish(DishSource source) {
		if (source == null) return null;
		List<DishSource> sources = new ArrayList<DishSource>();
		sources.add(source);
		List<DishDocument> documents = convertSourcesToDocuments(sources);
		if (documents == null || documents.size() == 0) {
			return null;
		} else {
			return indexDishDocument(documents.get(0));
		}
	}

	private JestResult indexDishDocument(DishDocument dishDocument) {
		if (dishDocument == null) return null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			String doc = mapper.writeValueAsString(dishDocument);
			Index index = new Index.Builder(doc).index(Indices.FOOD).type(Types.DISH).build();
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

	private List<DishDocument> convertSourcesToDocuments(List<DishSource> sources) {
		if (sources == null || sources.isEmpty()) {
			return Collections.emptyList();
		} else {
			List<DishDocument> documents = new ArrayList<DishDocument>();
			Map<String, DishDocument> imagesToFetch = new HashMap<String, DishDocument>();
			Map<String, DishDocument> restaurantsToFetch = new HashMap<String, DishDocument>();
			Map<String, DishDocument> menusToFetch = new HashMap<String, DishDocument>();

			Map<String, DishDocument> dishToDoc = new HashMap<String, DishDocument>();
			
			for (DishSource source : sources) {
				DishDocument document = new DishDocument();
				document.setObjectId(source.getObjectId());
				document.setName(source.getName());
				document.setEnglishName(source.getEnglishName());
				document.setCreatedAt(source.getCreatedAt());
				document.setUpdatedAt(source.getUpdatedAt());
				document.setLikeCount(source.getLikeCount());
				document.setDislikeCount(source.getDislikeCount());
				document.setNeutralCount(source.getNeutralCount());
				if (ParseValidator.isValidPointer(source.getPicture())) {
					imagesToFetch.put(source.getPicture().getObjectId(), document);
				}
				if (ParseValidator.isValidPointer(source.getFromRestaurant())) {
					restaurantsToFetch.put(source.getFromRestaurant().getObjectId(), document);
				}
				if (ParseValidator.isValidPointer(source.getMenu())) {
					menusToFetch.put(source.getMenu().getObjectId(), document);

				}
				dishToDoc.put(source.getObjectId(), document);
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
			query = ParseQuery.getQuery(ParseClass.RESTAURANT);
			query.whereContainedIn("objectId", restaurantsToFetch.keySet());
			try {
				List<ParseObject> restaurants = query.find();
				assembleRestaurants(restaurants, restaurantsToFetch);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			query = ParseQuery.getQuery(ParseClass.MENU);
			query.whereContainedIn("objectId", menusToFetch.keySet());
			try {
				List<ParseObject> menus = query.find();
				assembleMenus(menus, menusToFetch);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			query = ParseQuery.getQuery(ParseClass.LIST_MEMBER);
			ParseQuery<?> innerQuery = ParseQuery.getQuery(ParseClass.DISH);
			innerQuery.whereContainedIn("objectId", dishToDoc.keySet());
			query.whereMatchesQuery("dish", innerQuery);
			query.limit(500);
			boolean fetchMore = true;
			while (fetchMore) {
				try {
					List<ParseObject> listMembers = query.find();
					assembleLists(listMembers, dishToDoc);
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

	private void assembleLists(List<ParseObject> listMembers, Map<String, DishDocument> dishToDoc) {
		if (listMembers == null || listMembers.size() <= 0 || dishToDoc == null || dishToDoc.size() <= 0) return;
		for (ParseObject listMember : listMembers) {
			ParseObject dish = listMember.getParseObject("dish");
			ParseObject list = listMember.getParseObject("list");
			try {
				list.fetchIfNeeded();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (dish == null || list == null) {
				continue;
			} else {
				DishDocument document = dishToDoc.get(dish.getObjectId());
				List<SimplifiedDishList> lists = document.getLists();
				if (lists == null) {
					lists = new ArrayList<SimplifiedDishList>();
					document.setLists(lists);
				}
				SimplifiedDishList dishList = new SimplifiedDishList();
				dishList.setObjectId(list.getObjectId());
				dishList.setName(list.getString("name"));
				lists.add(dishList);
			}
		}
		
	}

	private void assembleMenus(List<ParseObject> menus, Map<String, DishDocument> menusToFetch) {
		if (menus == null || menus.isEmpty() || menusToFetch == null || menusToFetch.isEmpty()) {
			return;
		}
		for (ParseObject menu : menus) {
			SimplifiedMenu menuItem = new SimplifiedMenu();
			menuItem.setName(menu.getString("name"));
			menuItem.setEnglishName(menu.getString("english_name"));
			menuItem.setObjectId(menu.getObjectId());
			DishDocument document = menusToFetch.get(menu.getObjectId());
			document.setMenu(menuItem);
		}
	}

	private void assembleRestaurants(List<ParseObject> restaurants, Map<String, DishDocument> restaurantsToFetch) {
		if (restaurants == null || restaurants.isEmpty() || restaurantsToFetch == null || restaurantsToFetch.isEmpty()) {
			return;
		}
		for (ParseObject restaurant : restaurants) {
			SimplifiedRestaurant rest = new SimplifiedRestaurant();
			rest.setName(restaurant.getString("name"));
			rest.setEnglishName(restaurant.getString("english_name"));
			rest.setObjectId(restaurant.getObjectId());
			if (restaurant.getParseGeoPoint("coordinates") != null) {
				GeoPoint coordinates = new GeoPoint();
				coordinates.setLat(restaurant.getParseGeoPoint("coordinates").getLatitude());
				coordinates.setLon(restaurant.getParseGeoPoint("coordinates").getLongitude());
				rest.setCoordinates(coordinates);
			}
			DishDocument document = restaurantsToFetch.get(restaurant.getObjectId());
			document.setFromRestaurant(rest);
		}
	}

	private void assembleImages(List<ParseObject> images, Map<String, DishDocument> imagesToFetch) {
		if (images == null || images.isEmpty() || imagesToFetch == null || imagesToFetch.isEmpty()) {
			return;
		}
		for (ParseObject image : images) {
			ParseFile original = image.getParseFile("original");
			ParseFile thumbnail = image.getParseFile("thumbnail");
			if (original != null || thumbnail != null) {
				DishDocument document = imagesToFetch.get(image.getObjectId());
				Picture picture = new Picture();
				picture.setOriginal(original.getUrl());
				picture.setThumbnail(thumbnail.getUrl());
				document.setPicture(picture);
			}
		}
	}

	public JestResult indexDishes(List<DishSource> sources) {
		if (sources == null || sources.size() == 0) {
			return null;
		} else {
			List<DishDocument> documents = convertSourcesToDocuments(sources);
			if (documents == null || documents.size() == 0) {
				return null;
			} else {
				return bulkIndexDishDocument(documents);
			}
		}
	}

	private JestResult bulkIndexDishDocument(List<DishDocument> documents) {
		if (documents == null || documents.size() <= 0) {
			return null;
		} 
		ObjectMapper mapper = new ObjectMapper();
		List<Index> actions = new ArrayList<Index>();
		for (DishDocument document : documents) {
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
			Bulk bulk = new Bulk.Builder().defaultIndex(Indices.FOOD).defaultType(Types.DISH).addAction(actions).build();
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
