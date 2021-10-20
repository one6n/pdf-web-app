package it.one6n.pdfwebapp.services;

import java.io.InputStream;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;

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
	@Autowired
	private GridFsTemplate gridFsTemplate;

	public Set<String> getCollections() {
		Set<String> collections = getMongoTemplate().getCollectionNames();
		return collections;
	}

	public void createCollection(String collectionName) {
		getMongoTemplate().createCollection(collectionName);
	}

	public <T> void dropCollection(Class<T> entityClass) {
		getMongoTemplate().dropCollection(entityClass);
	}

	public void dropCollection(String collectionName) {
		getMongoTemplate().dropCollection(collectionName);
	}

	public GridFSBucket findOrCreateBucket(String bucketName) {
		return GridFSBuckets.create(getMongoTemplate().getDb(), bucketName);
	}

	/**
	 * This method perform an insert operation. Use saveDocument for upsert.
	 * 
	 * @param obj            Object to save
	 * @param collectionName name of the collection to use
	 * @return the inserted object
	 */
	public Object insert(Object obj, String collectionName) {
		return getMongoTemplate().insert(obj, collectionName);
	}

	/**
	 * This method perform an insert operation in the collection of the given
	 * domainType. Use saveDocument for upsert.
	 * 
	 * @param obj Object to save
	 * @return the inserted object
	 */
	public Object insert(Object obj) {
		return getMongoTemplate().insert(obj);
	}

	/**
	 * This method perform an upsert operation.
	 * 
	 * @param obj            Object to save
	 * @param collectionName name of the collection to use
	 * @return the inserted object
	 */
	public Object save(Object obj, String collectionName) {
		return getMongoTemplate().save(obj, collectionName);
	}

	/**
	 * This method perform an upsert operation in the collection of the given
	 * domainType.
	 * 
	 * @param obj Object to save
	 * @return the inserted object
	 */
	public Object save(Object obj) {
		return getMongoTemplate().save(obj);
	}

	public DeleteResult delete(Object obj, String collectionName) {
		return getMongoTemplate().remove(obj, collectionName);
	}

	public ObjectId uploadFileFromInputStream(String bucketName, String filename, InputStream is,
			GridFSUploadOptions metadata) {
		GridFSBucket bucket = findOrCreateBucket(bucketName);
		if (metadata != null)
			return bucket.uploadFromStream(filename, is, metadata);
		else
			return uploadFileFromInputStream(bucketName, filename, is);
	}

	public ObjectId uploadFileFromInputStream(String bucketName, String filename, InputStream is) {
		GridFSBucket bucket = findOrCreateBucket(bucketName);
		return bucket.uploadFromStream(filename, is, null);
	}

	public InputStream getInputStreamById(String bucketName, ObjectId id) {
		GridFSBucket bucket = findOrCreateBucket(bucketName);
		return bucket.openDownloadStream(id);
	}

	public InputStream getInputStreamByFilename(String bucketName, String filename) {
		GridFSBucket bucket = findOrCreateBucket(bucketName);
		return bucket.openDownloadStream(filename);
	}

	public ObjectId storeFileFromInputStream(String bucketName, String filename, InputStream is,
			GridFSUploadOptions metadata) {
		GridFSBucket bucket = findOrCreateBucket(bucketName);
		if (metadata != null)
			return bucket.uploadFromStream(filename, is, metadata);
		else
			return storeFileFromInputStream(bucketName, filename, is);
	}

	public ObjectId storeFileFromInputStream(String bucketName, String filename, InputStream is) {
		GridFSBucket bucket = findOrCreateBucket(bucketName);
		return bucket.uploadFromStream(filename, is);
	}

	public void deleteFileById(String bucketName, ObjectId id) {
		GridFSBucket bucket = findOrCreateBucket(bucketName);
		bucket.delete(id);
	}

	/**
	 * This method delete from the given bucket all the files with the given
	 * filename.
	 * 
	 * @param bucketName name of the bucket where perform the operation
	 * @param filename   name of the bucket where perform the operation
	 */
	public void deleteFilesByFilename(String bucketName, String filename) {
		GridFSBucket bucket = findOrCreateBucket(bucketName);
		GridFSFindIterable files = bucket.find(Filters.eq("filename", filename));
		for (GridFSFile file : files)
			bucket.delete(file.getId());
	}
}
