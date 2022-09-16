package it.eng.sil.myaccount.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtils {

	private static final Log log = LogFactory.getLog(XmlUtils.class);

	/**
	 * Valida un xml con un file xsd
	 * 
	 * @param xsdFile
	 *            il percorso completo del file XSD per la validazione
	 * @throws IOException
	 * @throws SAXException
	 */
	public static void validateXml(String datiRichiestaXml, String xsdFile) throws SAXException, IOException {

		Schema schema = XmlUtils.getXsdSchema(xsdFile);

		Validator validator = schema.newValidator();
		// at last perform validation:
		StringReader datiXmlReader = new StringReader(datiRichiestaXml);
		StreamSource datiXmlStreamSource = new StreamSource(datiXmlReader);
		validator.validate(datiXmlStreamSource);
	}

	public static Schema getXsdSchema(String xsdFile) throws SAXException {
		String schemaLang = "http://www.w3.org/2001/XMLSchema";

		// get validation driver:
		SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

		Class<Utils> classe = Utils.class;
		ClassLoader loader = classe.getClassLoader();
		InputStream is = loader.getResourceAsStream("xsd" + File.separator + xsdFile);
		StreamSource streamSource = new StreamSource(is);
		Schema schema = factory.newSchema(streamSource);
		return schema;
	}

	/**
	 * Converte un Document in stringa
	 * 
	 * @param document
	 * @return
	 * @throws TransformerException
	 */

	public static String getStringFromDoc(org.w3c.dom.Document doc) {
		DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
		LSSerializer lsSerializer = domImplementation.createLSSerializer();
		return lsSerializer.writeToString(doc);
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

	public static Document stringToDom(String docustring) throws ParserConfigurationException, SAXException,
			IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource source = new InputSource(new StringReader(docustring));

		return builder.parse(source);
	}

}
