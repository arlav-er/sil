package com.engiweb.framework.dbaccess.factory;

import com.engiweb.framework.dbaccess.ConnectionPoolDescriptor;
import com.engiweb.framework.dbaccess.pool.AppServerManagedConnectionPool;
import com.engiweb.framework.dbaccess.pool.ConnectionPoolInterface;

/**
 * @author zoppello
 * 
 *         To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates.
 *         To enable and disable the creation of type comments go to Window>Preferences>Java>Code Generation.
 */
public class AppServerManagedConnectionPoolFactory {
	public ConnectionPoolInterface createConnectionPool(ConnectionPoolDescriptor connectionPoolDescriptor) {
		ConnectionPoolInterface returnedPool = null;
		returnedPool = new AppServerManagedConnectionPool(connectionPoolDescriptor);
		return returnedPool;
	}
}
