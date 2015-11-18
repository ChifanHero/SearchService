package com.sohungry.search.indexer.resource;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sohungry.search.index.response.Response;
import com.sohungry.search.index.source.DishSource;
import com.sohungry.search.indexer.DishIndexer;

import io.searchbox.client.JestResult;

@RestController
@RequestMapping(value = "/index/dish")
public class DishIndexResource {
	
	@RequestMapping(value = "/single", method = RequestMethod.POST, produces = {"application/json"})
    public Response index(@RequestBody DishSource source) {
		Response response = new Response();
		JestResult jestResult = new DishIndexer().indexDish(source);
		if (jestResult != null) {
			response.setSuccess(jestResult.isSucceeded());
			response.setErrorMessage(jestResult.getErrorMessage());
		}
        return response;
    }
	
	@RequestMapping(value = "/bulk", method = RequestMethod.POST, produces = {"application/json"})
    public Response index(@RequestBody List<DishSource> sources) {
		Response response = new Response();
		JestResult jestResult = new DishIndexer().indexDishes(sources);
		if (jestResult != null) {
			response.setSuccess(jestResult.isSucceeded());
			response.setErrorMessage(jestResult.getErrorMessage());
		}
        return response;
    }
}