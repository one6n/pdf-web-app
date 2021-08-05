package it.one6n.pdfwebapp.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.one6n.pdfwebapp.models.PdfEntry;

public interface PdfEntryRepo extends CrudRepository<PdfEntry, Long> {
	@Query("select p from PdfEntry p order by insertDate desc")
	List<PdfEntry> findAllOrderByInsertDateDesc();
}
