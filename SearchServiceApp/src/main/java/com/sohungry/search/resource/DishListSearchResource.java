package com.sohungry.search.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sohungry.search.finder.DishListFinder;
import com.sohungry.search.model.DishList;
import com.sohungry.search.model.DishListSearchRequest;
import com.sohungry.search.model.DishListSearchResponse;

@RestController
@RequestMapping(value = "/search")
public class DishListSearchResource {
	
	@RequestMapping(value = "/dish", method = RequestMethod.POST, produces = {"application/json"})
    public DishListSearchResponse search(@RequestBody DishListSearchRequest searchRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        
		List<DishList> searchResults = new DishListFinder.Builder(searchRequest).build().find();
		DishListSearchResponse searchResponse = new DishListSearchResponse();
		searchResponse.setResults(searchResults);
		return searchResponse;
    }

}
