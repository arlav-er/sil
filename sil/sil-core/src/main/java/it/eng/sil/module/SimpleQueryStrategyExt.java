/*
 * Creato il 26-ott-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.util.QueryExecutor;

/**
 * @author savino
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class SimpleQueryStrategyExt extends AbstractQueryStrategyExt {

	private boolean reportException;

	/**
	 * @param _config
	 * @param reportException
	 */
	public SimpleQueryStrategyExt(ContestoComune _config, boolean reportException) {
		super(_config);
		this.reportException = reportException;
		// TODO Stub di costruttore generato automaticamente
	}

	/**
	 * @param module
	 */
	public SimpleQueryStrategyExt(ContestoComune module) {
		this(module, false);
		// TODO Stub di costruttore generato automaticamente
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see it.eng.sil.module.AbstractQueryStrategy2#executeQuery(com.engiweb.framework.base.SourceBean,
	 * java.lang.String)
	 */
	public Object executeQuery(SourceBean statement, String type) throws QueryStrategyException {
		// TODO Stub di metodo generato automaticamente
		if (reportException) {
			return QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
					(String) getConfig().getAttribute("POOL"), statement, type, true);
		} else {
			return QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
					(String) getConfig().getAttribute("POOL"), statement, type);
		}
	}

	/**
	 * restituisce la connessione aperta in transazione o null. Nel caso null.
	 * 
	 * @return DataConnection
	 */
	public DataConnection getTXDataConnection() {
		return null;
	}

}
