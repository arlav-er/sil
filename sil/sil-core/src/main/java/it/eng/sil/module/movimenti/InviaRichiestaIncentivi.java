/*
 * Created on Jun 24, 2005
 *
 */
package it.eng.sil.module.movimenti;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.mail.SendMail;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.cigs.bean.BeanUtils;
import it.eng.sil.util.xml.XMLValidator;

/**
 * Modulo che esegue la l'invio del movimento via mail
 * 
 * @author sgirotti
 */
public class InviaRichiestaIncentivi extends AbstractSimpleModule {

	private static final long serialVersionUID = 13476689L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InviaRichiestaIncentivi.class.getName());

	public final static String TAG_PREFIX = "";
	public final static String TAG_RICHIESTA_INCENTIVI = TAG_PREFIX + "RichiestaIncentivi";

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		int idSuccess = this.disableMessageIdSuccess();

		String xmlGenerato = "";

		// User objUser = (User) RequestContainer.getRequestContainer()
		// .getSessionContainer().getAttribute(User.USERID);

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);

		String prgmovimentoStr = (String) serviceRequest.getAttribute("prgmovimento");
		try {

			BigDecimal prgmovimento = new BigDecimal(prgmovimentoStr);
			xmlGenerato = buildRichiestaIncentivi(prgmovimento);
			serviceResponse.setAttribute("xmlGenerato", xmlGenerato);
			String validityErrors = getValidityErrors(xmlGenerato);
			if (StringUtils.isEmptyNoBlank(validityErrors)) {
				// serviceResponse.setAttribute("xmlGenerato", xmlGenerato);
				sendRichiestaByMail(xmlGenerato, serviceRequest);
				reportOperation.reportSuccess(idSuccess);
			} else {
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				EMFUserError usrErr = new EMFUserError(EMFErrorSeverity.ERROR,
						MessageCodes.InvioMovimentiByMail.CODE_XML_VALIDATION, validityErrors);
				reportOperation.reportFailure(usrErr, "buildRichiestaIncentivi", validityErrors);
			}
		} catch (EMFUserError e) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			reportOperation.reportFailure(e, "RichiestaIncentivi", e.getDescription());
			_logger.error(e);
		} catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			reportOperation.reportFailure(e, "RichiestaIncentivi", "Errore interno");
			_logger.error(e);
		}

		// if (!sendWSMyPortal(xmlGenerato)){
		// _logger.error(MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_WARN_WS);
		// }

	}

	private void sendRichiestaByMail(String xmlGenerato, SourceBean serviceRequest) throws EMFUserError {

		// Object[] inputParameters = new Object[0];
		// SourceBean ret = (SourceBean) QueryExecutor.executeQuery(
		// "GET_RICHIESTA_INCENTIVI_PARAMETRI_MAIL", inputParameters,
		// "SELECT", Values.DB_SIL_DATI);
		SendMail mail = new SendMail();
		// String toRecipient = "Stefano.Girotti@eng.it";
		String fromRecipient = (String) serviceRequest.getAttribute("mittente");
		mail.setFromRecipient(fromRecipient);
		String toRecipient = (String) serviceRequest.getAttribute("strdestinatari");
		mail.setToRecipient(toRecipient);
		String ccRecipient = (String) serviceRequest.getAttribute("strdestcc");
		mail.setCcRecipient(ccRecipient);
		// String fromRecipient = "Stefano.Girotti@eng.it";
		String bccRecipient = (String) serviceRequest.getAttribute("strdestccn");
		mail.setBccRecipient(bccRecipient + "," + fromRecipient);
		String subject = (String) serviceRequest.getAttribute("oggetto");
		mail.setSubject(subject);
		String body = (String) serviceRequest.getAttribute("corpoMail");
		mail.setBody(body);

		// senza un mittente non si manda la mail
		if (StringUtils.isEmptyNoBlank(fromRecipient)) {
			return;
		}

		String SMTPServer = "mail.eng.it";
		Object[] inputParameters = new Object[0];
		SourceBean ret = (SourceBean) QueryExecutor.executeQuery("RecuperaSmtpServer", inputParameters, "SELECT",
				Values.DB_SIL_DATI);
		SMTPServer = (String) ret.getAttribute("ROW.smtpserver");

		mail.setSMTPServer(SMTPServer);

		// File tempDir = new File(System.getProperty("java.io.tmpdir"));
		try {
			File tmpFile = File.createTempFile(TAG_RICHIESTA_INCENTIVI, ".xml");
			Writer fw = new BufferedWriter(new FileWriter(tmpFile));
			fw.write(xmlGenerato);
			fw.close();
			mail.setAttachments(new ArrayList());
			mail.getAttachments().add(tmpFile);

		} catch (Exception e) {
			_logger.error("Errore inizializzazione del contenuto della mail", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.InvioMovimentiByMail.ERR_INVIO_MAIL,
					e.getLocalizedMessage());
		}
		try {
			mail.send();
		} catch (Exception e) {
			_logger.error("Errore Invio mail", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.InvioMovimentiByMail.ERR_INVIO_MAIL,
					e.getLocalizedMessage());
		}

	}

	private String buildRichiestaIncentivi(BigDecimal prgMovimento) throws EMFUserError, TransformerException {
		//
		//
		// MyPortalRicercaPersonaleData risposta = new
		// MyPortalRicercaPersonaleData(
		// bdRichiesta, bdAlternativa);
		// risposta.costruisci();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder parser;
		try {
			parser = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			_logger.error("Errore inizializzazione del contenuto XML della richiesta", e);
			throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.ClicLavoro.CODE_ERR_INTERNO);
		}

		// Create blank DOM Document
		Document doc = parser.newDocument();

		// Insert the root element node
		Element rootElement = doc.createElement(TAG_RICHIESTA_INCENTIVI);
		rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		// rootElement.setAttribute("xmlns:vacancy",
		// "http://regione.emilia-romagna.it/vacancy/1");
		// rootElement.setAttribute("xmlns:xsi",
		// "http://www.w3.org/2001/XMLSchema-instance");
		// rootElement.setAttribute("xsi:schemaLocation",
		// "http://regione.emilia-romagna.it/vacancy/1");
		doc.appendChild(rootElement);

		Object[] inputParameters = new Object[1];
		inputParameters[0] = prgMovimento;

		SourceBean ret = (SourceBean) QueryExecutor.executeQuery("GET_RICHIESTA_INCENTIVI", inputParameters, "SELECT",
				Values.DB_SIL_DATI);
		SourceBean row = (SourceBean) ret.getAttribute("ROW");
		BeanUtils bu = new BeanUtils(row);
		Element datLavoro = getDatoreLavoroImpresa(doc, bu);
		rootElement.appendChild(datLavoro);
		BigDecimal cdnLavoratore = bu.getBigDecimal0("cdnlavoratore");
		Element sedeLavoro = getSedeLavoro(doc, bu);
		rootElement.appendChild(sedeLavoro);

		inputParameters[0] = cdnLavoratore;
		SourceBean retLs = (SourceBean) QueryExecutor.executeQuery("GET_TITOLO_STUDIO_PRINCIPALE_RICH_INCENTIVI",
				inputParameters, "SELECT", Values.DB_SIL_DATI);
		Object rowLsObj = retLs.getAttribute("ROW");

		BeanUtils buLs = null;
		if ((rowLsObj != null) && (rowLsObj instanceof SourceBean)) {
			SourceBean rowLs = (SourceBean) rowLsObj;
			buLs = new BeanUtils(rowLs);
		}
		// Titolo di soggiorno
		/*
		 * SourceBean retTs = (SourceBean) QueryExecutor.executeQuery( "GET_TITOLO_SOGGIORNO", inputParameters,
		 * "SELECT", Values.DB_SIL_DATI); Object rowTsObj = retTs.getAttribute("ROW");
		 * 
		 * BeanUtils buTs = null; if ((rowTsObj != null) && (rowTsObj instanceof SourceBean)) { SourceBean rowTs =
		 * (SourceBean) rowTsObj; buTs = new BeanUtils(rowTs); }
		 */
		BeanUtils buTs = null;

		Element lavoratore = getLavoratore(doc, bu, buLs, buTs);
		rootElement.appendChild(lavoratore);
		Element datiLavoro = getDatiLavoro(doc, bu);
		rootElement.appendChild(datiLavoro);

		String xmlOut = null;
		xmlOut = XMLValidator.domToString(doc);
		return xmlOut;
	}

	private Element getDatiLavoro(Document doc, BeanUtils bu) {
		Element datiLavoro = doc.createElement("DatiLavoro");
		String codtipomov = bu.getObjectToString4Html("codtipomov");
		if ("AVV".equals(codtipomov)) {
			datiLavoro.appendChild(getTxtElement(doc, "TipologiaAssunzioneTrasformazione", "A"));
			datiLavoro.appendChild(getTxtElement(doc, "DataTrasformazione", "", true));
			datiLavoro.appendChild(getTxtElement(doc, "CodiceTrasformazioneSil", "", true));
			datiLavoro.appendChild(getTxtElement(doc, "DescrizioneTrasformazioneSil", "", true));
			datiLavoro
					.appendChild(getTxtElement(doc, "DataInizioRapporto", bu.getObjectToString4Html("dat_inizio_avv")));
		} else if ("TRA".equals(codtipomov)) {
			datiLavoro.appendChild(getTxtElement(doc, "TipologiaAssunzioneTrasformazione", "T"));
			datiLavoro
					.appendChild(getTxtElement(doc, "DataTrasformazione", bu.getObjectToString4Html("dat_inizio_mov")));
			datiLavoro.appendChild(
					getTxtElement(doc, "CodiceTrasformazioneSil", bu.getObjectToString4Html("codtipotrasf")));
			datiLavoro.appendChild(
					getTxtElement(doc, "DescrizioneTrasformazioneSil", bu.getObjectToString4Html("trasf_descrizione")));
			datiLavoro.appendChild(getTxtElement(doc, "DataInizioRapporto", "", true));
		}

		datiLavoro.appendChild(getTxtElement(doc, "CodiceEntePrevidenzialeSil", bu.getObjectToString4Html("codente")));
		datiLavoro.appendChild(getTxtElement(doc, "DescrizioneEntePrevidenzialeSil",
				bu.getObjectToString4Html("ente_prev_descrizione")));
		datiLavoro.appendChild(
				getTxtElement(doc, "CodiceEntePrevidenziale", bu.getObjectToString4Html("strcodiceenteprev")));
		datiLavoro.appendChild(getTxtElement(doc, "PatInail", bu.getObjectToString4Html("STRPATINAIL")));
		datiLavoro.appendChild(
				getTxtElement(doc, "CodiceTipologiaContrattualeSil", bu.getObjectToString4Html("codcontratto")));
		datiLavoro.appendChild(getTxtElement(doc, "DescrizioneTipologiaContrattualeSil",
				bu.getObjectToString4Html("t_contr_strdescrizione")));

		String flgSocio = bu.getObjectToString4Html("flgsocio");
		if ("S".equals(flgSocio)) {
			datiLavoro.appendChild(getTxtElement(doc, "SocioLavoratore", "SI"));
		} else {
			datiLavoro.appendChild(getTxtElement(doc, "SocioLavoratore", "NO"));
		}
		datiLavoro.appendChild(getTxtElement(doc, "CodiceTipoOrarioSil", bu.getObjectToString4Html("codorario")));
		datiLavoro.appendChild(
				getTxtElement(doc, "DescrizioneTipoOrarioSil", bu.getObjectToString4Html("orario_descrizione")));
		String numOreSett = bu.getObjectToString4Html("numoresett");
		datiLavoro.appendChild(getTxtElement(doc, "OreSettimanaliMedie", numOreSett, true));
		// if (StringUtils.isFilledNoBlank(numOreSett)) { }
		datiLavoro.appendChild(
				getTxtElement(doc, "QualificaProfessionaleISTAT", bu.getObjectToString4Html("CODQUALIFICASRQ")));
		datiLavoro.appendChild(getTxtElement(doc, "CodiceContrattoApplicatoSil", bu.getObjectToString4Html("codccnl")));
		datiLavoro.appendChild(getTxtElement(doc, "DescrizioneContrattoApplicatoSil",
				bu.getObjectToString4Html("contr_coll_descrizione")));
		String decRetribuzioneMen = bu.getObjectToString4Html("decretribuzionemen");
		datiLavoro.appendChild(getTxtElement(doc, "RetribuzioneCompenso", decRetribuzioneMen, true));
		// if (StringUtils.isFilledNoBlank(decRetribuzioneMen)) { }

		return datiLavoro;
	}

	private Element getLavoratore(Document doc, BeanUtils bu, BeanUtils buLs, BeanUtils buTs) {

		Element lavoratore = doc.createElement("Lavoratore");

		lavoratore.appendChild(getTxtElement(doc, "CodiceFiscale", bu.getObjectToString4Html("lav_strcodicefiscale")));
		lavoratore.appendChild(getTxtElement(doc, "Cognome", bu.getObjectToString4Html("lav_strcognome")));
		lavoratore.appendChild(getTxtElement(doc, "Nome", bu.getObjectToString4Html("lav_strnome")));
		lavoratore.appendChild(getTxtElement(doc, "Sesso", bu.getObjectToString4Html("lav_strsesso")));
		lavoratore.appendChild(getTxtElement(doc, "DataNascita", bu.getObjectToString4Html("lav_datnasc")));
		lavoratore.appendChild(getTxtElement(doc, "ComuneNascita", bu.getObjectToString4Html("lav_codcomnas")));
		lavoratore.appendChild(getTxtElement(doc, "Cittadinanza", bu.getObjectToString4Html("lav_codcittadinanza")));
		lavoratore.appendChild(getTxtElement(doc, "ComuneResidenza", bu.getObjectToString4Html("lav_codcomres")));
		lavoratore.appendChild(getTxtElement(doc, "CapResidenza", bu.getObjectToString4Html("lav_strcapres")));
		lavoratore.appendChild(
				getTxtElement(doc, "IndirizzoResidenza", bu.getObjectToString4Html("lav_strindirizzores")));
		// Livello di Studio
		String codtitolo = "";
		String strdescrizione = "";
		if (buLs != null) {
			codtitolo = buLs.getObjectToString4Html("CODTITOLO");
			strdescrizione = buLs.getObjectToString4Html("STRDESCRIZIONE");
		}
		lavoratore.appendChild(getTxtElement(doc, "CodiceTitoloIstruzioneSil", codtitolo));
		lavoratore.appendChild(getTxtElement(doc, "DescrizioneTitoloIstruzioneSil", strdescrizione));

		return lavoratore;
	}

	private Element getSedeLavoro(Document doc, BeanUtils bu) {
		Element sedeLavoro = doc.createElement("SedeLavoro");
		sedeLavoro.appendChild(getTxtElement(doc, "ComuneSedeLavoro", bu.getObjectToString4Html("s_lav_codcom")));
		sedeLavoro.appendChild(getTxtElement(doc, "CapSedeLavoro", bu.getObjectToString4Html("s_lav_strcap")));
		sedeLavoro.appendChild(
				getTxtElement(doc, "IndirizzoSedeLavoro", bu.getObjectToString4Html("s_lav_strindirizzo")));
		sedeLavoro.appendChild(getTxtElement(doc, "TelSedeLavoro", bu.getObjectToString4Html("s_lav_strtel")));
		sedeLavoro.appendChild(getTxtElement(doc, "FaxSedeLavoro", bu.getObjectToString4Html("s_lav_strfax")));
		sedeLavoro.appendChild(getTxtElement(doc, "EmailSedeLavoro", bu.getObjectToString4Html("s_lav_stremail")));

		return sedeLavoro;
	}

	private Element getDatoreLavoroImpresa(Document doc, BeanUtils bu) {
		Element datLavoro = doc.createElement("DatoreLavoroImpresa");
		datLavoro.appendChild(getTxtElement(doc, "CodiceFiscale", bu.getObjectToString4Html("strcodicefiscale")));
		datLavoro.appendChild(getTxtElement(doc, "RagioneSociale", bu.getObjectToString4Html("strragionesociale")));
		datLavoro.appendChild(
				getTxtElement(doc, "CognomeLegaleRappresentate", bu.getObjectToString4Html("COGNOME_LRA")));
		datLavoro.appendChild(getTxtElement(doc, "NomeLegaleRappresentate", bu.getObjectToString4Html("NOME_LRA")));
		datLavoro.appendChild(getTxtElement(doc, "Settore", bu.getObjectToString4Html("codateco")));
		/*
		 * String codtipoazienda = bu.getObjectToString4Html("codtipoazienda"); String pa = "NO"; if
		 * ("PA".equals(codtipoazienda)) { pa = "SI"; } datLavoro .appendChild(getTxtElement(doc,
		 * "PubblicaAmministrazione", pa));
		 */

		datLavoro.appendChild(getTxtElement(doc, "ComuneSedeLegale", bu.getObjectToString4Html("codcom"), true));
		datLavoro.appendChild(getTxtElement(doc, "CapSedeLegale", bu.getObjectToString4Html("strcap"), true));

		datLavoro.appendChild(
				getTxtElement(doc, "IndirizzoSedeLegale", bu.getObjectToString4Html("strindirizzo"), true));
		datLavoro.appendChild(getTxtElement(doc, "TelSedeLegale", bu.getObjectToString4Html("strtel"), true));
		datLavoro.appendChild(getTxtElement(doc, "FaxSedeLegale", bu.getObjectToString4Html("strfax"), true));
		datLavoro.appendChild(getTxtElement(doc, "EmailSedeLegale", bu.getObjectToString4Html("stremail"), true));
		return datLavoro;
	}

	private Element getTxtElement(Document doc, String elemName, String elemValue) {
		return getTxtElement(doc, elemName, elemValue, false);
	}

	private Element getTxtElement(Document doc, String elemName, String elemValue, boolean isNillable) {

		Element elemObj = doc.createElement(elemName);
		if (StringUtils.isEmptyNoBlank(elemValue) && isNillable) {
			elemObj.setAttribute("xsi:nil", "true");
		}
		Text node = doc.createTextNode(String.valueOf(elemValue).trim());
		elemObj.appendChild(node);
		return elemObj;
	}

	private final QueryExecutorObject getQueryExecutorObject() {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			return getQueryExecutorObject(ctx);
		} catch (Exception e) {
			_logger.error("Errore nel web service di myportal", e);
			return null;
		}
	}

	private final QueryExecutorObject getQueryExecutorObject(InitialContext ctx)
			throws NamingException, SQLException, EMFInternalError {
		Object objs = ctx.lookup(Values.JDBC_JNDI_NAME);
		DataConnection dc = null;
		QueryExecutorObject qExec;
		if (objs instanceof DataSource) {
			DataSource ds = (DataSource) objs;
			Connection conn = ds.getConnection();
			dc = new DataConnection(conn, "2", new OracleSQLMapper());
			qExec = getQueryExecutorObject(dc);
		} else {
			// _logger.error("Impossibile ottenere una connessione");
			throw new SQLException();
		}
		return qExec;
	}

	private static QueryExecutorObject getQueryExecutorObject(DataConnection dc) {
		_logger.debug("getQueryExecutorObject(DataConnection) - start");

		QueryExecutorObject qExec = new QueryExecutorObject();

		qExec.setRequestContainer(null);
		qExec.setResponseContainer(null);
		qExec.setDataConnection(dc);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setTransactional(true);
		qExec.setDontForgetException(false);

		_logger.debug("getQueryExecutorObject(DataConnection) - end");
		return qExec;
	}

	public String getValidityErrors(String datiRichiestaXml) {
		if (_logger.isDebugEnabled()) {
			_logger.debug("getValidityErrors(String) - start");
		}

		try {
			String schemaLang = "http://www.w3.org/2001/XMLSchema";

			// get validation driver:
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

			// create schema by reading it from an XSD file:
			File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
					+ "xsd" + File.separator + "RichiestaIncentivi.xsd");
			StreamSource streamSource = new StreamSource(schemaFile);
			Schema schema = factory.newSchema(streamSource);
			Validator validator = schema.newValidator();
			// at last perform validation:
			StringReader datiXmlReader = new StringReader(datiRichiestaXml);
			StreamSource datiXmlStreamSource = new StreamSource(datiXmlReader);
			validator.validate(datiXmlStreamSource);
		} catch (Exception e) {
			_logger.error("getValidityErrors(String)", e);
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