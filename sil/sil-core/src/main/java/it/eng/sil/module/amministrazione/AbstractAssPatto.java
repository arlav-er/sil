package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;

public abstract class AbstractAssPatto extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AbstractAssPatto.class.getName());
	protected final static int INSERT = 0;
	protected final static int UNDEFINED = 3;
	protected final static int UPDATE = 2;
	protected final static int DELETE = 1;
	private String className = this.getClass().getName();
	private String pool = null;

	public void service(SourceBean request, SourceBean response) throws Exception {
		setPool(getPool());

		ReportOperationResult result = new ReportOperationResult(this, response);
		String prgPattoLavoratore = (String) request.getAttribute("PRG_PATTO_LAVORATORE");

		/*
		 * if (prgPattoLavoratore == null) { throw new Exception("il prgPattoLavoratore e' null"); }
		 */
		try {
			if (isTx(request)) {
				switch (getOperation(request)) {
				case INSERT: {
					doTransactionInsert(request);

					break;
				}

				case DELETE:
					doTransactionDelete(request);

					break;

				case UPDATE:
					doTransactionUpdate(request);

					break;

				case UNDEFINED:
				default:
					throw new Exception("tipo di richiesta non gestibile o inesistente");
				}
			} else {
				switch (getOperation(request)) {
				case INSERT: {
					doSimpleInsert(request);

					break;
				}

				case DELETE:
					doSimpleDelete(request);

					break;

				case UPDATE:
					doSimpleUpdate(request);

					break;

				case UNDEFINED:
				default:
					throw new Exception("tipo di richiesta non gestibile o inesistente");
				}
			}

			/*
			 * if (prgPattoLavoratore.equals("")) { doSimpleInsert(request); } else { doTransactionInsert(request); }
			 */
			result.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::insert:", request);

		} catch (Exception e) {
			result.reportFailure(MessageCodes.General.INSERT_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::insert:", e);

		}
	}

	/**
	 * Esegue l'inserimento solo nella tabella gestita dalla pagina chiamante senza intervenire sulla
	 * an_lav_patto_scelta non utilizzando transazioni.
	 * 
	 * @exception Exception
	 *                Se l' operazione fallisce
	 */
	protected void doSimpleInsert(SourceBean request) throws Exception {
		SourceBean statement = getInsertStatement();
		setInsertParameter(request);

		Boolean esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), getPool(),
				statement, "INSERT");

		if ((esito == null) || (!esito.booleanValue())) {
			throw new Exception("impossibile inserire il record");
		}
	}

	/**
	 * 
	 * 
	 */
	protected void doSimpleDelete(SourceBean request) throws Exception {
		String pool = (String) getConfig().getAttribute("POOL");

		SourceBean statement = getDeleteStatement(); // (SourceBean)
														// getConfig().getAttribute("QUERIES.DELETE_IND_T");
		setDeleteParameter(request);

		Boolean esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), getPool(),
				statement, "DELETE");

		if (!esito.booleanValue()) {
			throw new Exception("impossibile cancellare il record");
		}
	}

	/**
	 * Esegue l' operazione di inserimento sia nella tabella gestita dalla pagina chiamante sia nella tabella
	 * am_lav_patto_scelta in una transazione.
	 * 
	 * @exception Exception
	 *                Se fallisce l' operazione
	 */
	protected void doTransactionInsert(SourceBean request) throws Exception {
		TransactionQueryExecutor queryExecutor = null;

		try {
			//
			SourceBean stmIndispT = getInsertStatement();
			SourceBean insPatto = getInsLavPattoSceltaStatement();

			// String pool = (String) getConfig().getAttribute("POOL");
			queryExecutor = new TransactionQueryExecutor(getPool());
			queryExecutor.initTransaction();
			setTxInsParamenters(queryExecutor, request);

			Boolean res = null;

			// inserimento del record specifico della pagina
			res = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), stmIndispT,
					"INSERT");

			if (!res.booleanValue()) {
				throw new Exception("impossibile inserire la indisponibilità temporanea");
			}

			// inserimento del legame col patto
			res = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), insPatto,
					"INSERT");

			if (!res.booleanValue()) {
				throw new Exception("impossibile inserire la associazione in am_lav_patto_scelta");
			}

			queryExecutor.commitTransaction();
		} catch (Exception e) {
			queryExecutor.rollBackTransaction();
			throw e;
		}
	}

	/**
	 * Esegue l' operazione di cancellazione sia dalla tabella gestita dalla pagina chiamante sia dalla tabella
	 * am_lav_patto_scelta in una transazione.
	 * 
	 * @exception Exception
	 *                Se fallisce l' operazione
	 */
	protected void doTransactionDelete(SourceBean request) throws Exception {
		// String prgLavPattoScelta = (String)
		// request.getAttribute("prglavpattoscelta");
		TransactionQueryExecutor queryExecutor = null;

		try {
			SourceBean stmDelIndisp = getDeleteStatement(); // (SourceBean)
															// getConfig().getAttribute("QUERIES.DELETE_IND_T");
			SourceBean stmDelLavPatto = getDelLavPattoSceltaStatement(); // (SourceBean)
																			// getConfig().getAttribute("QUERIES.DELETE_LAV_PATTO_SCELTA");
			queryExecutor = new TransactionQueryExecutor(getPool());
			queryExecutor.initTransaction();
			setTxDelParamenters(queryExecutor, request);
			Boolean ret = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
					stmDelLavPatto, "DELETE");
			if (!ret.booleanValue()) {
				throw new Exception("impossibile cancellare la associazione col patto");
			}
			ret = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), stmDelIndisp,
					"DELETE");

			if (!ret.booleanValue()) {
				throw new Exception("impossibile cancellare la indisponibilità temporanea");
			}

			queryExecutor.commitTransaction();
		} catch (Exception e) {
			queryExecutor.rollBackTransaction();
			throw e;
		}
	}

	protected void doTransactionUpdate(SourceBean request) throws Exception {
		// String prgPattoLavoratore = (String)
		// Request.getAttribute("PRG_PATTO_LAVORATORE");
		TransactionQueryExecutor queryExecutor = null;

		/*
		 * if (prgPattoLavoratore == null) { throw new Exception("il prgpattolavoratore e' null"); }
		 */
		try {
			if (!isInsert(request)) {
				// cancella la associazione al patto
				SourceBean stmUp = getUpdateStatement(); // (SourceBean)
															// getConfig().getAttribute("QUERIES.UPDATE_IND_T");
				SourceBean stmDel = getDelLavPattoSceltaStatement(); // (SourceBean)
																		// getConfig().getAttribute("QUERIES.DEL_ASSOCIAZIONE_PATTO");

				// String pool = (String) getConfig().getAttribute("POOL");
				queryExecutor = new TransactionQueryExecutor(getPool());

				// ReportOperationResult result = new
				// ReportOperationResult(this, Response);
				queryExecutor.initTransaction();
				setTxUpParamenters(queryExecutor, request);

				Boolean res = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
						stmDel, "DELETE");

				if (!res.booleanValue()) {
					throw new Exception("impossibile cancellare la associazione al patto");
				}

				res = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), stmUp,
						"UPDATE");

				if (!res.booleanValue()) {
					throw new Exception("impossibile aggiornare la indisponibilità temporanea");
				}

				queryExecutor.commitTransaction();
			} else {
				// inserisci una nuova associazione al patto
				SourceBean stmUp = getUpdateStatement(); // (SourceBean)
															// getConfig().getAttribute("QUERIES.UPDATE_IND_T");
				SourceBean insPatto = getInsLavPattoSceltaStatement(); // (SourceBean)
																		// getConfig().getAttribute("QUERIES.INS_ASSOCIAZIONE_PATTO");

				// SourceBean stmPkPatto = (SourceBean)
				// getConfig().getAttribute("QUERIES.NEW_AM_LAV_PATTO_SCELTA");
				queryExecutor = new TransactionQueryExecutor(getPool());
				queryExecutor.initTransaction();
				setTxUpParamenters(queryExecutor, request);

				Boolean res = null;

				res = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), insPatto,
						"INSERT");

				if (!res.booleanValue()) {
					throw new Exception("impossibile inserire la associazione in am_lav_patto_scelta");
				}

				res = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), stmUp,
						"UPDATE");

				if (!res.booleanValue()) {
					throw new Exception("impossibile aggiornare la indisponibilita temporanea");
				}

				queryExecutor.commitTransaction();
			}
		} catch (Exception e) {
			queryExecutor.rollBackTransaction();
			throw e;
		}
	}

	/**
	 * 
	 */
	protected void doSimpleUpdate(SourceBean request) throws Exception {
		// String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = getUpdateStatement();
		setUpdateParameter(request);

		Boolean esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), getPool(),
				statement, "UPDATE");

		if ((esito == null) || (!esito.booleanValue())) {
			throw new Exception("impossibile aggiornare la indisponibilità temporanea");
		}
	}

	public String getPool() {
		return (String) getConfig().getAttribute("POOL");
	}

	public void setPool(String p) {
		this.pool = p;
	}

	/**
	 * Impostazione dei paramtri necessari allo statement di inserimento <b>senza transazione</b>(Es. primary key)
	 * 
	 * @exception Exception
	 *                Se fallisce una operazione sul db e/o risulta impossibile impostare i parametri
	 */
	protected abstract void setInsertParameter(SourceBean request) throws Exception;

	/**
	 * Impostazione dei paramtri necessari allo statement di cancellazione <b>senza transazione</b>
	 * 
	 * @exception Exception
	 *                Se fallisce una operazione sul db e/o risulta impossibile impostare i parametri
	 */
	protected abstract void setDeleteParameter(SourceBean request) throws Exception;

	/**
	 * Impostazione dei paramtri necessari allo statement di aggiornamento <b>senza transazione</b>
	 * 
	 * @exception Exception
	 *                Se fallisce una operazione sul db e/o risulta impossibile impostare i parametri
	 */
	protected abstract void setUpdateParameter(SourceBean request) throws Exception;

	/**
	 * @return SourceBean Lo statement per l' inserimento nella tabella am_lav_patto_scelta
	 */
	protected abstract SourceBean getInsLavPattoSceltaStatement();

	/**
	 * @return SourceBean Lo statement per la cancellazione dalla tabella am_lav_patto_scelta
	 */
	protected abstract SourceBean getDelLavPattoSceltaStatement();

	/**
	 * @return SourceBean Lo statement per l' inserimento nella tabella specifica gestita dalla pagina
	 */
	protected abstract SourceBean getInsertStatement();

	/**
	 * @return SourceBean Lo statement per la cancellazione dalla tabella specifica gestita dalla pagina
	 */
	protected abstract SourceBean getDeleteStatement();

	/**
	 * @return SourceBean Lo statement per l' aggiornamento della tabella specifica gestita dalla pagina
	 */
	protected abstract SourceBean getUpdateStatement();

	/**
	 * Bisogna impostare i parametri per le successive queries. Eventualmente fare una lettura dal db usando il
	 * TransactionQueryExecutor e quindi settando il valore ottenuto nella request.
	 * 
	 * @exception Exception
	 */
	protected abstract void setTxInsParamenters(TransactionQueryExecutor queryExecutor, SourceBean request)
			throws Exception;

	/**
	 * Bisogna impostare i parametri per le successive queries. Eventualmente fare una lettura dal db usando il
	 * TransactionQueryExecutor e quindi settando il valore ottenuto nella request.
	 * 
	 * @exception Exception
	 */
	protected abstract void setTxDelParamenters(TransactionQueryExecutor queryExecutor, SourceBean request)
			throws Exception;

	/**
	 * Bisogna impostare i parametri per le successive queries. Eventualmente fare una lettura dal db usando il
	 * TransactionQueryExecutor e quindi settando il valore ottenuto nella request.
	 * 
	 * @exception Exception
	 */
	protected abstract void setTxUpParamenters(TransactionQueryExecutor queryExecutor, SourceBean request)
			throws Exception;

	/**
	 * @return int il tipo di operazione da eseguire INSERT inserimento UPDATE aggiornamento DELETE cancellazione
	 *         UNDEFINED situazione di errore
	 */
	protected abstract int getOperation(SourceBean request);

	/**
	 * @return true se si deve eseguire una operazione con gestione della transazione false se l' operazione col db e'
	 *         non transazionale
	 */
	private boolean isTx(SourceBean request) throws Exception {
		String op = (String) request.getAttribute("operazioneColPatto");

		if ((op == null) || ((Integer.parseInt(op) < -1) && (Integer.parseInt(op) > 1))) {
			throw new Exception("operazioneColPatto = null impossibile scegliere quale operazione eseguire col patto");
		}

		return (!op.equals("0"));
	}

	/**
	 * @return true se si deve inserire un record di associazione nella tabella am_lav_patto_scelta false se si deve
	 *         cancellare il record di associazione nella tabella am_lav_patto_scelta
	 */
	private boolean isInsert(SourceBean request) throws Exception {
		String op = (String) request.getAttribute("operazioneColPatto");

		if (op.equals("1")) {
			return false;
		} else if (op.equals("-1")) {
			return true;
		} else {
			throw new Exception("impossibile scegliere se bisogna cancellare od inseriere una associazione col patto");
		}
	}
}