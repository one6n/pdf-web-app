package it.one6n.pdfwebapp.configs;

import org.springframework.beans.factory.annotation.Value;

public class MyMongoConfig extends BaseMongoConfig {

	@Value("${mongo.database.name}")
	private String dbName;

	@Override
	protected String getDatabaseName() {
		return dbName;
	}
}
