package com.sohungry.search.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sohungry.search.model.DishList;
import com.sohungry.search.model.DishListSearchRequest;
import com.sohungry.search.model.DishListSearchResponse;
import com.sohungry.search.v1.finder.DishListFinder;

@RestController
@RequestMapping(value = "/search")
public class DishListSearchResource {
	
	@RequestMapping(value = "/lists", method = RequestMethod.POST, produces = {"application/json"})
    public DishListSearchResponse search(@RequestBody DishListSearchRequest searchRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        
		boolean debugMode = "1".equals(request.getHeader("debugMode"));
		String language = request.getLocale().getLanguage();
		List<DishList> searchResults = new DishListFinder.Builder(searchRequest).setDebugMode(debugMode).setLanguage(language).build().find();
		DishListSearchResponse searchResponse = new DishListSearchResponse();
		searchResponse.setResults(searchResults);
		return searchResponse;
    }

}
