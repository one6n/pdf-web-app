package it.one6n.pdfwebapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.client.gridfs.model.GridFSFile;

import it.one6n.pdfwebapp.models.PdfMongoEntry;
import it.one6n.pdfwebapp.services.PdfMongoService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@SpringBootTest
class PdfWebAppApplicationTests {
	@Autowired
	private PdfMongoService pdfMongoService;
	@Autowired
	private GridFsTemplate gridFsTemplate;

	@Value("classpath:test.pdf")
	private Resource testFile;

	@BeforeEach
	public void clearDB() {
		getPdfMongoService().getMongoTemplate().dropCollection("test");
	}

	@Test
	void contextLoads() {
	}

	@Test
	public void shouldCreateMongoCollection() {
		getPdfMongoService().createCollection("test");
		Set<String> collections = getPdfMongoService().getCollections();
		assertEquals(1, collections.size());
		for (String collectionName : collections)
			assertEquals("test", collectionName);
	}

	@Test
	public void shouldSaveAndFindEntriesWithQueryCriteria() {
		getPdfMongoService().createCollection("test");

		PdfMongoEntry entry = null;
		int numberOfEntries = 5;
		for (int i = 0; i < numberOfEntries; i++) {
			entry = new PdfMongoEntry();
			entry.setFilename("testFilename" + (i + 1));
			entry.setNumberOfPages(i + 1);
			assertNotNull(getPdfMongoService().getPdfMongoEntryRepo().save(entry));
			log.debug("created entries: filename={}, numberOfPages={}", entry.getFilename(), entry.getNumberOfPages());
		}

		List<PdfMongoEntry> entries = new ArrayList<>();
		entries = getPdfMongoService().getMongoTemplate().findAll(PdfMongoEntry.class);
		assertEquals(5, entries.size());

		entries.clear();
		Query query = new Query();
		query.addCriteria(Criteria.where("filename").is("testFilename1")).limit(1);
		entries = getPdfMongoService().getMongoTemplate().find(query, PdfMongoEntry.class);
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
		entries = getPdfMongoService().getMongoTemplate().find(query, PdfMongoEntry.class);
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
		entries = getPdfMongoService().getMongoTemplate().find(query, PdfMongoEntry.class);
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
	}
}
