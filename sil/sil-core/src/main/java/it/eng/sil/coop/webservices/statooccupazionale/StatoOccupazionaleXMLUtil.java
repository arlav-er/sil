package it.eng.sil.coop.webservices.statooccupazionale;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import it.eng.sil.coop.webservices.madreperla.servizi.UtilityXml;
import it.eng.sil.module.cigs.bean.DatiAccorpante;

public class StatoOccupazionaleXMLUtil {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(StatoOccupazionaleXMLUtil.class.getName());

	/**
	 * Costruisce l'xml
	 * 
	 * @param codiceFiscale
	 * @param nome
	 * @param cognome
	 * @param domicilio
	 * @param stato
	 *            occupazionale
	 * @param mesi
	 *            anzianità
	 * @param data
	 *            dichiarazione IM
	 * @return String con l'XML
	 */
	public static String buildXmlNotifica(String codiceFiscaleAccorpato, String user, String codprovincia,
			String cln_pwd, Date opdate, Vector<String> accordi, DatiAccorpante anagAccorpante) {

		_logger.debug("buildXml() - start - genero xml notifica accorpamento");

		// Create instance of DocumentBuilderFactory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// Get the DocumentBuilder
		DocumentBuilder parser;
		String returnString = "";
		try {
			parser = factory.newDocumentBuilder();
			// Create blank DOM Document
			Document doc = parser.newDocument();
			// Insert the root element node
			Element accorpamentoElem = doc.createElement("acc:accorpamento");
			accorpamentoElem.setAttribute("schemaVersion", "1");
			accorpamentoElem.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			doc.appendChild(accorpamentoElem);

			Element lav = doc.createElement("Lavoratore");
			accorpamentoElem.appendChild(lav);

			UtilityXml.appendTextChild("acc:cf_accorpato", codiceFiscaleAccorpato, lav, doc);
			UtilityXml.appendTextChild("acc:cf_accorpante", anagAccorpante.getCodiceFiscale(), lav, doc);

			// anagrafica accorpante
			UtilityXml.appendTextChild("acc:nome_accorpante", anagAccorpante.getNome(), lav, doc);
			UtilityXml.appendTextChild("acc:cognome_accorpante", anagAccorpante.getCognome(), lav, doc);
			UtilityXml.appendTextChild("acc:sesso_accorpante", anagAccorpante.getSesso(), lav, doc);
			UtilityXml.appendTextChild("acc:comune_nascita_accorpante", anagAccorpante.getComuneNascita(), lav, doc);
			UtilityXml.appendTextChild("acc:data_nascita_accorpante", anagAccorpante.getDataNascita(), lav, doc);

			// Busta metadata, autenticazione ed orario accorpamento
			Element meta = doc.createElement("acc:metadata");
			accorpamentoElem.appendChild(meta);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
			StringBuffer sb = new StringBuffer(sdf.format(opdate));
			UtilityXml.appendTextChild("acc:OrarioInvio", sb.toString(), meta, doc);

			for (String accN : accordi) {
				UtilityXml.appendTextChild("acc:Accordi", accN.toString(), accorpamentoElem, doc);
			}

			try {
				returnString = UtilityXml.domToString(doc);
			} catch (TransformerException e) {
				_logger.error("Errore nella trasformazione del xml da inviare", e);
			}
			_logger.debug("buildXml() - end");

		} catch (ParserConfigurationException e) {
			_logger.error("Errore nel parsing dell'xml da inviare", e);
		}
		return returnString;

	}
}
