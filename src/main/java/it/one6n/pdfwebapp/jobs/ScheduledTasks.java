package it.one6n.pdfwebapp.jobs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.one6n.pdfwebapp.models.PdfEntry;
import it.one6n.pdfwebapp.services.PdfEntryService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Component
public class ScheduledTasks {

	@Autowired
	private PdfEntryService pdfEntryService;

	@Value("${jobs.removeoldpdf.maxage}")
	private long maxAge;

	// @Scheduled(cron = "${jobs.removeoldpdf.cron}")
	public void removeOldPdf() {
		log.info("job REMOVE OLD PDF start");

		List<PdfEntry> oldEntries = getPdfEntryService().findAllOrderByDateDesc();
		for (PdfEntry entry : oldEntries)
			if (System.currentTimeMillis() - entry.getInsertDate().getTime() > maxAge) {
				getPdfEntryService().deletePdfById(entry.getId());
				log.debug("deleting pdf={}, insertDate={}", entry.getFilename(), entry.getInsertDate());
			}

		log.info("job REMOVE OLD PDF end");
	}
}
