package it.eng.afExt.utils;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutorObject;

public class TransactionQueryExecutor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(TransactionQueryExecutor.class.getName());
	public static final String UPDATE = "UPDATE";
	public static final String DELETE = "DELETE";
	public static final String INSERT = "INSERT";
	public static final String SELECT = "SELECT";
	protected DataConnection dataConnection = null; // connessione al DB
	protected boolean dbExceptions = false;
	// per ricordare se ci sono state eccezioni di exec sul DB
	protected boolean rollBackAfterDBExceptions = false;
	// per ricordare se è stato fatto rollback dopo eccezioni su exec sul DB
	private String className = this.getClass().getName();
	// private boolean wasAutoCommit;
	private RequestContainer requestContiner;
	private ResponseContainer responseContainer;

	public TransactionQueryExecutor(DataConnection dataConnection, RequestContainer requestContainer,
			ResponseContainer responseContainer) {
		this.dataConnection = dataConnection;
		this.requestContiner = requestContainer;
		this.responseContainer = responseContainer;
	}

	/**
	 * Costruttore apre la connessione al DB utilizzando il pool indicato. la connessione sarà utilizzata per eseguire
	 * la o le query indicate
	 */
	public TransactionQueryExecutor(String pool) throws EMFInternalError {
		this(pool, null, null);
	}

	public TransactionQueryExecutor(String pool, RequestContainer requestContainer, ResponseContainer responseContainer)
			throws EMFInternalError {
		DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
		dataConnection = dataConnectionManager.getConnection(pool);
		this.requestContiner = requestContainer;
		this.responseContainer = responseContainer;
	}

	public TransactionQueryExecutor(String pool, com.engiweb.framework.dispatching.module.AbstractModule m)
			throws EMFInternalError {
		this(pool, m.getRequestContainer(), m.getResponseContainer());
	}

	public TransactionQueryExecutor(String pool, com.engiweb.framework.dispatching.module.AbstractHttpModule m)
			throws EMFInternalError {
		this(pool, m.getRequestContainer(), m.getResponseContainer());
	}

	public TransactionQueryExecutor(String pool, com.engiweb.framework.dispatching.action.AbstractAction m)
			throws EMFInternalError {
		this(pool, m.getRequestContainer(), m.getResponseContainer());
	}

	public TransactionQueryExecutor(String pool, com.engiweb.framework.dispatching.action.AbstractHttpAction m)
			throws EMFInternalError {
		this(pool, m.getRequestContainer(), m.getResponseContainer());
	}

	/** DEPRECATED */
	/*
	 * public TransactionQueryExecutor(DataConnection newDataConnection)throws EMFInternalError{ dataConnection =
	 * newDataConnection; }
	 */
	/**
	 * Inizializza una transazione
	 */
	public void initTransaction() throws EMFInternalError {
		dataConnection.initTransaction();
	}

	/**
	 * Commit di una transazione
	 */
	public void commitTransaction() throws EMFInternalError {
		dataConnection.commitTransaction();
		Utils.releaseResources(dataConnection, null, null);
	}

	/**
	 * Rollback di una transazione
	 */
	public void rollBackTransaction() throws EMFInternalError {
		dataConnection.rollBackTransaction();
		Utils.releaseResources(dataConnection, null, null);
		// in caso ci siano state eccezioni sull'esecuzione di statement su db
		// si ricorda
		// che è stato eseguito un rollback.
		if (dbExceptions) {
			rollBackAfterDBExceptions = true;
		}
	}

	/***
	 * Chiusura di una transazione
	 */
	public void closeConnTransaction() throws EMFInternalError {
		Utils.releaseResources(dataConnection, null, null);
	}

	/**
	 * Esecuzione di una query
	 */
	public Object executeQuery(RequestContainer requestContainer, ResponseContainer responseContainer, SourceBean query,
			String type) throws EMFInternalError {
		QueryExecutorObject queryExecObj = new QueryExecutorObject();
		queryExecObj.setRequestContainer(requestContainer);
		queryExecObj.setResponseContainer(responseContainer);
		queryExecObj.setDataConnection(dataConnection);
		queryExecObj.setQuery(query);
		queryExecObj.setType(type);
		queryExecObj.setTransactional(true);
		queryExecObj.setDontForgetException(false);
		Object result = queryExecObj.exec();
		if (result == null) {
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Errore nella transazione");
		} else if (result instanceof EMFInternalError) {
			throw (EMFInternalError) result;
		} else if (result instanceof EMFUserError) {
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Errore nella transazione", (Exception) result);
		} else if (result instanceof Exception) {
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Errore nella transazione", (Exception) result);
		}
		return result;
	}

	/**
	 * Esegue una operazione col db partendo dal nome dello statement censito nel file di configurazione generale, dall'
	 * array di parametri gia' costruito, e dal tipo di operazione.
	 * 
	 * @return il SourceBean della select oppure il Boolean del risultato della insert/update/delete
	 */
	public Object executeQuery(String stmName, Object[] fieldWhere, String type) throws EMFInternalError {
		String statement = SQLStatements.getStatement(stmName);
		return executeQueryByStringStatement(statement, fieldWhere, type);
	}

	private void setQueryParameter(String fieldname, ArrayList parameter, Object field, DataConnection dc) {
		if (fieldname == null) {
			fieldname = "";
		}
		if (field instanceof String) {
			parameter.add(dc.createDataField(fieldname, Types.VARCHAR, field));
		} else if (field instanceof Integer) {
			parameter.add(dc.createDataField(fieldname, Types.INTEGER, field));
		} else if (field instanceof Double) {
			parameter.add(dc.createDataField(fieldname, Types.DOUBLE, field));
		} else if (field instanceof Float) {
			parameter.add(dc.createDataField(fieldname, Types.FLOAT, field));
		} else if (field instanceof Character) {
			parameter.add(dc.createDataField(fieldname, Types.CHAR, field));
		} else if (field instanceof BigDecimal) {
			parameter.add(dc.createDataField(fieldname, Types.NUMERIC, field));
		} else if (field instanceof Long) {
			parameter.add(dc.createDataField(fieldname, Types.NUMERIC, field));
		} else if (field instanceof Object) {
			if (field == null) {
				parameter.add(dc.createDataField(fieldname, Types.VARCHAR, field));
			} else {
				parameter.add(dc.createDataField(fieldname, Types.VARCHAR, field.toString()));
			}
		} else {
			parameter.add(dc.createDataField(fieldname, Types.VARCHAR, field));
		}
	}

	/**
	 * Esegue una operazione col db partendo dal nome dello statement censito nel file di configurazione generale, dall'
	 * array di parametri gia' costruito, e dal tipo di operazione.
	 * 
	 * @return il SourceBean della select oppure il Boolean del risultato della insert/update/delete
	 */
	public Object executeQueryByStringStatement(String statement, Object[] fieldWhere, String type)
			throws EMFInternalError {
		ArrayList inputParameters = null;
		// String statement = SQLStatements.getStatement(stmName);
		if (fieldWhere == null) {
			inputParameters = new ArrayList(0);
		} else {
			inputParameters = new ArrayList(fieldWhere.length);
			for (int i = 0; i < fieldWhere.length; i++) {
				setQueryParameter("", inputParameters, fieldWhere[i], dataConnection);
			}
		}
		QueryExecutorObject queryExecObj = new QueryExecutorObject();
		// queryExecObj.setRequestContainer(requestContainer);
		// queryExecObj.setResponseContainer(responseContainer);
		queryExecObj.setRequestContainer(this.requestContiner);
		queryExecObj.setResponseContainer(this.responseContainer);
		queryExecObj.setStatement(statement);
		queryExecObj.setDataConnection(dataConnection);
		queryExecObj.setInputParameters(inputParameters);
		queryExecObj.setType(type);
		queryExecObj.setTransactional(true); // NB!
		queryExecObj.setDontForgetException(true);
		Object result = queryExecObj.exec();
		if (result == null) {
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Errore nella transazione");
		} else if (result instanceof EMFInternalError) {
			throw (EMFInternalError) result;
		} else if (result instanceof EMFUserError) {
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Errore nella transazione", (Exception) result);
		} else if (result instanceof Exception) {
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Errore nella transazione", (Exception) result);
		}
		return result;
	}

	/**
	 * Metodo richiamato dal Garbage Collector
	 */
	protected void finalize() {
		// se ci sono state eccezioni di exec su db e nessuna classe esterna ha
		// invocato il metodo rollBackTransaction
		// ci si preoccupa di farlo a questo punto se la connessione è ancora
		// valida
		// _logger.warn("Thread: " + Thread.currentThread().getId() + ":: " + className + "::finalize: avvio rilascio della connessione al POOL");
		
		// if (requestContiner != null) _logger.warn("Thread: " + Thread.currentThread().getId() + ":: " + className + "::finalize. Request Container " + requestContiner.toXML());
		// if(requestContiner!= null && requestContiner.getSessionContainer() != null) _logger.warn("Thread: " + Thread.currentThread().getId() + ":: " + className + "::finalize. Session Container " + requestContiner.getSessionContainer().toXML());
		// if (responseContainer!= null) _logger.warn("Thread: " + Thread.currentThread().getId() + ":: " + className + "::finalize. Response Container: "+ responseContainer.toXML());		
		
		// Recupero e stampa dello stacktrace
		/*
		StackTraceElement[] list = Thread.currentThread().getStackTrace();		
		if (list != null) {
			for (StackTraceElement item : list) {
				_logger.warn("Thread: " + Thread.currentThread().getId() + ":: " + className + "::finalize. StackTrace: " + item.getClassName() + "." + item.getMethodName());
			}
		}*/
		
		if (dbExceptions && !rollBackAfterDBExceptions && dataConnection != null) {
			try {
				_logger.warn("Thread: " + Thread.currentThread().getId() + ":: " + className + "::finalize: rollback della transazione");
				dataConnection.rollBackTransaction();
			} catch (Exception e) {
			}
		}
		
		if (dataConnection != null && !dataConnection.isClosed()) {
			_logger.warn("Thread: " + Thread.currentThread().getId() + ":: " + className + "::finalize: rilascio della connessione");
		}
		Utils.releaseResources(dataConnection, null, null);
		// _logger.warn("Thread: " + Thread.currentThread().getId() + ":: " + className + "::finalize: fine rilascio della connessione al POOL");
	}

	public DataConnection getDataConnection() {
		return this.dataConnection;
	}

	/**
	 * @param connection
	 */
	public void setDataConnection(DataConnection connection) {
		dataConnection = connection;
	}

} // public class TransactionQueryExecutor
