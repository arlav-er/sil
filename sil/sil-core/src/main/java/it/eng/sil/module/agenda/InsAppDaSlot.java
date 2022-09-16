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

public class InsAppDaSlot extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsAppDaSlot.class.getName());
	private String className = this.getClass().getName();

	public InsAppDaSlot() {
	}

	public void service(SourceBean request, SourceBean response) {
		// ArrayList retList = null;
		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);

		try {
			String strLegaAppPatto = (String) request.getAttribute("LEGA_APP_PATTO");
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);
			SourceBean statementSB = (SourceBean) getConfig().getAttribute("QUERY");
			String statement = statementSB.getAttribute("STATEMENT").toString();
			String sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// Prelevo i valori dei parametri di Input dalla Request
			String prgParSlot = StringUtils.getAttributeStrNotNull(request, "prgParSlot");
			String codParCpi = StringUtils.getAttributeStrNotNull(request, "codParCpi");

			/*
			 * 15/07/2004 Se non c'è il codCpi nella request, lo prelevo dalla sessione, in modo da evitare errori in
			 * fase di inserimento appuntamento da slot quando si proviene dal link nel footer del patto.
			 */
			if (codParCpi.equals("")) {
				codParCpi = user.getCodRif();
			}

			String prgAzienda = (String) request.getAttribute("PRGAZIENDA");
			BigInteger prgParAzienda = null;
			if (prgAzienda != null && !prgAzienda.equals("")) {
				prgParAzienda = new BigInteger(prgAzienda);
			}
			String prgUnita = (String) request.getAttribute("PRGUNITA");
			BigInteger prgParUnita = null;
			if (prgUnita != null && !prgUnita.equals("")) {
				prgParUnita = new BigInteger(prgUnita);
			}
			String cdnLavoratore = (String) request.getAttribute("CDNLAVORATORE");
			if (cdnLavoratore.equals("")) {
				cdnLavoratore = null;
			}
			BigInteger cdnParLavoratore = null;
			if (cdnLavoratore != null && !cdnLavoratore.equals("")) {
				cdnParLavoratore = new BigInteger(cdnLavoratore);
			}
			String cdnParUtente = StringUtils.getAttributeStrNotNull(request, "cdnParUtente");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(12);
			// Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			// parameters.add(conn.createDataField("prgParSlot", Types.BIGINT,
			// new BigInteger(prgParSlot)));
			parameters.add(conn.createDataField("str_prgParSlot", Types.VARCHAR, prgParSlot));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("str_codParCpi", Types.VARCHAR, codParCpi));
			command.setAsInputParameters(paramIndex++);
			// parameters.add(conn.createDataField("prgParAzienda",
			// Types.BIGINT, prgParAzienda));
			parameters.add(conn.createDataField("str_prgParAzienda", Types.VARCHAR, prgAzienda));
			command.setAsInputParameters(paramIndex++);
			// parameters.add(conn.createDataField("prgParUnita", Types.BIGINT,
			// prgParUnita));
			parameters.add(conn.createDataField("str_prgParUnita", Types.VARCHAR, prgUnita));
			command.setAsInputParameters(paramIndex++);
			// parameters.add(conn.createDataField("cdnParLavoratore",
			// Types.BIGINT, cdnParLavoratore));
			parameters.add(conn.createDataField("str_cdnParLavoratore", Types.VARCHAR, cdnLavoratore));
			command.setAsInputParameters(paramIndex++);
			// parameters.add(conn.createDataField("cdnParUtente", Types.BIGINT,
			// new BigInteger(cdnParUtente)));
			parameters.add(conn.createDataField("str_cdnParUtente", Types.VARCHAR, cdnParUtente));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("str_flgParLegaPatto", Types.VARCHAR, strLegaAppPatto));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("str_doCommit", Types.VARCHAR, "true"));
			command.setAsInputParameters(paramIndex++);

			// parametri di Output
			parameters.add(conn.createDataField("prgParAppuntamento", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("codParCpiAppunt", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			parameters.add(conn.createDataField("ErrCodeOut", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			// Reperisco i valori di output della stored e preparo il SourceBean
			// della response
			// 1. Codice di Ritorno
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			// String codiceRit = (String) df.getObjectValue();
			String codiceRit = df.getStringValue();
			if (codiceRit == null) {
				codiceRit = "-1";
			} else {
				codiceRit = codiceRit.trim();
			}
			_logger.debug(codiceRit);

			// 2. prgParAppuntamento
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			// String prgParAppuntamento = (String) df.getObjectValue();
			String prgParAppuntamento = df.getStringValue();
			if (prgParAppuntamento == null) {
				prgParAppuntamento = "";
			} else {
				prgParAppuntamento = prgParAppuntamento.trim();
			}
			_logger.debug(prgParAppuntamento);

			// 3. codParCpiAppunt
			pdr = (PunctualDataResult) outputParams.get(2);
			df = pdr.getPunctualDatafield();
			// String codParCpiAppunt = (String) df.getObjectValue();
			String codParCpiAppunt = df.getStringValue();
			if (codParCpiAppunt == null) {
				codParCpiAppunt = "";
			} else {
				codParCpiAppunt = codParCpiAppunt.trim();
			}
			_logger.debug(codParCpiAppunt);

			// 4. ErrCodeOut
			pdr = (PunctualDataResult) outputParams.get(3);
			df = pdr.getPunctualDatafield();
			// String ErrCodeOut = (String) df.getObjectValue();
			String ErrCodeOut = df.getStringValue();
			if (ErrCodeOut == null) {
				ErrCodeOut = "";
			} else {
				ErrCodeOut = ErrCodeOut.trim();
			}
			_logger.debug(ErrCodeOut);

			// response.setAttribute(cdr.getSourceBean());
			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			row.setAttribute("PRGAPPUNTAMENTO", prgParAppuntamento);
			row.setAttribute("CODCPIAPPUNTAMENTO", codParCpiAppunt);
			row.setAttribute("ErrCodeOut", ErrCodeOut);
			response.setAttribute((SourceBean) row);
			it.eng.sil.util.TraceWrapper.debug(_logger, this.className, row);

			ror.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);

		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			ror.reportFailure(e, className, msg);
		} finally {
			Utils.releaseResources(conn, command, dr);
		}

	}

}