/*
 * Created on Jun 24, 2005
 *
 */
package it.eng.sil.module.amministrazione.accorpamento;

import java.io.File;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

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
import it.eng.sil.module.cigs.bean.DatiAccorpante;
import it.eng.sil.security.User;
import it.eng.sil.util.InfoProvinciaSingleton;
import it.eng.sil.util.InfoRegioneSingleton;
import oracle.jdbc.OracleTypes;

/**
 * Modulo che esegue la cancellazione e accorpamento del lavoratore. Viene chiamata una stored procedure che provvede a
 * cio'.
 * 
 * @author savino,pegoraro
 */
public class AccorpaLavoratore extends AbstractSimpleModule {

	private static final long serialVersionUID = 13476689L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AccorpaLavoratore.class.getName());

	private static final String serviceName = "'SIL_ACC_2_SILRER'";

	private final static String XSD_ACC_WS = "lavoratori_accorpati.xsd";

	final File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd"
			+ File.separator + "accorpatolav" + File.separator + XSD_ACC_WS);

	private static final String WS_LOGON = "SELECT prgws, struserid, " + "strpassword AS cln_pwd " + "FROM   ts_ws "
			+ " WHERE  codservizio = " + serviceName + " AND codprovincia = ?";

	private static final String LOG_SELECT = "SELECT CDNLAVORATORE, CDNLAVORATOREACCORPATO, CDNUTINS,"
			+ " DTMINS, PRGLAVORATOREACCORPA, STRCODICEFISCALE, STRCODICEFISCALEACCORPATO"
			+ " FROM AN_LAVORATORE_ACCORPA WHERE CDNLAVORATORE=? AND CDNLAVORATOREACCORPATO=?";

	private static final String LOG_SELECT_ACCORPA = "SELECT LOG.PRGACCORDOACCORPA, LOG.PRGLAVORATOREACCORPA, ACC.CODACCORDO CODACCORDO"
			+ " FROM  CI_ACCORDO_ACCORPA LOG JOIN CI_ACCORDO ACC ON LOG.PRGACCORDO = ACC.PRGACCORDO WHERE PRGLAVORATOREACCORPA=?";

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		TransactionQueryExecutor accorpamentoTransaction = null;
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ArrayList<DataField> parameters = null;
		String codiceRit = null;

		ReportOperationResult resultOperation = new ReportOperationResult(this, serviceResponse);

		try {
			String cdnLavInCuiAccorpare = (String) serviceRequest.getAttribute("cdnLavInCuiAccorpare");
			String cdnLavDaAccorpare = (String) serviceRequest.getAttribute("cdnLavoratoreDaAccorpare");
			SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
			User user = (User) sessionContainer.getAttribute(User.USERID);
			// codfis necessario per segnalazione al SILRER
			String cf1 = (String) serviceRequest.getAttribute("codiceFiscaleFrom");
			String cf2 = (String) serviceRequest.getAttribute("codiceFiscaleTo");
			String encryptKey = (String) sessionContainer.getAttribute("_ENCRYPTER_KEY_");

			_logger.debug("Si sta per eseguire l'accorpamento del cdnLavoratore=" + cdnLavDaAccorpare
					+ " al cdnLavoratore=" + cdnLavInCuiAccorpare + ", che dunque verra' cancellato");

			/*
			 * La transazione serve per fare commit SOLO una volta consultato il log eventualmente facendo rollback se
			 * in LG_ACCORDO_ACCORPA ci son errori
			 */
			accorpamentoTransaction = new TransactionQueryExecutor(getPool());
			enableTransactions(accorpamentoTransaction);
			accorpamentoTransaction.initTransaction();

			conn = accorpamentoTransaction.getDataConnection();
			SourceBean statementSB = (SourceBean) getConfig().getAttribute("PROC_ACCORPA_LAVORATORE");
			String statement = statementSB.getAttribute("STATEMENT").toString();
			String sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// si impostano i parametri della procedure
			parameters = new ArrayList<DataField>(20);

			// 1.Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", OracleTypes.VARCHAR, null));
			command.setAsOutputParameters(0);

			// 2. cdnLavoratore risultato dell'accorpamento
			parameters.add(conn.createDataField("cdnLavDaAccorpare", java.sql.Types.VARCHAR, cdnLavDaAccorpare));
			command.setAsInputParameters(1);

			// 3. cdnLavoratore da accorpare e cancellare
			parameters.add(conn.createDataField("cdnLavInCuiAccorpare", java.sql.Types.VARCHAR, cdnLavInCuiAccorpare));
			command.setAsInputParameters(2);

			// 4. cdnLavoratore da accorpare e cancellare
			// Integer utModInt = new Integer(user.getCodut());
			String utente = Integer.toString(user.getCodut(), 10);
			parameters.add(conn.createDataField("cdnUtMod", java.sql.Types.VARCHAR, utente));
			command.setAsInputParameters(3);

			// 5. cdnLavoratore da accorpare e cancellare
			parameters.add(conn.createDataField("ecrypterkey", java.sql.Types.VARCHAR, encryptKey));
			command.setAsInputParameters(4);

			// 5. p_errCode
			/*
			 * parameters.add(conn.createDataField("p_errCode", java.sql.Types.VARCHAR, null));
			 * command.setAsOutputParameters(4);
			 */

			// // la procedure viene eseguita
			dr = command.execute(parameters);

			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			codiceRit = df.getObjectValue().toString();

			/* Capisc' quali accordi sono andati accorpati */
			BigDecimal prgLavoratoreAccorpante = getPrgLavoratoreAccorpante(cdnLavDaAccorpare, cdnLavInCuiAccorpare,
					conn);
			Vector<String> accordiAccorpati = getAccordiAccorpati(prgLavoratoreAccorpante, conn);

			// recupero l'anagrafica del lavoratore accorpante
			DatiAccorpante anagAccorpante = getAnagraficaAccorpante(cdnLavDaAccorpare, cdnLavInCuiAccorpare, conn);
			anagAccorpante.setPrgLavoratoreAccorpante(prgLavoratoreAccorpante);

			if (!codiceRit.equals("0")) {
				accorpamentoTransaction.rollBackTransaction();
				/*
				 * l'accorpamento e' fallito per qualche motivo il relativo codice si trova in errCode // se errCode
				 * mappa un codice in // MessageCodes.Anagrafica.AccorpaLavoratore.XXXX // lo si potrebbe usare
				 * direttamente i //
				 */
				serviceResponse.setAttribute("ESITO_ACCORPAMENTO", "FAIL");
				serviceResponse.setAttribute("ERR_CODE", codiceRit);
				if (codiceRit.equals("-2")) {
					resultOperation.reportFailure(MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_FAIL_LOGGING);
				} else if (codiceRit.equals("-3")) {
					resultOperation.reportFailure(MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_FAIL_CORSI);
				} else if (codiceRit.equals("-4")) {
					resultOperation.reportFailure(MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_FAIL_COLLOQUI);
				} else if (codiceRit.equals("-5")) {
					resultOperation.reportFailure(MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_FAIL_ISCRIZIONI);
				} else if (codiceRit.equals("-6")) {
					resultOperation.reportFailure(MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_FAIL_SAP);
				} else {
					resultOperation.reportFailure(MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_FAIL);
				}
			} else {// operazione completata con successo
				// transExec.rollBackTransaction();
				accorpamentoTransaction.commitTransaction();
				/*
				 * Invocazione WS notifica codicerit contiene i prgaccordo accorpati
				 */
				if (!sendWSNotificaAccorpato(cf1, cf2, prgLavoratoreAccorpante, accordiAccorpati, anagAccorpante)) {
					// In caso di errore il metodo invia una mail
					resultOperation.reportFailure(MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_WARN_WS);
				}
				// L'accorpamento e' comunque andato bene
				serviceResponse.setAttribute("ESITO_ACCORPAMENTO", "OK");
				resultOperation.reportSuccess(MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_SUCCESS);
				resultOperation.reportSuccess(MessageCodes.AccorpamentoLavoratore.WAR_ACCORPAMENTO_SUCCESS_INVIA_SAP);
			}
		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			if (accorpamentoTransaction != null) {
				accorpamentoTransaction.rollBackTransaction();
			}
			int messageCode = MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_FAIL;
			if (ex.getNativeException() instanceof SQLException) {
				SQLException sqlEx = (SQLException) ex.getNativeException();
				int sqlErCode = sqlEx.getErrorCode();
				if (sqlErCode == 20000 || sqlErCode == 20990) {
					messageCode = MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_FAIL_CONCORRENZA;
				} else if (sqlErCode == 2291 || sqlErCode == 2292) {
					messageCode = MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_FAIL_INTEGRITA_REF;
				}
			}
			serviceResponse.setAttribute("ESITO_ACCORPAMENTO", "FAIL");
			resultOperation.reportFailure(messageCode, ex, this.getClass().toString(), ex.getMessage());
			it.eng.sil.util.TraceWrapper.debug(_logger, "", ex.getNativeException());

		} catch (Exception e) {
			if (accorpamentoTransaction != null) {
				accorpamentoTransaction.rollBackTransaction();
			}
			serviceResponse.setAttribute("ESITO_ACCORPAMENTO", "FAIL");
			// resultOperation.reportFailure(MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_FAIL);
			resultOperation.reportFailure(MessageCodes.AccorpamentoLavoratore.ACCORPAMENTO_FAIL, e,
					this.getClass().toString(), e.getMessage());
			it.eng.sil.util.TraceWrapper.debug(_logger, "", e);

		} finally {
			Utils.releaseResources(conn, command, dr);
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

	/*
	 * Valida l'XML generato contro l'xsd da analisi ritorna null se tutto OK
	 */
	private final String getValidityErrors(String datiRichiestaXml) {
		_logger.debug("getValidityErrors(String) - start");

		try {
			String schemaLang = "http://www.w3.org/2001/XMLSchema";
			// get validation driver:
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);
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
			return returnString;
		}
		_logger.debug("getValidityErrors(String) - end");
		return null;
	}

	/**
	 * Invia l'xml al web service
	 * 
	 * @param cf1
	 * @param cf2
	 * @param prgLavAcc
	 * @param accordi
	 * @param anagAccorpante
	 * @return true se tutto va a buon fine
	 */
	private final boolean sendWSNotificaAccorpato(String cf1, String cf2, BigDecimal prgLavAcc, Vector<String> accordi,
			DatiAccorpante anagAccorpante) {
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
			_logger.warn("L'Endpoint SilNotificheLavoratore_REG" + regCode + " NON è attivo!!");
			return true;
		}

		proxy.setEndpoint(lavEp);

		// proxy.setEndpoint("http://172.28.101.21:28080/SilRerWeb/services/NotificheLavoratore");
		OperazioniLavoratore risposta = proxy.getOperazioniLavoratore();

		if (accordi.size() < 1) {
			_logger.info("Non ci sono accordi CIG da accorpare per questi lavoratori, NON notifico a SILRER");
			return true;
		}

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
				throw new Exception("Record non trovato");

		} catch (Exception e) {
			_logger.error("Impossibile trovare in TS_WS username per il servizio:" + serviceName + ",  provincia:"
					+ codprovincia, e);
			return false;
		} finally {
			if (dc != null)
				try {
					dc.close();
				} catch (EMFInternalError e) {
					e.printStackTrace();
				}
		}

		try {
			String xmlToSend = AccorpamentoXMLUtil.buildXmlNotifica(cf1, username, codprovincia, password, new Date(),
					accordi, anagAccorpante);

			String validityErrors = getValidityErrors(xmlToSend);
			if (!StringUtils.isEmptyNoBlank(validityErrors)) {
				final String validityError = "Errore di validazione xml accorpamento: " + validityErrors;
				_logger.warn("Errori nella validazione di" + schemaFile.getName() + " :" + validityError);
				_logger.debug(xmlToSend);

				throw new Exception("Accorpamento WS: validityErrors:" + validityErrors + ", datiXml:" + xmlToSend);
			}
			String returned = risposta.putNotificaAccorpamento(xmlToSend);
			_logger.debug("Valore tornato dal WS accorpamento: " + returned);

			if (returned.startsWith("-")) {
				// questo invia una mail
				_logger.error("Valore negativo tornato dal WS accorpamento: " + returned + "\n\n" + xmlToSend);
				return false;
			}

		} catch (Exception e) {
			// questo invia una mail
			_logger.error("Errore nel web service di notifica accorpamento", e);
			return false;
		}
		return true;
	}

	/**
	 * Recupera il prg del lavoratore accorpante
	 * 
	 * @param cdnlavaccorpato
	 * @param cdnlavaccorpante
	 * @param dc
	 * @return BigDecimal
	 */
	private BigDecimal getPrgLavoratoreAccorpante(String cdnlavaccorpato, String cdnlavaccorpante, DataConnection dc) {
		QueryExecutorObject qExec = new QueryExecutorObject();

		qExec.setTransactional(true);
		BigDecimal prgLavoratoreAccorpa = null;
		qExec.setDataConnection(dc);

		try {
			List<DataField> inPars = new ArrayList<DataField>();

			inPars.add(dc.createDataField("", Types.VARCHAR, cdnlavaccorpante));
			inPars.add(dc.createDataField("", Types.VARCHAR, cdnlavaccorpato));
			qExec.setInputParameters(inPars);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setStatement(LOG_SELECT);
			Object result = qExec.exec();
			if (result instanceof SourceBean) {
				SourceBean logon = (SourceBean) result;
				prgLavoratoreAccorpa = (BigDecimal) logon.getAttribute("ROW.PRGLAVORATOREACCORPA");
			}
			if (prgLavoratoreAccorpa == null)
				throw new Exception("Record in AN_LAVORATORE_ACCORPA non trovato");

		} catch (Exception e) {
			_logger.error("Eccezione nel recupero accordi accorpati dal log. Lavoratore:" + cdnlavaccorpante, e);
		}

		return prgLavoratoreAccorpa;

	}

	/**
	 * Ottiene gli accordi accorpati per comunicazione a WS
	 * 
	 * @param prgLavoratoreAccorpante
	 * @return Vettore degli accordi accorpati
	 */
	@SuppressWarnings("unchecked")
	private Vector<String> getAccordiAccorpati(BigDecimal prgLavoratoreAccorpante, DataConnection dc) {

		QueryExecutorObject qExec = new QueryExecutorObject();
		Vector<String> ret = new Vector<String>();

		qExec.setTransactional(true);
		qExec.setDataConnection(dc);

		try {

			List<DataField> inPars = new ArrayList<DataField>();
			DataField newpar = dc.createDataField("", Types.INTEGER, prgLavoratoreAccorpante);
			inPars.add(newpar);

			qExec.setInputParameters(inPars);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setStatement(LOG_SELECT_ACCORPA);

			Object result = qExec.exec();
			if (result instanceof SourceBean) {
				SourceBean logon = (SourceBean) result;
				Vector<SourceBean> tmpvect = logon.getAttributeAsVector("ROW");
				Iterator<SourceBean> it = tmpvect.iterator();
				while (it.hasNext()) {
					SourceBean tmp = it.next();
					String tmpdec = (String) tmp.getAttribute("CODACCORDO");
					ret.add(tmpdec);
				}
			}

		} catch (Exception e) {
			_logger.error("Eccezione nel recupero accordi accorpati dal log. Lavoratore:" + prgLavoratoreAccorpante, e);
		}

		return ret;
	}

	/**
	 * Ottiene le informazione di anagrafica del lavoratore accorpante
	 * 
	 * @param cdnlavaccorpato
	 * @param cdnlavaccorpante
	 * @param dc
	 * @return Lista di dati anagrafici dell'accorpante
	 */
	private DatiAccorpante getAnagraficaAccorpante(String cdnlavaccorpato, String cdnlavaccorpante, DataConnection dc) {

		final String GET_ANAGRAFICA_ACCORPANTE = "SELECT AL.STRCODICEFISCALE, " + "AL.STRNOME, " + "AL.STRCOGNOME, "
				+ "AL.STRSESSO, " + "AL.CODCOMNAS, " + "to_char(AL.DATNASC, 'yyyy-MM-dd') as DATNASC "
				+ "FROM AN_LAVORATORE_ACCORPA ANA " + "INNER JOIN AN_LAVORATORE AL "
				+ "ON ANA.CDNLAVORATORE          = AL.CDNLAVORATORE " + "WHERE ANA.CDNLAVORATORE       = ? "
				+ "AND ANA.CDNLAVORATOREACCORPATO= ?";

		// DataConnection dc = transExec.getDataConnection();
		QueryExecutorObject qExec = new QueryExecutorObject();
		DatiAccorpante ret = new DatiAccorpante();

		qExec.setTransactional(true);
		qExec.setDataConnection(dc);

		try {
			// qExec = getQueryExecutorObject();
			List<DataField> inPars = new ArrayList<DataField>();

			inPars.add(dc.createDataField("", Types.VARCHAR, cdnlavaccorpante));
			inPars.add(dc.createDataField("", Types.VARCHAR, cdnlavaccorpato));
			qExec.setInputParameters(inPars);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setStatement(GET_ANAGRAFICA_ACCORPANTE);
			Object result = qExec.exec();
			if (result instanceof SourceBean) {
				SourceBean logon = (SourceBean) result;
				String codiceFiscale = (String) logon.getAttribute("ROW.STRCODICEFISCALE");
				String nome = (String) logon.getAttribute("ROW.STRNOME");
				String cognome = (String) logon.getAttribute("ROW.STRCOGNOME");
				String sesso = (String) logon.getAttribute("ROW.STRSESSO");
				String codComNasc = (String) logon.getAttribute("ROW.CODCOMNAS");
				String datNasc = (String) logon.getAttribute("ROW.DATNASC");
				ret.setCodiceFiscale(codiceFiscale);
				ret.setNome(nome);
				ret.setCognome(cognome);
				ret.setSesso(sesso);
				ret.setComuneNascita(codComNasc);
				ret.setDataNascita(datNasc);
			}

		} catch (Exception e) {
			_logger.error("Eccezione nel recupero accordi accorpati dal log. Lavoratore:" + cdnlavaccorpante, e);
			return ret;
		}

		return ret;
	}

}