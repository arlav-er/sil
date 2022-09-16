package it.eng.sil.module.ido;

import java.util.ArrayList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;

// @author: Stefania Orioli

public class GrigliaProv extends AbstractModule {
	public GrigliaProv() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		ArrayList retList = null;
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);

			String EM = StringUtils.getAttributeStrNotNull(request, "EM");
			SourceBean statementSB = null;
			String statement = "";
			String sqlStr = "";
			int paramIndex = 0;
			ArrayList parameters = null;
			String query_select = "";
			boolean grigliaOk = false;
			SQLCommand stmt = null;
			DataResult res = null;
			ScrollableDataResult sdr = null;
			SourceBean rowsSourceBean = null;

			statementSB = (SourceBean) getConfig().getAttribute("QUERY");
			statement = statementSB.getAttribute("STATEMENT").toString();
			sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);
			parameters = new ArrayList(1);
			// Parametro di Ritorno
			// parameters.add(conn.createDataField("str",
			// java.sql.Types.VARCHAR, null));
			parameters.add(conn.createDataField("str", java.sql.Types.CLOB, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			// CompositeDataResult cdr = (CompositeDataResult)
			// dr.getDataObject();
			// List outputParams = cdr.getContainedDataResult();
			// Reperisco i valori di output della stored
			// Query di Ritorno
			// PunctualDataResult pdr = ( PunctualDataResult )
			// outputParams.get(0);
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			query_select = df.getStringValue();

			stmt = conn.createSelectCommand(query_select);
			LogUtils.logDebug("GrigliaProv", query_select, this);
			res = stmt.execute();
			sdr = (ScrollableDataResult) res.getDataObject();
			rowsSourceBean = sdr.getSourceBean();
			if (rowsSourceBean != null) {
				response.setAttribute((SourceBean) rowsSourceBean);
			}
			ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		} catch (Exception e) {
			String msg = "Errore nel calcolo dei dati della griglia ";
			ror.reportFailure(e, className, msg);
		} finally {
			Utils.releaseResources(conn, command, dr);
		}
	}
}