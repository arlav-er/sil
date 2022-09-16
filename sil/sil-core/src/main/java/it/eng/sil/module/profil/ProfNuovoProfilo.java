package it.eng.sil.module.profil;

import java.sql.Types;
import java.util.ArrayList;

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
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

public class ProfNuovoProfilo extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProfNuovoProfilo.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		// ArrayList retList = null;
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
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
			String strdenominazione = StringUtils.getAttributeStrNotNull(request, "denominazione");
			String strnota = StringUtils.getAttributeStrNotNull(request, "nota");

			// Trovo l'utente connesso
			RequestContainer requestContainer = getRequestContainer();
			SessionContainer sessionContainer = requestContainer.getSessionContainer();
			// Recupero utente
			User user = (User) sessionContainer.getAttribute(User.USERID);
			String pCdnUtInsMod = Integer.toString(user.getCodut());

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(4);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("cdnProfNuovo", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("strdenominazione", Types.VARCHAR, strdenominazione));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("strnota", Types.VARCHAR, strnota));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("pCdnUtInsMod", Types.VARCHAR, pCdnUtInsMod));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();

			// Reperisco i valori di output della stored e....
			DataField df = pdr.getPunctualDatafield();
			String cdnProfNuovo = (String) df.getObjectValue();
			// ..preparo il SourceBean della response
			SourceBean row = new SourceBean("ROW");
			row.setAttribute("cdnProfNuovo", cdnProfNuovo);
			response.setAttribute((SourceBean) row);

			request.delAttribute("cdnprofilo");
			request.setAttribute("cdnprofilo", cdnProfNuovo);

			ror.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::Chiamata_StoredProcedure_OK:",
					request);

		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			ror.reportFailure(e, className, msg);
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					this.getClass().getName() + "::Errore_chiamata_StoredProcedure:", request);

		} finally {
			Utils.releaseResources(conn, command, dr);
		}

	}// end service()

}// class ProfNuovoProfilo
