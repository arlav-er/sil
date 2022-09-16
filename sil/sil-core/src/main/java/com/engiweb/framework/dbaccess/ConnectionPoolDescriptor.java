package com.engiweb.framework.dbaccess;

import java.util.HashMap;
import java.util.Map;

/**
 * Questa classe rappresenta il descrittore di un pool di connessioni nel sottosistema dell'accesso dei dati in
 * particolare una connessione è individuata da:
 * <li>un nome
 * <li>una factory class
 * <li>una mappa di parametri
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class ConnectionPoolDescriptor {
	private String connectionPoolName = null;
	private String connectionPoolFactory = null;
	private Map connectionPoolParameters = null;

	/**
	 * Costruttore
	 */
	public ConnectionPoolDescriptor() {
		connectionPoolParameters = new HashMap();
	} // public ConnectionPoolDescriptor()

	/**
	 * Aggiunge un parametro al descrittore della connessione
	 * 
	 * @param <B>ConnectionPoolParameter
	 *            </B> connectionPoolParameter - l'oggetto rappresentante il parametro della connessione da aggiungere
	 */
	public void addConnectionPoolParameter(ConnectionPoolParameter connectionPoolParameter) {
		connectionPoolParameters.put(connectionPoolParameter.getName(), connectionPoolParameter);
	} // public void addConnectionPoolParameter(ConnectionPoolParameter
		// connectionPoolParameter)

	/**
	 * Ricava il parametro della connessione con nome connectionPoolParameterName
	 * 
	 * @param <B>String
	 *            </B> connectionPoolParameterName - il nome del parametro della connessione da ricavare
	 * @return un oggetto di tipo <B>ConnectionPoolParameter</B> rappresenatnte il parametro cercato
	 */
	public ConnectionPoolParameter getConnectionPoolParameter(String connectionPoolParameterName) {
		return (ConnectionPoolParameter) connectionPoolParameters.get(connectionPoolParameterName);
	} // public ConnectionPoolParameter getConnectionPoolParameter(String

	// connectionPoolParameterName)

	/**
	 * Metodo getter per il parametro connectionPoolFactory
	 * 
	 * @return un oggetto di tipo <B>String</B> con il nome della classe che è factory per il pool descritto
	 */
	public String getConnectionPoolFactory() {
		return connectionPoolFactory;
	} // public String getConnectionPoolFactory()

	/**
	 * Metodo setter per il parametro connectionPoolFactory
	 * 
	 * @param un
	 *            oggetto di tipo <B>String</B> con il nome della classe che è factory per il pool descritto
	 */
	public void setConnectionPoolFactory(String newConnectionPoolFactory) {
		connectionPoolFactory = newConnectionPoolFactory;
	} // public void setConnectionPoolFactory(String newConnectionPoolFactory)

	/**
	 * Metodo getter per il parametro connectionPoolName
	 * 
	 * @return un oggetto di tipo <B>String</B> con il nome del pool descritto
	 */
	public String getConnectionPoolName() {
		return connectionPoolName;
	} // public String getConnectionPoolName()

	/**
	 * Metodo setter per il parametro connectionPoolName
	 * 
	 * @param un
	 *            oggetto di tipo <B>String</B> con il nome del pool descritto
	 */
	public void setConnectionPoolName(String newConnectionPoolName) {
		connectionPoolName = newConnectionPoolName;
	} // public void setConnectionPoolName(String newConnectionPoolName)

	/**
	 * Metodo getter per il parametro connectionPoolParameters
	 * 
	 * @return un oggetto di tipo <B>Map</B> conetennte la mappa dei parametri del pool di connessioni
	 */
	public Map getConnectionPoolParameters() {
		return connectionPoolParameters;
	} // public Map getConnectionPoolParameters()

	/**
	 * Metodo setter per il parametro connectionPoolParameters
	 * 
	 * @param un
	 *            oggetto di tipo <B>Map</B> conetennte la mappa dei parametri del pool di connessioni
	 */
	public void setConnectionPoolParameters(Map newConnectionPoolParameters) {
		connectionPoolParameters = newConnectionPoolParameters;
	} // public void setConnectionPoolParameters(Map
		// newConnectionPoolParameters)
} // end Class ConnectionPoolDescriptor
