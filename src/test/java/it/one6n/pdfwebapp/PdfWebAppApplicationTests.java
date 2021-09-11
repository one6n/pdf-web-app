package it.one6n.pdfwebapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;

import it.one6n.pdfwebapp.models.PdfMongoEntry;
import it.one6n.pdfwebapp.services.PdfMongoService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@SpringBootTest
class PdfWebAppApplicationTests {

	public static final String TEST_COLLECTION_NAME = "test";
	public static final String TEST_BUCKET_NAME = "testBucket";

	@Autowired
	private PdfMongoService pdfMongoService;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private GridFsTemplate gridFsTemplate;

	@Value("classpath:test.pdf")
	private Resource testFile;

	@BeforeEach
	public void clearDB() {
		log.info("Cleaning Test Collection");
		getMongoTemplate().dropCollection(TEST_COLLECTION_NAME);
		Set<String> collections = getPdfMongoService().getCollections();
		log.info("Remaining Collection:");
		for (String collection : collections)
			log.info("collection={}", collection);
		log.info("Cleaning Test Bucket");
		GridFSBucket testBucket = getPdfMongoService().createBucket(TEST_BUCKET_NAME);
		testBucket.drop();
		log.info("Cleaning standard Bucket from test file");
		Query query = new Query();
		query.addCriteria(Criteria.where("filename").regex("test"));
		GridFSFindIterable testFiles = getGridFsTemplate().find(query);
		for (GridFSFile file : testFiles)
			log.info("fileDeletedFromFiles.fs={}", file.getFilename());
		getGridFsTemplate().delete(query);
	}

	@Test
	void contextLoads() {
	}

	@Test
	public void shouldCreateMongoCollection() {
		getPdfMongoService().createCollection(TEST_COLLECTION_NAME);
		Set<String> collections = getPdfMongoService().getCollections();
		assertTrue(collections.contains(TEST_COLLECTION_NAME));
	}

	@Test
	public void shouldSaveAndFindEntriesWithQueryCriteria() {
		getPdfMongoService().createCollection(TEST_COLLECTION_NAME);

		PdfMongoEntry entry = null;
		int numberOfEntries = 5;
		for (int i = 0; i < numberOfEntries; i++) {
			entry = new PdfMongoEntry();
			entry.setFilename("testFilename" + (i + 1));
			entry.setNumberOfPages(i + 1);
			assertNotNull(getPdfMongoService().insertDocument(entry, TEST_COLLECTION_NAME));
			log.debug("created entries: filename={}, numberOfPages={}", entry.getFilename(), entry.getNumberOfPages());
		}

		List<PdfMongoEntry> entries = new ArrayList<>();
		entries = getMongoTemplate().findAll(PdfMongoEntry.class, TEST_COLLECTION_NAME);
		assertEquals(5, entries.size());

		entries.clear();
		Query query = new Query();
		query.addCriteria(Criteria.where("filename").is("testFilename1")).limit(1);
		entries = getMongoTemplate().find(query, PdfMongoEntry.class, TEST_COLLECTION_NAME);
		assertEquals(1, entries.size());
		assertEquals("testFilename1", entries.get(0).getFilename());

		entries.clear();
		query = new Query();
		Criteria criteria = new Criteria();
		List<Criteria> criterias = new ArrayList<>();
		criterias.add(Criteria.where("filename").is("testFilename1"));
		criterias.add(Criteria.where("filename").is("testFilename2"));
		query.addCriteria(criteria.orOperator(criterias));
		log.debug("query={}", query);
		entries = getMongoTemplate().find(query, PdfMongoEntry.class, TEST_COLLECTION_NAME);
		assertEquals(2, entries.size());
		assertEquals("testFilename1", entries.get(0).getFilename());
		assertEquals("testFilename2", entries.get(1).getFilename());

		entries.clear();
		criterias.clear();
		query = new Query();
		criteria = new Criteria();
		criterias.add(Criteria.where("filename").regex("testFilename"));
		criterias.add(Criteria.where("numberOfPages").gt(1));
		criteria.andOperator(criterias);
		query.addCriteria(criteria);
		log.debug("query={}", query);
		entries = getMongoTemplate().find(query, PdfMongoEntry.class, TEST_COLLECTION_NAME);
		assertEquals(4, entries.size());
		assertEquals("testFilename2", entries.get(0).getFilename());
		assertEquals(2, entries.get(0).getNumberOfPages());
	}

	@Test
	public void shouldSaveAndFindWithGridFsTemplate() throws Exception {
		log.info("ResourceFilename={}", getTestFile().getFilename());
		ObjectId id = getGridFsTemplate().store(getTestFile().getInputStream(), "test.pdf");
		assertNotNull(id);

		Query query = new Query();
		query.addCriteria(Criteria.where("filename").is("test.pdf"));
		GridFSFile result = getGridFsTemplate().find(query).first();
		assertNotNull(result);
		assertEquals("test.pdf", result.getFilename());
		assertEquals(id, result.getId().asObjectId().getValue());

		query = new Query();
		Document metadata = new Document();
		metadata.append("testMetadata", "test");
		id = getGridFsTemplate().store(getTestFile().getInputStream(), getTestFile().getFilename(), metadata);
		query.addCriteria(Criteria.where("metadata.testMetadata").is("test"));
		result = getGridFsTemplate().findOne(query);
		assertNotNull(result);
		assertEquals("test.pdf", result.getFilename());
		assertEquals(id, result.getId().asObjectId().getValue());
		assertEquals("test", result.getMetadata().get("testMetadata"));
	}
}
