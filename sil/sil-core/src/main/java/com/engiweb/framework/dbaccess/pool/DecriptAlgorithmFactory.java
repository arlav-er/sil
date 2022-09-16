package com.engiweb.framework.dbaccess.pool;

import com.engiweb.framework.dbaccess.Configurator;
import com.engiweb.framework.dbaccess.ConnectionPoolDescriptor;
import com.engiweb.framework.dbaccess.ConnectionPoolParameter;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

/**
 * @author Bernabei Angelo
 * @version 1.0
 */
public class DecriptAlgorithmFactory {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DecriptAlgorithmFactory.class.getName());
	private static ConnectionPoolDescriptor infoConnectionPool = null;

	public DecriptAlgorithmFactory() {
		super();
	}

	/**
	 * Crea il provider sulla base del pool, il nome della classe è presente nel file di configurazione data_access
	 * 
	 * @param poolName
	 * @return
	 */
	public static IDecriptAlgorithm create(String poolName) throws EMFInternalError {
		infoConnectionPool = Configurator.getInstance().getConnectionPoolDescriptor(poolName);
		if (infoConnectionPool != null) {
			ConnectionPoolParameter parameter = infoConnectionPool.getConnectionPoolParameter("algorithm");
			if (parameter == null)
				return null;
			String className = parameter.getValue();
			IDecriptAlgorithm provider = null;
			try {
				provider = (IDecriptAlgorithm) Class.forName(className).newInstance();
			} catch (InstantiationException e) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "PasswordProviderFactory::create", e);

				throw new EMFInternalError(EMFErrorSeverity.ERROR,
						"PasswordProviderFactory::create::InstantiationException" + e.getMessage());
			} catch (IllegalAccessException e) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "PasswordProviderFactory::create", e);

				throw new EMFInternalError(EMFErrorSeverity.ERROR,
						"PasswordProviderFactory::create::IllegalAccessException" + e.getMessage());
			} catch (ClassNotFoundException e) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "PasswordProviderFactory::create", e);

				throw new EMFInternalError(EMFErrorSeverity.ERROR,
						"PasswordProviderFactory::create::ClassNotFoundException" + e.getMessage());
			}
			return provider;
		} else
			return null;
	}
}