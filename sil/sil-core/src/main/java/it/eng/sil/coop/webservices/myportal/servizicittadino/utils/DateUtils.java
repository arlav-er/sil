package it.eng.sil.coop.webservices.myportal.servizicittadino.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateUtils {

	public static XMLGregorianCalendar stringToGregorianDate(String date)
			throws DatatypeConfigurationException, ParseException {

		if (date == null) {
			return null;
		}

		DateFormat dfIn = new SimpleDateFormat("dd/MM/yyyy");
		// DateFormat dfOut = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00");
		DateFormat dfOut = new SimpleDateFormat("yyyy-MM-dd");
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(dfOut.format(dfIn.parse(date)));

	}

	public static String gregorianDateToString(XMLGregorianCalendar gregorianDate) {

		if (gregorianDate == null) {
			return null;
		}

		Date date = null;
		DateFormat dfIn = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dfOut = new SimpleDateFormat("dd/MM/yyyy");
		String dateInXmlFormat = gregorianDate.toXMLFormat();

		try {
			date = dfIn.parse(dateInXmlFormat);
		} catch (ParseException e) {
			return null;
		}

		return dfOut.format(date);

	}

}
