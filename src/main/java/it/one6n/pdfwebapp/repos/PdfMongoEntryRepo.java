package it.one6n.pdfwebapp.repos;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import it.one6n.pdfwebapp.models.PdfMongoEntry;

public interface PdfMongoEntryRepo extends MongoRepository<PdfMongoEntry, ObjectId> {

}
