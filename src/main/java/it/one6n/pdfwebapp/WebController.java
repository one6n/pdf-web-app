package it.one6n.pdfwebapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class WebController {

	private final static String HOME_PATH = "home";

	@Value("${spring.application.name}")
	private String title;

	@GetMapping
	public String getHome(Model model) {
		model.addAttribute("title", title);
		return "home";
	}
}
