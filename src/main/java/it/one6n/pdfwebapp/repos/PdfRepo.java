package it.one6n.pdfwebapp.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.one6n.pdfwebapp.models.PdfPojo;

public interface PdfRepo extends CrudRepository<PdfPojo, Long> {
	@Query("select p from PdfPojo p order by insertDate desc")
	List<PdfPojo> findAllOrderByInsertDateDesc();
}
