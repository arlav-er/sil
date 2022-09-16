package com.engiweb.framework.dbaccess.pool;

import java.sql.Connection;
import java.sql.SQLException;

import com.engiweb.framework.dbaccess.ConnectionPoolDescriptor;

public class UniversalConnectionPool implements ConnectionPoolInterface {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(UniversalConnectionPool.class.getName());
	private boolean logDBStatus = false;
	private JDCConnectionDriver _jdccd = null;

	public UniversalConnectionPool(ConnectionPoolDescriptor connectionPoolDescriptor) {
		try {
			_jdccd = new JDCConnectionDriver(
					connectionPoolDescriptor.getConnectionPoolParameter("jdbcDriver").getValue(),
					connectionPoolDescriptor.getConnectionPoolParameter("connectionString").getValue(),
					connectionPoolDescriptor.getConnectionPoolParameter("user").getValue(),
					connectionPoolDescriptor.getConnectionPoolParameter("userPassword").getValue(),
					connectionPoolDescriptor.getConnectionPoolParameter("poolMinLimit").getValue(),
					connectionPoolDescriptor.getConnectionPoolParameter("poolMaxLimit").getValue(),
					connectionPoolDescriptor.getConnectionPoolParameter("poolRetryTime").getValue(),
					connectionPoolDescriptor.getConnectionPoolParameter("poolAttemptsNumber").getValue(),
					connectionPoolDescriptor.getConnectionPoolParameter("poolReapingTime").getValue(),
					connectionPoolDescriptor.getConnectionPoolParameter("poolConnTimeOut").getValue());
			this.logDBStatus = Boolean
					.valueOf(connectionPoolDescriptor.getConnectionPoolParameter("logDBStatus").getValue())
					.booleanValue();
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "UniversalConnectionPool::UniversalConnectionPool:", ex);

		} // catch (Exception ex) try
	} // public GenericConnectionPoolIFaceImpl(String connIdentifier)

	public synchronized Connection getConnection() {
		if (_jdccd == null) {
			_logger.debug("GenericConnectionPoolIFaceImpl::getConnection: JDC connection driver è nullo");

			return null;
		} // if (_jdccd == null)
		Connection conn = null;
		try {
			conn = _jdccd.connect();
		} // try
		catch (SQLException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "DB2ConnectionPoolIFaceImpl::getConnection: _jdccd.connect()",
					ex);

		} // catch (SQLException ex)
		return conn;
	} // public Connection getConnection()

	public synchronized void logPoolState() {
		// log_db_status
		if (!logDBStatus)
			return;
		if (_jdccd == null) {
			_logger.debug("UniversalConnectionPool::logPoolState: JDC connection driver è nullo");

			return;
		} // if (_jdccd == null)
		_jdccd.logPoolState();
	} // public synchronized void logPoolState()

	public synchronized void release() throws SQLException {
		if (_jdccd == null) {
			_logger.debug("UniversalConnectionPool::release: JDC connection driver nullo");

			return;
		} // if (_jdccd == null)
		_jdccd.release();
		_jdccd = null;
	} // public synchronized void release() throws SQLException
} // public class UniversalConnectionPool implements ConnectionPoolInterface
