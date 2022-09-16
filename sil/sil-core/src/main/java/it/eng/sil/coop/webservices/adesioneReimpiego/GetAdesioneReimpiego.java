package it.eng.sil.coop.webservices.adesioneReimpiego;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.madreperla.servizi.UtilityXml;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import it.eng.sil.util.xml.XMLValidator;

public class GetAdesioneReimpiego {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetAdesioneReimpiego.class.getName());
	private final Integer CDN_UT_SIL_PORTALE = 150;

	public static final Pattern dataCheck = Pattern
			.compile("(19|20)\\d\\d([- /.])(0[1-9]|1[012])([- /.])(0[1-9]|[12][0-9]|3[01])");
	public static final Pattern codiceFiscaleCheck = Pattern
			.compile("([A-Z]{6}[0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z])|([0-9]{11})");

	String xmlReimpiego = "";
	BigDecimal cdnLavoratore = null;
	SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");

	File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd"
			+ File.separator + "Reimpiego" + File.separator + "InputXML_Reimpiego.xsd");

	File schemaFileOutput = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd"
			+ File.separator + "Reimpiego" + File.separator + "outputXML_Reimpiego.xsd");

	public String getAdesioneReimpiego(String inputXML) {

		String esito = "";
		TransactionQueryExecutor transExec = null;
		String descrizione = "";
		SourceBean infoLavSB = null;
		String dataCalcolo = "";
		String codFiscale = "";
		String dichiarazione = "";
		Document doc;

		try {
			doc = XMLValidator.parseXmlFile(inputXML);
			XPath xpath = XPathFactory.newInstance().newXPath();

			XPathExpression exprCF = xpath.compile("/Reimpiego/CodiceFiscale");
			Object cf = exprCF.evaluate(doc, XPathConstants.STRING);

			XPathExpression exprDataRif = xpath.compile("/Reimpiego/DataRiferimento");
			Object dataRif = exprDataRif.evaluate(doc, XPathConstants.STRING);

			XPathExpression exprDich = xpath.compile("/Reimpiego/Dichiarazione");
			Object dichiaraz = exprDich.evaluate(doc, XPathConstants.STRING);

			dichiarazione = dichiaraz.toString();
			codFiscale = cf.toString();
			dataCalcolo = dataRif.toString();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date data1 = formatter.parse(dataCalcolo);
			String strData = DateUtils.format(data1);

			xmlReimpiego = validaXML_Reimpiego(cf, dataRif, dichiarazione, inputXML);
			if ("".equals(xmlReimpiego)) {
				transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
				transExec.initTransaction();

				infoLavSB = executeSelectReimpiego("GET_STATO_OCCUPAZ_WS_COMP_AMM", codFiscale, strData, transExec);

				if (!infoLavSB.containsAttribute("ROW") && "NASPI".equals(dichiarazione)) {
					esito = "08";
					descrizione = "Dati Stato Occupazionale da inviare non trovati";
				} else {
					esito = "00";
					descrizione = "Adesione consentita";
				}
				xmlReimpiego = createXMLReimpiego(esito, descrizione, codFiscale, infoLavSB);

				String validityErrors = XMLValidator.getValidityErrors(xmlReimpiego, schemaFileOutput);

				if (validityErrors != null) {
					_logger.error("Errore nella validazione dell'xml da inviare " + validityErrors);
					_logger.error("xml output generato: " + xmlReimpiego);
					xmlReimpiego = createXMLRisposta("11", "Errore nella validazione dell'xml da inviare");
				} else if ("00".equals(esito)) {
					Object queryRichAdesione = transExec.executeQuery("INSERT_RICHIESTA_ADESIONE", new Object[] {
							codFiscale, cdnLavoratore, dichiarazione, strData, CDN_UT_SIL_PORTALE, CDN_UT_SIL_PORTALE },
							"INSERT");
					if (queryRichAdesione == null || !(queryRichAdesione instanceof Boolean
							&& ((Boolean) queryRichAdesione).booleanValue() == true)) {
						xmlReimpiego = createXMLRisposta("09", "Impossibile memorizzare richiesta adesione sul SIL");
					}

					transExec.commitTransaction();
				}
			}
		} catch (Exception e) {
			try {
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
				xmlReimpiego = createXMLRisposta("99", "Errore generico Servizio Adesione Reimpiego");
			} catch (Exception e1) {
				_logger.debug("Errore generico Servizio Adesione Reimpiego ", e1);
			}

			_logger.debug("Errore generico Servizio Adesione Reimpiego ", e);
		}
		return xmlReimpiego;
	}

	private final String validaXML_Reimpiego(Object cf, Object dataRif, String dichiarazione, String inputXML)
			throws Exception {
		String returnString = "";

		String dataCalcolo = dataRif.toString();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date data1 = formatter.parse(dataCalcolo);
		String strData = DateUtils.format(data1);

		try {
			XMLValidator.checkObjectFieldExists(cf, "codicefiscale", true, codiceFiscaleCheck, "\"Codice fiscale\"");
		} catch (MandatoryFieldException e1) {
			_logger.error("Codice fiscale non valido");
			returnString = createXMLRisposta("01", "Codice fiscale non valido");
			return returnString;
		} catch (FieldFormatException e2) {
			_logger.error("Codice fiscale non valido");
			returnString = createXMLRisposta("01", "Codice fiscale non valido");
			return returnString;
		}

		Object[] inputParameters = new Object[1];
		inputParameters[0] = cf.toString();

		SourceBean lavoratore = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE", inputParameters,
				"SELECT", Values.DB_SIL_DATI);
		if (!lavoratore.containsAttribute("ROW")) {
			_logger.error("Codice fiscale non trovato");
			returnString = createXMLRisposta("02", "Codice fiscale non trovato");
			return returnString;
		} else {
			cdnLavoratore = (BigDecimal) lavoratore.getAttribute("ROW.CDNLAVORATORE");
		}

		try {
			XMLValidator.checkObjectFieldExists(dataCalcolo, "dataRiferimento", true, dataCheck,
					"\"Data di Riferimento\"");
		} catch (MandatoryFieldException e1) {
			_logger.error("Data non valida");
			returnString = createXMLRisposta("03", "Data non valida");
			return returnString;
		} catch (FieldFormatException e2) {
			_logger.error("Data non valida");
			returnString = createXMLRisposta("03", "Data non valida");
			return returnString;
		}

		if (DateUtils.compare(strData, DateUtils.getNow()) > 0) {
			_logger.error("Data errata perché futura");
			returnString = createXMLRisposta("04", "Data errata perché futura");
			return returnString;
		}

		String validityErrors = XMLValidator.getValidityErrors(inputXML, schemaFile);
		if (validityErrors != null) {
			_logger.error("Errore nella validazione dell'xml pervenuto " + validityErrors);
			_logger.error("xml pervenuto: " + inputXML);
			returnString = createXMLRisposta("10", "Errore nella validazione dell'xml pervenuto");
			return returnString;
		}

		inputParameters = new Object[1];
		inputParameters[0] = cdnLavoratore;

		if ("NASPI".equals(dichiarazione)) {
			SourceBean didAttiva = (SourceBean) QueryExecutor.executeQuery("GET_DID_LAVORATORE_CLOSEDID",
					inputParameters, "SELECT", Values.DB_SIL_DATI);
			if (!didAttiva.containsAttribute("ROW")) {
				_logger.error("Lavoratore non possiede una DID attiva protocollata");
				returnString = createXMLRisposta("05", "Lavoratore non possiede una DID attiva protocollata");
				return returnString;
			} else {
				/*
				 * inputParameters = new Object[2]; inputParameters[0] = cdnLavoratore; inputParameters[1] = strData;
				 * SourceBean disoccSB = (SourceBean)QueryExecutor.executeQuery("CHECK_PRIVO_DI_LAVORO_WS_REIMPIEGO",
				 * inputParameters, "SELECT", Values.DB_SIL_DATI); if (!disoccSB.containsAttribute("ROW")) {
				 * _logger.error("Lavoratore non privo di lavoro"); returnString = createXMLRisposta("06",
				 * "Lavoratore non privo di lavoro"); return returnString; } else if ("NASPI".equals(dichiarazione)) {
				 * boolean flagReqNaspi = true; String dataDidAttiva =
				 * (String)didAttiva.getAttribute("ROW.DATDICHIARAZIONE"); inputParameters = new Object[2];
				 * inputParameters[0] = cdnLavoratore; inputParameters[1] = dataDidAttiva;
				 */
				boolean flagReqNaspi = false;
				String dataDidAttiva = (String) didAttiva.getAttribute("ROW.DATDICHIARAZIONE");
				inputParameters = new Object[3];
				inputParameters[0] = strData;
				inputParameters[1] = cdnLavoratore;
				inputParameters[2] = dataDidAttiva;
				SourceBean cessazSB = (SourceBean) QueryExecutor.executeQuery("CHECK_CESSAZIONE_PER_NASPI_WS_REIMPIEGO",
						inputParameters, "SELECT", Values.DB_SIL_DATI);
				if (cessazSB.containsAttribute("ROW")) {
					String codMvCessazione = StringUtils.getAttributeStrNotNull(cessazSB, "ROW.CODMVCESSAZIONE");
					if (codMvCessazione.equals("LC")) {
						BigDecimal mesiRapporto = (BigDecimal) cessazSB.getAttribute("ROW.mesi_rapporto");
						BigDecimal mesiPrimaDataAdesione = (BigDecimal) cessazSB
								.getAttribute("ROW.mesi_antecedenti_data_adesione");
						int mesiMax = 24;
						if (mesiRapporto.intValue() < 48)
							mesiMax = Math.round(mesiRapporto.intValue() / 2);
						if (mesiPrimaDataAdesione.intValue() <= mesiMax)
							flagReqNaspi = true;
					}
				}
				if (!flagReqNaspi) {
					_logger.error("Requisiti mancanti per Lavoratore in NASPI");
					returnString = createXMLRisposta("07", "Requisiti mancanti per Lavoratore in NASPI");
					return returnString;
				}
			}
		} else if ("CIGS".equals(dichiarazione)) {
			SourceBean checkStatoOccupazSB = (SourceBean) QueryExecutor.executeQuery(
					"CHECK_STATO_OCCUPAZ_PER_CIGS_WS_REIMPIEGO", inputParameters, "SELECT", Values.DB_SIL_DATI);
			int checkStatoOccupaz = SourceBeanUtils.getAttrInt(checkStatoOccupazSB, "ROW.checkStatoOccupaz", 0);
			if (checkStatoOccupaz != 1) {
				_logger.error("Requisiti mancanti per Lavoratore in CIGS");
				returnString = createXMLRisposta("12", "Requisiti mancanti per Lavoratore in CIGS");
				return returnString;
			}
		}

		return returnString;
	}

	public static String createXMLRisposta(String codice, String descrizione) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		String returnString = "";
		parser = factory.newDocumentBuilder();
		Document doc = parser.newDocument();
		Element SOElem = doc.createElement("Risposta");
		SOElem.setAttribute("schemaVersion", "1");
		SOElem.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		doc.appendChild(SOElem);

		Element esito = doc.createElement("Esito");
		SOElem.appendChild(esito);

		UtilityXml.appendTextChild("codice", codice, esito, doc);
		UtilityXml.appendTextChild("descrizione", descrizione, esito, doc);

		returnString = UtilityXml.domToString(doc);

		return returnString;
	}

	private SourceBean executeSelectReimpiego(String query_name, String codiceFiscale, String dataCalcolo,
			TransactionQueryExecutor tExec) throws EMFUserError {
		Object[] inputParameters = new Object[44];
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
		inputParameters[35] = dataCalcolo;
		inputParameters[36] = System.getProperty("_ENCRYPTER_KEY_");
		inputParameters[37] = dataCalcolo;
		inputParameters[38] = codiceFiscale;
		inputParameters[39] = dataCalcolo;
		inputParameters[40] = dataCalcolo;
		inputParameters[41] = dataCalcolo;
		inputParameters[42] = dataCalcolo;
		inputParameters[43] = dataCalcolo;

		SourceBean ret = null;

		try {
			ret = (SourceBean) tExec.executeQuery(query_name, inputParameters, "SELECT");
		} catch (EMFInternalError e) {
			_logger.error("Errore nell'esecuzione della query: " + query_name);
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO, new Vector());
		}

		return ret;
	}

	private final String createXMLReimpiego(String codice, String descrizione, String cf, SourceBean infoLav) {

		_logger.debug("buildXml() - start - genero xml ");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		String returnString = "";

		String cognome = "";
		String nome = "";
		String datNasc = "";
		String codstatooccupazragg = "";
		String descrStatoOcc = "";
		String statoOcc = "";
		String mesi_anz = "";
		BigDecimal numMesiSosp = null;
		BigDecimal numMesiSospPrec = null;
		BigDecimal numMesiAnz = null;
		BigDecimal giorniAnz = null;
		BigDecimal numMesiRischioDisocc = null;
		BigDecimal numMesiAnzPrec = null;
		BigDecimal numMesiSospFornero = null;
		String numMesiSospInt = null;
		String numMesiSospPrecInt = null;
		String numMesiAnzInt = null;
		String numGiorniAnzInt = null;
		String numMesiRischioDisoccInt = null;
		String numMesiAnzPrecInt = null;
		String numMesiSospForneroInt = null;
		String dataDichIM = "";
		String codcpi = "";
		String strCodCpi = "";
		Object nMesiSospForneroCompleto = null;
		BigDecimal numGGRestantiSospFornero = null;
		Object mesiRischioDisoccCompleto = null;
		BigDecimal numGGRestantiRischioDisocc = null;
		BigDecimal ggSospForneroRischioDisocc = null;

		if (infoLav.containsAttribute("ROW")) {
			cognome = StringUtils.getAttributeStrNotNull(infoLav, "ROW.STRCOGNOME");
			nome = StringUtils.getAttributeStrNotNull(infoLav, "ROW.STRNOME");
			datNasc = StringUtils.getAttributeStrNotNull(infoLav, "ROW.datNasc");
			codstatooccupazragg = StringUtils.getAttributeStrNotNull(infoLav, "ROW.codstatooccupazragg");
			descrStatoOcc = StringUtils.getAttributeStrNotNull(infoLav, "ROW.strStatoOcc");
			statoOcc = StringUtils.getAttributeStrNotNull(infoLav, "ROW.statoOcc");
			numMesiSosp = (BigDecimal) infoLav.getAttribute("ROW.NUMMESISOSP");
			numMesiSospPrec = (BigDecimal) infoLav.getAttribute("ROW.NUMMESISOSPPREC");
			numMesiAnz = (BigDecimal) infoLav.getAttribute("ROW.MESI_ANZ");
			giorniAnz = (BigDecimal) infoLav.getAttribute("ROW.GIORNI_ANZ");
			numMesiRischioDisocc = (BigDecimal) infoLav.getAttribute("ROW.mesi_rischio_disocc");
			numMesiAnzPrec = (BigDecimal) infoLav.getAttribute("ROW.mesi_anz_prec");
			numMesiSospFornero = (BigDecimal) infoLav.getAttribute("ROW.mesiSospFornero2014");
			// calcolo mesi anzianità
			if (numMesiAnz != null) {
				numMesiAnzInt = String.valueOf(numMesiAnz.intValue());
			} else {
				numMesiAnzInt = "0";
			}
			if (giorniAnz != null) {
				numGiorniAnzInt = String.valueOf(giorniAnz.intValue());
			} else {
				numGiorniAnzInt = "0";
			}
			if (numMesiAnzPrec != null) {
				numMesiAnzPrecInt = String.valueOf(numMesiAnzPrec.intValue());
			} else {
				numMesiAnzPrecInt = "0";
			}
			if (numMesiSospPrec != null) {
				numMesiSospPrecInt = String.valueOf(numMesiSospPrec.intValue());
			} else {
				numMesiSospPrecInt = "0";
			}
			if (numMesiSosp != null) {
				numMesiSospInt = String.valueOf(numMesiSosp.intValue());
			} else {
				numMesiSospInt = "0";
			}
			if (numMesiSospFornero != null) {
				numMesiSospForneroInt = String.valueOf(numMesiSospFornero.intValue());
			} else {
				numMesiSospForneroInt = "0";
			}
			if (numMesiRischioDisocc != null) {
				numMesiRischioDisoccInt = String.valueOf(numMesiRischioDisocc.intValue());
			} else {
				numMesiRischioDisoccInt = "0";
			}

			nMesiSospForneroCompleto = infoLav.getAttribute("mesiSospFornero2014_completo");
			if (nMesiSospForneroCompleto != null && !nMesiSospForneroCompleto.equals("")) {
				String[] sospFornero = nMesiSospForneroCompleto.toString().split("-");
				if (sospFornero.length == 4) {
					numGGRestantiSospFornero = new BigDecimal(sospFornero[3]);
				} else {
					numGGRestantiSospFornero = new BigDecimal(0);
				}
			} else {
				numGGRestantiSospFornero = new BigDecimal(0);
			}

			mesiRischioDisoccCompleto = infoLav.getAttribute("mesi_rischio_disocc_completo");
			if (mesiRischioDisoccCompleto != null && !mesiRischioDisoccCompleto.equals("")) {
				String[] rischioDisocc = mesiRischioDisoccCompleto.toString().split("-");
				if (rischioDisocc.length == 2) {
					numGGRestantiRischioDisocc = new BigDecimal(rischioDisocc[1]);
				} else {
					numGGRestantiRischioDisocc = new BigDecimal(0);
				}
			} else {
				numGGRestantiRischioDisocc = new BigDecimal(0);
			}
			ggSospForneroRischioDisocc = numGGRestantiSospFornero.add(numGGRestantiRischioDisocc);
			int mesiAggiuntivi = (ggSospForneroRischioDisocc.intValue()) / 30;

			if (numMesiSospInt != null && Integer.parseInt(numMesiSospInt) < 0)
				numMesiSospInt = "0";
			if (numMesiSospPrecInt != null && Integer.parseInt(numMesiSospPrecInt) < 0)
				numMesiSospPrecInt = "0";
			if (numMesiAnzInt != null && Integer.parseInt(numMesiAnzInt) < 0)
				numMesiAnzInt = "0";
			if (numGiorniAnzInt != null && Integer.parseInt(numGiorniAnzInt) < 0)
				numGiorniAnzInt = "0";
			if (numMesiAnzPrecInt != null && Integer.parseInt(numMesiAnzPrecInt) < 0)
				numMesiAnzPrecInt = "0";
			if (numMesiSospForneroInt != null && Integer.parseInt(numMesiSospForneroInt) < 0)
				numMesiSospForneroInt = "0";
			if (numMesiRischioDisoccInt != null && Integer.parseInt(numMesiRischioDisoccInt) < 0)
				numMesiRischioDisoccInt = "0";

			mesi_anz = String.valueOf(Integer.parseInt(numMesiAnzPrecInt) + Integer.parseInt(numMesiAnzInt)
					- (Integer.parseInt(numMesiSospPrecInt) + Integer.parseInt(numMesiSospInt)
							+ Integer.parseInt(numMesiSospForneroInt) + Integer.parseInt(numMesiRischioDisoccInt)
							+ mesiAggiuntivi));

			int meseDiffAnzianitaGiorni = 0;
			int numGGAnzResidui = Integer.parseInt(numGiorniAnzInt);
			if (numGGAnzResidui >= (ggSospForneroRischioDisocc.intValue() % 30)) {
				numGGAnzResidui = numGGAnzResidui - (ggSospForneroRischioDisocc.intValue() % 30);
			} else {
				if ((ggSospForneroRischioDisocc.intValue() % 30) > 0) {
					numGGAnzResidui = numGGAnzResidui + (30 - (ggSospForneroRischioDisocc.intValue() % 30));
					meseDiffAnzianitaGiorni = 1;
				}
			}

			if (mesi_anz != null && !mesi_anz.equals("") && Integer.parseInt(mesi_anz) > 0
					&& meseDiffAnzianitaGiorni > 0) {
				mesi_anz = String.valueOf(Integer.parseInt(mesi_anz) - meseDiffAnzianitaGiorni);
			}

			dataDichIM = StringUtils.getAttributeStrNotNull(infoLav, "ROW.dataDichIM");
			codcpi = StringUtils.getAttributeStrNotNull(infoLav, "ROW.codcpi");
			strCodCpi = StringUtils.getAttributeStrNotNull(infoLav, "ROW.strDescrizione");
		}

		try {
			parser = factory.newDocumentBuilder();
			// Create blank DOM Document
			Document doc = parser.newDocument();
			// Insert the root element node
			Element SOElem = doc.createElement("Risposta");
			SOElem.setAttribute("schemaVersion", "1");
			SOElem.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			doc.appendChild(SOElem);

			Element esito = doc.createElement("Esito");
			SOElem.appendChild(esito);

			UtilityXml.appendTextChild("codice", codice, esito, doc);
			UtilityXml.appendTextChild("descrizione", descrizione, esito, doc);

			if (infoLav.containsAttribute("ROW")) {
				Element so = doc.createElement("datiStatoOccupazionale");
				SOElem.appendChild(so);

				UtilityXml.appendTextChild("CodiceFiscale", cf, so, doc);
				UtilityXml.appendTextChild("Cognome", cognome, so, doc);
				UtilityXml.appendTextChild("Nome", nome, so, doc);
				UtilityXml.appendTextChild("DataNascita", datNasc, so, doc);

				Element stato = doc.createElement("StatoOccupazionale");
				so.appendChild(stato);

				UtilityXml.appendTextChild("CodiceSO", codstatooccupazragg, stato, doc);
				UtilityXml.appendTextChild("DescrizioneSO", statoOcc, stato, doc);
				UtilityXml.appendTextChild("MesiAnzianita", mesi_anz, stato, doc);
				UtilityXml.appendNullableTextChild("DataDid", dataDichIM, stato, doc);

				Element cpi = doc.createElement("DatiCPI");
				so.appendChild(cpi);

				UtilityXml.appendTextChild("CodiceCPI", codcpi, cpi, doc);
				UtilityXml.appendTextChild("DescrCPI", strCodCpi, cpi, doc);
			}

			try {
				returnString = UtilityXml.domToString(doc);
			} catch (TransformerException e) {
				_logger.error("Errore nella trasformazione del xml da inviare", e);
			}
			_logger.debug("buildXml() - end");

		} catch (ParserConfigurationException e) {
			_logger.error("Errore nel parsing dell'xml da inviare", e);
		}
		return returnString;
	}
}
