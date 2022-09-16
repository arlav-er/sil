package it.eng.sil.security.handlers.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.session.Session;
import org.apache.log4j.Logger;

import it.eng.sil.Values;

class LogServiceSoapDB {

	// private static final String TABELLA_DEFAULT = "TS_WS_TRACCIAMENTO";
	private static String TABELLA_DEFAULT = "TS_WS_TRACCIAMENTO";
	private static String SEQUENCE_DEFAULT = "TS_WS_TRACCIAMENTO";
	private static final String TABELLA_SERVIZI = "TS_WS_LOG_SERVIZI";

	private static final String KEY_SQL_DEFAULT = "select s_%TABELLA%.nextval as keyTabella from dual";

	private static final String INSERT_SQL_DEFAULT = "insert into %TABELLA% "
			+ " (prgwstracciamento, stroperazione, strmessaggiosoap, strurl, strverso, strtipo) "
			+ " values (?, ?, ?, ?, ?, ? )";

	private static final String KEY_SQL_SERVICE = "select s_%TABELLA%.nextval as keyTabella from dual";

	private static final String INSERT_SQL_SERVICE_REQ = "insert into %TABELLA% "
			+ " (PRGWSLOGSERVIZI, STRMITTENTE, STRSERVICENAME, STROPERATION, STRCODICEFISCALELAV, PRGWSTRACCIAMENTOREQ, DTMREQ) "
			+ " values (?, ?, ?, ?, ?, ?, sysdate )";

	private static final String UPDATE_SQL_SERVICE_RESP = "update %TABELLA% "
			+ " set codesito = ?, prgwstracciamentoresp = ?, dtmresp = sysdate " + " where prgwslogservizi = ?";

	private static final Logger log = Logger.getLogger(LogServiceSoapDB.class.getName());

	private String tipo = null;
	private String verso = null;
	private String servizio = null;
	private String mittente = null;
	private String codiceFiscaleReq = null;
	private String esito = null;

	LogServiceSoapDB() {

	}

	// TIPO = REQUEST|RESPONSE VERSO = CLIENT|SERVER
	LogServiceSoapDB(String tipo, String verso, String nomeServizio, String mitt, String codiceFiscaleReq) {
		this.tipo = tipo;
		this.verso = verso;
		this.servizio = nomeServizio;
		this.mittente = mitt;
		this.codiceFiscaleReq = codiceFiscaleReq;
	}

	// TIPO = REQUEST|RESPONSE VERSO = CLIENT|SERVER
	LogServiceSoapDB(String tipo, String verso, String codEsito) {
		this.tipo = tipo;
		this.verso = verso;
		this.esito = codEsito;
	}

	/* fs20150226 - start */
	private String calcolaSuffissoTabella() {
		Date data = new Date();
		String anno = (new SimpleDateFormat("yyyy")).format(data);
		int mese = Integer.parseInt((new SimpleDateFormat("MM")).format(data));
		return "_" + anno + "_" + (mese % 3 == 0 ? (mese / 3) : (mese / 3) + 1);
	}
	/* fs20150226 - end */

	public void invoke(MessageContext msgContext) throws AxisFault {
		log.debug("LogServiceSoapDB::inizio");
		Connection conn = null;
		BigDecimal prgWsTracciamento = null;
		BigDecimal prgWsServiziLog = null;
		Session sessionMsgContext = null;
		TABELLA_DEFAULT = "TS_WS_TRACCIAMENTO";
		/* fs20150226 - start */
		if (System.getProperty("storicizzazione.abilitata") != null
				&& "true".equalsIgnoreCase(System.getProperty("storicizzazione.abilitata"))) {
			TABELLA_DEFAULT += calcolaSuffissoTabella();
		}
		/* fs20150226 - end */

		try {
			sessionMsgContext = MessageContext.getCurrentContext().getSession();

			String SQL_KEY = KEY_SQL_DEFAULT.replaceFirst("%TABELLA%", SEQUENCE_DEFAULT);

			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(Values.JDBC_JNDI_NAME);

			conn = ds.getConnection();

			PreparedStatement statement = conn.prepareStatement(SQL_KEY, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				prgWsTracciamento = rs.getBigDecimal("keyTabella");
			} else {
				throw new Exception("Errore nella sequence");
			}

			rs.close();
			statement.close();

			String SQL = INSERT_SQL_DEFAULT.replaceFirst("%TABELLA%", TABELLA_DEFAULT);
			String messaggio = msgContext.getCurrentMessage().getSOAPPartAsString();

			String operazione = msgContext.getOperation().getName();

			String url = (String) msgContext.getProperty("transport.url");

			PreparedStatement ps = conn.prepareStatement(SQL);

			ps.setBigDecimal(1, prgWsTracciamento);
			ps.setString(2, operazione);
			ps.setString(3, messaggio);
			ps.setString(4, url);
			ps.setString(5, verso);
			ps.setString(6, tipo);
			ps.execute();
			ps.close();

			if (this.tipo.equalsIgnoreCase("REQUEST")) {

				String SQL_KEY_SERVICE = KEY_SQL_SERVICE.replaceFirst("%TABELLA%", TABELLA_SERVIZI);
				PreparedStatement statement1 = conn.prepareStatement(SQL_KEY_SERVICE, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				ResultSet rs1 = statement1.executeQuery();

				if (rs1.next()) {
					prgWsServiziLog = rs1.getBigDecimal("keyTabella");
					sessionMsgContext.set("KEY_WS_LOG_SERVIZI", prgWsServiziLog);
				} else {
					throw new Exception("Errore nella sequence");
				}

				rs1.close();
				statement1.close();

				String SQL_Service = INSERT_SQL_SERVICE_REQ.replaceFirst("%TABELLA%", TABELLA_SERVIZI);

				PreparedStatement ps1 = conn.prepareStatement(SQL_Service);

				ps1.setBigDecimal(1, prgWsServiziLog);
				ps1.setString(2, mittente);
				ps1.setString(3, servizio);
				ps1.setString(4, operazione);
				ps1.setString(5, codiceFiscaleReq);
				ps1.setBigDecimal(6, prgWsTracciamento);
				ps1.execute();

				ps1.close();
			} else {
				prgWsServiziLog = (BigDecimal) sessionMsgContext.get("KEY_WS_LOG_SERVIZI");

				String SQL_Service = UPDATE_SQL_SERVICE_RESP.replaceFirst("%TABELLA%", TABELLA_SERVIZI);

				PreparedStatement ps1 = conn.prepareStatement(SQL_Service);

				ps1.setString(1, esito);
				ps1.setBigDecimal(2, prgWsTracciamento);
				ps1.setBigDecimal(3, prgWsServiziLog);
				ps1.execute();

				ps1.close();
			}

		} catch (Exception e) {
			// si scrive nel log e si continua
			log.error("LogServiceSoapDB: si e' verificato un errore: " + toString(), e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error(e);
				}
			}
		}
	}

	public String toString() {
		return "tipo=" + notNull(tipo) + " verso=" + notNull(verso);
	}

	private String notNull(String n) {
		if (n != null)
			return "{null}";
		return n;
	}

}
