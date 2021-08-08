package it.one6n.pdfwebapp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import it.one6n.pdfwebapp.services.MongoService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@SpringBootTest
class PdfWebAppApplicationTests {
	@Autowired
	private MongoService mongoService;

	@Test
	void contextLoads() {
	}

	@Test
	public void shouldCreateMongoCollection() {
		getMongoService().createCollection("test");
		Set<String> collections = getMongoService().getCollections();
		assertEquals(1, collections.size());
		for (String collectionName : collections)
			assertEquals("test", collectionName);
	}
}
