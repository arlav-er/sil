package it.eng.sil.cig.bean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.eng.sil.util.xml.SilNamespaceContext;

/**
 * Bean che processa l'XML dell'accorpamento lavoratore e facilita l'utilizzo dei dati
 * 
 * @author uberti
 */
public class AccorpaLavoratoreBean {

	static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AccorpaLavoratoreBean.class.getName());

	private static final String NS_PREFIX = "acc";

	private String cf_accorpato;
	private String cf_accorpante;
	private String orarioInvio;
	private String username;
	private String password;
	private List<String> listAccordi;
	private String codiceDomanda;
	private XP xp;

	public AccorpaLavoratoreBean() {

	}

	public AccorpaLavoratoreBean(Element accorpatoElement) {
		xp = new XP(accorpatoElement);
	}

	public String getCf_accorpato() {
		return cf_accorpato;
	}

	public void setCf_accorpato(String cfAccorpato) {
		cf_accorpato = cfAccorpato;
	}

	public String getCf_accorpante() {
		return cf_accorpante;
	}

	public void setCf_accorpante(String cfAccorpante) {
		cf_accorpante = cfAccorpante;
	}

	public String getOrarioInvio() {
		return orarioInvio;
	}

	public void setOrarioInvio(String orarioInvio) {
		this.orarioInvio = orarioInvio;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getListAccordi() {
		return listAccordi;
	}

	public void setListAccordi(List<String> listAccordi) {
		this.listAccordi = listAccordi;
	}

	public String getCodiceDomanda() {
		return codiceDomanda;
	}

	public void setCodiceDomanda(String codiceDomanda) {
		this.codiceDomanda = codiceDomanda;
	}

	public String toString() {
		return String.format(
				"AccorpaLavoratoreBean [cf_accorpato=%s, cf_accorpante=%s, orarioInvio=%s, username=%s, password=%s, listAccordi=%s]",
				this.getCf_accorpato(), this.getCf_accorpante(), this.getOrarioInvio(), this.getUsername(),
				this.getPassword(), this.getListAccordi());
	}

	/**
	 * Costruisce il bean a partire dai dati dell'XML
	 * 
	 * @throws ParseException
	 */
	public void parseAcc() throws ParseException, XPathExpressionException {
		setCf_accorpante(xp.getText(xp.cfAccorpanteXPathExp));
		setCf_accorpato(xp.getText(xp.cfAccorpatoXPathExp));
		String orarioInvioStr = xp.getText(xp.orarioInvioXPathExp);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date temp = df.parse(orarioInvioStr);
		df = new SimpleDateFormat("dd/mm/yyyy:hh:mm:ss");
		orarioInvioStr = df.format(temp);
		setOrarioInvio(orarioInvioStr);
		setPassword(xp.getText(xp.passwordXPathExp));
		setUsername(xp.getText(xp.usernameXPathExp));
		NodeList list = xp.getList(xp.listAccordiXPathExp);
		setListAccordi(new ArrayList<String>());
		if (list != null) {
			int i = 0;
			while (i < list.getLength()) {
				getListAccordi().add(list.item(i).getTextContent());
				++i;
			}
		}
		setCodiceDomanda(xp.getText(xp.codiceDomandaXPathExp));
	}

	public class XP {

		private String cfAccorpatoXPathExp = "/acc:accorpamento/acc:Lavoratori/acc:cf_accorpato";
		private String cfAccorpanteXPathExp = "/acc:accorpamento/acc:Lavoratori/acc:cf_accorpante";

		private String orarioInvioXPathExp = "/acc:accorpamento/acc:metadata/acc:OrarioInvio";
		private String usernameXPathExp = "/acc:accorpamento/acc:metadata/acc:SILMittente/@username";
		private String passwordXPathExp = "/acc:accorpamento/acc:metadata/acc:SILMittente/@password";
		private String listAccordiXPathExp = "/acc:accorpamento/acc:Accordi";

		private String codiceDomandaXPathExp = "/acc:accorpamento/acc:metadata/acc:codiceDomanda";

		public Date getDateOrNull(String xPathExp) throws ParseException, XPathExpressionException {
			return getDateIfNotNull(xPathExp, null);
		}

		public String getTextOrNull(String xPathExp) throws XPathExpressionException {
			return getTextIfNotNull(xPathExp, null);
		}

		private SimpleDateFormat date2xsd;
		private Element elem2parse;
		private XPath xPath;

		XP(Element elem2parse) {
			date2xsd = new SimpleDateFormat("yyyy-MM-dd");
			this.setElem2parse(elem2parse);

			this.xPath = initXPath();
		}

		private XPath initXPath() {
			XPath xPath = XPathFactory.newInstance().newXPath();
			logger.debug("Namespace:" + this.getElem2parse().getNamespaceURI());
			SilNamespaceContext context = new SilNamespaceContext(NS_PREFIX, this.getElem2parse().getNamespaceURI());
			xPath.setNamespaceContext(context);

			return xPath;
		}

		String getText(String xPathExp) throws XPathExpressionException {
			final Element singleNode = (Element) xPath.evaluate(xPathExp, this.getElem2parse(), XPathConstants.NODE);

			return singleNode.getTextContent();
		}

		NodeList getList(String xPathExp) throws XPathExpressionException {
			NodeList nodeList = (NodeList) xPath.evaluate(xPathExp, this.getElem2parse(), XPathConstants.NODESET);

			return nodeList;
		}

		String getTextIfNotNull(String xPathExp, String failSafe) throws XPathExpressionException {
			final Node singleNode = (Element) xPath.evaluate(xPathExp, this.getElem2parse(), XPathConstants.NODE);
			if (singleNode == null) {
				return failSafe;
			}
			return singleNode.getTextContent();
		}

		Date getDate(String xPathExp) throws ParseException, XPathExpressionException {
			final Node singleNode = (Element) xPath.evaluate(xPathExp, this.getElem2parse(), XPathConstants.NODE);

			return date2xsd.parse(singleNode.getTextContent());
		}

		Date getDateIfNotNull(String xPathExp, Date failSafe) throws ParseException, XPathExpressionException {
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
}
