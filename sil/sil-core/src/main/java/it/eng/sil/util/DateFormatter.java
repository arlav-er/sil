package it.eng.sil.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;

/**
 * Questa classe definisce il formato di Date e Timestamp: nel costruttore viene specificato il formato
 */
public class DateFormatter {

	private String formatoData = "";
	private String formatoTimestamp = "";

	public DateFormatter() {
		this("dd/MM/yyyy", "dd/MM/yyyy hh:mm:ss");
	}

	public DateFormatter(String _formatoData, String _formatoTimestamp) {
		this.formatoData = _formatoData;
		this.formatoTimestamp = _formatoTimestamp;
	}

	/**
	 * data una data String restituisce una data SqlDate nel formato precedentemente specificato nel costruttore
	 */
	public java.sql.Date getSqlDate(String dataStr) {
		SimpleDateFormat formatter = new SimpleDateFormat(this.formatoData);
		java.util.Date data = formatter.parse(dataStr, new ParsePosition(0));

		return new java.sql.Date(data.getTime());
	}

	/**
	 * data una data-ora String restituisce una data-ora SqlTimestamp nel formato precedentemente specificato nel
	 * costruttore
	 */
	public java.sql.Timestamp getSqlTimestamp(String dataStr) {
		SimpleDateFormat formatter = new SimpleDateFormat(this.formatoTimestamp);
		java.util.Date data = formatter.parse(dataStr, new ParsePosition(0));

		return new java.sql.Timestamp(data.getTime());
	}

	/**
	 * Trasforma un oggetto java.sql.Date in una Stringa
	 */
	public String getStringDate(java.sql.Date data) {
		String str = "";

		if (data != null) {
			// formattatore per trasformare stringhe in date
			SimpleDateFormat sdf = new SimpleDateFormat(this.formatoData);
			str = sdf.format(new java.util.Date(data.getTime()));
		}
		return str;
	}

	/**
	 * Rende la data di oggi in formato Stringa
	 */
	public String getStringDate() {
		String str = "";

		// formattatore per trasformare stringhe in date
		SimpleDateFormat sdf = new SimpleDateFormat(this.formatoData);
		str = sdf.format(new java.util.Date());

		return str;
	}

}
