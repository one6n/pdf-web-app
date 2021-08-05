package it.one6n.pdfwebapp.models;

import java.sql.Blob;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class PdfEntry {

	@Id
	@GeneratedValue
	private Long id;
	private String filename;
	private int numberOfPages;
	private Date insertDate;
	private Blob data;
}
