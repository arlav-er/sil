package it.eng.sil.module;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.util.QueryExecutor;

/**
 * Strategia di query semplice, quindi senza transazioni.
 * 
 * @author vaccari
 * @created December 1, 2003
 */
class SimpleQueryStrategy extends AbstractQueryStrategy {
	private boolean reportException = false;

	/**
	 * Constructor for the SimpleQueryStrategy object
	 * 
	 * @param module
	 *            Modulo che utilizza questo strategy
	 */
	public SimpleQueryStrategy(AbstractSimpleModule module) {
		super(module);
	}

	/**
	 * Constructor for the SimpleQueryStrategy object
	 * 
	 * @param module
	 *            Modulo che utilizza questo strategy
	 */
	public SimpleQueryStrategy(AbstractSimpleModule module, boolean reportException) {
		super(module);
		this.reportException = reportException;
	}

	/**
	 * Esegue la query indicata.
	 * 
	 * @param statement
	 *            Statement della query
	 * @param type
	 *            Tipo di query ("UPDATE" ecc.)
	 * @return Risultato della query, dipende dal tipo di query
	 * @exception QueryStrategyException
	 *                Exception lanciata in caso di errore
	 */
	public Object executeQuery(SourceBean statement, String type) throws QueryStrategyException {
		if (reportException) {
			return QueryExecutor.executeQuery(getModule().getRequestContainer(), getModule().getResponseContainer(),
					getModule().getPool(), statement, type, true);
		} else {
			return QueryExecutor.executeQuery(getModule().getRequestContainer(), getModule().getResponseContainer(),
					getModule().getPool(), statement, type);
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
