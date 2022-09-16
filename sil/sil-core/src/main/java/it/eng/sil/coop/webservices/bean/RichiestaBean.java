package it.eng.sil.coop.webservices.bean;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import it.eng.sil.coop.webservices.utils.Utils;

public class RichiestaBean {
	private String codiceFiscale = null;
	private String outputXML = null;
	private Element rootXML = null;

	public RichiestaBean(Document doc) throws Exception {
		String path = null;
		try {
			this.rootXML = doc.getDocumentElement();
			path = "CodiceFiscale";
			String cf = getNodeValue(rootXML, path);
			if (cf.equals("")) {
				outputXML = Utils.createXMLRisposta("03", "Codice fiscale non trovato");
			} else {
				setCodiceFiscale(cf);
				boolean checkControllo = Utils.checkCodiceFiscale(getCodiceFiscale());
				if (!checkControllo) {
					outputXML = Utils.createXMLRisposta("04", "Codice fiscale non valido");
				}
			}
		} catch (Exception e) {
			outputXML = Utils.createXMLRisposta("99", "Errore generico");
		}
	}

	public void checkCodiceFiscale(String path) throws Exception {
		String cf = getNodeValue(rootXML, path);
		if (cf.equals("")) {
			outputXML = Utils.createXMLRisposta("03", "Codice fiscale non trovato");
		} else {
			setCodiceFiscale(cf);
			boolean checkControllo = Utils.checkCodiceFiscale(getCodiceFiscale());
			if (!checkControllo) {
				outputXML = Utils.createXMLRisposta("04", "Codice fiscale non valido");
			}
		}
	}

	public String getCodiceFiscale() {
		return this.codiceFiscale;
	}

	public void setCodiceFiscale(String val) {
		this.codiceFiscale = val;
	}

	public String getOutputXML() {
		return this.outputXML;
	}

	public void setOutputXML(String val) {
		this.outputXML = val;
	}

	public Element getRootXML() {
		return this.rootXML;
	}

	public void setRootXML(Element root) {
		this.rootXML = root;
	}

	public String getNodeValue(Element root, String path) throws Exception {
		String valore = "";
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node currNode = (Node) xpath.evaluate(path, root, XPathConstants.NODE);
		if (currNode != null) {
			Node n = currNode.getFirstChild();
			if (n != null) {
				valore = n.getNodeValue();
			}
		}
		return valore;
	}

}
