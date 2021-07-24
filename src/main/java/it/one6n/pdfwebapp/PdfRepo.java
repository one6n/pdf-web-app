package it.one6n.pdfwebapp;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PdfRepo extends CrudRepository<PdfPojo, Long> {
	@Query("select p from PdfPojo p order by insertDate desc")
	List<PdfPojo> findAllOrderByInsertDateDesc();
}
