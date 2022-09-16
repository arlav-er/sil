package it.eng.sil.cig.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.eng.sil.util.xml.SilNamespaceContext;

/**
 * Bean per processare l'XML di un'iscrizione MBO e facilita l'utilizzo dei dati
 * 
 * @author manuel
 */
public class ImportaIscrizioneMBOBean {

	public static Logger logger = Logger.getLogger(ImportaIscrizioneMBOBean.class.getName());

	private static final String NS_PREFIX = "mbo";

	private String codiceComunicazione;
	private String dataInvio;

	/* Mobilita */
	private String tipoIscrMbo;
	private String dataInizioMov;
	private String dataFineMov;
	private String dataInizioMob;
	private String dataFineMob;
	private String dataMaxDiff;
	private String dataCRT;
	private String numAttoMob;
	private String codMansione;
	private String codCCNL;
	private String codEnteDetermina;
	private String flgNonImprenditore;
	private String flgCasoDubbio;

	/* Azienda */
	private String codiceFiscaleAz;
	private String ragioneSociale;
	private String tipologiaAzienda;
	private String denominazioneSede;
	private String matricolaINPS;
	private String settore;
	private String ccnlApplicato;
	private String codAzStato;
	private String indirizzoAz;
	private String comuneAz;
	private String capAz;
	private String telefonoAz;
	private String fax;
	private String eMail;

	/* Lavoratore */
	private String codiceFiscaleLav;
	private String cognome;
	private String nome;
	private String sesso;
	private String dataNascita;
	private String comuneNascita;
	private String cittadinanza;
	private String indirizzoDom;
	private String comuneDom;
	private String capDom;
	private String indirizzoRes;
	private String comuneRes;
	private String capRes;
	private String cellulare;
	private String telefono;

	private XP xp;

	public ImportaIscrizioneMBOBean() {

	}

	public ImportaIscrizioneMBOBean(Element iscrizioneElement) {
		xp = new XP(iscrizioneElement);
	}

	/**
	 * Costruisce il bean a partire dai dati dell'XML
	 * 
	 * @throws ParseException
	 */
	public void parseIscrizione() throws ParseException, XPathExpressionException {
		setDataInvio(xp.getText(xp.dataInvioXPathExp));
		setCodiceComunicazione(xp.getText(xp.codiceComunicazioneXPathExp));
		setTipoIscrMbo(xp.getText(xp.tipoIscrMboXPathExp));
		setDataInizioMov(xp.getText(xp.dataInizioMovXPathExp));
		setDataFineMov(xp.getText(xp.dataFineMovXPathExp));
		setDataInizioMob(xp.getText(xp.dataInizioMobXPathExp));
		setDataFineMob(xp.getText(xp.dataFineMobXPathExp));
		setDataMaxDiff(xp.getText(xp.dataMaxDiffXPathExp));
		setDataCRT(xp.getTextIfNotNull(xp.dataCRTXPathExp, ""));
		setNumAttoMob(xp.getTextIfNotNull(xp.numAttoMobXPathExp, ""));
		setCodMansione(xp.getText(xp.codMansioneXPathExp));
		setCodCCNL(xp.getText(xp.codCCNLXPathExp));
		setCodEnteDetermina(xp.getText(xp.codEnteDeterminaXPathExp));
		setFlgNonImprenditore(xp.getText(xp.flgNonImprenditoreXPathExp));
		setFlgCasoDubbio(xp.getTextIfNotNull(xp.flgCasoDubbioXPathExp, ""));
		setCodiceFiscaleAz(xp.getText(xp.codiceFiscaleAzXPathExp));
		setRagioneSociale(xp.getText(xp.ragioneSocialeXPathExp));
		setTipologiaAzienda(xp.getText(xp.tipologiaAziendaXPathExp));
		setDenominazioneSede(xp.getTextIfNotNull(xp.denominazioneSedeXPathExp, ""));
		setMatricolaINPS(xp.getTextIfNotNull(xp.matricolaINPSXPathExp, ""));
		setSettore(xp.getText(xp.settoreXPathExp));
		setCcnlApplicato(xp.getTextIfNotNull(xp.ccnlApplicatoXPathExp, ""));
		setCodAzStato(xp.getText(xp.codAzStatoXPathExp));
		setIndirizzoAz(xp.getText(xp.indirizzoAzXPathExp));
		setComuneAz(xp.getText(xp.comuneAzXPathExp));
		setCapAz(xp.getTextIfNotNull(xp.capAzXPathExp, ""));
		setTelefonoAz(xp.getTextIfNotNull(xp.telefonoAzXPathExp, ""));
		setFax(xp.getTextIfNotNull(xp.faxXPathExp, ""));
		seteMail(xp.getTextIfNotNull(xp.eMailXPathExp, ""));
		setCodiceFiscaleLav(xp.getText(xp.codiceFiscaleLavXPathExp));
		setCognome(xp.getText(xp.cognomeXPathExp));
		setNome(xp.getText(xp.nomeXPathExp));
		setSesso(xp.getText(xp.sessoXPathExp));
		setDataNascita(xp.getText(xp.dataNascitaXPathExp));
		setComuneNascita(xp.getText(xp.comuneNascitaXPathExp));
		setCittadinanza(xp.getText(xp.cittadinanzaXPathExp));
		setIndirizzoDom(xp.getText(xp.indirizzoDomXPathExp));
		setComuneDom(xp.getText(xp.comuneDomXPathExp));
		setCapDom(xp.getText(xp.capDomXPathExp));
		setIndirizzoRes(xp.getText(xp.indirizzoResXPathExp));
		setComuneRes(xp.getText(xp.comuneResXPathExp));
		setCapRes(xp.getTextIfNotNull(xp.capResXPathExp, ""));
		setCellulare(xp.getTextIfNotNull(xp.cellulareXPathExp, ""));
		setTelefono(xp.getTextIfNotNull(xp.telefonoXPathExp, ""));
	}

	public class XP {

		private String codiceComunicazioneXPathExp = "/mbo:DomandaMBO/@codiceComunicazione";
		private String dataInvioXPathExp = "/mbo:DomandaMBO/@dataInvio";

		/* Mobilita */
		private String tipoIscrMboXPathExp = "/mbo:DomandaMBO/mbo:datiIscrizioneMobilita/@tipoIscrMbo";
		private String dataInizioMovXPathExp = "/mbo:DomandaMBO/mbo:datiIscrizioneMobilita/@dataInizioMov";
		private String dataFineMovXPathExp = "/mbo:DomandaMBO/mbo:datiIscrizioneMobilita/@dataFineMov";
		private String dataInizioMobXPathExp = "/mbo:DomandaMBO/mbo:datiIscrizioneMobilita/@dataInizioMob";
		private String dataFineMobXPathExp = "/mbo:DomandaMBO/mbo:datiIscrizioneMobilita/@dataFineMob";
		private String dataMaxDiffXPathExp = "/mbo:DomandaMBO/mbo:datiIscrizioneMobilita/@dataMaxDiff";
		private String dataCRTXPathExp = "/mbo:DomandaMBO/mbo:datiIscrizioneMobilita/@dataCRT";
		private String numAttoMobXPathExp = "/mbo:DomandaMBO/mbo:datiIscrizioneMobilita/@numAttoMob";
		private String codMansioneXPathExp = "/mbo:DomandaMBO/mbo:datiIscrizioneMobilita/@codMansione";
		private String codCCNLXPathExp = "/mbo:DomandaMBO/mbo:datiIscrizioneMobilita/@codCCNL";
		private String codEnteDeterminaXPathExp = "/mbo:DomandaMBO/mbo:datiIscrizioneMobilita/@codEnteDetermina";
		private String flgNonImprenditoreXPathExp = "/mbo:DomandaMBO/mbo:datiIscrizioneMobilita/@flgNonImprenditore";
		private String flgCasoDubbioXPathExp = "/mbo:DomandaMBO/mbo:datiIscrizioneMobilita/@flgCasoDubbio";

		/* Azienda */
		private String codiceFiscaleAzXPathExp = "/mbo:DomandaMBO/mbo:Azienda/@codiceFiscale";
		private String ragioneSocialeXPathExp = "/mbo:DomandaMBO/mbo:Azienda/@ragioneSociale";
		private String tipologiaAziendaXPathExp = "/mbo:DomandaMBO/mbo:Azienda/@tipologiaAzienda";
		private String denominazioneSedeXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/@denominazioneSede";
		private String matricolaINPSXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/@matricolaINPS";
		private String settoreXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/@settore";
		private String ccnlApplicatoXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/@ccnlApplicato";
		private String codAzStatoXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/@codAzStato";
		private String indirizzoAzXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:IndirizzoCompleto/mbo:indirizzo";
		private String comuneAzXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:IndirizzoCompleto/mbo:comune";
		private String capAzXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:IndirizzoCompleto/mbo:cap";
		private String telefonoAzXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Recapiti/mbo:telefono";
		private String faxXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Recapiti/mbo:fax";
		private String eMailXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Recapiti/mbo:e-mail";

		/* Lavoratore */
		private String codiceFiscaleLavXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Lavoratore/@codiceFiscale";
		private String cognomeXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Lavoratore/@cognome";
		private String nomeXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Lavoratore/@nome";
		private String sessoXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Lavoratore/@sesso";
		private String dataNascitaXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Lavoratore/@dataNascita";
		private String comuneNascitaXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Lavoratore/@comuneNascita";
		private String cittadinanzaXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Lavoratore/@cittadinanza";
		private String indirizzoDomXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Lavoratore/mbo:Indirizzi/mbo:domicilio/mbo:indirizzo";
		private String comuneDomXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Lavoratore/mbo:Indirizzi/mbo:domicilio/mbo:comune";
		private String capDomXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Lavoratore/mbo:Indirizzi/mbo:domicilio/mbo:cap";
		private String indirizzoResXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Lavoratore/mbo:Indirizzi/mbo:residenza/mbo:indirizzo";
		private String comuneResXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Lavoratore/mbo:Indirizzi/mbo:residenza/mbo:comune";
		private String capResXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Lavoratore/mbo:Indirizzi/mbo:residenza/mbo:cap";
		private String cellulareXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Lavoratore/mbo:Recapiti/mbo:cellulare";
		private String telefonoXPathExp = "/mbo:DomandaMBO/mbo:Azienda/mbo:Sede/mbo:Lavoratore/mbo:Recapiti/mbo:telefono";

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

	/* Getters and Setters come se non ci fosse un domani */

	public String getCodiceComunicazione() {
		return codiceComunicazione;
	}

	public void setCodiceComunicazione(String codiceComunicazione) {
		this.codiceComunicazione = codiceComunicazione;
	}

	public String getDataInvio() {
		return dataInvio;
	}

	public void setDataInvio(String dataInvio) {
		this.dataInvio = dataInvio;
	}

	public String getTipoIscrMbo() {
		return tipoIscrMbo;
	}

	public void setTipoIscrMbo(String tipoIscrMbo) {
		this.tipoIscrMbo = tipoIscrMbo;
	}

	public String getDataInizioMov() {
		return dataInizioMov;
	}

	public void setDataInizioMov(String dataInizioMov) {
		this.dataInizioMov = dataInizioMov;
	}

	public String getDataFineMov() {
		return dataFineMov;
	}

	public void setDataFineMov(String dataFineMov) {
		this.dataFineMov = dataFineMov;
	}

	public String getDataInizioMob() {
		return dataInizioMob;
	}

	public void setDataInizioMob(String dataInizioMob) {
		this.dataInizioMob = dataInizioMob;
	}

	public String getDataFineMob() {
		return dataFineMob;
	}

	public void setDataFineMob(String dataFineMob) {
		this.dataFineMob = dataFineMob;
	}

	public String getDataMaxDiff() {
		return dataMaxDiff;
	}

	public void setDataMaxDiff(String dataMaxDiff) {
		this.dataMaxDiff = dataMaxDiff;
	}

	public String getDataCRT() {
		return dataCRT;
	}

	public void setDataCRT(String dataCRT) {
		this.dataCRT = dataCRT;
	}

	public String getNumAttoMob() {
		return numAttoMob;
	}

	public void setNumAttoMob(String numAttoMob) {
		this.numAttoMob = numAttoMob;
	}

	public String getCodMansione() {
		return codMansione;
	}

	public void setCodMansione(String codMansione) {
		this.codMansione = codMansione;
	}

	public String getCodCCNL() {
		return codCCNL;
	}

	public void setCodCCNL(String codCCNL) {
		this.codCCNL = codCCNL;
	}

	public String getCodEnteDetermina() {
		return codEnteDetermina;
	}

	public void setCodEnteDetermina(String codEnteDetermina) {
		this.codEnteDetermina = codEnteDetermina;
	}

	public String getFlgNonImprenditore() {
		return flgNonImprenditore;
	}

	public void setFlgNonImprenditore(String flgNonImprenditore) {
		if ("SI".equalsIgnoreCase(flgNonImprenditore))
			this.flgNonImprenditore = "S";
		else
			this.flgNonImprenditore = "N";
	}

	public String getFlgCasoDubbio() {
		return flgCasoDubbio;
	}

	public void setFlgCasoDubbio(String flgCasoDub) {
		this.flgCasoDubbio = flgCasoDub;
	}

	public String getCodiceFiscaleAz() {
		return codiceFiscaleAz;
	}

	public void setCodiceFiscaleAz(String codiceFiscaleAz) {
		this.codiceFiscaleAz = codiceFiscaleAz;
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public String getTipologiaAzienda() {
		return tipologiaAzienda;
	}

	public void setTipologiaAzienda(String tipologiaAzienda) {
		this.tipologiaAzienda = tipologiaAzienda;
	}

	public String getDenominazioneSede() {
		return denominazioneSede;
	}

	public void setDenominazioneSede(String denominazioneSede) {
		this.denominazioneSede = denominazioneSede;
	}

	public String getMatricolaINPS() {
		return matricolaINPS;
	}

	public void setMatricolaINPS(String matricolaINPS) {
		this.matricolaINPS = matricolaINPS;
	}

	public String getSettore() {
		return settore;
	}

	public void setSettore(String settore) {
		this.settore = settore;
	}

	public String getCcnlApplicato() {
		return ccnlApplicato;
	}

	public void setCcnlApplicato(String ccnlApplicato) {
		this.ccnlApplicato = ccnlApplicato;
	}

	public String getCodAzStato() {
		return codAzStato;
	}

	public void setCodAzStato(String codAzStato) {
		this.codAzStato = codAzStato;
	}

	public String getIndirizzoAz() {
		return indirizzoAz;
	}

	public void setIndirizzoAz(String indirizzoAz) {
		this.indirizzoAz = indirizzoAz;
	}

	public String getComuneAz() {
		return comuneAz;
	}

	public void setComuneAz(String comuneAz) {
		this.comuneAz = comuneAz;
	}

	public String getCapAz() {
		return capAz;
	}

	public void setCapAz(String capAz) {
		this.capAz = capAz;
	}

	public String getTelefonoAz() {
		return telefonoAz;
	}

	public void setTelefonoAz(String telefonoAz) {
		this.telefonoAz = telefonoAz;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public String getCodiceFiscaleLav() {
		return codiceFiscaleLav;
	}

	public void setCodiceFiscaleLav(String codiceFiscaleLav) {
		this.codiceFiscaleLav = codiceFiscaleLav;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSesso() {
		return sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	public String getDataNascita() {
		return dataNascita;
	}

	public void setDataNascita(String dataNascita) {
		this.dataNascita = dataNascita;
	}

	public String getComuneNascita() {
		return comuneNascita;
	}

	public void setComuneNascita(String comuneNascita) {
		this.comuneNascita = comuneNascita;
	}

	public String getCittadinanza() {
		return cittadinanza;
	}

	public void setCittadinanza(String cittadinanza) {
		this.cittadinanza = cittadinanza;
	}

	public String getIndirizzoDom() {
		return indirizzoDom;
	}

	public void setIndirizzoDom(String indirizzoDom) {
		this.indirizzoDom = indirizzoDom;
	}

	public String getComuneDom() {
		return comuneDom;
	}

	public void setComuneDom(String comuneDom) {
		this.comuneDom = comuneDom;
	}

	public String getCapDom() {
		return capDom;
	}

	public void setCapDom(String capDom) {
		this.capDom = capDom;
	}

	public String getIndirizzoRes() {
		return indirizzoRes;
	}

	public void setIndirizzoRes(String indirizzoRes) {
		this.indirizzoRes = indirizzoRes;
	}

	public String getComuneRes() {
		return comuneRes;
	}

	public void setComuneRes(String comuneRes) {
		this.comuneRes = comuneRes;
	}

	public String getCapRes() {
		return capRes;
	}

	public void setCapRes(String capRes) {
		this.capRes = capRes;
	}

	public String getCellulare() {
		return cellulare;
	}

	public void setCellulare(String cellulare) {
		this.cellulare = cellulare;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

}
