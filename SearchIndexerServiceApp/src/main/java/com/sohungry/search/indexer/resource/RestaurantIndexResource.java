package com.sohungry.search.indexer.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sohungry.search.index.response.Response;
import com.sohungry.search.index.source.RestaurantSource;
import com.sohungry.search.indexer.RestaurantIndexer;

import io.searchbox.client.JestResult;


@RestController
@RequestMapping(value = "/index/restaurant")
public class RestaurantIndexResource {
	
	@RequestMapping(value = "/single", method = RequestMethod.POST, produces = {"application/json"})
    public Response index(@RequestBody RestaurantSource source, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		Response response = new Response();
		if (source.getObjectId() == null || source.getObjectId().isEmpty()) {
			response.setErrorMessage("The incoming object is invalid");
			httpResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
			return response;
		}
		JestResult jestResult = new RestaurantIndexer().indexRestaurant(source);
		if (jestResult != null) {
			response.setSuccess(jestResult.isSucceeded());
			response.setErrorMessage(jestResult.getErrorMessage());
		}
        return response;
    }
	
	@RequestMapping(value = "/bulk", method = RequestMethod.POST, produces = {"application/json"})
    public Response index(@RequestBody List<RestaurantSource> sources, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		Response response = new Response();
		JestResult jestResult = new RestaurantIndexer().indexRestaurants(sources);
		if (jestResult != null) {
			response.setSuccess(jestResult.isSucceeded());
			response.setErrorMessage(jestResult.getErrorMessage());
		}
        return response;
    }
	
	@RequestMapping(value = "/single/{id}", method = RequestMethod.DELETE, produces = {"application/json"})
    public Response delete(@PathVariable("id") String id, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		Response response = new Response();
		if (id == null || id.isEmpty()) {
			httpResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
			return response;
		}
		JestResult jestResult = new RestaurantIndexer().deleteRestaurant(id);
		if (jestResult != null) {
			response.setSuccess(jestResult.isSucceeded());
			response.setErrorMessage(jestResult.getErrorMessage());
		}
        return response;
    }
}
