package it.one6n.pdfwebapp;

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

import it.one6n.pdfutils.PdfUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Service
public class PdfService {

	@Autowired
	private PdfRepo pdfRepo;

	public PdfPojo findPdfById(Long id) {
		return getPdfRepo().findById(id).orElse(null);
	}

	public void deletePdfById(Long id) {
		if (id != null)
			getPdfRepo().deleteById(id);
	}

	public PdfPojo savePdf(MultipartFile inputFile) {
		if (inputFile.isEmpty())
			return null;

		try {
			return savePdf(buildPdfPojoFromMultipartFile(inputFile));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public PdfPojo savePdf(PdfPojo pdf) {
		if (pdf == null)
			return null;

		return getPdfRepo().save(pdf);
	}

	public PdfPojo buildPdfPojoFromMultipartFile(MultipartFile file) throws IOException, SerialException, SQLException {
		PdfPojo pdf = new PdfPojo();
		byte[] barr = file.getBytes();
		Blob data = new SerialBlob(barr);
		pdf.setFilename(file.getOriginalFilename());
		pdf.setData(data);
		pdf.setNumberOfPages(getNumberOfPages(barr));
		pdf.setInsertDate(new Date());
		return pdf;
	}

	public List<PdfPojo> buildSplittedPdfPojo(List<PDDocument> documents, String originalFilename)
			throws IOException, SerialException, SQLException {
		List<PdfPojo> splittedPdf = null;
		if (documents != null && originalFilename != null) {
			splittedPdf = new ArrayList<>();
			int counter = 1;
			for (PDDocument splittedDocument : documents) {
				PdfPojo pojo = new PdfPojo();
				pojo.setFilename(originalFilename.substring(0, originalFilename.length() - 4) + "_" + counter + ".pdf");
				pojo.setNumberOfPages(splittedDocument.getNumberOfPages());
				pojo.setInsertDate(new Date());
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				splittedDocument.save(bos);
				pojo.setData(new SerialBlob(bos.toByteArray()));
				splittedPdf.add(pojo);
				++counter;
			}
		}
		return splittedPdf;
	}

	public List<PdfPojo> splitDocuments(PdfPojo pdf, int delimiter) throws SQLException, IOException {
		List<PdfPojo> splittedPdf = null;
		if (pdf != null) {
			splittedPdf = new ArrayList<>();
			byte[] barr = deserialize(pdf.getData());
			PDDocument docOriginal = PDDocument.load(barr);
			List<PDDocument> splittedDocuments = PdfUtils.splitDocument(docOriginal, delimiter);
			splittedPdf = buildSplittedPdfPojo(splittedDocuments, pdf.getFilename());
		}
		return splittedPdf;
	}

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

	public byte[] deserialize(Blob data) throws SQLException {
		byte[] barr = null;
		if (data.length() > 0) {
			barr = data.getBytes(1, (int) data.length());
		}
		return barr;
	}
}