package com.sohungry.search.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sohungry.search.finder.RestaurantFinder;
import com.sohungry.search.model.Restaurant;
import com.sohungry.search.model.RestaurantSearchRequest;
import com.sohungry.search.model.RestaurantSearchResponse;

@RestController
@RequestMapping(value = "/search")
public class RestaurantSearchResource {
	
	
	@RequestMapping(value = "/restaurants", method = RequestMethod.POST, produces = {"application/json"})
    public RestaurantSearchResponse search(@RequestBody RestaurantSearchRequest searchRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        
		boolean debugMode = "1".equals(request.getHeader("debugMode"));
		String language = request.getLocale().getLanguage();
		List<Restaurant> searchResults = new RestaurantFinder.Builder(searchRequest).setDebugMode(debugMode).setLanguage(language).build().find();
		RestaurantSearchResponse searchResponse = new RestaurantSearchResponse();
		searchResponse.setResults(searchResults);
		return searchResponse;
    }
}