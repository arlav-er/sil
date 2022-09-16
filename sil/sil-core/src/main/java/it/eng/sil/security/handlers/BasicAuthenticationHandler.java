package it.eng.sil.security.handlers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.log4j.Logger;

import it.eng.sil.Values;

public class BasicAuthenticationHandler extends BasicHandler {
	private static final Logger log = Logger.getLogger(BasicAuthenticationHandler.class.getName());

	public void invoke(MessageContext msgContext) throws AxisFault {
		log.info("BasicAuthenticationHandler::inizio");
		boolean ok = true;

		String targetServiceName = msgContext.getTargetService();
		String operazione = msgContext.getOperation().getName();

		String[] utenteWS = getMittente(targetServiceName);

		String userWS = utenteWS[0];
		String pwdWS = utenteWS[1];

		String username = msgContext.getUsername();
		String password = msgContext.getPassword();

		if (username.equals(userWS)) {
			if (!password.equals(pwdWS)) {
				log.info("BasicAuthenticationHandler: l'utente " + username + " ha fornito una password errata");
				ok = false;
			}
		} else {
			log.info("BasicAuthenticationHandler: l'utente [" + username + "] non e' stato trovato!");
			ok = false;
		}

		if (ok) {
			log.debug("BasicAuthenticationHandler: richiesta AUTORIZZATA per il servizio [" + operazione + "]");
			return;
		}

		log.info("BasicAuthenticationHandler: richiesta NON AUTORIZZATA (" + username + ":" + password + ")");
		throw new AxisFault("(401) NON AUTORIZZATO");

	}

	private String[] getMittente(String nomeServizio) throws AxisFault {

		String[] mitt = new String[2];
		Connection conn = null;
		ResultSet dr = null;
		Statement command = null;
		// attivo la connessione
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(Values.JDBC_JNDI_NAME);

			conn = ds.getConnection();

			String statement = "SELECT STRUSERID, STRPASSWORD FROM TS_WS WHERE upper(CODSERVIZIO) = upper('"
					+ nomeServizio + "')";
			command = conn.createStatement();

			dr = command.executeQuery(statement);

			while (dr.next()) {
				mitt[0] = dr.getString(1);
				mitt[1] = dr.getString(2);
			}
			return mitt;

		} catch (Exception ex) {
			log.fatal("Errore nell'inizializzazione della lista degli endpoint", ex);
			return mitt;
		} finally {
			try {
				if (dr != null) {
					dr.close();
				}
				if (command != null) {
					command.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ex) {
				log.fatal("Eccezione nella chiusura delle connessioni al db ", ex);
			}
		}
	}
}