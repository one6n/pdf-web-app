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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import it.one6n.pdfwebapp.models.PdfMongoEntry;
import it.one6n.pdfwebapp.pojos.RestResult;
import it.one6n.pdfwebapp.pojos.SplitInfo;
import it.one6n.pdfwebapp.services.PdfMongoService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RestController
@RequestMapping("/api/rest")
public class RestWebController {

	public static final String DOWNLOAD_PDF_PATH = "/downloadPdf/{id}";
	public static final String SPLIT_FILE_PATH = "/splitFile";

	@Autowired
	private PdfMongoService pdfMongoService;

	@PostMapping(path = SPLIT_FILE_PATH, produces = "application/json")
	public RestResult splitFile(@RequestBody SplitInfo splitInfo) {
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
