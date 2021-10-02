package it.one6n.pdfwebapp.configs;

import com.mongodb.ConnectionString;

/*
 * Add @Configuration for custom config
 */
public class MyMongoConfig extends BaseMongoConfig {

	@Override
	public ConnectionString getConnectionString() {
		return new ConnectionString(getConnectionUri());
	}
}