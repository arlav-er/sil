package com.engiweb.framework.dbaccess.pool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class JDCConnectionDriver implements Driver {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(JDCConnectionDriver.class.getName());
	private static final String URL_PREFIX = "jdbc:";
	private static final int MAJOR_VERSION = 1;
	private static final int MINOR_VERSION = 0;
	private JDCConnectionPool _pool = null;

	public JDCConnectionDriver(String driver, String url, String user, String password, String poolMinLimitStr,
			String poolMaxLimitStr, String poolRetryTimeStr, String poolAttemptsNumberStr, String poolReapingTimeStr,
			String poolConnTimeoutStr)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
		_logger.debug("JDCConnectionDriver::JDCConnectionDriver: invocato");

		if ((driver == null) || (url == null) || (user == null) || (password == null) || (poolMinLimitStr == null)
				|| (poolMaxLimitStr == null) || (poolRetryTimeStr == null) || (poolAttemptsNumberStr == null)
				|| (poolReapingTimeStr == null) || (poolConnTimeoutStr == null)) {
			_logger.debug("JDCConnectionDriver::JDCConnectionDriver: parametri non validi");

			return;
		} // if ((driver == null) || (url == null) || ...
		_logger.debug("JDCConnectionDriver::JDCConnectionDriver: driver   = " + driver);

		_logger.debug("JDCConnectionDriver::JDCConnectionDriver: url      = " + url);

		_logger.debug("JDCConnectionDriver::JDCConnectionDriver: user     = " + user);

		/*
		 * _logger.debug( "JDCConnectionDriver::JDCConnectionDriver: password = " + password);
		 * 
		 */

		Class.forName(driver);

		/*
		 * Class.forName(driver).newInstance(); DriverManager.registerDriver(this);
		 */

		int poolMinLimit = 0;
		try {
			poolMinLimit = Integer.parseInt(poolMinLimitStr);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"JDCConnectionDriver::JDCConnectionDriver: Integer.parseInt(poolMinLimitStr)", ex);

		} // catch (Exception ex)
		int poolMaxLimit = 0;
		try {
			poolMaxLimit = Integer.parseInt(poolMaxLimitStr);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"JDCConnectionDriver::JDCConnectionDriver: Integer.parseInt(poolMaxLimitStr)", ex);

		} // catch (Exception ex)
		int poolRetryTime = 0;
		try {
			poolRetryTime = Integer.parseInt(poolRetryTimeStr);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"JDCConnectionDriver::JDCConnectionDriver: Integer.parseInt(poolRetryTimeStr)", ex);

		} // catch (Exception ex)
		int poolAttemptsNumber = 0;
		try {
			poolAttemptsNumber = Integer.parseInt(poolAttemptsNumberStr);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"JDCConnectionDriver::JDCConnectionDriver: Integer.parseInt(poolAttemptsNumberStr)", ex);

		} // catch (Exception ex)
		int poolReapingTime = 0;
		try {
			poolReapingTime = Integer.parseInt(poolReapingTimeStr);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"JDCConnectionDriver::JDCConnectionDriver: Integer.parseInt(poolReapingTimeStr)", ex);

		} // catch (Exception ex)
		long poolConnTimeout = 0;
		try {
			poolConnTimeout = Long.parseLong(poolConnTimeoutStr);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"JDCConnectionDriver::JDCConnectionDriver: Integer.parseInt(poolConnTimeoutStr)", ex);

		} // catch (Exception ex)
		_pool = new JDCConnectionPool(url, user, password, poolMinLimit, poolMaxLimit, poolRetryTime,
				poolAttemptsNumber, poolReapingTime, poolConnTimeout);
	} // public JDCConnectionDriver(...)

	public Connection connect() throws SQLException {
		return connect(URL_PREFIX, null);
	} // public Connection connect() throws SQLException

	public Connection connect(String url, Properties props) throws SQLException {
		_logger.debug("JDCConnectionDriver::connect: invocato");

		if (url == null) {
			_logger.debug("JDCConnectionDriver::connect: parametro url non valido");

			return null;
		} // if (url == null)
		if (!url.startsWith(URL_PREFIX)) {
			_logger.debug("JDCConnectionDriver::connect: il parametro url non inizia con " + URL_PREFIX);

			return null;
		} // if (!url.startsWith(URL_PREFIX))
		_logger.debug("JDCConnectionDriver::connect: url = " + url);

		return _pool.getConnection();
	} // public Connection connect(String url, Properties props) throws
		// SQLException

	public boolean acceptsURL(String url) {
		_logger.debug("JDCConnectionDriver::acceptsURL: invocato");

		return url.startsWith(URL_PREFIX);
	} // public boolean acceptsURL(String url)

	public int getMajorVersion() {
		_logger.debug("JDCConnectionDriver::getMajorVersion: invocato");

		return MAJOR_VERSION;
	} // public int getMajorVersion()

	public int getMinorVersion() {
		_logger.debug("JDCConnectionDriver::getMinorVersion: invocato");

		return MINOR_VERSION;
	} // public int getMinorVersion()

	public DriverPropertyInfo[] getPropertyInfo(String str, Properties props) {
		_logger.debug("JDCConnectionDriver::getPropertyInfo: invocato");

		return new DriverPropertyInfo[0];
	} // public DriverPropertyInfo[] getPropertyInfo(String str, Properties
		// props)

	public boolean jdbcCompliant() {
		_logger.debug("JDCConnectionDriver::jdbcCompliant: invocato");

		return false;
	} // public boolean jdbcCompliant()

	public void logPoolState() {
		_logger.debug("JDCConnectionDriver::logPoolState: invocato");

		if (_pool == null) {
			_logger.debug("JDCConnectionDriver::logPoolState: JDC connection pool nullo !");

			return;
		} // if (_jdccd == null)
		_pool.logPoolState();
	} // public void logPoolState()

	public synchronized void release() throws SQLException {
		_logger.debug("JDCConnectionDriver::release: invocato");

		if (_pool == null) {
			_logger.debug("JDCConnectionDriver::release: JDC connection pool nullo !");

			return;
		} // if (_jdccd == null)
		_pool.release();
		_pool = null;
	} // public synchronized void release() throws SQLException

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
} // public class JDCConnectionDriver implements Driver
