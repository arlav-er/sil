package com.engiweb.framework.dbaccess.sql;

import java.sql.Connection;
import java.sql.SQLException;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.dbaccess.sql.command.CommandCreator;
import com.engiweb.framework.dbaccess.sql.command.legacy.LegacyCommandCreator;
import com.engiweb.framework.dbaccess.sql.command.std.DefaultCommandCreator;
import com.engiweb.framework.dbaccess.sql.mappers.SQLMapper;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

/**
 * Questa Classe rappresenta una Connessione al DataBase ed è responsabile della :
 * 
 * <li>gestione delle transazioni ( Di Default una dataConnection è auto commited )
 * <li>creazione dei comandi da eseguire
 * <li>creazione dei DataField
 * 
 * ATTENZIONE: UNA VOLTA TERMINATO L'USO DELLA CONNESSIONE E' NECESSARIO RICHIAMARE ESPLICITAMENTE IL METODO close()
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class DataConnection {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DataConnection.class.getName());
	private Connection _connection = null;
	private CommandCreator _commandCreator = null;
	private float _driverVersionNumber = -1;
	private SQLMapper _sqlMapper = null;
	private boolean _closed = false;

	// om20201111: aggiunto il getter di _closed per debug della TransactionQueryExecutor. Quando non più necessario rimuoverlo
	public boolean isClosed() {
		return _closed;
	}
	
	/**
	 * Costruttore : Attenzione la connessione viene creata invocando setAutoCommit(true).
	 * 
	 * @param <code>Connection</code>
	 *            connection - la connessione sql alla base dell'oggetto dataConnection
	 * @param <code>String</code>
	 *            driverVersion - la versione del driver utilizzato dalla connessione
	 * @param <code>SQLMapper</code>
	 *            sqlMapper - la classe per il mappinf dei dataField
	 * @throws <code>EMFInternalError</code>
	 * - Se viene generato qualche errore durante l'inizializzazione della connessione
	 */
	public DataConnection(Connection connection, String driverVersion, SQLMapper sqlMapper) throws EMFInternalError {
		try {
			_connection = connection;
			if (!isRequestDistributed())
				_connection.setAutoCommit(true);
			_commandCreator = getCommandCreator(driverVersion);
			_sqlMapper = sqlMapper;
			_closed = false;
		} // try
		catch (SQLException sqle) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DataConnection::DataConnection:", sqle);

			throw new EMFInternalError(EMFErrorSeverity.ERROR, "DataConnection::DataConnection: " + sqle.getMessage());
		} // catch (SQLException sqle) try
	} // public DataConnection(Connection connection, String driverVersion,

	// SQLMapper sqlMapper) throws EMFInternalError

	/**
	 * Questo metodo è utilizzato per aprire una transazione sulla connessione.
	 * 
	 * @throws <code>EMFInternalError</code>
	 * - Se viene generato qualche errore durante l'inizializzazzione della transazione
	 */
	public void initTransaction() throws EMFInternalError {
		if (isRequestDistributed()) {
			_logger.warn("DataConnection::initTransaction: connessione distribuita !");

			return;
		} // if (isRequestDistributed())
		if (_closed)
			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"DataConnection::initTransaction: connessione già chiusa");
		try {
			_connection.setAutoCommit(false);
		} // try
		catch (SQLException sqle) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DataConnection::initTransaction:", sqle);

			throw new EMFInternalError(EMFErrorSeverity.ERROR, "DataConnection::initTransaction: " + sqle.getMessage());
		} // catch (SQLException sqle) try
	} // public void initTransaction() throws EMFInternalError

	/**
	 * Questo metodo è utilizzato per effettuare la commit delle operazioni effettuate durante una transazione sulla
	 * connessione.
	 * 
	 * @throws <code>EMFInternalError</code>
	 * - Se viene generato qualche errore durante il commit della transazione
	 */
	public void commitTransaction() throws EMFInternalError {
		if (isRequestDistributed()) {
			_logger.warn("DataConnection::commitTransaction: connessione distribuita !");

			return;
		} // if (isRequestDistributed())
		if (_closed)
			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"DataConnection::commitTransaction: connessione già chiusa");
		try {
			_connection.commit();
			_connection.setAutoCommit(true);
		} // try
		catch (SQLException sqle) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DataConnection::commitTransaction:", sqle);

			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"DataConnection::commitTransaction: " + sqle.getMessage());
		} // catch (SQLException sqle) try
	} // public void commitTransaction() throws EMFInternalError

	/**
	 * Questo metodo è utilizzato per eseguire il rollBack delle operazioni effettuate durante una transazione sulla
	 * connessione
	 * 
	 * @throws <code>EMFInternalError</code>
	 * - Se viene generato qualche errore durante il rollBack
	 */
	public void rollBackTransaction() throws EMFInternalError {
		if (isRequestDistributed()) {
			_logger.warn("DataConnection::rollBackTransaction: connessione distribuita !");

			return;
		} // if (isRequestDistributed())
		if (_closed)
			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"DataConnection::rollBackTransaction: connessione già chiusa");
		try {
			_connection.rollback();
			_connection.setAutoCommit(true);
		} // try
		catch (SQLException sqle) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DataConnection::rollBackTransaction:", sqle);

			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"DataConnection::rollBackTransaction: " + sqle.getMessage());
		} // catch (SQLException sqle) try
	} // public void rollBackTransaction() throws EMFInternalError

	/**
	 * Questo metodo è utilizzato per chiudere la connessione
	 * 
	 * @throws <code>EMFInternalError</code>
	 * - Se viene generato qualche errore durante la chiusura della connessione
	 */
	public void close() throws EMFInternalError {
		if (_closed) {
			_logger.debug("DataConnection::close: connessione già chiusa");

			return;
		} // if (_closed)
		_closed = true;
		try {
			if (!(_connection.isClosed())) {
				if (!isRequestDistributed() && !(_connection.getAutoCommit())) {
					_connection.rollback();
					_logger.warn("DataConnection::close: connessione aperta, rolling back !");

					_connection.setAutoCommit(true);
				} // if (!distributeBoolean && !(_connection.getAutoCommit()))
				_connection.close();
				
				
				// Utils.printSilStackedElements("<<<<<");
				
				
				
				
			} // if (!(_connection.isClosed()))
		} // try
		catch (Exception sqle) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DataConnection::close:", sqle);

			throw new EMFInternalError(EMFErrorSeverity.ERROR, "DataConnection::close: " + sqle.getMessage());
		} // catch (Exception sqle) try
		finally {
			_connection = null;
			_commandCreator = null;
			_sqlMapper = null;
		} // finally try
	} // public void close() throws EMFInternalError

	/**
	 * Crea un SQLCommand di insert per la connessione dato il CommandString.
	 * 
	 * @param <code>String</code>
	 *            commandString - La stringa contenente il comando sql.
	 * @return un oggetto di tipo <code>SQLCommand</code> rappresentante il comando di insert.
	 */
	public SQLCommand createInsertCommand(String commandString) {
		if (_closed) {
			_logger.warn("DataConnection::createInsertCommand: connessione già chiusa");

			return null;
		} // if (_closed)
		return _commandCreator.createInsertCommand(this, commandString);
	} // public SQLCommand createInsertCommand(String commandString)

	/**
	 * Crea un SQLCommand di delete per la connessione dato il CommandString.
	 * 
	 * @param <code>String</code>
	 *            commandString - La stringa contenente il comando sql.
	 * @return un oggetto di tipo <code>SQLCommand</code> rappresentante il comando di delete .
	 */
	public SQLCommand createDeleteCommand(String commandString) {
		if (_closed) {
			_logger.warn("DataConnection::createDeleteCommand: connessione già chiusa");

			return null;
		} // if (_closed)
		return _commandCreator.createDeleteCommand(this, commandString);
	} // public SQLCommand createDeleteCommand(String commandString)

	/**
	 * Crea un SQLCommand per eseguire un stored procedure dato il CommandString.
	 * 
	 * @param <code>String</code>
	 *            commandString - La stringa contenente il comando sql.
	 * @return un oggetto di tipo <code>SQLCommand</code> rappresentante il comando per l'esecuzione di una stored
	 *         procedure.
	 */
	public SQLCommand createStoredProcedureCommand(String commandString) {
		if (_closed) {
			_logger.warn("DataConnection::createStoredProcedureCommand: connessione già chiusa");

			return null;
		} // if (_closed)
		return _commandCreator.createStoredProcedureCommand(this, commandString);
	} // public SQLCommand createDeleteCommand(String commandString)

	/**
	 * Crea un SQLCommand di update per la connessione dato il CommandString.
	 * 
	 * @param <code>String</code>
	 *            commandString - La stringa contenente il comando sql.
	 * @return un oggetto di tipo <code>SQLCommand</code> rappresentante il comando di update.
	 */
	public SQLCommand createUpdateCommand(String commandString) {
		if (_closed) {
			_logger.warn("DataConnection::createUpdateCommand: connessione già chiusa");

			return null;
		} // if (_closed)
		return _commandCreator.createUpdateCommand(this, commandString);
	} // public SQLCommand createUpdateCommand(String commandString)

	/**
	 * Crea un SQLCommand di select per la connessione dato il CommandString.
	 * 
	 * @param <code>String</code>
	 *            commandString - La stringa contenente il comando sql.
	 * @return un oggetto di tipo <code>SQLCommand</code> rappresentante il comando di select.
	 */
	public SQLCommand createSelectCommand(String commandString) {
		return createSelectCommand(commandString, true);
	} // public SQLCommand createSelectCommand(String commandString)

	public SQLCommand createSelectCommand(String commandString, boolean scroll) {
		if (_closed) {
			_logger.warn("DataConnection::createSelectCommand: connessione già chiusa");

			return null;
		} // if (_closed)
		return _commandCreator.createSelectCommand(this, commandString, scroll);
	} // public SQLCommand createSelectCommand(String commandString, boolean
		// scroll)

	/**
	 * Crea un DataField associandolo all' SqlMapper della connessione.
	 * 
	 * @param <code>String</code>
	 *            name - La stringa contenente il comando sql.
	 * @param int
	 *            type - il java.sql.Type del DataField.
	 * @param <code>Object
	 *            value</code> - valore del DataField.
	 * @return un oggetto di tipo <code>DataField</code>
	 */
	public DataField createDataField(String name, int type, Object value) {
		if (_closed) {
			_logger.warn("DataConnection::createDataField: connessione già chiusa");

			return null;
		} // if (_closed)
		if (value instanceof String)
			return new DataField(name, type, (String) value, _sqlMapper);
		else
			return new DataField(name, type, value, _sqlMapper);
	} // public DataField createDataField(String name, int type, Object value)

	/**
	 * Crea un DataField associandolo all' SqlMapper della connessione.
	 * 
	 * @param <code>String</code>
	 *            name - La stringa contenente il comando sql.
	 * @param int
	 *            type - il java.sql.Type del DataField.
	 * @param <code>Object
	 *            value</code> - valore del DataField.
	 * @param <code>String
	 *            typeName</code> - fully-qualified name di un SQL structured type.
	 * @return un oggetto di tipo <code>DataField</code>
	 */
	// Modifica Monica del 27/01/2004 - inizio
	public DataField createDataField(String name, int type, Object value, String typeName) {
		if (_closed) {
			_logger.warn("DataConnection::createDataField: connessione già chiusa");

			return null;
		} // if (_closed)
		if (value instanceof String)
			return new DataField(name, type, (String) value, _sqlMapper, typeName);
		else
			return new DataField(name, type, value, _sqlMapper, typeName);
	} // public DataField createDataField(String name, int type, String value,
		// String typeName)

	/**
	 * Crea un DataField associandolo all'SqlMapper della connessione.
	 * 
	 * @param <code>String</code>
	 *            name - La stringa contenente il comando sql.
	 * @param int
	 *            type - il java.sql.Type del DataField.
	 * @param <code>String
	 *            value</code> - valore del DataField.
	 * @param <code>String
	 *            typeName</code> - fully-qualified name di un SQL structured type.
	 * @return un oggetto di tipo <code>DataField</code>
	 */
	public DataField createDataField(String name, int type, String value, String typeName) {
		if (_closed) {
			_logger.warn("DataConnection::createDataField: connessione già chiusa");

			return null;
		} // if (_closed)
		return new DataField(name, type, value, _sqlMapper, typeName);
	} // public DataField createDataField(String name, int type, String value,
		// String typeName)

	// Modifica Monica del 27/01/2004 - fine
	/**
	 * Crea un DataField associandolo all'SqlMapper della connessione.
	 * 
	 * @param <code>String</code>
	 *            name - La stringa contenente il comando sql.
	 * @param int
	 *            type - il java.sql.Type del DataField.
	 * @param <code>String
	 *            value</code> - valore del DataField.
	 * @return un oggetto di tipo <code>DataField</code>
	 */
	public DataField createDataField(String name, int type, String value) {
		if (_closed) {
			_logger.warn("DataConnection::createDataField: connessione già chiusa");

			return null;
		} // if (_closed)
		return new DataField(name, type, value, _sqlMapper);
	} // public DataField createDataField(String name, int type, Object value)

	/**
	 * Questo metodo permette di recuperare l'oggetto che rappresenta la tipologia di Mapper associata a quella
	 * connessione
	 * 
	 * @return l'oggetto di tipo <code>SQLMapper</code> associato alla connessione
	 */
	public SQLMapper getSQLMapper() {
		if (_closed) {
			_logger.warn("DataConnection::getSQLMapper: connessione già chiusa");

			return null;
		} // if (_closed)
		return _sqlMapper;
	} // public SQLMapper getSQLMapper()

	/**
	 * Metodo per ottenere il livello di isolamento associato al DataConnection.
	 * 
	 * @return int il valore di una delle seguenti costanti :
	 *         <li>TRANSACTION_READ_UNCOMMITTED
	 *         <li>TRANSACTION_READ_COMMITTED
	 *         <li>TRANSACTION_REPEATABLE_READ
	 *         <li>TRANSACTION_SERIALIZABLE
	 */
	public int getTransactionIsolation() throws EMFInternalError {
		if (_closed)
			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"DataConnection::getTransactionIsolation: connessione già chiusa");
		try {
			return _connection.getTransactionIsolation();
		} // try
		catch (SQLException sqle) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DataConnection::getTransactionIsolation:", sqle);

			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"DataConnection::getTransactionIsolation: " + sqle.getMessage());
		} // catch (SQLException sqle) try
	} // public int getTransactionIsolation() throws EMFInternalError

	/**
	 * Metodo per impostare il livello di isolamento associato al DataConnection.
	 * 
	 * @param int
	 *            transactionIsolationLevel il valore di una delle seguenti costanti :
	 *            <li>TRANSACTION_READ_UNCOMMITTED
	 *            <li>TRANSACTION_READ_COMMITTED
	 *            <li>TRANSACTION_REPEATABLE_READ
	 *            <li>TRANSACTION_SERIALIZABLE
	 */
	public void setTransactionIsolation(int transactionIsolationLevel) throws EMFInternalError {
		if (_closed)
			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"DataConnection::setTransactionIsolation: connessione già chiusa");
		try {
			_connection.setTransactionIsolation(transactionIsolationLevel);
		} // try
		catch (SQLException sqle) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DataConnection::setTransactionIsolation:", sqle);

			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"DataConnection::setTransactionIsolation: " + sqle.getMessage());
		} // catch (SQLException sqle)
	} // public void setTransactionIsolation(int transactionIsolationLevel)
		// throws EMFInternalError

	/**
	 * Metodo per ottenere la connessione .
	 * 
	 * @return <code>Connection</code> la connessione attiva.
	 */
	public Connection getInternalConnection() {
		if (_closed) {
			_logger.warn("DataConnection::getInternalConnection: connessione già chiusa");

			return null;
		} // if (_closed)
		return _connection;
	} // public Connection getInternalConnection()

	private CommandCreator getCommandCreator(String driverVersion) {
		float driverVersionFloat = Float.parseFloat(driverVersion);
		if (driverVersionFloat >= 2.0)
			return new DefaultCommandCreator();
		else
			return new LegacyCommandCreator();
	} // private CommandCreator getCommandCreator(String driverVersion)

	private boolean isRequestDistributed() {
		boolean distributedBoolean = false;
		RequestContainer requestContainer = RequestContainer.getRequestContainer();
		if (requestContainer != null)
			distributedBoolean = requestContainer.isRequestDistributed();
		return distributedBoolean;
	} // private boolean isRequestDistributed()

	/*
	 * protected void finalize() { if (!_closed) { try { close(); } // try catch (Exception ex) {
	 * it.eng.sil.util.TraceWrapper.fatal( _logger,"DataConnection::finalize:", ex); } // catch (Exception ex) } // if
	 * (!_closed) } // protected void finalize()
	 */
} // public class DataConnection
