package com.sohungry.search.resource;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sohungry.search.domain.context.ApplicationContext;
import com.sohungry.search.domain.context.RestaurantRequestContext;
import com.sohungry.search.domain.context.builder.ApplicationContextBuilder;
import com.sohungry.search.domain.context.builder.RestaurantRequestContextBuilder;
import com.sohungry.search.model.Error;
import com.sohungry.search.model.RestaurantSearchRequest;
import com.sohungry.search.model.RestaurantSearchResponse;
import com.sohungry.search.request.validator.RestaurantSearchRequestValidator;
import com.sohungry.search.task.restaurant.GoogleRestaurantDedupeTask;
import com.sohungry.search.task.restaurant.RestaurantGoogleSearchTask;
import com.sohungry.search.task.restaurant.RestaurantNativeSearchTask;
import com.sohungry.search.task.restaurant.RestaurantSearchResponseBuilderTask;

import github.familysyan.concurrent.tasks.TaskConfiguration;
import github.familysyan.concurrent.tasks.orchestrator.Orchestrator;
import github.familysyan.concurrent.tasks.orchestrator.OrchestratorFactory;

@RestController
@RequestMapping(value = "/search/v2")
public class RestaurantSearchV2Resource {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RestaurantSearchV2Resource.class);
	
	
	@RequestMapping(value = "/restaurants", method = RequestMethod.POST, produces = {"application/json"})
    public RestaurantSearchResponse search(@RequestBody RestaurantSearchRequest searchRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        
		RestaurantSearchRequestValidator validator = new RestaurantSearchRequestValidator(searchRequest);
		if (!validator.validate()) {
			Error error = validator.getError();
			RestaurantSearchResponse searchResponse = new RestaurantSearchResponse();
			searchResponse.setError(error);
			response.sendError(400);
			return searchResponse;
		}
		ApplicationContext appContext = new ApplicationContextBuilder(request, 2).build();
		RestaurantRequestContext requestContext = new RestaurantRequestContextBuilder(searchRequest, appContext).build();
		ExecutorService executor = Executors.newFixedThreadPool(5);
		Orchestrator orchestrator = new Orchestrator.Builder(executor).build(); 
		OrchestratorFactory.setOrchestrator(orchestrator);
		RestaurantNativeSearchTask nativeSearchTask = new RestaurantNativeSearchTask(requestContext);
		orchestrator.acceptTask(nativeSearchTask);
		RestaurantSearchResponseBuilderTask responseBuilderTask = new RestaurantSearchResponseBuilderTask(requestContext);
		TaskConfiguration responseBuilderTC = new TaskConfiguration(responseBuilderTask);
		if (requestContext.getKeyword() != null && requestContext.getOffset() == 0) {
			RestaurantGoogleSearchTask googleSearchTask = new RestaurantGoogleSearchTask(requestContext);
			GoogleRestaurantDedupeTask dedupeTask = new GoogleRestaurantDedupeTask(requestContext);
			TaskConfiguration dedupeTC = new TaskConfiguration(dedupeTask).addDependency(nativeSearchTask).addDependency(googleSearchTask);
			orchestrator.acceptTask(googleSearchTask);
			orchestrator.acceptTask(dedupeTask, dedupeTC);
			responseBuilderTC.addDependency(dedupeTask);
		} else {
			responseBuilderTC.addDependency(nativeSearchTask);
		}
		orchestrator.acceptTask(responseBuilderTask, responseBuilderTC);
		RestaurantSearchResponse searchResponse = null;
		try {
			searchResponse = (RestaurantSearchResponse) orchestrator.getTaskResult(responseBuilderTask.getUniqueTaskId(), 5000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			LOGGER.error("Orchestrator failed", e);
			searchResponse = new RestaurantSearchResponse();
			Error error = new Error();
			error.setMessage("Backend error occured");
			searchResponse.setError(error);
			response.sendError(500);
		} 
		orchestrator.shutdown();
		return searchResponse;
    }
}