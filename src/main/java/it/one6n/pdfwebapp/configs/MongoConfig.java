package it.one6n.pdfwebapp.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoRepositories(basePackages = "it.one6n.pdfwebapp.repos")
public class MongoConfig extends AbstractMongoClientConfiguration {

	@Value("${mongo.connection.uri}")
	private String connectionUri;
	@Value("${mongo.database.name}")
	private String dbName;

	@Override
	public String getDatabaseName() {
		return dbName;
	}

	@Bean
	public MongoClient mongoClient() {
		ConnectionString connectionString = new ConnectionString(
				String.format("mongodb://%s/%s", connectionUri, dbName));
		MongoClientSettings mongoClientSettings = MongoClientSettings.builder().applyConnectionString(connectionString)
				.build();
		return MongoClients.create(mongoClientSettings);
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongoClient(), getDatabaseName());
	}
}
