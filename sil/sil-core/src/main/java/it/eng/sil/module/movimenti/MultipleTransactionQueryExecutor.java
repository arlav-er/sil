/*
 * Creato il 7-lug-04
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * @author roccetti
 * 
 *         TransactionQueryExecutor per transazioni multiple.
 *         <p/>
 * 
 *         !!!Classe per l'esecuzione di più transazioni con la stessa connessione!!!
 *         <p/>
 * 
 *         !!!E' importantissimo ricordarsi di chiudere la connessione con il metodo closeConnection() dopo aver
 *         eseguito tutte le transazioni.!!!
 * 
 */
public class MultipleTransactionQueryExecutor extends TransactionQueryExecutor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(MultipleTransactionQueryExecutor.class.getName());
	public static int numberOfConnection = 0;

	private String className = this.getClass().getName();

	/**
	 * @param pool
	 *            nome del pool di connessioni
	 * @param m
	 *            modulo
	 * @throws EMFInternalError
	 */
	public MultipleTransactionQueryExecutor(String pool, com.engiweb.framework.dispatching.module.AbstractModule m)
			throws EMFInternalError {
		super(pool, m);
		numberOfConnection++;
	}

	/**
	 * @param pool
	 *            nome del pool di connessioni
	 * @throws EMFInternalError
	 */
	public MultipleTransactionQueryExecutor(String pool) throws EMFInternalError {
		super(pool);
		numberOfConnection++;
	}

	// 24/10/2006 Modififcata da Davide e Franco
	// La modifica è dovuta al fatto che, dal momento che la connessine non
	// veniva chiusa
	// e quindi che le risorse non venivano rilasciate, in validazione massiva
	// rimanevano aperti
	// dei cursori fino al raggiungimento del limite massimo.
	// Questo comportail fallimento dell'inserimento del movimento anche se
	// potenzialemente valido
	/**
	 * Commit di una transazione senza chiusura della connessione
	 */
	public void commitTransaction() throws EMFInternalError {
		super.commitTransaction();
		closeConnection();

		setDataConnection(DataConnectionManager.getInstance().getConnection());
	}

	// 24/10/2006 Modififcata da Davide e Franco
	// La modifica è dovuta al fatto che, dal momento che la connessine non
	// veniva chiusa
	// e quindi che le risorse non venivano rilasciate, in validazione massiva
	// rimanevano aperti
	// dei cursori fino al raggiungimento del limite massimo.
	// Questo comportail fallimento dell'inserimento del movimento anche se
	// potenzialemente valido
	/**
	 * Rollback di una transazione senza chiusura della connessione
	 */
	public void rollBackTransaction() throws EMFInternalError {

		super.rollBackTransaction();
		closeConnection();

		setDataConnection(DataConnectionManager.getInstance().getConnection());
	}

	/**
	 * Metodo per la chiususra della connessione
	 */
	public void closeConnection() {
		Utils.releaseResources(dataConnection, null, null);
		numberOfConnection--;
	}

	/**
	 * Metodo richiamato dal Garbage Collector
	 */
	protected void finalize() {

		super.finalize();
		numberOfConnection--;
		// _logger.warn("Rilascio della connessione al POOL");

		/*
		 * //se ci sono state eccezioni di exec su db e nessuna classe esterna ha invocato il metodo rollBackTransaction
		 * //ci si preoccupa di farlo a questo punto se la connessione è ancora valida if(dbExceptions &&
		 * !rollBackAfterDBExceptions && dataConnection!=null){ try{ dataConnection.rollBackTransaction(); }catch
		 * (Exception e) {} } Utils.releaseResources(dataConnection,null,null);
		 */

	}

}