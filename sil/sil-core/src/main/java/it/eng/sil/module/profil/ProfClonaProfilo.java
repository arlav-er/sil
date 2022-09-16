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
import it.eng.sil.security.User;

public class ProfClonaProfilo extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProfClonaProfilo.class.getName());
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
			Long cdnprofiloBD = new Long((String) request.getAttribute("cdnprofilo"));

			// Trovo l'utente connesso
			RequestContainer requestContainer = getRequestContainer();
			SessionContainer sessionContainer = requestContainer.getSessionContainer();
			// Recupero utente
			User user = (User) sessionContainer.getAttribute(User.USERID);
			Long pCdnUtInsModBD = new Long(user.getCodut());

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(3);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("cdnprofilo", Types.BIGINT, cdnprofiloBD));
			command.setAsInputParameters(paramIndex++);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("cdnProfClonato", Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("pCdnUtInsMod", Types.BIGINT, pCdnUtInsModBD));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();

			// Reperisco i valori di output della stored e....
			DataField df = pdr.getPunctualDatafield();
			Long cdnProfClonatoBD = (Long) df.getObjectValue();
			// ..preparo il SourceBean della response
			SourceBean row = new SourceBean("ROW");
			row.setAttribute("cdnProfClonato", cdnProfClonatoBD.toString());
			response.setAttribute((SourceBean) row);

			request.delAttribute("cdnprofilo");
			request.setAttribute("cdnprofilo", cdnProfClonatoBD.toString());

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

}// class ProfClonaProfilo
