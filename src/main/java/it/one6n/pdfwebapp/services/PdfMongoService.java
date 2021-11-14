package it.one6n.pdfwebapp.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
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

	public PdfMongoEntry findPdfEntryById(String id) {
		return getPdfMongoEntryRepo().findById(id).orElseThrow();
	}

	public InputStream findPdfFile(ObjectId id, Date date) {
		return getInputStreamById(buildPdfFileBucketName(date), id);
	}

	public String buildPdfFileBucketName(Date date) {
		String formattedDate = new SimpleDateFormat("yyyyMMdd").format(date);
		StringBuilder sb = new StringBuilder("pdfFile_");
		sb.append(formattedDate);
		return sb.toString();
	}

	public PdfMongoEntry savePdfMongoEntryAndFileFromMultipartFile(MultipartFile inputFile) {
		try {
			ObjectId gridFsId = storeFileFromInputStream(buildPdfFileBucketName(new Date()),
					inputFile.getOriginalFilename(), inputFile.getInputStream());
			PdfMongoEntry entry = buildPdfMongoEntryFromMultipartFile(inputFile, gridFsId);
			return savePdfMongoEntry(entry);
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

	public List<PdfMongoEntry> buildPdfMongoEntriesAndSaveFilesFromDocuments(List<PDDocument> documents,
			String baseFilename) throws IOException {
		List<PdfMongoEntry> entries = null;
		if (documents != null && baseFilename != null) {
			entries = new ArrayList<>();
			int counter = 1;
			for (PDDocument doc : documents)
				try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
					PdfMongoEntry entry = new PdfMongoEntry();
					String filename = baseFilename.substring(0, baseFilename.indexOf(".pdf")) + "_" + counter + ".pdf";
					entry.setFilename(filename);
					entry.setNumberOfPages(doc.getNumberOfPages());
					entry.setInsertDate(new Date());
					doc.save(bos);
					try (InputStream is = new ByteArrayInputStream(bos.toByteArray())) {
						ObjectId gridFsId = storeFileFromInputStream(buildPdfFileBucketName(entry.getInsertDate()),
								filename, is);
						entry.setGridFsId(gridFsId);
						entries.add(entry);
						++counter;
					}
				}
		}
		return entries;
	}

	public List<PdfMongoEntry> splitPdfFromByteArray(byte[] barr, String filename, int delimiter) throws IOException {
		List<PdfMongoEntry> splittedPdf = null;
		if (barr != null && barr.length > 0) {
			splittedPdf = new ArrayList<>();
			List<PDDocument> splittedDocuments = null;
			try (PDDocument docOriginal = getPdfService().getDocumentFromByteArray(barr)) {
				splittedDocuments = getPdfService().splitDocument(docOriginal, delimiter);
				splittedPdf = buildPdfMongoEntriesAndSaveFilesFromDocuments(splittedDocuments, filename);
			} finally {
				for (PDDocument doc : splittedDocuments)
					doc.close();
			}
		}
		return splittedPdf;
	}

	public PdfMongoEntry mergeDocumentsFromEntries(String id1, String id2) {
		PdfMongoEntry mergedEntry = null;
		PdfMongoEntry firstEntry = findPdfEntryById(id1);
		PdfMongoEntry secondEntry = findPdfEntryById(id2);

		try (InputStream is1 = getInputStreamById(buildPdfFileBucketName(firstEntry.getInsertDate()),
				firstEntry.getGridFsId());
				InputStream is2 = getInputStreamById(buildPdfFileBucketName(firstEntry.getInsertDate()),
						secondEntry.getGridFsId());
				PDDocument doc1 = getPdfService().getDocumentFromByteArray(IOUtils.toByteArray(is1));
				PDDocument doc2 = getPdfService().getDocumentFromByteArray(IOUtils.toByteArray(is2));
				PDDocument mergedDocument = getPdfService().mergeDocuments(new PDDocument[] { doc1, doc2 });) {
			log.debug("mergedDocument numberOfPages={}", mergedDocument.getNumberOfPages());
			mergedEntry = buildPdfMongoEntriesAndSaveFilesFromDocuments(List.of(mergedDocument), "merged.pdf").get(0);
		} catch (IOException e) {
			throw new RuntimeException("Error in merge operation");
		}
		return mergedEntry;
	}

	public void deletePdfEntryAndFile(String entryId, ObjectId gridFsId, Date insertDate) {
		deleteFileFromBucketById(buildPdfFileBucketName(insertDate), gridFsId);
		getPdfMongoEntryRepo().deleteById(entryId);
	}

	public PdfMongoEntry savePdfMongoEntry(PdfMongoEntry entry) {
		return getPdfMongoEntryRepo().save(entry);
	}

	public List<PdfMongoEntry> findAllOrderByInsertDateDesc() {
		return getPdfMongoEntryRepo().findAllByOrderByInsertDateDesc();
	}
}
