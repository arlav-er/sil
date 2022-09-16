package it.eng.sil.module.ido;

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
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

// @author: Stefania Orioli

public class MatchStoricizzaXIncrocio extends AbstractModule {
	public MatchStoricizzaXIncrocio() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		String sFlagCopia = "1";
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
			String prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "PRGRICHIESTAAZ");

			RequestContainer requestContainer = getRequestContainer();
			SessionContainer sessionContainer = requestContainer.getSessionContainer();
			// Recupero l'utente
			User user = (User) sessionContainer.getAttribute(User.USERID);
			// String cdnParUtente = Integer.toString(user.getCodut());

			// Controllo se l'utente è abilitato o meno alla gestione della
			// copia di lavoro
			PageAttribs attributiMatch = new PageAttribs(user, "GestIncrocioPage");
			boolean gestCopia = attributiMatch.containsButton("GEST_COPIA");
			String flgGestCopia = gestCopia ? "1" : "0";

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(5);
			// Parametro di Ritorno
			parameters.add(conn.createDataField("codiceRit", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgRichiestaAz", Types.INTEGER, prgRichiestaAz));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("flgCopia", Types.VARCHAR, sFlagCopia));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("flgGestCopia", Types.INTEGER, flgGestCopia));
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
			String codiceRit = (String) df.getStringValue();
			// 2. ErrCodeOut
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			String ErrCodeOut = (String) df.getStringValue();
			if (ErrCodeOut == null) {
				ErrCodeOut = "";
			}

			SourceBean row = new SourceBean("ROW");
			if (codiceRit.equals("-1")) {
				row.setAttribute("CodiceRit", codiceRit);
			} else {
				row.setAttribute("CodiceRit", "0");
			}
			row.setAttribute("ErrCodeOut", ErrCodeOut);
			row.setAttribute("NEWPRGRICHIESTAAZ", codiceRit);
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