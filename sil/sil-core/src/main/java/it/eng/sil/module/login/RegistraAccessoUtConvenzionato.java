/*
 * Creato il 11-gen-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.login;

import java.sql.Types;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
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
public class RegistraAccessoUtConvenzionato extends AbstractHttpModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(RegistraAccessoUtConvenzionato.class.getName());

	/*
	 * (non Javadoc)
	 * 
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		HttpServletRequest request = getHttpRequest();
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);

		DataConnection conn = null;
		StoredProcedureCommand command = null;
		DataResult dr = null;
		try {
			// imposto la chiamata per la stored procedure
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);

			String strNumeroPratica = (String) sessionContainer.getAttribute("NUMPRATICA");

			if (strNumeroPratica != null && !("").equalsIgnoreCase(strNumeroPratica)) {
				_logger.info("RegistraAccessoUtConvenzionato: utente convenzionato collegato: cdnut=" + user.getCodut()
						+ " - numero pratica=" + strNumeroPratica);

				SourceBean statementSB = (SourceBean) getConfig().getAttribute("QUERY");
				String statement = statementSB.getAttribute("statement").toString();
				String sqlStr = SQLStatements.getStatement(statement);
				command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

				// imposto i parametri
				ArrayList parameters = new ArrayList(2);
				parameters.add(conn.createDataField("p_CDNUTENTE", Types.BIGINT, new Integer(user.getCodut())));
				command.setAsInputParameters(0);
				parameters.add(conn.createDataField("p_STRNUMEROPRATICA", Types.VARCHAR, strNumeroPratica));
				command.setAsInputParameters(1);

				// eseguo!!
				dr = command.execute(parameters);
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "RegistraAccessoUtConvenzionato: ", (Exception) e);

		} finally {
			Utils.releaseResources(conn, command, null);
		}

	}

}