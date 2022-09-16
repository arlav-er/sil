package it.eng.sil.coop.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Converter {

	public static String dateConverter(String sourceFormat, String destFormat, String data) throws ParseException {
		DateFormat formatter = new SimpleDateFormat(sourceFormat);
		DateFormat convertitore = new SimpleDateFormat(destFormat);

		Date _data = null;

		if (data != null && !data.equals("")) {
			_data = (Date) formatter.parse(data);
			return convertitore.format(_data);
		} else
			return "";

	}

	public static String flagConverter(String source) {
		if (source == null || source.equals(""))
			return "";

		if (source.equals("SI") || source.equals("S"))
			return "S";
		else if (source.equals("NO") || source.equals("N"))
			return "N";

		return "";
	}

}
