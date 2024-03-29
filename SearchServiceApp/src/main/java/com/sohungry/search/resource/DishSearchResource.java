package com.sohungry.search.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sohungry.search.model.Dish;
import com.sohungry.search.model.DishSearchRequest;
import com.sohungry.search.model.DishSearchResponse;
import com.sohungry.search.v1.finder.DishFinder;

@RestController
@RequestMapping(value = "/search")
public class DishSearchResource {
	
	@RequestMapping(value = "/dishes", method = RequestMethod.POST, produces = {"application/json"})
    public DishSearchResponse search(@RequestBody DishSearchRequest searchRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        
		boolean debugMode = "1".equals(request.getHeader("debugMode"));
		String language = request.getLocale().getLanguage();
		List<Dish> searchResults = new DishFinder.Builder(searchRequest).setDebugMode(debugMode).setLanguage(language).build().find();
		DishSearchResponse searchResponse = new DishSearchResponse();
		searchResponse.setResults(searchResults);
		return searchResponse;
    }

}
