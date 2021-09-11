package it.one6n.pdfwebapp.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.result.DeleteResult;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Service
public class BaseMongoService {

	@Value("${mongo.database.name}")
	private String dbName;
	@Autowired
	private MongoClient mongoClient;
	@Autowired
	private MongoTemplate mongoTemplate;

	public Set<String> getCollections() {
		Set<String> collections = getMongoTemplate().getCollectionNames();
		return collections;
	}

	public void createCollection(String collectionName) {
		getMongoTemplate().createCollection(collectionName);
	}

	public GridFSBucket createBucket(String bucketName) {
		return GridFSBuckets.create(getMongoClient().getDatabase(getDbName()), bucketName);
	}

	/**
	 * This method perform an insert operation. Use saveDocument for upsert.
	 * 
	 * @param obj            Object to save
	 * @param collectionName name of the collection to use
	 * @return the inserted object
	 */
	public Object insertDocument(Object obj, String collectionName) {
		return getMongoTemplate().insert(obj, collectionName);
	}

	/**
	 * This method perform an upsert operation.
	 * 
	 * @param obj            Object to save
	 * @param collectionName name of the collection to use
	 * @return the inserted object
	 */
	public Object saveDocument(Object obj, String collectionName) {
		return getMongoTemplate().save(obj, collectionName);
	}

	public DeleteResult removeDocument(Object obj, String collectionName) {
		return getMongoTemplate().remove(obj, collectionName);
	}
}
