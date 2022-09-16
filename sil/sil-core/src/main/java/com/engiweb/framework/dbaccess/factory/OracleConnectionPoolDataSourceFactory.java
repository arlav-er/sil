package com.engiweb.framework.dbaccess.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.sql.DataSource;

import com.engiweb.framework.dbaccess.ConnectionPoolDescriptor;
import com.engiweb.framework.dbaccess.pool.ConnectionPoolInterface;
import com.engiweb.framework.dbaccess.pool.DecriptAlgorithmFactory;
import com.engiweb.framework.dbaccess.pool.IDecriptAlgorithm;
import com.engiweb.framework.dbaccess.pool.NativePoolWrapper;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

/**
 * Questa classe è responsabile per la creazione di un ConnectionPool per database oracle L'implementazione di questa
 * classe è fatta totalmente attraverso l'utilizzo di tecniche di refelection, questo ci ci permette di compilare un
 * progetto senza avere a disposizione le librerie specifiche del vendor
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class OracleConnectionPoolDataSourceFactory {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(OracleConnectionPoolDataSourceFactory.class.getName());

	/**
	 * Il factory method responsabile per la creazione di un pool di connessioni per database Oracle
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
		try {
			String connectionStringParameter = connectionPoolDescriptor.getConnectionPoolParameter("connectionString")
					.getValue();
			String userNameParameter = connectionPoolDescriptor.getConnectionPoolParameter("user").getValue();
			/*
			 * Eventualmente Decifro la password.
			 */
			String poolName = connectionPoolDescriptor.getConnectionPoolName();
			// ottengo il riferimento alla classe che implementa l'algoritmo
			IDecriptAlgorithm decipherAlgoritm = DecriptAlgorithmFactory.create(poolName);
			String hiddenPassword = connectionPoolDescriptor.getConnectionPoolParameter("userPassword").getValue();
			String userPasswordParameter = null;
			if (decipherAlgoritm != null) {
				userPasswordParameter = decipherAlgoritm.decipher(hiddenPassword);
			} else {
				userPasswordParameter = hiddenPassword;
			}

			String poolMinLimitParameter = connectionPoolDescriptor.getConnectionPoolParameter("poolMinLimit")
					.getValue();
			int poolMinLimit = Integer.parseInt(poolMinLimitParameter);
			String poolMaxLimitParameter = connectionPoolDescriptor.getConnectionPoolParameter("poolMaxLimit")
					.getValue();
			int poolMaxLimit = Integer.parseInt(poolMaxLimitParameter);
			// questi 2 parametri sono opzionali...

			/*
			 * ConnectionPoolParameter cacheTimeToLiveTimeoutParameter = connectionPoolDescriptor
			 * .getConnectionPoolParameter("cacheTimeToLiveTimeout"); int cacheTimeToLiveTimeout = 0; if
			 * (cacheTimeToLiveTimeoutParameter != null) cacheTimeToLiveTimeout =
			 * Integer.parseInt(cacheTimeToLiveTimeoutParameter.getValue()); ConnectionPoolParameter
			 * cacheInactivityTimeoutParameter = connectionPoolDescriptor
			 * .getConnectionPoolParameter("cacheInactivityTimeout"); int cacheInactivityTimeout = 0; if
			 * (cacheInactivityTimeoutParameter != null) { cacheInactivityTimeout =
			 * Integer.parseInt(cacheInactivityTimeoutParameter.getValue()); } String cacheSchemeParameter =
			 * connectionPoolDescriptor.getConnectionPoolParameter("cacheScheme").getValue();
			 */

			//
			// Begin to Create Object via Reflection
			// Create Oracle ConnectionPoolDataSource
			//
			Class ocpdsClass = Class.forName("oracle.jdbc.pool.OracleConnectionPoolDataSource");
			Object ocpdsObject = ocpdsClass.newInstance();
			//
			// Setting URL
			//
			Method method = null;
			Class[] parameterTypesOfMethod = new Class[] { String.class };
			Object[] argumentsOfMethod = new Object[] { connectionStringParameter };
			method = ocpdsClass.getMethod("setURL", parameterTypesOfMethod);
			method.invoke(ocpdsObject, argumentsOfMethod);
			//
			// Setting USER
			//
			method = null;
			parameterTypesOfMethod = new Class[] { String.class };
			argumentsOfMethod = new Object[] { userNameParameter };
			method = ocpdsClass.getMethod("setUser", parameterTypesOfMethod);
			method.invoke(ocpdsObject, argumentsOfMethod);
			//
			// Setting PASSWORD
			//
			method = null;
			parameterTypesOfMethod = new Class[] { String.class };
			argumentsOfMethod = new Object[] { userPasswordParameter };
			method = ocpdsClass.getMethod("setPassword", parameterTypesOfMethod);
			method.invoke(ocpdsObject, argumentsOfMethod);

			//
			// NOW I MUST CREATE OrcaleConnectionCacheImpl passing
			// OracleConnectionPoolDataSource as parameter
			//

			/*
			 * Class oracleConnectionCacheImplClass = Class.forName("oracle.jdbc.pool.OracleConnectionCacheImpl");
			 * Object oracleConnectionCacheImplObject = null; Class[] parameterTypesOfConstructor = new Class[] {
			 * Class.forName("javax.sql.ConnectionPoolDataSource") }; Object[] argumentsOfConstructor = new Object[] {
			 * ocpdsObject }; Constructor oracleConnectionCacheImplConstructor = oracleConnectionCacheImplClass
			 * .getConstructor(parameterTypesOfConstructor); oracleConnectionCacheImplObject =
			 * oracleConnectionCacheImplConstructor.newInstance(argumentsOfConstructor); // // Set Min Limit // method =
			 * null; parameterTypesOfMethod = new Class[] { int.class }; argumentsOfMethod = new Object[] { new
			 * Integer(poolMinLimit) }; method = oracleConnectionCacheImplClass.getMethod("setMinLimit",
			 * parameterTypesOfMethod); method.invoke(oracleConnectionCacheImplObject, argumentsOfMethod); // // Setting
			 * maxLimit // method = null; parameterTypesOfMethod = new Class[] { int.class }; argumentsOfMethod = new
			 * Object[] { new Integer(poolMaxLimit) }; method = oracleConnectionCacheImplClass.getMethod("setMaxLimit",
			 * parameterTypesOfMethod); method.invoke(oracleConnectionCacheImplObject, argumentsOfMethod);
			 * 
			 * // // Setting setCacheTimeToLiveTimeout // if (cacheTimeToLiveTimeoutParameter != null) { method = null;
			 * parameterTypesOfMethod = new Class[] { long.class }; argumentsOfMethod = new Object[] { new
			 * Long(cacheTimeToLiveTimeout) }; method =
			 * oracleConnectionCacheImplClass.getMethod("setCacheTimeToLiveTimeout", parameterTypesOfMethod);
			 * method.invoke(oracleConnectionCacheImplObject, argumentsOfMethod); } // // Setting
			 * setCacheInactivityTimeout // if (cacheInactivityTimeoutParameter != null) { method = null;
			 * parameterTypesOfMethod = new Class[] { long.class }; argumentsOfMethod = new Object[] { new
			 * Long(cacheInactivityTimeout) }; method =
			 * oracleConnectionCacheImplClass.getMethod("setCacheInactivityTimeout", parameterTypesOfMethod);
			 * method.invoke(oracleConnectionCacheImplObject, argumentsOfMethod); } // // Set cacheScheme // method =
			 * null; parameterTypesOfMethod = new Class[] { int.class }; // Field schemeField = //
			 * oracleConnectionCacheImplClass.getField("FIXED_RETURN_NULL_SCHEME");
			 * 
			 * Field schemeField = oracleConnectionCacheImplClass.getField(cacheSchemeParameter); argumentsOfMethod =
			 * new Object[] { new Integer(schemeField.getInt(oracleConnectionCacheImplObject)) }; method =
			 * oracleConnectionCacheImplClass.getMethod("setCacheScheme", parameterTypesOfMethod);
			 * method.invoke(oracleConnectionCacheImplObject, argumentsOfMethod);
			 */
			return new NativePoolWrapper((DataSource) ocpdsObject);
		} catch (ClassNotFoundException cnfe) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "OracleConnectionPoolDataSourceFactory::createConnectionPool:",
					cnfe);

			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"OracleConnectionPoolDataSourceFactory::createConnectionPoolDataSource::ClassNotFoundException"
							+ cnfe.getMessage());
		} catch (InvocationTargetException ite) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "OracleConnectionPoolDataSourceFactory::createConnectionPool:",
					ite);

			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"OracleConnectionPoolDataSourceFactory::createConnectionPoolDataSource::InvocationTargetException"
							+ ite.getMessage());
		} catch (IllegalAccessException iae) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "OracleConnectionPoolDataSourceFactory::createConnectionPool:",
					iae);

			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"OracleConnectionPoolDataSourceFactory::createConnectionPoolDataSource::IllegalAccessExceptionTarget"
							+ iae.getMessage());
		} catch (InstantiationException ie) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "OracleConnectionPoolDataSourceFactory::createConnectionPool:",
					ie);

			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"OracleConnectionPoolDataSourceFactory::createConnectionPoolDataSource::InstantiationException"
							+ ie.getMessage());
		} catch (NoSuchMethodException ie) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "OracleConnectionPoolDataSourceFactory::createConnectionPool:",
					ie);

			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"OracleConnectionPoolDataSourceFactory::createConnectionPoolDataSource::NoSuchMethodException"
							+ ie.getMessage());
		} /*
			 * catch (NoSuchFieldException ie) { it.eng.sil.util.TraceWrapper.debug(_logger,
			 * "OracleConnectionPoolDataSourceFactory::createConnectionPool:", ie);
			 * 
			 * throw new EMFInternalError(EMFErrorSeverity.ERROR,
			 * "OracleConnectionPoolDataSourceFactory::createConnectionPoolDataSource::NoSuchFieldException" +
			 * ie.getMessage()); }
			 */
		// end tryCatch//end tryCatch
	} //
} // end OracleConnectionPoolDataSourceFactory
