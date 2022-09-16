/*
 * Creato il 22-ott-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.util;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author savino
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class ConnessionePerReport implements Connection {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ConnessionePerReport.class.getName());
	private Connection conn;

	public ConnessionePerReport(Connection conn) {
		this.conn = conn;
		//log("Chiamato il costruttore");
		//InizializzaLicenzaCC inizializzaLicenzaCC = new InizializzaLicenzaCC();
		//inizializzaLicenzaCC.init(null);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#createStatement()
	 */
	public Statement createStatement() throws SQLException {
		log("Chiamato createStatement()");
		return conn.createStatement();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		log("Chiamato prepareStatement()");
		return conn.prepareStatement(sql);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	public CallableStatement prepareCall(String sql) throws SQLException {
		log("Chiamato prepareCall()");
		return conn.prepareCall(sql);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#nativeSQL(java.lang.String)
	 */
	public String nativeSQL(String sql) throws SQLException {
		log("Chiamato nativeSQL()");
		return conn.nativeSQL(sql);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#setAutoCommit(boolean)
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		log("Chiamato setAutoCommit(): chiamata annullata ****************");
		// conn.setAutoCommit(autoCommit);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#getAutoCommit()
	 */
	public boolean getAutoCommit() throws SQLException {
		log("Chiamato getAutoCommit()");
		return conn.getAutoCommit();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#commit()
	 */
	public void commit() throws SQLException {
		log("Chiamato commit(): chiamata annullata ***************");
		// conn.commit();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#rollback()
	 */
	public void rollback() throws SQLException {
		log("Chiamato rollback(): chiamata annullata ***************");
		// conn.rollback();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#close()
	 */
	public void close() throws SQLException {

		log("Chiamato close(): chiamata annullata ***************");
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#isClosed()
	 */
	public boolean isClosed() throws SQLException {
		log("Chiamato isClose()");
		return conn.isClosed();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#getMetaData()
	 */
	public DatabaseMetaData getMetaData() throws SQLException {

		return conn.getMetaData();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly) throws SQLException {
		log("Chiamato setReadOnly() : chiamata annullata *****");
		// conn.setReadOnly(readOnly);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#isReadOnly()
	 */
	public boolean isReadOnly() throws SQLException {
		log("Chiamato isReadOnly()");
		return conn.isReadOnly();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#setCatalog(java.lang.String)
	 */
	public void setCatalog(String catalog) throws SQLException {
		log("Chiamato setCatalog(): chiamata annullata ******* ");
		// conn.setCatalog(catalog);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#getCatalog()
	 */
	public String getCatalog() throws SQLException {
		log("Chiamato getCatalog()");
		return conn.getCatalog();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#setTransactionIsolation(int)
	 */
	public void setTransactionIsolation(int level) throws SQLException {
		log("Chiamato setTransactionIsolation(): chiamata annullata ******* ");
		// conn.setTransactionIsolation(level);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#getTransactionIsolation()
	 */
	public int getTransactionIsolation() throws SQLException {
		log("Chiamato getTransactionIsolation()");
		return conn.getTransactionIsolation();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		log("Chiamato getWarnings()");
		return conn.getWarnings();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		log("Chiamato clearWarnings(): chiamata annullata ******* ");
		// conn.clearWarnings();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#createStatement(int, int)
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		log("Chiamato createStatement(int resultSetType, int resultSetConcurrency)");
		return conn.createStatement(resultSetType, resultSetConcurrency);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		// Stub di metodo generato automaticamente
		log("Chiamato prepareStatement(String sql, int resultSetType, int resultSetConcurrency)");
		return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		// Stub di metodo generato automaticamente
		log("Chiamato prepareCall(String sql, int resultSetType, int resultSetConcurrency)");
		return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#getTypeMap()
	 */
	public Map getTypeMap() throws SQLException {
		// Stub di metodo generato automaticamente
		log("Chiamato getTypeMap()");
		return conn.getTypeMap();
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see java.sql.Connection#setTypeMap(java.util.Map)
	 */
	public void setTypeMap(Map map) throws SQLException {
		// Stub di metodo generato automaticamente
		log("Chiamato setTypeMap()");
		conn.setTypeMap(map);
	}

	private void log(String msg) {
		_logger.debug(getClass().getName() + ": " + msg);

	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Blob createBlob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSchema() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
}