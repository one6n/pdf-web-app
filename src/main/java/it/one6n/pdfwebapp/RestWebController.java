package it.one6n.pdfwebapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RestController
@RequestMapping("/rest")
public class RestWebController {

	public static final String SPLIT_FILE_PATH = "/splitFile/{id}";

	@Autowired
	private PdfService pdfService;

	@PostMapping(path = SPLIT_FILE_PATH, produces = "application/json")
	public RestResult splitFile(@PathVariable Long id) {
		log.debug("id={}", id);
		RestResult result = new RestResult(false);
		if (id != null) {
			PdfPojo pdf = getPdfService().findPdfById(id);
			if (pdf != null) {
				// result.setData(pdf);
				try {
					log.debug("File loaded: filename={}, size={}", pdf.getFilename(), pdf.getData().length());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				result.setResult(true);
			}
		}
		return result;
	}
}
