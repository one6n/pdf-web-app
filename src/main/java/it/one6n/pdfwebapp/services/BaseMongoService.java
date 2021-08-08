package it.one6n.pdfwebapp.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Service
public class BaseMongoService {

	@Autowired
	private MongoClient mongoClient;

	@Autowired
	private MongoTemplate mongoTemplate;

	public Set<String> getCollections() {
		Set<String> collections = getMongoTemplate().getCollectionNames();
		log.debug("collections={}", collections);
		return collections;
	}

	public void createCollection(String collectionName) {
		getMongoTemplate().createCollection(collectionName);
	}

	public void insertDocument(Object obj, String collectionName) {
		getMongoTemplate().insert(obj, collectionName);
	}

	public void saveDocument(Object obj, String collectionName) {
		getMongoTemplate().save(obj, collectionName);
	}

	public void removeDocument(Object obj, String collectionName) {
		getMongoTemplate().remove(obj, collectionName);
	}
}
