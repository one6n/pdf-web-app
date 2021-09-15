package it.one6n.pdfwebapp.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public abstract class BaseMongoConfig extends AbstractMongoClientConfiguration {

	@Override
	protected abstract String getDatabaseName();

	@Bean
	public MongoClient mongoClient() {
		return MongoClients.create();
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongoClient(), getDatabaseName());
	}
}
