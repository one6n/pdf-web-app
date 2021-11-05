package it.one6n.pdfwebapp.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import it.one6n.pdfwebapp.models.PdfMongoEntry;
import it.one6n.pdfwebapp.services.PdfMongoService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Controller
public class WebController {

	public static final String HOME_PATH = "/";
	public static final String SPLIT_PATH = "/split";
	public static final String EDIT_SPLIT_PATH = "/editSplit";
	public static final String DOWNLOAD_SPLITTED_PATH = "/downloadSplitted";

	public final static String HOME_PAGE = "home";
	public static final String SPLIT_PAGE = "split";
	public static final String EDIT_SPLIT_PAGE = "editSplit";
	private static final String DOWNLOAD_SPLITTED_PAGE = "downloadSplitted";

	@Value("${spring.application.name}")
	private String title;

	@Autowired
	private PdfMongoService pdfMongoService;

	@GetMapping(HOME_PATH)
	public String getHomePage(Model model) {
		log.debug("Enter HomePage");
		model.addAttribute("title", title);
		return HOME_PAGE;
	}

	@GetMapping(SPLIT_PATH)
	public String getSplitPage(Model model) {
		log.debug("Enter SplitPage");
		model.addAttribute("title", title);
		return SPLIT_PAGE;
	}

	@PostMapping(EDIT_SPLIT_PATH)
	public ModelAndView getEditSplitPage(@RequestParam("file") MultipartFile inputFile) {
		log.debug("pdf={}, size={}", inputFile.getOriginalFilename(), inputFile.getSize());
		ModelAndView model = new ModelAndView(EDIT_SPLIT_PAGE);
		PdfMongoEntry entry = getPdfMongoService().savePdfMongoEntryAndFileFromMultipartFile(inputFile);
		log.debug("Saved: entryId={}, gridFsId={}, filename={}", entry.getId(), entry.getGridFsId(),
				entry.getFilename());
		model.addObject("title", title);
		model.addObject("id", entry.getId());
		model.addObject("filename", entry.getFilename());
		model.addObject("numPages", entry.getNumberOfPages());
		return model;
	}

	@GetMapping(DOWNLOAD_SPLITTED_PATH)
	public String getDownloadSplittedPage(Model model, @RequestParam Map<String, String> params) {
		log.debug("Enter DownloadSplittedPage");
		log.debug("params={}", params == null ? null : params);
		List<PdfMongoEntry> documents = new ArrayList<>();
		try {
			for (Entry<String, String> param : params.entrySet()) {
				if (StringUtils.startsWithIgnoreCase(param.getKey(), "id")) {
					PdfMongoEntry entry = getPdfMongoService().findPdfEntryById(param.getValue());
					if (entry != null)
						documents.add(entry);
					else
						throw new RuntimeException("No pdf found with id: " + param.getValue());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		model.addAttribute("title", title);
		model.addAttribute("documents", documents);
		return DOWNLOAD_SPLITTED_PAGE;
	}
}
