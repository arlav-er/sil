package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import oracle.jdbc.OracleTypes;

public class CMProspCalcoloSommeTempiParziali extends AbstractModule {
	public CMProspCalcoloSommeTempiParziali() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);

			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			int paramIndex = 0;
			ArrayList parameters = null;

			ScrollableDataResult sdr = null;
			SourceBean rowsSourceBean = null;
			String p_prgProspettoInf = "";
			String codTipoPTDisabile = (String) request.getAttribute("CODTIPOPTDISABILE");

			Object prgProspettoInf = request.getAttribute("PRGPROSPETTOINF");
			if (prgProspettoInf == null)
				throw new Exception();
			if (prgProspettoInf instanceof BigDecimal)
				p_prgProspettoInf = ((BigDecimal) prgProspettoInf).toString();
			else if (prgProspettoInf instanceof String)
				p_prgProspettoInf = (String) prgProspettoInf;

			statementSB = (SourceBean) getConfig().getAttribute("QUERY");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);
			parameters = new ArrayList(2);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("risultato", OracleTypes.CURSOR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// 2. p_prgProspettoInf
			parameters.add(conn.createDataField("p_prgProspettoInf", java.sql.Types.BIGINT,
					new BigInteger(p_prgProspettoInf)));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				sdr = (ScrollableDataResult) dr.getDataObject();
			}
			// recupero il SourceBean
			rowsSourceBean = sdr.getSourceBean();
			// setto il SourceBean nella Response
			if (rowsSourceBean != null) {
				response.setAttribute((SourceBean) rowsSourceBean);
			}

			ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		} catch (Exception e) {
			String msg = "Errore nel calcolo della scopertura del prospetto ";
			ror.reportFailure(e, className, msg);
		} finally {
			Utils.releaseResources(conn, command, dr);
		}
	}
}