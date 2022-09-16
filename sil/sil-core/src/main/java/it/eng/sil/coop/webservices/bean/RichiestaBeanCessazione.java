package it.eng.sil.coop.webservices.bean;

import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.eng.sil.coop.webservices.utils.Utils;

public class RichiestaBeanCessazione extends RichiestaBean {

	private ArrayList<String> listMovimenti = null;
	private String dataCessazione = null;
	private String motivoCessazione = null;

	public RichiestaBeanCessazione(Document doc) throws Exception {
		super(doc);
		try {
			if (getOutputXML() == null) {
				listMovimenti = new ArrayList<String>();

				String datiMovimenti = "ComunicaCessazione/SetMovimenti";
				String pathPrg = "prgMovimento";

				XPath xpath = XPathFactory.newInstance().newXPath();

				Node nSetMovimenti = (Node) xpath.evaluate(datiMovimenti, doc, XPathConstants.NODE);
				NodeList movListNode = (NodeList) xpath.evaluate(pathPrg, nSetMovimenti, XPathConstants.NODESET);

				for (int i = 0; i < movListNode.getLength(); i++) {
					Node movNode = movListNode.item(i);

					String prgMovimento = it.eng.sil.coordinamento.utils.XMLUtils.getNodeValue(movNode);

					listMovimenti.add(i, prgMovimento);
				}

				String path = "DataCessazione";
				String dataCess = getNodeValue(getRootXML(), path);
				if (dataCess == null || dataCess.equals("")) {
					setOutputXML(Utils.createXMLRisposta("99", "Data cessazione assente"));
				} else {
					dataCess = dataCess.substring(8, 10) + "/" + dataCess.substring(5, 7) + "/"
							+ dataCess.substring(0, 4);
					setDataCessazione(dataCess);
				}

				path = "MotivoCessazione";
				String motivoCess = getNodeValue(getRootXML(), path);
				if (motivoCess == null || motivoCess.equals("")) {
					motivoCess = "AL";
				}
				setMotivoCessazione(motivoCess);
			}
		} catch (Exception e) {
			setOutputXML(Utils.createXMLRisposta("99", "Errore generico"));
		}
	}

	public ArrayList<String> getMovimenti() {
		return this.listMovimenti;
	}

	public String getDataCessazione() {
		return this.dataCessazione;
	}

	public void setDataCessazione(String val) {
		this.dataCessazione = val;
	}

	public String getMotivoCessazione() {
		return this.motivoCessazione;
	}

	public void setMotivoCessazione(String val) {
		this.motivoCessazione = val;
	}

}
