package it.eng.sil.coop.webservices.clicLavoro.candidatura;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFUserError;

import it.eng.sil.coop.webservices.clicLavoro.CLUtility;
import it.eng.sil.module.clicLavoro.DatiSistemaCL;
import it.eng.sil.module.clicLavoro.candidatura.DatiAggiuntiviLavoratore;
import it.eng.sil.module.clicLavoro.candidatura.DatiAnagraficiLavoratore;
import it.eng.sil.module.clicLavoro.candidatura.DatiCurriculariLavoratore;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;

public class CLCandidaturaDataInvioMassivo extends CLUtility {
	static Logger _logger = Logger.getLogger(CLCandidaturaDataInvioMassivo.class.getName());
	public BigDecimal cdnLavoratore;
	public String codiceFiscale;
	public String codCPI;
	public String dataInvio;
	public String dataScad;
	public List<String> codAmbDiff;
	public String codCandidatura;
	public String titoloCandidatura;
	public String tipoComunicazioneCL;
	// complimenti a chi l'ha messo qui. Un lavoro da manuale
	final static String SCHEMA_XSD = "myportal" + File.separator + "clic_lavoro_candidatura_038_massivo.xsd";

	Element candidaturaElement;

	public CLCandidaturaDataInvioMassivo() {

	}

	public CLCandidaturaDataInvioMassivo(String codiceFiscale, String codCPI, String dataInvio, List<String> codAmbDiff,
			String codCandidatura, String dataScad, String titolo, String tipoComunicazione) {
		super(SCHEMA_XSD);
		this.codiceFiscale = codiceFiscale;
		this.codCPI = codCPI;
		this.dataInvio = dataInvio;
		this.codAmbDiff = codAmbDiff;
		this.codCandidatura = codCandidatura;
		this.dataScad = dataScad;
		this.titoloCandidatura = titolo;
		this.tipoComunicazioneCL = tipoComunicazione;

	}

	/**
	 * Costruttore utilizzato solo per i test (CandidaturaMansioneTestCase e CandidaturaTestCase) e modulo implementato
	 * da CLCandidatura non usato Per questo motivo non viene passato il parametro tipoComunicazione
	 * 
	 * @param codiceFiscale
	 * @param codCPI
	 * @param dataInvio
	 * @param codAmbDiff
	 */
	public CLCandidaturaDataInvioMassivo(String codiceFiscale, String codCPI, String dataInvio,
			List<String> codAmbDiff) {
		super(SCHEMA_XSD);
		this.codiceFiscale = codiceFiscale;
		this.codCPI = codCPI;
		this.dataInvio = dataInvio;
		this.codAmbDiff = codAmbDiff;
	}

	public CLCandidaturaDataInvioMassivo(String codiceFiscale, String codCPI, String dataInvio, List<String> codAmbDiff,
			String tipoComunicazione) {
		super(SCHEMA_XSD);
		this.codiceFiscale = codiceFiscale;
		this.codCPI = codCPI;
		this.dataInvio = dataInvio;
		this.codAmbDiff = codAmbDiff;
		this.tipoComunicazioneCL = tipoComunicazione;

	}

	public CLCandidaturaDataInvioMassivo(String codCPI, String dataInvio, String tipoComunicazione) {
		super(SCHEMA_XSD);// che è super giga robot d'acciaio?
		this.codCPI = codCPI;
		this.dataInvio = dataInvio;
		this.tipoComunicazioneCL = tipoComunicazione;

	}

	public void costruisci() throws MandatoryFieldException, FieldFormatException, EMFUserError {

		// Create instance of DocumentBuilderFactory, get the DocumentBuilder
		factory = DocumentBuilderFactory.newInstance();

		try {
			parser = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			_logger.error("Errore di inizializzazione del messaggio di risposta", e);
			return;
		}

		// Create blank DOM Document
		doc = parser.newDocument();

		candidaturaElement = doc.createElement(TAG_CANDIDATURA);
		candidaturaElement.setAttribute("xmlns:cliclavoro", "http://servizi.lavoro.gov.it/candidatura");
		candidaturaElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

		candidaturaElement.setAttribute("xsi:schemaLocation",
				"http://servizi.lavoro.gov.it/candidatura clic_lavoro_candidatura_038_massivo.xsd");

		doc.appendChild(candidaturaElement);

		// inizializza il cdnLavoratore
		Object[] inputParameters = new Object[1];
		inputParameters[0] = codiceFiscale;
		SourceBean ret = executeSelect("CL_GET_CDN_LAVORATORE", inputParameters, null);
		cdnLavoratore = (BigDecimal) ret.getAttribute("ROW.CDNLAVORATORE");

		createElementDatiAnagrafici(candidaturaElement, codCPI, null);
		createElementDatiCurriculari(candidaturaElement, null);
		createElementDatiSistema(candidaturaElement, codCPI, null, codAmbDiff);
		if (tipoComunicazioneCL != null && !tipoComunicazioneCL.equals("")
				&& tipoComunicazioneCL.equalsIgnoreCase(TIPOCOMUNICAZIONECL)) {
			createElementDatiAggiuntivi(candidaturaElement, null, titoloCandidatura);
		}
	}

	public void costruisci(BigDecimal cdnLavoratore, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {

		// Create instance of DocumentBuilderFactory, get the DocumentBuilder
		factory = DocumentBuilderFactory.newInstance();

		try {
			parser = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			_logger.error("Errore di inizializzazione del messaggio di risposta", e);
			return;
		}

		// Create blank DOM Document
		doc = parser.newDocument();

		candidaturaElement = doc.createElement(TAG_CANDIDATURA);
		candidaturaElement.setAttribute("xmlns:cliclavoro", "http://servizi.lavoro.gov.it/candidatura");
		candidaturaElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

		candidaturaElement.setAttribute("xsi:schemaLocation",
				"http://servizi.lavoro.gov.it/candidatura clic_lavoro_candidatura_038_massivo.xsd");

		doc.appendChild(candidaturaElement);

		this.cdnLavoratore = cdnLavoratore;

		createElementDatiAnagrafici(candidaturaElement, codCPI, dc);
		createElementDatiCurriculari(candidaturaElement, dc);
		createElementDatiSistema(candidaturaElement, codCPI, dc, codAmbDiff);
		if (tipoComunicazioneCL != null && !tipoComunicazioneCL.equals("")
				&& tipoComunicazioneCL.equalsIgnoreCase(TIPOCOMUNICAZIONECL)) {
			createElementDatiAggiuntivi(candidaturaElement, dc, titoloCandidatura);
		}
	}

	/**
	 * Recupera le altre informazioni relative al Lavoratore
	 * 
	 * @param candidaturaElement
	 * @throws FieldFormatException
	 * @throws MandatoryFieldException
	 * @throws EMFUserError
	 */
	private void createElementDatiAnagrafici(Element candidaturaElement, String codCPI, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		Element lavoratoreElement = doc.createElement(TAG_LAVORATORE);
		DatiAnagraficiLavoratore datiAnagraficiLavoratore = new DatiAnagraficiLavoratore(doc, cdnLavoratore);
		datiAnagraficiLavoratore.createElementDatiAnagrafici(lavoratoreElement, dc);
		datiAnagraficiLavoratore.createElementDomicilio(lavoratoreElement, dc);
		datiAnagraficiLavoratore.createElementRecapiti(lavoratoreElement, codCPI, dc);

		candidaturaElement.appendChild(lavoratoreElement);
	}

	private void createElementDatiCurriculari(Element candidaturaElement, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		Element datiCurriculariElement = doc.createElement(TAG_DATICURRICULARI);
		DatiCurriculariLavoratore datiCurriculariLavoratore = new DatiCurriculariLavoratore(doc, cdnLavoratore,
				dataInvio, tipoComunicazioneCL);
		datiCurriculariLavoratore.createElementEsperienzeLavorative(datiCurriculariElement, dc);
		datiCurriculariLavoratore.createElementIstruzioni(datiCurriculariElement, dc);
		datiCurriculariLavoratore.createElementFormazioni(datiCurriculariElement, dc);
		datiCurriculariLavoratore.createElementLingue(datiCurriculariElement, dc);
		datiCurriculariLavoratore.createElementConoscInformatiche(datiCurriculariElement, dc);
		datiCurriculariLavoratore.createElementAbilitazPatenti(datiCurriculariElement, dc);

		// da rivedere
		datiCurriculariLavoratore.createElementProfessDesiderataInvioMassivo(datiCurriculariElement, dc);

		DatiAnagraficiLavoratore datiAnagraficiLavoratore = new DatiAnagraficiLavoratore(doc, cdnLavoratore);
		datiAnagraficiLavoratore.createElementAltreInformazioni(datiCurriculariElement);

		candidaturaElement.appendChild(datiCurriculariElement);
	}

	private void createElementDatiAggiuntivi(Element candidaturaElement, DataConnection dc, String titoloCandidatura)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		Element datiAggiuntiviElement = doc.createElement(TAG_DATIAGGIUNTIVI);
		DatiAggiuntiviLavoratore datiAggiuntivi = new DatiAggiuntiviLavoratore(doc, cdnLavoratore, dataInvio,
				titoloCandidatura);
		datiAggiuntivi.createElementGenerali(datiAggiuntiviElement, dc);
		datiAggiuntivi.createElementAbilitazioni(datiAggiuntiviElement, dc);
		datiAggiuntivi.createElementAnnotazioni(datiAggiuntiviElement, dc);

		candidaturaElement.appendChild(datiAggiuntiviElement);
	}

	/**
	 * Recupera i dati di sistema
	 * 
	 * @param candidaturaElement
	 * @throws EMFUserError
	 */
	private void createElementDatiSistema(Element candidaturaElement, String codCPI, DataConnection dc,
			List<String> codAmbDiff) throws MandatoryFieldException, FieldFormatException, EMFUserError {
		Element datiSistemaElement = doc.createElement(TAG_DATISISTEMA);
		DatiSistemaCL datiSistema = new DatiSistemaCL(doc, codCPI, dataInvio, this.cdnLavoratore);
		datiSistema.createElementDatiSistemaCandidatura(datiSistemaElement, dc, codAmbDiff, codCandidatura, dataScad);

		candidaturaElement.appendChild(datiSistemaElement);
	}

	public BigDecimal getCdnLavoratore() {
		return cdnLavoratore;
	}

	public void setCdnLavoratore(BigDecimal cdnLavoratore) {
		this.cdnLavoratore = cdnLavoratore;
	}

	public String getCodCPI() {
		return codCPI;
	}

	public void setCodCPI(String codCPI) {
		this.codCPI = codCPI;
	}

	public String getDataInvio() {
		return dataInvio;
	}

	public void setDataInvio(String dataInvio) {
		this.dataInvio = dataInvio;
	}

}
