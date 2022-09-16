package it.eng.sil.security.handlers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.sql.DataSource;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.log4j.Logger;
import org.apache.ws.security.WSPasswordCallback;

import it.eng.sil.Values;

public class SILWss4jAuthenticationHandler implements CallbackHandler {

	private static final Logger log = Logger.getLogger(SILWss4jAuthenticationHandler.class.getName());

	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof WSPasswordCallback) {
				WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
				log.info("WSS4J: Utente: " + pc.getIdentifier() + ", tipo autenticazione: " + pc.getUsage());

				MessageContext msgContext = MessageContext.getCurrentContext();
				try {
					SOAPBody sb = msgContext.getCurrentMessage().getSOAPBody();
				} catch (SOAPException e) {
					throw new UnsupportedCallbackException(callbacks[i], "(500) AXIS WSS4J: SOAPBody " + e);
				}
				String targetServiceName = msgContext.getTargetService();
				String operazione = msgContext.getOperation().getName();

				String[] utenteWS = getMittente(targetServiceName);

				String user = utenteWS[0];
				String pwd = utenteWS[1];

				if (pc.getUsage() == WSPasswordCallback.USERNAME_TOKEN) {

					if (!user.equals(pc.getIdentifier())) {
						throw new IOException("(500) AXIS WSS4J: Utente non corretto: " + pc.getIdentifier());
					}

					pc.setPassword(pwd);

				} else if (pc.getUsage() == WSPasswordCallback.USERNAME_TOKEN_UNKNOWN) {

					if (!user.equals(pc.getIdentifier())) {
						throw new IOException("(500) AXIS WSS4J: Utente non corretto: " + pc.getIdentifier());
					}

					if (!pwd.equals(pc.getPassword())) {
						throw new IOException("(500) AXIS WSS4J:  Password errata per l'utente: " + pc.getIdentifier());
					}
				}
			} else {
				throw new UnsupportedCallbackException(callbacks[i], "(500) AXIS WSS4J:  Callback NON SOPPORTATO");
			}
		}
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