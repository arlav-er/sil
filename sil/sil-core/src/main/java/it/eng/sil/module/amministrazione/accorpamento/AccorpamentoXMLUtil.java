package it.eng.sil.module.amministrazione.accorpamento;

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

/**
 * Classe di utilita' per la costruzione degli XML destinati all'accorpamento dei lavoratori
 * 
 * @author uberti
 */
public class AccorpamentoXMLUtil {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AccorpamentoXMLUtil.class.getName());

	/**
	 * Costruisce l'xml da inviare al SIL Regionale
	 * 
	 * @param codiceFiscaleAccorpato
	 * @param user
	 * @param codprovincia
	 * @param cln_pwd
	 * @param opdate
	 * @param accordi
	 * @param anagAccorpante
	 * @return String con l'XML da inviare al SIL Regionale
	 */
	public static String buildXmlNotifica(String codiceFiscaleAccorpato, String user, String codprovincia,
			String cln_pwd, Date opdate, Vector<String> accordi, DatiAccorpante anagAccorpante) {

		_logger.debug("buildXml() - start - genero xml notifica accorpamento");

		// se non ci sono accordi, non dovevo nemmeno essere qui
		assert accordi != null;

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
			accorpamentoElem.setAttribute("xmlns:acc", "http://regione.emilia-romagna.it/sil/accorpamento/1");
			accorpamentoElem.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			accorpamentoElem.setAttribute("xsi:schemaLocation",
					"http://regione.emilia-romagna.it/sil/accorpamento/1 lavoratori_accorpati.xsd");
			doc.appendChild(accorpamentoElem);

			Element lav = doc.createElement("acc:Lavoratori");
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

			Element mitt = doc.createElement("acc:SILMittente");
			mitt.setAttribute("password", cln_pwd);
			mitt.setAttribute("username", user);
			meta.appendChild(mitt);

			String padded = zeroPad(anagAccorpante.getPrgLavoratoreAccorpante().intValue(), 11);
			String coddomanda = "ACCLAV0" + codprovincia + padded;
			UtilityXml.appendTextChild("acc:codiceDomanda", coddomanda, meta, doc);

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

	private static String zeroPad(int value, int size) {
		String s = "00000000000000" + value;
		return s.substring(s.length() - size);
	}

}
