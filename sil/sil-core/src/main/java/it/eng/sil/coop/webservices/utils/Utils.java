package it.eng.sil.coop.webservices.utils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axis.encoding.Base64;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.sil.Values;
import it.eng.sil.coop.webservices.madreperla.servizi.UtilityXml;
import it.eng.sil.util.xml.XMLValidator;

public class Utils {
	private static final Logger logger = Logger.getLogger(Utils.class);
	public static final int numMaxTentativiProtocollazione = 3;
	public static final String utenteServiziPortale = "150";
	public static final BigDecimal USER_BATCH = new BigDecimal("100");
	public static final String SERVIZIORENDICONTAZIONE = "RENDICONTAZIONE";
	private static final String SCHEMA_XSD_OUTPUT = "risposta.xsd";
	public final static Pattern PATTERN_CODICE_FISCALE = Pattern
			.compile("([A-Z]{6}[\\dLMNPQRSTUV]{2}[A-Z][\\dLMNPQRSTUV]{2}[A-Z][\\dLMNPQRSTUV]{3}[A-Z])|(\\d{11})");

	public static boolean checkCodiceFiscale(String codiceFisc) throws Exception {
		if (codiceFisc == null || codiceFisc.equals("") || codiceFisc.length() != 16) {
			return false;
		}
		// Setto il pattern per il confronto
		Pattern p = Pattern.compile("[A-Z]{6}[0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z]");
		// Eseguiamo il match della stringa codiceFisc con il pattern
		Matcher m = p.matcher(codiceFisc);
		boolean matchFound = m.matches();
		return matchFound;
	}

	public static String createXMLRisposta(String codice, String descrizione) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		String returnString = "";
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

		returnString = UtilityXml.domToString(doc);

		File schemaFile = new File(ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsd"
				+ File.separator + "rispostaGenerica" + File.separator + SCHEMA_XSD_OUTPUT);

		String validityErrors = XMLValidator.getValidityErrors(returnString, schemaFile);
		if (validityErrors != null && validityErrors.length() > 0) {
			returnString = createXMLRispostaErroreGenerico();
		}
		return returnString;
	}

	private static String createXMLRispostaErroreGenerico() throws Exception {

		String codice = "99";
		String descrizione = "Errore generico";

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		String returnString = "";
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

		returnString = UtilityXml.domToString(doc);
		return returnString;
	}

	public static String createXMLRispostaWithFile(String codice, String descrizione, FileInputStream filePDF)
			throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		String returnString = "";
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

		byte[] bytes = new byte[1024];
		String strCode64 = "";

		while (filePDF.read(bytes) != -1) {
			strCode64 = strCode64 + Base64.encode(bytes);
		}

		UtilityXml.appendTextChild("codice", codice, esito, doc);
		UtilityXml.appendTextChild("descrizione", descrizione, esito, doc);
		UtilityXml.appendTextChild("file", strCode64, esito, doc);

		returnString = UtilityXml.domToString(doc);

		return returnString;
	}

	public static QueryExecutorObject getQueryExecutorObject() throws SQLException, EMFInternalError {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			return getQueryExecutorObject(ctx);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static QueryExecutorObject getQueryExecutorObject(InitialContext ctx)
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

	public static QueryExecutorObject getQueryExecutorObject(DataConnection dc) {
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
