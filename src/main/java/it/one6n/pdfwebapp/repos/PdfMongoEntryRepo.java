package it.one6n.pdfwebapp.repos;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.one6n.pdfwebapp.models.PdfMongoEntry;

public interface PdfMongoEntryRepo extends MongoRepository<PdfMongoEntry, String> {

}
