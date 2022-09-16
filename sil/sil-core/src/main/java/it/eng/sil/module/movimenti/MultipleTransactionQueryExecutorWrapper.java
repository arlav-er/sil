package it.eng.sil.module.movimenti;

import com.engiweb.framework.error.EMFInternalError;

public class MultipleTransactionQueryExecutorWrapper extends MultipleTransactionQueryExecutor {

	private boolean rollbackCatched;
	private boolean txEnabled;

	public MultipleTransactionQueryExecutorWrapper(String pool) throws EMFInternalError {
		super(pool);
	}

	/**
	 * La commit viene effettivamente chiamata SOLO SE la transazione e' stata abilitata con la chiamata al metodo
	 * #enableTransaction().
	 */
	public void commitTransaction() throws EMFInternalError {
		if (txEnabled)
			super.commitTransaction();
	}

	/**
	 * La rollback viene effettivamente chiamata SOLO SE la transazione e' stata abilitata con la chiamata al metodo
	 * #enableTransaction().
	 */
	public void rollBackTransaction() throws EMFInternalError {
		if (txEnabled)
			super.rollBackTransaction();
		rollbackCatched = true;
	}

	/**
	 * La closeConnection viene effettivamente chiamata SOLO SE la transazione e' stata abilitata con la chiamata al
	 * metodo #enableTransaction().
	 */
	public void closeConnection() {
		if (txEnabled)
			super.closeConnection();
	}

	/**
	 * La initTransaction viene effettivamente chiamata SOLO SE la transazione e' stata abilitata con la chiamata al
	 * metodo #enableTransaction().
	 */
	public void initTransaction() throws EMFInternalError {
		if (txEnabled)
			super.initTransaction();
	}

	/**
	 * Questo metodo non deve essere abilitato: chiama sempre la finalize della superclasse.
	 */
	protected void finalize() {
		super.finalize();
	}

	/**
	 * Chiamata alla rollback della transazione
	 */
	public void TRUE_RollBackTransaction() throws EMFInternalError {
		super.rollBackTransaction();
	}

	/**
	 * Chiamata alla commit della transazione
	 */
	public void TRUE_CommitTransaction() throws EMFInternalError {
		super.commitTransaction();
	}

	/**
	 * Chiamata alla init della transazione
	 */
	public void TRUE_InitTransaction() throws EMFInternalError {
		super.initTransaction();
		rollbackCatched = false;
	}

	/**
	 * Chiamata alla close della connessione (rilascio al pool)
	 */
	public void TRUE_CloseConnection() {
		super.closeConnection();
		rollbackCatched = false;
	}

	public boolean isRollbackCathed() {
		return this.rollbackCatched;
	}

	/**
	 * abilitazione delle chiamate ai metodi ereditati dalla classe MultipleTransactionQueryExecutor.<br>
	 * Quindi la chiamata del metodo {@link #rollBackTransaction()} non produce effetti.
	 */
	public void enableTXOperation() {
		this.txEnabled = true;
	}

	/**
	 * disabilitazione delle chiamate ai metodi ereditati dalla classe MultipleTransactionQueryExecutor.<br>
	 * Quindi la chiamata del metodo {@link #rollBackTransaction()} produrra' effettivamente la rollback della
	 * transazione.
	 */
	public void disableTXOperation() {
		this.txEnabled = false;
	}

}
