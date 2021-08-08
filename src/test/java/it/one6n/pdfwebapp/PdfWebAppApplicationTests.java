package it.one6n.pdfwebapp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import it.one6n.pdfwebapp.services.PdfMongoService;
import lombok.Getter;

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
}
