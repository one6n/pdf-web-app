package it.one6n.pdfwebapp.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories
public class MongoConfig {

	/*
	 * @Value("${mongo.connection.uri}") private String connectionUri;
	 * 
	 * @Value("${mongo.database.name}") private String dbName;
	 * 
	 * @Bean public MongoClient mongo() { ConnectionString connectionString = new
	 * ConnectionString( String.format("mongodb://%s/%s", connectionUri, dbName));
	 * MongoClientSettings mongoClientSettings =
	 * MongoClientSettings.builder().applyConnectionString(connectionString)
	 * .build();
	 * 
	 * return MongoClients.create(mongoClientSettings); }
	 * 
	 * @Bean public MongoTemplate mongoTemplate() throws Exception { return new
	 * MongoTemplate(mongo(), "pdfApp"); }
	 */
}
