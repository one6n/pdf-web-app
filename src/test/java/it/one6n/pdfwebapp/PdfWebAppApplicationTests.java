package it.one6n.pdfwebapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
	public void shouldSaveAndFindEntries() {
		getPdfMongoService().createCollection("test");

		PdfMongoEntry entry = null;
		int numberOfEntries = 5;
		for (int i = 0; i < numberOfEntries; i++) {
			entry = new PdfMongoEntry();
			entry.setFilename("testFilename" + (i + 1));
			assertNotNull(getPdfMongoService().getPdfMongoEntryRepo().save(entry));
			log.debug("created entries={}", entry.getFilename());
		}

		List<PdfMongoEntry> entries = new ArrayList<>();
		entries = getPdfMongoService().getMongoTemplate().findAll(PdfMongoEntry.class);
		assertEquals(5, entries.size());

	}
}
