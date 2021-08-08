package it.one6n.pdfwebapp.repos;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.one6n.pdfwebapp.models.PdfEntry;

public interface PdfEntryMongoRepo extends MongoRepository<PdfEntry, String> {

}
