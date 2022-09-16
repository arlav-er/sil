package it.eng.sil.module.collocamentoMirato;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
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

public class BatchAggiornaProspettoDaMovimento implements IBatchMDBConsumer {
	// FV 27/02/2008
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(BatchAggiornaProspettoDaMovimento.class.getName());

	public static final String APP_NAME = "BatchAggiornaProspettoDaMovimento";
	private BatchObject batchObject;
	private String[] parametri;
	private LogBatch logBatch;
	private String dataBatch = DateUtils.getNow();

	public BatchAggiornaProspettoDaMovimento(BatchObject batchObject) {
		_logger.debug("==== INIZIO ====");
		this.batchObject = batchObject;
		String dir = ConfigSingleton.getLogBatchPath();
		String nomeFile = File.separator + "BatchAggiornaProspettoDaMovimento";
		_logger.debug("nomeFile=" + nomeFile);
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

	public boolean start() {
		// boolean check = false;
		_logger.debug("==== START ====");

		TransactionQueryExecutor trans = null;
		try {

			if (logBatch == null) {
				System.err.println("Log non inizializzzato");
				System.exit(1);
			}

			logBatch.writeLog("=========== Avvio Batch per aggiornare tutti i prospetti dai movimenti  ===========");

			RequestContainer requestContainer = new RequestContainer();
			SessionContainer sessionContainer = new SessionContainer(true);
			sessionContainer.setAttribute("_CDUT_", new BigDecimal(parametri[1]));
			sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
			// User user = new UserBatch().getUser(parametri[0], parametri[1], parametri[2]);
			// sessionContainer.setAttribute(User.USERID,user);
			requestContainer.setSessionContainer(sessionContainer);

			trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			trans.initTransaction();

			logBatch.writeLog("=========== INIZIO CHIAMATA PROCEDURA AGGIORNAMENTO  ===========");

			// getAggiornaProsp(trans);

			trans.commitTransaction();
			logBatch.writeLog("=========== AGGIORNAMENTO AVVENUTO CORRETTAMENTE  ===========");
			logBatch.writeLog("=========== FINE CHIAMATA PROCEDURA AGGIORNAENTO  ===========");
			return true;
		} catch (Exception e2) {
			_logger.fatal("", e2);
			if (trans != null) {
				try {
					trans.rollBackTransaction();
				} catch (Exception ex) {
					_logger.fatal("", ex);
				}
			}
			logBatch.writeLog("=========== ERRORE PROCEDURA AGGIORNAMENTO PROSPETTI DA MOVIMENTI ===========");
			return false;
		}
	}

	public void getAggiornaProsp(TransactionQueryExecutor txExecutor) throws Exception {
		// ArrayList retList = null;
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;

		// DataConnectionManager dcm = DataConnectionManager.getInstance();
		conn = txExecutor.getDataConnection();

		// SourceBean statementSB = null;
		// String statement = "";
		// String sqlStr = "";
		String codiceRit = "";
		int paramIndex = 0;
		ArrayList parameters = null;
		// String query_select = "";
		// boolean grigliaOk = false;
		// SQLCommand stmt = null;
		// DataResult res = null;
		// ScrollableDataResult sdr = null;
		// SourceBean rowsSourceBean = null;
		String dataProspetto = parametri[0];
		String encryptKey = System.getProperty("_ENCRYPTER_KEY_");
		String p_cdnUtente = parametri[1];

		_logger.debug("param procedure: dataProspetto=" + dataProspetto + ", encryptKey=" + encryptKey
				+ ", p_cdnUtente=" + p_cdnUtente);

		command = (StoredProcedureCommand) conn
				.createStoredProcedureCommand("{ call ? := PG_COLL_MIRATO_2.BATCH_AGG_PROSPETTI_DA_MOV(?,?,?) }");

		parameters = new ArrayList(4);

		// 1. Parametro di Ritorno
		parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);

		// 2. p_dataProspetto
		parameters.add(conn.createDataField("p_dataProspetto", java.sql.Types.VARCHAR, dataProspetto));
		command.setAsInputParameters(paramIndex++);
		// 3 p_cdnUtente
		parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
		command.setAsInputParameters(paramIndex++);
		// 4 p_key
		parameters.add(conn.createDataField("p_key", java.sql.Types.VARCHAR, encryptKey));
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
			new Exception("Errore applicativo: codRit=" + codiceRit);
		}

	}

	public void setParametri() {
		String[] args = this.batchObject.getParams();
		if (logBatch == null) {
			String dir = ConfigSingleton.getLogBatchPath();
			String nomeFile = File.separator + "BatchAggiornaProspettoDaMovimento";
			String data = "";
			data = DateUtils.getNow();
			dataBatch = data;
			data = data.replace('/', '-');
			nomeFile = nomeFile + data + ".log";
			logBatch = new LogBatch(nomeFile, dir);
		}

		parametri = new String[5];
		if (args.length == 4) { // viene chiamato dalla pagina
			parametri[0] = args[3]; // data fine range (usata)

		} else {
			parametri[0] = dataBatch; // data fine range (usata)
		}

		parametri[1] = args[0]; // user //Se avviati da .bat impostarlo di
								// default
		parametri[2] = args[1]; // profilo user //Se avviati da .bat impostarlo
								// di default
		parametri[3] = args[2]; // gruppo user //Se avviati da .bat impostarlo
								// di default

		_logger.debug("par 0=" + parametri[0] + ", par 1=" + parametri[1] + ", par 2=" + parametri[2] + ", par 3="
				+ parametri[3]);

	}

	/*
	 * public void setParametri(String[] args) { parametri = new String[5];
	 * 
	 * 
	 * _logger.debug("parametri[0]=" + args[0]);
	 * 
	 * if(args.length == 4) { //viene chiamato dalla pagina parametri[0] = args[1]; //user //Se avviati da .bat
	 * impostarlo di default parametri[1] = args[2]; //profilo user //Se avviati da .bat impostarlo di default
	 * parametri[2] = args[3]; //gruppo user //Se avviati da .bat impostarlo di default parametri[3] = args[0]; //data
	 * prospetto
	 * 
	 * //logBatch.writeLog("par 0=" + parametri[0] + ", par 1="+parametri[1]+ ", par 2="+ parametri[2]+ ", par 3="+
	 * parametri[3]); } else { //viene schedulato parametri[0] = args[0]; //user //Se avviati da .bat impostarlo di
	 * default parametri[1] = args[1]; //profilo user //Se avviati da .bat impostarlo di default parametri[2] = args[2];
	 * //gruppo user //Se avviati da .bat impostarlo di default parametri[3] = DateUtils.getNow(); //data prospetto
	 * 
	 * logBatch.writeLog("par 0=" + parametri[0] + ", par 1="+parametri[1]+ ", par 2="+ parametri[2]+ ", par 3="+
	 * parametri[3]+ ", par 4="+ parametri[4]); }
	 * 
	 * }
	 */

	@Override
	public void execBatch() {
		try {
			this.setParametri();
			this.start();

		} catch (Exception e) {
			_logger.fatal(e);
			// e.printStackTrace();
		}
	}

}
