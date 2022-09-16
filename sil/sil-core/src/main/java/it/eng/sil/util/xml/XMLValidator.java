package it.eng.sil.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

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
	public static Pattern codFiscRegEx = Pattern.compile(
			"([A-Z]{6}[\\dLMNPQRSTUV]{2}[A-Z][\\dLMNPQRSTUV]{2}[A-Z][\\dLMNPQRSTUV]{3}[A-Z])|(\\d{11})|(\\d{16})");
	public static Pattern capRegEx = Pattern.compile("\\d{5}");
	public static Pattern emailRegEx = Pattern.compile(
			"([A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*@[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*)?");
	// public static Pattern emailRegEx =
	// Pattern.compile("(([A-Za-z0-9]+_+)|([A-Za-z0-9]+\\-+)|([A-Za-z0-9]+\\.+)|([A-Za-z0-9]+\\++))*[A-Za-z0-9]+@((\\w+\\-+)|(\\w+\\.))*\\w{1,63}\\.[a-zA-Z]{2,6}");
	public static Pattern comuneRegEx = Pattern.compile("([A-Z]{1}\\d{3})|([0]{4})");
	public static Pattern ccnlRegEx = Pattern.compile("([0-9,A-Z]{3})|([A-Z]{2})");

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(XMLValidator.class.getName());

	/**
	 * Verifica se una data proprietà del SourceBean rispetta una pattern e l'eventuale obbligatorietà. Il controllo
	 * viene eseguito su tutti i record contenuti nel sourceBean e risultato di una query.
	 * 
	 * @param bean
	 *            SourceBean che contiene una serie di record con una lista di attributi
	 * @param fieldName
	 *            nome dell'attributo che si vuole verificare
	 * @param required
	 *            il campo è obbligatorio?
	 * @param regExp
	 *            eventuale Pattern che deve rispettare il campo, può essere null
	 * @param userFriendlyName
	 *            in caso di errore è il nome che verrà mostrato per indicare il campo.
	 * @throws MandatoryFieldException
	 *             Nel caso il campo fosse obbligatorio e non è presente in uno dei record del sourceBean o il
	 *             SourceBean passato come parametro sia null
	 * @throws FieldFormatException
	 *             Nel caso il campo debba rispettare un determinato Pattern e ciò non avviene.
	 */
	public static void checkFieldExists(SourceBean bean, String fieldName, boolean required, Pattern regExp,
			String userFriendlyName) throws MandatoryFieldException, FieldFormatException {
		if (bean == null && required) {
			throw new MandatoryFieldException(userFriendlyName);
		} else if (!required)
			return;
		Vector<SourceBean> rows = bean.getAttributeAsVector("ROW");
		for (SourceBean row : rows) {
			Object field = row.getAttribute(fieldName);
			existAndFormat(field, required, regExp, userFriendlyName);
		}
	}

	/**
	 * Verifica l'esistenza di almeno una riga all'interno del SourceBean. In caso contrario lancia una
	 * MandatoryFieldException indicando il nome passato come secondo parametro come campo mancante
	 */
	public static void checkRowsExists(SourceBean bean, String tag) throws MandatoryFieldException {
		Vector<SourceBean> rows = bean.getAttributeAsVector("ROW");
		if (rows.size() == 0)
			throw new MandatoryFieldException(tag);

	}

	/**
	 * Verifica presenza e formato del campo dell'XML in esame.
	 * 
	 * @param field2
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
	public static String existAndFormat(Object field2, boolean required, Pattern regExp, String userFriendlyField)
			throws MandatoryFieldException, FieldFormatException {

		String field = null;
		if (field2 != null) {
			field = field2.toString();
		}

		if (required) {
			if (StringUtils.isEmptyNoBlank(field)) {
				throw new MandatoryFieldException(userFriendlyField);
			}
		}

		if (field != null) {
			field = field.replace("\r\n", " ");
		}
		if (regExp != null && !StringUtils.isEmptyNoBlank(field)) {
			Matcher matcher = regExp.matcher(field);
			if (!matcher.matches())
				throw new FieldFormatException(userFriendlyField);
		}

		return field;
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
	public static String getValidityErrors(String datiRichiestaXml, String xsdFile) {

		try {
			String schemaLang = "http://www.w3.org/2001/XMLSchema";

			// get validation driver:
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

			// create schema by reading it from an XSD file:
			File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
					+ "xsd" + File.separator + xsdFile);
			StreamSource streamSource = new StreamSource(schemaFile);
			Schema schema = factory.newSchema(streamSource);
			Validator validator = schema.newValidator();
			// at last perform validation:
			StringReader datiXmlReader = new StringReader(datiRichiestaXml);
			StreamSource datiXmlStreamSource = new StreamSource(datiXmlReader);
			validator.validate(datiXmlStreamSource);
		} catch (Exception e) {
			_logger.error("Errore durante la validazione dell'xml", e);
			String returnString = e.getLocalizedMessage();
			return returnString;
		}
		return null;
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
	public static String getValidityErrors(String datiRichiestaXml, File schemaFile) {

		try {
			String schemaLang = "http://www.w3.org/2001/XMLSchema";

			// get validation driver:
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

			StreamSource streamSource = new StreamSource(schemaFile);
			Schema schema = factory.newSchema(streamSource);
			Validator validator = schema.newValidator();
			// at last perform validation:
			StringReader datiXmlReader = new StringReader(datiRichiestaXml);
			StreamSource datiXmlStreamSource = new StreamSource(datiXmlReader);
			validator.validate(datiXmlStreamSource);
		} catch (Exception e) {
			_logger.error("Errore durante la validazione dell'xml", e);
			String returnString = e.getLocalizedMessage();
			return returnString;
		}
		return null;
	}

	public static String domToString(Document document) throws TransformerException {
		String result = null;

		if (document != null) {
			StringWriter strWtr = new StringWriter();
			StreamResult strResult = new StreamResult(strWtr);
			TransformerFactory tfac = TransformerFactory.newInstance();
			Transformer t = tfac.newTransformer();
			t.transform(new DOMSource(document.getDocumentElement()), strResult);
			result = strResult.getWriter().toString();
		}
		return result;
	}

	public static void checkObjectFieldExists(Object field, String fieldName, boolean required, Pattern regExp,
			String userFriendlyName) throws MandatoryFieldException, FieldFormatException {

		existAndFormat(field, required, regExp, userFriendlyName);
	}

	public static Document parseXmlFile(String in) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(in));
		return db.parse(is);
	}
}
