package it.one6n.pdfwebapp.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduledTasks {

	private String removeOldPdfCron;

	@Scheduled(fixedDelay = 10000)
	public void removeOldPdf() {
		log.info("job REMOVE OLD PDF start");
		log.info("job REMOVE OLD PDF end");
	}
}
