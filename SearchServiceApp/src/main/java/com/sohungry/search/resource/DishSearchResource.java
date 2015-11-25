package com.sohungry.search.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sohungry.search.finder.DishFinder;
import com.sohungry.search.model.Dish;
import com.sohungry.search.model.DishSearchRequest;
import com.sohungry.search.model.DishSearchResponse;

@RestController
@RequestMapping(value = "/search")
public class DishSearchResource {
	
	@RequestMapping(value = "/dish", method = RequestMethod.POST, produces = {"application/json"})
    public DishSearchResponse search(@RequestBody DishSearchRequest searchRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        
		List<Dish> searchResults = new DishFinder.Builder(searchRequest).build().find();
		DishSearchResponse searchResponse = new DishSearchResponse();
		searchResponse.setResults(searchResults);
		return searchResponse;
    }

}
