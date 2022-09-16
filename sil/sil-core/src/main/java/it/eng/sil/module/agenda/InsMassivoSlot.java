package it.eng.sil.module.agenda;

import java.math.BigInteger;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;

public class InsMassivoSlot extends AbstractModule {
	private String className = this.getClass().getName();

	public InsMassivoSlot() {
	}

	public void service(SourceBean request, SourceBean response) {
		// ArrayList retList = null;
		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);
			SourceBean statementSB = (SourceBean) getConfig().getAttribute("QUERY");
			String statement = statementSB.getAttribute("STATEMENT").toString();
			String sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// Prelevo i valori dei parametri di Input dalla Request
			String dataParInizio = StringUtils.getAttributeStrNotNull(request, "DATAPARINIZIO");
			String dataParFine = StringUtils.getAttributeStrNotNull(request, "DATAPARFINE");
			String codParSettimana = StringUtils.getAttributeStrNotNull(request, "CODPARSETTIMANA");
			String codParCpi = StringUtils.getAttributeStrNotNull(request, "CODPARCPI");
			String codParStatoSlot = StringUtils.getAttributeStrNotNull(request, "CODPARSTATOSLOT");
			String cdnParUtente = StringUtils.getAttributeStrNotNull(request, "CDNPARUTENTE");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(8);
			// Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("dataParInizio", Types.VARCHAR, dataParInizio));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("dataParFine", Types.VARCHAR, dataParFine));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("codParSettimana", Types.BIGINT, new BigInteger(codParSettimana)));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("codParCpi", Types.VARCHAR, codParCpi));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("codParStatoSlot", Types.VARCHAR, codParStatoSlot));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("cdnParUtente", Types.BIGINT, new BigInteger(cdnParUtente)));
			command.setAsInputParameters(paramIndex++);

			// parametri di Output
			parameters.add(conn.createDataField("ErrCodeOut", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			// String codiceRit = (String) df.getObjectValue();
			String codiceRit = df.getStringValue();
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			// BigDecimal ErrCodeOut = (BigDecimal) df.getObjectValue();
			String ErrCode = df.getStringValue();
			if (ErrCode == null) {
				ErrCode = "";
			}
			// if(ErrCodeOut!=null) { ErrCode = ErrCodeOut.toString(); }

			// response.setAttribute(cdr.getSourceBean());
			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			row.setAttribute("ErrCodeOut", ErrCode);
			response.setAttribute((SourceBean) row);

			ror.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);

		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			ror.reportFailure(e, className, msg);
		} finally {
			Utils.releaseResources(conn, command, dr);
		}

	}

}