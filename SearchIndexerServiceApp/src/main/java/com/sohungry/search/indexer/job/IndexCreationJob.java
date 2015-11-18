package com.sohungry.search.indexer.job;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.springframework.stereotype.Component;

import com.sohungry.search.elastic.factory.ElasticsearchRestClientFactory;
import com.sohungry.search.index.Indices;
import com.sohungry.search.index.Types;

import io.searchbox.client.JestClient;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.mapping.PutMapping;
import io.searchbox.indices.type.TypeExist;

/**
 * On service startup, check if necessary indices are existing. If not, create
 * indices with predefined settings (src/main/resources/settings/index_settings.json)
 * 
 * @author shiyan
 */
@Component
public class IndexCreationJob {

	@PostConstruct
	public void createIndex() throws IOException {
		JestClient client = ElasticsearchRestClientFactory.getRestClient();
		if (!indexExist(Indices.FOOD)) {
			
			String settings = readFile("/settings/index_settings.json");
			try {
				client.execute(new CreateIndex.Builder(Indices.FOOD)
						.settings(ImmutableSettings.builder()
								.loadFromSource(settings).build().getAsMap())
						.build());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!isTypeExist(Indices.FOOD, Types.RESTAURANT)) {
			putRestaurantMappings(client);
		}
		if (!isTypeExist(Indices.FOOD, Types.DISH)) {
			putDishMappings(client);
		}
	}
	
	private void putDishMappings(JestClient client) throws IOException {
		String mappings = readFile("/mappings/dish_mappings.json");
		PutMapping putMapping = new PutMapping.Builder(Indices.FOOD, Types.DISH, mappings).build();
		client.execute(putMapping);
	}

	private void putRestaurantMappings(JestClient client) throws IOException {
		String mappings = readFile("/mappings/restaurant_mappings.json");
		PutMapping putMapping = new PutMapping.Builder(Indices.FOOD, Types.RESTAURANT, mappings).build();
		client.execute(putMapping);
	}
	

	private boolean indexExist(String indexName) {
		JestClient client = ElasticsearchRestClientFactory.getRestClient();
		try {
			boolean indexExists = client.execute(new IndicesExists.Builder(indexName).build()).isSucceeded();
			return indexExists;
		} catch (IOException e) {
			return true; // status unknown, return true to prevent unexpected index creation
		}

	}
	
	private boolean isTypeExist(String indexName, String typeName) {
		JestClient client = ElasticsearchRestClientFactory.getRestClient();
		try {
			boolean indexExists = client.execute(new TypeExist.Builder(indexName).addType(typeName).build()).isSucceeded();
			return indexExists;
		} catch (IOException e) {
			return true; // status unknown, return true to prevent unexpected type creation
		}
	}
	
	private String readFile(String filePath) {
		String result = "";
		try {
		    result = IOUtils.toString(this.getClass().getResourceAsStream(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return result;
	}

}
