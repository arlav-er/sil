/*
 * Created on 14-nov-07
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.webservices.madreperla.servizi;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.engiweb.framework.configuration.ConfigSingleton;

/**
 * @author loc_esposito
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class UtilityXml {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(UtilityXml.class.getName());

	public static String replace(String subject, String find, String replace) {
		StringBuffer buf = new StringBuffer();
		int l = find.length();
		int s = 0;
		int i = subject.indexOf(find);
		while (i != -1) {
			buf.append(subject.substring(s, i));
			buf.append(replace);
			s = i + l;
			i = subject.indexOf(find, s);
		}
		buf.append(subject.substring(s));
		return buf.toString();
	}

	public static Document validaXml(String xmlString, String namespace, String pathSchemaFile)
			throws SAXNotRecognizedException, SAXNotSupportedException, IOException, SAXException {
		Document document = null;
		Schema schema = null;
		try {
			DocumentBuilder parser;
			parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = parser.parse(xmlString);
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			StreamSource schemaFile = new StreamSource(new File(pathSchemaFile));
			schema = factory.newSchema(schemaFile);
		} catch (ParserConfigurationException e) {
			_logger.error("Errore durante il parsing dell'xml", e);
			throw new IOException(e);
		}

		Validator validator = schema.newValidator();

		try {
			validator.validate(new DOMSource(document));
		} catch (SAXException e) {
			_logger.error("Errore durante la validazione dell'xml", e);
			throw new IOException(e);
		}

		return document;
	}

	/**
	 * Esegue la validazione XML utilizzando un dato schema XSD.
	 * 
	 * @param datiRichiestaXml
	 *            xml da validare
	 * @param SCHEMA_XSD
	 *            schema XSD da utilizzare per validare
	 * @return null se tutto va a buon fine, la stringa contenente l'errore se la validazione fallisce
	 */
	public static String getValidityErrors(String datiRichiestaXml, String SCHEMA_XSD) {

		try {
			String schemaLang = "http://www.w3.org/2001/XMLSchema";

			// get validation driver:
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

			// create schema by reading it from an XSD file:
			File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
					+ "xsd" + File.separator + SCHEMA_XSD);
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

	public static String getElementValue(Node node, String elementName) {
		Element element = (Element) node;

		NodeList elementNodeList = element.getElementsByTagName(elementName);
		Element subElement = (Element) elementNodeList.item(0);
		NodeList subElementList = subElement.getChildNodes();

		Node elNode = (Node) subElementList.item(0);

		if (elNode == null)
			return "";

		String value = elNode.getNodeValue().trim();

		return value;
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

	public static String nodeToString(Node document) throws TransformerException {
		String result = null;

		if (document != null) {
			StringWriter strWtr = new StringWriter();
			StreamResult strResult = new StreamResult(strWtr);
			TransformerFactory tfac = TransformerFactory.newInstance();
			Transformer t = tfac.newTransformer();
			t.transform(new DOMSource(document), strResult);
			result = strResult.getWriter().toString();
		}
		return result;
	}

	public static void appendTextChild(String _childName, String _childText, Element _parent, Document _dom) {
		Element el = _dom.createElement(_childName);
		Text text = _dom.createTextNode(_childText);
		el.appendChild(text);
		_parent.appendChild(el);
	}

	public static void appendNullableTextChild(String _childName, String _childText, Element _parent, Document _dom) {
		Element el = _dom.createElement(_childName);
		Text text = _dom.createTextNode(_childText);
		if (_childText.equals(""))
			el.setAttribute("xsi:nil", "true");
		else
			el.appendChild(text);
		_parent.appendChild(el);
	}

	public static String adeguamentoCaratteriNonValidi(String str) {
		int index = str.indexOf("&");
		if ((index != -1) && (!str.substring(index + 1, index + 5).equals("amp;"))) {
			str = str.substring(0, index + 1) + "amp;" + adeguamentoCaratteriNonValidi(str.substring(index + 1));
		}
		return str;
	}
}
