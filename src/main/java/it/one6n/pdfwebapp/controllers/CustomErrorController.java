package it.one6n.pdfwebapp.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

	public static final String ERROR_PATH = "/error";

	private static final String ERROR_PAGE = "error";

	@Value("${spring.application.name}")
	private String title;

	@RequestMapping(ERROR_PATH)
	public String handleError(Model model, HttpServletResponse response) {
		int status = response.getStatus();
		model.addAttribute("title", title);
		model.addAttribute("status", status);
		return ERROR_PAGE;
	}
}
