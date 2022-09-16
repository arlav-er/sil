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

public class BatchCancellaMovAppErrore implements IBatchMDBConsumer {
	private BatchObject batchObject;
	private String[] parametri;
	private LogBatch logBatch;

	public BatchCancellaMovAppErrore(BatchObject batchObject) throws Exception {
		this.batchObject = batchObject;
		String dir = ConfigSingleton.getLogBatchPath();
		String nomeFile = File.separator + "BatchCancellaMovAppErr";
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
				this.logBatch.writeLog("Errore. Non sono stati passati tutti i parametri");
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
				msgParametri = "Data inizio range movimenti : " + parametri[0].toString();
				logBatch.writeLog(msgParametri);
			}

			if (parametri[1] != null) {
				msgParametri = "Data fine range movimenti : " + parametri[1].toString();
				logBatch.writeLog(msgParametri);
			}

			if (parametri[2] != null) {
				msgParametri = "Tipo movimenti : " + parametri[2].toString();
				logBatch.writeLog(msgParametri);
			}

			if (parametri[3] != null) {
				msgParametri = "CF azienda : " + parametri[3].toString();
				logBatch.writeLog(msgParametri);
			}

			if (parametri[4] != null) {
				msgParametri = "CF lavoratore : " + parametri[4].toString();
				logBatch.writeLog(msgParametri);
			}

			if (parametri[5] != null) {
				msgParametri = "Codice unico comunicazione : " + parametri[5].toString();
				logBatch.writeLog(msgParametri);
			}

			if (parametri[6] != null) {
				msgParametri = "Codice errore da considerare : " + parametri[6].toString();
				logBatch.writeLog(msgParametri);
			}

			if (parametri[7] != null) {
				msgParametri = "Data inizio range inserimento movimenti : " + parametri[7].toString();
				logBatch.writeLog(msgParametri);
			}

			if (parametri[8] != null) {
				msgParametri = "Data fine range inserimento movimenti : " + parametri[8].toString();
				logBatch.writeLog(msgParametri);
			}

			if (parametri[9] != null) {
				msgParametri = "Codice utente inserimento movimenti : " + parametri[9].toString();
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

			String P_dataDA = parametri[0].equals("00/00/0000") ? null : parametri[0];
			String P_dataA = parametri[1].equals("00/00/0000") ? null : parametri[1];
			String P_tipoMov = parametri[2].equals("X") ? null : parametri[2];
			String P_cfAz = parametri[3].equals("X") ? null : parametri[3];
			String P_cfLav = parametri[4].equals("X") ? null : parametri[4];
			String P_codCom = parametri[5].equals("X") ? null : parametri[5];
			String P_codErr = parametri[6];
			String P_dataInsDA = parametri[7].equals("00/00/0000") ? null : parametri[7];
			String P_dataInsA = parametri[8].equals("00/00/0000") ? null : parametri[8];
			String P_utIns = parametri[9].equals("X") ? null : parametri[9];

			logBatch.writeLog("");
			logBatch.writeLog("====== Inizio esecuzione Batch ======");
			logBatch.writeLog("");

			String ret = executeStored("", P_codErr, P_dataDA, P_dataA, P_tipoMov, P_cfAz, P_cfLav, P_codCom,
					P_dataInsDA, P_dataInsA, P_utIns);

			logBatch.writeLog(ret);

			logBatch.writeLog("");
			logBatch.writeLog("====== Fine esecuzione Batch ======");
			logBatch.writeLog("");

			return;
		} catch (Exception se) {
			logBatch.writeLog(se.toString());
		}
	}

	String executeStored(String result, String codiceErr, String dataDA, String dataA, String tipoMov, String cfAz,
			String cfLav, String codCom, String dataInsDA, String dataInsA, String utIns) throws Exception {
		StoredProcedureCommand command = null;
		DataConnection conn = null;
		String codiceRit = null;
		TransactionQueryExecutor trans = null;
		try {
			trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			conn = trans.getDataConnection();
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(
					"{ call PG_MOVIMENTI.CANC_MOV_APP_ERR(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(8);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("result", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("codiceErr", Types.VARCHAR, codiceErr));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("dataDA", Types.VARCHAR, dataDA));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("dataA", Types.VARCHAR, dataA));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("tipoMov", Types.VARCHAR, tipoMov));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("cfAz", Types.VARCHAR, cfAz));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("cfLav", Types.VARCHAR, cfLav));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("codCom", Types.VARCHAR, codCom));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("dataInsDA", Types.VARCHAR, dataInsDA));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("dataInsA", Types.VARCHAR, dataInsA));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("utIns", Types.VARCHAR, utIns));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			DataResult dr = command.execute(parameters);
			PunctualDataResult cdr = (PunctualDataResult) dr.getDataObject();

			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			codiceRit = df.getStringValue();
		} catch (Exception e) {
			logBatch.writeLog(e.toString());
			throw new Exception("Errore nell'esecuzione della stored");
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return (codiceRit == null ? "" : codiceRit);
	}

	public boolean setParametri() {
		String[] args = this.batchObject.getParams();
		if (args.length != 13) {
			return false;
		}

		parametri = new String[13];
		parametri[0] = args[0]; // Data inizio range in cui prendere i movimenti.
		parametri[1] = args[1]; // Data fine range in cui prendere i movimenti
		parametri[2] = args[2]; // Tipo movimenti da considerare
		parametri[3] = args[3]; // CF Azienda dei movimenti da considerare
		parametri[4] = args[4]; // CF Lavoratore dei movimenti da considerare
		parametri[5] = args[5]; // Codice unico comunicazione del movimento da considerare
		parametri[6] = args[6]; // Codice dell'errore in base al quale considerare i movimenti
		parametri[7] = args[7]; // Data inizio range in cui sono stati inseriti i movimenti
		parametri[8] = args[8]; // Data fine range in cui sono stati inseriti i movimenti
		parametri[9] = args[9]; // Utente di inserimento dei movimenti

		parametri[10] = args[10]; // user
		parametri[11] = args[11]; // profilo user
		parametri[12] = args[12]; // gruppo user

		return true;
	}
}
