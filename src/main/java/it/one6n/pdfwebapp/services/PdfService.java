package it.one6n.pdfwebapp.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public class PdfService {

	// Pdfbox utility methods
	public int getNumberOfPages(byte[] barr) throws IOException {
		int numberOfPages = 0;
		try (PDDocument doc = PDDocument.load(barr)) {
			numberOfPages = getNumberOfPages(doc);
		}
		return numberOfPages;
	}

	public int getNumberOfPages(PDDocument document) throws IOException {
		return document.getNumberOfPages();
	}

	public List<PDDocument> splitDocument(PDDocument document, int index) throws IOException {
		List<PDDocument> splitted = null;
		Iterator<PDPage> iterator = document.getPages().iterator();
		int i = 0;
		splitted = new ArrayList<>();
		PDDocument doc1 = new PDDocument();
		while (i < index && iterator.hasNext()) {
			doc1.addPage(iterator.next());
			++i;
		}
		splitted.add(doc1);
		PDDocument doc2 = new PDDocument();
		while (iterator.hasNext()) {
			doc2.addPage(iterator.next());
			++i;
		}
		splitted.add(doc2);
		return splitted;
	}
}