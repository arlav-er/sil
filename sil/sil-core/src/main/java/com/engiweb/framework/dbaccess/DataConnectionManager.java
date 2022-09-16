package com.engiweb.framework.dbaccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.engiweb.framework.dbaccess.factory.ConnectionPoolFactory;
import com.engiweb.framework.dbaccess.pool.ConnectionPoolInterface;
import com.engiweb.framework.dbaccess.pool.NativePoolWrapper;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.mappers.SQLMapper;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.sil.util.TraceWrapper;

/**
 * Questa Classe è il manager di tutti i pool di connessioni registrate con il sottosistema di accesso ai dati e ha le
 * seguenti responsabilità :
 * 
 * <li>Mantiene tutti i pool registrati con il sottositema di accesso ai dati
 * <li>Mette a disposizioni connessioni al DB sui diversi pool registrati nel sottositema di accesso ai dati
 * <li>Mette a disposizione connessioni al DB su pool registrati in un contesto JNDI
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class DataConnectionManager {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DataConnectionManager.class.getName());
	private static DataConnectionManager _instance = null;
	private ConnectionPoolInterface _defaultConnectionPool = null;
	private HashMap _registeredConnectionPool = null;
	private HashMap _driversVersion = null;
	private HashMap _sqlMappers = null;
	private SQLMapper _defaultSQLMapper = null;
	private String _defaultDriverVersion = null;

	private DataConnectionManager() throws EMFInternalError {
		_registeredConnectionPool = new HashMap();
		_driversVersion = new HashMap();
		_sqlMappers = new HashMap();
		//
		// Retrive the names of all configured DataSources
		//
		List registeredPoolNames = Configurator.getInstance().getRegisteredConnectionPoolNames();
		Iterator it = registeredPoolNames.iterator();
		String connectionPoolName = null;
		ConnectionPoolDescriptor connectionPoolDescriptor = null;
		ConnectionPoolInterface connectionPool = null;
		String driverVersion = new String("-1");
		//
		// Create First Connession Register it and Then set it as Default
		//
		if (it.hasNext()) {
			try {
				connectionPoolName = ((String) it.next());
				connectionPoolDescriptor = Configurator.getInstance().getConnectionPoolDescriptor(connectionPoolName);
				connectionPool = ConnectionPoolFactory.createConnectionPool(connectionPoolDescriptor);
				_registeredConnectionPool.put(connectionPoolDescriptor.getConnectionPoolName(), connectionPool);
				_defaultConnectionPool = connectionPool;
				_driversVersion.put(connectionPoolDescriptor.getConnectionPoolName(),
						connectionPoolDescriptor.getConnectionPoolParameter("driverVersion").getValue());
				_defaultDriverVersion = connectionPoolDescriptor.getConnectionPoolParameter("driverVersion").getValue();
				_defaultSQLMapper = (SQLMapper) ((Class
						.forName(connectionPoolDescriptor.getConnectionPoolParameter("sqlMapperClass").getValue()))
								.newInstance());
				_sqlMappers.put(connectionPoolDescriptor.getConnectionPoolName(), _defaultSQLMapper);
				_logger.debug("DataConnectionManager::DataConnectionManager: registered connection pool ["
						+ connectionPoolName + "] as default connection pool");

				SQLMapper tempMapper;
				while (it.hasNext()) {
					tempMapper = null;
					connectionPoolName = ((String) it.next());
					connectionPoolDescriptor = Configurator.getInstance()
							.getConnectionPoolDescriptor(connectionPoolName);
					connectionPool = ConnectionPoolFactory.createConnectionPool(connectionPoolDescriptor);
					_registeredConnectionPool.put(connectionPoolDescriptor.getConnectionPoolName(), connectionPool);
					_driversVersion.put(connectionPoolDescriptor.getConnectionPoolName(),
							connectionPoolDescriptor.getConnectionPoolParameter("driverVersion").getValue());
					tempMapper = (SQLMapper) ((Class
							.forName(connectionPoolDescriptor.getConnectionPoolParameter("sqlMapperClass").getValue()))
									.newInstance());
					_sqlMappers.put(connectionPoolDescriptor.getConnectionPoolName(), tempMapper);
					_logger.debug(
							"DataConnectionManager::DataConnectionManager: registered connection pool data source ["
									+ connectionPoolName + "] in DataConnectionManager");

				} // while (it.hasNext())
			} // try
			catch (Exception ex) {
				TraceWrapper.fatal(_logger, "DataConnectionManager::DataConnectionManager:", ex);

				throw new EMFInternalError(EMFErrorSeverity.ERROR,
						"DataConnectionManager::DataConnectionManager: cannot initialize DataConnectionManager error occurred during the inizialization of "
								+ connectionPoolDescriptor.getConnectionPoolName(),
						ex);
			} // catch (Exception ex) try
		} // if (it.hasNext())
		else
			_logger.warn("DataConnectionManager::DataConnectionManager: "
					+ "ATTENTION NO CONNECION POOL REGISTERED WITH MANAGER PROBABLY YOU WILL USE THIS MANAGER ONLY AS AN INTERFACE TO APPLICATION SERVER");

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				release();
			} // public void run()
		} // new Thread()
		);
	} // private DataConnectionManager() throws EMFInternalError

	/**
	 * Questo metodo serve per ottenere l'unica istanza dell'oggetto DataConnectionManager
	 * 
	 * @return DataConnectionManager il manager di tutti i pool registrati
	 */
	public static DataConnectionManager getInstance() throws EMFInternalError {
		if (_instance == null) {
			synchronized (DataConnectionManager.class) {
				if (_instance == null)
					_instance = new DataConnectionManager();
			} // synchronized(DataConnectionManager.class)
		} // if (_instance == null)
		return _instance;
	} // public static DataConnectionManager getInstance()

	/**
	 * Metodo per ottenere una connessione dal pool di default (il primo nome registrato nel file xml)
	 * 
	 * @return un oggetto di tipo <B>DataConnection</B> rappresentante la connessione al db
	 * @return un oggetto di tipo <B>DataConnection</B> rappresentante la connessione al db
	 * @throws <B>EMFInternalError
	 *             </B> - Se si verifica qualche problema nel recuperare la connessione al db
	 */
	public synchronized DataConnection getConnection() throws EMFInternalError {
		Monitor monitor = MonitorFactory.start("model.data-access.get-connection.default");
		try {
			Connection conn = _defaultConnectionPool.getConnection();
			if (conn != null)
				return new DataConnection(conn, _defaultDriverVersion, _defaultSQLMapper);
			else {
				_logger.fatal("DataConnectionManager::getConnection:il pool ha ritornato una connessione null");

				throw new EMFInternalError(EMFErrorSeverity.ERROR,
						"DataConnectionManager::getConnection:il pool ha ritornato una connessione null");
			}
		} // try
		catch (SQLException ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DataConnectionManager::getConnection:", ex);

			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"DataConnectionManager::getConnection: error in getting connection from the pool", ex);
		} // catch (SQLException ex) try
		finally {
			monitor.stop();
		} // finally
	} // public synchronized DataConnection getConnection() throws
		// EMFInternalError

	/**
	 * Metodo per ottenere una connessione dal pool identificato dal parametro passato(il primo nome registrato nel file
	 * xml)
	 * 
	 * @param <B>String
	 *            </B> connectionPoolName - il nome del pool dal quale si vuole ottenere una connessione
	 * @return un oggetto di tipo <B>DataConnection</B> rappresentante la connessione al db
	 * @throws <B>EMFInternalError
	 *             </B> - Se si verifica qulache problema nel recuperare la connessione al db
	 */
	public synchronized DataConnection getConnection(String connectionPoolName) throws EMFInternalError {
		Monitor monitor = MonitorFactory.start("model.data-access.get-connection." + connectionPoolName.toLowerCase());
		try {
			ConnectionPoolInterface cp = (ConnectionPoolInterface) _registeredConnectionPool.get(connectionPoolName);
			if (cp == null)
				throw new EMFInternalError(EMFErrorSeverity.ERROR,
						"DataConnectionManager::getConnection: cannot get connection pool " + connectionPoolName
								+ " not present in DataConnectionManager");
			else {
				Connection conn = cp.getConnection();
				if (conn != null) {
					DataConnection dataConnection = new DataConnection(conn,
							(String) _driversVersion.get(connectionPoolName),
							(SQLMapper) _sqlMappers.get(connectionPoolName));
					return dataConnection;
				} else {
					_logger.fatal("DataConnectionManager::getConnection:il pool ha ritornato una connessione null");

					throw new EMFInternalError(EMFErrorSeverity.ERROR,
							"DataConnectionManager::getConnection:il pool ha ritornato una connessione null");
				}
			} // if (cp == null) else
		} // try
		catch (SQLException ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DataConnectionManager::getConnection:", ex);

			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"DataConnectionManager::getConnection: error in getting connection from the pool", ex);
		} // catch (SQLException ex) try
		finally {
			monitor.stop();
		} // finally
	} // public synchronized DataConnection getConnection(String
		// connectionPoolName) throws EMFInternalError

	/**
	 * Metodo per ottenere una connessione dal pool definito nel Contesto ctx con jndiName
	 * 
	 * @param <B>Context
	 *            </B> ctx - il contesto jndi da cui ricavare il pool
	 * @param <B>String
	 *            </B> jndiName - il nome jndi della connessione nel contesto ctx
	 * @SQLMapper mapper - l'oggetto sqlMapper per la connessione
	 * @return un oggetto di tipo <B>DataConnection</B> rappresentante la connessione al db
	 * @throws <B>EMFInternalError
	 *             </B> - Se si verifica qualche problema nel recuperare la connessione al db
	 */
	public synchronized DataConnection getConnection(Context ctx, String jndiName, SQLMapper mapper)
			throws EMFInternalError {
		// This method allow a Client to use ConnectionPool Defined in
		// Application Server Context
		Monitor monitor = MonitorFactory.start("model.data-access.get-connection." + jndiName.toLowerCase());
		try {
			DataSource cpds = (DataSource) ctx.lookup(jndiName);
			if (cpds == null)
				throw new EMFInternalError(EMFErrorSeverity.ERROR,
						"DataConnectionManager::getConnection: cannot get connection pool " + jndiName
								+ " not present in jndi context");
			else {
				NativePoolWrapper nativePool = new NativePoolWrapper(cpds);
				Connection conn = nativePool.getConnection();
				if (conn != null) {
					DataConnection dataConnection = new DataConnection(conn, String.valueOf(2.0), mapper);
					return dataConnection;
				} else {
					_logger.fatal("DataConnectionManager::getConnection:il pool ha ritornato una connessione null");

					throw new EMFInternalError(EMFErrorSeverity.ERROR,
							"DataConnectionManager::getConnection:il pool ha ritornato una connessione null");
				}
			} // if (cpds == null) else
		} // try
		catch (NamingException ne) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DataConnectionManager::getConnection:", ne);

			throw new EMFInternalError(EMFErrorSeverity.ERROR, "DataConnectionManager::getConnection:", ne);
		} // catch (NamingException ne)
		catch (SQLException sqle) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DataConnectionManager::getConnection:", sqle);

			throw new EMFInternalError(EMFErrorSeverity.ERROR, "DataConnectionManager::getConnection:", sqle);
		} // catch (SQLException sqle) try
		finally {
			monitor.stop();
		} // finally
	} // public synchronized DataConnection getConnection(Context ctx, String
		// jndiName, SQLMapper mapper) throws EMFInternalError

	public synchronized void release() {
		if (_instance != null) {
			synchronized (DataConnectionManager.class) {
				if (_instance != null) {
					Collection registeredConnectionPools = _registeredConnectionPool.values();
					Iterator registeredConnectionPoolsIterator = registeredConnectionPools.iterator();
					while (registeredConnectionPoolsIterator.hasNext()) {
						ConnectionPoolInterface registeredConnectionPool = (ConnectionPoolInterface) registeredConnectionPoolsIterator
								.next();
						try {
							registeredConnectionPool.release();
						} // try
						catch (SQLException sqle) {
							it.eng.sil.util.TraceWrapper.fatal(_logger, "DataConnectionManager::release:", sqle);

						} // catch (SQLException sqle) try
					} // while (registeredConnectionPoolsIterator.hasNext())
					_instance = null;
				} // if (_instance != null)
			} // synchronized(DataConnectionManager.class)
		} // if (_instance != null)
	} // public synchronized void release()
} // public class DataConnectionManager
