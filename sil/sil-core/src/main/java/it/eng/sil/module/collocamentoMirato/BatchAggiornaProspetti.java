package it.eng.sil.module.collocamentoMirato;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.util.amministrazione.impatti.LogBatch;
import it.eng.sil.util.batch.BatchRunnable;
import it.eng.sil.util.batch.mdb.BatchObject;
import it.eng.sil.util.batch.mdb.IBatchMDBConsumer;

public class BatchAggiornaProspetti implements BatchRunnable, IBatchMDBConsumer {

	public static final String APP_NAME = "BatchAggiornaProspetti";
	private BatchObject batchObject;
	private String[] parametri;
	private LogBatch logBatch;
	private String dataBatch = DateUtils.getNow();

	public BatchAggiornaProspetti(BatchObject batchObject) {
		this.batchObject = batchObject;
		String dir = ConfigSingleton.getLogBatchPath();
		String nomeFile = File.separator + "BatchAggiornaProspetti";

		if (this.batchObject.getParams().length == 4) { // viene chiamato dalla pagina
			dataBatch = this.batchObject.getParams()[0];
		} else {
			dataBatch = DateUtils.getNow(); // viene schedulato
		}

		String data = DateUtils.getNow();
		data = data.replace('/', '-');

		nomeFile = nomeFile + data + ".log";
		logBatch = new LogBatch(nomeFile, dir);
	}

	public void start() {
		boolean check = false;
		TransactionQueryExecutor trans = null;
		try {

			if (logBatch == null) {
				System.err.println("Log non inizializzzato");
				System.exit(1);
			}

			logBatch.writeLog("=========== Avvio Batch per aggiornare tutti i prospetti  ===========");
			// logBatch.writeLog("par 0=" + parametri[0] + ", par 1="+parametri[1]+ ", par 2="+ parametri[2]+ ", par
			// 3="+ parametri[3]);

			RequestContainer requestContainer = new RequestContainer();
			SessionContainer sessionContainer = new SessionContainer(true);
			sessionContainer.setAttribute("_CDUT_", new BigDecimal(parametri[1]));
			sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
			requestContainer.setSessionContainer(sessionContainer);

			trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			trans.initTransaction();

			logBatch.writeLog("=========== INIZIO CHIAMATA PROCEDURA AGGIORNAENTO  ===========");

			check = getAggiornaProsp(trans);

			if (!check) {
				trans.rollBackTransaction();
				logBatch.writeLog("=========== ERRORE PROCEDURA AGGIORNAMENTO PROSPETTI ===========");
				throw new Exception("ERRORE PROCEDURA AGGIORNAMENTO PROSPETTI");
			} else {
				trans.commitTransaction();
				logBatch.writeLog("=========== AGGIORNAMENTO AVVENUTO CORRETTAMENTE  ===========");
			}

			logBatch.writeLog("=========== FINE CHIAMATA PROCEDURA AGGIORNAENTO  ===========");
		} catch (SourceBeanException e1) {
			if (trans != null) {
				try {
					trans.rollBackTransaction();
				} catch (EMFInternalError e) {
					// TODO Auto-generated catch block
					logBatch.writeLog("=========== EXCEPTION:  \n" + e + "===========");
				}
			}
			e1.printStackTrace();
			logBatch.writeLog("=========== EXCEPTION:  \n" + e1 + "===========");
		} catch (Exception e2) {
			if (trans != null) {
				try {
					trans.rollBackTransaction();
				} catch (EMFInternalError e) {
					// TODO Auto-generated catch block
					logBatch.writeLog("=========== EXCEPTION:  \n" + e + "===========");
				}
			}
			e2.printStackTrace();
			logBatch.writeLog("=========== \n" + e2 + "  ===========");
		}
	}

	public boolean getAggiornaProsp(TransactionQueryExecutor txExecutor) throws Exception {
		ArrayList retList = null;
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;

		try {
			conn = txExecutor.getDataConnection();

			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			String codiceRit = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			String query_select = "";
			boolean grigliaOk = false;
			SQLCommand stmt = null;
			DataResult res = null;
			ScrollableDataResult sdr = null;
			SourceBean rowsSourceBean = null;
			String dataProspetto = dataBatch;

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_COLL_MIRATO_2.BATCH_AGGIORNA_PROSPETTI(?) }");

			parameters = new ArrayList(2);

			// 1. Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// 2. p_dataProspetto
			parameters.add(conn.createDataField("p_dataProspetto", java.sql.Types.VARCHAR, dataProspetto));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			// Reperisco i valori di output della stored
			// 0. Codice di Ritorno
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			codiceRit = df.getStringValue();

			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			// Predispongo la Response
			if (!codiceRit.equals("0")) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			throw (e);
		}
	}

	public void setParametri() {
		String[] args = this.batchObject.getParams();

		parametri = new String[5];

		if (args.length == 4) { // viene chiamato dalla pagina
			parametri[0] = args[1]; // user //Se avviati da .bat impostarlo di default
			parametri[1] = args[2]; // profilo user //Se avviati da .bat impostarlo di default
			parametri[2] = args[3]; // gruppo user //Se avviati da .bat impostarlo di default
			parametri[3] = args[0]; // data prospetto

			// logBatch.writeLog("par 0=" + parametri[0] + ", par 1="+parametri[1]+ ", par 2="+ parametri[2]+ ", par
			// 3="+ parametri[3]);
		} else { // viene schedulato
			parametri[0] = args[0]; // user //Se avviati da .bat impostarlo di default
			parametri[1] = args[1]; // profilo user //Se avviati da .bat impostarlo di default
			parametri[2] = args[2]; // gruppo user //Se avviati da .bat impostarlo di default
			parametri[3] = DateUtils.getNow(); // data prospetto

			// logBatch.writeLog("par 0=" + parametri[0] + ", par 1="+parametri[1]+ ", par 2="+ parametri[2]+ ", par
			// 3="+ parametri[3]+ ", par 4="+ parametri[4]);
		}

	}

	@Override
	public void execBatch() {
		try {
			this.setParametri();
			this.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
