package com.engiweb.framework.dbaccess.pool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Questa interfaccia descrive i metodi che un pool di connessioni generico deve mettere a disposizione
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public interface ConnectionPoolInterface {
	/**
	 * Ritorna una connessione dal pool
	 * 
	 * @return <code>Connection</code> - L'oggetto Connection ritornato dal pool
	 */
	public Connection getConnection() throws SQLException;

	/**
	 * Questa
	 */
	public void release() throws SQLException;
} // public interface ConnectionPoolInterface
