package it.one6n.pdfwebapp;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequestMapping("/rest")
public class RestController {

	public static final String SPLIT_FILE_PATH = "/splitFile";

	@PostMapping(SPLIT_FILE_PATH)
	public void splitFile() {
		log.debug("Request in " + SPLIT_FILE_PATH);
	}
}
