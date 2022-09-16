package it.eng.sil.util.batch;

import java.io.File;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.util.amministrazione.impatti.LogBatch;
import it.eng.sil.util.batch.mdb.BatchObject;
import it.eng.sil.util.batch.mdb.IBatchMDBConsumer;

public class BatchChiusuraCmSuperamentoEta implements IBatchMDBConsumer {
	private BatchObject batchObject;
	private String[] parametri;
	private LogBatch logBatch;

	public BatchChiusuraCmSuperamentoEta(BatchObject batchObject) throws Exception {
		this.batchObject = batchObject;
		String dir = ConfigSingleton.getLogBatchPath();
		String nomeFile = File.separator + "BatchChiusuraCmSuperamentoEta";
		String data = "";

		data = DateUtils.getNow(); // data in cui viene schedulato

		data = data.replace('/', '-');
		nomeFile = nomeFile + data + ".log";
		logBatch = new LogBatch(nomeFile, dir);
	}

	@Override
	public void execBatch() {
		try {
			if (!this.setParametri()) {
				this.logBatch.writeLog(
						"Errore. Non sono stati passati tutti i parametri : " + this.batchObject.getParams().length);
				return;
			}
			this.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		try {
			String msgParametri = "";
			logBatch.writeLog("========================== PARAMETRI BATCH ============================");
			logBatch.writeLog(msgParametri);

			if (parametri[0] != null) {
				msgParametri = "Data riferimento eta : " + parametri[0].toString();
				logBatch.writeLog(msgParametri);
			}

			msgParametri = "";
			logBatch.writeLog(msgParametri);
			logBatch.writeLog("========================================================================");
			msgParametri = "";
			logBatch.writeLog(msgParametri);

			// Stampa delle variabili java
			logBatch.writeLogVarJava(DateUtils.getNow());
			logBatch.writeLogVarJava("====== Variabili di configurazione java ======");
			Properties p = java.lang.System.getProperties();
			Enumeration<?> e = p.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String val = p.getProperty(key);
				logBatch.writeLogVarJava(key + ": " + val + ";");
			}
			logBatch.writeLogVarJava("====== Fine sezione di configurazione ========");

			String p_dataRifEta = parametri[0].equals("00/00/0000") ? null : parametri[0];
			String p_ut = parametri[1];
			String p_key = System.getProperty("_ENCRYPTER_KEY_");

			logBatch.writeLog("");
			logBatch.writeLog("====== Inizio esecuzione Batch ======");
			logBatch.writeLog("");

			String ret = executeStored(p_dataRifEta, p_ut, p_key);

			logBatch.writeLog(ret);

			logBatch.writeLog("");
			logBatch.writeLog("====== Fine esecuzione Batch ======");
			logBatch.writeLog("");

			return;
		} catch (Exception se) {
			logBatch.writeLog(se.toString());
		}
	}

	String executeStored(String p_dataRifEta, String p_ut, String p_key) throws Exception {
		StoredProcedureCommand command = null;
		DataConnection conn = null;
		String result = null;
		TransactionQueryExecutor trans = null;
		try {
			trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			conn = trans.getDataConnection();
			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call PG_COLL_MIRATO.BatchChiusuraCmSuperamentoEta(?, ?, ?, ?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(3);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("p_result", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("p_dataRifEta", Types.VARCHAR, p_dataRifEta));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("p_ut", Types.VARCHAR, p_ut));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("p_key", Types.VARCHAR, p_key));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			DataResult dr = command.execute(parameters);
			PunctualDataResult cdr = (PunctualDataResult) dr.getDataObject();

			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			result = df.getStringValue();

			if (result != null)
				logBatch.writeLog(result);
		} catch (Exception e) {
			logBatch.writeLog(e.toString());
			throw new Exception("Errore nell'esecuzione della stored");
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return (result == null ? "" : result);
	}

	public boolean setParametri() {
		String[] args = this.batchObject.getParams();
		if (args.length < 4) {
			return false;
		}
		parametri = new String[4];
		parametri[0] = args[0]; // Data riferimento controllo età

		parametri[1] = args[1]; // user
		parametri[2] = args[2]; // profilo user
		parametri[3] = args[3]; // gruppo user
		return true;
	}
}
