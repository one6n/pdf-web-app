package it.one6n.pdfwebapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.one6n.pdfwebapp.repos.PdfMongoEntryRepo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Service
public class PdfMongoService extends BaseMongoService {

	@Autowired
	private PdfMongoEntryRepo pdfMongoEntryRepo;

}
