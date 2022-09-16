package it.eng.sil.coop.webservices.clicLavoro.ricercaPersonale;

import java.io.File;
import java.math.BigDecimal;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.clicLavoro.CLUtility;
import it.eng.sil.coop.webservices.clicLavoro.ricercaPersonale.decodificatori.AmpiezzaDecoder;
import it.eng.sil.coop.webservices.clicLavoro.ricercaPersonale.decodificatori.Decoder;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import it.eng.sil.util.xml.XMLValidator;

/**
 * Classe che si occupa di costruire un xml per la ricerca di personale a partire da un prgRichiesta, prgAlternativa,
 * codice del cpi e codice offerta
 * 
 * Le richieste di personale per le quali si richiede l'xml devono soddisfare determinati requisiti: il codateco
 * dell'unità azienda ha una versione in notazione puntata (AN_UNITA_AZIENDA-(codateco)-DE_ATTIVITA.CODATECODOT) la data
 * di pubblicazione non deve essere null (DO_RICHIESTA_AZIENDA.DATPUBBLICAZIONE) deve esserci almeno un record nella
 * DO_CONTRATTO, collegato alla richiesta, con FLGINVIOCL = 'S' e CODCONTRATTO diverso da null. deve esserci almeno un
 * record nella tabella DO_MANSIONE, collegato alla richiesta, con FLGINVIOCL = 'S' la mansione richiesta ha una
 * versione in notazione puntata (DO_MANSIONE-(codmansione)-DE_MANSIONE.CODMANSIONEDOT) deve esserci almeno un record
 * nella tabella DO_COMUNE, collegato alla richiesta, con FLGINVIOCL = 'S'
 * 
 * Esempio:
 * 
 * @see it.eng.sil.coop.webservices.clicLavoro.ricercaPersonale.CLRicercaPersonale#buildRichiestaDiPersonale
 * @author rodi
 *
 */

public class CLRicercaPersonaleData extends CLUtility {
	static Logger _logger = Logger.getLogger(CLRicercaPersonaleData.class.getName());
	private static final String CODTIPOCOMUNICAZIONE_BLEN = "01_BLEN";
	private static final String CODTIPOCOMUNICAZIONE_CLICLAVORO = "01";
	/**
	 * Schema XSD per la validazione dell'xml prodotto.
	 */
	final static String SCHEMA_XSD = "myportal" + File.separator + "clic_lavoro_ricerca_personale_064.xsd";

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
	private String dataScad;
	private String codCPI;
	private String strProfiloRichiesto = null;
	private String codComunicazione = null;
	private String codTipoComunicazioneCl = null;

	/**
	 * Istanzia una nuova classe per la creazione di un xml a partire dai parametri passati
	 * 
	 * @param prgRichiestaAz
	 *            progressivo della richiesta
	 * @param prgAlternativa
	 *            progressivo dell'alternativa legata alla richiesta
	 */
	public CLRicercaPersonaleData(BigDecimal prgRichiestaAz, BigDecimal prgAlternativa, String codCPI,
			String codTipoComunicazioneCl) {
		super(SCHEMA_XSD);
		this.prgAlternativa = prgAlternativa;
		this.prgRichiestaAzienda = prgRichiestaAz;
		this.codCPI = codCPI;
		this.codTipoComunicazioneCl = codTipoComunicazioneCl;
	}

	/**
	 * Istanzia una nuova classe per la creazione di un xml a partire dai parametri passati
	 * 
	 * @param prgRichiestaAz
	 *            progressivo della richiesta
	 * @param prgAlternativa
	 *            progressivo dell'alternativa legata alla richiesta
	 */
	public CLRicercaPersonaleData(BigDecimal prgRichiestaAz, BigDecimal prgAlternativa, String codCPI,
			String strProfiloRichiesto, String codTipoComunicazioneCl) {
		super(SCHEMA_XSD);
		this.prgAlternativa = prgAlternativa;
		this.prgRichiestaAzienda = prgRichiestaAz;
		this.codCPI = codCPI;
		this.strProfiloRichiesto = strProfiloRichiesto;
		this.codTipoComunicazioneCl = codTipoComunicazioneCl;
	}

	public CLRicercaPersonaleData(BigDecimal prgRichiestaAz, BigDecimal prgAlternativa, String codCPI,
			String strProfiloRichiesto, String codComunicazione, String dataScad, String codTipoComunicazioneCl) {
		super(SCHEMA_XSD);
		this.prgAlternativa = prgAlternativa;
		this.prgRichiestaAzienda = prgRichiestaAz;
		this.codCPI = codCPI;
		this.strProfiloRichiesto = strProfiloRichiesto;
		this.codComunicazione = codComunicazione;
		this.dataScad = dataScad;
		this.codTipoComunicazioneCl = codTipoComunicazioneCl;
	}

	public void setDataScad(String dataScad) {
		this.dataScad = dataScad;
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
	public void costruisci(boolean checkPreInvio) throws EMFUserError, MandatoryFieldException, FieldFormatException {

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
				"http://servizi.lavoro.gov.it/vacancy clic_lavoro_ricerca_personale_064.xsd");
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
						"Errore nel recupero di prgazienda e prgunita per la generazione dell'xml da inviare a clic lavoro.");
				throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_INPUT_ERRATO,
						"Azienda o Unità Azienda non trovate.");
			}
		}

		costruisciDatoreLavoro(rootElement, null, checkPreInvio);

		costruisciRichiesta(rootElement, null);

		costruisciAltreInformazioni(rootElement, null);

		costruisciDatiSistema(rootElement, null, checkPreInvio);

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
				"http://servizi.lavoro.gov.it/vacancy clic_lavoro_ricerca_personale_064.xsd");
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

			SourceBean ret = executeSelect("RP_GET_PRGAZIENDA_PRGUNITA", inputParameters, dc);

			this.prgAzienda = (BigDecimal) ret.getAttribute("ROW.PRGAZIENDA");
			this.prgUnita = (BigDecimal) ret.getAttribute("ROW.PRGUNITA");

			if (prgAzienda == null || prgUnita == null) {
				_logger.error(
						"Errore nel recupero di prgazienda e prgunita per la generazione dell'xml da inviare a clic lavoro.");
				throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_INPUT_ERRATO,
						"Azienda o Unità Azienda non trovate.");
			}
		}

		costruisciDatoreLavoro(rootElement, dc, false);

		costruisciRichiesta(rootElement, dc);

		costruisciAltreInformazioni(rootElement, dc);

		costruisciDatiSistema(rootElement, dc, false);

	}

	private void costruisciDatoreLavoro(Element vacancyElement, DataConnection dc, boolean checkPreInvio)
			throws EMFUserError, MandatoryFieldException, FieldFormatException {

		Element datoreLavoroElement = doc.createElement(TAG_DATORELAVORO);

		vacancyElement.appendChild(datoreLavoroElement);

		costruisciDatiAnagrafici(datoreLavoroElement, dc);
		costruisciDatiContatto(datoreLavoroElement, dc, checkPreInvio);

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
		SourceBean datiAnagraficiSB = executeSelectByPrgAziendaPrgUnita("RP_GET_DATIANAGRAFICI", dc);

		XMLValidator.checkRowsExists(datiAnagraficiSB, TAG_DATIANAGRAFICI);

		decodeField(datiAnagraficiSB, "ampiezza", new AmpiezzaDecoder());

		XMLValidator.checkFieldExists(datiAnagraficiSB, "codicefiscale", true, codiceFiscaleCheck,
				"\"Codice fiscale del Datore di lavoro\"");
		XMLValidator.checkFieldExists(datiAnagraficiSB, "denominazione", true, lengthRangeCheck(1, 100),
				"\"Denominazione del Datore di lavoro\"");
		XMLValidator.checkFieldExists(datiAnagraficiSB, "settore", true, atecoCheck,
				"\"Settore del Datore di lavoro\"");
		XMLValidator.checkFieldExists(datiAnagraficiSB, "ampiezza", false, ampiezzaCheck,
				"\"Ampiezza del Datore di lavoro\"");

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
	private void costruisciDatiContatto(Element datoreLavoroElement, DataConnection dc, boolean checkPreInvio)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		SourceBean datiContattoSB = null;
		if (checkPreInvio) {
			datiContattoSB = executeSelectByPrgRichiestaAzAndCodCPI("RP_GET_DATICONTATTO_PREINVIO", dc);
		} else {
			datiContattoSB = executeSelectByPrgRichiestaAzAndCodCL("RP_GET_DATICONTATTO", dc);
		}
		XMLValidator.checkRowsExists(datiContattoSB, TAG_DATICONTATTO);
		XMLValidator.checkFieldExists(datiContattoSB, "idcomune", false, comuneCheck,
				"\"Comune del Datore di lavoro\"");
		XMLValidator.checkFieldExists(datiContattoSB, "cap", false, capCheck, "\"Cap del Datore di lavoro\"");
		XMLValidator.checkFieldExists(datiContattoSB, "email", true, emailCheck,
				"è obbligatorio compilare il campo email del \"Riferimento\" nella sezione \"Pubblicazione\" o il campo email del \"Riferimento\" nella sezione \"Dati generali\"");
		generateTag(datoreLavoroElement, TAG_DATICONTATTO, datiContattoSB);
	}

	/**
	 * Esegue una generica query che necessita di prgRichiestaAz e codTipoComunicazioneCl come parametro
	 * 
	 * @param query_name
	 *            nome della query da eseguire
	 * @return SourceBean contenente i risultati della query
	 * @throws EMFUserError
	 */
	private SourceBean executeSelectByPrgRichiestaAzAndCodCL(String query_name, DataConnection dc) throws EMFUserError {
		Object[] inputParameters = new Object[2];
		inputParameters[0] = prgRichiestaAzienda;
		inputParameters[1] = codTipoComunicazioneCl;

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

	private void costruisciRichiesta(Element vacancyElement, DataConnection dc)
			throws EMFUserError, MandatoryFieldException, FieldFormatException {
		Element richiestaElement = doc.createElement(TAG_RICHIESTA);

		vacancyElement.appendChild(richiestaElement);

		costruisciProfiloRichiesto(richiestaElement, dc);
		costruisciIstruzioneFormazione(richiestaElement, dc);
		costruisciCondizioniOfferte(richiestaElement, dc);
		costruisciDurataRichiesta(richiestaElement, dc);
	}

	private void costruisciAltreInformazioni(Element vacancyElement, DataConnection dc)
			throws EMFUserError, MandatoryFieldException, FieldFormatException {
		SourceBean altreInformazioniSB;
		if (codTipoComunicazioneCl.equalsIgnoreCase(CODTIPOCOMUNICAZIONE_BLEN)) {
			// PER ORA A BLEN BISOGNA SEMPRE INVIARE "NO"
			altreInformazioniSB = executeSelect("RP_GET_ALTREINFORMAZIONI_BLEN");
		} else {
			altreInformazioniSB = executeSelectByPrgRichiestaAz("RP_GET_ALTREINFORMAZIONI", dc);
		}

		XMLValidator.checkRowsExists(altreInformazioniSB, TAG_ALTREINFORMAZIONI);
		XMLValidator.checkFieldExists(altreInformazioniSB, "FLGNULLAOSTA", true, siNoCheck, "\"N.O.\"");

		generateTagReplaceTagName(vacancyElement, TAG_ALTREINFORMAZIONI, altreInformazioniSB, "FLGNULLAOSTA", "N.O.");
	}

	private void generateTagReplaceTagName(Element parent, String tagName, SourceBean sourceBean,
			String nameAttributeSB, String nameTag) {
		Vector<SourceBean> rows = sourceBean.getAttributeAsVector("ROW");

		// per ogni record presente nel sourceBean
		for (SourceBean row : rows) {
			// crea un nuovo tag
			Element element = doc.createElement(tagName);

			addSourceBeanToElementReplaceTagName(row, element, nameAttributeSB, nameTag);

			// aggiungi l'elemento al padre
			parent.appendChild(element);

		} // prossimo record

		return;
	}

	private void addSourceBeanToElementReplaceTagName(SourceBean bean, Element parent, String nameAttributeSB,
			String nameTag) {
		Vector<SourceBeanAttribute> attributi = bean.getContainedAttributes();

		if (!attributi.isEmpty()) {
			/* per ogni attributo all'interno del sourceBean */
			for (SourceBeanAttribute attributo : attributi) {
				String nome = attributo.getKey().toLowerCase();

				if (nome.equalsIgnoreCase(nameAttributeSB)) {
					nome = nameTag;
				}

				Object valore = attributo.getValue();

				Element tag = doc.createElement(TAG_PREFIX + nome);
				Text node = doc.createTextNode(String.valueOf(valore));
				tag.appendChild(node);

				parent.appendChild(tag);

			}
		}
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
		SourceBean profiloRichiestoSB = executeSelectByPrgRichiestaAzPrgAlternativaStrProfilo("RP_GET_PROFILORICHIESTO",
				dc);

		XMLValidator.checkRowsExists(profiloRichiestoSB, TAG_PROFILORICHIESTO);

		XMLValidator.checkFieldExists(profiloRichiestoSB, "numerolavoratori", true, numerico1_4Check,
				"\"Numero di lavoratori Richiesto\"");
		XMLValidator.checkFieldExists(profiloRichiestoSB, "idprofessione", true, qualificaCheck, "\"Id Professione\"");
		XMLValidator.checkFieldExists(profiloRichiestoSB, "descrprofessione", true, lengthRangeCheck(1, 250),
				"\"Descrizione Professione\"");
		XMLValidator.checkFieldExists(profiloRichiestoSB, "descrizionericerca", true, null, "\"Descrizione Ricerca\"");

		generateTag(richiestaElement, TAG_PROFILORICHIESTO, profiloRichiestoSB);

	}

	private void costruisciIstruzioneFormazione(Element richiestaElement, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {

		Element istruzioneFormazioneElement = doc.createElement(TAG_ISTRUZIONEFORMAZIONE);

		richiestaElement.appendChild(istruzioneFormazioneElement);

		costruisciTitoloStudio(istruzioneFormazioneElement, dc);

		SourceBean idAlboSB = (SourceBean) executeSelectByPrgRichiestaAz("RP_GET_IDALBO", dc);
		XMLValidator.checkFieldExists(idAlboSB, "idalbo", false, albiCheck, "\"Id Albo\"");
		if (idAlboSB != null) {
			addTag(istruzioneFormazioneElement, idAlboSB);
		}

		SourceBean lingueSB = executeSelectByPrgRichiestaAzPrgAlternativa("RP_GET_LINGUE", dc);
		XMLValidator.checkFieldExists(lingueSB, "idlingua", true, linguaCheck, "\"Id Lingua\"");
		XMLValidator.checkFieldExists(lingueSB, "idlivelloletto", true, livelloLinguaCheck, "\"Livello lingua Letto\"");
		XMLValidator.checkFieldExists(lingueSB, "idlivelloscritto", true, livelloLinguaCheck,
				"\"Livello Lingua Scritto\"");
		XMLValidator.checkFieldExists(lingueSB, "idlivelloparlato", true, livelloLinguaCheck,
				"\"Livello Lingua Parlato\"");
		generateTag(istruzioneFormazioneElement, TAG_LINGUA, lingueSB);

		// aggiunge le conoscenze informatiche al tag
		costruisciConoscenzeInformatiche(istruzioneFormazioneElement, dc);

		// aggiunge le 'altre capacità', la query restituisce più record da concatenare in un'unica stringa
		costruisciAltreCapacita(istruzioneFormazioneElement, dc);

		// aggiunge il tag 'trasferte', la query restituisce sicuramente un solo record
		SourceBean trasferteSB = (SourceBean) executeSelectByPrgRichiestaAz("RP_GET_TRASFERTE", dc);
		XMLValidator.checkFieldExists(trasferteSB, "trasferte", false, siNoCheck, "\"Disponibilità alle trasferte\"");
		addTag(istruzioneFormazioneElement, trasferteSB);

		// aggiunge tutti i tag 'idpatenteguida'
		SourceBean idpatenteguidaSB = (SourceBean) executeSelectByPrgRichiestaAz("RP_GET_PATENTIGUIDA", dc);
		XMLValidator.checkFieldExists(idpatenteguidaSB, "idpatenteguida", false, patentiCheck, "\"Patenti di Guida\"");
		addTag(istruzioneFormazioneElement, idpatenteguidaSB);

		// aggiunge il tag 'idmezzitrasporto', la query restituisce un record
		SourceBean mezziTrasportoSB = (SourceBean) executeSelectByPrgRichiestaAz("RP_GET_MEZZIDITRASPORTO", dc);
		XMLValidator.checkFieldExists(mezziTrasportoSB, "idmezzitrasporto", false, siNoCheck,
				"\"Disponibilità di mezzi di trasporto\"");
		addTag(istruzioneFormazioneElement, mezziTrasportoSB);

		// aggiunge i patentini
		SourceBean patentiniSB = (SourceBean) executeSelectByPrgRichiestaAz("RP_GET_PATENTINI", dc);
		XMLValidator.checkFieldExists(patentiniSB, "idpatentino", false, patentiCheck, "\"Patentino\"");
		addTag(istruzioneFormazioneElement, patentiniSB);

		// aggiunge liste speciali
		// SourceBean listeSpecialiSB = (SourceBean) executeSelectByPrgRichiestaAz("RP_GET_LISTE_SPECIALI");
		// XMLValidator.checkFieldExists(listeSpecialiSB, "idlistespeciali", false, listeSpecialiCheck, "\"Liste
		// Speciali\"");
		// addTag( istruzioneFormazioneElement, listeSpecialiSB);

		// aggiunge ulteriori requisiti

	}

	/**
	 * Inserisce il tag ALTRECAPACITA' con l'elenco suddiviso da virgola
	 * 
	 * @param istruzioneFormazioneElement
	 * @throws EMFUserError
	 */
	private void costruisciAltreCapacita(Element istruzioneFormazioneElement, DataConnection dc) throws EMFUserError {
		StringBuilder altrecapacita = new StringBuilder("");
		Vector<SourceBean> altreCapacitaSB = executeSelectByPrgRichiestaAzPrgAlternativa("RP_GET_ALTRECAPACITA", dc)
				.getAttributeAsVector("ROW");

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
		SourceBean titoloStudioSB = executeSelectByPrgRichiestaAzPrgAlternativa("RP_GET_TITOLOSTUDIO", dc);
		XMLValidator.checkFieldExists(titoloStudioSB, "idtitolostudio", true, titoloStudioCheck,
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
				"RP_GET_CONOSCENZEINFORMATICHE", dc);
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

		Object[] inputParameters = new Object[4];
		inputParameters[0] = prgRichiestaAzienda;
		inputParameters[1] = prgRichiestaAzienda;
		inputParameters[2] = prgRichiestaAzienda;
		inputParameters[3] = prgRichiestaAzienda;

		SourceBean condizioniOfferteSB = executeSelect("RP_GET_CONDIZIONIOFFERTE", inputParameters, dc);

		XMLValidator.checkRowsExists(condizioniOfferteSB, TAG_CONDIZIONIOFFERTE);

		XMLValidator.checkFieldExists(condizioniOfferteSB, "idcomune", true, comuneCheck,
				"\"Comune del luogo di lavoro\"");

		XMLValidator.checkFieldExists(condizioniOfferteSB, "idtipologiacontratto", true, rapportiCheck,
				"\"Tipologia contratto\"");
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
		// SourceBean durataRichiestaSB = executeSelectByPrgRichiestaAz("RP_GET_DURATARICHIESTA", dc);
		SourceBean durataRichiestaSB = executeSelectByPrgRichiestaAzAndDataScad("RP_GET_DURATARICHIESTA", dataScad, dc);

		XMLValidator.checkRowsExists(durataRichiestaSB, TAG_DURATARICHIESTA);

		// XMLValidator.checkFieldExists(durataRichiestaSB, "datapubblicazione",true,dataCheck,"\"Data di
		// pubblicazione\"");
		XMLValidator.checkFieldExists(durataRichiestaSB, "datapubblicazione", true, dataTimeCheck,
				"\"Data di pubblicazione\"");
		XMLValidator.checkFieldExists(durataRichiestaSB, "datascadenza", true, dataCheck, "\"Data di scadenza\"");

		generateTag(richiestaElement, TAG_DURATARICHIESTA, durataRichiestaSB);

	}

	private void costruisciDatiSistema(Element candidaturaElement, DataConnection dc, boolean checkPreInvio)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {

		SourceBean datiSistemaSB = null;

		if (checkPreInvio) {
			datiSistemaSB = (SourceBean) QueryExecutor.executeQuery("RP_GET_DATISISTEMA_PREINVIO", new Object[] {
					prgRichiestaAzienda, codComunicazione, prgRichiestaAzienda, prgRichiestaAzienda, codCPI }, "SELECT",
					Values.DB_SIL_DATI);
		} else {
			if (dc != null) {
				try {
					datiSistemaSB = executeSelect("RP_GET_DATISISTEMA",
							new Object[] { prgRichiestaAzienda, prgRichiestaAzienda, codTipoComunicazioneCl,
									prgRichiestaAzienda, codTipoComunicazioneCl, codCPI },
							dc);
				} catch (EMFUserError e) {
					_logger.error("Errore nell'esecuzione della query: RP_GET_DATISISTEMA");
					throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO,
							new Vector());
				}
			} else {
				datiSistemaSB = (SourceBean) QueryExecutor.executeQuery(
						"RP_GET_DATISISTEMA", new Object[] { prgRichiestaAzienda, prgRichiestaAzienda,
								codTipoComunicazioneCl, prgRichiestaAzienda, codTipoComunicazioneCl, codCPI },
						"SELECT", Values.DB_SIL_DATI);
			}
		}

		// try {
		// datiSistemaSB.updAttribute("ROW.CODICEOFFERTA", codiceOfferta);
		// datiSistemaSB.updAttribute("ROW.CODICEOFFERTAINTERMEDIARIO", codiceOfferta);
		// } catch (SourceBeanException ex) {
		// _logger.error("Errore inaspettato durante l'aggiornamento dati del SourceBean",ex);
		// }

		XMLValidator.checkRowsExists(datiSistemaSB, TAG_DATISISTEMA);

		XMLValidator.checkFieldExists(datiSistemaSB, "cap", false, capCheck, "\"Cap del CPI\"");

		XMLValidator.checkFieldExists(datiSistemaSB, "tipoofferta", false, tipoComunicazioneCheck, "\"Tipo Offerta\"");
		XMLValidator.checkFieldExists(datiSistemaSB, "codiceofferta", false, lengthRangeCheck(1, 25),
				"\"Codice offerta\"");
		XMLValidator.checkFieldExists(datiSistemaSB, "codiceoffertaintermediario", false, lengthRangeCheck(1, 25),
				"\"Codice offerta intermediario\"");

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

	private SourceBean executeSelectByPrgRichiestaAzPrgAlternativaStrProfilo(String query_name, DataConnection dc)
			throws EMFUserError {
		Object[] inputParameters = new Object[4];
		inputParameters[0] = strProfiloRichiesto;
		inputParameters[1] = codTipoComunicazioneCl;
		inputParameters[2] = prgRichiestaAzienda;
		inputParameters[3] = prgAlternativa;

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

	private SourceBean executeSelectByPrgRichiestaAzAndDataScad(String query_name, String dataScad, DataConnection dc)
			throws EMFUserError {
		Object[] inputParameters = new Object[2];
		inputParameters[0] = dataScad;
		inputParameters[1] = prgRichiestaAzienda;

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
	 * Esegue una generica query che necessita di prgRichiestaAz e codCPI come parametri
	 * 
	 * @param query_name
	 *            nome della query da eseguire
	 * @return SourceBean contenente i risultati della query
	 * @throws EMFUserError
	 */
	private SourceBean executeSelectByPrgRichiestaAzAndCodCPI(String query_name, DataConnection dc)
			throws EMFUserError {
		Object[] inputParameters = new Object[2];
		inputParameters[0] = codCPI;
		inputParameters[1] = prgRichiestaAzienda;

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
