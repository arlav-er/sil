package it.eng.sil.coop.webservices.apapi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.madreperla.servizi.UtilityXml;
import it.eng.sil.util.DBAccess;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import it.eng.sil.util.xml.XMLValidator;

public class StatoOccupazionaleAPAPI {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StatoOccupazionaleAPAPI.class.getName());

	private static final Pattern codiceFiscaleCheck = Pattern
			.compile("([A-Z]{6}[0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z])|([0-9]{11})");

	private File inputXML_getStatoOccupazionaleAPAPI_SchemaFile = new File(
			ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd" + File.separator
					+ "apapi" + File.separator + "inputXML_getStatoOccupazionaleAPAPI.xsd");

	private File outputXML_getStatoOccupazionaleAPAPI_SchemaFile = new File(
			ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd" + File.separator
					+ "apapi" + File.separator + "outputXML_getStatoOccupazionaleAPAPI.xsd");

	public String getStatoOccupazionaleAPAPI(String inputXML) {

		String outputXml = "";
		String esito = "";
		DataConnection dataConnection = null;
		StoredProcedureCommand command = null;
		DataResult dataResult = null;
		String descrizione = "";

		SourceBean infoLavSB = null;
		String codiceFiscale = "";

		try {

			///////////////////////////
			// VALIDAZIONE INPUT XML //
			///////////////////////////

			Node codiceFiscaleNode = null;
			Date dataDiInterrogazione = new Date();

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(false);
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder
					.parse(new InputSource(new ByteArrayInputStream(inputXML.getBytes("utf-8"))));
			Element root = document.getDocumentElement();

			// codice fiscale e data di interrogazione

			XPath xpath = XPathFactory.newInstance().newXPath();
			codiceFiscaleNode = (Node) xpath.evaluate("/StatoOccupazionale/CodiceFiscale", root, XPathConstants.NODE);

			codiceFiscale = codiceFiscaleNode.getTextContent();

			// controlla presenza errori dati input

			outputXml = getErroriDatiInput(inputXML, codiceFiscale, dataDiInterrogazione);

			if (!"".equalsIgnoreCase(outputXml)) {
				// è già presente una risposta,
				// riscontrati errore in fase di validazione
				return outputXml;
			}

			boolean inputXmlIsValid = XmlUtils.isXmlValid(inputXML, inputXML_getStatoOccupazionaleAPAPI_SchemaFile);
			if (!inputXmlIsValid) {
				_logger.debug("input xml non valido");
				outputXml = XmlUtils.createXMLRisposta("99", "Errore generico");
				return outputXml;
			}

			///////////////////////
			// GESTIONE CHIAMATA //
			///////////////////////

			_logger.info("Il servizio di SO lavoratore e' stato chiamato");

			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			infoLavSB = executeSelectSO("GET_STATO_OCCUPAZ_WS", codiceFiscale, dataDiInterrogazione, dataConnection);

			if (!infoLavSB.containsAttribute("ROW")) {
				esito = "99";
				descrizione = "errore generico";
			} else {
				esito = "00";
				descrizione = "OK";
			}

			outputXml = createXMLStatoOccupazionele(esito, descrizione, codiceFiscale, infoLavSB);

			////////////////////////
			// VALIDAZIONE OUTPUT //
			////////////////////////

			boolean outputXmlIsValid = XmlUtils.isXmlValid(outputXml, outputXML_getStatoOccupazionaleAPAPI_SchemaFile);
			if (!outputXmlIsValid) {
				_logger.debug("output xml non valido");
				outputXml = XmlUtils.createXMLRisposta("99", "Errore generico");
			}

		} catch (Exception e) {

			try {
				outputXml = XmlUtils.createXMLRisposta("99", "Errore generico");
			} catch (Exception eg) {
				_logger.debug("Errore in fase di generazione di una risposta con errore ", eg);
			}

			_logger.debug("Errore ", e);

		} finally {
			Utils.releaseResources(dataConnection, command, dataResult);
		}

		return outputXml;

	}

	private SourceBean executeSelectSO(String query_name, String codiceFiscale, Date dataDiInterrogazione,
			DataConnection dc) throws EMFUserError {

		SourceBean ret = null;

		String dataCalcolo = DateUtils.format(dataDiInterrogazione);

		Object[] inputParameters = new Object[41];
		inputParameters[0] = dataCalcolo;
		inputParameters[1] = dataCalcolo;
		inputParameters[2] = dataCalcolo;
		inputParameters[3] = dataCalcolo;
		inputParameters[4] = dataCalcolo;
		inputParameters[5] = dataCalcolo;
		inputParameters[6] = dataCalcolo;
		inputParameters[7] = dataCalcolo;
		inputParameters[8] = dataCalcolo;
		inputParameters[9] = dataCalcolo;
		inputParameters[10] = dataCalcolo;
		inputParameters[11] = dataCalcolo;
		inputParameters[12] = dataCalcolo;
		inputParameters[13] = dataCalcolo;
		inputParameters[14] = dataCalcolo;
		inputParameters[15] = dataCalcolo;
		inputParameters[16] = dataCalcolo;
		inputParameters[17] = dataCalcolo;
		inputParameters[18] = dataCalcolo;
		inputParameters[19] = dataCalcolo;
		inputParameters[20] = dataCalcolo;
		inputParameters[21] = dataCalcolo;
		inputParameters[22] = dataCalcolo;
		inputParameters[23] = dataCalcolo;
		inputParameters[24] = dataCalcolo;
		inputParameters[25] = dataCalcolo;
		inputParameters[26] = dataCalcolo;
		inputParameters[27] = dataCalcolo;
		inputParameters[28] = dataCalcolo;
		inputParameters[29] = dataCalcolo;
		inputParameters[30] = dataCalcolo;
		inputParameters[31] = dataCalcolo;
		inputParameters[32] = dataCalcolo;
		inputParameters[33] = dataCalcolo;
		inputParameters[34] = dataCalcolo;
		inputParameters[35] = codiceFiscale;
		inputParameters[36] = dataCalcolo;
		inputParameters[37] = dataCalcolo;
		inputParameters[38] = dataCalcolo;
		inputParameters[39] = dataCalcolo;
		inputParameters[40] = dataCalcolo;

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

	private final String createXMLStatoOccupazionele(String codice, String descrizione, String cf, SourceBean infoLav) {

		_logger.debug("buildXml() - begin");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		String returnString = "";

		String codstatooccupazragg = "";
		String statoOcc = "";
		String dataDichIM = "";
		String codcpi = "";
		String strCodCpi = "";

		if (infoLav != null) {

			codstatooccupazragg = StringUtils.getAttributeStrNotNull(infoLav, "ROW.codstatooccupazragg");
			statoOcc = StringUtils.getAttributeStrNotNull(infoLav, "ROW.statoOcc");
			dataDichIM = StringUtils.getAttributeStrNotNull(infoLav, "ROW.dataDichIM");
			codcpi = StringUtils.getAttributeStrNotNull(infoLav, "ROW.codcpi");
			strCodCpi = StringUtils.getAttributeStrNotNull(infoLav, "ROW.strDescrizione");

		}

		try {

			documentBuilder = factory.newDocumentBuilder();
			Document doc = documentBuilder.newDocument();
			Element SOElem = doc.createElement("Risposta");
			SOElem.setAttribute("schemaVersion", "1");
			SOElem.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			doc.appendChild(SOElem);

			Element esito = doc.createElement("Esito");
			UtilityXml.appendTextChild("codice", codice, esito, doc);
			UtilityXml.appendTextChild("descrizione", descrizione, esito, doc);
			SOElem.appendChild(esito);

			Element so = doc.createElement("datiStatoOccupazionaleAPAPI");

			Element statoOccupazionale = doc.createElement("StatoOccupazionale");
			UtilityXml.appendTextChild("CodiceSO", codstatooccupazragg, statoOccupazionale, doc);
			UtilityXml.appendTextChild("DescrizioneSO", statoOcc, statoOccupazionale, doc);
			so.appendChild(statoOccupazionale);

			Element datiDid = doc.createElement("DatiDID");
			UtilityXml.appendTextChild("CodiceCPI", codcpi, datiDid, doc);
			UtilityXml.appendTextChild("DescrCPI", strCodCpi, datiDid, doc);
			UtilityXml.appendNullableTextChild("DataDID", dataDichIM, datiDid, doc);
			so.appendChild(datiDid);

			SOElem.appendChild(so);

			try {
				returnString = UtilityXml.domToString(doc);
			} catch (TransformerException e) {
				_logger.error("Errore nella trasformazione del xml da inviare", e);
			}

		} catch (ParserConfigurationException e) {
			_logger.error("Errore nel parsing dell'xml da inviare", e);
		}

		_logger.debug("buildXml() - end");

		return returnString;

	}

	private final String getErroriDatiInput(String inputXML, String codiceFiscale, Date dataRif) throws Exception {

		String returnString = "";

		Object[] inputParameters = null;
		BigDecimal cdnLavoratore = null;
		String strData = DateUtils.format(dataRif);

		// controlla cf

		try {
			XMLValidator.checkObjectFieldExists(codiceFiscale, "codicefiscale", true, codiceFiscaleCheck,
					"\"Codice fiscale del Datore di lavoro\"");
		} catch (MandatoryFieldException e1) {
			returnString = XmlUtils.createXMLRisposta("04", "Codice fiscale non valido");
			return returnString;
		} catch (FieldFormatException e2) {
			returnString = XmlUtils.createXMLRisposta("04", "Codice fiscale non valido");
			return returnString;
		}

		// ricerca lavoratore

		inputParameters = new Object[1];
		inputParameters[0] = codiceFiscale;
		SourceBean lavoratore = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE", inputParameters,
				"SELECT", Values.DB_SIL_DATI);
		if (!lavoratore.containsAttribute("ROW")) {
			returnString = XmlUtils.createXMLRisposta("03", "Codice fiscale non trovato");
			return returnString;
		} else {
			cdnLavoratore = (BigDecimal) lavoratore.getAttribute("ROW.CDNLAVORATORE");
		}

		// ricerca cpi ver verifica competenza lavoratore

		inputParameters = new Object[3];
		inputParameters[0] = cdnLavoratore;
		inputParameters[1] = strData;
		inputParameters[2] = strData;

		SourceBean cpiLav = (SourceBean) QueryExecutor.executeQuery("GET_CPI_AN_LAVORATORE_COMPETENTE_DATA",
				inputParameters, "SELECT", Values.DB_SIL_DATI);
		String codCpiLav = (String) cpiLav.getAttribute("ROW.CPICOMP");
		String codTipoCpi = (String) cpiLav.getAttribute("ROW.codmonotipocpi");

		if (codCpiLav == null) {
			returnString = XmlUtils.createXMLRisposta("99", "Errore generico");
			return returnString;
		} else {
			if (codTipoCpi != null && !"C".equalsIgnoreCase(codTipoCpi)) {
				returnString = XmlUtils.createXMLRisposta("07", "Lavoratore non competente");
				return returnString;
			}
		}

		return returnString;

	}

}
