/*
 * Created on May 30, 2007
 */
package it.eng.sil.coordinamento.wsClient.np;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.eng.sil.coordinamento.utils.XMLUtils;

/**
 * @author savino
 */
public class RispostaXML {

	private static Logger logger = Logger.getLogger(RispostaXML.class.getName());

	protected String codiceEsito;
	protected String descrizioneEsito;
	protected String tipoRisposta;

	public RispostaXML(String codiceEsito, String descEsito, String tipo) {
		this.codiceEsito = codiceEsito;
		this.descrizioneEsito = descEsito;
		this.tipoRisposta = tipo;
	}

	public RispostaXML(String xml) throws Exception {
		if (xml == null)
			throw new IllegalArgumentException("La stringa xml e' nulla");

		InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(false);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document doc = documentBuilder.parse(is);
		Element root = doc.getDocumentElement();
		NodeList nl = root.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			short tipo = node.getNodeType();
			// Considero solo gli elementi immediatamente sotto la radice
			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (node.getNodeName().equals("EsitoOutput")) {
				NodeList datiEsito = node.getChildNodes();
				for (int j = 0; j < datiEsito.getLength(); j++) {
					Node nodoEsito = datiEsito.item(j);
					short tipo_1 = nodoEsito.getNodeType();
					if (nodoEsito.getNodeName().equals("Codice_Esito"))
						setCodiceEsito(XMLUtils.getNodeValue(nodoEsito));
					if (nodoEsito.getNodeName().equals("Descr_Esito"))
						setDescrizioneEsito(XMLUtils.getNodeValue(nodoEsito));
					if (nodoEsito.getNodeName().equals("Tipo_Risposta"))
						setTipoRisposta(XMLUtils.getNodeValue(nodoEsito));
				}
			} else if (node.getNodeName().equals("Evento") || node.getNodeName().equals("RicevutaConsegna")) {
			} else
				throw new Exception("Elemento xml non previsto: " + node.getNodeName());
		}
	}

	/**
	 * @return
	 */
	public String getCodiceEsito() {
		return codiceEsito;
	}

	/**
	 * @return
	 */
	public String getDescrizioneEsito() {
		return descrizioneEsito;
	}

	/**
	 * @return
	 */
	public String getTipoRisposta() {
		return tipoRisposta;
	}

	/**
	 * @param string
	 */
	public void setCodiceEsito(String string) {
		codiceEsito = string;
	}

	/**
	 * @param string
	 */
	public void setDescrizioneEsito(String string) {
		descrizioneEsito = string;
	}

	/**
	 * @param string
	 */
	public void setTipoRisposta(String string) {
		tipoRisposta = string;
	}

	public String toXMLString() {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(false);
		try {

			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document doc = documentBuilder.newDocument();

			Element e = doc.createElement("PSEvento");
			doc.appendChild(e);
			e.setAttribute("xmlns", "http://www.pubsub.welfare.gov.it/evento");
			Element esito = doc.createElement("EsitoOutput");
			e.appendChild(esito);
			Element codice = doc.createElement("Codice_Esito");
			Node n = doc.createTextNode(codiceEsito);
			codice.appendChild(n);
			esito.appendChild(codice);
			codice = doc.createElement("Descr_Esito");
			n = doc.createTextNode(descrizioneEsito);
			codice.appendChild(n);
			esito.appendChild(codice);
			codice = doc.createElement("Tipo_Risposta");
			n = doc.createTextNode(tipoRisposta);
			codice.appendChild(n);
			esito.appendChild(codice);

			return XMLUtils.toXMLString(doc.getDocumentElement());
		} catch (Exception e) {
			logger.error("Impossibile creare il documento di risposta", e);
			return null;
		}
	}

}
