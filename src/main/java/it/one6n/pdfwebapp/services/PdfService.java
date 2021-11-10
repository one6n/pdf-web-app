package it.one6n.pdfwebapp.services;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import it.one6n.pdfutils.PdfUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public class PdfService {

	public int getNumberOfPages(byte[] barr) throws IOException {
		return PdfUtils.getNumberOfPages(barr);
	}

	public int getNumberOfPages(PDDocument document) throws IOException {
		return PdfUtils.getNumberOfPages(document);
	}

	public List<PDDocument> splitDocument(PDDocument document, int index) throws IOException {
		return PdfUtils.splitDocument(document, index);
	}
}