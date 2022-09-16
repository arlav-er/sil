package com.engiweb.framework.dbaccess.pool;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.engiweb.framework.dbaccess.ConnectionPoolDescriptor;

/**
 * @author zoppello
 * 
 *         To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates.
 *         To enable and disable the creation of type comments go to Window>Preferences>Java>Code Generation.
 */
public class AppServerManagedConnectionPool implements ConnectionPoolInterface {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(AppServerManagedConnectionPool.class.getName());
	private ConnectionPoolDescriptor _connectionPoolDescriptor = null;

	public AppServerManagedConnectionPool(ConnectionPoolDescriptor connectionPoolDescriptor) {
		_connectionPoolDescriptor = connectionPoolDescriptor;
	}

	/**
	 * @see com.engiweb.framework.dbaccess.pool.ConnectionPoolInterface#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		try {
			String jndiName = (String) _connectionPoolDescriptor.getConnectionPoolParameter("jndiName").getValue();
			// FV 08/06/2004
			// String user =
			// (String)_connectionPoolDescriptor.getConnectionPoolParameter("user").getValue();
			// String userPassword =
			// (String)_connectionPoolDescriptor.getConnectionPoolParameter("userPassword").getValue();
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(jndiName);
			// return ds.getConnection(user, userPassword);

			// Utils.printSilStackedElements(">>>>>");
			
			return ds.getConnection();
		} catch (NamingException ne) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "AppServerManagedConnectionPool::getConnection:", ne);

			throw new SQLException("Naming Exception");
		}
	}


	/**
	 * @see com.engiweb.framework.dbaccess.pool.ConnectionPoolInterface#release()
	 */
	public void release() throws SQLException {
	}
}