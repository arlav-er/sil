package it.eng.sil.module;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;

/**
 * Classe base astratta per le strategie di esecuzione delle query. Implementa lo "Strategy" Design Pattern.
 * 
 * @author Corrado Vaccari
 * @created November 28, 2003
 */
public abstract class AbstractQueryStrategy {

	private AbstractSimpleModule module;

	/**
	 * Costruttore.
	 * 
	 * @param module
	 *            Modulo che utilizza questo strategy
	 */
	public AbstractQueryStrategy(AbstractSimpleModule module) {

		if (module == null) {
			throw new IllegalArgumentException("AbstractSimpleModule required");
		}

		this.module = module;
	}

	/**
	 * Gets the module attribute of the AbstractQueryStrategy object
	 * 
	 * @return The module value
	 */
	public AbstractSimpleModule getModule() {
		return this.module;
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
	public abstract Object executeQuery(SourceBean statement, String type) throws QueryStrategyException;

	/**
	 * restituisce la connessione aperta in transazione o null.
	 * 
	 * @return DataConnection
	 */
	public abstract DataConnection getTXDataConnection();
}
