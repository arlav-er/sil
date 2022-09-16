package it.eng.sil.module.mobilita;

import java.io.File;
import java.io.StringReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import it.eng.afExt.utils.StringUtils;

/**
 * Classe che contiene metodi ricorrenti nella validazione di un XML.<br>
 * <b>NON</b> sostituisce la validazione per XSD, semplicemente esegue alcuni controlli per permettere la creazione in
 * <code>it.eng.sil.module.mobilita.XMLMessageParser</code> dell'eventuale messaggio di errore.<br>
 * La validazione tramite XSD va comunque chiamata.
 * 
 * @author uberti
 */
public class XMLValidator {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(XMLValidator.class.getName());

	public static Pattern codFiscRegEx = Pattern.compile(
			"([A-Z]{6}[\\dLMNPQRSTUV]{2}[A-Z][\\dLMNPQRSTUV]{2}[A-Z][\\dLMNPQRSTUV]{3}[A-Z])|(\\d{11})|(\\d{16})");
	public static Pattern capRegEx = Pattern.compile("\\d{5}");
	public static Pattern emailRegEx = Pattern.compile(
			"(([A-Za-z0-9]+_+)|([A-Za-z0-9]+\\-+)|([A-Za-z0-9]+\\.+)|([A-Za-z0-9]+\\++))*[A-Za-z0-9]+@((\\w+\\-+)|(\\w+\\.))*\\w{1,63}\\.[a-zA-Z]{2,6}");
	public static Pattern comuneRegEx = Pattern.compile("([A-Z]{1}\\d{3})|([0]{4})");
	public static Pattern ccnlRegEx = Pattern.compile("([0-9,A-Z]{3})|([A-Z]{2})");

	/**
	 * Verifica presenza e formato del campo dell'XML in esame.
	 * 
	 * @param xmlField
	 *            String campo da verificare
	 * @param required
	 *            boolean indica se il campo deve essere obbligatorio
	 * @param regExp
	 *            Pattern regular expression per il formato del campo
	 * @param userFriendlyField
	 *            valore user-friendly da mostrare nel messaggio di errore
	 * @return String il campo stesso
	 * @throws MandatoryFieldException
	 *             se il campo e' vuoto o <code>null</code>
	 * @throws FieldFormatException
	 *             se il campo non rispetta la regular expression
	 */
	public static String existAndFormat(String xmlField, boolean required, Pattern regExp, String userFriendlyField)
			throws MandatoryFieldException, FieldFormatException {

		String field = xmlField;

		if (required)
			if (StringUtils.isEmptyNoBlank(field)) {
				throw new MandatoryFieldException(userFriendlyField);
			}

		if (regExp != null && !StringUtils.isEmptyNoBlank(field)) {
			Matcher matcher = regExp.matcher(field);
			if (!matcher.matches())
				throw new FieldFormatException(userFriendlyField);
		}

		return field;
	}

	/**
	 * Verifica se il campo passato e' numerico e, se necessario, se e' obbligatorio.
	 * 
	 * @param xmlField
	 *            campo dell'XML da controllare
	 * @param required
	 *            boolean per decidere se il campo e' obbligatorio o meno
	 * @param message
	 *            valore user-friendly da mostrare nel messaggio di errore
	 * @return String il campo stesso
	 * @throws MandatoryFieldException
	 *             se il campo e' vuoto o <code>null</code>
	 * @throws FieldFormatException
	 *             se il campo non e' numerico
	 */
	public static String isNumber(String xmlField, boolean required, String userFriendlyField)
			throws MandatoryFieldException, FieldFormatException {

		String field = xmlField;

		if (required) {
			if (StringUtils.isEmptyNoBlank(field)) {
				throw new MandatoryFieldException(userFriendlyField);
			}
		}

		char[] chars = field.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			if (!Character.isDigit(chars[i])) {
				throw new FieldFormatException(userFriendlyField);
			}
		}

		return field;
	}

	/**
	 * Verifica se il campo dato e' compreso nella lunghezza richiesta.
	 * 
	 * @param xmlField
	 *            campo dell'XML da controllare
	 * @param min
	 *            lunghezza minima della stringa
	 * @param max
	 *            lunghezza massima della stringa
	 * @param messageParameter
	 *            valore user-friendly da mostrare nel messaggio di errore
	 * @return String il campo stesso
	 * @throws FieldFormatException
	 *             se il campo non e' della lunghezza richiesta
	 */
	public static String isFieldLengthBetween(String xmlField, int min, int max, String messageParameter)
			throws FieldFormatException {

		if (xmlField == null)
			throw new FieldFormatException(messageParameter);

		String field = xmlField;

		if (!(xmlField.length() > min && xmlField.length() < max))
			throw new FieldFormatException(messageParameter);

		return field;
	}

	/**
	 * Verifica se il campo dato e' maggiore della lunghezza minima richiesta.
	 * 
	 * @param xmlField
	 *            campo dell'XML da controllare
	 * @param min
	 *            lunghezza minima della stringa
	 * @param messageParameter
	 *            valore user-friendly da mostrare nel messaggio di errore
	 * @return String il campo stesso
	 * @throws FieldFormatException
	 *             se il campo non e' della lunghezza richiesta
	 */
	public static String isFieldLengthMin(String xmlField, int min, String messageParameter)
			throws FieldFormatException {

		if (xmlField == null)
			throw new FieldFormatException(messageParameter);

		String field = xmlField;

		if (xmlField.length() < min)
			throw new FieldFormatException(messageParameter);

		return field;
	}

	/**
	 * Verifica se il campo dato e' minore nella lunghezza richiesta.
	 * 
	 * @param xmlField
	 *            campo dell'XML da controllare
	 * @param max
	 *            lunghezza massima della stringa
	 * @param messageParameter
	 *            valore user-friendly da mostrare nel messaggio di errore
	 * @return String il campo stesso
	 * @throws FieldFormatException
	 *             se il campo non e' della lunghezza richiesta
	 */
	public static String isFieldLengthMax(String xmlField, int max, String messageParameter)
			throws FieldFormatException {

		if (xmlField == null)
			throw new FieldFormatException(messageParameter);

		String field = xmlField;

		if (xmlField.length() > max)
			throw new FieldFormatException(messageParameter);

		return field;
	}

	/**
	 * Verifica se il campo dato e' della lunghezza richiesta.
	 * 
	 * @param xmlField
	 *            campo dell'XML da controllare
	 * @param requiredLength
	 *            lunghezza richiesta della stringa
	 * @param messageParameter
	 *            valore user-friendly da mostrare nel messaggio di errore
	 * @return String il campo stesso
	 * @throws FieldFormatException
	 *             se il campo non e' della lunghezza richiesta
	 */
	public static String isFieldLengthEqual(String xmlField, int requiredLength, String messageParameter)
			throws FieldFormatException {

		if (xmlField == null)
			throw new FieldFormatException(messageParameter);

		String field = xmlField;

		if (xmlField.length() != requiredLength)
			throw new FieldFormatException(messageParameter);

		return field;
	}

	/**
	 * Data una stringa alfanumerica, ritorna solo i caratteri numerici in essa contenuti
	 * 
	 * @param stringToParse
	 *            stringa da controllare
	 * @return String composta da caratteri numerici
	 */
	public static String getNumber(String stringToParse) {

		if (StringUtils.isEmptyNoBlank(stringToParse)) {
			return stringToParse;
		}

		StringBuffer sb = new StringBuffer();
		char[] chars = stringToParse.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			if (Character.isDigit(chars[i])) {
				sb.append(chars[i]);
			}
		}

		return sb.toString();
	}

	/**
	 * Verifica se il campo dato e' nella sequenza di valori richiesti.
	 * 
	 * @param xmlField
	 *            campo dell'XML da controllare
	 * @param enumeration
	 *            Vector sequenza di valori richiesti
	 * @param messageParameter
	 *            valore user-friendly da mostrare nel messaggio di errore
	 * @return String il campo stesso
	 * @throws FieldFormatException
	 *             se il campo non e' della lunghezza richiesta
	 */
	public static String isFieldInEnumeration(String xmlField, Vector<String> enumeration, String messageParameter)
			throws FieldFormatException {

		if (xmlField == null)
			throw new FieldFormatException(messageParameter);

		String field = xmlField;

		if (!enumeration.contains(field))
			throw new FieldFormatException(messageParameter);

		return field;

	}

	/**
	 * Valida l'xml della domanda da inviare
	 * 
	 * @param xsdFile
	 *            il file XSD per la validazione
	 * @param datiRichiestaXml
	 *            xml da validare
	 * @return String con gli errori di validazione
	 */
	public static String getValidityErrors(File xsdFile, String datiRichiestaXml) {

		try {
			String schemaLang = "http://www.w3.org/2001/XMLSchema";

			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);
			StreamSource streamSource = new StreamSource(xsdFile);
			Schema schema = factory.newSchema(streamSource);
			Validator validator = schema.newValidator();

			StringReader datiXmlReader = new StringReader(datiRichiestaXml);
			StreamSource datiXmlStreamSource = new StreamSource(datiXmlReader);
			validator.validate(datiXmlStreamSource);

		} catch (Exception e) {
			_logger.error("getValidityErrors(String)", e);
			String returnString = e.getLocalizedMessage();
			return returnString;
		}

		return null;
	}

}
