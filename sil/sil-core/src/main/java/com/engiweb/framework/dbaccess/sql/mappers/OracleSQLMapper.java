package com.engiweb.framework.dbaccess.sql.mappers;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.MessageFormat;
import java.text.ParseException;

import com.engiweb.framework.dbaccess.Configurator;
import com.engiweb.framework.dbaccess.EngDateFormatter;

/**
 * Questa classe è responsabile per la conversione da oggetti a stringa e viceversa per gli oggetti di tipo sql.DATE e
 * sql.TIMESTAMP che non sono standard e variano a seconda del vendor Questa classe implementa il Mapper per Database
 * ORACLE
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class OracleSQLMapper implements SQLMapper {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(OracleSQLMapper.class.getName());

	public String getStringValue(int sqlType, Object objectValue) {
		if (objectValue == null) {
			_logger.debug("OracleSQLMapper::getStringValue: objectValue nullo");

			return null;
		} // if (objectValue == null)
		String stringValue = null;
		try {
			switch (sqlType) {
			case Types.DATE:
				stringValue = getStringValueForDate(objectValue);
				break;
			case Types.TIMESTAMP:
				stringValue = getStringValueForTimeStamp(objectValue);
				break;
			case Types.CLOB:
				stringValue = ((Clob) objectValue).getSubString(1, (int) ((Clob) objectValue).length());
				break;
			default:
				stringValue = objectValue.toString();
				break;
			} // switch (sqlType)
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "OracleSQLMapper::getStringValue:", ex);

		} // catch (Exception ex)
		return stringValue;
	} // public String getStringValue(int sqlType, Object objectValue)

	public Object getObjectValue(int sqlType, String stringValue) {
		if (stringValue == null) {
			_logger.debug("OracleSQLMapper::getObjectValue: stringValue nullo");

			return null;
		} // if (stringValue == null)
		Object objectValue = null;
		try {
			switch (sqlType) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				objectValue = stringValue;
				break;
			case Types.BIGINT:
			case Types.BIT:
			case Types.DECIMAL:
			case Types.DOUBLE:
			case Types.FLOAT:
			case Types.INTEGER:
			case Types.NUMERIC:
			case Types.REAL:
			case Types.SMALLINT:
			case Types.TINYINT:
				objectValue = new BigDecimal(stringValue);
				break;
			case Types.BINARY:
			case Types.LONGVARBINARY:
			case Types.VARBINARY:
				objectValue = stringValue.getBytes();
				break;
			case Types.DATE:
				objectValue = getObjectValueForDate(stringValue);
				break;
			case Types.TIMESTAMP:
				objectValue = getObjectValueForTimeStamp(stringValue);
				break;
			default:
				_logger.debug("OracleSQLMapper::getObjectValue: tipo sql non previsto");

				objectValue = stringValue;
			} // switch (sqlType)
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "OracleSQLMapper::getObjectValue:", ex);

		} // catch (Exception ex)
		return objectValue;
	} // public Object getObjectValue(int sqlType, String stringValue)

	/**
	 * Questo metodo converte un valore timestamp di tipo Object in un oggetto di tipo String
	 * 
	 * @param <B>Object
	 *            </B> objectTimeStampValue - L'oggetto rappresentante il timeStamp
	 * @return <B>String}> il valore in un oggetto di tipo String
	 */
	public String getStringValueForTimeStamp(Object objectTimeStampValue) {
		if (objectTimeStampValue == null) {
			_logger.debug("OracleSQLMapper::getStringValueForTimeStamp: objectTimeStampValue nullo");

			return null;
		} // if (objectTimeStampValue == null)
		if (!(objectTimeStampValue instanceof Timestamp)) {
			_logger.debug(
					"OracleSQLMapper::getStringValueForTimeStamp: objectTimeStampValue non è di tipo java.sql.Timestamp");

			return objectTimeStampValue.toString();
		} // if (!(objectTimeStampValue instanceof java.sql.Timestamp)))
		String result = null;
		try {
			EngDateFormatter engDateFormatter = new EngDateFormatter(Configurator.getInstance().getTimeStampFormat());
			MessageFormat mf = engDateFormatter.getMessageFormat();
			Object[] arguments = new Object[engDateFormatter.getMessageFormatNumberOfParameters()];
			arguments[engDateFormatter.get_dayPositionInMessageFormat()] = getStringWithANumberOfDigit(
					engDateFormatter.get_dayNumberOfDigit(), ((Timestamp) objectTimeStampValue).getDate());
			arguments[engDateFormatter.get_monthPositionInMessageFormat()] = getStringWithANumberOfDigit(
					engDateFormatter.get_monthNumberOfDigit(), (((Timestamp) objectTimeStampValue).getMonth() + 1));
			arguments[engDateFormatter.get_yearPositionInMessageFormat()] = getStringWithANumberOfDigit(
					engDateFormatter.get_yearNumberOfDigit(), (((Timestamp) objectTimeStampValue).getYear() + 1900));
			arguments[engDateFormatter.get_hourPositionInMessageFormat()] = getStringWithANumberOfDigit(
					engDateFormatter.get_hourNumberOfDigit(), (((Timestamp) objectTimeStampValue).getHours()));
			arguments[engDateFormatter.get_minutesPositionInMessageFormat()] = getStringWithANumberOfDigit(
					engDateFormatter.get_minutesNumberOfDigit(), (((Timestamp) objectTimeStampValue).getMinutes()));
			arguments[engDateFormatter.get_secondsPositionInMessageFormat()] = getStringWithANumberOfDigit(
					engDateFormatter.get_secondsNumberOfDigit(), (((Timestamp) objectTimeStampValue).getSeconds()));
			result = mf.format(arguments);
		} // try
		catch (ParseException pe) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "OracleSQLMapper::getStringValueForTimeStamp:", pe);

		} // catch (ParseException pe) try
		return result;
	} // public String getStringValueForTimeStamp(Object objectTimeStampValue)

	/**
	 * Questo metodo converte un valore data di tipo Object in un oggetto di tipo String
	 * 
	 * @param <B>Object
	 *            </B> objectDateValue - L'oggetto rappresentante la data
	 * @return <B>String}> il valore in un oggetto di tipo String
	 */
	public String getStringValueForDate(Object objectDateValue) {
		if (objectDateValue == null) {
			_logger.debug("OracleSQLMapper::getStringValueForDate: objectDateValue nullo");

			return null;
		} // if (objectDateValue == null)
		if (!(objectDateValue instanceof Date)) {
			_logger.debug("OracleSQLMapper::getStringValueForDate: objectDateValue non è di tipo java.sql.Date");

			return objectDateValue.toString();
		} // if (!(objectDateValue instanceof Date))
		String result = null;
		try {
			EngDateFormatter engDateFormatter = new EngDateFormatter(Configurator.getInstance().getDateFormat());
			MessageFormat mf = engDateFormatter.getMessageFormat();
			Object[] arguments = new Object[engDateFormatter.getMessageFormatNumberOfParameters()];
			arguments[engDateFormatter.get_dayPositionInMessageFormat()] = getStringWithANumberOfDigit(
					engDateFormatter.get_dayNumberOfDigit(), ((Date) objectDateValue).getDate());
			arguments[engDateFormatter.get_monthPositionInMessageFormat()] = getStringWithANumberOfDigit(
					engDateFormatter.get_monthNumberOfDigit(), (((Date) objectDateValue).getMonth() + 1));
			arguments[engDateFormatter.get_yearPositionInMessageFormat()] = getStringWithANumberOfDigit(
					engDateFormatter.get_yearNumberOfDigit(), (((Date) objectDateValue).getYear() + 1900));
			result = mf.format(arguments);
		} // try
		catch (ParseException pe) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "OracleSQLMapper::getStringValueForDate:", pe);

		} // catch (ParseException pe) try
		return result;
	} // public String getStringValueForDate(Object objectDateValue)

	/**
	 * Questo metodo converte un valore data di tipo Stringa in un oggetto
	 * 
	 * @param <B>String}>
	 *            il valore di una data in una Stringa
	 * @return <B>Object</B> L'oggetto rappresentante la data
	 */
	public Object getObjectValueForDate(String stringDateValue) {
		if (stringDateValue == null) {
			_logger.debug("OracleSQLMapper::getObjectValueForDate: stringDateValue nullo");

			return null;
		} // if (stringDateValue == null)
		Date result = null;
		try {
			EngDateFormatter engDateFormatter = new EngDateFormatter(Configurator.getInstance().getDateFormat());
			engDateFormatter.format(stringDateValue);
			int day = (engDateFormatter.getDay() != -1 ? engDateFormatter.getDay() : 0);
			int month = (engDateFormatter.getMonth() != -1 ? engDateFormatter.getMonth() : 0);
			int year = (engDateFormatter.getYear() != -1 ? engDateFormatter.getYear() : 0);
			result = new Date(year - 1900, month - 1, day);
		} // try
		catch (ParseException pe) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "OracleSQLMapper::getObjectValueForDate:", pe);

		} // catch (ParseException pe) try
		return result;
	} // public Object getObjectValueForDate(String stringDateValue)

	/**
	 * Questo metodo converte un valore timestamp di tipo Stringa in un oggetto
	 * 
	 * @param <B>String}>
	 *            il valore di un timestamp in una stringa
	 * @return <B>Object</B> L'oggetto rappresentante il timeStamp
	 */
	public Object getObjectValueForTimeStamp(String stringTimeStampValue) {
		if (stringTimeStampValue == null) {
			_logger.debug("OracleSQLMapper::getObjectValueForTimeStamp: stringTimeStampValue nullo");

			return null;
		} // if (timeStampValue == null)
		Timestamp result = null;
		try {
			EngDateFormatter engDateFormatter = new EngDateFormatter(Configurator.getInstance().getTimeStampFormat());
			engDateFormatter.format(stringTimeStampValue);
			int day = (engDateFormatter.getDay() != -1 ? engDateFormatter.getDay() : 0);
			int month = (engDateFormatter.getMonth() != -1 ? engDateFormatter.getMonth() : 0);
			int year = (engDateFormatter.getYear() != -1 ? engDateFormatter.getYear() : 0);
			int hour = (engDateFormatter.getHours() != -1 ? engDateFormatter.getHours() : 0);
			int minute = (engDateFormatter.getMinutes() != -1 ? engDateFormatter.getMinutes() : 0);
			int second = (engDateFormatter.getSeconds() != -1 ? engDateFormatter.getSeconds() : 0);
			int nano = 0;
			result = new Timestamp(year - 1900, month - 1, day, hour, minute, second, nano);
		} // try
		catch (ParseException pe) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "OracleSQLMapper::getObjectValueForDate:", pe);

		} // catch (ParseException pe) try
		return result;
	} // public Object getObjectValueForTimeStamp(String stringTimeStampValue)

	private String getStringWithANumberOfDigit(int numberOfDigit, int value) {
		String valueStr = String.valueOf(value);
		if (valueStr.length() == numberOfDigit)
			return valueStr;
		if ((valueStr.length() < numberOfDigit)) {
			StringBuffer sb = new StringBuffer(numberOfDigit);
			for (int i = 0; i < (numberOfDigit - valueStr.length()); i++)
				sb.append("0");
			sb.append(valueStr);
			return sb.toString();
		} // if ((valueStr.length() < numberOfDigit))
		return valueStr.substring(0, numberOfDigit);
	} // public getStringWithANumberOfDigit(int numberOfDigit, int value)
} // public class OracleSQLMapper implements SQLMapper
