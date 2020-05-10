package com.app.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
	@Value( "${spring.data.mongodb.username}" )
	private String user; // the user name
	@Value( "${spring.data.mongodb.authentication-database}" )
	private String authDatabase; // the name of the database in which the user is defined
	@Value( "${spring.data.mongodb.password}" )
	private char[] password; // the password as a character array
	@Value( "${spring.data.mongodb.database}" )
	private String dataBaseName;
	
	@Override
	public MongoClient mongoClient() {
		MongoCredential credential = MongoCredential.createCredential(user, authDatabase, password);
		MongoClientSettings settings = MongoClientSettings.builder().credential(credential)
				.applyToSslSettings(builder -> builder.enabled(false))
				.applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress("localhost", 27017))))
				.build();
		return MongoClients.create(settings);
	}

	@Override
	protected String getDatabaseName() {
		return dataBaseName;
	}

}
