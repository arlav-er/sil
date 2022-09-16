package it.eng.sil.coop.webservices.blen;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.blen.input.statooccupazionale.StatoOccupazionale;
import it.eng.sil.coop.webservices.blen.output.statooccupazionale.Movimenti;
import it.eng.sil.coop.webservices.blen.output.statooccupazionale.Movimento;
import it.eng.sil.coop.webservices.blen.output.statooccupazionale.SINOCheck;
import it.eng.sil.coop.webservices.blen.output.statooccupazionale.StatoOccupazionale.Esito;
import it.eng.sil.util.DBAccess;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import it.eng.sil.util.xml.XMLValidator;

public class ControlloLavoratore {

	private static final String ESITO_OK = "101";
	private static final String ESITO_KO_NO_PRESENTE = "902";
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ControlloLavoratore.class.getName());
	public static final Pattern codiceFiscaleCheck = Pattern.compile(
			"([A-Z]{6}[0-9LMNPQRSTUV]{2}[ABCDEHLMPRST]{1}[0-9LMNPQRSTUV]{2}[A-Z]{1}[0-9LMNPQRSTUV]{3}[A-Z]{1})|([0-9]{11})");

	protected static final String XSD_PATH = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "blen" + File.separator;

	protected static final File inputXsd = new File(XSD_PATH + "inputXML_ControlloLavoratore.xsd");
	protected static final File outputXsd = new File(XSD_PATH + "outputXML_ControlloLavoratore.xsd");

	public String getControlloLavoratore(String inputXML) {

		JAXBContext jaxbContext;
		DataConnection dataConnection = null;
		String esito = "101";
		String descrizione = "Dato preso in carico dal sistema";
		String outputXml = "";
		StatoOccupazionale soInput = null;
		SourceBean statoOccupazionaleSB = null;
		Vector movimentiSB = null;
		String codiceFiscale = "";
		boolean cigs = false;
		boolean esperienzaEdile = false;
		boolean mobilita = false;

		///////////////////////////
		// VALIDAZIONE INPUT XML //
		///////////////////////////

		if (!isXmlValid(inputXML, inputXsd)) {
			_logger.error("Errore validazione input xml");
			esito = "913";
			descrizione = "Validazione xml: non superata(XSD)";
			outputXml = createXMLStatoOccupazionele(esito, descrizione, codiceFiscale, statoOccupazionaleSB, cigs,
					esperienzaEdile, mobilita, movimentiSB);
			return outputXml;
		}

		////////////////////////////////
		// WEB SERVICE BUSINESS LOGIC //
		////////////////////////////////

		try {
			jaxbContext = JAXBContext.newInstance(StatoOccupazionale.class);
			soInput = (StatoOccupazionale) jaxbContext.createUnmarshaller().unmarshal(new StringReader(inputXML));

			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String dataCalcolo = DateUtils.getNow();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			java.util.Date data1 = formatter.parse(dataCalcolo);
			String strData = DateUtils.format(data1);

			codiceFiscale = soInput.getCodiceFiscale();

			// Controllo validita codice fiscale
			try {
				XMLValidator.checkObjectFieldExists(codiceFiscale, "codicefiscale", true, codiceFiscaleCheck,
						"\"Codice fiscale del Datore di lavoro\"");
			} catch (MandatoryFieldException e1) {
				esito = "913";
				descrizione = "Validazione xml: non superata(XSD)";
			} catch (FieldFormatException e2) {
				esito = "913";
				descrizione = "Validazione xml: non superata(XSD)";
			}

			// Controllo Esistenza Codice Fiscale
			if (ESITO_OK.equals(esito)) {
				Object[] inputParameters = new Object[1];
				inputParameters[0] = codiceFiscale;

				SourceBean lavoratore = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE", inputParameters,
						"SELECT", Values.DB_SIL_DATI);
				if (!lavoratore.containsAttribute("ROW")) {
					esito = ESITO_KO_NO_PRESENTE;
					descrizione = "Parametri non riconosciuti";
				}
			}

			if (ESITO_OK.equals(esito)) {
				statoOccupazionaleSB = executeSelectSO("GET_STATO_OCCUPAZ_BLEN_WS", codiceFiscale, strData,
						dataConnection);

				if (!statoOccupazionaleSB.containsAttribute("ROW")) {
					esito = ESITO_KO_NO_PRESENTE;
					descrizione = "Parametri non riconosciuti";
				} else {
					BigDecimal cdnLavoratore = (BigDecimal) statoOccupazionaleSB.getAttribute("ROW.CDNLAVORATORE");
					movimentiSB = getMovimentiApertiDa(strData, cdnLavoratore, dataConnection);

					cigs = isCigs(strData, cdnLavoratore, dataConnection);
					esperienzaEdile = isEsperienzaEdile(strData, cdnLavoratore, dataConnection);
					mobilita = isMobilita(strData, cdnLavoratore, dataConnection);

					esito = ESITO_OK;
					descrizione = "OK";
				}
			}

		} catch (JAXBException e) {
			esito = "999";
			descrizione = "Errore generico";
			_logger.error("Errore nell'xml in input", e);
		} catch (Exception e) {
			esito = "999";
			descrizione = "Errore generico";
			_logger.error("Errore generico", e);
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, null, null);
		}

		outputXml = createXMLStatoOccupazionele(esito, descrizione, codiceFiscale, statoOccupazionaleSB, cigs,
				esperienzaEdile, mobilita, movimentiSB);

		////////////////////////////
		// VALIDAZIONE OUTPUT XML //
		////////////////////////////

		if (!isXmlValid(outputXml, outputXsd)) {
			_logger.error("Errore validazione output xml");
			esito = "999";
			descrizione = "Errore generico";
			outputXml = createXMLStatoOccupazionele(esito, descrizione, codiceFiscale, statoOccupazionaleSB, cigs,
					esperienzaEdile, mobilita, movimentiSB);
			return outputXml;
		}

		return outputXml;

	}

	private SourceBean executeSelectSO(String query_name, String codiceFiscale, String dataCalcolo, DataConnection dc)
			throws EMFUserError {
		Object[] inputParameters = new Object[41];
		for (int i = 0; i < inputParameters.length; i++) {
			inputParameters[i] = dataCalcolo;
		}
		inputParameters[35] = codiceFiscale;
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

	private boolean isMobilita(String strData, BigDecimal cdnLavoratore, DataConnection dataConnection)
			throws Exception {
		Object params[] = new Object[3];
		params[0] = strData;
		params[1] = strData;
		params[2] = cdnLavoratore;

		SourceBean res = (SourceBean) executeSelect("COUNT_MOBILITA_BLEN_WS", params, dataConnection);
		BigDecimal numMob = (BigDecimal) res.getAttribute("ROW.NUMMOB");

		return numMob.intValue() > 0;
	}

	private boolean isEsperienzaEdile(String strData, BigDecimal cdnLavoratore, DataConnection dataConnection)
			throws Exception {
		Object params[] = new Object[4];
		params[0] = cdnLavoratore;
		params[1] = strData;
		params[2] = strData;
		params[3] = strData;

		SourceBean res = (SourceBean) executeSelect("COUNT_ESPERIENZA_EDILE_BLEN_WS", params, dataConnection);
		BigDecimal num = (BigDecimal) res.getAttribute("ROW.NUM");

		return num.intValue() > 0;
	}

	private boolean isCigs(String strData, BigDecimal cdnLavoratore, DataConnection dataConnection) throws Exception {
		Object params[] = new Object[3];
		params[0] = strData;
		params[1] = strData;
		params[2] = cdnLavoratore;

		SourceBean res = (SourceBean) executeSelect("COUNT_CIGS_BLEN_WS", params, dataConnection);
		BigDecimal numCigs = (BigDecimal) res.getAttribute("ROW.NUMCIG");

		return numCigs.intValue() > 0;

	}

	/**
	 * Legge tutti i movimenti aperti di un lavoratore a partire da una certa data
	 */
	private Vector getMovimentiApertiDa(String data, Object cdnLavoratore, DataConnection dc) throws Exception {
		Object params[] = new Object[3];
		params[0] = cdnLavoratore;
		params[1] = data;
		params[2] = data;
		Vector movimenti = null;

		SourceBean res = (SourceBean) executeSelect("GET_MOVIMENTI_BLEN_WS", params, dc);
		if (res != null)
			movimenti = res.getAttributeAsVector("ROW");

		return movimenti;
	}

	private SourceBean executeSelect(String query_name, Object[] inputParameters, DataConnection dc)
			throws EMFUserError {

		SourceBean ret = null;

		if (dc != null) {
			DBAccess dbAcc = new DBAccess();

			try {
				ret = dbAcc.selectToSourceBean(query_name, inputParameters, dc);
			} catch (EMFInternalError e) {
				_logger.error("Errore nell'esecuzione della query: " + query_name);
				throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO, new Vector());
			}
		} else {
			ret = (SourceBean) QueryExecutor.executeQuery(query_name, inputParameters, "SELECT", Values.DB_SIL_DATI);
		}

		return ret;
	}

	private final String createXMLStatoOccupazionele(String codiceEsito, String descrizioneEsito, String cf,
			SourceBean statoOccupazSB, boolean cigs, boolean esperienzaEdile, boolean mobilita, Vector movimentiSB) {

		_logger.debug("createXMLStatoOccupazionele() - start - genero xml ");
		String returnString = "";

		String cognome = "";
		String nome = "";
		String codstatooccupazragg = "";

		String descStatoOccupazionale = "";

		if (statoOccupazSB != null) {

			cognome = StringUtils.getAttributeStrNotNull(statoOccupazSB, "ROW.STRCOGNOME");
			nome = StringUtils.getAttributeStrNotNull(statoOccupazSB, "ROW.STRNOME");
			codstatooccupazragg = StringUtils.getAttributeStrNotNull(statoOccupazSB, "ROW.codstatooccupazragg");

			descStatoOccupazionale = StringUtils.getAttributeStrNotNull(statoOccupazSB, "ROW.statoocc");

		}

		it.eng.sil.coop.webservices.blen.output.statooccupazionale.StatoOccupazionale soOutput = new it.eng.sil.coop.webservices.blen.output.statooccupazionale.StatoOccupazionale();
		Esito esito = new Esito();
		esito.setCodice(codiceEsito);
		esito.setDescrizione(descrizioneEsito);
		soOutput.setEsito(esito);

		soOutput.setCF(cf);

		// valori obbligatori in xsd
		soOutput.setCodStatoOccupazionale("ND");
		soOutput.setDescStatoOccupazionale("Lavoratore non presente nella banca dati SIL o non attivo");

		if (ESITO_OK.equals(codiceEsito)) {

			soOutput.setCognome(cognome);
			soOutput.setNome(nome);
			soOutput.setCodStatoOccupazionale(codstatooccupazragg);
			soOutput.setDescStatoOccupazionale(descStatoOccupazionale);
			soOutput.setCigs(cigs ? SINOCheck.SI : SINOCheck.NO);
			soOutput.setEsperienzaEdile(esperienzaEdile ? SINOCheck.SI : SINOCheck.NO);
			soOutput.setMobilita(mobilita ? SINOCheck.SI : SINOCheck.NO);
			Movimenti movimenti = new Movimenti();

			for (Object elem : movimentiSB) {
				SourceBean movimentoSB = (SourceBean) elem;
				Movimento movimento = new Movimento();
				movimento.setCcnl(StringUtils.getAttributeStrNotNull(movimentoSB, "codccnl"));
				movimento.setCodProfessione(StringUtils.getAttributeStrNotNull(movimentoSB, "codmansionedot"));
				movimento.setDataAssunzione(StringUtils.getAttributeStrNotNull(movimentoSB, "datInizioAvv"));
				movimento.setDataPresuntaFine(StringUtils.getAttributeStrNotNull(movimentoSB, "datFineMovEffettiva"));
				movimento.setSettore(StringUtils.getAttributeStrNotNull(movimentoSB, "codatecodot"));
				movimenti.getMovimento().add(movimento);
			}

			soOutput.setMovimenti(movimenti);
		}

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext
					.newInstance(it.eng.sil.coop.webservices.blen.output.statooccupazionale.StatoOccupazionale.class);
			StringWriter writer = new StringWriter();
			jaxbContext.createMarshaller().marshal(soOutput, writer);
			returnString = writer.toString();
		} catch (JAXBException e) {
			_logger.error("Errore nella creazione dell' xml da inviare", e);
		}

		return returnString;
	}

	protected boolean isXmlValid(String inputXML, File schemaFile) {

		String validityErrors = XMLValidator.getValidityErrors(inputXML, schemaFile);

		if (validityErrors != null) {
			_logger.error("Errori di validazione file XML:\n" + validityErrors);
			return false;
		}
		return true;

	}

}
