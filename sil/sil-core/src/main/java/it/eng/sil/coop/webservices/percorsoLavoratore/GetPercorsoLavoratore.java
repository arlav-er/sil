package it.eng.sil.coop.webservices.percorsoLavoratore;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.utils.Utils;
import it.eng.sil.module.amministrazione.DynamicRicercaPercorsoLavoratoreModular;
import it.eng.sil.module.cigs.bean.BeanUtils;
import it.eng.sil.util.xml.XMLValidator;

public class GetPercorsoLavoratore {

	public class XP {

		/**
		 * Logger for this class
		 */
		private final Logger logger = Logger.getLogger(XP.class);
		public XPathExpression CODICE_FISCALE, DATA_FINE, DATA_INIZIO, TIPOLOGIA_INFORMAZIONE, ID_PROVINCIA;
		private XPath xpath;

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

	public static final String ROW = "ROW";

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetPercorsoLavoratore.class.getName());

	SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");

	private static final String[] tipoInfoGestite = { "A", "B_S", "B_N", "O_S", "O_N", "C", "D", "E", "R", "F", "G",
			"H", "I", "L", "N", "M", "P", "Q", "S" };
	public static final String[] _tipoInfo = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "L", "M", "N", "O", "P",
			"Q", "R", "S" };

	private static final List<String> tipoInfoAmmesse = Arrays.asList(tipoInfoGestite);

	private static final String Risposta = "Risposta";
	// private static final String PERCORSO_LAVORATORE = "Risposta";
	private static final String XSD_PERCORSO_LAVORATORE_REQ = "percorsoLavoratoreReq.xsd";

	public static void checkInfoDaVisualizzare(String codiceFiscale, List<String> infoDaVisualizzare)
			throws TransformerException, GetPercorsoLavoratoreException {
		for (String info : infoDaVisualizzare) {
			if (tipoInfoAmmesse.contains(info)) {
				// info richiesta ammessa
			} else {
				throw new GetPercorsoLavoratoreException(codiceFiscale, "99", "Info:" + info + " non ammissibile");
			}
		}
	}

	public static String getXsdPercorsoLavoratoreReq() {
		String xsdPath = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd"
				+ File.separator + "PercorsoLavoratore" + File.separator + XSD_PERCORSO_LAVORATORE_REQ;
		return xsdPath;
	}

	public static String GEN_ERROR_RESULT = "<?xml version='1.0' encoding='UTF-8'?><Risposta xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' schemaVersion='1'><Esito><codice>99</codice><descrizione>Errore generico</descrizione></Esito></Risposta>";
	public static String OK_RESULT = "<?xml version='1.0' encoding='UTF-8'?><Risposta xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' schemaVersion='1'><Esito><codice>00</codice><descrizione>OK</descrizione></Esito></Risposta>";

	// private String createErrorResponse(Document doc, Element rootElement,
	// String _codiceFiscale, String codiceStr, String descrizioneStr)
	// throws TransformerException {
	//
	// Element esitoTag = getEsito(doc, codiceStr, descrizioneStr);
	// rootElement.appendChild(esitoTag);
	// // doc.appendChild(rootElement);
	// String xmlOut = createResponseString(doc);
	// return xmlOut;
	// }

	public static String createResponseString(Document doc) throws TransformerException {
		String xmlOut = XMLValidator.domToString(doc);
		return xmlOut;
	}

	private Element getElement(Document doc, SourceBean sb) {
		String STATUS = BeanUtils.getObjectToString4Html(sb, "STATUS", "");
		String SERVIZI = BeanUtils.getObjectToString4Html(sb, "SERVIZI", "");
		String MOVIMENTI = BeanUtils.getObjectToString4Html(sb, "MOVIMENTI", "");

		// String elemName = "DataDa";
		// String elemValue = BeanUtils.getObjectToString4Html(sb, "DataDa",
		// "");
		// // String elemValue = BeanUtils.getObjectToString4Html(sb, "DataDa",
		// "");
		//
		// elemName = "DataA";
		// elemValue = BeanUtils.getObjectToString4Html(sb, "DataA", "");
		// Element DataA = getTxtElement(doc, elemName, elemValue);

		// elemName = ;
		String TipoValue = "";

		String DataDaValue = "";
		String DataAValue = "";
		String splitter = "-&amp;gt;";
		if (StringUtils.isFilledNoBlank(STATUS)) {
			TipoValue = "ST";
			String[] dateMov = STATUS.split(splitter);
			if (dateMov.length > 0) {
				DataDaValue = dateMov[0];
			}
			if (dateMov.length > 1) {
				DataAValue = dateMov[1];
			}
		} else if (StringUtils.isFilledNoBlank(SERVIZI)) {
			TipoValue = "SE";
			DataDaValue = SERVIZI;
		} else if (StringUtils.isFilledNoBlank(MOVIMENTI)) {
			TipoValue = "MO";
			String[] dateMov = MOVIMENTI.split(splitter);
			if (dateMov.length > 0) {
				DataDaValue = dateMov[0];
			}
			if (dateMov.length > 1) {
				DataAValue = dateMov[1];
			}
		}
		Element Tipo = getTxtElement(doc, "Tipo", TipoValue);
		Element DataDa = getTxtElement(doc, "DataDa", DataDaValue);
		Element DataA = getTxtElement(doc, "DataA", DataAValue);

		TipoValue = BeanUtils.getObjectToString(sb, "DESCRIZIONEPERCORSO", "");
		Element Descrizione = getTxtElement(doc, "Descrizione", TipoValue);
		Element element = doc.createElement("element");

		element.appendChild(DataDa);
		element.appendChild(DataA);
		element.appendChild(Tipo);
		element.appendChild(Descrizione);

		return element;
	}

	private Element getEsito(org.w3c.dom.Document doc, String codiceStr, String descrizioneStr) {
		// Element tag = doc.createElement(TAG_PREFIX+nome);
		// Text node = doc.createTextNode(String.valueOf(valore));
		// tag.appendChild(node);

		Element codice = getTxtElement(doc, "codice", codiceStr);
		Element descrizione = getTxtElement(doc, "descrizione", descrizioneStr);
		Element esito = doc.createElement("Esito");
		esito.appendChild(codice);
		esito.appendChild(descrizione);

		return esito;
	}

	/**
	 * XML da avere <br>
	 * String codiceFiscale, <br>
	 * String dataInizio, String dataFine,<br>
	 * String idProvincia,<br>
	 * String strListaTipologia<br>
	 *
	 * @throws Exception
	 *
	 */
	public String getPercorsoLavoratore(String inputXML) {
		String resultXml;
		QueryExecutorObject qExec = null;
		try {
			qExec = Utils.getQueryExecutorObject();
			resultXml = getPercorsoLavoratoreImpl(inputXML, qExec);
		} catch (GetPercorsoLavoratoreException e) {
			_logger.error("getStampaPercorsoLavoratoreImpl", e);
			try {
				resultXml = Utils.createXMLRisposta(e.getRespCode(), e.getRespDesc());
			} catch (Exception e1) {
				_logger.error("Utils.createXMLRisposta", e1);
				resultXml = GEN_ERROR_RESULT;
			}

		} catch (Exception e) {
			resultXml = GEN_ERROR_RESULT;
		} finally {
			try {
				qExec.getDataConnection().close();
			} catch (EMFInternalError e) {
				_logger.error("qExec.getDataConnection().close();", e);

			}
		}

		return resultXml;

	}

	private String getPercorsoLavoratoreImpl(String inputXML, QueryExecutorObject qExec)
			throws GetPercorsoLavoratoreException, EMFUserError, TransformerException, ParserConfigurationException,
			SAXException, IOException, XPathExpressionException, EMFInternalError, SQLException, ParseException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		try {
			parser = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			_logger.error("Errore di inizializzazione del messaggio di risposta", e);
			// FIXME creare i messaggi per PercorsoLavoratore???
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO);
		}
		// Create blank DOM Document
		Document doc = parser.newDocument();
		Element rootElement = doc.createElement(Risposta);
		rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		doc.appendChild(rootElement);

		String validityErrors = getValidityErrors(inputXML, getXsdPercorsoLavoratoreReq());

		if (!StringUtils.isEmptyNoBlank(validityErrors)) {
			final String validityError = "Errore di validazione xml: " + validityErrors;
			_logger.warn(validityError);
			_logger.warn(inputXML);
			throw new GetPercorsoLavoratoreException("", "99", validityError);
			// return createErrorResponse(doc, rootElement, "", "99",
			// validityErrors);
		}

		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource source = new InputSource(new StringReader(inputXML));
		Document document = builder.parse(source);

		_logger.info("Il servizio di info lavoratore e' stato chiamato");

		XP xp = new XP(document.getDocumentElement());

		String codiceFiscale = xp.getText(xp.CODICE_FISCALE);
		// controllare correttezza date
		String dataInizioStr = xp.getTextOrNull(xp.DATA_INIZIO);
		String dataFineStr = xp.getTextOrNull(xp.DATA_FINE);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dataIniz = null;
		if (StringUtils.isFilledNoBlank(dataInizioStr)) {
			dataIniz = sdf.parse(dataInizioStr);
		}
		Date datafin = null;
		if (StringUtils.isFilledNoBlank(dataFineStr)) {
			datafin = sdf.parse(dataFineStr);
		}

		GetPercorsoLavoratore.checkDate(codiceFiscale, dataIniz, datafin);
		String dataInizSql = "";
		String dataFineSql = "";
		SimpleDateFormat sdfSql = new SimpleDateFormat("dd/MM/yyyy");

		if (dataIniz != null) {
			dataInizSql = sdfSql.format(dataIniz);
		}
		if (datafin != null) {
			dataFineSql = sdfSql.format(datafin);
		}

		// controllare correttezza date

		String idProvinciaStr = xp.getText(xp.ID_PROVINCIA);
		String tipologiaInformazioneStr = xp.getText(xp.TIPOLOGIA_INFORMAZIONE);

		// if (StringUtils.isEmptyNoBlank(codiceFiscale)
		// || StringUtils.isEmptyNoBlank(tipologiaInformazioneStr)
		// || StringUtils.isEmptyNoBlank(idProvinciaStr)) {
		// controlli gestiti a livello xsd
		// }

		String codiceStr = null;
		String descrizioneStr = null;
		if (!Utils.PATTERN_CODICE_FISCALE.matcher(codiceFiscale).matches()) {
			codiceStr = "04";
			descrizioneStr = "Codice fiscale non valido";
			throw new GetPercorsoLavoratoreException(codiceFiscale, codiceStr, descrizioneStr);
		}

		DataConnection dc = qExec.getDataConnection();
		List<DataField> inPars = new ArrayList<DataField>();
		inPars.add(dc.createDataField("codiceFiscaleLavoratore", Types.VARCHAR, codiceFiscale));

		qExec.setInputParameters(inPars);
		qExec.setTokenStatement("GET_CDNLAVORATORE");

		Object returned = qExec.exec();
		SourceBean lav = null;
		BigDecimal cdnLavoratore = null;
		if (returned instanceof SourceBean) {
			SourceBean sb = (SourceBean) returned;
			Vector<SourceBean> vectLav = sb.getAttributeAsVector(ROW);
			if (vectLav.size() > 1) {
				// non dovrebbe accadere - ERRORE!
				_logger.warn(codiceStr);
				_logger.warn(descrizioneStr);
				throw new GetPercorsoLavoratoreException(codiceFiscale, codiceStr, descrizioneStr);
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

		String dataRifCompetenza = DateUtils.getNow();
		checkCompetenzaLavByDate(codiceFiscale, cdnLavoratore, dataRifCompetenza, dataRifCompetenza, qExec, dc, inPars);

		String[] tipologiaInformazione = tipologiaInformazioneStr.split("-");
		List<String> infoDaVisualizzare = new ArrayList<String>(Arrays.asList(tipologiaInformazione));

		checkInfoDaVisualizzare(codiceFiscale, infoDaVisualizzare);

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

		// Collection infoDaVisualizzare = null;
		String finalStm = DynamicRicercaPercorsoLavoratoreModular.popolaStmRicercaPercLav(cdnLavoratore.toString(),
				dataInizSql, dataFineSql, infoDaVisualizzare, stmBshowQual, stmOshowQual);

		inPars.clear();
		qExec.setInputParameters(inPars);
		qExec.setTokenStatement(null);
		qExec.setStatement(finalStm);

		returned = qExec.exec();
		List<Element> elementsList = new ArrayList<Element>();
		if (returned instanceof SourceBean) {
			SourceBean sb = (SourceBean) returned;
			Vector<SourceBean> vectElem = sb.getAttributeAsVector(ROW);
			for (SourceBean sourceBean : vectElem) {
				elementsList.add(getElement(doc, sourceBean));
			}
		}

		Element esitoTag = getEsito(doc, "00", "OK");

		Element datiTag = doc.createElement("dati");
		Element cfTag = getTxtElement(doc, "codicefiscale", codiceFiscale);
		datiTag.appendChild(cfTag);
		rootElement.appendChild(esitoTag);
		if (datiTag != null) {
			rootElement.appendChild(datiTag);
		}
		if (elementsList != null) {
			for (Element element : elementsList) {
				rootElement.appendChild(element);
			}
		}

		String xmlOut = createResponseString(doc);
		return xmlOut;
	}

	/**
	 *
	 * Se almeno 1 data è richiesta faccio il controllo di competenza
	 * 
	 * @param codiceFiscale
	 * @param cdnLavoratore
	 * @param dataInizSql
	 * @param dataFineSql
	 * @param qExec
	 * @param dc
	 * @param inPars
	 * @throws GetPercorsoLavoratoreException
	 */
	private void checkCompetenzaLavByDate(String codiceFiscale, BigDecimal cdnLavoratore, String dataInizSql,
			String dataFineSql, QueryExecutorObject qExec, DataConnection dc, List<DataField> inPars)
			throws GetPercorsoLavoratoreException {
		if ((StringUtils.isFilledNoBlank(dataInizSql) || StringUtils.isFilledNoBlank(dataFineSql))) {

			String dataInizComp = dataInizSql;
			String dataFineComp = dataFineSql;

			if (StringUtils.isEmptyNoBlank(dataInizSql) && !StringUtils.isEmptyNoBlank(dataFineSql)) {
				dataInizComp = dataFineSql;
				dataFineComp = dataFineSql;
				// controllo su data fine
			}
			if (!StringUtils.isEmptyNoBlank(dataInizSql) && StringUtils.isEmptyNoBlank(dataFineSql)) {
				dataInizComp = dataInizSql;
				dataFineComp = dataInizSql;
				// controllo su data inizio
			}
			String codCpi = checkCompetenzaLav(cdnLavoratore, codiceFiscale, dataInizComp, dataFineComp, qExec, inPars);
		}
	}

	/**
	 * XML da avere codiceFiscale codProvincia dataInizio dataFine strListaTipologia flgIntestazione flgInfoStatoOcc
	 *
	 * @return
	 * @throws InputValidationException
	 * @throws GetPercorsoLavoratoreException
	 */
	public String getStampaPercorsoLavoratore(String inputXML)
			throws InputValidationException, GetPercorsoLavoratoreException {
		GetStampaPercorsoLavoratore impl = new GetStampaPercorsoLavoratore();
		String resultXml;
		QueryExecutorObject qExec = null;
		TransactionQueryExecutor transExec = null;

		try {
			transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			qExec = Utils.getQueryExecutorObject();
			resultXml = impl.getStampaPercorsoLavoratoreImpl(inputXML, qExec, transExec);
		} catch (GetPercorsoLavoratoreException e) {
			resultXml = GEN_ERROR_RESULT;
			_logger.error("getStampaPercorsoLavoratoreImpl", e);
			try {
				resultXml = Utils.createXMLRisposta(e.getRespCode(), e.getRespDesc());
			} catch (Exception e1) {
				_logger.error("Utils.createXMLRisposta", e1);
			}
			try {
				transExec.rollBackTransaction();
			} catch (Exception ex) {
				_logger.error("transExec.rollBackTransaction;", ex);
			}
		} catch (Exception e) {
			resultXml = GEN_ERROR_RESULT;
			try {
				transExec.rollBackTransaction();
			} catch (Exception ex) {
				_logger.error("transExec.rollBackTransaction;", ex);
			}
		} finally {
			// Chiudo tutte le connessioni
			try {
				qExec.getDataConnection().close();
			} catch (Exception e) {
				_logger.error("qExec.getDataConnection().close();", e);
			}
			try {
				transExec.getDataConnection().close();
			} catch (Exception e) {
				_logger.error("transExec.getDataConnection().close();", e);
			}
		}
		return resultXml;
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

		try {
			String schemaLang = "http://www.w3.org/2001/XMLSchema";

			// get validation driver:
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

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

	public static String checkCompetenzaLav(BigDecimal cdnLavoratore, String codiceFiscale, String dataInizSql,
			String dataFineSql, QueryExecutorObject qExec, List<DataField> inPars)
			throws GetPercorsoLavoratoreException {

		Object returned;
		DataConnection dc = qExec.getDataConnection();
		inPars.clear();
		inPars.add(dc.createDataField("cdnlavoratore", Types.VARCHAR, cdnLavoratore));
		// Controllo data inizio minore o uguale
		inPars.add(dc.createDataField("dataInizio", Types.VARCHAR, dataInizSql));

		// Controllo data fine nulla o maggiore uguale
		inPars.add(dc.createDataField("dataFine", Types.VARCHAR, dataFineSql));

		qExec.setInputParameters(inPars);
		qExec.setStatement(null);
		qExec.setTokenStatement("GET_CPI_AN_LAVORATORE_COMPETENTE_DATA");

		returned = qExec.exec();
		Object codCpi = null;
		boolean isCompetente = false;
		if (returned instanceof SourceBean) {
			SourceBean sb = (SourceBean) returned;
			Vector<SourceBean> vectCompLav = sb.getAttributeAsVector(GetStampaPercorsoLavoratore.ROW);
			if (vectCompLav.size() > 0) {
				// cerco nel record se è competente
				SourceBean sourceBean = vectCompLav.get(0);
				isCompetente = "C".equals(sourceBean.getAttribute("codmonotipocpi"));
				codCpi = sourceBean.getAttribute("cpicomp");
			}
		}
		if (!isCompetente || codCpi == null) {
			throw new GetPercorsoLavoratoreException(codiceFiscale, "07", "Lavoratore non competente");
		}
		return codCpi.toString();
	}

	/**
	 * Controlla la correttezza delle date, se non sono nulle
	 *
	 * @param codiceFiscale
	 * @param dataIniz
	 * @param datafin
	 * @throws GetPercorsoLavoratoreException
	 */
	public static void checkDate(String codiceFiscale, Date dataIniz, Date datafin)
			throws GetPercorsoLavoratoreException {
		if (dataIniz != null && datafin != null) {
			Date now = new Date();
			if (now.before(dataIniz) || now.before(datafin)) {
				// lancio errore mancanza di parametri
				throw new GetPercorsoLavoratoreException(codiceFiscale, "06", "Data errata perché futura");
			}

			if (dataIniz.after(datafin)) {
				throw new GetPercorsoLavoratoreException(codiceFiscale, "08", "Data inizio maggiore della data fine");
			}
		}
	}

}