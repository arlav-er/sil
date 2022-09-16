package it.eng.sil.module.ido;

import java.sql.Types;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.security.User;

// @author: Stefania Orioli

public class SalvaRosaDef extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SalvaRosaDef.class.getName());

	public SalvaRosaDef() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {
		String prgRichiestaAz = (String) request.getAttribute("PRGRICHIESTAAZ");
		String prgRichiestaOrig = (String) request.getAttribute("PRGORIG");
		String prgRosa = (String) request.getAttribute("PRGROSA");
		ReportOperationResult ror = new ReportOperationResult(this, response);
		TransactionQueryExecutor queryExecutor = null;
		boolean attivaTrigger = true;
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		DataConnectionManager dcm = null;

		try {
			if ((prgRichiestaAz == null) || (prgRosa == null) || (prgRichiestaOrig == null)) {
				attivaTrigger = false;
				throw new Exception(
						"uno dei valori, o tutti e 3, prgRichiestaAz, prgRichiestaOrig e prgRosa sono nulli!");
			}
			String pool = (String) getConfig().getAttribute("POOL");
			SourceBean stmRosa = (SourceBean) getConfig().getAttribute("QUERIES.QUERY_ROSA");
			SourceBean stmIncrocio = (SourceBean) getConfig().getAttribute("QUERIES.QUERY_RICH");
			queryExecutor = new TransactionQueryExecutor(pool);
			queryExecutor.initTransaction();
			Boolean ris = new Boolean(false);
			boolean cancOk = false;

			// CONTROLLO SE DEVO ESEGUIRE LA CANCELLAZIONE AUTOMATICA
			SourceBean stmCheckCanc = (SourceBean) getConfig().getAttribute("QUERIES.QUERY_CHECK_CANC");
			String statement = stmCheckCanc.getAttribute("STATEMENT").toString();
			String sqlStr = SQLStatements.getStatement(statement);
			// String stmCheckCanc = "SELECT nvl(FLGCANCELLADAROSA,'S') AS
			// FLGCANCELLADAROSA FROM TS_GENERALE WHERE ROWNUM=1";

			SourceBean stmCancAuto = (SourceBean) getConfig().getAttribute("QUERIES.QUERY_CANC");
			dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);
			SQLCommand stmt = conn.createSelectCommand(sqlStr);
			DataResult res = stmt.execute();
			ScrollableDataResult sdr = (ScrollableDataResult) res.getDataObject();
			SourceBean rowsSourceBean = sdr.getSourceBean();
			String checkCanc = (String) rowsSourceBean.getAttribute("ROW.FLGCANCELLADAROSA");
			if (checkCanc.equals("S")) {
				String p_prgRosa = (String) request.getAttribute("PRGROSA");
				// Trovo l'utente connesso
				RequestContainer requestContainer = getRequestContainer();
				SessionContainer sessionContainer = requestContainer.getSessionContainer();
				// Recupero utente
				User user = (User) sessionContainer.getAttribute(User.USERID);
				String p_cdnUt = Integer.toString(user.getCodut());
				String p_prgOrig = (String) request.getAttribute("PRGORIG");
				Object[] parameters = new Object[4];
				statement = stmCancAuto.getAttribute("STATEMENT").toString();
				parameters[0] = null;
				parameters[1] = p_prgRosa;
				parameters[2] = p_cdnUt;
				parameters[3] = p_prgOrig;
				ris = (Boolean) queryExecutor.executeQuery(statement, parameters, "UPDATE");
				if (ris.booleanValue()) {
					cancOk = true;
				} else {
					cancOk = false;
				}
			} else {
				cancOk = true;
			} // La cancellazione automatica non deve essere effettuata
			if (!cancOk) {
				attivaTrigger = false;
				throw new Exception(
						"Impossibile eseguire la canc. automatica durante il salvataggio della rosa definitiva");
			}

			// STATO DELLA RICHIESTA
			ris = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), stmIncrocio,
					"UPDATE");
			if (!ris.booleanValue()) {
				attivaTrigger = false;
				throw new Exception(
						"Impossibile modificare lo stato della richiesta durante il salvataggio della rosa definitiva");
			}

			// STATO DELLA ROSA
			ris = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), stmRosa,
					"UPDATE");
			if (!ris.booleanValue()) {
				attivaTrigger = false;
				throw new Exception("Impossibile salvare la rosa definitiva");
			}

			queryExecutor.commitTransaction();
			Utils.releaseResources(conn, command, dr);
			// dcm = null;

			// Spostato fuori dalla transaction altrimenti legge i valori errati
			// sul tipo di rosa!!! (19/10/2005)
			// AGGIORNO STORIA ROSA
			if (attivaTrigger) {
				// Se procedo così non esegue il rollback!
				statement = "{ call PG_STORIA_ROSA.PDAGGSTORIAROSAXSTATO(?) }";
				// dcm = DataConnectionManager.getInstance();

				DataConnection conn2 = null;
				DataResult dr2 = null;
				StoredProcedureCommand command2 = null;

				try {
					conn2 = dcm.getConnection(pool);
					int paramIndex = 0;
					command2 = (StoredProcedureCommand) conn2.createStoredProcedureCommand(statement);
					ArrayList parameters = new ArrayList(1);
					// preparazione del Parametri di Input: p_prgRosa
					parameters.add(conn2.createDataField("p_prgRosa", Types.VARCHAR, prgRosa));
					command2.setAsInputParameters(paramIndex++);
					// Chiamata alla Stored Procedure
					dr2 = command2.execute(parameters);
				} finally {
					Utils.releaseResources(conn2, command2, dr2);
				}
				// CompositeDataResult cdr = (CompositeDataResult)
				// dr.getDataObject();

				/*
				 * Non si puo' fare -> Franco ha modificato QueryExecutorObject!!!! SourceBean stmTrig = (SourceBean)
				 * getConfig().getAttribute("QUERIES.QUERY_TRIG"); statement =
				 * stmTrig.getAttribute("STATEMENT").toString(); Object[] parameters = new Object[1]; parameters[0] =
				 * prgRosa; ris = (Boolean) queryExecutor.executeQuery(statement, parameters, "STORED");
				 * if(!ris.booleanValue()) { throw new Exception("ERRORE nell'esecuzione di PDAGGSTORIAROSAXSTATO"); }
				 */
			}

		} catch (Exception e) {
			queryExecutor.rollBackTransaction();
			ror.reportFailure(MessageCodes.General.UPDATE_FAIL);
			it.eng.sil.util.TraceWrapper.error(_logger, className + ":: transaction update failure: ", e);

		} finally {
			Utils.releaseResources(conn, command, dr);
		}

	}
}
