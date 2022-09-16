/**
 * 
 */
package it.eng.sil.coop.webservices.corsoCIG;

import java.io.File;
import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.util.xml.SilNamespaceContext;
import it.eng.sil.util.xml.XMLValidator;
import oracle.sql.CLOB;

/**
 * @author girotti
 * 
 */
public class CorsoCIG {

	private static final String ESITO_CORSO_CIG_XSD = "esito_corso_CIG.xsd";

	private enum TipoEvento {
	I("Inserimento"), // (SOLO ORIENTER2)
	C("Cancellazione"), // (SOLO ORIENTER2)
	R("Resoconto"); // (SOLO SIFER)

	private final String descrizione;

	// private final String descrizione; // in meters
	// public String descrizione;
	TipoEvento(String _descrizione) {
		this.descrizione = _descrizione;

	}

	}

	public static final String ROW = "ROW";
	private static final String CORSO = "corso";
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(CorsoCIG.class);
	// private static final String LISTA_ESITO_CORSO_XP = "/corso:esitoCorso";

	private static final String WS_LOGON = "SELECT prgws, strdescrizioneservizio, struserid, "
			+ "decrypt ( strpassword, ?) AS cln_pwd, codprovincia " + "FROM   ts_ws " + "WHERE  prgws IN (2, 3) "
			+ "AND struserid = ?";

	private static final String FIND_COD_ACCORDO_DEFINITIVO = "SELECT iscr.prgaltraiscr, " + "       acc.prgaccordo, "
			+ "       acc.codaccordo, " + "       lav.strcodicefiscale, "
			+ "       TO_CHAR (iscr.datinizio, 'dd/mm/YYYY') AS datinizio, "
			+ "       TO_CHAR (iscr.datfine, 'dd/mm/YYYY')   AS datfine " + "FROM   am_altra_iscr iscr "
			+ "       INNER JOIN an_lavoratore lav " + "       ON     iscr.cdnlavoratore = lav.cdnlavoratore "
			+ "       INNER JOIN ci_accordo acc " + "       ON     iscr.prgaccordo = acc.prgaccordo "
			+ "WHERE  lav.strcodicefiscale   = UPPER (?) " + "AND    acc.codaccordo         = ?";

	private static final String FIND_COD_ACCORDO_TEMP = "SELECT iscr.prgaltraiscr, " + "       iscr.prgaccordo, "
			+ "       lav.strcodicefiscale, " + "       TO_CHAR ( iscr.datinizio, 'dd/mm/YYYY' ) AS datinizio, "
			+ "       TO_CHAR ( iscr.datfine, 'dd/mm/YYYY' )   AS datfine " + "FROM   am_altra_iscr iscr "
			+ "       INNER JOIN an_lavoratore lav " + " ON     iscr.cdnlavoratore  = lav.cdnlavoratore "
			+ "WHERE  lav.strcodicefiscale = UPPER (?) " + "AND    iscr.prgaltraiscr    = ?";

	private static final String INSERT_CI_CORSO_ORIENTER = "INSERT " + "INTO   ci_corso_orienter " + "       ( "
			+ "              prgcorsoci, codrifpa, " + "              strdescrizionerifpa, strreferentecognome, "
			+ "              strreferentenome, strreferentetel, " + "              prgazienda, prgunita, "
			+ "              codsede, dtmeventoorienter, codcomsede " + "       ) " + "       VALUES " + "       ( "
			+ "              ?, ?, " + "              ?, ?, " + "              ?, ?, " + "              ?, ?, "
			+ "              ?, ?, ? " + "       )";

	private static final String INSERT_CI_CORSO = "INSERT " + "INTO   ci_corso " + "       ( "
			+ "              prgcorsoci, prgaltraiscr, " + "              datinizioprev, datfineprev, "
			+ "              datpresaincarico, datinizio, " + "              datfine, datritiro, "
			+ "              strmotivoritiro, cdnutins, " + "              dtmins, cdnutmod, "
			+ "              dtmmod, dtmevento, prgColloquio, prgPercorso  " + "       ) " + "       VALUES "
			+ "       ( " + "              ?, ?, " + "              ?, ?, " + "              ?, ?, "
			+ "              ?, ?, " + "              ?, ?, " + "              SYSDATE, ?, "
			+ "              SYSDATE, ?,?, ? " + "       )";

	private static final String FIND_AZIENDA_ENTE_W_SEDE = "SELECT az.strcodicefiscale, " + "       az.prgazienda, "
			+ "       (SELECT uaz.prgunita " + "       FROM    an_unita_azienda uaz "
			+ "       WHERE   uaz.prgazienda = az.prgazienda " + "       AND     uaz.codcom = ? "
			+ "       AND     ROWNUM = 1) AS prgunita, " + "       (SELECT NVL(MAX(uaz.prgunita), 0) "
			+ "       FROM    an_unita_azienda uaz " + "       WHERE   uaz.prgazienda = az.prgazienda) AS maxprgunita "
			+ "FROM   an_azienda az " + "WHERE  az.strcodicefiscale = ?";

	private static final String INSERT_ENTE_AN_AZIENDA = "INSERT " + "INTO   an_azienda " + "       ( "
			+ "              prgazienda, strcodicefiscale, " + "              strragionesociale, codtipoazienda, "
			+ "              cdnutins, dtmins, " + "              cdnutmod, dtmmod " + "       ) " + "       VALUES "
			+ "       ( " + "              ?, ?, " + "              ?, ?, " + "              ?, SYSDATE, "
			+ "              ?, SYSDATE " + "       )";
	private static final String INSERT_SEDE_ENTE_AN_UNITA_AZIENDA = "INSERT " + "INTO   an_unita_azienda " + "       ( "
			+ "              prgazienda, prgunita, " + "              strindirizzo, codcom, "
			+ "              codazstato, codateco, " + "              cdnutins, dtmins, "
			+ "              cdnutmod, dtmmod,flgsede " + "       ) " + "       VALUES " + "       ( "
			+ "              ?, ?, " + "              ?, ?, " + "              ?, ?, " + "              ?, SYSDATE, "
			+ "              ?, SYSDATE,'N' " + "       )";
	private static final String FIND_COD_CORSO_ORIENTER = "SELECT c.prgcorsoci, " + "       c.dtmevento, "
			+ "       c.dtmcancellazione, " + "       co.dtmeventoorienter " + "FROM   ci_corso c "
			+ "       INNER JOIN ci_corso_orienter co " + "       ON     c.prgcorsoci = co.prgcorsoci "
			+ "WHERE  co.codrifpa         = ? " + "AND    co.codsede          = ? " + "AND    c.prgaltraiscr      = ?";

	private static final String FIND_COD_CORSO_CATALOGO = "SELECT c.prgcorsoci, c.dtmevento, c.dtmcancellazione "
			+ "FROM   ci_corso c " + "       INNER JOIN ci_corso_catalogo cc " + " ON c.prgcorsoci = cc.prgcorsoci "
			+ "WHERE  cc.numidproposta = ? " + "AND    cc.numrecid      = ? " + "AND    c.prgaltraiscr   = ?";

	private static final String UPDATE_CORSO_CANCELLAZIONE = "UPDATE ci_corso " + "SET   "
			+ "       PRGCOLLOQUIO           = ?, " + "       PRGPERCORSO     		 = ?, "
			+ "       CDNUTMOD               = ?, " + "       DTMMOD        		 = sysdate, "
			+ "       cdnutentecancellazione = ?, " + "       dtmcancellazione       = sysdate, " + "	dtmevento = ?"
			+ "WHERE  prgcorsoci             = ?";
	private static final String UPDATE_CORSO_RESOCONTO = "UPDATE ci_corso " + "SET    datinizioprev          = ?, "
			+ "       datfineprev            = ?, " + "       datpresaincarico       = ?, "
			+ "       datinizio              = ?, " + "       datfine                = ?, "
			+ "       datritiro              = ?, " + "       strmotivoritiro        = ?, "
			+ "       CDNUTMOD               = ?, " + "       DTMMOD        		 = sysdate, "
			+ "       DTMEVENTO              = ? " + "WHERE  prgcorsoci             = ?";
	private static final String DECRYPT_PASSWORD = "";
	private static final String INSERT_CI_CORSO_CATALOGO = "INSERT " + "INTO   ci_corso_catalogo " + "       ( "
			+ "              prgcorsoci, numidproposta, numrecid " + "       ) " + "       VALUES " + "       ( "
			+ "              ?, ?, ? " + "       )";

	private static final String INSERT_ERR_LOG = "INSERT " + "INTO   ts_ws_err " + "       ( "
			+ "              prgwserr, dtmelaborazione, " + "              codmonotipo, strcodicecomunicazione, "
			+ "              strsqlcode, strsqlerrm, " + "              txttracciatoxml " + "       ) "
			+ "       VALUES " + "       ( " + "              s_ts_ws_err.NEXTVAL, SYSDATE, " + "              ?, ?, "
			+ "              ?, ?, " + "              ? " + "       )";
	private static final String COD_MONO_TIPO = "eC";
	private static final String CODICE_COMUNICAZIONE = "N/A";
	private static final String INSERT_LOG_COMUNICAZIONE = "INSERT INTO TS_WS_ERR"
			+ " ( PRGWSERR,DTMELABORAZIONE,CODMONOTIPO,STRCODICECOMUNICAZIONE,TXTTRACCIATOXML,STRSQLCODE,STRSQLERRM ) VALUES"
			+ " ( ?,SYSDATE,?,?,EMPTY_CLOB(),?,? )";

	private Integer codUtenteDefault = 190;
	private boolean isLoginOrienter2 = false;
	private boolean isLoginSIFER = false;
	private String userName;
	private String password;
	private boolean isCorsoCatalogo;
	private boolean isCorsoOrienter;

	// public void esitoCorsoCIGService(String userName, String password, String
	// datiXml) {
	// try {
	// esitoCorsoCIGService(userName, password, datiXml);
	// } catch (Exception e) {
	// }
	// }

	public void corsoCIGService(String userName, String password, String datiXml) throws Exception {
		this.userName = userName;
		this.password = password;
		if (logger.isDebugEnabled()) {
			log2DB(COD_MONO_TIPO, CODICE_COMUNICAZIONE, ESITO_CORSO_CIG_XSD, "deBugOnly", datiXml);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("corsoCIGService(String, String, String) - start");
		}
		final String strError = "Impossibile autenticare userName:" + userName + ",  password:" + password;
		try {
			if (isAutenticazioneValida(userName, password)) {
				logger.debug("Autenticato con userName:" + userName + ",  password:" + password);
			} else {
				logger.warn(strError);
				log2DB(COD_MONO_TIPO, CODICE_COMUNICAZIONE, ESITO_CORSO_CIG_XSD, strError, datiXml);
				throw new Exception(strError);
				// return;
			}
		} catch (Exception e) {
			logger.warn(strError, e);
			log2DB(COD_MONO_TIPO, CODICE_COMUNICAZIONE, ESITO_CORSO_CIG_XSD,
					strError + " Exception:" + e.getLocalizedMessage(), datiXml);
			throw e;
			// throw new Exception(strError);
			// return;
		}

		String validityErrors = getValidityErrors(datiXml);
		if (!StringUtils.isEmptyNoBlank(validityErrors)) {
			final String validityError = "Errore di validazione xml: " + validityErrors;
			logger.warn(validityError);
			logger.warn(datiXml);
			log2DB(COD_MONO_TIPO, CODICE_COMUNICAZIONE, ESITO_CORSO_CIG_XSD, validityError, datiXml);
			throw new Exception("validityErrors:" + validityErrors + ", datiXml:" + datiXml);
		}

		try {
			Document document = XMLValidator.parseXmlFile(datiXml);
			processEsitoCorso(document.getDocumentElement());

		} catch (Exception e) {
			logger.warn("corsoCIGService(String, String, String) datiXml:" + datiXml, e);
			log2DB(COD_MONO_TIPO, CODICE_COMUNICAZIONE, ESITO_CORSO_CIG_XSD, e.getLocalizedMessage(), datiXml);
			throw e;
			// e.printStackTrace();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("corsoCIGService(String, String, String) - end");
		}
	}

	private void log2DB(String _codmonotipo, String _strcodicecomunicazione, String esitoCorsoCigXsd, String strError,
			String datiXml) {
		if (logger.isDebugEnabled()) {
			logger.debug("log2DB(String, String, String, String, String) - start");
		}

		try {
			logger.error("codmonotipo:" + _codmonotipo + ", strcodicecomunicazione:" + _strcodicecomunicazione
					+ ", esitoCorsoCigXsd:" + esitoCorsoCigXsd + ", strError:" + strError + ", datiXml:" + datiXml);
			logVersoDB(_codmonotipo, _strcodicecomunicazione, esitoCorsoCigXsd, strError, datiXml);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.warn("codmonotipo:" + _codmonotipo + ", strcodicecomunicazione:" + _strcodicecomunicazione
					+ ", esitoCorsoCigXsd:" + esitoCorsoCigXsd + ", strError:" + strError + ", datiXml:" + datiXml, e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("log2DB(String, String, String, String, String) - end");
		}
	}

	@Deprecated
	private static void log2DB(String _codmonotipo, String _strcodicecomunicazione, String codError, String strError,
			String datiXml, QueryExecutorObject qExec) {
		if (logger.isDebugEnabled()) {
			logger.debug("log2DB(String, String, String, String, String, QueryExecutorObject) - start");
		}

		DataConnection dc = null;
		try {
			List<DataField> inPars = new ArrayList<DataField>();
			dc = qExec.getDataConnection();
			inPars.add(dc.createDataField("codmonotipo", Types.VARCHAR, _codmonotipo));
			inPars.add(dc.createDataField("strcodicecomunicazione", Types.VARCHAR, _strcodicecomunicazione));
			inPars.add(dc.createDataField("strsqlcode", Types.VARCHAR, codError));
			inPars.add(dc.createDataField("strsqlerrm", Types.VARCHAR, strError));
			inPars.add(dc.createDataField("txttracciatoxml", Types.VARCHAR, datiXml));
			qExec.setInputParameters(inPars);
			qExec.setType(QueryExecutorObject.INSERT);
			qExec.setDontForgetException(true);
			qExec.setStatement(INSERT_ERR_LOG);
			Object returned = qExec.exec();
			String errorString = "Inserimento log Db Fallito";
			if (!Boolean.TRUE.equals(returned)) {
				logger.warn(errorString);
				if (returned instanceof Exception) {
					Exception exc = (Exception) returned;
					logger.warn("Exception:" + exc.getLocalizedMessage());
				}
			}
		} finally {
			if (dc != null)
				try {
					// dc.commitTransaction();
					dc.close();
				} catch (EMFInternalError e) {
					logger.warn("log2DB(String, String, String, String, String, QueryExecutorObject)", e);
					// e.printStackTrace();
				}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("log2DB(String, String, String, String, String, QueryExecutorObject) - end");
		}
	}

	public void logVersoDB(String codMonoTipo, String codComunicazione, String strsqlcode, String strsqlerrm,
			String txtTracciatoXml) throws Exception {
		// String _codmonotipo, String _strcodicecomunicazione, String codError,
		// String strError, String datiXml,
		Writer outStream = null;
		TransactionQueryExecutor transExec = null;

		transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);

		transExec.initTransaction();

		try {
			SourceBean row = (SourceBean) transExec.executeQuery("GET_NEXT_PRGWSERR", new Object[] {}, "SELECT");

			// Recupero prgWsErr
			BigDecimal prgWsErr = (BigDecimal) row.getAttribute("ROW.PRGWSERR");

			if (strsqlerrm == null)
				strsqlerrm = "";

			transExec
					.executeQueryByStringStatement(INSERT_LOG_COMUNICAZIONE,
							new Object[] { prgWsErr, codMonoTipo, codComunicazione, strsqlcode,
									(strsqlerrm.length() < 1000) ? strsqlerrm : strsqlerrm.substring(0, 1000) },
							"INSERT");

			row = (SourceBean) transExec.executeQuery("WRITE_CLOB_COMUNICAZIONE", new Object[] { prgWsErr }, "SELECT");
			CLOB resultClob = (CLOB) row.getAttribute("ROW.TXTTRACCIATOXML");

			outStream = resultClob.getCharacterOutputStream();

			int chunk = resultClob.getChunkSize();

			String strMessage = txtTracciatoXml;
			String subStr = "";

			while (strMessage.length() > chunk) {
				subStr = strMessage.substring(0, chunk);
				strMessage = strMessage.substring(chunk - 1);
				outStream.write(subStr, 0, subStr.length());
			}
			outStream.write(strMessage, 0, strMessage.length());

			outStream.flush();

			transExec.commitTransaction();

		} catch (Exception e) {
			try {
				if (transExec != null)
					transExec.rollBackTransaction();
			} catch (EMFInternalError e1) {
				throw new Exception("DBErrorLogger: problema con la rollback");
			}
			throw e;
		}
	}

	private boolean isAutenticazioneValida(String userName, String password) throws Exception {
		// Controllare autenticazione - by db!
		DataConnection dc = null;
		QueryExecutorObject qExec;
		try {
			qExec = getQueryExecutorObject();
			List<DataField> inPars = new ArrayList<DataField>();
			dc = qExec.getDataConnection();
			inPars.add(dc.createDataField("", Types.VARCHAR, DECRYPT_PASSWORD));
			inPars.add(dc.createDataField("", Types.VARCHAR, userName));
			qExec.setInputParameters(inPars);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setStatement(WS_LOGON);
			Object result = qExec.exec();
			if (result instanceof SourceBean) {
				SourceBean logon = (SourceBean) result;
				Object cln_pwd = logon.getAttribute("ROW.cln_pwd");
				Object prgws = logon.getAttribute("ROW.prgws");
				if (password.equals(cln_pwd)) {
					// login valido
					// stabilisco di chi!
					isLoginOrienter2 = "2".equalsIgnoreCase(prgws.toString());
					isLoginSIFER = "3".equalsIgnoreCase(prgws.toString());
				}
			}
			if (isLoginOrienter2 || isLoginSIFER) {
				logger.debug("Autenticato con userName:" + userName + ",  password:" + password);
				return true;
			} else {
				logger.error("Impossibile autenticare userName:" + userName + ",  password:" + password);
				return false;
			}
		} catch (Exception e) {
			logger.error("Impossibile autenticare userName:" + userName + ",  password:" + password, e);
			// return false;
			throw e;
		} finally {
			if (dc != null)
				try {
					dc.close();
				} catch (EMFInternalError e) {
					// e.printStackTrace();
				}
		}
	}

	private void processEsitoCorso(Element esitoCorso) throws Exception {
		// esitoCorso
		XP xp = new XP(esitoCorso);
		String tipoEventoStr = xp.getText(xp.tipoEventoXPathExp);
		TipoEvento tipoEvento = TipoEvento.valueOf(tipoEventoStr);
		switch (tipoEvento) {
		case I:
			if (isLoginOrienter2) {
				processEsitoCorsoInserimento(esitoCorso);
			} else {
				final String errore = "Operazione di Inserimento non permessa con queste credenziali userName:"
						+ userName + ",  password:" + password;
				logger.warn(errore);
				throw new Exception(errore);
			}
			break;
		case C:
			if (isLoginOrienter2) {
				processEsitoCorsoCancellazione(esitoCorso);
			} else {
				final String errore = "Operazione di Cancellazione non permessa con queste credenziali userName:"
						+ userName + ",  password:" + password;
				logger.warn(errore);
				throw new Exception(errore);
			}
			break;
		case R:
			if (isLoginSIFER) {
				processEsitoCorsoResoconto(esitoCorso);
			} else {
				final String errore = "Operazione di Resoconto non permessa con queste credenziali userName:" + userName
						+ ",  password:" + password;
				logger.warn(errore);
				throw new Exception(errore);
			}
			break;

		default:
			throw new UnsupportedOperationException("Non gestito tipoEvento:" + tipoEvento);
		// break;
		}

	}

	private void processEsitoCorsoResoconto(Element esitoCorso) throws Exception {
		XP xp = new XP(esitoCorso);
		Ente ente = parseEnte(xp);
		Corso corso = parseCorso(xp);
		EventoCorsoOrienter evCorOr = parseEventoCorsoOrienter(xp);
		String codiceFiscaleLavoratore = xp.getText(xp.codiceFiscaleLavoratoreXPathExp);
		String accordoCrisiCodice = xp.getText(xp.accordoCrisiCodiceXPathExp);
		String datiParsed = "codiceFiscale:" + codiceFiscaleLavoratore + ", accordoCrisiCodice:" + accordoCrisiCodice
				+ ", corso:" + corso + ", eventoCorsoOrienter:" + evCorOr + ", ente:" + ente;
		if (corso.datPresaInCarico == null && corso.datInizio == null && corso.datFine == null
				&& corso.datInizioPrev == null && corso.datFinePrev == null && corso.datRitiro == null
		// && StringUtils.isEmptyNoBlank(corso.strMotivoRitiro)
		) {
			throw new Exception("Nessuna data Presente! " + datiParsed);
		}
		String errorString = null;
		controllaCodiciCorso(corso, evCorOr, datiParsed);

		QueryExecutorObject qExec = getQueryExecutorObject();
		DataConnection dc = null;
		try {
			dc = qExec.getDataConnection();
			dc.initTransaction();
			SourceBean lav = getLavoratore(codiceFiscaleLavoratore, accordoCrisiCodice, qExec, datiParsed);
			BigDecimal prgaltraiscr = (BigDecimal) lav.getAttribute("prgaltraiscr");
			Vector<SourceBean> vCorsi;
			vCorsi = getVectorCorsi(qExec, datiParsed, corso, evCorOr, prgaltraiscr);
			// List<BigDecimal> lPkCorsi2canc = new ArrayList<BigDecimal>();
			BigDecimal prgCorsoCi = null;
			BigDecimal prgCorsoCi2Res = null;
			Date dtmEvento2Res = null;
			Object dtmcancellazione = null;
			Date eventoCorrente = corso.dtmEvento;
			errorString = "Corso non trovato! " + datiParsed;
			if (vCorsi.isEmpty()) {
				throw new Exception(errorString);
			} else {
				// devo resocontare l'ultimo corso che non è già cancellato
				// se non c'è un resoconto precedente
				for (SourceBean corsoSb : vCorsi) {
					logger.debug("corsoSb2" + corsoSb);
					dtmcancellazione = corsoSb.getAttribute("dtmcancellazione");
					Date dtmevento = (Date) corsoSb.getAttribute("dtmevento");
					// Date dtmeventoorienter = (Date)
					// corsoSb2.getAttribute("dtmeventoorienter");
					prgCorsoCi = (BigDecimal) corsoSb.getAttribute("prgCorsoCi");
					if (dtmcancellazione == null) {
						if (dtmEvento2Res == null) {
							if (eventoCorrente.after(dtmevento)) {
								prgCorsoCi2Res = prgCorsoCi;
								dtmEvento2Res = dtmevento;
							} else {
								logger.info("Trovato un resoconto successivo - scarto il resoconto attuale");
								return;
							}
						} else if (dtmEvento2Res.before(dtmevento) && eventoCorrente.after(dtmevento)) {
							prgCorsoCi2Res = prgCorsoCi;
							dtmEvento2Res = dtmevento;
						}
					}
				}
			}

			// Controllare che ci sia il corso indicato
			// a questo punto non dovrebbe mai mancare!
			if (prgCorsoCi2Res == null) {
				logger.warn(errorString);
				throw new Exception(errorString);
			}

			List<DataField> inPars = new ArrayList<DataField>();
			inPars.clear();
			qExec.setInputParameters(inPars);
			qExec.setType(QueryExecutorObject.UPDATE);

			String updCorsoDynPref = "UPDATE ci_corso SET ";
			// + "SET datinizioprev = ?, "
			// + " datfineprev = ?, " +
			// " datpresaincarico = ?, "
			// + " datinizio = ?, " +
			// " datfine = ?, "
			// + " datritiro = ?, " +

			String updCorsoDynPost = ",       strmotivoritiro        = ?, " + "       CDNUTMOD               = ?, "
					+ "       DTMMOD        		 = sysdate, " + "       DTMEVENTO              = ? "
					+ "WHERE  prgcorsoci             = ?";

			String updCorsoDyn_ = "UPDATE ci_corso " + "SET    datinizioprev          = ?, "
					+ "       datfineprev            = ?, " + "       datpresaincarico       = ?, "
					+ "       datinizio              = ?, " + "       datfine                = ?, "
					+ "       datritiro              = ?, " + "       strmotivoritiro        = ?, "
					+ "       CDNUTMOD               = ?, " + "       DTMMOD        		 = sysdate, "
					+ "       DTMEVENTO              = ? " + "WHERE  prgcorsoci             = ?";

			StringBuilder sb = new StringBuilder();
			sb.append(updCorsoDynPref);
			Map<String, Date> name2Date = new HashMap<String, Date>();
			Map<String, Date> name2DatenotNull = new HashMap<String, Date>();

			// Prendo tutte le date
			name2Date.put("datpresaincarico", corso.datPresaInCarico);
			name2Date.put("datinizioprev", corso.datInizioPrev);
			name2Date.put("datfineprev", corso.datFinePrev);
			name2Date.put("datinizio", corso.datInizio);
			name2Date.put("datfine", corso.datFine);
			name2Date.put("datritiro", corso.datRitiro);

			// elimino le date nulle
			Iterator<String> it = name2Date.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				Date value = name2Date.get(key);
				if (value != null) {
					name2DatenotNull.put(key, value);
				}
			}

			// Compilo la query Dinamica ed i relativi parametri
			Iterator<String> itnn = name2DatenotNull.keySet().iterator();
			while (itnn.hasNext()) {
				String key = itnn.next();
				Date value = name2DatenotNull.get(key);
				name2DatenotNull.put(key, value);
				sb.append(key);
				sb.append(" = ? ");
				if (itnn.hasNext()) {
					sb.append(", ");
				}
				inPars.add(dc.createDataField(key, Types.TIMESTAMP, getTSorNull(value)));
			}
			sb.append(updCorsoDynPost);
			inPars.add(dc.createDataField("strmotivoritiro", Types.VARCHAR, corso.strMotivoRitiro));
			inPars.add(dc.createDataField("CDNUTMOD", Types.NUMERIC, corso.cdnUtenteOp));
			inPars.add(dc.createDataField("DTMEVENTO", Types.TIMESTAMP, getTSorNull(eventoCorrente)));
			inPars.add(dc.createDataField("prgcorsoci", Types.NUMERIC, prgCorsoCi2Res));
			logger.debug("Query di update generata:");
			String updCorsoDyn = sb.toString();
			qExec.setStatement(updCorsoDyn);
			logger.debug(updCorsoDyn);

			errorString = "Corso non Aggiornato " + datiParsed;
			Object returned = qExec.exec();
			if (!Boolean.TRUE.equals(returned)) {
				logger.warn(errorString);
				throw new Exception(errorString);
			}
			dc.commitTransaction();
			return;
		} catch (Exception e) {
			if (dc != null) {
				dc.rollBackTransaction();
			}
			throw e;
		} finally {
			if (dc != null) {
				dc.close();
			}

		}
	}

	private Vector<SourceBean> getVectorCorsi(QueryExecutorObject qExec, String datiParsed, Corso corso,
			EventoCorsoOrienter evCorOr, BigDecimal prgaltraiscr) throws Exception {
		Vector<SourceBean> vCorsi;
		if (isCorsoOrienter) {
			vCorsi = getVectorCorsiOrienter(qExec, datiParsed, prgaltraiscr, evCorOr.codRifPA, evCorOr.codSede);
		} else {
			vCorsi = getVectorCorsiCatalogo(qExec, datiParsed, prgaltraiscr, corso);
		}
		return vCorsi;
	}

	@SuppressWarnings("unchecked")
	private Vector<SourceBean> getVectorCorsiCatalogo(QueryExecutorObject qExec, String datiParsed,
			BigDecimal prgaltraiscr, Corso _corso) throws Exception {
		List<DataField> inPars = new ArrayList<DataField>();
		// BigDecimal prgaltraiscr = (BigDecimal)
		// lav.getAttribute("prgaltraiscr");
		DataConnection dc = qExec.getDataConnection();
		inPars.add(dc.createDataField("", Types.NUMERIC, _corso.numIdProposta));
		inPars.add(dc.createDataField("", Types.NUMERIC, _corso.numRecId));
		inPars.add(dc.createDataField("prgaltraiscr", Types.NUMERIC, prgaltraiscr));
		qExec.setInputParameters(inPars);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(FIND_COD_CORSO_CATALOGO);

		Object returned = qExec.exec();
		String errorString = "Corso non trovato " + datiParsed;
		if (returned instanceof SourceBean) {
			SourceBean sb = (SourceBean) returned;
			Vector<SourceBean> vCorsi = sb.getAttributeAsVector(ROW);
			if (vCorsi == null) {
				logger.warn(errorString);
				throw new Exception(errorString);
			} else
				return vCorsi;
		}
		logger.warn(errorString);
		throw new Exception(errorString);
	}

	private void controllaCodiciCorso(Corso corso, EventoCorsoOrienter evCorOr, String datiParsed) throws Exception {
		String errorString;
		// isNotCorsoOrienter = StringUtils.isEmptyNoBlank(evCorOr.codRifPA);
		// isNotCorsoCatalogo = corso.numIdProposta == null;
		isCorsoOrienter = !StringUtils.isEmptyNoBlank(evCorOr.codRifPA);
		isCorsoCatalogo = corso.numIdProposta != null;
		if (isCorsoCatalogo || isCorsoOrienter) {
			logger.debug("Abbiamo un corso");
		} else {
			errorString = "Nessun Codice Corso Presente, " + datiParsed;
			logger.warn(errorString);
			throw new Exception(errorString);
		}
	}

	private void processEsitoCorsoCancellazione(Element esitoCorso) throws Exception {
		XP xp = new XP(esitoCorso);
		Ente ente = parseEnte(xp);
		Corso corso = parseCorso(xp);
		EventoCorsoOrienter evCorOr = parseEventoCorsoOrienter(xp);
		String codiceFiscaleLavoratore = xp.getText(xp.codiceFiscaleLavoratoreXPathExp);
		String accordoCrisiCodice = xp.getText(xp.accordoCrisiCodiceXPathExp);
		String datiParsed = "codiceFiscale:" + codiceFiscaleLavoratore + ", accordoCrisiCodice:" + accordoCrisiCodice
				+ ", corso:" + corso + ", eventoCorsoOrienter:" + evCorOr + ", ente:" + ente;
		// String datiParsed = " corso:" + corso + ", eventoCorsoOrienter:" +
		// evCorOr + ", ente:" + ente;
		String errorString;
		controllaCodiciCorso(corso, evCorOr, datiParsed);
		QueryExecutorObject qExec = getQueryExecutorObject();
		DataConnection dc = null;
		try {
			dc = qExec.getDataConnection();
			dc.initTransaction();
			SourceBean lav = getLavoratore(codiceFiscaleLavoratore, accordoCrisiCodice, qExec, datiParsed);
			BigDecimal prgaltraiscr = (BigDecimal) lav.getAttribute("prgaltraiscr");
			Vector<SourceBean> vCorsi = getVectorCorsi(qExec, datiParsed, corso, evCorOr, prgaltraiscr);
			// List<BigDecimal> lPkCorsi2canc = new ArrayList<BigDecimal>();
			BigDecimal prgCorsoCi = null;
			BigDecimal prgCorsoCi2Del = null;
			Date dtmEventoOrienter2Del = null;
			Object dtmcancellazione = null;
			Date eventoCorrente = corso.dtmEvento;
			if (vCorsi.isEmpty()) {
				// controllo e nel caso inserisco l'ente
				controllaInserisciEnte(ente, evCorOr, corso, datiParsed, qExec);
				// devo inserire il corso - lo metto attivo
				prgCorsoCi = inserisciCorso(prgaltraiscr, ente, corso, evCorOr, datiParsed, qExec);
				prgCorsoCi2Del = prgCorsoCi;
			} else {
				// devo cancellare l'ultimo corso che non è già cancellato
				for (SourceBean corsoSb : vCorsi) {
					logger.debug("corsoSb2" + corsoSb);
					dtmcancellazione = corsoSb.getAttribute("dtmcancellazione");
					// Date dtmevento = (Date)
					// corsoSb.getAttribute("dtmevento");
					// Date dtmeventoorienter = (Date)
					// corsoSb.getAttribute("dtmeventoorienter");
					Date dtmevento = (Date) corsoSb.getAttribute("dtmevento");
					prgCorsoCi = (BigDecimal) corsoSb.getAttribute("prgCorsoCi");
					if (!dtmevento.after(eventoCorrente) && dtmcancellazione == null) {
						if (dtmEventoOrienter2Del == null) {
							prgCorsoCi2Del = prgCorsoCi;
							dtmEventoOrienter2Del = dtmevento;
						} else if (dtmEventoOrienter2Del.before(dtmevento)) {
							prgCorsoCi2Del = prgCorsoCi;
							dtmEventoOrienter2Del = dtmevento;
						}
						// lPkCorsi2canc.add(prgCorsoCi);
					}
				}
				// se arrivo qui
				// nessun corso ancora attivo
				// lo inserisco
				if (prgCorsoCi2Del == null) {
					// controllo e nel caso inserisco l'ente
					// non dovrebbe servire a questo punto...
					controllaInserisciEnte(ente, evCorOr, corso, datiParsed, qExec);
					// devo inserire il corso - lo metto attivo
					prgCorsoCi = inserisciCorso(prgaltraiscr, ente, corso, evCorOr, datiParsed, qExec);
					prgCorsoCi2Del = prgCorsoCi;
				}
			}

			// Controllare che ci sia il corso indicato
			errorString = "Corso non trovato " + datiParsed;
			// if (prgCorsoCi == null || prgCorsoOrienter == null) {
			if (prgCorsoCi2Del == null) {
				logger.warn(errorString);
				throw new Exception(errorString);
			}
			// if (dtmcancellazione == null) {
			// // corso ancora attivo
			// logger.debug("Canzellazione corso prgCorsoCi:" + prgCorsoCi);
			// } else {
			// // corso già cancellato
			// logger.warn("corso prgCorsoCi:" + prgCorsoCi +
			// " già cancellato? data cancellazione:"
			// + dtmcancellazione);
			// }
			List<DataField> inPars = new ArrayList<DataField>();
			// for (BigDecimal prgCorsoCi2 : lPkCorsi2canc) {
			inPars.clear();
			qExec.setInputParameters(inPars);
			qExec.setType(QueryExecutorObject.UPDATE);
			inPars.add(dc.createDataField("PRGCOLLOQUIO", Types.NULL, null));
			inPars.add(dc.createDataField("PRGPERCORSO", Types.NULL, null));
			inPars.add(dc.createDataField("CDNUTMOD", Types.NUMERIC, corso.cdnUtenteOp));
			inPars.add(dc.createDataField("cdnUtenteCancellazione", Types.NUMERIC, corso.cdnUtenteOp));
			inPars.add(dc.createDataField("dtmevento", Types.TIMESTAMP, getTSorNull(eventoCorrente)));
			inPars.add(dc.createDataField("prgcorsoci", Types.NUMERIC, prgCorsoCi2Del));
			qExec.setStatement(UPDATE_CORSO_CANCELLAZIONE);

			errorString = "Corso non Aggiornato " + datiParsed;
			Object returned = qExec.exec();
			if (!Boolean.TRUE.equals(returned)) {
				logger.warn(errorString);
				throw new Exception(errorString);
			}
			// }

			dc.commitTransaction();
			return;
		} catch (Exception e) {
			if (dc != null) {
				dc.rollBackTransaction();
			}
			throw e;
		} finally {
			if (dc != null) {
				dc.close();
			}

		}
	}

	private BigDecimal inserisciCorso(BigDecimal prgaltraiscr, Ente ente, Corso corso, EventoCorsoOrienter evCorOr,
			String datiParsed, QueryExecutorObject qExec) throws Exception {
		String errorString = null;

		List<DataField> inPars = new ArrayList<DataField>();
		inPars.clear();
		DataConnection dc = qExec.getDataConnection();
		BigDecimal prgcorsoci = DBKeyGenerator.getNextSequence(dc, "S_CI_CORSO");
		final Date dtmEventoAttuale = corso.dtmEvento;

		inPars.add(dc.createDataField("prgcorsoci", Types.NUMERIC, prgcorsoci));
		inPars.add(dc.createDataField("prgaltraiscr", Types.NUMERIC, prgaltraiscr));
		inPars.add(dc.createDataField("datinizioprev", Types.TIMESTAMP, getTSorNull(corso.datInizioPrev)));
		inPars.add(dc.createDataField("datfineprev", Types.TIMESTAMP, getTSorNull(corso.datFinePrev)));
		inPars.add(dc.createDataField("datpresaincarico", Types.TIMESTAMP, getTSorNull(corso.datPresaInCarico)));
		inPars.add(dc.createDataField("datinizio", Types.TIMESTAMP, getTSorNull(corso.datInizio)));
		inPars.add(dc.createDataField("datfine", Types.TIMESTAMP, getTSorNull(corso.datFine)));
		inPars.add(dc.createDataField("datritiro", Types.TIMESTAMP, getTSorNull(corso.datRitiro)));

		inPars.add(dc.createDataField("strmotivoritiro", Types.VARCHAR, corso.strMotivoRitiro));
		inPars.add(dc.createDataField("cdnutins", Types.NUMERIC, corso.cdnUtenteOp));
		inPars.add(dc.createDataField("cdnutmod", Types.NUMERIC, corso.cdnUtenteOp));
		inPars.add(dc.createDataField("dtmevento", Types.TIMESTAMP, getTSorNull(dtmEventoAttuale)));
		inPars.add(dc.createDataField("prgColloquio", Types.NUMERIC, corso.prgColloquio));
		inPars.add(dc.createDataField("prgPercorso", Types.NUMERIC, corso.prgPercorso));

		qExec.setInputParameters(inPars);
		qExec.setType(QueryExecutorObject.INSERT);
		qExec.setStatement(INSERT_CI_CORSO);
		Object returned = qExec.exec();
		if (!Boolean.TRUE.equals(returned)) {
			errorString = "Errore inserimento del corso " + datiParsed;
			logger.warn(errorString);
			throw new Exception(errorString);
		}
		// In base a che codici ho
		// inserisco un corso di un tipo piuttosto che l'altro

		if (isCorsoCatalogo) {
			inserisciCorsoCatalogo(corso, datiParsed, qExec, prgcorsoci);
		} else {
			inserisciCorsoOrienter(evCorOr, datiParsed, qExec, prgcorsoci, dtmEventoAttuale);
		}
		return prgcorsoci;

	}

	private void inserisciCorsoCatalogo(Corso corso, String datiParsed, QueryExecutorObject qExec,
			BigDecimal prgcorsoci) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("inserisciCorsoCatalogo(Corso, String, QueryExecutorObject, BigDecimal) - start");
		}

		Object returned;
		String errorString;
		List<DataField> inPars = new ArrayList<DataField>();
		DataConnection dc = qExec.getDataConnection();
		inPars.clear();
		inPars.add(dc.createDataField("", Types.NUMERIC, prgcorsoci));
		inPars.add(dc.createDataField("", Types.NUMERIC, corso.numIdProposta));
		inPars.add(dc.createDataField("", Types.NUMERIC, corso.numRecId));

		qExec.setInputParameters(inPars);
		qExec.setType(QueryExecutorObject.INSERT);
		qExec.setStatement(INSERT_CI_CORSO_CATALOGO);
		returned = qExec.exec();
		if (logger.isDebugEnabled()) {
			logger.debug("returned:" + returned + " " + datiParsed);
		}
		if (!Boolean.TRUE.equals(returned)) {
			errorString = "Errore Inserimento corso Catalogo, " + datiParsed;
			logger.warn(errorString);
			throw new Exception(errorString);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("inserisciCorsoCatalogo(Corso, String, QueryExecutorObject, BigDecimal) - end");
		}
	}

	private void inserisciCorsoOrienter(EventoCorsoOrienter evCorOr, String datiParsed, QueryExecutorObject qExec,
			BigDecimal prgcorsoci, final Date dtmEventoAttuale) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(
					"inserisciCorsoOrienter(EventoCorsoOrienter, String, QueryExecutorObject, BigDecimal, Date) - start");
		}

		Object returned;
		String errorString;
		List<DataField> inPars = new ArrayList<DataField>();
		DataConnection dc = qExec.getDataConnection();
		inPars.clear();
		inPars.add(dc.createDataField("", Types.VARCHAR, prgcorsoci));
		inPars.add(dc.createDataField("", Types.VARCHAR, evCorOr.codRifPA));
		inPars.add(dc.createDataField("", Types.VARCHAR, evCorOr.strDescrizioneRifPA));
		inPars.add(dc.createDataField("", Types.VARCHAR, evCorOr.strReferenteCognome));
		inPars.add(dc.createDataField("", Types.VARCHAR, evCorOr.strReferenteNome));
		inPars.add(dc.createDataField("", Types.VARCHAR, evCorOr.strReferenteTel));
		inPars.add(dc.createDataField("", Types.NUMERIC, evCorOr.prgAzienda));
		inPars.add(dc.createDataField("", Types.NUMERIC, evCorOr.prgUnita));
		inPars.add(dc.createDataField("", Types.VARCHAR, evCorOr.codSede));
		inPars.add(dc.createDataField("dtmeventoorienter", Types.TIMESTAMP, getTSorNull(dtmEventoAttuale)));
		inPars.add(dc.createDataField("codcomsede", Types.VARCHAR, evCorOr.codComuneSedeCorso));

		qExec.setInputParameters(inPars);
		qExec.setType(QueryExecutorObject.INSERT);
		qExec.setStatement(INSERT_CI_CORSO_ORIENTER);
		returned = qExec.exec();
		if (logger.isDebugEnabled()) {
			logger.debug("returned:" + returned + " " + datiParsed);
		}
		if (!Boolean.TRUE.equals(returned)) {
			errorString = "Errore Inserimento corso orienter, " + datiParsed;
			logger.warn(errorString);
			throw new Exception(errorString);
		}

		if (logger.isDebugEnabled()) {
			logger.debug(
					"inserisciCorsoOrienter(EventoCorsoOrienter, String, QueryExecutorObject, BigDecimal, Date) - end");
		}
	}

	/**
	 * @param qExec
	 * @param datiParsed
	 * @param prgaltraiscr
	 * @param codRifPA
	 * @param codSede
	 * @return tutti i corsi compatibili con la richiesta ricevuta
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Vector<SourceBean> getVectorCorsiOrienter(QueryExecutorObject qExec, String datiParsed,
			BigDecimal prgaltraiscr, String codRifPA, String codSede) throws Exception {
		List<DataField> inPars = new ArrayList<DataField>();
		// BigDecimal prgaltraiscr = (BigDecimal)
		// lav.getAttribute("prgaltraiscr");
		DataConnection dc = qExec.getDataConnection();
		inPars.add(dc.createDataField("codRifPA", Types.VARCHAR, codRifPA));
		inPars.add(dc.createDataField("codSede", Types.VARCHAR, codSede));
		inPars.add(dc.createDataField("prgaltraiscr", Types.NUMERIC, prgaltraiscr));
		qExec.setInputParameters(inPars);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(FIND_COD_CORSO_ORIENTER);

		Object returned = qExec.exec();
		String errorString = "Corso non trovato " + datiParsed;
		if (returned instanceof SourceBean) {
			SourceBean sb = (SourceBean) returned;
			Vector<SourceBean> vCorsi = sb.getAttributeAsVector(ROW);
			if (vCorsi == null) {
				logger.warn(errorString);
				throw new Exception(errorString);
			} else
				return vCorsi;
		}
		logger.warn(errorString);
		throw new Exception(errorString);
	}

	private void processEsitoCorsoInserimento(Element esitoCorso) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("processEsitoCorsoInserimento(Element) - start");
		}
		XP xp = new XP(esitoCorso);
		Corso corso = parseCorso(xp);
		EventoCorsoOrienter evCorOr = parseEventoCorsoOrienter(xp);
		Ente ente = parseEnte(xp);
		String codiceFiscaleLavoratore = xp.getText(xp.codiceFiscaleLavoratoreXPathExp);
		String accordoCrisiCodice = xp.getText(xp.accordoCrisiCodiceXPathExp);
		String datiParsed = "codiceFiscale:" + codiceFiscaleLavoratore + ", accordoCrisiCodice:" + accordoCrisiCodice
				+ ", corso:" + corso + ", eventoCorsoOrienter:" + evCorOr + ", ente:" + ente;
		// String datiParsed = " corso:" + corso + ", eventoCorsoOrienter:" +
		// evCorOr + ", ente:" + ente;
		controllaCodiciCorso(corso, evCorOr, datiParsed);
		QueryExecutorObject qExec = getQueryExecutorObject();
		DataConnection dc = null;
		try {
			dc = qExec.getDataConnection();
			dc.initTransaction();
			// cerco il lavotarore
			SourceBean lav = getLavoratore(codiceFiscaleLavoratore, accordoCrisiCodice, qExec, datiParsed);
			controllaInserisciEnte(ente, evCorOr, corso, datiParsed, qExec);
			BigDecimal prgaltraiscr = (BigDecimal) lav.getAttribute("prgaltraiscr");
			// cerco tutti i corsi compatibili con l'evento
			Vector<SourceBean> vCorsi = getVectorCorsi(qExec, datiParsed, corso, evCorOr, prgaltraiscr);
			BigDecimal prgCorsoCi = null;
			Object dtmcancellazione = null;
			Date eventoCorrente = corso.dtmEvento;
			// è da inserire - a meno che non trovo una cancellazione successiva
			if (vCorsi.isEmpty()) {
				// controllo e nel caso inserisco l'ente
				controllaInserisciEnte(ente, evCorOr, corso, datiParsed, qExec);
				// devo inserire il corso - lo metto attivo
				prgCorsoCi = inserisciCorso(prgaltraiscr, ente, corso, evCorOr, datiParsed, qExec);

			} else if (vCorsi.size() > 0) {
				// va inserito se: non c'è una cancellazione successiva
				boolean isCorso2Insert = true;
				for (SourceBean corsoSb : vCorsi) {
					logger.debug("corsoSb" + corsoSb);
					dtmcancellazione = corsoSb.getAttribute("dtmcancellazione");
					// Date dtmevento = (Date)
					// corsoSb2.getAttribute("dtmevento");
					// Date dtmeventoorienter = (Date)
					// corsoSb2.getAttribute("dtmeventoorienter");
					Date dtmevento = (Date) corsoSb.getAttribute("dtmevento");
					prgCorsoCi = (BigDecimal) corsoSb.getAttribute("prgCorsoCi");
					if (dtmcancellazione != null && !dtmevento.before(eventoCorrente)) {
						// prgCorsoCi = (BigDecimal)
						// corsoSb2.getAttribute("prgCorsoCi");
						isCorso2Insert = false;
						break;
					}
				}
				if (isCorso2Insert) {
					// se arrivo qui - nessun corso ancora attivo
					// lo inserisco
					// controllo e nel caso inserisco l'ente
					// non dovrebbe servire a questo punto...
					controllaInserisciEnte(ente, evCorOr, corso, datiParsed, qExec);
					// devo inserire il corso - lo metto attivo
					dc.initTransaction();
					prgCorsoCi = inserisciCorso(prgaltraiscr, ente, corso, evCorOr, datiParsed, qExec);
				}
			}

			// ho trovato le informazioni che mi servono - inserisco i dati nel
			// DB
			// BigDecimal prgcorsoci = inserisciCorso(prgaltraiscr, ente, corso,
			// evCorOr, datiParsed, qExec);
			logger.debug("Inserito corso con prgcorsoci" + prgCorsoCi);
			if (logger.isDebugEnabled()) {
				logger.debug("processEsitoCorsoInserimento(Element) - end");
			}
			dc.commitTransaction();
			return;
		} catch (Exception e) {
			if (dc != null) {
				dc.rollBackTransaction();
			}
			throw e;
		} finally {
			if (dc != null) {
				dc.close();
			}

		}

	}

	/**
	 * inserisce in evCorOr.prgAzienda e evCorOr.prgUnita i dati dell'azienda trovata o creata
	 * 
	 * @param ente
	 * @param evCorOr
	 * @param corso
	 * @param datiParsed
	 * @param qExec
	 * @throws Exception
	 */
	private void controllaInserisciEnte(Ente ente, EventoCorsoOrienter evCorOr, Corso corso, String datiParsed,
			QueryExecutorObject qExec) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(
					"controllaInserisciEnte(Ente, EventoCorsoOrienter, Corso, String, QueryExecutorObject) - start");
		}
		boolean consentiAltroTentativo = true;
		boolean isUtileAltroTentativo = false;
		int tentativiMax = 2;
		for (int i = 0; (i < tentativiMax); i++) {
			try {
				qExec.setDontForgetException(true);
				DataConnection dc = qExec.getDataConnection();
				dc.initTransaction();
				controllaInserisciEnteLight(ente, evCorOr, corso, datiParsed, qExec);
				qExec.setDontForgetException(false);
				dc.getInternalConnection().commit();
				if (logger.isDebugEnabled()) {
					logger.debug(
							"controllaInserisciEnte(Ente, EventoCorsoOrienter, Corso, String, QueryExecutorObject) - end");
				}
				return;
			} catch (Exception e) {
				logger.error("controllaInserisciEnte(Ente, EventoCorsoOrienter, Corso, String, QueryExecutorObject)",
						e);
				if (consentiAltroTentativo) {
					if (e instanceof EMFInternalError) {
						EMFInternalError exc = (EMFInternalError) e;
						Exception natExc = exc.getNativeException();
						if (natExc instanceof SQLException) {
							SQLException sqlExc = (SQLException) natExc;
							isUtileAltroTentativo = "23000".equalsIgnoreCase(sqlExc.getSQLState())
									|| sqlExc.getErrorCode() == 1;
							if (isUtileAltroTentativo) {
								// si può riprovare - una volta sola!
								// magari è un problema di concorrenza
								logger.error("SQLState:" + sqlExc.getSQLState());
								logger.error("ErrorCode:" + sqlExc.getErrorCode());
								consentiAltroTentativo = false;
							}
						}
					} else if (isUtileAltroTentativo) {
						logger.warn("si può riprovare - magari è un problema di concorrenza");
					} else {
						throw e;
					}
				} else {
					throw e;
				}
			} finally {
				qExec.setDontForgetException(false);
			}
		} // for (int i = 0; (i < tentativiMax); i++)
	}

	/**
	 * @param ente
	 * @param evCorOr
	 * @param corso
	 * @param datiParsed
	 * @param qExec
	 * @throws Exception
	 */
	private void controllaInserisciEnteLight(Ente ente, EventoCorsoOrienter evCorOr, Corso corso, String datiParsed,
			QueryExecutorObject qExec) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(
					"controllaInserisciEnteLight(Ente, EventoCorsoOrienter, Corso, String, QueryExecutorObject) - start");
		}

		List<DataField> inPars = new ArrayList<DataField>();
		inPars.clear();
		DataConnection dc = qExec.getDataConnection();
		inPars.add(dc.createDataField("codComune", Types.VARCHAR, ente.codComune));
		inPars.add(dc.createDataField("codiceFiscale", Types.VARCHAR, ente.codiceFiscale));
		qExec.setInputParameters(inPars);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(FIND_AZIENDA_ENTE_W_SEDE);
		Object returned = qExec.exec();
		// mi aspetto di avere ritornato l'ente CON SEDE - se esiste -
		// altrimenti l'indicazione x crearlo
		if (logger.isDebugEnabled()) {
			logger.debug("returned:" + returned + " " + datiParsed);
		}
		String errorString;
		if (!(returned instanceof SourceBean)) {
			// errore nella query!?
			errorString = "Ricerca dati ente - azienda, " + datiParsed;
			logger.warn(errorString);
			throw new Exception(errorString);
		} else {
			SourceBean enteSb = (SourceBean) returned;
			evCorOr.prgAzienda = (BigDecimal) enteSb.getAttribute("ROW.prgAzienda");
			evCorOr.prgUnita = (BigDecimal) enteSb.getAttribute("ROW.prgUnita");
			BigDecimal maxPrgUnita = (BigDecimal) enteSb.getAttribute("ROW.maxPrgUnita");
			final boolean isAziendaPresent = (evCorOr.prgAzienda != null);
			if (!isAziendaPresent) {
				// dobbiamo registrare l'ente con tanto di sede legale
				// prendo il prg da :S_AN_AZIENDA
				evCorOr.prgAzienda = DBKeyGenerator.getNextSequence(dc, "S_AN_AZIENDA");
				inPars.clear();
				qExec.setType(QueryExecutorObject.INSERT);
				inPars.add(dc.createDataField("prgAzienda", Types.NUMERIC, evCorOr.prgAzienda));
				inPars.add(dc.createDataField("codiceFiscale", Types.VARCHAR, ente.codiceFiscale));
				inPars.add(dc.createDataField("ragioneSociale", Types.VARCHAR, ente.ragioneSociale));
				inPars.add(dc.createDataField("CODTIPOAZIENDA", Types.VARCHAR, "AZI"));
				inPars.add(dc.createDataField("CDNUTINS", Types.NUMERIC, corso.cdnUtenteOp));
				inPars.add(dc.createDataField("CDNUTMOD", Types.NUMERIC, corso.cdnUtenteOp));
				qExec.setInputParameters(inPars);
				qExec.setStatement(INSERT_ENTE_AN_AZIENDA);
				returned = qExec.exec();
				if (returned instanceof Exception) {
					throw (Exception) returned;
				}
				if (!Boolean.TRUE.equals(returned)) {
					errorString = "Inserimento azienda, " + datiParsed;
					logger.warn(errorString);
					throw new Exception(errorString);
				}
			} // if (evCorOr.prgAzienda == null )
			if (evCorOr.prgUnita == null && evCorOr.prgAzienda != null) {
				// l'azienda o era presente oppure l'abbiamo aggiunta
				inPars.clear();
				Integer inAttivita = 1;
				if (isAziendaPresent) {
					// azienda già presente - devo trovare il prgUnita da
					// utilizzare
					// se l'azienda era già presente ma senza la sede giusta
					// mi ha tornato il maxPrgUnita - gli aggiungo 1 ed
					// ottengo quanto desiderato
					evCorOr.prgUnita = BigDecimal.ONE.add(maxPrgUnita);
				} else {
					evCorOr.prgUnita = BigDecimal.ONE;
				}
				inPars.clear();
				inPars.add(dc.createDataField("prgAzienda", Types.NUMERIC, evCorOr.prgAzienda));
				inPars.add(dc.createDataField("prgUnita", Types.NUMERIC, evCorOr.prgUnita));
				String indirizzo = "";
				if (!StringUtils.isEmptyNoBlank(ente.indirizzo)) {
					int beginIndex = 0;
					int endIndex = Math.min(ente.indirizzo.length() - 1, 60);
					indirizzo = ente.indirizzo.substring(beginIndex, endIndex);
				}
				inPars.add(dc.createDataField("indirizzo", Types.VARCHAR, indirizzo));
				inPars.add(dc.createDataField("codComune", Types.VARCHAR, ente.codComune));
				inPars.add(dc.createDataField("codazstato", Types.VARCHAR, inAttivita));
				// 80303 - ALTRE SCUOLE E CORSI DI FORMAZIONE SPECIALE
				if (StringUtils.isEmptyNoBlank(ente.codATECO)) {
					ente.codATECO = "422200";
				}
				inPars.add(dc.createDataField("codateco", Types.VARCHAR, ente.codATECO));
				inPars.add(dc.createDataField("CDNUTINS", Types.NUMERIC, corso.cdnUtenteOp));
				inPars.add(dc.createDataField("CDNUTMOD", Types.NUMERIC, corso.cdnUtenteOp));
				qExec.setStatement(INSERT_SEDE_ENTE_AN_UNITA_AZIENDA);
				qExec.setInputParameters(inPars);
				qExec.setType(QueryExecutorObject.INSERT);
				returned = qExec.exec();
				if (returned instanceof Exception) {
					throw (Exception) returned;
				}
				if (!Boolean.TRUE.equals(returned)) {
					errorString = "Inserimento unita azienda, " + datiParsed;
					logger.warn(errorString);
					throw new Exception(errorString);
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(
					"controllaInserisciEnteLight(Ente, EventoCorsoOrienter, Corso, String, QueryExecutorObject) - end");
		}
	}

	@SuppressWarnings("unchecked")
	private SourceBean getLavoratore(String codiceFiscaleLavoratore, String accordoCrisiCodice,
			QueryExecutorObject qExec, String datiParsed) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("getLavoratore(String, String, QueryExecutorObject, String) - start");
		}

		List<DataField> inPars = new ArrayList<DataField>();

		DataConnection dc = qExec.getDataConnection();
		inPars.add(dc.createDataField("codiceFiscaleLavoratore", Types.VARCHAR, codiceFiscaleLavoratore));
		inPars.add(dc.createDataField("accordoCrisiCodice", Types.VARCHAR, accordoCrisiCodice));

		qExec.setInputParameters(inPars);
		qExec.setStatement(FIND_COD_ACCORDO_DEFINITIVO);
		final String cfLavCodAccordo = "codiceFiscale:" + codiceFiscaleLavoratore + ", accordoCrisiCodice:"
				+ accordoCrisiCodice;

		final String errorOnLav = "Controllare lavoratore con " + cfLavCodAccordo;

		Object returned = qExec.exec();
		// returned = qExec.exec();
		SourceBean lav = null;
		if (returned instanceof SourceBean) {
			SourceBean sb = (SourceBean) returned;
			Vector<SourceBean> vectLav = sb.getAttributeAsVector(ROW);
			if (vectLav.size() > 1) {
				// non dovrebbe accadere - ERRORE!
				logger.warn(errorOnLav);
				throw new Exception(errorOnLav);
			}
			if (vectLav.size() > 0) {
				// ritorno il 1' elemento
				lav = vectLav.firstElement();
			} else {
				logger.debug("non ho trovato alcun record provo a cercare con cod accordo temp, " + errorOnLav);
			}
		}

		if (lav == null) {
			// non ho trovato il codice definitivo - provo a cercarlo come
			// codice
			// temporaneo
			qExec.setStatement(FIND_COD_ACCORDO_TEMP);
			returned = qExec.exec();
			if (returned instanceof SourceBean) {
				SourceBean sb = (SourceBean) returned;
				Vector<SourceBean> vectLav = sb.getAttributeAsVector(ROW);
				if (vectLav.size() > 0) {
					if (vectLav.size() > 1) {
						// non dovrebbe accadere - log errore ma vado oltre
						logger.warn(errorOnLav);
						throw new Exception(errorOnLav);
					}
					// in ogni caso ritorno il 1' elemento
					lav = vectLav.firstElement();
				} else {
					final String errorNotFound = "non ho trovato alcun record nemmeno con cod accordo temp, "
							+ errorOnLav;
					logger.debug(errorNotFound);
				}
			}
		} // if (lav==null)
		if (lav == null) {
			String errorString = "Fallita Ricerca dati Lavoratore, " + datiParsed;
			logger.warn(errorString);
			throw new Exception(errorString);
		} // if (lav==null)

		if (logger.isDebugEnabled()) {
			logger.debug("getLavoratore(String, String, QueryExecutorObject, String) - end");
		}
		return lav;

	}

	private Timestamp getTSorNull(final Date dtm) {
		if (dtm == null) {
			return null;
		}
		if (dtm instanceof Timestamp) {
			return (Timestamp) dtm;
		}
		return new Timestamp(dtm.getTime());
	}

	private Ente parseEnte(XP xp) throws XPathExpressionException {
		Ente ente = new Ente();
		ente.codiceFiscale = xp.getText(xp.codiceFiscaleXPathExp);
		ente.codATECO = xp.getTextIfNotNull(xp.codATECOXPathExp, "");
		ente.codCCNL = xp.getText(xp.codCCNLXPathExp);
		ente.codComune = xp.getText(xp.codComuneXPathExp);
		ente.indirizzo = xp.getText(xp.indirizzoXPathExp);
		ente.ragioneSociale = xp.getTextOrNull(xp.ragioneSocialeXPathExp);
		return ente;
	}

	private QueryExecutorObject getQueryExecutorObject() throws SQLException, EMFInternalError {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			return getQueryExecutorObject(ctx);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;

	}

	private QueryExecutorObject getQueryExecutorObject(InitialContext ctx)
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
			logger.error("Impossibile ottenere una connessione");
			return null;
		}
		return qExec;
	}

	private EventoCorsoOrienter parseEventoCorsoOrienter(XP xp) throws XPathExpressionException {
		EventoCorsoOrienter evCO = new EventoCorsoOrienter();
		evCO.codRifPA = xp.getTextOrNull(xp.codRifPAXPathExp);
		evCO.codComuneSedeCorso = xp.getTextOrNull(xp.codComuneSedeCorsoXPathExp);
		evCO.strDescrizioneRifPA = xp.getTextOrNull(xp.strDescrizioneRifPAXPathExp);
		evCO.codSede = xp.getTextOrNull(xp.codSedeXPathExp);
		evCO.strReferenteCognome = xp.getTextOrNull(xp.strReferenteCognomeXPathExp);
		evCO.strReferenteNome = xp.getTextOrNull(xp.strReferenteNomeXPathExp);
		evCO.strReferenteTel = xp.getTextOrNull(xp.strReferenteTelXPathExp);
		return evCO;
	}

	private Corso parseCorso(XP xp) throws ParseException, XPathExpressionException {
		if (logger.isDebugEnabled()) {
			logger.debug("parseCorso(Element) - start");
		}
		Corso corso = new Corso();
		corso.datPresaInCarico = xp.getDateOrNull(xp.datPresaInCaricoXPathExp);
		corso.datFine = xp.getDateOrNull(xp.datFineXPathExp);
		corso.datFinePrev = xp.getDateOrNull(xp.datFinePrevXPathExp);
		corso.datInizio = xp.getDateOrNull(xp.datInizioXPathExp);
		corso.datInizioPrev = xp.getDateOrNull(xp.datInizioPrevXPathExp);
		corso.datRitiro = xp.getDateOrNull(xp.datRitiroXPathExp);

		corso.strMotivoRitiro = xp.getTextOrNull(xp.strMotivoRitiroXPathExp);
		String strOrarioInvio = xp.getText(xp.strOrarioInvioXPathExp);
		// SimpleDateFormat sdfTs = new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
		// corso.dtmEvento = new
		// Timestamp(sdfTs.parse(strOrarioInvio).getTime());
		corso.dtmEvento = Timestamp.valueOf(strOrarioInvio);
		String prgColloquioStr = xp.getTextOrNull(xp.prgColloquioXPathExp);
		if (StringUtils.isEmptyNoBlank(prgColloquioStr)) {
			corso.prgColloquio = null;
		} else {
			corso.prgColloquio = new BigDecimal(prgColloquioStr);
		}
		String prgPercorsoStr = xp.getTextOrNull(xp.prgPercorsoXPathExp);
		if (StringUtils.isEmptyNoBlank(prgPercorsoStr)) {
			corso.prgPercorso = null;
		} else {
			corso.prgPercorso = new BigDecimal(prgPercorsoStr);
		}
		if (corso.prgPercorso == null || corso.prgColloquio == null) {
			// Devo annullarli entrambi!
			corso.prgPercorso = null;
			corso.prgColloquio = null;
		}
		String cdnUtenteOpStr = xp.getTextOrNull(xp.cdnUtenteOpXPathExp);
		if (StringUtils.isEmptyNoBlank(cdnUtenteOpStr)) {
			corso.cdnUtenteOp = BigDecimal.valueOf(codUtenteDefault);
		} else {
			corso.cdnUtenteOp = new BigDecimal(cdnUtenteOpStr);
		}
		String numIdPropostaStr = xp.getTextOrNull(xp.numIdPropostaXPathExp);
		if (!StringUtils.isEmptyNoBlank(numIdPropostaStr)) {
			corso.numIdProposta = new BigDecimal(numIdPropostaStr);
		}
		String numRecIdStr = xp.getTextOrNull(xp.numRecIdXPathExp);
		if (!StringUtils.isEmptyNoBlank(numRecIdStr)) {
			corso.numRecId = new BigDecimal(numRecIdStr);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("parseCorso(Element) - end");
		}
		return corso;
	}

	private String getValidityErrors(String datiRichiestaXml) {
		if (logger.isDebugEnabled()) {
			logger.debug("getValidityErrors(String) - start");
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
			File schemaFile = new File(
					ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + ESITO_CORSO_CIG_XSD);
			StreamSource streamSource = new StreamSource(schemaFile);
			Schema schema = factory.newSchema(streamSource);
			Validator validator = schema.newValidator();
			// at last perform validation:
			StringReader datiXmlReader = new StringReader(datiRichiestaXml);
			StreamSource datiXmlStreamSource = new StreamSource(datiXmlReader);
			validator.validate(datiXmlStreamSource);
		} catch (Exception e) {
			logger.warn("getValidityErrors(String)", e);

			// e.printStackTrace();
			String returnString = e.getLocalizedMessage();
			if (logger.isDebugEnabled()) {
				logger.debug("getValidityErrors(String) - end");
			}
			return returnString;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("getValidityErrors(String) - end");
		}
		return null;
	}

	public class Ente {
		/**
		 * Logger for this class
		 */
		private final Logger logger = Logger.getLogger(Ente.class);

		/**
		 */

		String codiceFiscale;
		String ragioneSociale;
		String indirizzo;
		String codComune;
		String codATECO;
		String codCCNL;

		@Override
		public String toString() {
			return "Ente [codATECO=" + codATECO + ", codCCNL=" + codCCNL + ", codComune=" + codComune
					+ ", codiceFiscale=" + codiceFiscale + ", indirizzo=" + indirizzo + ", ragioneSociale="
					+ ragioneSociale + "]";
		}

	}

	public class Corso {
		/**
		 * Logger for this class
		 */
		private final Logger logger = Logger.getLogger(Corso.class);

		BigDecimal prgCorsoCI; // NUMBER(38) NOT NULL,
		BigDecimal prgAltraIscr; // NUMBER(38) NOT NULL,
		BigDecimal prgColloquio; // NUMBER(38),
		BigDecimal prgPercorso; // NUMBER(38),
		Date datInizioPrev; // datE,
		Date datFinePrev; // datE,
		Date datPresaInCarico; // datE,
		Date datInizio; // datE,
		Date datFine; // datE,
		Date datRitiro; // datE,
		String strMotivoRitiro; // VARCHAR2(200 BYTE),
		BigDecimal cdnUtIns; // NUMBER(38) NOT NULL,
		Date dtmIns; // TIMESTAMP(6) NOT NULL,
		BigDecimal cdnUtMod; // NUMBER(38) NOT NULL,
		Date dtmMod; // TIMESTAMP(6) NOT NULL,
		BigDecimal cdnUtenteOp; // NUMBER(38) NOT NULL,
		// Date dtmCancellazione; // TIMESTAMP(6) NOT NULL,
		Timestamp dtmEvento; // TIMESTAMP(6) NOT NULL,
		BigDecimal numIdProposta;
		BigDecimal numRecId;

		@Override
		public String toString() {
			return "Corso [cdnUtIns=" + cdnUtIns + ", cdnUtMod=" + cdnUtMod + ", datFine=" + datFine + ", datFinePrev="
					+ datFinePrev + ", datInizio=" + datInizio + ", datInizioPrev=" + datInizioPrev
					+ ", datPresaInCarico=" + datPresaInCarico + ", datRitiro=" + datRitiro + ", dtmEvento=" + dtmEvento
					+ ", dtmIns=" + dtmIns + ", dtmMod=" + dtmMod + ", prgAltraIscr=" + prgAltraIscr + ", prgColloquio="
					+ prgColloquio + ", prgCorsoCI=" + prgCorsoCI + ", prgPercorso=" + prgPercorso
					+ ", strMotivoRitiro=" + strMotivoRitiro + "]";
		}

	}

	public class EventoCorsoOrienter {
		/**
		 * Logger for this class
		 */
		private final Logger logger = Logger.getLogger(EventoCorsoOrienter.class);

		public String codComuneSedeCorso;
		BigDecimal prgCorsoOrienter; // NUMBER(38) NOT NULL,
		String codRifPA; // VARCHAR2(20 BYTE) NOT NULL,
		String strDescrizioneRifPA; // VARCHAR2(400 BYTE) NOT NULL,
		String strReferenteCognome; // VARCHAR2(40 BYTE) NOT NULL,
		String strReferenteNome; // VARCHAR2(40 BYTE) NOT NULL,
		String strReferenteTel; // VARCHAR2(20 BYTE),
		BigDecimal prgAzienda; // NUMBER(38) NOT NULL,
		BigDecimal prgUnita; // NUMBER(38) NOT NULL,
		String codSede; // VARCHAR2(20 BYTE) NOT NULL,
		Timestamp dtmEventoOrienter;

		@Override
		public String toString() {
			return "EventoCorsoOrienter [codRifPA=" + codRifPA + ", codSede=" + codSede + ", dtmEventoOrienter="
					+ dtmEventoOrienter + ", prgAzienda=" + prgAzienda + ", prgCorsoOrienter=" + prgCorsoOrienter
					+ ", prgUnita=" + prgUnita + ", strDescrizioneRifPA=" + strDescrizioneRifPA
					+ ", strReferenteCognome=" + strReferenteCognome + ", strReferenteNome=" + strReferenteNome
					+ ", strReferenteTel=" + strReferenteTel + "]";
		}

	}

	public class XP {
		/**
		 * Logger for this class
		 */
		private final Logger logger = Logger.getLogger(XP.class);

		private String numRecIdXPathExp = "/corso:esitoCorso/corso:corso/corso:idSedeCorsoCatalogo";
		private String numIdPropostaXPathExp = "/corso:esitoCorso/corso:corso/corso:idPropopostaCatalogoScelta";

		private String prgColloquioXPathExp = "/corso:esitoCorso/corso:evento/corso:codColl";
		private String prgPercorsoXPathExp = "/corso:esitoCorso/corso:evento/corso:codPerc";
		private String cdnUtenteOpXPathExp = "/corso:esitoCorso/corso:evento/corso:codOp";
		private String strOrarioInvioXPathExp = "/corso:esitoCorso/corso:evento/corso:orarioInvio";
		// Ente
		private String indirizzoXPathExp = "/corso:esitoCorso/corso:ente/corso:sedeLegaleEnte/corso:indirizzo";
		private String ragioneSocialeXPathExp = "/corso:esitoCorso/corso:ente/corso:ragioneSocialeEnte";
		private String codComuneXPathExp = "/corso:esitoCorso/corso:ente/corso:sedeLegaleEnte/corso:codComune";
		private String codCCNLXPathExp = "/corso:esitoCorso/corso:ente/corso:sedeLegaleEnte/corso:codCCNL";
		private String codATECOXPathExp = "/corso:esitoCorso/corso:ente/corso:sedeLegaleEnte/corso:codATECO";
		private String codiceFiscaleXPathExp = "/corso:esitoCorso/corso:ente/corso:codiceFiscaleEnte";
		// FineEnte
		private String tipoEventoXPathExp = "/corso:esitoCorso/corso:evento/corso:tipo";
		private String accordoCrisiCodiceXPathExp = "/corso:esitoCorso/corso:corso/corso:accordoCrisiCodice";
		private String codiceFiscaleLavoratoreXPathExp = "/corso:esitoCorso/corso:corso/corso:codiceFiscaleLavoratore";
		// private String listaEsitoCorsoXPathExp = LISTA_ESITO_CORSO_XP;

		// xsd 2 oggetto eventoCorsoOrienter
		private String strReferenteTelXPathExp = "/corso:esitoCorso/corso:referente/corso:tel";
		private String strReferenteCognomeXPathExp = "/corso:esitoCorso/corso:referente/corso:cognome";
		private String strReferenteNomeXPathExp = "/corso:esitoCorso/corso:referente/corso:nome";
		private String codSedeXPathExp = "/corso:esitoCorso/corso:corso/corso:idSedeCorsoOrienter";
		private String strDescrizioneRifPAXPathExp = "/corso:esitoCorso/corso:corso/corso:descrizioneRifPA";
		private String codRifPAXPathExp = "/corso:esitoCorso/corso:corso/corso:rifPAOperazioneScelta";
		private String codComuneSedeCorsoXPathExp = "/corso:esitoCorso/corso:corso/corso:codComuneSedeCorso";

		// xsd 2 oggetto Corso
		private String datPresaInCaricoXPathExp = "/corso:esitoCorso/corso:corso/corso:presaInCaricoEnte";
		private String strMotivoRitiroXPathExp = "/corso:esitoCorso/corso:corso/corso:motivoRitiro";
		private String datRitiroXPathExp = "/corso:esitoCorso/corso:corso/corso:dataRitiro";
		private String datInizioPrevXPathExp = "/corso:esitoCorso/corso:corso/corso:dataInizioPrev";
		private String datInizioXPathExp = "/corso:esitoCorso/corso:corso/corso:dataInizioEffettiva";
		private String datFinePrevXPathExp = "/corso:esitoCorso/corso:corso/corso:dataFinePrev";
		private String datFineXPathExp = "/corso:esitoCorso/corso:corso/corso:dataFineEffettiva";

		public Date getDateOrNull(String xPathExp) throws ParseException, XPathExpressionException {
			return getDateIfNotNull(xPathExp, null);
		}

		public String getTextOrNull(String xPathExp) throws XPathExpressionException {
			return getTextIfNotNull(xPathExp, null);
		}

		private SimpleDateFormat date2xsd;
		private Element elem2parse;
		private XPath xPath;

		XP(Element elem2parse) {
			date2xsd = new SimpleDateFormat("yyyy-MM-dd");
			this.setElem2parse(elem2parse);

			this.xPath = initXPath();
		}

		private XPath initXPath() {
			XPath xPath = XPathFactory.newInstance().newXPath();
			logger.debug("Namespace:" + this.getElem2parse().getNamespaceURI());
			SilNamespaceContext context = new SilNamespaceContext(CORSO, this.getElem2parse().getNamespaceURI());
			xPath.setNamespaceContext(context);

			return xPath;
		}

		String getText(String xPathExp) throws XPathExpressionException {
			final Element singleNode = (Element) xPath.evaluate(xPathExp, this.getElem2parse(), XPathConstants.NODE);

			return singleNode.getTextContent();
		}

		String getTextIfNotNull(String xPathExp, String failSafe) throws XPathExpressionException {
			final Node singleNode = (Element) xPath.evaluate(xPathExp, this.getElem2parse(), XPathConstants.NODE);
			if (singleNode == null) {
				return failSafe;
			}
			return singleNode.getTextContent();
		}

		Date getDate(String xPathExp) throws ParseException, XPathExpressionException {
			final Node singleNode = (Element) xPath.evaluate(xPathExp, this.getElem2parse(), XPathConstants.NODE);

			return date2xsd.parse(singleNode.getTextContent());
		}

		Date getDateIfNotNull(String xPathExp, Date failSafe) throws ParseException, XPathExpressionException {
			final Node singleNode = (Element) xPath.evaluate(xPathExp, this.getElem2parse(), XPathConstants.NODE);
			if (singleNode == null) {
				return failSafe;
			}
			return date2xsd.parse(singleNode.getTextContent());
		}

		public void setElem2parse(Element elem2parse) {
			this.elem2parse = elem2parse;
		}

		public Element getElem2parse() {
			return elem2parse;
		}

	}

	private static QueryExecutorObject getQueryExecutorObject(DataConnection dc) {
		if (logger.isDebugEnabled()) {
			logger.debug("getQueryExecutorObject(DataConnection) - start");
		}

		QueryExecutorObject qExec = new QueryExecutorObject();

		qExec.setRequestContainer(null);
		qExec.setResponseContainer(null);
		qExec.setDataConnection(dc);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setTransactional(true);
		qExec.setDontForgetException(false);

		if (logger.isDebugEnabled()) {
			logger.debug("getQueryExecutorObject(DataConnection) - end");
		}
		return qExec;
	}
}
