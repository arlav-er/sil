package it.eng.sil.security.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.log4j.Logger;

import it.eng.sil.Values;

/**
 * 
 * @author Franco Vuoto
 * 
 */

class LogSoapDB {
	/*
	 * private static final String INSERT_SQL = "insert into %TABELLA% " +
	 * " (prgwstracciamento, stroperazione, strmessaggiosoap, strurl, strverso, strtipo) " +
	 * " values (S_ts_ws_tracciamento.nextval , ?, ?, ?, ?, ? )";
	 */
	private static final String INSERT_SQL = "insert into %TABELLA% "
			+ " (prgwstracciamento, stroperazione, strmessaggiosoap, strurl, strverso, strtipo) "
			+ " values (S_%TABELLA%.nextval , ?, ?, ?, ?, ? )";

	private static final long serialVersionUID = 2910443977128691195L;
	private static final Logger log = Logger.getLogger(LogSoapDB.class.getName());

	/*
	 * TIPO = REQUEST|RESPONSE VERSO = CLIENT|SERVER
	 */

	private String tipo = null;
	private String verso = null;
	private String tabella = null;
	private String sequence = null;

	LogSoapDB() {

	}

	LogSoapDB(String tipo, String verso, String tabella) {
		this.tipo = tipo;
		this.verso = verso;
		this.tabella = tabella;
		this.sequence = tabella;

		/* fs20150226 - start */
		if (System.getProperty("storicizzazione.abilitata") != null
				&& "true".equalsIgnoreCase(System.getProperty("storicizzazione.abilitata"))) {
			this.tabella += calcolaSuffissoTabella();
		}
		/* fs20150226 - end */
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
		log.debug("LogSoapDB::inizio");
		Connection conn = null;
		try {

			/* fs20150226 start */
			// String SQL = INSERT_SQL.replaceFirst("%TABELLA%", this.tabella);
			String _SQL = INSERT_SQL.replaceFirst("%TABELLA%", this.tabella);
			String SQL = _SQL.replaceFirst("%TABELLA%", this.sequence);
			if (log.isDebugEnabled())
				log.debug("sql : " + SQL);
			/* fs20150226 end */

			String messaggio = msgContext.getCurrentMessage().getSOAPPartAsString();

			String operazione = msgContext.getOperation().getName();
			String url = (String) msgContext.getProperty("transport.url");

			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(Values.JDBC_JNDI_NAME);

			conn = ds.getConnection();

			PreparedStatement ps = conn.prepareStatement(SQL);
			ps.setString(1, operazione);

			ps.setString(2, messaggio);
			ps.setString(3, url);
			ps.setString(4, verso);
			ps.setString(5, tipo);
			ps.execute();
			ps.close();

		} catch (Exception e) {
			log.error("LogSoapDB: si e' verificato un errore: " + toString(), e);

			// si continua lo stesso...
			// throw new AxisFault(e.getMessage());

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
