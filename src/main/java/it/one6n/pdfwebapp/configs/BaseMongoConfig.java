package it.one6n.pdfwebapp.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import lombok.Getter;

@Getter
public abstract class BaseMongoConfig {

	@Value("${mongodb.connection.uri}")
	private String connectionUri;

	public abstract ConnectionString getConnectionString();

	@Bean
	public MongoClient mongoClient() {
		MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
				.applyConnectionString(getConnectionString()).build();

		return MongoClients.create(mongoClientSettings);
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongoClient(), getConnectionString().getDatabase());
	}
}