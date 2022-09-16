package com.engiweb.framework.dbaccess.factory;

import com.engiweb.framework.dbaccess.ConnectionPoolDescriptor;
import com.engiweb.framework.dbaccess.pool.ConnectionPoolInterface;
import com.engiweb.framework.dbaccess.pool.UniversalConnectionPool;
import com.engiweb.framework.error.EMFInternalError;

/**
 * Questa classe è responsabile per la creazione di un ConnectionPool Universale implementato da Engiweb
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public final class UniversalConnectionPoolFactory {
	/**
	 * Il factory method responsabile per la creazione di un pool di connessioni universale
	 * 
	 * @param <B>ConnectionPoolDescriptor
	 *            </B> connectionPoolDescriptor - l'oggetto descrittore del pool da creare deve contenere tutti i
	 *            parametri necessari per creare un pool di connessioni ad oracel.
	 * @return un oggetto di tipo <B>ConnectionPoolInterface</B> rappresentante il pool di connessioni nativo verso
	 *         oracle, il tipo ritornato è in realtà un oggetto di tipo NativePoolWrapper su un ConnectionPoolDataSource
	 *         di Oracle
	 */
	public ConnectionPoolInterface createConnectionPool(ConnectionPoolDescriptor connectionPoolDescriptor)
			throws EMFInternalError {
		return new UniversalConnectionPool(connectionPoolDescriptor);
	}
} // end class
