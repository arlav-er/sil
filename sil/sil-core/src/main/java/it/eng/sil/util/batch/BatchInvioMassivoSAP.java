package it.eng.sil.util.batch;

import java.io.File;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.rpc.holders.StringHolder;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.gov.lavoro.servizi.servizicoapSap.ServizicoapWSProxy;
import it.gov.lavoro.servizi.servizicoapSap.types.Risposta_invioSAP_TypeEsito;
import it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_invioSAP_TypeEsitoHolder;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

/**
 * Batch per l'invio richieste personale
 * 
 * @author
 *
 */
public class BatchInvioMassivoSAP implements BatchRunnable {

	private static final String INPUT_XSD = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator
			+ "xsd" + File.separator + "sap" + File.separator + "Rev008_SAP.xsd";
	private static Logger logger = Logger.getLogger(BatchInvioMassivoSAP.class.getName());
	public static final String END_POINT_NAME_INVIOSAP = "InvioSAP";
	private BigDecimal USER_BATCH = new BigDecimal("365");
	private Vector vettCdnLavoratore = new Vector();

	public static void main(String[] args) {
		BatchInvioMassivoSAP app = null;
		try {
			app = new BatchInvioMassivoSAP();
			if (args.length == 0) {
				app.start();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
		}
	}

	public BatchInvioMassivoSAP() {

	}

	public void start() {
		logger.error("=========== Avvio Batch ===========");
		logger.error("INVIO MASSIVO YG SAP: Avvio Batch");

		TransactionQueryExecutor transExec = null;
		BigDecimal[] arrLavs = null;
		try {
			transExec = initTransazione();
			oracle.jdbc.OracleConnection oracleConn = it.eng.sil.util.Utils
					.getOracleConnection(transExec.getDataConnection().getInternalConnection());
			arrLavs = getArrayLavoratoriDaSpedire(oracleConn);

		} catch (Exception e) {
			logger.error("Errore recupero lista lavoratori", e);
		} finally {
			try {
				transExec.commitTransaction();
			} catch (EMFInternalError e) {
				logger.error("Errore transazione", e);
			}
			Utils.releaseResources(transExec.getDataConnection(), null, null);
		}

		if (arrLavs != null) {
			int numLavoratoriEstr = arrLavs.length;
			logger.info("INVIO MASSIVO YG SAP: totali da inviare: " + numLavoratoriEstr);

			for (int i = 0; i < numLavoratoriEstr; i++) {
				// 2
				elaboraLavorare(arrLavs, numLavoratoriEstr, i);
			}
		}

		logger.error("INVIO MASSIVO YG SAP: Fine Batch");
	}

	private void elaboraLavorare(BigDecimal[] arrLavs, int numLavoratoriEstr, int i) {
		TransactionQueryExecutor transExecLav = null;
		try {
			transExecLav = initTransazione();
			oracle.jdbc.OracleConnection oracleConnLav = it.eng.sil.util.Utils
					.getOracleConnection(transExecLav.getDataConnection().getInternalConnection());

			BigDecimal cdnLavoratore = arrLavs[i];
			int numcur = i + 1;
			logger.info("INVIO MASSIVO YG SAP: elaborazione di " + numcur + "/" + numLavoratoriEstr + " cdnLavoratore: "
					+ cdnLavoratore.toString());
			sendXmlLavoratore(transExecLav, oracleConnLav, cdnLavoratore);

			try {
				transExecLav.commitTransaction();
			} catch (EMFInternalError e) {
				logger.error("Errore transazione", e);
			}

		} catch (Exception e) {
			logger.error("Errore invio lavoratore cdnLavoratore", e);
		} finally {
			Utils.releaseResources(transExecLav.getDataConnection(), null, null);
		}
	}

	private void pause(int seconds) {
		Date start = new Date();
		Date end = new Date();
		while (end.getTime() - start.getTime() < seconds * 1000) {
			end = new Date();
		}
	}

	private TransactionQueryExecutor initTransazione() throws Exception {
		InitialContext ctx = new InitialContext();
		Object objs = ctx.lookup(Values.JDBC_JNDI_NAME);
		DataConnection dataConnection = null;
		if (objs instanceof DataSource) {
			DataSource ds = (DataSource) objs;
			Connection conn = ds.getConnection();
			dataConnection = new DataConnection(conn, "2", new OracleSQLMapper());
		} else {
			logger.error("Impossibile ottenere una connessione");
			throw new SQLException();
		}

		TransactionQueryExecutor transExec = new TransactionQueryExecutor(dataConnection, null, null);
		transExec.initTransaction();
		// dataConnection.getInternalConnection().setAutoCommit(false);

		return transExec;
	}

	private BigDecimal[] getArrayLavoratoriDaSpedire(oracle.jdbc.OracleConnection oracleConn) throws Exception {

		// recupero i lavoratori da inviare
		ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("INT_ARRAY", oracleConn);
		Object nominativi[] = vettCdnLavoratore.toArray();
		ARRAY arrayCdnLavoratore = new ARRAY(descriptor, oracleConn, nominativi);

		OracleCallableStatement stmtLista = (OracleCallableStatement) oracleConn
				.prepareCall("{ call ?:= PG_SAP.getLavoratoriBatchSap() }");
		stmtLista.registerOutParameter(1, OracleTypes.ARRAY, "INT_ARRAY");
		stmtLista.execute();
		arrayCdnLavoratore = stmtLista.getARRAY(1);

		stmtLista.close();

		if (arrayCdnLavoratore != null) {
			BigDecimal[] arrLavs = (BigDecimal[]) arrayCdnLavoratore.getArray();
			return arrLavs;
		}

		return null;
	}

	private void sendXmlLavoratore(TransactionQueryExecutor transExec, oracle.jdbc.OracleConnection oracleConn,
			BigDecimal cdnLavoratore) throws Exception {
		String encrypterKey = System.getProperty("_ENCRYPTER_KEY_");
		String dataDichiarazione = null;
		String codiceCpiSap = null;

		Object params[] = new Object[1];
		params[0] = cdnLavoratore;
		SourceBean res = (SourceBean) transExec.executeQuery("GET_AN_LAVORATORE_ANAG", params, "SELECT");
		if (res != null) {
			String strCodiceFiscale = (String) res.getAttribute("ROW.STRCODICEFISCALE");
			String datNascita = (String) res.getAttribute("ROW.DATNASC");
			String codCpiMin = "";
			Object paramsCpi[] = new Object[1];
			paramsCpi[0] = cdnLavoratore;
			SourceBean resCpi = (SourceBean) transExec.executeQuery("GET_CPI_AN_LAVORATORE", params, "SELECT");
			if (res != null) {
				codCpiMin = (String) resCpi.getAttribute("ROW.cpicompmin");
			}

			// creo sap
			// crea XML SAP e recor collegati TS_TRACCIAMENTO_SAP
			CallableStatement stmtCreaSap = oracleConn.prepareCall("{? = call pg_sap.getXMLSAP(?, ?, ?, ?, ?, ?, ?) }");

			stmtCreaSap.registerOutParameter(1, OracleTypes.CLOB);
			stmtCreaSap.setBigDecimal(2, cdnLavoratore);
			stmtCreaSap.setString(3, encrypterKey);
			stmtCreaSap.setBigDecimal(4, USER_BATCH);
			stmtCreaSap.setString(5, "01"); // codtipovariazione
			stmtCreaSap.setString(6, dataDichiarazione);
			stmtCreaSap.setString(7, codiceCpiSap);
			stmtCreaSap.registerOutParameter(8, OracleTypes.VARCHAR);

			stmtCreaSap.execute();

			String xml = (String) stmtCreaSap.getString(1);
			String codErrore = (String) stmtCreaSap.getString(8);

			stmtCreaSap.close();

			if (xml != null && !("").equalsIgnoreCase(xml)) {
				// inserisce in TS_TRACCIAMENTO_SAP
				Object[] paramsIns = new Object[8];
				paramsIns[0] = cdnLavoratore;
				paramsIns[1] = xml;
				paramsIns[2] = codErrore;
				paramsIns[3] = null;
				paramsIns[4] = null;
				paramsIns[5] = null;
				paramsIns[6] = null;
				paramsIns[7] = "01";
				Boolean insertAccorpaLav = (Boolean) transExec.executeQuery("INSERT_TS_TRACCIAMENTO_SAP", paramsIns,
						"INSERT");
				if (!insertAccorpaLav.booleanValue()) {
					throw new Exception("Errore nell'inserimento TS_TRACCIAMENTO_SAP");
				}

				if (("00").equalsIgnoreCase(codErrore)) {
					// invio la sap
					invioSap(xml, cdnLavoratore, strCodiceFiscale, datNascita, codCpiMin, transExec);
				}
			}
		}
	}

	private void invioSap(String xmlSap, BigDecimal cdnLavoratore, String strCodiceFiscale, String datNascita,
			String codCpiMin, TransactionQueryExecutor transExec) throws Exception {
		logger.info("INIZIO CHIAMATA INVIO SAP, cdnLavoratore =" + cdnLavoratore);

		it.gov.lavoro.servizi.servizicoapSap.types.holders.Risposta_invioSAP_TypeEsitoHolder esito = new Risposta_invioSAP_TypeEsitoHolder();
		javax.xml.rpc.holders.StringHolder messaggioErrore = new StringHolder();
		javax.xml.rpc.holders.StringHolder codiceSAP = new StringHolder("");

		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();
		EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
		String invioSapEP = eps.getUrl(END_POINT_NAME_INVIOSAP);

		ServizicoapWSProxy proxy = new ServizicoapWSProxy();
		proxy.setEndpoint(invioSapEP);

		if (!validazioneXml(xmlSap, INPUT_XSD)) {
			logger.error("BatchInvioMassivoSAP: Errore in validazione input. cdnlavoratore = " + cdnLavoratore);
			throw new Exception("BatchInvioMassivoSAP: Errore in validazione input.");
		}

		logger.debug("send invio la sap= " + xmlSap);
		proxy.invioSAP(xmlSap, esito, messaggioErrore, codiceSAP);

		logger.info("risposta invio sap   cdnLavoratore =" + cdnLavoratore + " - codice=" + codiceSAP.value);
		logger.info("risposta invio la sap messaggio di errore=" + messaggioErrore.value);
		logger.info("risposta invio la sap esito=" + esito.value);

		String codMinSapInvioSap = codiceSAP.value;

		// chiudo record precedente di SP_LAVORATORE
		// aggiorno il record
		// solo se mi viene restiruito un codice SAP valido
		if (codMinSapInvioSap != null && !("").equalsIgnoreCase(codMinSapInvioSap)) {
			Object[] paramsUpdate = new Object[2];
			paramsUpdate[0] = USER_BATCH;
			paramsUpdate[1] = cdnLavoratore;
			Object queryResUpdate = transExec.executeQuery("UPDATE_SP_LAVORATORE_INVIO_SAP", paramsUpdate, "UPDATE");
			if (queryResUpdate == null
					|| !(queryResUpdate instanceof Boolean && ((Boolean) queryResUpdate).booleanValue() == true)) {
				throw new Exception("Impossibile eseguire update del codice sap in an_yg_dati_invio");
			}

			// inserisco il record in SP_LAVORATORE
			Object[] params = new Object[8];
			params[0] = codMinSapInvioSap;
			params[1] = codCpiMin;
			params[2] = datNascita;
			params[3] = cdnLavoratore;
			params[4] = USER_BATCH;
			params[5] = USER_BATCH;
			params[6] = strCodiceFiscale;
			params[7] = "01"; // codstatosap ATTIVA
			Object queryRes = transExec.executeQuery("INSERT_SP_LAVORATORE_INVIO_SAP", params, "INSERT");
			if (queryRes == null || !(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
				throw new Exception("Impossibile eseguire update del codice sap in an_yg_dati_invio");
			}

			Object[] paramsEstr = new Object[5];
			paramsEstr[0] = codMinSapInvioSap;
			paramsEstr[1] = "01"; // codTipoVariazione
			paramsEstr[2] = messaggioErrore.value;
			paramsEstr[3] = esito.value;
			paramsEstr[4] = cdnLavoratore;
			Object queryResEstr = transExec.executeQuery("UPDATE_TS_TRACCIAMENTO_SAP", paramsEstr, "UPDATE");
			if (queryResEstr == null
					|| !(queryResEstr instanceof Boolean && ((Boolean) queryResEstr).booleanValue() == true)) {
				throw new Exception("Impossibile eseguire update del codice sap in ts_estrazione_sap");
			}
		}

		if ((Risposta_invioSAP_TypeEsito.KO).equals(esito.value)) {
			Object[] paramsEstr = new Object[5];
			paramsEstr[0] = codMinSapInvioSap;
			paramsEstr[1] = "01"; // codTipoVariazione
			paramsEstr[2] = messaggioErrore.value;
			paramsEstr[3] = esito.value;
			paramsEstr[4] = cdnLavoratore;
			Object queryResEstr = transExec.executeQuery("UPDATE_TS_TRACCIAMENTO_SAP", paramsEstr, "UPDATE");
			if (queryResEstr == null
					|| !(queryResEstr instanceof Boolean && ((Boolean) queryResEstr).booleanValue() == true)) {
				throw new Exception("Impossibile eseguire update del codice sap in ts_estrazione_sap");
			}
		}

		logger.info("FINE CHIAMATA INVIO SAP, cdnLavoratore =" + cdnLavoratore);
	}

	/* Valida l'xml passato in input con l'xsd passato come secondo parametro */
	private static boolean validazioneXml(String xml, String xsdPath) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

			File schemaFile = new File(xsdPath);
			StreamSource streamSource = new StreamSource(schemaFile);
			Schema schema = factory.newSchema(streamSource);
			Validator validator = schema.newValidator();
			StreamSource datiXmlStreamSource = new StreamSource(new StringReader(xml));
			validator.validate(datiXmlStreamSource);
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}
}
