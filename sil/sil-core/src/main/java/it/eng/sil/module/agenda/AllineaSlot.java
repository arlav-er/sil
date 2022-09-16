package it.eng.sil.module.agenda;

import java.math.BigInteger;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
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

public class AllineaSlot extends AbstractModule {
	private String className = this.getClass().getName();

	public AllineaSlot() {
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
			String prgParAppuntamento = StringUtils.getAttributeStrNotNull(request, "PRGAPPUNTAMENTO");
			String codParCpi = StringUtils.getAttributeStrNotNull(request, "CODCPI");
			String codVecchioStato = (String) request.getAttribute("OLDSTATO");
			BigInteger bParAllineaAppuntamento = null;
			BigInteger cdnFlagCancella = null;
			String parAllApp = StringUtils.getAttributeStrNotNull(request, "bParAllineaAppuntamento");
			if (!parAllApp.equals("")) {
				bParAllineaAppuntamento = new BigInteger(parAllApp);
			}
			// String cdnParUtente = StringUtils.getAttributeStrNotNull(request,
			// "cdnParUtente");
			// Trovo l'utente connesso
			RequestContainer requestContainer = getRequestContainer();
			SessionContainer sessionContainer = requestContainer.getSessionContainer();
			// Recupero utente
			User user = (User) sessionContainer.getAttribute(User.USERID);
			String cdnParUtente = Integer.toString(user.getCodut());
			// Se prgParAppuntamento è nullo prelevo il suo valore dalla session
			if (prgParAppuntamento.equals("")) {
				prgParAppuntamento = sessionContainer.getAttribute("AG_NEW_PRG_APP").toString();
				sessionContainer.delAttribute("AG_NEW_PRG_APP");
			}

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(8);
			// Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("codParCpi", Types.VARCHAR, codParCpi));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("prgParAppuntamento", Types.BIGINT, prgParAppuntamento));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("nParAllineaAppuntamento", Types.INTEGER, bParAllineaAppuntamento));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("codParVecchioStato", Types.VARCHAR, codVecchioStato));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("cdnParUtente", Types.BIGINT, cdnParUtente));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("flagCancella", Types.INTEGER, cdnFlagCancella));
			command.setAsInputParameters(paramIndex++);

			// parametri di Output
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
			// 2. ErrCodeOut
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			// String ErrCodeOut = (String) df.getObjectValue();
			String ErrCodeOut = df.getStringValue();
			if (ErrCodeOut == null) {
				ErrCodeOut = "";
			}

			// response.setAttribute(cdr.getSourceBean());
			SourceBean row = new SourceBean("ROW");
			row.setAttribute("CodiceRit", codiceRit);
			row.setAttribute("ErrCodeOut", ErrCodeOut);
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