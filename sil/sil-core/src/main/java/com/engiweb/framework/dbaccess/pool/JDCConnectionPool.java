package com.engiweb.framework.dbaccess.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

class ConnectionReaper extends Thread {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(JDCConnectionPool.class.getName());
	private JDCConnectionPool _pool = null;
	private long _delay = 60000; // pool_reaping_time
	private boolean _exit = false;

	ConnectionReaper(JDCConnectionPool pool, int reapingTime) {
		_logger.debug("ConnectionReaper::ConnectionReaper: invocato");

		_pool = pool;
		if (reapingTime > -1)
			_delay = reapingTime;
		_exit = false;
	} // ConnectionReaper(JDCConnectionPool pool)

	public void run() {
		_logger.debug("ConnectionReaper::run: thread partito");

		while (!_exit) {
			try {
				sleep(_delay);
			} // try
			catch (InterruptedException e) {
			} // catch (InterruptedException e)
			_pool.reapConnections();
		} // while (!_exit)
		_pool.closeConnections();
		_pool = null;
	} // public void run()

	public void terminate() {
		_exit = true;
		this.interrupt();
	} // public void terminate()
} // class ConnectionReaper extends Thread

public class JDCConnectionPool {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(JDCConnectionPool.class.getName());
	private Vector _connections = null;
	private String _url = null;
	private String _user = null;
	private String _password = null;
	private ConnectionReaper _reaper = null;
	private int _poolMinLimit = 0; // pool_min_limit
	private int _poolMaxLimit = 10; // pool_max_limit
	private long _poolConnTimeout = 120000; // pool_conn_timeout
	private int _poolRetryTime = 500; // poo_retry_time
	private int _poolAttemptsNumber = 60; // pool_attempts_number

	public JDCConnectionPool(String url, String user, String password, int poolMinLimit, int poolMaxLimit,
			int poolRetryTime, int poolAttemptsNumber, int poolReapingTime, long poolConnTimeout) {
		_logger.debug("JDCConnectionPool::JDCConnectionPool: invocato");

		if ((url == null) || (user == null) || (password == null)) {
			_logger.fatal("JDCConnectionPool::JDCConnectionPool: parametri non validi");

			return;
		} // if ((url == null) || (user == null) || (password == null))
		_url = url;
		_user = user;
		_password = password;
		if (poolMinLimit >= 0)
			_poolMinLimit = poolMinLimit;
		if (poolMaxLimit > _poolMinLimit)
			_poolMaxLimit = poolMaxLimit;
		else
			_poolMaxLimit = _poolMinLimit;
		if (poolRetryTime > 0)
			_poolRetryTime = poolRetryTime;
		if (poolAttemptsNumber > 0)
			_poolAttemptsNumber = poolAttemptsNumber;
		if (poolConnTimeout > 0)
			_poolConnTimeout = poolConnTimeout;
		_connections = new Vector();
		JDCConnection c = null;
		for (int i = 0; i < _poolMinLimit; i++)
			try {
				c = createConnection();
				_connections.addElement(c);
			} // try
			catch (SQLException ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "JDCConnectionPool::getConnection: c = createConnection()",
						ex);

			} // catch (SQLException ex)
		_logger.debug(
				"JDCConnectionPool::JDCConnectionPool: pool inizializzato con " + _connections.size() + " connessioni");

		_reaper = new ConnectionReaper(this, poolReapingTime);
		_reaper.start();
	} // public JDCConnectionPool(String url, String user, String password)

	public synchronized void reapConnections() {
		_logger.debug("JDCConnectionPool::reapConnections: invocato");

		long stale = System.currentTimeMillis() - _poolConnTimeout;
		Enumeration connlist = _connections.elements();
		int initSize = _connections.size();
		while ((_connections.size() > _poolMinLimit) && (connlist.hasMoreElements())) {
			JDCConnection conn = (JDCConnection) connlist.nextElement();
			// if (conn.inUse() && (stale > conn.getLastUse()) &&
			// !conn.validate())
			if (!conn.inUse() && (stale > conn.getLastUse()))
				removeConnection(conn);
		} // while ((connections.size() > poolMinLimit) &&
			// (connlist.hasMoreElements()))
		_logger.debug("JDCConnectionPool::reapConnections: [" + (initSize - _connections.size())
				+ "] connessioni rimosse dal pool");

	} // public synchronized void reapConnections()

	public synchronized void closeConnections() {
		_logger.debug("JDCConnectionPool::closeConnections: invocato");

		Enumeration connlist = _connections.elements();
		while ((connlist != null) && (connlist.hasMoreElements())) {
			JDCConnection conn = (JDCConnection) connlist.nextElement();
			removeConnection(conn);
		} // while ((connlist != null) && (connlist.hasMoreElements()))
	} // public synchronized void closeConnections()

	private synchronized void removeConnection(JDCConnection conn) {
		_logger.debug("JDCConnectionPool::removeConnection: invocato");

		if (conn != null) {
			try {
				conn.forceClose();
			} // try
			catch (SQLException ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "JDCConnectionPool::removeConnection: conn.forceClose()",
						ex);

			} // catch (SQLException ex)
		} // if (conn != null)
		_connections.removeElement(conn);
	} // private synchronized void removeConnection(JDCConnection conn)

	public synchronized Connection getConnection() throws SQLException {
		_logger.debug("JDCConnectionPool::getConnection: invocato");

		JDCConnection c = null;
		int retries = 0;
		while ((c == null) && (retries < _poolAttemptsNumber)) {
			retries++;
			_logger.debug("JDCConnectionPool::getConnection: tentativo n. " + retries);

			for (int i = 0; i < _connections.size(); i++) {
				c = (JDCConnection) _connections.elementAt(i);
				if (c.lease()) {
					_logger.debug("JDCConnectionPool::getConnection: recuperata una connessione libera dal pool");

					if (!c.validate()) {
						_logger.debug("JDCConnectionPool::getConnection: la connessione non è valida e verrà rimossa");

						removeConnection(c);
						break;
					} // if (!c.validate())
					logPoolState();
					return c;
				} // if (c.lease())
			} // for (int i = 0; i < _connections.size(); i++)
			c = null;
			if (_connections.size() < _poolMaxLimit) {
				try {
					c = createConnection();
				} // try
				catch (SQLException ex) {
					it.eng.sil.util.TraceWrapper.fatal(_logger, "JDCConnectionPool::getConnection: createConnection()",
							ex);

				} // catch (SQLException ex)
				if (c != null) {
					_logger.debug("JDCConnectionPool::getConnection: creata una nuova connessione");

					_connections.addElement(c);
					c.lease();
					logPoolState();
				} // if (c != null)
				else
					_logger.fatal("JDCConnectionPool::getConnection: connessione nulla ritornata dal database server");

				return c;
			} // if (_connections.size() < _poolMaxLimit)
			try {
				Thread.sleep(_poolRetryTime);
			} // try
			catch (InterruptedException ie) {
			} // catch(InterruptedException ie)
		} // while ((c == null) && (retries < _poolAttemptsNumber))
		_logger.fatal("JDCConnectionPool::getConnection: recupero connessione fallito");

		return null;
	} // public synchronized Connection getConnection() throws SQLException

	public JDCConnection createConnection() throws SQLException {
		_logger.debug("JDCConnectionPool::createConnection: invocato");

		Connection conn = null;
		try {
			conn = DriverManager.getConnection(_url, _user, _password);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					"JDCConnectionPool::getConnection: DriverManager.getConnection(url, user, password)", ex);

			return null;
		} // catch (Exception ex)
		return new JDCConnection(conn, this);
	} // public JDCConnection createConnection() throws SQLException

	public synchronized void returnConnection(JDCConnection conn) {
		_logger.debug("JDCConnectionPool::returnConnection: invocato");

		try {
			if (!conn.getAutoCommit()) {
				_logger.error(
						"JDCConnectionPool::returnConnection: la connessione tornata al pool contiene una transazione aperta, roolback ...");

				conn.rollback();
			} // if (!conn.getAutoCommit())
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "JDCConnectionPool::returnConnection:", ex);

		} // catch (Exception ex)
		conn.expireLease();
		logPoolState();
	} // public synchronized void returnConnection(JDCConnection conn)

	public synchronized void logPoolState() {
		int inUseConns = 0;
		JDCConnection c = null;
		for (int i = 0; i < _connections.size(); i++) {
			c = (JDCConnection) _connections.elementAt(i);
			if (c.inUse())
				inUseConns++;
		} // for (int i = 0; i < _connections.size(); i++)
		_logger.debug("JDCConnectionPool::logPoolState: numero di connessioni nel pool [" + _connections.size() + "]");

		_logger.debug("JDCConnectionPool::logPoolState: numero di connessioni in uso   [" + inUseConns + "]");

	} // public synchronized void logPoolState()

	public synchronized void release() throws SQLException {
		_logger.debug("JDCConnectionPool::release: invocato");

		if (_reaper == null) {
			_logger.warn("JDCConnectionPool::release: reaper null !");

			return;
		} // if (_reaper == null)
		_reaper.terminate();
		_reaper = null;
	} // public synchronized void release() throws SQLException
} // public class JDCConnectionPool
