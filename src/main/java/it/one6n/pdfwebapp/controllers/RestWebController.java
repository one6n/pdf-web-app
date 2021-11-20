package it.one6n.pdfwebapp.controllers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import it.one6n.pdfwebapp.models.PdfMongoEntry;
import it.one6n.pdfwebapp.pojos.RestResult;
import it.one6n.pdfwebapp.pojos.SplitInfo;
import it.one6n.pdfwebapp.services.PdfMongoService;
import it.one6n.pdfwebapp.services.PdfService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RestController
@RequestMapping(RestWebController.REST_API_BASE_PATH)
public class RestWebController {

	public static final String REST_API_BASE_PATH = "/api/rest";

	public static final String SPLIT_DOCUMENT_PATH = "/splitDocument";
	public static final String MERGE_DOCUMENTS_PATH = "/mergeDocuments";
	public static final String UPLOAD_FILE_PATH = "/uploadFile";
	public static final String DOWNLOAD_PDF_PATH = "/downloadPdf/{id}";

	@Autowired
	private PdfMongoService pdfMongoService;
	@Autowired
	private PdfService pdfService;

	@PostMapping(path = SPLIT_DOCUMENT_PATH, produces = "application/json")
	public RestResult splitDocument(@RequestBody SplitInfo splitInfo) {
		log.info("splitInfo={}", splitInfo == null ? null : splitInfo);
		RestResult result = new RestResult(false);
		if (splitInfo != null) {
			try {
				String id = splitInfo.getId();
				int splitIndex = splitInfo.getSplitIndex();
				PdfMongoEntry originalPdfEntry = getPdfMongoService().findPdfEntryById(id);
				if (originalPdfEntry != null)
					if (originalPdfEntry.getNumberOfPages() > splitIndex) {
						try (InputStream is = getPdfMongoService().findPdfFile(originalPdfEntry.getGridFsId(),
								originalPdfEntry.getInsertDate())) {
							byte[] barr = IOUtils.toByteArray(is);
							log.debug("File loaded: filename={}, size={}", originalPdfEntry.getFilename(), barr.length);
							List<String> ids = new ArrayList<>();
							List<PdfMongoEntry> splittedDocuments = getPdfMongoService().splitPdfFromByteArray(barr,
									originalPdfEntry.getFilename(), splitIndex);
							for (PdfMongoEntry pdf : splittedDocuments) {
								PdfMongoEntry saved = getPdfMongoService().savePdfMongoEntry(pdf);
								log.debug("pdf={}, id={}, numberOfPages={}", saved.getFilename(), saved.getId(),
										saved.getNumberOfPages());
								ids.add(saved.getId());
							}
							getPdfMongoService().deletePdfEntryAndFile(originalPdfEntry.getId(),
									originalPdfEntry.getGridFsId(), originalPdfEntry.getInsertDate());
							result.setData(ids);
						}
					}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (result.getData() != null)
				result.setResult(true);
		}
		return result;
	}

	@GetMapping(path = MERGE_DOCUMENTS_PATH, produces = "application/json")
	public RestResult mergeDocuments(@RequestParam String id1, @RequestParam String id2) {
		log.info("id1={}, id2={}", id1, id2);
		RestResult result = new RestResult(false);
		try {
			PdfMongoEntry merged = getPdfMongoService().mergeDocumentsFromEntries(id1, id2);
			if (merged != null) {
				merged = getPdfMongoService().savePdfMongoEntry(merged);
				result.setResult(true);
				result.setData(merged.getId());
			} else
				throw new RuntimeException("Error in merge operation");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@PostMapping(path = UPLOAD_FILE_PATH, produces = "application/json")
	public RestResult uploadFile(@RequestParam("file") MultipartFile inputFile) {
		log.info("inputFile size={}", inputFile == null ? null : inputFile.getSize());
		PdfMongoEntry entry = getPdfMongoService().savePdfMongoEntryAndFileFromMultipartFile(inputFile);
		if (entry != null)
			return new RestResult(true, entry.getId());
		else
			return new RestResult(false);
	}

	@GetMapping(path = DOWNLOAD_PDF_PATH)
	public ResponseEntity<StreamingResponseBody> downloadPdf(@PathVariable String id, HttpServletResponse response) {
		log.info("id={}", id == null ? null : id);
		try {
			PdfMongoEntry entry = getPdfMongoService().findPdfEntryById(id);
			if (entry == null)
				throw new RuntimeException("Not found document with id: " + id);

			StreamingResponseBody stream = out -> {
				try (InputStream pdf = getPdfMongoService().findPdfFile(entry.getGridFsId(), entry.getInsertDate())) {
					long bytesRead = IOUtils.copyLarge(pdf, out);
					log.debug("bytesRead={}", bytesRead);
				}
			};
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/pdf");
			headers.add("Content-Disposition", "attachment; filename=" + entry.getFilename());
			return new ResponseEntity<StreamingResponseBody>(stream, headers, HttpStatus.OK);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
