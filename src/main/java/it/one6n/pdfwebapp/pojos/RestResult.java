package it.one6n.pdfwebapp.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RestResult {

	private boolean result;
	private Object data;

	public RestResult(boolean result) {
		this(result, null);
	}
}