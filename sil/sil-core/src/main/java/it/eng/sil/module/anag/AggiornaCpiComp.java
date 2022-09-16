package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class AggiornaCpiComp extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AggiornaCpiComp.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {
		disableMessageIdSuccess();
		disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		TransactionQueryExecutor txExecutor = new TransactionQueryExecutor(getPool(), this);
		try {

			txExecutor.initTransaction();
			enableTransactions(txExecutor);

			// doUpdate(request, response);
			// sostituito con una procedure sul db
			aggCpiCompetente(txExecutor, request, response);

			setSectionQueryUpdate("UPDATE_AN_LAV_CPI");

			doUpdate(request, response);
			txExecutor.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		} catch (Throwable t) {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL, (Exception) t, "service", "");
			it.eng.sil.util.TraceWrapper.debug(_logger, "AggiornaCpiComp.service()", (Exception) t);

			txExecutor.rollBackTransaction();
		}
	}

	private void aggCpiCompetente(TransactionQueryExecutor tx, SourceBean request, SourceBean response)
			throws Exception {
		java.sql.Connection connection = tx.getDataConnection().getInternalConnection();
		java.sql.CallableStatement stmt = connection.prepareCall("{call pg_gestamm.aggiornaCpiCompetente(?,?,?) }");
		Object user = getRequestContainer().getSessionContainer().getAttribute("_CDUT_");

		String p_prgLavStoriaInf = StringUtils.getAttributeStrNotNull(request, "prgLavStoriaInf");
		String p_newCodCpi = StringUtils.getAttributeStrNotNull(request, "newCodCpi");

		// stmt.registerOutParameter(1, OracleTypes.VARCHAR);
		stmt.setString(1, user.toString());
		stmt.setString(2, p_prgLavStoriaInf);
		stmt.setString(3, p_newCodCpi);
		stmt.execute();
	}
}