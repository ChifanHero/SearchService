package com.sohungry.search.indexer.job;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * On service startup, check if necessary indices are existing. If not, create indices with predefined settings
 * @author shiyan
 */
@Component
public class IndexCreationJob{

	@PostConstruct
	public void createIndex() {
		
	}
	

}
