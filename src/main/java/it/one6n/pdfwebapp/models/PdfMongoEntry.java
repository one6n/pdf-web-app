package it.one6n.pdfwebapp.models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document
public class PdfMongoEntry {

	@Id
	private String id;
	private ObjectId gridFsId;
	private String filename;
	private int numberOfPages;
	private Date insertDate;
}
