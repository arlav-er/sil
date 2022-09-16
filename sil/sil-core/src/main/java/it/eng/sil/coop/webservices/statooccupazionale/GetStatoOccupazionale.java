package it.eng.sil.coop.webservices.statooccupazionale;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.AttachmentPart;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
import it.eng.sil.bean.Documento;
import it.eng.sil.coop.webservices.madreperla.servizi.UtilityXml;
import it.eng.sil.util.DBAccess;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import it.eng.sil.util.xml.XMLValidator;

public class GetStatoOccupazionale {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetStatoOccupazionale.class.getName());

	private Documento doc = new Documento();
	private File tempFile;
	private static final String FORMATO_DATA = "dd/MM/yyyy";
	private static SimpleDateFormat df = new SimpleDateFormat(FORMATO_DATA);

	public static final Pattern dataCheck = Pattern
			.compile("(19|20)\\d\\d([- /.])(0[1-9]|1[012])([- /.])(0[1-9]|[12][0-9]|3[01])");
	public static final Pattern codiceFiscaleCheck = Pattern
			.compile("([A-Z]{6}[0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z])|([0-9]{11})");
	public static final int numMaxTentativiProtocollazione = 3;
	String xmlSO = "";

	SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");

	private static final String serviceName = "";

	File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd"
			+ File.separator + "statooccupazionale" + File.separator + "inputXML_statoOccupazionale.xsd");

	File schemaFileOutput = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd"
			+ File.separator + "statooccupazionale" + File.separator + "outputXML_stato_occ.xsd");

	public String getStatoOccupazionale(String inputXML) {

		String esito = "";
		DataConnection dataConnection = null;
		StoredProcedureCommand command = null;
		DataResult dataResult = null;
		String descrizione = "";
		SourceBean infoLavSB = null;
		String dataCalcolo = "";
		String codFiscale = "";
		String compAmm = "";
		Document doc;

		try {
			doc = XMLValidator.parseXmlFile(inputXML);
			XPath xpath = XPathFactory.newInstance().newXPath();
			// recupero valori
			XPathExpression exprCF = xpath.compile("/StatoOccupazionale/CodiceFiscale");
			Object cf = exprCF.evaluate(doc, XPathConstants.STRING);
			XPathExpression exprDataRif = xpath.compile("/StatoOccupazionale/DataRiferimento");
			Object dataRif = exprDataRif.evaluate(doc, XPathConstants.STRING);
			XPathExpression exprIdProv = xpath.compile("/StatoOccupazionale/IdProvincia");
			Object idProv = exprIdProv.evaluate(doc, XPathConstants.STRING);
			// recupero attributo per verificare la competenza amministrativa del lavoratore
			Element statoOcc = doc.getDocumentElement();
			if (statoOcc != null) {
				compAmm = statoOcc.getAttribute("CompAmm");
			}

			codFiscale = cf.toString();
			dataCalcolo = dataRif.toString();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date data1 = formatter.parse(dataCalcolo);
			String strData = DateUtils.format(data1);

			xmlSO = validaXMLSO(cf, dataRif, compAmm, inputXML);

			if ("".equals(xmlSO)) {
				_logger.info("Il servizio di SO lavoratore e' stato chiamato");
				DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
				dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
				dataConnection.initTransaction();

				infoLavSB = executeSelectSO("GET_STATO_OCCUPAZ_WS_COMP_AMM", codFiscale, strData, dataConnection);

				if (!infoLavSB.containsAttribute("ROW")) {
					esito = "99";
					descrizione = "dati non trovati";
				} else {
					esito = "00";
					descrizione = "OK";
				}
				xmlSO = createXMLStatoOccupazionele(esito, descrizione, codFiscale, infoLavSB);

				String validityErrors = XMLValidator.getValidityErrors(xmlSO, schemaFileOutput);

				if (validityErrors != null) {
					_logger.error("Errore nella validazione dell'xml da inviare" + validityErrors);
					_logger.error("xml output generato: " + xmlSO);
					xmlSO = createXMLRisposta("99", "Errore generico");
				}

				dataConnection.commitTransaction();
			}
		} catch (Exception e) {
			try {
				dataConnection.rollBackTransaction();
			} catch (EMFInternalError e1) {
				_logger.debug("Errore info Lavoratore ", e1);
			}

			_logger.debug("Errore info Lavoratore ", e);
		} finally {
			Utils.releaseResources(dataConnection, command, dataResult);
		}
		return xmlSO;
	}

	public String getStampaStatoOccupazionale(String inputXML)
			throws InputValidationException, GetStatoOccupazionaleException {
		DataHandler dh = null;
		Document doc;
		String protocollo = "OFF";
		String codFiscale = "";
		String dataCalcolo = "";
		String prov = "";
		String stampa = "";
		String compAmm = "";

		try {
			doc = XMLValidator.parseXmlFile(inputXML);
			XPath xpath = XPathFactory.newInstance().newXPath();
			// recupero valori
			XPathExpression exprCF = xpath.compile("/StatoOccupazionale/CodiceFiscale");
			Object cf = exprCF.evaluate(doc, XPathConstants.STRING);
			XPathExpression exprDataRif = xpath.compile("/StatoOccupazionale/DataRiferimento");
			Object dataRif = exprDataRif.evaluate(doc, XPathConstants.STRING);
			XPathExpression exprIdProv = xpath.compile("/StatoOccupazionale/IdProvincia");
			Object idProv = exprIdProv.evaluate(doc, XPathConstants.STRING);

			XPathExpression exprStampa = xpath.compile("/StatoOccupazionale/Stampa/ModelloStampa");
			Object stampaSO = exprStampa.evaluate(doc, XPathConstants.STRING);
			XPathExpression exprProtocollo = xpath.compile("/StatoOccupazionale/Stampa/Protocollazione");
			Object prot = exprProtocollo.evaluate(doc, XPathConstants.STRING);
			// recupero attributo per verificare la competenza amministrativa del lavoratore
			Element statoOcc = doc.getDocumentElement();
			if (statoOcc != null) {
				compAmm = statoOcc.getAttribute("CompAmm");
			}

			codFiscale = cf.toString();
			prov = idProv.toString();
			stampa = stampaSO.toString();
			xmlSO = validaXMLSO(cf, dataRif, compAmm, inputXML);

			if ((stampaSO == null || "".equals(stampaSO) || prot == null || "".equals(prot))) {
				xmlSO = createXMLRisposta("09", "Errore parametri stampa");
				return xmlSO;
			}

			if ("".equals(xmlSO)) {
				protocollo = prot.toString();
				if (protocollo.equalsIgnoreCase("ON")) {
					int iTentativo = 0;
					do {
						iTentativo = iTentativo + 1;
						dh = getStampaSO(codFiscale, prov, stampa);
					} while (dh == null && iTentativo < numMaxTentativiProtocollazione);
				} else {
					dh = getStampaSO(codFiscale, prov, stampa);
				}
				if (dh != null) {
					MessageContext msgContext = MessageContext.getCurrentContext();
					Message rspmsg = msgContext.getResponseMessage();
					_logger.debug("org.apache.axis.attachments.Attachments.SEND_TYPE_MIME : "
							+ org.apache.axis.attachments.Attachments.SEND_TYPE_MIME);
					int inputAttachmentType = rspmsg.getAttachmentsImpl().getSendType();
					rspmsg.getAttachmentsImpl().setSendType(inputAttachmentType);
					AttachmentPart attachPart = rspmsg.createAttachmentPart(dh);
					rspmsg.addAttachmentPart(attachPart);
					xmlSO = createXMLRisposta("00", "OK");
				} else {
					xmlSO = createXMLRisposta("08", "Errore di protocollazione");
					return xmlSO;
				}
			}
			return xmlSO;
		} catch (Exception e) {
			_logger.debug("Errore stampa so Lavoratore ", e);
		}
		return xmlSO;
	}

	public DataHandler getStampaSO(String codiceFisc, String provincia, String stampa) {

		// perhaps fixes #2965
		doc = new Documento();

		DataHandler dh = null;
		String currentDate = DateUtils.getNow();
		Object[] inputParameters = new Object[1];
		inputParameters[0] = codiceFisc;

		SourceBean lavoratore = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE", inputParameters,
				"SELECT", Values.DB_SIL_DATI);
		if (lavoratore != null) {
			BigDecimal cdnLavoratore = (BigDecimal) lavoratore.getAttribute("ROW.CDNLAVORATORE");

			Object[] inputParametersCpi = new Object[1];
			inputParametersCpi[0] = cdnLavoratore;
			SourceBean cpiLav = (SourceBean) QueryExecutor.executeQuery("GET_CPI_AN_LAVORATORE", inputParametersCpi,
					"SELECT", Values.DB_SIL_DATI);
			String codCpi = (String) cpiLav.getAttribute("ROW.CPICOMP");
			;

			Object[] inputParametersReg = new Object[0];
			// recupero la stampa SituazioneLavPatronato
			// if("22".equals(provincia)) { // TRENTO
			doc.setCrystalClearRelativeReportFile("Situazione_Lavorativa/" + stampa);
			doc.setCodTipoDocumento("SSLP");
			doc.setStrDescrizione("Visura della situazione lavorativa");
			/*
			 * } else { if("2".equals(provincia)) { // VDA
			 * //doc.setCrystalClearRelativeReportFile("Situazione_Lavorativa/SituazioneLav_VDA_CC.rpt"); } else {
			 * doc.setCrystalClearRelativeReportFile("Situazione_Lavorativa/SituazioneLav_CC.rpt"); }
			 * doc.setStrDescrizione("Situazione Lavorativa"); doc.setCodTipoDocumento("SSL"); }
			 */

			doc.setCodCpi(codCpi);
			doc.setCdnLavoratore(cdnLavoratore);
			doc.setPrgAzienda(null);
			doc.setPrgUnita(null);
			doc.setFlgDocAmm("S");
			doc.setFlgDocIdentifP("N");
			doc.setDatInizio(currentDate);
			doc.setStrNumDoc(null);
			doc.setStrEnteRilascio(codCpi);
			doc.setCodMonoIO("O");
			doc.setDatAcqril(currentDate);
			doc.setCodModalitaAcqril(null);
			doc.setCodTipoFile(null);
			doc.setStrNomeDoc("SituazioneLav_" + codiceFisc + ".pdf");
			doc.setDatFine(null);
			doc.setStrNote("");
			doc.setTipoProt("S");

			// Setting userid...
			doc.setCdnUtIns(new BigDecimal("190"));
			doc.setCdnUtMod(new BigDecimal("190"));

			// parametri per il report
			Map prompts = new HashMap();
			prompts.put("codCpi", codCpi);
			prompts.put("cdnLavoratore", cdnLavoratore.toString());

			SourceBean rowProt = (SourceBean) QueryExecutor.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT",
					Values.DB_SIL_DATI);

			if (rowProt != null) {
				rowProt = (rowProt.containsAttribute("ROW") ? (SourceBean) rowProt.getAttribute("ROW") : rowProt);
				BigDecimal numAnnoProt = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
				BigDecimal numProtocollo = (BigDecimal) rowProt.getAttribute("NUMPROTOCOLLO");
				String datProtocollazione = (String) rowProt.getAttribute("DATAORAPROT");
				doc.setNumAnnoProt(numAnnoProt);
				doc.setNumProtocollo(numProtocollo);
				doc.setDatProtocollazione(datProtocollazione);
				// parametri per il report
				prompts.put("numAnnoProt", numAnnoProt);
				prompts.put("numProt", numProtocollo);
				prompts.put("dataProt", datProtocollazione);
			}

			doc.setCrystalClearPromptFields(prompts);

			try {
				doc.insert();
			} catch (Exception ex) {
				_logger.error("FALLITO INSERIMENTO: getStampaSO()" + codiceFisc + " : " + ex);
				return null;
			}
		}

		File temp = doc.getTempFile();
		dh = new DataHandler(new FileDataSource(temp));
		return dh;
	}

	private SourceBean executeSelectSO(String query_name, String codiceFiscale, String dataCalcolo, DataConnection dc)
			throws EMFUserError {
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

	public SourceBean executeSelect(String query_name, Object[] inputParameters, DataConnection dc)
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

	private final String createXMLStatoOccupazionele(String codice, String descrizione, String cf, SourceBean InfoLav) {

		_logger.debug("buildXml() - start - genero xml ");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		String returnString = "";

		String cognome = "";
		String nome = "";
		String datNasc = "";
		String comNasc = "";
		String denomNasc = "";
		String targaNasc = "";
		String comRes = "";
		String denomRes = "";
		String targaRes = "";
		String strIndirizzoRes = "";
		String comDom = "";
		String denomDom = "";
		String targaDom = "";
		String strIndirizzoDom = "";
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
		String dataElencoAnag = "";
		String sesso = "";
		Object nMesiSospForneroCompleto = null;
		BigDecimal numGGRestantiSospFornero = null;
		Object mesiRischioDisoccCompleto = null;
		BigDecimal numGGRestantiRischioDisocc = null;
		BigDecimal ggSospForneroRischioDisocc = null;
		String intermittente = null;
		String disabile68 = null;
		String listeSpeciali = null;
		String dataDichiarazione = null;

		if (InfoLav != null) {
			cognome = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.STRCOGNOME");
			nome = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.STRNOME");
			datNasc = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.datNasc");
			comNasc = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.comNasc");
			denomNasc = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.denomNasc");
			targaNasc = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.targaNasc");
			comRes = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.comRes");
			denomRes = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.denomRes");
			targaRes = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.targaRes");
			strIndirizzoRes = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.strIndirizzoRes");
			comDom = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.comDom");
			denomDom = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.denomDom");
			targaDom = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.targaDom");
			strIndirizzoDom = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.strIndirizzoDom");
			codstatooccupazragg = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.codstatooccupazragg");
			descrStatoOcc = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.strStatoOcc");
			statoOcc = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.statoOcc");
			numMesiSosp = (BigDecimal) InfoLav.getAttribute("ROW.NUMMESISOSP");
			numMesiSospPrec = (BigDecimal) InfoLav.getAttribute("ROW.NUMMESISOSPPREC");
			numMesiAnz = (BigDecimal) InfoLav.getAttribute("ROW.MESI_ANZ");
			giorniAnz = (BigDecimal) InfoLav.getAttribute("ROW.GIORNI_ANZ");
			numMesiRischioDisocc = (BigDecimal) InfoLav.getAttribute("ROW.mesi_rischio_disocc");
			numMesiAnzPrec = (BigDecimal) InfoLav.getAttribute("ROW.mesi_anz_prec");
			numMesiSospFornero = (BigDecimal) InfoLav.getAttribute("ROW.mesiSospFornero2014");
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

			nMesiSospForneroCompleto = InfoLav.getAttribute("mesiSospFornero2014_completo");
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

			mesiRischioDisoccCompleto = InfoLav.getAttribute("mesi_rischio_disocc_completo");
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

			dataDichIM = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.dataDichIM");
			codcpi = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.codcpi");
			strCodCpi = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.strDescrizione");
			dataElencoAnag = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.dataElencoAnag");
			sesso = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.strsesso");
			intermittente = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.Intermittente");
			disabile68 = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.Disabilel68");
			listeSpeciali = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.listeSpeciali");
			dataDichiarazione = StringUtils.getAttributeStrNotNull(InfoLav, "ROW.datadichiarazione");
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

			Element so = doc.createElement("datiStatoOccupazionale");
			SOElem.appendChild(so);

			UtilityXml.appendTextChild("CodiceFiscale", cf, so, doc);
			UtilityXml.appendTextChild("Cognome", cognome, so, doc);
			UtilityXml.appendTextChild("Nome", nome, so, doc);
			UtilityXml.appendTextChild("DataNascita", datNasc, so, doc);
			UtilityXml.appendTextChild("CodComNascita", comNasc, so, doc);
			UtilityXml.appendTextChild("ComNascita", denomNasc, so, doc);
			UtilityXml.appendTextChild("TargaComNascita", targaNasc, so, doc);
			UtilityXml.appendTextChild("Sesso", sesso, so, doc);

			Element res = doc.createElement("Residenza");
			so.appendChild(res);

			UtilityXml.appendTextChild("CodiceComune", comRes, res, doc);
			UtilityXml.appendTextChild("DescrComune", denomRes, res, doc);
			UtilityXml.appendTextChild("Targa", targaRes, res, doc);
			UtilityXml.appendTextChild("Indirizzo", strIndirizzoRes, res, doc);

			Element dom = doc.createElement("Domicilio");
			so.appendChild(dom);

			UtilityXml.appendTextChild("CodiceComune", comDom, dom, doc);
			UtilityXml.appendTextChild("DescrComune", denomDom, dom, doc);
			UtilityXml.appendTextChild("Targa", targaDom, dom, doc);
			UtilityXml.appendTextChild("Indirizzo", strIndirizzoDom, dom, doc);

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
			UtilityXml.appendTextChild("DataIscrElencoAnag", dataElencoAnag, cpi, doc);

			if (StringUtils.isFilledNoBlank(codstatooccupazragg) || StringUtils.isFilledNoBlank(descrStatoOcc)
					|| StringUtils.isFilledNoBlank(intermittente) || StringUtils.isFilledNoBlank(disabile68)
					|| StringUtils.isFilledNoBlank(listeSpeciali)) {
				Element da = doc.createElement("DatiAggiuntivi");
				SOElem.appendChild(da);
				if (StringUtils.isFilledNoBlank(codstatooccupazragg)) {
					UtilityXml.appendTextChild("CodiceStatoOccupazione", codstatooccupazragg, da, doc);
				}
				if (StringUtils.isFilledNoBlank(descrStatoOcc)) {
					UtilityXml.appendTextChild("DescrizioneStatoOccupazione", descrStatoOcc, da, doc);
				}
				if (StringUtils.isFilledNoBlank(intermittente)) {
					UtilityXml.appendTextChild("Intermittente", intermittente, da, doc);
				}
				if (StringUtils.isFilledNoBlank(disabile68)) {
					UtilityXml.appendTextChild("Disabilel68", disabile68, da, doc);
				}
				if (StringUtils.isFilledNoBlank(listeSpeciali)) {
					UtilityXml.appendTextChild("ListeSpeciali", listeSpeciali, da, doc);
				}
				if (StringUtils.isFilledNoBlank(dataDichiarazione)) {
					UtilityXml.appendTextChild("DataDichiarazione", dataDichiarazione, da, doc);
				}
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

	private final String validaXMLSO(Object cf, Object dataRif, String compAmm, String inputXML) throws Exception {
		String returnString = "";
		BigDecimal cdnLavoratore = null;

		String dataCalcolo = dataRif.toString();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date data1 = formatter.parse(dataCalcolo);
		String strData = DateUtils.format(data1);

		Object[] inputParameters = new Object[1];
		inputParameters[0] = cf.toString();

		SourceBean lavoratore = (SourceBean) QueryExecutor.executeQuery("SELECT_AN_LAVORATORE", inputParameters,
				"SELECT", Values.DB_SIL_DATI);
		if (!lavoratore.containsAttribute("ROW")) {
			returnString = createXMLRisposta("03", "Codice fiscale non trovato");
			return returnString;
		} else {
			cdnLavoratore = (BigDecimal) lavoratore.getAttribute("ROW.CDNLAVORATORE");
		}
		try {
			XMLValidator.checkObjectFieldExists(cf, "codicefiscale", true, codiceFiscaleCheck,
					"\"Codice fiscale del Datore di lavoro\"");
		} catch (MandatoryFieldException e1) {
			returnString = createXMLRisposta("04", "Codice fiscale non valido");
			return returnString;
		} catch (FieldFormatException e2) {
			returnString = createXMLRisposta("04", "Codice fiscale non valido");
			return returnString;
		}
		try {
			XMLValidator.checkObjectFieldExists(dataCalcolo, "dataRiferimento", true, dataCheck,
					"\"Data di Riferimento\"");
		} catch (MandatoryFieldException e1) {
			returnString = createXMLRisposta("05", "Data non valida");
			return returnString;
		} catch (FieldFormatException e2) {
			returnString = createXMLRisposta("05", "Data non valida");
			return returnString;
		}

		if (DateUtils.compare(strData, DateUtils.getNow()) > 0) {
			returnString = createXMLRisposta("06", "Data errata perché futura");
			return returnString;
		}

		if (compAmm == null || compAmm.equals("") || compAmm.equals("1")) {
			Object[] inputParametersCpi = new Object[3];
			inputParametersCpi[0] = cdnLavoratore;
			inputParametersCpi[1] = strData;
			inputParametersCpi[2] = strData;

			SourceBean cpiLav = (SourceBean) QueryExecutor.executeQuery("GET_CPI_AN_LAVORATORE_COMPETENTE_DATA",
					inputParametersCpi, "SELECT", Values.DB_SIL_DATI);
			String codCpiLav = (String) cpiLav.getAttribute("ROW.CPICOMP");
			String codTipoCpi = (String) cpiLav.getAttribute("ROW.codmonotipocpi");

			if (codCpiLav == null) {
				returnString = createXMLRisposta("99", "Errore generico");
				return returnString;
			} else {
				if (codTipoCpi != null && !"C".equalsIgnoreCase(codTipoCpi)) {
					returnString = createXMLRisposta("07", "Lavoratore non competente");
					return returnString;
				}
			}
		}

		String validityErrors = XMLValidator.getValidityErrors(inputXML, schemaFile);
		if (validityErrors != null) {
			_logger.error("Errore nella validazione dell'xml da inviare" + validityErrors);
			_logger.error("xml generato: " + inputXML);
			returnString = createXMLRisposta("99", "Errore generico");
			return returnString;
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

}
