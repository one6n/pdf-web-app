package it.one6n.pdfwebapp.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import it.one6n.pdfwebapp.PdfPojo;
import it.one6n.pdfwebapp.PdfService;
import it.one6n.pdfwebapp.RestResult;
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
	private PdfService pdfService;

	@PostMapping(path = SPLIT_FILE_PATH, produces = "application/json")
	public RestResult splitFile(@RequestBody Map<String, String> input) {
		log.debug("input={}", input);
		RestResult result = new RestResult(false);
		if (input != null) {
			try {
				Long id = Long.parseLong(input.get("id"));
				int splitIndex = Integer.parseInt(input.get("splitIndex"));
				PdfPojo originalPdf = getPdfService().findPdfById(id);
				if (originalPdf != null)
					if (originalPdf.getNumberOfPages() > splitIndex) {
						log.debug("File loaded: filename={}, size={}", originalPdf.getFilename(),
								originalPdf.getData().length());
						List<Long> ids = new ArrayList<>();
						List<PdfPojo> splittedDocuments = getPdfService().splitPdf(originalPdf, splitIndex);
						for (PdfPojo pdf : splittedDocuments) {
							PdfPojo saved = getPdfService().savePdf(pdf);
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

	@GetMapping(path = DOWNLOAD_PDF_PATH, produces = "application/pdf")
	public @ResponseBody byte[] downloadPdf(@PathVariable String id, HttpServletResponse response) {
		log.debug("id={}", id == null ? null : id);
		byte[] barr = null;
		if (id != null) {
			try {
				PdfPojo pdf = getPdfService().findPdfById(Long.parseLong(id));
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
}
