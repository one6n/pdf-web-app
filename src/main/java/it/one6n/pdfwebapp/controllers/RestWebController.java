package it.one6n.pdfwebapp.controllers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import it.one6n.pdfwebapp.models.PdfEntry;
import it.one6n.pdfwebapp.models.PdfMongoEntry;
import it.one6n.pdfwebapp.pojos.RestResult;
import it.one6n.pdfwebapp.services.PdfMongoService;
import it.one6n.pdfwebapp.services.PdfService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RestController
@RequestMapping("/api/rest")
public class RestWebController {

	public static final String DOWNLOAD_PDF_PATH = "/downloadPdf/{id}";
	public static final String MONGO_DOWNLOAD_PDF_PATH = "/mongoDownloadPdf/{id}";

	public static final String SPLIT_FILE_PATH = "/splitFile";
	public static final String SPLIT_FILE_MONGO_PATH = "/splitFileMongo";

	@Autowired
	private PdfService pdfService;
	@Autowired
	private PdfMongoService pdfMongoService;

	@PostMapping(path = SPLIT_FILE_PATH, produces = "application/json")
	public RestResult splitFile(@RequestBody Map<String, String> input) {
		log.debug("input={}", input);
		RestResult result = new RestResult(false);
		if (input != null) {
			try {
				Long id = Long.parseLong(input.get("id"));
				int splitIndex = Integer.parseInt(input.get("splitIndex"));
				PdfEntry originalPdf = getPdfService().findPdfById(id);
				if (originalPdf != null)
					if (originalPdf.getNumberOfPages() > splitIndex) {
						log.debug("File loaded: filename={}, size={}", originalPdf.getFilename(),
								originalPdf.getData().length());
						List<Long> ids = new ArrayList<>();
						List<PdfEntry> splittedDocuments = getPdfService().splitPdf(originalPdf, splitIndex);
						for (PdfEntry pdf : splittedDocuments) {
							PdfEntry saved = getPdfService().savePdf(pdf);
							log.debug("pdf={}, id={}, numberOfPages={}", saved.getFilename(), saved.getId(),
									saved.getNumberOfPages());
							ids.add(saved.getId());
						}
						getPdfService().deletePdfById(originalPdf.getId());
						result.setData(ids);
					}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (result.getData() != null)
				result.setResult(true);
		}
		return result;
	}

	@PostMapping(path = SPLIT_FILE_MONGO_PATH, produces = "application/json")
	public RestResult splitFileMongo(@RequestBody Map<String, String> input) {
		log.debug("input={}", input);
		RestResult result = new RestResult(false);
		if (input != null) {
			try {
				String id = input.get("id");
				int splitIndex = Integer.parseInt(input.get("splitIndex"));
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

	@GetMapping(path = DOWNLOAD_PDF_PATH, produces = "application/pdf")
	public @ResponseBody byte[] downloadPdf(@PathVariable String id, HttpServletResponse response) {
		log.debug("id={}", id == null ? null : id);
		byte[] barr = null;
		if (id != null) {
			try {
				PdfEntry pdf = getPdfService().findPdfById(Long.parseLong(id));
				if (pdf == null)
					throw new RuntimeException("Not found document with id: " + id);
				barr = getPdfService().deserialize(pdf.getData());
				response.setHeader("Content-Disposition", "attachment; filename=" + pdf.getFilename());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return barr;
	}

	@GetMapping(path = MONGO_DOWNLOAD_PDF_PATH)
	public ResponseEntity<StreamingResponseBody> mongoDownloadPdf(@PathVariable String id,
			HttpServletResponse response) {
		log.debug("id={}", id == null ? null : id);
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
