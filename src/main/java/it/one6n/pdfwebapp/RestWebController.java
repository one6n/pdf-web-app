package it.one6n.pdfwebapp;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rest")
public class RestWebController {

	public static final String SPLIT_FILE_PATH = "/splitFile";

	@PostMapping(SPLIT_FILE_PATH)
	public void splitFile() {
		log.debug("Request in " + SPLIT_FILE_PATH);
	}
}
