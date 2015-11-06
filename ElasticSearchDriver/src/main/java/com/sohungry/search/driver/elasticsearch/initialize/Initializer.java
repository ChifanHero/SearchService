package com.sohungry.search.driver.elasticsearch.initialize;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.sohungry.search.driver.elasticsearch.factory.ElasticsearchRestClientFactory;

/**
 * Initialize Elasticsearch Client on startup and share it on all threads
 * @author shiyan
 */
@Component
public class Initializer {
	
	@PostConstruct
	public void initialize() {
		ElasticsearchRestClientFactory.initializeElasticsearchClient();
	}
}
