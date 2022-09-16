package it.eng.sil.module.ido;

import java.math.BigInteger;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class CMAnnullaGradAnnuale extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CMAnnullaGradAnnuale.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws EMFInternalError {

		RequestContainer reqCont = getRequestContainer();
		ResponseContainer resCont = getResponseContainer();
		ReportOperationResult ror = new ReportOperationResult(this, response);

		disableMessageIdSuccess();

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean errors = false;
		TransactionQueryExecutor transExec = null;
		try {
			transExec = new TransactionQueryExecutor(getPool());
			enableTransactions(transExec);

			transExec.initTransaction();

			// calcolo posizione nominativi in gradutaoria
			annullaGraduatoria(request, response, transExec);

			transExec.commitTransaction();
			ror.reportSuccess(idSuccess);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"CMAnnullaGradAnnuale::service(): Impossibile annullare la graduatoria!", ex);

			// ror.reportFailure(idFail);
			// ror.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			errors = true;
		}
	}

	private void annullaGraduatoria(SourceBean request, SourceBean response, TransactionQueryExecutor txExec)
			throws Exception {

		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		String p_prgGraduatoria = "";

		p_prgGraduatoria = (String) request.getAttribute("PRGGRADUATORIA");

		SourceBean statementSB = null;
		String statement = "";
		String sqlStr = "";
		String codiceRit = "";
		String p_cdnUtente = String.valueOf(user.getCodut());

		int paramIndex = 0;
		ArrayList parameters = null;
		conn = txExec.getDataConnection();

		statementSB = (SourceBean) getConfig().getAttribute("CM_ANNULLA_GRAD_ANNUALE");
		statement = statementSB.getAttribute("STATEMENT").toString();
		sqlStr = SQLStatements.getStatement(statement);
		command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

		parameters = new ArrayList(3);
		// 1.Parametro di Ritorno
		parameters.add(conn.createDataField("codiceRit", java.sql.Types.VARCHAR, null));
		command.setAsOutputParameters(paramIndex++);
		// 2 prgGraduatoria OLD
		parameters.add(conn.createDataField("prgGraduatoria", java.sql.Types.BIGINT, new BigInteger(p_prgGraduatoria)));
		command.setAsInputParameters(paramIndex++);
		// 3 utente modifica
		parameters.add(conn.createDataField("p_cdnUtente", java.sql.Types.BIGINT, new BigInteger(p_cdnUtente)));
		command.setAsInputParameters(paramIndex++);

		// Chiamata alla Stored Procedure
		dr = command.execute(parameters);

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
				msg = "Annulla graduatoria annuale";

				break;
			default:
				msgCode = MessageCodes.General.OPERATION_FAIL;
				msg = "Annulla graduatoria annuale: errore di ritorno non ammesso.";

			}
			response.setAttribute("error", "true");
			response.setAttribute((SourceBean) row);
			ror.reportFailure(msgCode);
			_logger.debug(msg);

		}

	}
}