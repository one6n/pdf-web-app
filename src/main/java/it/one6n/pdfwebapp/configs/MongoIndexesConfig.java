package it.one6n.pdfwebapp.configs;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import it.one6n.pdfwebapp.models.PdfMongoEntry;
import lombok.Getter;

@Getter
@Configuration
public class MongoIndexesConfig {

	@Autowired
	private MongoTemplate mongoTemplate;

	@PostConstruct
	public void initIndexes() {
		getMongoTemplate().indexOps(PdfMongoEntry.class).ensureIndex(new Index().on("filename", Direction.ASC));
	}
}
