package it.eng.sil.test.coop.clicLavoro;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.eng.sil.util.xml.SilNamespaceContext;

public class XP {

	public static String path1XPathExp = "/cliclavoro:Vacancy/cliclavoro:Richiesta/cliclavoro:IstruzioneFormazione";
	public static String lingueXPathExp = "/cliclavoro:Vacancy/cliclavoro:Richiesta/cliclavoro:IstruzioneFormazione/cliclavoro:Lingua";
	public static String modalitalavoroXPathExp = "/cliclavoro:Vacancy/cliclavoro:Richiesta/cliclavoro:CondizioniOfferte/cliclavoro:idmodalitalavoro/text()";
	public static String modalitalavoroNodeXPathExp = "/cliclavoro:Vacancy/cliclavoro:Richiesta/cliclavoro:CondizioniOfferte/cliclavoro:idmodalitalavoro";
	public static String mansioniXPathExp = "/cliclavoro:Candidatura/cliclavoro:DatiCurriculari/cliclavoro:ProfessioneDesiderataDisponibilita";
	public static String esperienzeXPathExp = "/cliclavoro:Candidatura/cliclavoro:DatiCurriculari/cliclavoro:EsperienzeLavorative";
	public static String profDesidDispContrattoXPathExp = "/cliclavoro:Candidatura/cliclavoro:DatiCurriculari/cliclavoro:ProfessioneDesiderataDisponibilita";

	public Date getDateOrNull(String xPathExp) throws ParseException, XPathExpressionException {
		return getDateIfNotNull(xPathExp, null);
	}

	public String getTextOrNull(String xPathExp) throws XPathExpressionException {
		return getTextIfNotNull(xPathExp, null);
	}

	private SimpleDateFormat date2xsd;
	private Element elem2parse;
	private XPath xPath;

	public XP(Element elem2parse) {
		date2xsd = new SimpleDateFormat("yyyy-MM-dd");
		this.setElem2parse(elem2parse);

		this.xPath = initXPath();
	}

	private XPath initXPath() {
		XPath xPath = XPathFactory.newInstance().newXPath();
		SilNamespaceContext context = new SilNamespaceContext("cliclavoro", this.getElem2parse().getNamespaceURI());
		xPath.setNamespaceContext(context);

		return xPath;
	}

	public String getText(String xPathExp) throws XPathExpressionException {
		final Element singleNode = (Element) xPath.evaluate(xPathExp, this.getElem2parse(), XPathConstants.NODE);

		return singleNode.getTextContent();
	}

	public Node getNode(String xPathExp) throws XPathExpressionException {
		return (Node) xPath.evaluate(xPathExp, this.getElem2parse(), XPathConstants.NODE);
	}

	public NodeList getList(String xPathExp) throws XPathExpressionException {
		NodeList nodeList = (NodeList) xPath.evaluate(xPathExp, this.getElem2parse(), XPathConstants.NODESET);

		return nodeList;
	}

	public String getTextIfNotNull(String xPathExp, String failSafe) throws XPathExpressionException {
		final Node singleNode = (Element) xPath.evaluate(xPathExp, this.getElem2parse(), XPathConstants.NODE);
		if (singleNode == null) {
			return failSafe;
		}
		return singleNode.getTextContent();
	}

	public Date getDate(String xPathExp) throws ParseException, XPathExpressionException {
		final Node singleNode = (Element) xPath.evaluate(xPathExp, this.getElem2parse(), XPathConstants.NODE);

		return date2xsd.parse(singleNode.getTextContent());
	}

	public Date getDateIfNotNull(String xPathExp, Date failSafe) throws ParseException, XPathExpressionException {
		final Node singleNode = (Element) xPath.evaluate(xPathExp, this.getElem2parse(), XPathConstants.NODE);
		if (singleNode == null) {
			return failSafe;
		}
		return date2xsd.parse(singleNode.getTextContent());
	}

	public void setElem2parse(Element elem2parse) {
		this.elem2parse = elem2parse;
	}

	public Element getElem2parse() {
		return elem2parse;
	}

}
