package it.one6n.pdfwebapp;

import java.sql.Blob;
import java.util.Date;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

	public void savePdf(MultipartFile inputFile) {
		if (!inputFile.isEmpty()) {
			try {
				byte[] barr = inputFile.getBytes();
				Blob data = new SerialBlob(barr);
				PdfPojo pdf = buildPdfPojo(inputFile.getOriginalFilename(), data);
				getPdfRepo().save(pdf);
				log.debug("Saved: filename={}, data={}", pdf.getFilename(), barr.length);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public PdfPojo buildPdfPojo(String fileName, Blob data) {
		PdfPojo pdf = new PdfPojo();
		pdf.setFilename(fileName);
		pdf.setData(data);
		pdf.setInsertDate(new Date());
		return pdf;
	}
}
