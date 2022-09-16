package it.eng.sil.coop.webservices.apapi;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import it.eng.sil.coop.webservices.madreperla.servizi.UtilityXml;
import it.eng.sil.util.xml.XMLValidator;

public class XmlUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(XmlUtils.class.getName());

	public static String createXMLRisposta(String codice, String descrizione) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		String returnString = "";
		parser = factory.newDocumentBuilder();
		Document doc = parser.newDocument();
		Element SOElem = doc.createElement("Risposta");
		SOElem.setAttribute("schemaVersion", "1");
		SOElem.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		doc.appendChild(SOElem);

		Element esito = doc.createElement("Esito");
		SOElem.appendChild(esito);

		UtilityXml.appendTextChild("codice", codice, esito, doc);
		UtilityXml.appendTextChild("descrizione", descrizione, esito, doc);

		returnString = UtilityXml.domToString(doc);

		return returnString;
	}

	/**
	 * Controlla se il documento XML fornito in input è valido rispetto al xsd
	 * 
	 * @param inputXML
	 * @return
	 */
	public static boolean isXmlValid(String inputXML, File schemaFile) {

		String validityErrors = XMLValidator.getValidityErrors(inputXML, schemaFile);
		if (validityErrors != null) {
			_logger.error("XML di input: " + inputXML);
			_logger.error("Errore nella validazione del file xml: " + validityErrors);
			return false;
		}
		return true;

	}

}
