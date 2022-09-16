/*
 * Creato il 26-ott-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;

/**
 * @author savino
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */

import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * Strategia di query transazionale.
 * 
 * @author Corrado Vaccari
 * @created December 1, 2003
 */
class TransQueryStrategyExt extends AbstractQueryStrategyExt {

	private TransactionQueryExecutor transQueryExec;

	/**
	 * Constructor for the TransQueryStrategy object
	 * 
	 * @param module
	 *            Modulo che utilizza questo strategy
	 * @param transQueryExec
	 *            Query executor usato per gestire le transazioni
	 */
	public TransQueryStrategyExt(ContestoComune module, TransactionQueryExecutor transQueryExec) {

		super(module);

		if (transQueryExec == null) {
			throw new IllegalArgumentException("TransactionQueryExecutor required");
		}

		this.transQueryExec = transQueryExec;
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

		try {
			return transQueryExec.executeQuery(getRequestContainer(), getResponseContainer(), statement, type);
		} catch (EMFInternalError intEx) {
			throw new QueryStrategyException(
					"Error calling QueryExecutor.executeQuery from SimpleQueryStrategy.executeQuery", intEx);
		}
	}

	/**
	 * restituisce la connessione aperta in transazione o null. Nel caso la connessione aperta.
	 * 
	 * @return DataConnection
	 */
	public DataConnection getTXDataConnection() {
		return transQueryExec.getDataConnection();
	}

}
