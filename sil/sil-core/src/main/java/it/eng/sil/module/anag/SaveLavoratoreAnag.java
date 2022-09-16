package it.eng.sil.module.anag;

import java.io.File;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfException;
import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.coop.webservices.operazioniLavoratore.OperazioniLavoratore;
import it.eng.sil.coop.webservices.operazioniLavoratore.OperazioniLavoratoreProxy;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.amministrazione.accorpamento.AccorpamentoXMLUtil;
import it.eng.sil.module.cigs.bean.DatiAccorpante;
import it.eng.sil.util.InfoProvinciaSingleton;
import it.eng.sil.util.InfoRegioneSingleton;

public class SaveLavoratoreAnag extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SaveLavoratoreAnag.class.getName());

	private static final String serviceName = "'SIL_ACC_2_SILRER'";

	private final static String XSD_ACC_WS = "lavoratori_accorpati.xsd";

	private static final String WS_LOGON = "SELECT prgws, struserid, " + "strpassword AS cln_pwd " + "FROM   ts_ws "
			+ "WHERE  codservizio = " + serviceName + " AND codprovincia = ?";

	public void service(SourceBean request, SourceBean response) {

		String strCodiceFiscale = (String) request.getAttribute("strCodiceFiscale");
		String strCodiceFiscaleHid = (String) request.getAttribute("strCodiceFiscaleHid");
		String strNome = (String) request.getAttribute("strNome");
		String strCognome = (String) request.getAttribute("strCognome");
		String strSesso = (String) request.getAttribute("strSesso");
		String datNasc = (String) request.getAttribute("datNasc");
		String codComNas = (String) request.getAttribute("codComNas");

		String numFigli = (String) request.getAttribute("numFigli");
		String codCittadinanzaHid = (String) request.getAttribute("codCittadinanzaHid");
		String codCittadinanza2Hid = (String) request.getAttribute("codCittadinanza2Hid");
		String codstatoCivile = (String) request.getAttribute("codstatoCivile");
		String flgMilite = (String) request.getAttribute("flgMilite");
		String strNote = (String) request.getAttribute("strNote");
		String FLGCFOK = (String) request.getAttribute("FLGCFOK");
		String user = RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_").toString();
		String cdnLavoratore = (String) request.getAttribute("cdnLavoratore");
		String numKloLavoratore = (String) request.getAttribute("numKloLavoratore");

		Object params[] = new Object[16];
		TransactionQueryExecutor transExec = null;
		Boolean updateLav = new Boolean(false);
		Boolean updatePerm = new Boolean(false);

		Boolean insertAccorpaLav = new Boolean(false);
		Boolean updateCilav = new Boolean(false);
		Boolean insertAccordoAccorpa = new Boolean(false);

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		/*
		 * commentato redmine 5204 this.setSectionQuerySelect("QUERY_CANEDITCFSAP"); SourceBean canEditCFSapsb =
		 * doSelect(request, response);
		 */

		try {
			response.delAttribute("USER_MESSAGE");
		} catch (SourceBeanException e1) {
		}

		/*
		 * commentato redmine 5204 //controllo se esiste SAP con stato Attiva/Perdita di Titolarità BigDecimal
		 * checkSapCf = (BigDecimal) canEditCFSapsb.getAttribute("ROW.CHECKSAPCF"); //posso modificare il cf solo se
		 * tale numero è pari a 0 boolean canEditCF = (checkSapCf.intValue() == 0); //se ho modificato il codice fiscale
		 * e non potevo allora salto tutto e stampo l'errore. if ((!strCodiceFiscale.equals(strCodiceFiscaleHid)) &&
		 * !canEditCF) { reportOperation.reportFailure(MessageCodes.Anag.UPDATE_FAIL_CF); return; }
		 */

		// controllo il CF solo se la combo box è su 'NO' o ''
		boolean checkCF = request.getAttribute("FLGCFOK").toString().compareTo("S") != 0;

		try {
			// se devo controllare il CF
			if (checkCF) {
				CF_utils.verificaCF(strCodiceFiscale, strNome, strCognome, strSesso, datNasc, codComNas);
			}
		} catch (CfException cfEx) {
			reportOperation.reportFailure(cfEx.getMessageIdFail());
		}
		try {
			int idFail = disableMessageIdFail();
			int idSuccess = disableMessageIdSuccess();

			transExec = new TransactionQueryExecutor(getPool(), this);
			enableTransactions(transExec);
			transExec.initTransaction();

			// Aggiornamento an_lavoratore
			// doUpdate(request, response);
			params[0] = strCodiceFiscale;
			params[1] = strCognome;
			params[2] = strNome;
			params[3] = strSesso;
			params[4] = datNasc;
			params[5] = codComNas;
			params[6] = numFigli;
			params[7] = codCittadinanzaHid;
			params[8] = codCittadinanza2Hid;
			params[9] = codstatoCivile;
			params[10] = flgMilite;
			params[11] = strNote;
			params[12] = FLGCFOK;
			params[13] = user;
			params[14] = numKloLavoratore;
			params[15] = cdnLavoratore;

			updateLav = (Boolean) transExec.executeQuery("SAVE_AN_LAVORATOREANAG", params, "UPDATE");

			// Aggiornamento am_ex_perm_sogg
			if (updateLav.booleanValue()) {
				params = new Object[5];
				boolean cittadinanzaCEE1 = false;
				boolean cittadinanzaCEE2 = false;
				params = new Object[1];

				params[0] = codCittadinanzaHid;
				SourceBean sb = (SourceBean) transExec.executeQuery("GET_FLAG_CITTADINANZA_CEE", params, "SELECT");
				cittadinanzaCEE1 = sb.containsAttribute("ROW.FLGCEE") && sb.getAttribute("ROW.FLGCEE").equals("S");

				if ((codCittadinanza2Hid != null) && !codCittadinanza2Hid.equals("")) {
					params[0] = codCittadinanza2Hid;
					sb = (SourceBean) transExec.executeQuery("GET_FLAG_CITTADINANZA_CEE", params, "SELECT");
					cittadinanzaCEE2 = sb.containsAttribute("ROW.FLGCEE") && sb.getAttribute("ROW.FLGCEE").equals("S");
				}

				params[0] = cdnLavoratore;
				SourceBean sb2 = (SourceBean) transExec.executeQuery("GET_PERM_SOGG_LAV", params, "SELECT");
				String prgPermSog = sb2.containsAttribute("ROW.prgPermSogg")
						? sb2.getAttribute("ROW.prgPermSogg").toString()
						: "";

				if ((prgPermSog != null) && !prgPermSog.equals("")) {
					if (cittadinanzaCEE1 || cittadinanzaCEE2) {
						params = new Object[3];
						params[0] = DateUtils.getNow();
						params[1] = user;
						params[2] = prgPermSog;
						updatePerm = (Boolean) transExec.executeQuery("UPDATE_AM_EX_PERM", params, "UPDATE");

						if (!updatePerm.booleanValue())
							throw new Exception("Errore nell'aggiornamento");
					}
				}
			}

			if (!updateLav.booleanValue())
				throw new Exception("Errore nell'aggiornamento");

			/*
			 * 09/09/2010 il codice fiscale è modificabile anche se il lavoratore è iscritto alla CIG deve essere
			 * inserito in AN_LAVORATORE_ACCORPA il record che traccia vecchio e nuovo CF
			 */

			Vector<SourceBean> vecAccordi = new Vector<SourceBean>();
			Vector vecprgAccordi = new Vector();
			BigDecimal prgLavAccorpa = null;
			BigDecimal prgAccordo = null;

			if ((!strCodiceFiscale.equals(strCodiceFiscaleHid))) {

				params = new Object[5];
				params[0] = cdnLavoratore;
				params[1] = cdnLavoratore;
				params[2] = strCodiceFiscale;
				params[3] = strCodiceFiscaleHid;
				params[4] = user;
				insertAccorpaLav = (Boolean) transExec.executeQuery("INSERT_AN_LAVORATORE_ACCORPA", params, "INSERT");

				if (!insertAccorpaLav.booleanValue())
					throw new Exception("Errore nell'inserimento AN_LAVORATORE_ACCORPA");
				else {
					this.setMessageIdElementDuplicate(MessageCodes.General.ELEMENT_DUPLICATED);
					this.setSectionQueryInsert("QUERY_INSERT_TS_AUT_SAP");
					this.setSectionQuerySelect("QUERY_SELECT_TS_AUT_SAP");
					boolean insInvioAutSAP = doInsertNoDuplicateNoReport(request, response);
					if (!insInvioAutSAP) {
						throw new Exception("Errore nell'inserimento TS_INVIO_AUT_SAP");
					}

					params = new Object[1];
					params[0] = cdnLavoratore;

					/*
					 * se esistono iscrizioni con accordi si deve aggiornare CI_LAVORATORE con il nuovo CF e inserire
					 * gli accordi in ci_accordo_accorpa
					 */
					SourceBean sbAccordi = (SourceBean) transExec.executeQuery("GET_PRGACCORDO_ISCR", params, "SELECT");
					vecprgAccordi = sbAccordi.getAttributeAsVector("ROW");

					if (vecprgAccordi.size() > 0) {
						params = new Object[3];
						params[0] = strCodiceFiscale;
						params[1] = user;
						params[2] = strCodiceFiscaleHid;
						updateCilav = (Boolean) transExec.executeQuery("UPDATE_CI_LAVORATORE", params, "UPDATE");

						if (!updateCilav.booleanValue())
							throw new Exception("Errore nell'aggiornamento CI_LAVORATORE");
						else {
							params = new Object[3];
							params[0] = cdnLavoratore;
							params[1] = strCodiceFiscale;
							params[2] = strCodiceFiscaleHid;

							SourceBean lavAccorpa = (SourceBean) transExec.executeQuery("GET_PRGLAVORATOREACCORPA",
									params, "SELECT");
							prgLavAccorpa = (BigDecimal) lavAccorpa.getAttribute("ROW.PRGLAVORATOREACCORPA");

							params = new Object[2];
							for (int i = 0; i < vecprgAccordi.size(); i++) {
								SourceBean rowAcc = (SourceBean) vecprgAccordi.get(i);
								prgAccordo = (BigDecimal) rowAcc.getAttribute("prgaccordo");
								params[0] = prgLavAccorpa;
								params[1] = prgAccordo;

								insertAccordoAccorpa = (Boolean) transExec.executeQuery("INSERT_CI_ACCORDO_ACCORPA",
										params, "INSERT");

								if (!insertAccordoAccorpa.booleanValue())
									throw new Exception("Errore nell'inserimento CI_ACCORDO_ACCORPA");
							}

							params = new Object[1];
							params[0] = cdnLavoratore;

							SourceBean accordi = (SourceBean) transExec.executeQuery("GET_ACCORDO_ISCR", params,
									"SELECT");
							vecAccordi = accordi.getAttributeAsVector("ROW");
						}
					}
				}
			}

			transExec.commitTransaction();
			MessageAppender.appendMessage(response, MessageCodes.General.UPDATE_SUCCESS);
			// Savino 20/4/2006: rendo conforme la response del modulo alla
			// chiamata dei metodi DoXXX()
			// sara' utilizzato dalla condizione xml del modulo di cooperazione
			// applicativa
			response.setAttribute(UPDATE_OK, "TRUE");

			/* Le iscrizioni con accordo vanno notificate al SIL Regionale tramite il web service */

			if (vecAccordi.size() > 0) {

				Vector<String> accordiStr = new Vector<String>();
				for (SourceBean temp : vecAccordi) {
					accordiStr.add(String.valueOf(temp.getAttribute("CODACCORDO")));
				}

				DatiAccorpante datiAccorpante = new DatiAccorpante();
				datiAccorpante.setCodiceFiscale(strCodiceFiscale);
				datiAccorpante.setNome(strNome);
				datiAccorpante.setCognome(strCognome);
				datiAccorpante.setSesso(strSesso);
				datiAccorpante.setComuneNascita(codComNas);

				SimpleDateFormat fa = new SimpleDateFormat("dd/MM/yyyy");
				SimpleDateFormat fb = new SimpleDateFormat("yyyy-MM-dd");
				datiAccorpante.setDataNascita(fb.format(fa.parse(datNasc)));

				datiAccorpante.setPrgLavoratoreAccorpante(prgLavAccorpa);

				if (!sendWSNotificaAscrConAccordo(strCodiceFiscaleHid, datiAccorpante, accordiStr, prgLavAccorpa))
					// In caso di errore il metodo invia una mail
					MessageAppender.appendMessage(response, MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_WARN_WS);
			} else
				_logger.info("Non ci sono iscrizioni CIG con accordi, NON notifico a SILRER");

		} catch (Exception e) {
			try {
				if (transExec != null) {
					MessageAppender.appendMessage(response, MessageCodes.General.UPDATE_FAIL);
					transExec.rollBackTransaction();
				}
			} catch (EMFInternalError ie) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "service()", (Exception) ie);

			}
		}

	}

	private final QueryExecutorObject getQueryExecutorObject() {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			return getQueryExecutorObject(ctx);
		} catch (Exception e) {
			e.printStackTrace();
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

	/* Valida l'XML generato contro l'xsd da analisi */
	private final String getValidityErrors(String datiRichiestaXml) {
		if (_logger.isDebugEnabled()) {
			_logger.debug("getValidityErrors(String) - start");
		}

		try {
			String schemaLang = "http://www.w3.org/2001/XMLSchema";

			// get validation driver:
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

			File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
					+ "xsd" + File.separator + "accorpatolav" + File.separator + XSD_ACC_WS);

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

	private final boolean sendWSNotificaAscrConAccordo(String strCodiceFiscaleOld, DatiAccorpante datiAccorpante,
			Vector<String> accordi, BigDecimal prgaLavAccorpa) {
		/*
		 * OperazioniLavoratoreProxy proxy = new OperazioniLavoratoreProxy();
		 * proxy.setEndpoint("http://172.28.101.21:28080/SilRerWeb/services/OperazioniLavoratore"); OperazioniLavoratore
		 * risposta = proxy.getOperazioniLavoratore();
		 */

		OperazioniLavoratoreProxy proxy = new OperazioniLavoratoreProxy();

		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();
		EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);

		/* Cerco l'endpoint della REGIONE di questo SIL */
		InfoRegioneSingleton reg = InfoRegioneSingleton.getInstance();
		String regCode = reg.getCodice();
		String lavEp = eps.getUrl("SilNotificheLavoratore_REG" + regCode);

		/* Se non trovo l'endpoint, tutto ok, magarisiamo in umbria */
		if (lavEp == null) {
			_logger.warn("Nessun EP definito per la comunicazione al SIL regionale");
			return true;
		}
		/* Se l'endpoint non è attivo lo stesso, torna true */
		if (!eps.isActive("SilNotificheLavoratore_REG" + regCode)) {
			_logger.warn("L'Endpoint SilNotificheLavoratore_REG" + regCode + " NON e' attivo!!");
			return true;
		}

		proxy.setEndpoint(lavEp);

		// proxy.setEndpoint("http://172.28.101.21:28080/SilRerWeb/services/NotificheLavoratore");
		OperazioniLavoratore risposta = proxy.getOperazioniLavoratore();
		// ottiene username e pass per questa provincia
		InfoProvinciaSingleton infoProvincia = InfoProvinciaSingleton.getInstance();
		String codprovincia = infoProvincia.getCodice();

		DataConnection dc = null;
		QueryExecutorObject qExec;
		String username = "", password = "";
		try {
			qExec = getQueryExecutorObject();
			List<DataField> inPars = new ArrayList<DataField>();
			dc = qExec.getDataConnection();
			// inPars.add(dc.createDataField("", Types.VARCHAR,
			// DECRYPT_PASSWORD));
			inPars.add(dc.createDataField("", Types.VARCHAR, codprovincia));
			qExec.setInputParameters(inPars);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setStatement(WS_LOGON);
			Object result = qExec.exec();
			if (result instanceof SourceBean) {
				SourceBean logon = (SourceBean) result;
				password = (String) logon.getAttribute("ROW.cln_pwd");
				username = (String) logon.getAttribute("ROW.struserid");
			}
			if (password == null || username == null)
				throw new Exception("Record non trovato in ts_ws");

		} catch (Exception e) {
			_logger.error("Impossibile trovare in TS_WS username per il servizio:" + serviceName + ",  provincia:"
					+ codprovincia, e);
			return false;
		} finally {
			if (dc != null)
				try {
					dc.close();
				} catch (EMFInternalError e) {

				}
		}

		try {
			String xmlToSend = AccorpamentoXMLUtil.buildXmlNotifica(strCodiceFiscaleOld, username, codprovincia,
					password, new Date(), accordi, datiAccorpante);

			String validityErrors = getValidityErrors(xmlToSend);
			if (!StringUtils.isEmptyNoBlank(validityErrors)) {
				final String validityError = "Errore di validazione xml iscrizione: " + validityErrors;
				_logger.warn(validityError);
				_logger.warn(xmlToSend);
				throw new Exception("Accorpamento WS: validityErrors:" + validityErrors + ", datiXml:" + xmlToSend);
			}

			String returned = risposta.putNotificaAccorpamento(xmlToSend);
			_logger.debug("Valore tornato dal WS accorpamento: " + returned);

			if (returned.startsWith("-")) {
				_logger.error("Valore negativo tornato dal WS accorpamento: " + returned + "\n\n" + xmlToSend);
				return false;
			}
		} catch (Exception e) {
			_logger.error("Errore web service notifica cambio codice fiscale", e);
			return false;
		}
		return true;

		/********************** FINE WS *****************************/
	}
}