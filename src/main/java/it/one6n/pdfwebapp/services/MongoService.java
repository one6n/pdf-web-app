package it.one6n.pdfwebapp.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;

import it.one6n.pdfwebapp.repos.PdfEntryMongoRepo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Service
public class MongoService {

	@Autowired
	private MongoClient mongoClient;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private PdfEntryMongoRepo pdfEntryMongoRepo;

	public Set<String> getCollections() {
		Set<String> collections = getMongoTemplate().getCollectionNames();
		log.debug("collections={}", collections);
		return collections;
	}

	public void createCollection(String collectionName) {
		mongoTemplate.createCollection(collectionName);
	}
}
