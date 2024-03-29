package com.sohungry.search.elastic.factory;

import com.sohungry.search.elastic.config.ElasticConfig;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

/**
 * Factory for ES <a href="https://github.com/searchbox-io/Jest">Jest Client</a>
 * 
 * @author shiyan
 */
public class ElasticsearchRestClientFactory {

	private static JestClientFactory factory;

	public static JestClient getRestClient() {
		if (factory == null) {
			initializeElasticsearchClient();
		}
		return factory.getObject();
	}

	public synchronized static void initializeElasticsearchClient() {
		if (factory == null) {
			factory = new JestClientFactory();
			factory.setHttpClientConfig(new HttpClientConfig.Builder(ElasticConfig.AWS_ENDPOINT).multiThreaded(true).build());
		}
	}

}
