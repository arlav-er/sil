/*
 * Creato il 7-gen-05
 * Author: vuoto
 * 
 */
package it.eng.sil.session;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;

/**
 * @author vuoto
 * 
 */

import it.eng.sil.Values;

public class SessionListener implements HttpSessionListener {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SessionListener.class.getName());
	private String name = null;
	private HttpSession session = null;
	private String val = null;

	public void sessionCreated(HttpSessionEvent event) {
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		// System.out.println("SESSION
		// LISTENER::::::::::::::::::::::::::sessionDestroyed");

		session = event.getSession();

		String jSessionId = session.getId();
		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		try {
			// imposto la chiamata per la stored procedure

			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(Values.DB_SIL_DATI);

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call PG_ACCESSO.DEREGISTRA_ACCESSO(?) }");

			// imposto i parametri
			List parameters = new ArrayList(1);
			parameters.add(conn.createDataField("JSESSIONID", Types.VARCHAR, jSessionId));
			command.setAsInputParameters(0);

			// eseguo!!
			dr = command.execute(parameters);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "RegistraAccesso: ", (Exception) e);

		} finally {
			Utils.releaseResources(conn, command, null);

		}

		_logger.debug("RIlascio OK");

	}

}