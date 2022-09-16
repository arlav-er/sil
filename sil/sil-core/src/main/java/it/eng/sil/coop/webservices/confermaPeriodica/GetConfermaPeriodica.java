package it.eng.sil.coop.webservices.confermaPeriodica;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.confermaPeriodica.input.ConfermaPeriodica;
import it.eng.sil.coop.webservices.confermaPeriodica.output.Errore;
import it.eng.sil.coop.webservices.confermaPeriodica.output.Errori;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import it.eng.sil.util.xml.XMLValidator;

public class GetConfermaPeriodica {

	private final Integer CDN_UT_SIL_PORTALE = 150;
	private static final String ESITO_OK = "00";
	private static final String OR_PER = "OR_PER";
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetConfermaPeriodica.class.getName());

	public static final Pattern codiceFiscaleCheck = Pattern.compile(
			"([A-Z]{6}[0-9LMNPQRSTUV]{2}[ABCDEHLMPRST]{1}[0-9LMNPQRSTUV]{2}[A-Z]{1}[0-9LMNPQRSTUV]{3}[A-Z]{1})|([0-9]{11})");
	public static final Pattern emailCheck = Pattern.compile(
			"([A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*@[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*)?");

	protected static final String XSD_PATH = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "ConfermaPeriodica" + File.separator;

	protected static final File inputXsd = new File(XSD_PATH + "inputXML_confermaPeriodica.xsd");
	protected static final File outputXsd = new File(XSD_PATH + "outputXML_confermaPeriodica.xsd");

	private String esito = "00";
	private ConfermaPeriodica cpInput = null;
	private BigDecimal cdnLavoratore = null;
	private BigDecimal prgPatto = null;
	private String datscadenzaPatto = "";
	private BigDecimal prgColloquio = null;
	private BigDecimal prgPercorso = null;
	private String datstimata = "";
	private BigDecimal prgAzioni = null;
	private String codCpi = "";

	/**
	 * gestione conferma periodica
	 * 
	 * @param XML
	 *            stringa da validare con l'xsd di input
	 * @return Restituisce l'XML validato con l'xsd di output
	 */
	public String getConfermaPeriodica(String inputXML) {
		boolean esitoKO_AggiornamentoUtente = false;
		String outputXml = "";

		try {
			if (checkInputXml(inputXML)) {
				if (checkEsistenzaLavoratore()) {
					if (checkRequisiti()) {
						if (checkRangeTemporale()) {
							gestisciAzioniContatti();
							esitoKO_AggiornamentoUtente = aggiornaLavoratore();
						}
					}
				}
			}
		} catch (Exception e) {
			_logger.error("Errore generico");
			esito = "11";
		}

		outputXml = createXMLesito(esitoKO_AggiornamentoUtente);

		if (!isXmlValid(outputXml, outputXsd)) {
			_logger.error("Errore validazione output xml");
			esito = "07";
			outputXml = createXMLesito(esitoKO_AggiornamentoUtente);
		}

		return outputXml;
	}

	private final boolean checkInputXml(String inputXML) throws Exception {
		boolean checkokIX = true;

		if (!isXmlValid(inputXML, inputXsd)) {
			_logger.error("Errore validazione input xml");
			checkokIX = false;
		} else {
			cpInput = convertToConfermaPeriodica(inputXML);

			// Controllo validita codice fiscale ed email
			try {
				XMLValidator.checkObjectFieldExists(cpInput.getCodiceFiscale(), "codicefiscale", true,
						codiceFiscaleCheck, "\"Codice fiscale del lavoratore\"");
				XMLValidator.checkObjectFieldExists(cpInput.getEmail(), "email", true, emailCheck,
						"\"Email del lavoratore\"");
			} catch (MandatoryFieldException e1) {
				_logger.error("Errore validazione input xml");
				checkokIX = false;
			} catch (FieldFormatException e2) {
				_logger.error("Errore validazione input xml");
				checkokIX = false;
			}
		}
		if (!checkokIX)
			esito = "07";
		return checkokIX;
	}

	private final boolean checkEsistenzaLavoratore() throws Exception {
		boolean checkokEL = true;
		Object[] inputParameters = new Object[1];
		inputParameters[0] = cpInput.getCodiceFiscale();

		SourceBean lavoratore = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE", inputParameters,
				"SELECT", Values.DB_SIL_DATI);
		if (!lavoratore.containsAttribute("ROW.CDNLAVORATORE")) {
			_logger.error("Lavoratore non presente sul SIL");
			checkokEL = false;
			esito = "06";
		} else {
			cdnLavoratore = (BigDecimal) lavoratore.getAttribute("ROW.CDNLAVORATORE");
		}
		return checkokEL;
	}

	private final boolean checkRequisiti() throws Exception {
		boolean checkokR = true;
		Object[] inputParameters = new Object[1];
		inputParameters[0] = cdnLavoratore;

		SourceBean didAttiva = (SourceBean) QueryExecutor.executeQuery("GET_DID_LAVORATORE_CLOSEDID", inputParameters,
				"SELECT", Values.DB_SIL_DATI);
		if (!didAttiva.containsAttribute("ROW")) {
			_logger.error("Lavoratore non possiede una DID attiva protocollata sul SIL");
			esito = "02a";
			checkokR = false;
		} else {
			SourceBean pattoAperto = (SourceBean) QueryExecutor.executeQuery("GET_PATTO_APERTO_CONFERMA_PERIODICA",
					inputParameters, "SELECT", Values.DB_SIL_DATI);
			if (!pattoAperto.containsAttribute("ROW")) {
				_logger.error("Lavoratore non possiede un patto attivo protocollato sul SIL");
				esito = "02b";
				checkokR = false;
			} else {
				datscadenzaPatto = StringUtils.getAttributeStrNotNull(pattoAperto, "ROW.datscadenzaPatto");
				prgPatto = (BigDecimal) pattoAperto.getAttribute("ROW.PRGPATTOLAVORATORE");
				SourceBean confPerInCorso = (SourceBean) QueryExecutor.executeQuery("GET_CONFERMA_PERIODICA_IN_CORSO",
						inputParameters, "SELECT", Values.DB_SIL_DATI);
				if (!confPerInCorso.containsAttribute("ROW")) {
					_logger.error("Lavoratore non possiede una conferma periodica con esito 'in corso' sul SIL");
					esito = "02c";
					checkokR = false;
				} else {
					prgColloquio = (BigDecimal) confPerInCorso.getAttribute("ROW.PRGCOLLOQUIO");
					prgPercorso = (BigDecimal) confPerInCorso.getAttribute("ROW.PRGPERCORSO");
					datstimata = (String) confPerInCorso.getAttribute("ROW.datstimata");
					prgAzioni = (BigDecimal) confPerInCorso.getAttribute("ROW.prgAzioni");
					codCpi = (String) confPerInCorso.getAttribute("ROW.codCpi");

					if ("".equals(datscadenzaPatto) || DateUtils.compare(datstimata, datscadenzaPatto) != -1) {
						_logger.error(
								"Lavoratore con data stimata dell'azione di conferma periodica successiva alla data scadenza del patto");
						esito = "02d";
						checkokR = false;
					}
				}
			}
		}
		return checkokR;
	}

	private final boolean checkRangeTemporale() throws Exception {
		boolean checkokRT = true;
		String oggi = DateUtils.getNow();
		SourceBean checkSBGiorniCP = (SourceBean) QueryExecutor
				.executeQuery("GET_CONFIG_CHECK_GIORNI_CONFERMA_PERIODICA", null, "SELECT", Values.DB_SIL_DATI);
		int giorniCP = Integer.parseInt((String) checkSBGiorniCP.getAttribute("ROW.strvalore"));

		String datStimataPiuGiorniCP = DateUtils.aggiungiNumeroGiorni(datstimata, giorniCP);
		String datStimataMenoGiorniCP = DateUtils.aggiungiNumeroGiorni(datstimata, -giorniCP);
		if (DateUtils.compare(datStimataMenoGiorniCP, oggi) == 1
				|| DateUtils.compare(oggi, datStimataPiuGiorniCP) == 1) {
			_logger.error("Azione di conferma periodica del lavoratore al di fuori del range temporale stabilito");
			checkokRT = false;
			esito = "03";
		}
		return checkokRT;
	}

	private final void gestisciAzioniContatti() {
		TransactionQueryExecutor transExec = null;

		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			transExec.initTransaction();

			/* termino la validita' del record esistente */
			Object queryCpChiusura = transExec.executeQuery("CHIUDI_PERCORSO_CONFERMA_PERIODICA",
					new Object[] { CDN_UT_SIL_PORTALE, prgPercorso, prgColloquio }, "UPDATE");
			if (queryCpChiusura == null
					|| !(queryCpChiusura instanceof Boolean && ((Boolean) queryCpChiusura).booleanValue() == true)) {
				throw new Exception("Impossibile eseguire update per chiusura conferma periodica");
			}

			// inserisco un nuovo record
			SourceBean resNewPrgPercorso = (SourceBean) transExec.executeQuery("OR_PERCORSO_CONCORDATO_NEXTVAL", null,
					"SELECT");
			BigDecimal prgPercorsoNew = resNewPrgPercorso.containsAttribute("ROW")
					? (BigDecimal) resNewPrgPercorso.getAttribute("ROW.do_nextval")
					: (BigDecimal) resNewPrgPercorso.getAttribute("do_nextval");

			Object queryCpApertura = transExec.executeQuery("APRI_NUOVO_PERCORSO_CONFERMA_PERIODICA", new Object[] {
					prgColloquio, prgPercorsoNew, datscadenzaPatto, prgAzioni, CDN_UT_SIL_PORTALE, CDN_UT_SIL_PORTALE },
					"INSERT");
			if (queryCpApertura == null
					|| !(queryCpApertura instanceof Boolean && ((Boolean) queryCpApertura).booleanValue() == true)) {
				throw new Exception("Impossibile eseguire insert per apertura nuova conferma periodica");
			}

			// inserisco una nuova associazione al patto
			Object queryCpAssociazione = transExec.executeQuery("INS_LAV_PATTO_SCELTA",
					new Object[] { prgPatto, OR_PER, prgPercorsoNew.toString() }, "INSERT");
			if (queryCpAssociazione == null || !(queryCpAssociazione instanceof Boolean
					&& ((Boolean) queryCpAssociazione).booleanValue() == true)) {
				throw new Exception("Impossibile eseguire insert per nuova associazione al patto");
			}

			// inserisco un nuovo contatto
			SourceBean resNewPrgSpi = (SourceBean) transExec.executeQuery("IDO_GET_PRGSPIUT",
					new Object[] { CDN_UT_SIL_PORTALE }, "SELECT");
			BigDecimal prgSpi = resNewPrgSpi.containsAttribute("ROW")
					? (BigDecimal) resNewPrgSpi.getAttribute("ROW.prgspi")
					: (BigDecimal) resNewPrgSpi.getAttribute("prgspi");

			Object queryCpContatto = transExec.executeQuery("INSERT_AG_CONTATTO_CONFERMA_PERIODICA",
					new Object[] { codCpi, prgSpi, cdnLavoratore, CDN_UT_SIL_PORTALE, CDN_UT_SIL_PORTALE }, "INSERT");
			if (queryCpContatto == null
					|| !(queryCpContatto instanceof Boolean && ((Boolean) queryCpContatto).booleanValue() == true)) {
				throw new Exception("Impossibile eseguire insert per nuovo contatto");
			}

			transExec.commitTransaction();
		} catch (Exception e) {
			if (transExec != null) {
				try {
					transExec.rollBackTransaction();
				} catch (EMFInternalError e1) {
					e1.printStackTrace();
				}
			}
			_logger.error("Errore durante la gestione delle azioni o dei contatti del lavoratore");
			esito = "04";
		}
	}

	private final boolean aggiornaLavoratore() throws Exception {
		/* aggiorno i dati del lavoratore (cellulare e mail) */
		Boolean resQueryAggLav = (Boolean) QueryExecutor.executeQuery("AGGIORNA_AN_LAVORATORE_CONFERMA_PERIODICA",
				new Object[] { cpInput.getCellulare(), cpInput.getEmail(), CDN_UT_SIL_PORTALE, cdnLavoratore },
				"UPDATE", Values.DB_SIL_DATI);
		if (!resQueryAggLav.booleanValue()) {
			_logger.error("Errore durante aggiornamento dati del lavoratore");
			return true;
		}
		return false;
	}

	private final String createXMLesito(boolean esitoKOAggiornamentoUtente) {

		_logger.debug("createXMLesito() - start generazione xml ");
		String returnString = "";

		it.eng.sil.coop.webservices.confermaPeriodica.output.Esito esitoOutput = new it.eng.sil.coop.webservices.confermaPeriodica.output.Esito();
		Errori erroriOutput = new Errori();

		String descrEsito = setDescrizioneEsito();

		if (ESITO_OK.equals(esito)) {
			esitoOutput.setCodice("OK");
			esitoOutput.setDescrizione("Operazione completata");
		} else {
			esitoOutput.setCodice("KO");
			esitoOutput.setDescrizione("Operazione fallita");

			Errore erroreOutput = new Errore();
			erroreOutput.setCodiceErrore(esito);
			erroreOutput.setDescrizioneErrore(descrEsito);
			erroriOutput.getErrore().add(erroreOutput);
			esitoOutput.setErrori(erroriOutput);
		}

		if (esitoKOAggiornamentoUtente) {
			Errore erroreOutput = new Errore();
			erroreOutput.setCodiceErrore("05");
			erroreOutput.setDescrizioneErrore("Errore nell'aggiornamento dei dati dell'utente.");
			erroriOutput.getErrore().add(erroreOutput);
			esitoOutput.setErrori(erroriOutput);
		}

		_logger.debug("createXMLesito() - end generazione xml ");

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(it.eng.sil.coop.webservices.confermaPeriodica.output.Esito.class);
			StringWriter writer = new StringWriter();
			jaxbContext.createMarshaller().marshal(esitoOutput, writer);
			returnString = writer.toString();
		} catch (JAXBException e) {
			_logger.error("Errore nella creazione dell' xml da inviare", e);
		}

		return returnString;
	}

	private final String setDescrizioneEsito() {
		String descrizioneEsito = "";

		if ("02a".equals(esito)) {
			descrizioneEsito = "Impossibile elaborare la richiesta. Non risulta una Dichiarazione di Immediata Disponibilità rilasciata. Per verificare la tua situazione puoi recarti al tuo Centro per l'Impiego di competenza.";
		} else if ("02b".equals(esito)) {
			descrizioneEsito = "Impossibile elaborare la richiesta. Non risulta un Patto di Servizio stipulato. Per verificare la tua situazione puoi recarti al tuo Centro per l'Impiego di competenza.";
		} else if ("02c".equals(esito)) {
			descrizioneEsito = "Impossibile elaborare la richiesta. Non risulta una Conferma Periodica in corso. Per verificare la tua situazione puoi recarti al tuo Centro per l'Impiego di competenza.";
		} else if ("02d".equals(esito)) {
			descrizioneEsito = "Impossibile elaborare la richiesta. Il tuo Patto di Servizio risulta in scadenza o è già scaduto. Devi recarti personalmente al tuo Centro per l'Impiego di competenza per il rinnovo.";
		} else if ("03".equals(esito)) {
			descrizioneEsito = "Impossibile elaborare la richiesta. La conferma della DID deve essere resa entro il termine indicato nel Patto di Servizio da te sottoscritto e in tuo possesso. Per verificare la tua situazione puoi recarti al tuo Centro per l'Impiego di competenza.";
		} else if ("04".equals(esito)) {
			descrizioneEsito = "Errore generico durante l'operazione.";
		} else if ("06".equals(esito)) {
			descrizioneEsito = "Lavoratore non trovato sul sistema SIL.";
		} else if ("07".equals(esito)) {
			descrizioneEsito = "Errore nella validazione dell'XML.";
		} else if ("11".equals(esito)) {
			descrizioneEsito = "Errore generico.";
		}
		return descrizioneEsito;
	}

	protected boolean isXmlValid(String inputXML, File schemaFile) {

		String validityErrors = XMLValidator.getValidityErrors(inputXML, schemaFile);

		if (validityErrors != null) {
			_logger.error("Errori di validazione file XML:\n" + validityErrors);
			return false;
		}
		return true;
	}

	public ConfermaPeriodica convertToConfermaPeriodica(String xmlConfermaPeriodica) throws JAXBException {
		JAXBContext jaxbContext;
		ConfermaPeriodica confermaPeriodica = null;
		try {
			jaxbContext = JAXBContext.newInstance(ConfermaPeriodica.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			confermaPeriodica = (ConfermaPeriodica) jaxbUnmarshaller.unmarshal(new StringReader(xmlConfermaPeriodica));
		} catch (JAXBException e) {
			_logger.error("Errore durante la costruzione dell'oggetto dall'xml");
		}
		return confermaPeriodica;
	}
}
