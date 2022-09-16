package it.eng.sil.coop.webservices.myportal;

import java.math.BigDecimal;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.clicLavoro.ricercaPersonale.CLRicercaPersonaleException;
import it.eng.sil.coop.webservices.clicLavoro.ricercaPersonale.decodificatori.Decoder;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import it.eng.sil.util.xml.XMLValidator;

public class MyPortalRicercaPersonaleData extends MyPortalUtility {
	static Logger _logger = Logger.getLogger(MyPortalRicercaPersonaleData.class.getName());

	/**
	 * Schema XSD per la validazione dell'xml prodotto.
	 */
	final static String SCHEMA_XSD = "myportal_ricercapersonale.xsd";

	/* elemento root */
	private Element rootElement;

	public Element getRootElement() {
		return rootElement;
	}

	public void setRootElement(Element rootElement) {
		this.rootElement = rootElement;
	}

	/* parametri vari */
	private BigDecimal prgRichiestaAzienda;
	private BigDecimal prgAlternativa;
	private BigDecimal prgAzienda;
	private BigDecimal prgUnita;

	/**
	 * Istanzia una nuova classe per la creazione di un xml a partire dai parametri passati
	 * 
	 * @param prgRichiestaAz
	 *            progressivo della richiesta
	 * @param prgAlternativa
	 *            progressivo dell'alternativa legata alla richiesta
	 */
	public MyPortalRicercaPersonaleData(BigDecimal prgRichiestaAz, BigDecimal prgAlternativa) {
		super(SCHEMA_XSD);
		this.prgAlternativa = prgAlternativa;
		this.prgRichiestaAzienda = prgRichiestaAz;
	}

	/**
	 * Costruisce un xml
	 * 
	 * @param prgRichiestaAz
	 * @param prgAlternativa
	 * @throws EMFUserError
	 *             nel caso vi siano errori interni durante le operazioni
	 * @throws FieldFormatException
	 * @throws MandatoryFieldException
	 */
	public void costruisci() throws EMFUserError, MandatoryFieldException, FieldFormatException {

		// Create instance of DocumentBuilderFactory
		// Get the DocumentBuilder
		factory = DocumentBuilderFactory.newInstance();

		try {
			parser = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			_logger.error("Errore di inizializzazione del messaggio di risposta", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO);
		}

		// Create blank DOM Document
		doc = parser.newDocument();

		// Insert the root element node
		rootElement = doc.createElement(TAG_VACANCY);
		rootElement.setAttribute("xmlns:vacancy", "http://regione.emilia-romagna.it/vacancy/1");
		rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootElement.setAttribute("xsi:schemaLocation", "http://regione.emilia-romagna.it/vacancy/1");
		doc.appendChild(rootElement);

		if (prgRichiestaAzienda == null) {
			_logger.error("Il prgRichiestaAzienda non può essere null.");
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_INPUT_ERRATO,
					"Il prgRichiestaAzienda non può essere null.");
		}

		// inizializza il prgAzienda ed il prgUnita
		{
			Object[] inputParameters = new Object[1];
			inputParameters[0] = prgRichiestaAzienda;

			SourceBean ret = (SourceBean) QueryExecutor.executeQuery("RP_GET_PRGAZIENDA_PRGUNITA", inputParameters,
					"SELECT", Values.DB_SIL_DATI);

			this.prgAzienda = (BigDecimal) ret.getAttribute("ROW.PRGAZIENDA");
			this.prgUnita = (BigDecimal) ret.getAttribute("ROW.PRGUNITA");

			if (prgAzienda == null || prgUnita == null) {
				_logger.error(
						"Errore nel recupero di prgazienda e prgunita per la generazione dell'xml da inviare a myportal");
				throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_INPUT_ERRATO,
						"Azienda o Unita Azienda non trovate.");
			}
		}

		costruisciDatoreLavoro(rootElement, null);

		costruisciRichiesta(rootElement, null);

		costruisciDatiSistema(rootElement, null);

	}

	/**
	 * Costruisce un xml
	 * 
	 * @param prgRichiestaAz
	 * @param prgAlternativa
	 * @throws CLRicercaPersonaleException
	 * @throws EMFUserError
	 *             nel caso vi siano errori interni durante le operazioni
	 * @throws FieldFormatException
	 * @throws MandatoryFieldException
	 */
	public void costruisci(DataConnection dc) throws EMFUserError, MandatoryFieldException, FieldFormatException {

		// Create instance of DocumentBuilderFactory
		// Get the DocumentBuilder
		factory = DocumentBuilderFactory.newInstance();

		try {
			parser = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			_logger.error("Errore di inizializzazione del messaggio di risposta", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO);
		}

		// Create blank DOM Document
		doc = parser.newDocument();

		// Insert the root element node

		// doc.setPrefix("coop");
		rootElement = doc.createElement(TAG_VACANCY);
		// rootElem.setPrefix("a");
		// rootElem.setAttribute("schemaVersion", "1");
		rootElement.setAttribute("xmlns:cliclavoro", "http://servizi.lavoro.gov.it/vacancy");
		rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootElement.setAttribute("xsi:schemaLocation",
				"http://servizi.lavoro.gov.it/vacancy clic_lavoro_ricerca_personale_2_0.xsd");
		doc.appendChild(rootElement);

		if (prgRichiestaAzienda == null) {
			_logger.error("Il prgRichiestaAzienda non può essere null.");
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_INPUT_ERRATO,
					"Il prgRichiestaAzienda non può essere null.");
		}

		Object[] inputParameters = new Object[1];
		inputParameters[0] = prgRichiestaAzienda;

		SourceBean ret = executeSelect("MYPORTAL_GET_PRGAZIENDA_PRGUNITA", inputParameters, dc);

		this.prgAzienda = (BigDecimal) ret.getAttribute("ROW.PRGAZIENDA");
		this.prgUnita = (BigDecimal) ret.getAttribute("ROW.PRGUNITA");

		if (prgAzienda == null || prgUnita == null) {
			_logger.error(
					"Errore nel recupero di prgazienda e prgunita per la generazione dell'xml da inviare a myportal.");
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_INPUT_ERRATO,
					"Azienda o Unita Azienda non trovate.");
		}

		costruisciDatoreLavoro(rootElement, dc);

		costruisciRichiesta(rootElement, dc);

		costruisciDatiRegistrazioneAzienda(rootElement, dc);

		costruisciDatiSistema(rootElement, dc);

	}

	private void costruisciDatoreLavoro(Element vacancyElement, DataConnection dc)
			throws EMFUserError, MandatoryFieldException, FieldFormatException {

		Element datoreLavoroElement = doc.createElement(TAG_DATORELAVORO);

		vacancyElement.appendChild(datoreLavoroElement);

		costruisciDatiAnagrafici(datoreLavoroElement, dc);

		costruisciDatiContattoCpi(datoreLavoroElement, dc);
		costruisciDatiContattoAlternativo(datoreLavoroElement, dc);

	}

	/**
	 * Costruisce la sezione DATI ANAGRAFICI dell'xml
	 * 
	 * @param datoreLavoroElement
	 * @throws CLRicercaPersonaleException
	 * @throws FieldFormatException
	 * @throws MandatoryFieldException
	 * @throws EMFUserError
	 */
	private void costruisciDatiAnagrafici(Element datoreLavoroElement, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		SourceBean datiAnagraficiSB = executeSelectByPrgAziendaPrgUnita("MYPORTAL_GET_DATIANAGRAFICI", dc);

		XMLValidator.checkRowsExists(datiAnagraficiSB, TAG_DATIANAGRAFICI);

		XMLValidator.checkFieldExists(datiAnagraficiSB, "codicefiscale", true, codiceFiscaleCheck,
				"\"Codice fiscale del Datore di lavoro\"");
		XMLValidator.checkFieldExists(datiAnagraficiSB, "denominazione", true, lengthRangeCheck(1, 100),
				"\"Denominazione del Datore di lavoro\"");
		// XMLValidator.checkFieldExists(datiAnagraficiSB, "settore",false,atecoCheck,"\"Settore del Datore di
		// lavoro\"");

		generateTag(datoreLavoroElement, TAG_DATIANAGRAFICI, datiAnagraficiSB);
	}

	/**
	 * Costruisce la sezione DATI PER IL CONTATTO dell'xml
	 * 
	 * @param datoreLavoroElement
	 * @throws MandatoryFieldException
	 * @throws FieldFormatException
	 * @throws EMFUserError
	 * @throws SourceBeanException
	 * @throws CLRicercaPersonaleException
	 */
	private void costruisciDatiContattoCpi(Element datoreLavoroElement, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		SourceBean datiContattoSB = null;
		datiContattoSB = executeSelectByPrgRichiestaAz("MYPORTAL_GET_DATICONTATTO_CPI", dc);

		XMLValidator.checkRowsExists(datiContattoSB, TAG_DATICONTATTO);
		XMLValidator.checkFieldExists(datiContattoSB, "email", true, emailCheck, "Email del CPI");

		generateTag(datoreLavoroElement, TAG_DATICONTATTO, datiContattoSB);
	}

	/**
	 * Costruisce la sezione DATI PER IL CONTATTO dell'xml
	 * 
	 * @param datoreLavoroElement
	 * @throws MandatoryFieldException
	 * @throws FieldFormatException
	 * @throws EMFUserError
	 * @throws SourceBeanException
	 * @throws CLRicercaPersonaleException
	 */
	private void costruisciDatiContattoAlternativo(Element datoreLavoroElement, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		SourceBean datiContattoSB = null;
		datiContattoSB = executeSelectByPrgRichiestaAz("MYPORTAL_GET_DATICONTATTO", dc);

		XMLValidator.checkRowsExists(datiContattoSB, TAG_DATICONTATTO_ALTERNATIVO);
		XMLValidator.checkFieldExists(datiContattoSB, "email", true, emailCheck, "Email del Datore di lavoro");

		generateTag(datoreLavoroElement, TAG_DATICONTATTO_ALTERNATIVO, datiContattoSB);
	}

	private void costruisciRichiesta(Element vacancyElement, DataConnection dc)
			throws EMFUserError, MandatoryFieldException, FieldFormatException {
		Element richiestaElement = doc.createElement(TAG_RICHIESTA);

		vacancyElement.appendChild(richiestaElement);

		costruisciProfiloRichiesto(richiestaElement, dc);
		costruisciIstruzioneFormazione(richiestaElement, dc);
		costruisciCondizioniOfferte(richiestaElement, dc);
		costruisciDurataRichiesta(richiestaElement, dc);

	}

	/**
	 * @param richiestaElement
	 * @throws FieldFormatException
	 * @throws MandatoryFieldException
	 * @throws EMFUserError
	 * @throws CLRicercaPersonaleException
	 */
	private void costruisciProfiloRichiesto(Element richiestaElement, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		SourceBean profiloRichiestoSB = executeSelectByPrgRichiestaAzPrgAlternativa("MYPORTAL_GET_PROFILORICHIESTO",
				dc);

		XMLValidator.checkRowsExists(profiloRichiestoSB, TAG_PROFILORICHIESTO);

		XMLValidator.checkFieldExists(profiloRichiestoSB, "numerolavoratori", true, numerico1_4Check,
				"\"Numero di lavoratori Richiesto\"");
		// XMLValidator.checkFieldExists(profiloRichiestoSB, "codprofessione", true, lengthRangeCheck(10,12), "\"Codice
		// Professione\"");
		XMLValidator.checkFieldExists(profiloRichiestoSB, "descrprofessione", true, lengthRangeCheck(1, 250),
				"\"Descrizione Professione\"");
		XMLValidator.checkFieldExists(profiloRichiestoSB, "descrizionericerca", true, lengthRangeCheck(1, 4000),
				"\"Descrizione Ricerca\"");

		generateTag(richiestaElement, TAG_PROFILORICHIESTO, profiloRichiestoSB);

	}

	private void costruisciIstruzioneFormazione(Element richiestaElement, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {

		Element istruzioneFormazioneElement = doc.createElement(TAG_ISTRUZIONEFORMAZIONE);

		richiestaElement.appendChild(istruzioneFormazioneElement);

		costruisciTitoloStudio(istruzioneFormazioneElement, dc);

		SourceBean idAlboSB = (SourceBean) executeSelectByPrgRichiestaAz("MYPORTAL_GET_IDALBO", dc);
		XMLValidator.checkFieldExists(idAlboSB, "codalbo", false, albiCheck, "\"Id Albo\"");
		if (idAlboSB != null) {
			addTag(istruzioneFormazioneElement, idAlboSB);
		}

		SourceBean lingueSB = executeSelectByPrgRichiestaAzPrgAlternativa("MYPORTAL_GET_LINGUE", dc);
		XMLValidator.checkFieldExists(lingueSB, "codlingua", true, linguaCheck, "\"Id Lingua\"");
		// XMLValidator.checkFieldExists(lingueSB, "codlivelloletto", true, livelloLinguaCheck, "\"Livello lingua
		// Letto\"");
		// XMLValidator.checkFieldExists(lingueSB, "codlivelloscritto", true, livelloLinguaCheck, "\"Livello Lingua
		// Scritto\"");
		// XMLValidator.checkFieldExists(lingueSB, "codlivelloparlato", true, livelloLinguaCheck, "\"Livello Lingua
		// Parlato\"");
		generateTag(istruzioneFormazioneElement, TAG_LINGUA, lingueSB);

		// aggiunge le conoscenze informatiche al tag
		costruisciConoscenzeInformatiche(istruzioneFormazioneElement, dc);

		// aggiunge le 'altre capacità', la query restituisce più record da concatenare in un'unica stringa
		// costruisciAltreCapacita(istruzioneFormazioneElement, dc);

		// aggiunge il tag 'trasferte', la query restituisce sicuramente un solo record
		SourceBean trasferteSB = (SourceBean) executeSelectByPrgRichiestaAz("MYPORTAL_GET_TRASFERTE", dc);
		XMLValidator.checkFieldExists(trasferteSB, "trasferte", false, siNoCheck, "\"Disponibilità alle trasferte\"");
		addTag(istruzioneFormazioneElement, trasferteSB);

		// aggiunge tutti i tag 'idpatenteguida'
		SourceBean idpatenteguidaSB = (SourceBean) executeSelectByPrgRichiestaAz("MYPORTAL_GET_PATENTIGUIDA", dc);
		XMLValidator.checkFieldExists(idpatenteguidaSB, "codpatenteguida", false, lengthRangeCheck(1, 4),
				"\"Patenti di Guida\"");
		addTag(istruzioneFormazioneElement, idpatenteguidaSB);

		// aggiunge il tag 'idmezzitrasporto', la query restituisce un record
		SourceBean mezziTrasportoSB = (SourceBean) executeSelectByPrgRichiestaAz("MYPORTAL_GET_MEZZIDITRASPORTO", dc);
		XMLValidator.checkFieldExists(mezziTrasportoSB, "codmezzitrasporto", false, siNoCheck,
				"\"Disponibilità di mezzi di trasporto\"");
		addTag(istruzioneFormazioneElement, mezziTrasportoSB);

		// aggiunge i patentini
		SourceBean patentiniSB = (SourceBean) executeSelectByPrgRichiestaAz("MYPORTAL_GET_PATENTINI", dc);
		XMLValidator.checkFieldExists(patentiniSB, "codpatentino", false, lengthRangeCheck(1, 4), "\"Patentino\"");
		addTag(istruzioneFormazioneElement, patentiniSB);

		// aggiunge gli orari
		SourceBean orariSB = (SourceBean) executeSelectByPrgRichiestaAz("MYPORTAL_GET_ORARIO", dc);
		addTag(istruzioneFormazioneElement, orariSB);

		// aggiunge le agevolazioni
		/*
		 * SourceBean agevolazioniSB = (SourceBean) executeSelectByPrgRichiestaAz("MYPORTAL_GET_AGEVOLAZIONI", dc);
		 * addTag( istruzioneFormazioneElement, agevolazioniSB);
		 */
	}

	/**
	 * Inserisce il tag ALTRECAPACITA' con l'elenco suddiviso da virgola
	 * 
	 * @param istruzioneFormazioneElement
	 * @throws EMFUserError
	 */
	private void costruisciAltreCapacita(Element istruzioneFormazioneElement, DataConnection dc) throws EMFUserError {
		StringBuilder altrecapacita = new StringBuilder("");
		Vector<SourceBean> altreCapacitaSB = executeSelectByPrgRichiestaAzPrgAlternativa("MYPORTAL_GET_ALTRECAPACITA",
				dc).getAttributeAsVector("ROW");

		if (altreCapacitaSB != null) {
			for (int i = 0; i < altreCapacitaSB.size(); i++) {
				SourceBean capacitaSB = altreCapacitaSB.elementAt(i);
				altrecapacita.append(capacitaSB.getAttribute("capacita"));
				if (i != (altreCapacitaSB.size() - 1))
					altrecapacita.append(", ");
			}
		}

		if (!"".equals(altrecapacita.toString())) {
			// massimo 500 caratteri!
			Element tag = doc.createElement(TAG_PREFIX + "altrecapacita");
			Text node = doc.createTextNode(altrecapacita.substring(0, Math.min(altrecapacita.length(), 500)));
			tag.appendChild(node);
			istruzioneFormazioneElement.appendChild(tag);
		}
	}

	/**
	 * prgRichiestaAz = 56583, prgAlternativa = 1
	 *
	 * Costruisce ed inserisce tutti i tag del TITOLO DI STUDIO
	 * 
	 * @param istruzioneFormazioneElement
	 * @throws FieldFormatException
	 * @throws MandatoryFieldException
	 * @throws EMFUserError
	 * @throws CLRicercaPersonaleException
	 */
	private void costruisciTitoloStudio(Element istruzioneFormazioneElement, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		SourceBean titoloStudioSB = executeSelectByPrgRichiestaAzPrgAlternativa("MYPORTAL_GET_TITOLOSTUDIO", dc);
		XMLValidator.checkFieldExists(titoloStudioSB, "codtitolostudio", true, lengthRangeCheck(1, 8),
				"\"Titolo di studio\"");
		generateTag(istruzioneFormazioneElement, TAG_TITOLOSTUDIO, titoloStudioSB);
	}

	/**
	 *
	 * @param istruzioneFormazioneElement
	 * @throws EMFUserError
	 */
	private void costruisciConoscenzeInformatiche(Element istruzioneFormazioneElement, DataConnection dc)
			throws EMFUserError {
		SourceBean conoscenzeInformaticheSB = executeSelectByPrgRichiestaAzPrgAlternativa(
				"MYPORTAL_GET_CONOSCENZEINFORMATICHE", dc);
		if (conoscenzeInformaticheSB != null) {
			SourceBean conoscenzeFormatted = groupByFirstColumn(conoscenzeInformaticheSB, "conoscenzeinformatiche",
					1000);
			addTag(istruzioneFormazioneElement, conoscenzeFormatted);
		}
	}

	/**
	 *
	 * @param richiestaElement
	 * @throws FieldFormatException
	 * @throws MandatoryFieldException
	 * @throws EMFUserError
	 * @throws CLRicercaPersonaleException
	 */
	private void costruisciCondizioniOfferte(Element richiestaElement, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {

		Object[] inputParameters = new Object[3];
		inputParameters[0] = prgRichiestaAzienda;
		inputParameters[1] = prgRichiestaAzienda;
		inputParameters[2] = prgRichiestaAzienda;

		SourceBean condizioniOfferteSB = executeSelect("MYPORTAL_GET_CONDIZIONIOFFERTE", inputParameters, dc);

		XMLValidator.checkRowsExists(condizioniOfferteSB, TAG_CONDIZIONIOFFERTE);

		XMLValidator.checkFieldExists(condizioniOfferteSB, "codcomune", true, comuneCheck,
				"\"Comune del luogo di lavoro\"");

		// XMLValidator.checkFieldExists(condizioniOfferteSB, "codtipologiacontratto",true,modalitaCheck,"\"Codice
		// contratto\"");
		// checkField(condizioniOfferteSB, "idmodalitalavoro");

		generateTag(richiestaElement, TAG_CONDIZIONIOFFERTE, condizioniOfferteSB);

	}

	/**
	 * Costruisce la sezione relativa alla durata della richiesta. Il campo datapubblicazione è sempre presente in
	 * quanto questo metodo può essere richiamato solo su richieste pubblicate.
	 * 
	 * @param richiestaElement
	 * @throws FieldFormatException
	 * @throws MandatoryFieldException
	 * @throws EMFUserError
	 * @throws CLRicercaPersonaleException
	 *             nel caso in cui datapubblicazione non sia valorizzata.
	 */
	private void costruisciDurataRichiesta(Element richiestaElement, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		SourceBean durataRichiestaSB = executeSelectByPrgRichiestaAz("MYPORTAL_GET_DURATARICHIESTA", dc);

		XMLValidator.checkRowsExists(durataRichiestaSB, TAG_DURATARICHIESTA);

		XMLValidator.checkFieldExists(durataRichiestaSB, "datapubblicazione", true, dataCheck,
				"\"Data di pubblicazione\"");
		XMLValidator.checkFieldExists(durataRichiestaSB, "datascadenzapubblicazione", true, dataCheck,
				"\"Data di scadenza pubblicazione\"");

		generateTag(richiestaElement, TAG_DURATARICHIESTA, durataRichiestaSB);

	}

	private void costruisciDatiRegistrazioneAzienda(Element vacancyElement, DataConnection dc)
			throws EMFUserError, MandatoryFieldException, FieldFormatException {

		Element datiRegistrazioneAziendaElement = doc.createElement(TAG_REGISTRAZIONEAZIENDA);

		vacancyElement.appendChild(datiRegistrazioneAziendaElement);

		costruisciDatiRichiedente(datiRegistrazioneAziendaElement, dc);
		costruisciDatiAzienda(datiRegistrazioneAziendaElement, dc);

	}

	/**
	 *
	 * @param datiRegistrazioneAziendaElement
	 * @throws EMFUserError
	 * @throws FieldFormatException
	 * @throws MandatoryFieldException
	 */
	private void costruisciDatiRichiedente(Element datiRegistrazioneAziendaElement, DataConnection dc)
			throws EMFUserError, MandatoryFieldException, FieldFormatException {

		SourceBean datiRichiedenteSB = executeSelectByPrgRichiestaAz("MYPORTAL_GET_DATIRICHIEDENTE", dc);
		XMLValidator.checkFieldExists(datiRichiedenteSB, "emailRegistrazione", true, emailCheck, "\"Email azienda\"");
		generateTag(datiRegistrazioneAziendaElement, TAG_DATIRICHIEDENTE, datiRichiedenteSB);
	}

	/**
	 *
	 * @param datiRegistrazioneAziendaElement
	 * @throws EMFUserError
	 * @throws FieldFormatException
	 * @throws MandatoryFieldException
	 */
	private void costruisciDatiAzienda(Element datiRegistrazioneAziendaElement, DataConnection dc)
			throws EMFUserError, MandatoryFieldException, FieldFormatException {
		SourceBean datiAziendaSB = executeSelectByPrgRichiestaAz("MYPORTAL_GET_DATIAZIENDA", dc);
		XMLValidator.checkFieldExists(datiAziendaSB, "codicefiscale", true, codiceFiscaleCheck,
				"\"Codice fiscale azienda\"");
		XMLValidator.checkFieldExists(datiAziendaSB, "codComuneSedeOperativa", true, comuneCheck,
				"\"Comune della sede azienda\"");
		generateTag(datiRegistrazioneAziendaElement, TAG_DATIAZIENDA, datiAziendaSB);
	}

	private void costruisciDatiSistema(Element candidaturaElement, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {

		SourceBean datiSistemaSB = (SourceBean) executeSelectByPrgRichiestaAz("MYPORTAL_GET_DATISISTEMA", dc);

		XMLValidator.checkRowsExists(datiSistemaSB, TAG_DATISISTEMA);

		generateTag(candidaturaElement, TAG_DATISISTEMA, datiSistemaSB);

	}

	private void decodeField(SourceBean bean, String fieldName, Decoder decoder) throws EMFUserError {
		Vector<SourceBean> rows = bean.getAttributeAsVector("ROW");

		for (SourceBean row : rows) {
			Object field = row.getAttribute(fieldName);
			if (field != null) {
				String stringed = field.toString();
				String decoded = decoder.decode(stringed);
				try {
					row.updAttribute(fieldName, decoded);
				} catch (SourceBeanException e) {
					throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO);
				}
			}
		}
	}

	/**
	 * Esegue una generica query che necessita di prgRichiestaAz e prgAlternativa come parametro
	 * 
	 * @param query_name
	 *            nome della query da eseguire
	 * @return SourceBean contenente i risultati della query
	 * @throws EMFUserError
	 */
	private SourceBean executeSelectByPrgRichiestaAzPrgAlternativa(String query_name, DataConnection dc)
			throws EMFUserError {
		Object[] inputParameters = new Object[2];
		inputParameters[0] = prgRichiestaAzienda;
		inputParameters[1] = prgAlternativa;

		SourceBean ret = null;

		if (dc != null) {
			try {
				ret = executeSelect(query_name, inputParameters, dc);
			} catch (EMFUserError e) {
				_logger.error("Errore nell'esecuzione della query: " + query_name);
				throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO, new Vector());
			}
		} else {
			ret = (SourceBean) QueryExecutor.executeQuery(query_name, inputParameters, "SELECT", Values.DB_SIL_DATI);
		}

		return ret;
	}

	/**
	 * Esegue una generica query che necessita di prgRichiestaAz come parametro
	 * 
	 * @param query_name
	 *            nome della query da eseguire
	 * @return SourceBean contenente i risultati della query
	 * @throws EMFUserError
	 */
	private SourceBean executeSelectByPrgRichiestaAz(String query_name, DataConnection dc) throws EMFUserError {
		Object[] inputParameters = new Object[1];
		inputParameters[0] = prgRichiestaAzienda;

		SourceBean ret = null;

		if (dc != null) {
			try {
				ret = executeSelect(query_name, inputParameters, dc);
			} catch (EMFUserError e) {
				_logger.error("Errore nell'esecuzione della query: " + query_name);
				throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO, new Vector());
			}
		} else {
			ret = (SourceBean) QueryExecutor.executeQuery(query_name, inputParameters, "SELECT", Values.DB_SIL_DATI);
		}

		return ret;
	}

	/**
	 * Esegue una generica query che necessita di prgAzienda e prgUnita come parametri
	 * 
	 * @param query_name
	 *            nome della query da eseguire
	 * @return SourceBean contenente i risultati della query
	 * @throws EMFUserError
	 */
	private SourceBean executeSelectByPrgAziendaPrgUnita(String query_name, DataConnection dc) throws EMFUserError {
		Object[] inputParameters = new Object[1];
		inputParameters[0] = prgAzienda;

		SourceBean ret = null;

		if (dc != null) {
			try {
				ret = executeSelect(query_name, inputParameters, dc);
			} catch (EMFUserError e) {
				_logger.error("Errore nell'esecuzione della query: " + query_name);
				throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO, new Vector());
			}
		} else {
			ret = (SourceBean) QueryExecutor.executeQuery(query_name, inputParameters, "SELECT", Values.DB_SIL_DATI);
		}

		return ret;
	}

	/**
	 * Esegue una generica query che necessita di codCPI come parametro
	 * 
	 * @param query_name
	 *            nome della query da eseguire
	 * @return SourceBean contenente i risultati della query
	 */
	protected SourceBean executeSelectByCodCPI(String query_name) {
		Object[] inputParameters = new Object[1];
		inputParameters[0] = prgRichiestaAzienda;

		SourceBean ret = (SourceBean) QueryExecutor.executeQuery(query_name, inputParameters, "SELECT",
				Values.DB_SIL_DATI);

		return ret;
	}

}
