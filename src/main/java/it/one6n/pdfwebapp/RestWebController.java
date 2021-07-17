package it.one6n.pdfwebapp;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RestController
@RequestMapping("/rest")
public class RestWebController {

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
				PdfPojo pdf = getPdfService().findPdfById(id);
				if (pdf != null) {
					// result.setData(pdf);
					if (pdf.getNumberOfPages() > splitIndex) {
						log.debug("File loaded: filename={}, size={}", pdf.getFilename(), pdf.getData().length());

					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			result.setResult(true);
		}
		return result;
	}
}
