package it.eng.sil.coop.webservices.bean;

import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.eng.sil.coop.webservices.utils.Utils;

public class RichiestaBeanSana extends RichiestaBean {

	private ArrayList<String> listMovimenti = null;
	private ArrayList<String> redditiMovimenti = null;
	private ArrayList<String> numKloMovimenti = null;

	public RichiestaBeanSana(Document doc) throws Exception {
		super(doc);
		try {
			if (getOutputXML() == null) {
				listMovimenti = new ArrayList<String>();
				redditiMovimenti = new ArrayList<String>();
				numKloMovimenti = new ArrayList<String>();

				String datiMovimenti = "SanatoriaReddito/SetMovimenti";
				String pathMov = "Mov";
				String pathPrg = "prgMovimento";
				String pathReddito = "RedditoMensileSanato";
				String pathNumKlo = "numklomov";

				XPath xpath = XPathFactory.newInstance().newXPath();

				Node nSetMovimenti = (Node) xpath.evaluate(datiMovimenti, doc, XPathConstants.NODE);
				NodeList movListNode = (NodeList) xpath.evaluate(pathMov, nSetMovimenti, XPathConstants.NODESET);

				for (int i = 0; i < movListNode.getLength(); i++) {
					Node movNode = movListNode.item(i);

					Node prgNode = (Node) xpath.evaluate(pathPrg, movNode, XPathConstants.NODE);
					Node redditoNode = (Node) xpath.evaluate(pathReddito, movNode, XPathConstants.NODE);
					Node numkloNode = (Node) xpath.evaluate(pathNumKlo, movNode, XPathConstants.NODE);

					String prgMovimento = it.eng.sil.coordinamento.utils.XMLUtils.getNodeValue(prgNode);
					String reddito = it.eng.sil.coordinamento.utils.XMLUtils.getNodeValue(redditoNode);
					String numklo = it.eng.sil.coordinamento.utils.XMLUtils.getNodeValue(numkloNode);

					listMovimenti.add(i, prgMovimento);
					redditiMovimenti.add(i, reddito);
					numKloMovimenti.add(i, numklo);
				}
			}
		} catch (Exception e) {
			setOutputXML(Utils.createXMLRisposta("99", "Errore generico"));
		}
	}

	public ArrayList<String> getMovimenti() {
		return this.listMovimenti;
	}

	public ArrayList<String> getRedditiMov() {
		return this.redditiMovimenti;
	}

	public ArrayList<String> getNumKloMov() {
		return this.numKloMovimenti;
	}

}
