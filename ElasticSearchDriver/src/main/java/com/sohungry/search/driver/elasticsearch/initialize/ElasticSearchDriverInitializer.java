package com.sohungry.search.driver.elasticsearch.initialize;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.sohungry.search.driver.elasticsearch.factory.ElasticsearchRestClientFactory;

/**
 * Initialize Elasticsearch Client on startup and share it on all threads
 * @author shiyan
 */
@Component
public class ElasticSearchDriverInitializer {
	
	@PostConstruct
	public void initialize() {
		System.out.print("driver initialized");
		ElasticsearchRestClientFactory.initializeElasticsearchClient();
	}

}
