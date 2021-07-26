package it.one6n.pdfwebapp.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.one6n.pdfwebapp.PdfPojo;
import it.one6n.pdfwebapp.PdfService;
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
	private PdfService pdfService;

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
	public String getEditSplitPage(@RequestParam("file") MultipartFile inputFile, Model model) {
		log.debug("pdf={}, size={}", inputFile.getOriginalFilename(), inputFile.getSize());
		PdfPojo pdf = getPdfService().savePdf(inputFile);
		log.debug("Saved: id={}, filename={}", pdf.getId(), pdf.getFilename());
		model.addAttribute("title", title);
		model.addAttribute("id", pdf.getId());
		model.addAttribute("filename", pdf.getFilename());
		model.addAttribute("numPages", pdf.getNumberOfPages());
		return EDIT_SPLIT_PAGE;
	}

	@GetMapping(DOWNLOAD_SPLITTED_PATH)
	public String getDownloadSplittedPage(Model model, @RequestParam Map<String, String> params) {
		log.debug("Enter DownloadSplittedPage");
		log.debug("params={}", params == null ? null : params);
		List<PdfPojo> documents = new ArrayList<>();
		try {
			for (Entry<String, String> param : params.entrySet()) {
				PdfPojo pdf = getPdfService().findPdfById(Long.parseLong(param.getValue()));
				if (pdf != null)
					documents.add(pdf);
				else
					throw new Exception("No pdf found with id: " + param.getValue());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		model.addAttribute("title", title);
		model.addAttribute("documents", documents);
		return DOWNLOAD_SPLITTED_PAGE;
	}
}
