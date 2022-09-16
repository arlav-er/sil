package com.engiweb.framework.paginator.smart.impl;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.paginator.smart.IFaceRowHandler;
import com.engiweb.framework.util.QueryExecutor;

/**
 * La classe <code>DBRowHandler</code> estende <code>AbstractRowHandler</code> ed implementa i metodi di accesso alle
 * sorgenti di dati riconducibili al pacchetto dbaccess del framework.
 * <p>
 * 
 * @version 1.0, 15/03/2003
 */
public class DBRowHandler extends DBRowProvider implements IFaceRowHandler {
	/**
	 * Costruisce un <code>DBRowProvider</code>. Questo costruttore &egrave; vuoto ed ogni azione di inizializzazione
	 * &egrave; demandata al metodo <code>init(SourceBean config)</code>.
	 * <p>
	 * 
	 * @see DBRowHandler#init(SourceBean)
	 */
	public DBRowHandler() {
		super();
	} // public DBRowHandler()

	/**
	 * Cancella l'ultima riga recuperata o individuata con il comando <code>absolute(int).
	 * <p>
	 * &#64;return <code>boolean</code> esito dell'operazione
	 */
	public boolean deleteRow() {
		SourceBean config = getConfig();
		SourceBean query = (SourceBean) config.getAttribute("DELETE_QUERY");
		String pool = (String) config.getAttribute("POOL");
		return ((Boolean) (QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, query,
				"DELETE"))).booleanValue();
	} // public boolean deleteRow()

	/**
	 * Esegue l'inserimento di una riga nella lista. Ritorna l'esito dell'operazione.
	 * <p>
	 * 
	 * @return <code>boolean</code> esito dell'operazione
	 */
	public boolean insertRow() {
		SourceBean config = getConfig();
		SourceBean query = (SourceBean) config.getAttribute("INSERT_QUERY");
		String pool = (String) config.getAttribute("POOL");
		return ((Boolean) (QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, query,
				"INSERT"))).booleanValue();
	} // public boolean insertRow()

	/**
	 * Esegue l'operazione di select dei dati di dettaglio relativi all'ultima riga recuperata o individuata con il
	 * comando <code>absolute(int).
	 * <p>
	 * &#64;return <code>SourceBean</code> che contiene i dati richiesti.
	 */
	public SourceBean selectRow() {
		SourceBean config = getConfig();
		SourceBean query = (SourceBean) config.getAttribute("SELECT_QUERY");
		String pool = (String) config.getAttribute("POOL");
		return (SourceBean) (QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, query,
				"SELECT"));
	} // public SourceBean selectRow()

	/**
	 * Esegue l'operazione di update dei dati di dettaglio relativi all'ultima riga recuperata o individuata con il
	 * comando <code>absolute(int). I dati
	 * da modificare vengono recuperati come di consueto dal contesto attraverso
	 * i parametri dello statement fornito in fase di configurazione.
	 * <p>
	 * &#64;return <code>boolean</code> esito dell'operazione
	 * @see QueryExecutor#executeQuery(RequestContainer, ResponseContainer, String, SourceBean, String)
	 */
	public boolean updateRow() {
		SourceBean config = getConfig();
		SourceBean query = (SourceBean) config.getAttribute("UPDATE_QUERY");
		String pool = (String) config.getAttribute("POOL");
		return ((Boolean) (QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, query,
				"UPDATE"))).booleanValue();
	} // public boolean updateRow()
} // public class DBRowHandler extends DBRowProvider implements
	// IFaceRowHandler
