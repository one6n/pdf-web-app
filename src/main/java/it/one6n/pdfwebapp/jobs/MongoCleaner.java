package it.one6n.pdfwebapp.jobs;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.one6n.pdfwebapp.models.PdfMongoEntry;
import it.one6n.pdfwebapp.services.PdfMongoService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Component
public class MongoCleaner {

	public static final List<String> MONGO_PERSISTENT_COLLECTIONS = List.of("fs.chunks", "fs.files", "pdfMongoEntry");

	@Autowired
	private PdfMongoService pdfMongoService;

	@Value("${jobs.removeoldpdf.maxage}")
	private long maxAge;

	@Scheduled(cron = "${jobs.removeoldpdf.cron}")
	public void removeOldPdf() {
		log.info("job REMOVE OLD PDF from MONGO start");

		List<PdfMongoEntry> oldEntries = getPdfMongoService().findAllOrderByInsertDateDesc();
		log.debug("oldEntries= {}", oldEntries != null ? oldEntries : null);
		if (oldEntries != null && oldEntries.size() > 0)
			for (PdfMongoEntry entry : oldEntries)
				if (System.currentTimeMillis() - entry.getInsertDate().getTime() > getMaxAge()) {
					getPdfMongoService().deletePdfEntryAndFile(entry.getId(), entry.getGridFsId(),
							entry.getInsertDate());
					log.debug("deleted pdf={}, insertDate={}", entry.getFilename(), entry.getInsertDate());
				}
		log.info("job REMOVE OLD PDF from MONGO end");
	}

	@Scheduled(cron = "${jobs.removeoldcollection.cron}")
	public void removeOldCollection() {
		log.info("job REMOVE OLD Collection from MONGO start");
		Set<String> collections = getPdfMongoService().getCollections();
		LocalDate currentDate = LocalDate.now();
		for (String collection : collections) {
			if (!MONGO_PERSISTENT_COLLECTIONS.contains(collection)) {
				log.debug("collection={}", collection);
				String stringDate = collection.split("_")[1].substring(0, 8);
				LocalDate collectionDate = LocalDate.parse(stringDate, DateTimeFormatter.BASIC_ISO_DATE);
				if (collectionDate.isBefore(currentDate)) {
					getPdfMongoService().dropCollection(collection);
					log.info("deleted collection={}", collection);
				}
			}
		}
		log.info("job REMOVE OLD Collection from MONGO end");
	}
}
