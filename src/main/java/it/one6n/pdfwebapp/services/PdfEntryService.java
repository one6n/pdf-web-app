package it.one6n.pdfwebapp.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.one6n.pdfwebapp.models.PdfEntry;
import it.one6n.pdfwebapp.repos.PdfEntryRepo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public class PdfEntryService {

	@Autowired
	private PdfService pdfService;
	@Autowired
	private PdfEntryRepo pdfRepo;

	public PdfEntry findPdfById(Long id) {
		return getPdfRepo().findById(id).orElse(null);
	}

	public void deletePdfById(Long id) {
		if (id != null)
			getPdfRepo().deleteById(id);
	}

	public PdfEntry savePdf(MultipartFile inputFile) {
		if (inputFile.isEmpty())
			return null;

		try {
			return savePdf(buildPdfEntryFromMultipartFile(inputFile));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public PdfEntry savePdf(PdfEntry pdf) {
		if (pdf == null)
			return null;

		return getPdfRepo().save(pdf);
	}

	public List<PdfEntry> findAllOrderByDateDesc() {
		return getPdfRepo().findAllOrderByInsertDateDesc();
	}

	public PdfEntry buildPdfEntryFromMultipartFile(MultipartFile file)
			throws IOException, SerialException, SQLException {
		PdfEntry pdf = new PdfEntry();
		byte[] barr = file.getBytes();
		Blob data = new SerialBlob(barr);
		pdf.setFilename(file.getOriginalFilename());
		pdf.setData(data);
		pdf.setNumberOfPages(getPdfService().getNumberOfPages(barr));
		pdf.setInsertDate(new Date());
		return pdf;
	}

	public List<PdfEntry> buildPdfEntryFromDocuments(List<PDDocument> documents, String baseFilename)
			throws IOException, SerialException, SQLException {
		List<PdfEntry> entries = null;
		if (documents != null && baseFilename != null) {
			entries = new ArrayList<>();
			int counter = 1;
			for (PDDocument doc : documents)
				try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
					PdfEntry entry = new PdfEntry();
					String filename = baseFilename.substring(0, baseFilename.indexOf(".pdf")) + "_" + counter + ".pdf";
					entry.setFilename(filename);
					entry.setNumberOfPages(doc.getNumberOfPages());
					entry.setInsertDate(new Date());
					doc.save(bos);
					entry.setData(new SerialBlob(bos.toByteArray()));
					entries.add(entry);
					++counter;
				}
		}
		return entries;
	}

	public List<PdfEntry> splitPdf(PdfEntry pdf, int delimiter) throws SQLException, IOException {
		List<PdfEntry> splittedPdf = null;
		if (pdf != null) {
			splittedPdf = new ArrayList<>();
			byte[] barr = deserialize(pdf.getData());
			List<PDDocument> splittedDocuments = null;
			try (PDDocument docOriginal = PDDocument.load(barr)) {
				splittedDocuments = getPdfService().splitDocument(docOriginal, delimiter);
				splittedPdf = buildPdfEntryFromDocuments(splittedDocuments, pdf.getFilename());
			} finally {
				for (PDDocument doc : splittedDocuments)
					doc.close();
			}
		}
		return splittedPdf;
	}

	public byte[] deserialize(Blob data) throws SQLException {
		byte[] barr = null;
		if (data.length() > 0) {
			barr = data.getBytes(1, (int) data.length());
		}
		return barr;
	}
}