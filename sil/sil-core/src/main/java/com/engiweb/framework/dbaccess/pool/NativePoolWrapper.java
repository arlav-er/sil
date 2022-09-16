package com.engiweb.framework.dbaccess.pool;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * Questa classe costituisce un Wrapper su pool di connessioni di tipo JDBC 2.0 e permette a un pool di connessioni di
 * tipo jdbc 2.= nativo di essere utilizzato nel sottosistema di accesso ai dati
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class NativePoolWrapper implements ConnectionPoolInterface {
	DataSource _nativeConnectionPool = null;

	public NativePoolWrapper(DataSource nativeConnectionPool) {
		_nativeConnectionPool = nativeConnectionPool;
	} // public NativePoolWrapper(ConnectionPoolDataSource
		// nativeConnectionPool)

	/**
	 * Ritorna una connessione dal pool
	 * 
	 * @return <B>Connection</B> - L'oggetto Connection ritornato dal pool nativo
	 */
	public Connection getConnection() throws SQLException {
		return _nativeConnectionPool.getConnection();
	} // public Connection getConnection() throws SQLException

	public synchronized void release() throws SQLException {
	} // public synchronized void release() throws SQLException
} // end Class NativePoolWrapper
