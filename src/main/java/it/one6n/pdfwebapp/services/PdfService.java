package it.one6n.pdfwebapp.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
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

	// move to pdf library
	public PDDocument loadPDF(byte[] barr) {
		PDDocument document = null;
		if (barr != null && barr.length > 0) {
			try {
				document = PDDocument.load(barr);
			} catch (IOException e) {
				log.error("Error during the load the document from byte");
			}
		}
		return document;
	}

	public PDDocument mergeDocuments(InputStream is1, InputStream is2) throws IOException {
		try (PDDocument doc1 = getDocumentFromByteArray(IOUtils.toByteArray(is1));
				PDDocument doc2 = getDocumentFromByteArray(IOUtils.toByteArray(is2));) {
			return mergeDocuments(doc1, doc2);
		}
	}

	public PDDocument mergeDocuments(PDDocument doc1, PDDocument doc2) {
		PDDocument merged = null;
		return merged;
	}
}