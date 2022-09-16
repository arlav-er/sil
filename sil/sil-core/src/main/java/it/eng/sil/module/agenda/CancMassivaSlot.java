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
import it.eng.sil.security.User;

public class CancMassivaSlot extends AbstractModule {
	private String className = this.getClass().getName();

	public CancMassivaSlot() {
	}

	public void service(SourceBean request, SourceBean response) {
		// ArrayList retList = null;
		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);
			SourceBean statementSB = (SourceBean) getConfig().getAttribute("QUERY");
			String statement = statementSB.getAttribute("STATEMENT").toString();
			String sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// Prelevo i valori dei parametri di Input dalla Request
			String dataParInizioStr = StringUtils.getAttributeStrNotNull(request, "DATAPARINIZIOSTR");
			String dataParFineStr = StringUtils.getAttributeStrNotNull(request, "DATAPARFINESTR");
			String codParCpi = StringUtils.getAttributeStrNotNull(request, "CODCPI");
			String codParServizio = (String) request.getAttribute("CODPARSERVIZIO");
			if (codParServizio.equals("")) {
				codParServizio = null;
			}
			String prgSpi = StringUtils.getAttributeStrNotNull(request, "PRGSPIOPERATORE");
			BigInteger prgSpiOperatore = null;
			if (!prgSpi.equals("")) {
				prgSpiOperatore = new BigInteger(prgSpi);
			}
			String cdnParUtente = user.getCodRif();

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(8);
			// Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("codParCpi", Types.VARCHAR, codParCpi));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("dataParInizioStr", Types.VARCHAR, dataParInizioStr));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("dataParFineStr", Types.VARCHAR, dataParFineStr));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("codParServizio", Types.VARCHAR, codParServizio));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("prgSpiOperatore", Types.BIGINT, prgSpiOperatore));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("cdnParUtente", Types.VARCHAR, cdnParUtente));
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