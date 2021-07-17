package it.one6n.pdfwebapp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RestResult {

	private boolean result;
	private Object data;

	public RestResult(boolean result) {
		this(result, null);
	}
}