package it.one6n.pdfwebapp;

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
public class PdfPojo {

	@Id
	@GeneratedValue
	private Long id;
	private String filename;
	private Date insertDate;
	private Blob data;
}
