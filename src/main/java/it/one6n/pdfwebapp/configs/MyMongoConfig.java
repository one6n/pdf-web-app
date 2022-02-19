package it.one6n.pdfwebapp.configs;

import org.springframework.context.annotation.Configuration;

import com.mongodb.ConnectionString;

/*
 * Add @Configuration for custom config
 */
@Configuration
public class MyMongoConfig extends BaseMongoConfig {

	@Override
	public ConnectionString getConnectionString() {
		return new ConnectionString(getConnectionUri());
	}
}