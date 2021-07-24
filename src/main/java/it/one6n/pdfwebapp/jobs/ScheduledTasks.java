package it.one6n.pdfwebapp.jobs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.one6n.pdfwebapp.PdfPojo;
import it.one6n.pdfwebapp.PdfService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Component
public class ScheduledTasks {

	private String removeOldPdfCron;

	@Value("${jobs.removeoldpdf.maxage}")
	private long maxAge;

	@Autowired
	private PdfService pdfService;

	@Scheduled(cron = "${jobs.removeoldpdf.cron}")
	public void removeOldPdf() {
		log.info("job REMOVE OLD PDF start");

		List<PdfPojo> oldPdf = getPdfService().findAllOrderByDateDesc();
		for (PdfPojo pojo : oldPdf)
			if (System.currentTimeMillis() - pojo.getInsertDate().getTime() > maxAge) {
				getPdfService().deletePdfById(pojo.getId());
				log.debug("deleting pdf={}, insertDate={}", pojo.getFilename(), pojo.getInsertDate());
			}

		log.info("job REMOVE OLD PDF end");
	}
}
