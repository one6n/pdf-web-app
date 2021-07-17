package it.one6n.pdfwebapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

	public PdfPojo savePdf(MultipartFile inputFile) {
		if (inputFile.isEmpty())
			return null;

		try {
			PdfPojo pdf = getPdfRepo().save(buildPdfPojo(inputFile));
			log.debug("Saved: id={}, filename={}, size={}", pdf.getId(), pdf.getFilename(), pdf.getData().length());
			return pdf;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public PdfPojo buildPdfPojo(MultipartFile file) throws IOException, SerialException, SQLException {
		PdfPojo pdf = new PdfPojo();
		byte[] barr = file.getBytes();
		Blob data = new SerialBlob(barr);
		pdf.setFilename(file.getOriginalFilename());
		pdf.setData(data);
		pdf.setNumberOfPages(getNumberOfPages(barr));
		pdf.setInsertDate(new Date());
		return pdf;
	}

	public PdfPojo findPdfById(Long id) {
		return getPdfRepo().findById(id).orElse(null);
	}

	public byte[] deserialize(Blob data) throws SQLException {
		byte[] barr = null;
		if (data.length() > 0) {
			barr = data.getBytes(1, (int) data.length());
		}
		return barr;
	}

	public int getNumberOfPages(byte[] barr) throws IOException {
		return PDDocument.load(barr).getNumberOfPages();
	}

	public List<Long> splitDocuments(PdfPojo pdf, int delimiter) throws SQLException, IOException {
		List<Long> ids = null;
		if (pdf != null) {
			ids = new ArrayList<>(0);
			byte[] barr = deserialize(pdf.getData());
			PDDocument docOriginal = PDDocument.load(barr);
			List<PDDocument> splitted = PdfUtils.splitDocument(docOriginal, delimiter);
			int counter = 1;
			for (PDDocument splittedDocument : splitted) {
				PdfPojo pojo = new PdfPojo();
				pojo.setFilename(pdf.getFilename() + "_" + counter);
				pojo.setNumberOfPages(splittedDocument.getNumberOfPages());
				pojo.setInsertDate(new Date());
				OutputStream os = new ByteArrayOutputStream();
				/*
				 * byte[] barr = new byte[]; splittedDocument.save(os); os.write(barr);
				 */
				++counter;
			}
		}
		return ids;
	}
}
