/*
 * Creato il 11-mag-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.engiweb.framework.base.ApplicationContainer;
import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.security.User;
import it.eng.sil.util.amministrazione.impatti.LogBatch;
import it.eng.sil.util.amministrazione.impatti.UserBatch;
import it.eng.sil.util.batch.BatchRunnable;

public class BatchControlloMovimentiDoppi implements BatchRunnable {

	public static final String APP_NAME = "BatchControlloMovimentiDoppi";
	private String[] parametri;
	private LogBatch logBatch;
	private ApplicationContainer applicationContainer;
	private String dataBatch = DateUtils.getNow();

	public BatchControlloMovimentiDoppi(String[] args) {
		String dir = ConfigSingleton.getLogBatchPath();
		String nomeFile = File.separator + "BatchControlloMovimentiDoppi";

		nomeFile = nomeFile + dataBatch + ".log";
		logBatch = new LogBatch(nomeFile, dir);

		applicationContainer = ApplicationContainer.getInstance();
	}

	public void start() {
		boolean check = false;
		TransactionQueryExecutor trans = null;
		try {

			if (logBatch == null) {
				System.err.println("Log non inizializzzato");
				System.exit(1);
			}

			logBatch.writeLog("=========== Avvio Batch per validare tutti i movimenti  ===========");

			RequestContainer requestContainer = new RequestContainer();
			SessionContainer sessionContainer = new SessionContainer(true);
			sessionContainer.setAttribute("_CDUT_", new BigDecimal(parametri[1]));
			User user = new UserBatch().getUser(parametri[0], parametri[1], parametri[2]);
			sessionContainer.setAttribute(User.USERID, user);
			requestContainer.setSessionContainer(sessionContainer);

			trans = new TransactionQueryExecutor(Values.DB_SIL_DATI);

			if (parametri[3] == null) {
				logBatch.writeLog("=========== ERRORE DATA INIZIO MOVIMENTO NON VALORIZZATA ===========");
				throw new Exception("ERRORE DATA INIZIO MOVIMENTO NON VALORIZZATA");
			}

			check = getControllo(trans);
			if (!check) {
				logBatch.writeLog("=========== ERRORE PROCEDURA CONTROLLO MOVIMENTI DOPPI ===========");
				throw new Exception("ERRORE PROCEDURA CONTROLLO MOVIMENTI DOPPI");
			}

		} catch (SourceBeanException e1) {
			e1.printStackTrace();
			logBatch.writeLog("=========== EXCEPTION:  \n" + e1.getMessage() + "===========");
		} catch (Exception e2) {
			e2.printStackTrace();
			logBatch.writeLog("=========== \n" + e2.getMessage() + "  ===========");
		} finally {
			if (trans != null && trans.getDataConnection() != null) {
				Utils.releaseResources(trans.getDataConnection(), null, null);
			}
		}
	}

	public boolean getControllo(TransactionQueryExecutor txExecutor) throws Exception {
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

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_MOVIMENTI_DOPPI.CONTROLLO_MOV(?,?,?,?) }");

			parameters = new ArrayList(5);

			// 1. Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// 2. p_datInizioDaMov
			parameters.add(conn.createDataField("p_datInizioDaMov", java.sql.Types.VARCHAR, parametri[3]));
			command.setAsInputParameters(paramIndex++);
			// 3. p_datInizioAMov
			parameters.add(conn.createDataField("p_datInizioAMov", java.sql.Types.VARCHAR, parametri[4]));
			command.setAsInputParameters(paramIndex++);
			// 4. p_datComunicazDaMov
			parameters.add(conn.createDataField("p_datComunicazDaMov", java.sql.Types.VARCHAR, parametri[5]));
			command.setAsInputParameters(paramIndex++);
			// 5. p_datComunicazAMov
			parameters.add(conn.createDataField("p_datComunicazAMov", java.sql.Types.VARCHAR, parametri[6]));
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
				int msgCode = 0;
				String msg = null;
				switch (Integer.parseInt(codiceRit)) {
				case -1: // errore generico sql
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "errore nella procedura";

					break;
				default:
					msgCode = MessageCodes.General.OPERATION_FAIL;
					msg = "errore nella procedura";

					break;
				}

				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			String msg = "Errore movimenti doppi ";
			e.printStackTrace();
			throw new Exception("ERRORE PROCEDURA DI CONTROLLO" + e.getMessage());
		}
	}

	public void setParametri(String[] args) {
		parametri = new String[7];

		parametri[0] = args[0]; // user //Se avviati da .bat impostarlo di
								// default
		parametri[1] = args[1]; // profilo user //Se avviati da .bat impostarlo
								// di default
		parametri[2] = args[2]; // gruppo user //Se avviati da .bat impostarlo
								// di default
		parametri[3] = args[3]; // data inizio movimento da
		parametri[4] = args[4]; // data inizio movimento a
		parametri[5] = args[5]; // data comunicazione movimento da
		parametri[6] = args[6]; // data comunicazione movimento a
	}

	public void release() {
		if (applicationContainer != null) {
			applicationContainer.release();
		}
	}

	public static void main(String[] args) {
		BatchControlloMovimentiDoppi controlloDoppi = null;
		try {
			controlloDoppi = new BatchControlloMovimentiDoppi(args);
			controlloDoppi.setParametri(args);
			controlloDoppi.start();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			controlloDoppi.release();
		}
	}
}
