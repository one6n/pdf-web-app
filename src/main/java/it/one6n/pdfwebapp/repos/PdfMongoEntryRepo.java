package it.one6n.pdfwebapp.repos;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.one6n.pdfwebapp.models.PdfMongoEntry;

public interface PdfMongoEntryRepo extends MongoRepository<PdfMongoEntry, String> {

	List<PdfMongoEntry> findAllByOrderByInsertDateDesc();

}
