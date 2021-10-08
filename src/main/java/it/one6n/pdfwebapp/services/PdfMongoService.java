package it.one6n.pdfwebapp.services;

import java.io.IOException;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.one6n.pdfwebapp.models.PdfMongoEntry;
import it.one6n.pdfwebapp.repos.PdfMongoEntryRepo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Service
public class PdfMongoService extends BaseMongoService {

	@Autowired
	private PdfService pdfService;
	@Autowired
	private PdfMongoEntryRepo pdfMongoEntryRepo;

	public PdfMongoEntry findById(ObjectId id) {
		return getPdfMongoEntryRepo().findById(id).orElseThrow();
	}

	public void findPdfFile(String filename) {
		getGridFsTemplate().find(null);
	}

	public PdfMongoEntry savePdf(MultipartFile inputFile) {
		try {
			ObjectId gridFsId = uploadFileFromInputStream("fs.files", inputFile.getOriginalFilename(),
					inputFile.getInputStream());
			return buildPdfMongoEntryFromMultipartFile(inputFile, gridFsId);
		} catch (IOException e) {
			throw new RuntimeException("Error, file corrupted");
		}
	}

	public PdfMongoEntry buildPdfMongoEntryFromMultipartFile(MultipartFile file, ObjectId gridFsId) throws IOException {
		PdfMongoEntry entry = new PdfMongoEntry();
		entry.setFilename(file.getOriginalFilename());
		entry.setGridFsId(gridFsId);
		entry.setNumberOfPages(getPdfService().getNumberOfPages(file.getBytes()));
		entry.setInsertDate(new Date());
		return entry;
	}
}
