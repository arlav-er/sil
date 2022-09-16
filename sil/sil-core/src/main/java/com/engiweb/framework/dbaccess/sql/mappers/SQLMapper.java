package com.engiweb.framework.dbaccess.sql.mappers;

/**
 * Questa interfaccia definisce le funzioni di un SQLMapper che è responsabile per la conversione da oggetti a stringa e
 * viceversa per gli oggetti di tipo sql.DATE e sql.TIMESTAMP che non sono standard e variano a seconda del vendor
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public interface SQLMapper {
	String getStringValue(int sqlType, Object objectValue);

	Object getObjectValue(int sqlType, String stringValue);

	/**
	 * Questo metodo converte un valore data di tipo Stringa in un oggetto
	 * 
	 * @param <B>String
	 *            </B> il valore di una data in una Stringa
	 * @return <B>Object</B> L'oggetto rappresentante la data
	 */
	Object getObjectValueForDate(String stringDateValue);

	/**
	 * Questo metodo converte un valore timestamp di tipo Stringa in un oggetto
	 * 
	 * @param <B>String
	 *            </B> il valore di un timestamp in una stringa
	 * @return <B>Object</B> L'oggetto rappresentante il timeStamp
	 */
	Object getObjectValueForTimeStamp(String stringTimeStampValue);

	/**
	 * Questo metodo converte un valore timestamp di tipo Object in un oggetto di tipo String
	 * 
	 * @param <B>Object
	 *            </B> objectTimeStampValue - L'oggetto rappresentante il timeStamp
	 * @return <B>String</B> il valore in un oggetto di tipo String
	 */
	String getStringValueForTimeStamp(Object objectTimeStampValue);

	/**
	 * Questo metodo converte un valore data di tipo Object in un oggetto di tipo String
	 * 
	 * @param <B>Object
	 *            </B> objectDateValue - L'oggetto rappresentante la data
	 * @return <B>String</B> il valore in un oggetto di tipo String
	 */
	String getStringValueForDate(Object objectDateValue);
} // end Interface SQLMapper
