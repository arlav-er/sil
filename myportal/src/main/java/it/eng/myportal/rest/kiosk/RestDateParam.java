package it.eng.myportal.rest.kiosk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.WebApplicationException;

public class RestDateParam {

	private static final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
	private Date date;

	public RestDateParam(String dateStr) throws WebApplicationException {
		try {
			if (dateStr != null && !dateStr.isEmpty()) date = df.parse(dateStr);
		} catch (ParseException ex) {
			throw new WebApplicationException(ex);
		}
	}

	public Date getDate() {
		return date;
	}
}
