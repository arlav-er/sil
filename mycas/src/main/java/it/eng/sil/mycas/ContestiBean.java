package it.eng.sil.mycas;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContestiBean {
	private static final String SQL_LOAD_CONTEXTS = "select web_context from mycas.de_sistema_mycas WHERE flg_installato = true and web_context IS NOT NULL ";
	private static final String JNDI_DS = "java:/jdbc/MyCasDS";
	private static final Logger LOG = LoggerFactory.getLogger(ContestiBean.class);

	public ContestiBean() {

	}

	private Connection getConnection() throws SQLException {
		Connection conn = null;
		try {
			javax.naming.InitialContext ctx = new javax.naming.InitialContext();
			DataSource dbDS = (DataSource) ctx.lookup(JNDI_DS);
			conn = dbDS.getConnection();
		} catch (Exception dbEx) {
			LOG.error("Errore nel reperimento della connessione da JNDI: " + JNDI_DS, dbEx);
			throw new SQLException("Eccezione nell'accesso al database. " + dbEx.getMessage());
		}
		return conn;
	}

	public List<String> getContesti() throws IOException {

		List<String> contesti = new ArrayList<String>();

		Connection conn = null;
		try {
			conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(SQL_LOAD_CONTEXTS);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String contenuto = rs.getString("web_context");
				contesti.add(contenuto);
			}
			rs.close();
			pstmt.close();

		} catch (Exception ex) {
			LOG.error("Errore nel reperimento dei contesti al logout dal CAS", ex);
			throw new IOException(ex.getMessage());
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					LOG.error("Errore di chiusura della connessione dopo il reperimento dei contesti al logout dal CAS",
							e);
					throw new IOException(e.getMessage());
				}
		}
		return contesti;
	}
}
