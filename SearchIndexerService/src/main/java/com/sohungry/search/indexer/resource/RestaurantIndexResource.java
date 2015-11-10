package com.sohungry.search.indexer.resource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sohungry.search.entity.resource.RestaurantResource;


@RestController
@RequestMapping(value = "/index/restaurant")
public class RestaurantIndexResource {
	
	@RequestMapping(value = "/single", method = RequestMethod.POST, produces = {"application/json"})
    public void greeting(@RequestBody RestaurantResource restaurant) {
		int i = 0;
		int j = 1;
        
    }

}
