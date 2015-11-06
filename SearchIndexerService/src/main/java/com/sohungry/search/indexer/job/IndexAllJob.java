package com.sohungry.search.indexer.job;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * Periodically re-index all objects
 * @author shiyan
 *
 */
@Component
public class IndexAllJob{
	
	@PostConstruct
	public void scheduleJob() {
		
	}

}
