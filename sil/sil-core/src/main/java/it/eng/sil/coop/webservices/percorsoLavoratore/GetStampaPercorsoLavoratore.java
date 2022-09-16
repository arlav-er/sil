package it.eng.sil.coop.webservices.percorsoLavoratore;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.cigs.bean.BeanUtils;
import it.eng.sil.security.User;
import it.eng.sil.util.EncryptDecryptUtils;
import it.eng.sil.util.TraceWrapper;

public class GetStampaPercorsoLavoratore {

	private static final String COD_ERRORE_GENERICO = "99";

	private static final String ERRORE_GENERICO = "Errore generico";

	public class XP {

		/**
		 * Logger for this class
		 */
		private final Logger logger = Logger.getLogger(XP.class);
		public XPathExpression CODICE_FISCALE, DATA_FINE, DATA_INIZIO, TIPOLOGIA_INFORMAZIONE, ID_PROVINCIA;
		private XPath xpath;
		private XPathExpression ModelloStampa;
		private XPathExpression Protocollazione;
		private XPathExpression InfoStatoOccupazionale;
		private XPathExpression Intestazione;

		private Element elem2parse;

		public XP(Element elem2parse) throws XPathExpressionException {
			super();
			this.elem2parse = elem2parse;
			xpath = XPathFactory.newInstance().newXPath();
			CODICE_FISCALE = xpath.compile("/PercorsoLavoratore/CodiceFiscale");
			DATA_FINE = xpath.compile("/PercorsoLavoratore/DataFine");
			DATA_INIZIO = xpath.compile("/PercorsoLavoratore/DataInizio");
			TIPOLOGIA_INFORMAZIONE = xpath.compile("/PercorsoLavoratore/TipologiaInformazione");
			ID_PROVINCIA = xpath.compile("/PercorsoLavoratore/idProvincia");
			ModelloStampa = xpath.compile("/PercorsoLavoratore/Stampa/ModelloStampa");
			Protocollazione = xpath.compile("/PercorsoLavoratore/Stampa/Protocollazione");
			Intestazione = xpath.compile("/PercorsoLavoratore/Stampa/Intestazione");
			InfoStatoOccupazionale = xpath.compile("/PercorsoLavoratore/Stampa/InfoStatoOccupazionale");
		}

		public Element getElem2parse() {
			return elem2parse;
		}

		String getText(XPathExpression xp) throws XPathExpressionException {
			String str = xp.evaluate(getElem2parse());

			return str;
		}

		String getTextIfNotNull(XPathExpression xp, String failSafe) throws XPathExpressionException {
			String str = xp.evaluate(getElem2parse());
			if (StringUtils.isEmptyNoBlank(str)) {
				return failSafe;
			}
			return str;
		}

		public String getTextOrNull(XPathExpression xp) throws XPathExpressionException {
			return getTextIfNotNull(xp, null);
		}

		public void setElem2parse(Element elem2parse) {
			this.elem2parse = elem2parse;
		}

	}

	// private static final String NESSUN_ERRORE = "00";
	// private static final String CODICE_FISCALE_NON_TROVATO = "03";
	// private static final String CODICE_FISCALE_NON_VALIDO = "04";

	public static final String ROW = "ROW";

	public final static Pattern PATTERN_CODICE_FISCALE = Pattern
			.compile("([A-Z]{6}[\\dLMNPQRSTUV]{2}[A-Z][\\dLMNPQRSTUV]{2}[A-Z][\\dLMNPQRSTUV]{3}[A-Z])|(\\d{11})");

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(GetStampaPercorsoLavoratore.class.getName());

	private static final BigDecimal CdnUt = new BigDecimal(150);

	public String getStampaPercorsoLavoratoreImpl(String inputXML, QueryExecutorObject qExec,
			TransactionQueryExecutor transExec)
			throws GetPercorsoLavoratoreException, ParserConfigurationException, SAXException, IOException,
			XPathExpressionException, ParseException, TransformerException, EMFInternalError, SourceBeanException {
		_logger.info("Il servizio Stampa Percorso Lavoratore e' stato chiamato");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		String validityErrors = getValidityErrors(inputXML, GetPercorsoLavoratore.getXsdPercorsoLavoratoreReq());

		if (!StringUtils.isEmptyNoBlank(validityErrors)) {
			final String validityError = "Errore di validazione xml: " + validityErrors;
			_logger.warn(validityError);
			_logger.warn(inputXML);
			throw new GetPercorsoLavoratoreException("", COD_ERRORE_GENERICO, validityError);
		}

		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource source = new InputSource(new StringReader(inputXML));
		Document document = builder.parse(source);

		XP xp = new XP(document.getDocumentElement());

		String codiceFiscale = xp.getText(xp.CODICE_FISCALE);
		// controllare correttezza date
		String dataInizioStr = xp.getTextOrNull(xp.DATA_INIZIO);
		String dataFineStr = xp.getTextOrNull(xp.DATA_FINE);

		if (StringUtils.isEmptyNoBlank(dataInizioStr) || StringUtils.isEmptyNoBlank(dataFineStr)) {
			// lancio errore mancanza di parametri
			throw new GetPercorsoLavoratoreException(codiceFiscale, "05", "Data non valida");
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dataIniz = sdf.parse(dataInizioStr);
		Date datafin = sdf.parse(dataFineStr);
		GetPercorsoLavoratore.checkDate(codiceFiscale, dataIniz, datafin);
		SimpleDateFormat sdfSql = new SimpleDateFormat("dd/MM/yyyy");
		String dataInizSql = sdfSql.format(dataIniz);
		String dataFineSql = sdfSql.format(datafin);

		// controllo correttezza date

		String ModelloStampa = xp.getTextOrNull(xp.ModelloStampa);
		String Protocollazione = xp.getTextOrNull(xp.Protocollazione);
		String Intestazione = xp.getTextOrNull(xp.Intestazione);
		String InfoStatoOccupazionale = xp.getTextOrNull(xp.InfoStatoOccupazionale);

		String idProvinciaStr = xp.getText(xp.ID_PROVINCIA);
		String tipologiaInformazioneStr = xp.getText(xp.TIPOLOGIA_INFORMAZIONE);

		String codiceStr = null;
		String descrizioneStr = null;
		if (!PATTERN_CODICE_FISCALE.matcher(codiceFiscale).matches()) {
			// TODO - lancio errore 04
			codiceStr = "04";
			descrizioneStr = "Codice fiscale non valido";
			throw new GetPercorsoLavoratoreException(codiceFiscale, COD_ERRORE_GENERICO, ERRORE_GENERICO);
		}

		//
		DataConnection dc = qExec.getDataConnection();
		List<DataField> inPars = new ArrayList<DataField>();
		inPars.add(dc.createDataField("codiceFiscaleLavoratore", Types.VARCHAR, codiceFiscale));

		qExec.setInputParameters(inPars);
		qExec.setTokenStatement("GET_CDNLAVORATORE");

		Object returned = qExec.exec();
		// returned = qExec.exec();
		SourceBean lav = null;
		// String errorString = "Corso non trovato " + datiParsed;
		BigDecimal cdnLavoratore = null;
		if (returned instanceof SourceBean) {
			SourceBean sb = (SourceBean) returned;
			Vector<SourceBean> vectLav = sb.getAttributeAsVector(ROW);
			if (vectLav.size() > 1) {
				// non dovrebbe accadere - ERRORE!
				_logger.warn(codiceStr);
				_logger.warn(descrizioneStr);
				throw new GetPercorsoLavoratoreException(codiceFiscale, COD_ERRORE_GENERICO, ERRORE_GENERICO);
			}
			if (vectLav.size() > 0) {
				// ritorno il 1' elemento
				lav = vectLav.firstElement();
			} else {
				codiceStr = "04";
				descrizioneStr = "Codice fiscale non trovato";
				_logger.warn(codiceStr);
				_logger.warn(descrizioneStr);
				throw new GetPercorsoLavoratoreException(codiceFiscale, codiceStr, descrizioneStr);
			}
			BeanUtils bu = new BeanUtils(lav);
			cdnLavoratore = bu.getBigDecimal0("cdnlavoratore");
		}

		// check 07 Lavoratore non competente
		String dataOdiernaSql = sdfSql.format(new Date());
		String codCpi = GetPercorsoLavoratore.checkCompetenzaLav(cdnLavoratore, codiceFiscale,
				/* dataInizSql */
				dataOdiernaSql,
				/* dataFineSql */
				dataOdiernaSql, qExec, inPars);

		String[] tipologiaInformazione = tipologiaInformazioneStr.split("-");
		List<String> infoDaVisualizzare = new ArrayList<String>(Arrays.asList(tipologiaInformazione));

		GetPercorsoLavoratore.checkInfoDaVisualizzare(codiceFiscale, infoDaVisualizzare);

		String stmBshowQual = "";
		String stmOshowQual = "";
		// adeguare i codici ed i flag B & O alle info richieste
		// B_S Movimenti amministrativi da comunicazione obbligatoria è con
		// visualizzazione della qualifica
		// B_N Movimenti amministrativi da comunicazione obbligatoria è senza la
		// visualizzazione della qualifica
		if (infoDaVisualizzare.contains("B_S")) {
			infoDaVisualizzare.add("B");
			infoDaVisualizzare.remove("B_S");
			stmBshowQual = "S";
		}
		if (infoDaVisualizzare.contains("B_N")) {
			infoDaVisualizzare.add("B");
			infoDaVisualizzare.remove("B_N");
			stmBshowQual = "N";
		}
		// O_S Movimenti amministrativi dichiarati dal lavoratore/documentati è
		// con visualizzazione della qualifica
		// O_N Movimenti amministrativi dichiarati dal lavoratore/documentati è
		// senza la visualizzazione della qualifica
		if (infoDaVisualizzare.contains("O_S")) {
			infoDaVisualizzare.add("O");
			infoDaVisualizzare.remove("O_S");
			stmOshowQual = "S";
		}
		if (infoDaVisualizzare.contains("O_N")) {
			infoDaVisualizzare.add("O");
			infoDaVisualizzare.remove("O_N");
			stmOshowQual = "N";
		}

		// Aggiungo stampa
		String dataInizio = null;
		SourceBean request = null;
		try {
			RequestContainer requestContainer = new RequestContainer();

			SessionContainer sessionContainer = new SessionContainer(true);
			sessionContainer.setAttribute("_CDUT_", CdnUt);
			sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
			sessionContainer.setAttribute(User.USERID, CdnUt);
			requestContainer.setSessionContainer(sessionContainer);
			request = new SourceBean("SERVICE_REQUEST");
			// SourceBean response = new SourceBean("SERVICE_RESPONSE");

			// request.setAttribute("FORZA_INSERIMENTO", "true");
			// request.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
			// request.setAttribute("FORZA_CHIUSURA_MOBILITA", "true");
			// request.setAttribute("datDichiarazione", dataDichiarazione);
			request.setAttribute("cdnLavoratore", cdnLavoratore.toString());
			requestContainer.setServiceRequest(request);
			RequestContainer.setRequestContainer(requestContainer);
		} catch (Exception e) {
			TraceWrapper.error(_logger, "GetStampaPercorsoLavoratore:getStampaPercorsoLavoratoreImpl", e);
			throw new GetPercorsoLavoratoreException(codiceFiscale, COD_ERRORE_GENERICO, ERRORE_GENERICO, e);
		}
		// String progressivo = null;
		inPars.clear();
		inPars.add(dc.createDataField("cdnlavoratore", Types.VARCHAR, cdnLavoratore));
		qExec.setInputParameters(inPars);
		qExec.setStatement(null);
		qExec.setTokenStatement("GET_INFO_GEN_LAVORATORE");

		returned = qExec.exec();
		Map prompts = new HashMap();
		if (returned instanceof SourceBean) {
			SourceBean row = (SourceBean) returned;
			String nome = null, cognome = null, dataNascita = null, indDomicilio = null, comuneDomicilio = null,
					statoOcc = null;
			String dataInizioStOcc = null, dataAnzianitaDisocc = null, dataInizioDid = null;

			nome = it.eng.sil.util.Utils.notNull(row.getAttribute("row.strNome"));
			cognome = it.eng.sil.util.Utils.notNull(row.getAttribute("row.STRCOGNOME"));
			codiceFiscale = it.eng.sil.util.Utils.notNull(row.getAttribute("row.STRCODICEFISCALE"));
			dataNascita = it.eng.sil.util.Utils.notNull(row.getAttribute("row.datNasc"));
			indDomicilio = it.eng.sil.util.Utils.notNull(row.getAttribute("row.STRINDIRIZZODOM"));
			comuneDomicilio = it.eng.sil.util.Utils.notNull(row.getAttribute("row.comDomicilio"));
			statoOcc = it.eng.sil.util.Utils.notNull(row.getAttribute("row.statoOccupazionale"));
			dataAnzianitaDisocc = it.eng.sil.util.Utils.notNull(row.getAttribute("row.datAnzianitaDisoc"));
			dataInizioStOcc = it.eng.sil.util.Utils.notNull(row.getAttribute("row.datInizioSocc"));
			dataInizioDid = it.eng.sil.util.Utils.notNull(row.getAttribute("row.datInizioDid"));

			String infoInst;

			for (int i = 0; i < GetPercorsoLavoratore._tipoInfo.length; i++) {
				String infoPercorso = GetPercorsoLavoratore._tipoInfo[i];
				boolean trovatoPer = false;
				for (int j = 0; j < infoDaVisualizzare.size() && !trovatoPer; j++) {
					infoInst = infoDaVisualizzare.get(j);
					if (infoPercorso.equalsIgnoreCase(infoInst)) {
						prompts.put("info" + infoInst, infoInst);
						trovatoPer = true;
					}
				}
				if (!trovatoPer) {
					prompts.put("info" + infoPercorso, null);
				}
			}

			prompts.put("showQualCodB", stmBshowQual);
			prompts.put("showQualCodO", stmOshowQual);

			// passaggio parametri all'engine del report per nome
			prompts.put("cdnLavoratore", cdnLavoratore.toString());
			String cdnLavoratoreEncrypt = EncryptDecryptUtils.encrypt(cdnLavoratore.toString());
			prompts.put("cdnLavoratoreEncrypt", cdnLavoratoreEncrypt);
			prompts.put("codCpi", codCpi);
			prompts.put("codiceFiscale", codiceFiscale);
			prompts.put("comuneDomicilio", comuneDomicilio);
			prompts.put("dataInizioAnz", dataAnzianitaDisocc);
			prompts.put("dataInizioDid", dataInizioDid);
			prompts.put("dataInizioStOcc", dataInizioStOcc);
			prompts.put("dataNascita", dataNascita);
			prompts.put("indirizzoDomicilio", indDomicilio);
			prompts.put("nomeLavoratore", cognome + " " + nome);
			prompts.put("statoOccupaz", statoOcc);
			if ("ON".equals(Intestazione)) {
				prompts.put("titolo", "PERCORSO LAVORATORE");
			}
			prompts.put("InfoStatoOccupazionale", InfoStatoOccupazionale);
			prompts.put("Intestazione", Intestazione);
			prompts.put("Protocollazione", Protocollazione);
			prompts.put("dataDa", dataInizSql);
			prompts.put("dataA", dataFineSql);

		}

		transExec.initTransaction();

		DataHandler dh = getStampaPercorsoLav(ModelloStampa, codCpi, cdnLavoratore, dataInizio, CdnUt, request,
				transExec, "ON".equals(Protocollazione), prompts);
		String xmlOut = null;
		if (dh == null) {
			throw new GetPercorsoLavoratoreException(codiceFiscale, "08", "Errore di protocollazione");
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
			transExec.commitTransaction();
			xmlOut = GetPercorsoLavoratore.OK_RESULT;
		}

		return xmlOut;
	}

	private DataHandler getStampaPercorsoLav(String rptStampa, String codCpi, BigDecimal cdnLavoratore,
			String dataInizio, BigDecimal CdnUt, SourceBean request, TransactionQueryExecutor transExec,
			boolean isProtocolloReq, Map prompts)
			throws EMFInternalError, GetPercorsoLavoratoreException, SourceBeanException {
		DataHandler dh = null;
		Documento doc = null;
		// String progressivo = getProgressivo().toString();

		doc = new Documento();
		String currentDate = DateUtils.getNow();

		doc.setCrystalClearRelativeReportFile("Amministrazione" + File.separator + rptStampa);
		doc.setCodTipoDocumento("PERC_LAV");
		doc.setCodCpi(codCpi);
		doc.setChiaveTabella(null);
		doc.setCdnLavoratore(cdnLavoratore);
		doc.setPrgAzienda(null);
		doc.setPrgUnita(null);
		doc.setStrDescrizione("");
		doc.setFlgDocAmm("S");
		doc.setFlgDocIdentifP("N");
		doc.setDatInizio(dataInizio);
		doc.setStrNumDoc(null);
		doc.setStrEnteRilascio(codCpi);
		doc.setCodMonoIO("I");
		doc.setDatAcqril(currentDate);
		doc.setCodModalitaAcqril(null);
		doc.setCodTipoFile(null);
		doc.setStrNomeDoc("PercorsoLavoratore.pdf");
		doc.setDatFine(null);
		doc.setStrNote("");
		doc.setTipoProt("S");
		doc.setCodStatoAtto("PR");
		doc.setStrDescrizione("Percorso Lavoratore");
		doc.setFlgAutocertificazione("N");
		// doc.setPagina("DispoDettaglioPage");
		doc.setCdnUtIns(CdnUt);
		doc.setCdnUtMod(CdnUt);
		if (isProtocolloReq) {
			SourceBean rowProt = (SourceBean) transExec.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT");
			if (rowProt == null) {
				_logger.error("impossibile protocollare il documento di identificazione");
				throw new GetPercorsoLavoratoreException("", COD_ERRORE_GENERICO, ERRORE_GENERICO);
			}
			rowProt = (rowProt.containsAttribute("ROW") ? (SourceBean) rowProt.getAttribute("ROW") : rowProt);
			BigDecimal numAnnoProt = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
			BigDecimal numProtocollo = (BigDecimal) rowProt.getAttribute("NUMPROTOCOLLO");
			String datProtocollazione = (String) rowProt.getAttribute("DATAORAPROT");

			doc.setNumAnnoProt(numAnnoProt);
			doc.setNumProtocollo(numProtocollo);
			doc.setDatProtocollazione(datProtocollazione);

			request.setAttribute("numProt", numProtocollo.toString());
			request.setAttribute("annoProt", numAnnoProt.toString());
			request.setAttribute("dataProt", datProtocollazione);
		} // if (isProtocolloReq)

		// parametri per il report
		// Map prompts = new HashMap();
		// // prompts.put("par_DichDisp", progressivo);
		// prompts.put("codCpi", codCpi);
		// prompts.put("cdnLavoratore", cdnLavoratore.toString());
		doc.setCrystalClearPromptFields(prompts);

		try {
			doc.insert(transExec);
		} catch (Exception e) {
			_logger.error("impossibile inserire il documento", e);
			throw new GetPercorsoLavoratoreException("", COD_ERRORE_GENERICO, ERRORE_GENERICO, e);
		}

		File temp = doc.getTempFile();

		dh = new DataHandler(new FileDataSource(temp));

		return dh;
	}

	private Element getTxtElement(Document doc, String elemName, String elemValue) {

		Element elemObj = doc.createElement(elemName);
		Text node = doc.createTextNode(String.valueOf(elemValue));
		elemObj.appendChild(node);
		return elemObj;
	}

	private String getValidityErrors(String datiRichiestaXml, String xsdPath) {
		if (_logger.isDebugEnabled()) {
			_logger.debug("getValidityErrors(String) - start");
		}

		if (StringUtils.isEmptyNoBlank(xsdPath)) {
			return "";
		}

		try {
			String schemaLang = "http://www.w3.org/2001/XMLSchema";

			// get validation driver:
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

			// create schema by reading it from an XSD file:
			// URL schemaUrl =
			// getClass().getClassLoader().getResource("esito_corso_CIG.xsd");
			// StreamSource streamSource = new
			// StreamSource(schemaUrl.openStream());
			// String xsdPath = ConfigSingleton.getRootPath() + File.separator +
			// "WEB-INF" + File.separator
			// + GET_PERCORSO_LAVORATORE_REQ;
			File schemaFile = new File(xsdPath);
			StreamSource streamSource = new StreamSource(schemaFile);
			Schema schema = factory.newSchema(streamSource);
			Validator validator = schema.newValidator();
			// at last perform validation:
			StringReader datiXmlReader = new StringReader(datiRichiestaXml);
			StreamSource datiXmlStreamSource = new StreamSource(datiXmlReader);
			validator.validate(datiXmlStreamSource);
		} catch (Exception e) {
			_logger.warn("getValidityErrors(String)", e);

			// e.printStackTrace();
			String returnString = e.getLocalizedMessage();
			if (_logger.isDebugEnabled()) {
				_logger.debug("getValidityErrors(String) - end");
			}
			return returnString;
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("getValidityErrors(String) - end");
		}
		return null;
	}

}