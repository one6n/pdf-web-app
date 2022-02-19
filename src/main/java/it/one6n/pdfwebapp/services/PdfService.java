package it.one6n.pdfwebapp.services;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import it.one6n.pdfutils.PdfUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	public PDDocument getDocumentFromByteArray(byte[] barr) {
		return loadPDF(barr);
	}

	public List<PDDocument> splitDocument(PDDocument document, int index) throws IOException {
		return PdfUtils.splitDocument(document, index);
	}

	public PDDocument loadPDF(byte[] barr) {
		return PdfUtils.loadPDF(barr);
	}

	public PDDocument mergeDocuments(PDDocument... documents) {
		return PdfUtils.mergeDocuments(documents);
	}
}