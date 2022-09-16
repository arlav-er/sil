/*
 * Creato il 11-gen-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.login;

import java.net.InetAddress;
import java.sql.Types;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dispatching.module.AbstractHttpModule;

import it.eng.sil.security.User;

/**
 * @author Togna Cosimo
 * @author D'Auria Giovanni
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class RegistraAccesso extends AbstractHttpModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RegistraAccesso.class.getName());

	/*
	 * (non Javadoc)
	 * 
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		HttpServletRequest request = getHttpRequest();
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);

		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		try {
			// imposto la chiamata per la stored procedure
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);

			SourceBean statementSB = (SourceBean) getConfig().getAttribute("QUERY");
			String statement = statementSB.getAttribute("statement").toString();
			String sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			String remoteHost = request.getRemoteHost();
			String ipRemoteHost = request.getRemoteAddr();
			String address = ipRemoteHost;

			if ((remoteHost != null) && !remoteHost.equals("") && !ipRemoteHost.equalsIgnoreCase(remoteHost)) {
				address += "/" + remoteHost;
			}

			// imposto i parametri
			ArrayList parameters = new ArrayList(6);
			parameters.add(conn.createDataField("JSESSIONID", Types.VARCHAR, request.getSession().getId()));
			command.setAsInputParameters(0);
			parameters.add(conn.createDataField("CDNUTENTE", Types.BIGINT, new Integer(user.getCodut())));
			command.setAsInputParameters(1);
			parameters.add(conn.createDataField("IP", Types.VARCHAR, address));
			command.setAsInputParameters(2);
			parameters.add(conn.createDataField("browser", Types.VARCHAR, request.getHeader("USER-AGENT")));
			command.setAsInputParameters(3);
			parameters.add(conn.createDataField("os", Types.VARCHAR, request.getHeader("USER-AGENT")));
			command.setAsInputParameters(4);

			parameters.add(conn.createDataField("hostname", Types.VARCHAR, InetAddress.getLocalHost().getHostName()));
			command.setAsInputParameters(5);

			// eseguo!!
			dr = command.execute(parameters);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "RegistraAccesso: ", (Exception) e);

		} finally {
			Utils.releaseResources(conn, command, null);
		}

	}

}