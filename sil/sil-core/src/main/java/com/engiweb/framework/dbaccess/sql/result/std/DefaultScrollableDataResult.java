package com.engiweb.framework.dbaccess.sql.result.std;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFInternalError;

/**
 * Questa classe è l'implementazione dell'interfaccia ScrollableDataResult implementata basandosi sun un result set jdbc
 * 2.0
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class DefaultScrollableDataResult implements ScrollableDataResult {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DefaultScrollableDataResult.class.getName());
	private ResultSetMetaData _rsmd = null;
	private String[] _resultSetColumnsNames = null;
	private List _inputParameters = null;
	private int[] _resultSetColumnsTypes;
	private SQLCommand _sqlCommand = null;
	private ResultSet _rs = null;
	private boolean _closed = false;
	private int _columnCount = -1;
	private int _rowsNumber = 0;

	/**
	 * Costruttore
	 * 
	 * @param <B>SQLCommand
	 *            </B> sqlCommand - l'oggetto sqlCommand che ha generato per la quale l'oggetto è il risultato
	 * @param <B>List
	 *            </B> inputParameters - I Parametri di input del comando che ha generato questo risultato
	 * @param <B>ResultSet
	 *            </B> rs _ Il result set wrapperizzato da questo ScrollableDataResult
	 * @throws <B>EMFInternalError
	 *             </B> - Se si verifica qualche problema nella costruzione dell'oggetto.
	 */
	public DefaultScrollableDataResult(SQLCommand sqlCommand, List inputParameters, ResultSet rs)
			throws EMFInternalError {
		_logger.debug("DefaultScrollableDataResult::DefaultScrollableDataResult: invocato");

		try {
			_rs = rs;
			_inputParameters = inputParameters;
			_sqlCommand = sqlCommand;
			_rsmd = _rs.getMetaData();
			_columnCount = _rsmd.getColumnCount();
			_resultSetColumnsNames = new String[_columnCount];
			_resultSetColumnsTypes = new int[_columnCount];
			String columnName = null;
			int columnType = -1;
			for (int i = 0; i < _columnCount; i++) {
				columnName = _rsmd.getColumnName(i + 1);
				columnType = _rsmd.getColumnType(i + 1);
				_resultSetColumnsNames[i] = columnName;
				_resultSetColumnsTypes[i] = columnType;
			} // for (int i = 0; i < _columnCount; i++)
			setRowsNumber();
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "DefaultScrollableDataResult::DefaultScrollableDataResult: ");
		} // catch (SQLException sqle)
	} // public DefaultScrollableDataResult(SQLCommand sqlCommand, List

	// inputParameters, ResultSet rs)

	/**
	 * Questo metodo viene usato per scorrere l'oggetto in maniera sequenziale in modo simile all'operatore il metodo
	 * ritorna true se l'oggetto ha altre righe in avanti rispetto alla posizione corrente in cui si è posizionati
	 * 
	 * @return true se l'oggetto ha altre righe in avanti rispetto alla posizione corrente in cui si è posizionati false
	 *         altrimenti
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public boolean hasRows() throws EMFInternalError {
		try {
			return _rs.getRow() == 0 ? false : true;
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "DefaultScrollableDataResult::hasRows: ");
		} // catch (SQLException sqle)
	} // public boolean hasRows() throws EMFInternalError

	/**
	 * Questo metodo ritorna la riga corrente su cui il cursore è posizionato
	 * 
	 * @return <B>DataRow</B> - l'oggetto rappresentante la riga del resultset su cui il cursore è posizionato
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public DataRow getDataRow() throws EMFInternalError {
		// Monitor monitor =
		// MonitorFactory.start(
		// "model.data-access.default-scrollable-data-result.get-data-row");
		try {
			DataRow dataRow = new DataRow(_resultSetColumnsNames.length);
			for (int i = 0; i < _columnCount; i++) {
				dataRow.addColumn(i, _sqlCommand.getDataConnection().createDataField(_resultSetColumnsNames[i],
						_resultSetColumnsTypes[i], _rs.getObject(_resultSetColumnsNames[i])));
			} // for (int i = 0; i < _columnCount; i++)
			_rs.next();
			return dataRow;
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "DefaultScrollableDataResult::getDataRow: ");
		} // catch (SQLException sqle)
			// finally {
			// monitor.stop();
			// } // finally
	} // public DataRow getDataRow() throws EMFInternalError

	/**
	 * Questo metodo ritorna la riga della posizione i-esima dell'oggetto scrollableDataResult
	 * 
	 * @param int
	 *            position - il numero della riga che si vuole ottenere
	 * @return <B>DataRow</B> - l'oggetto rappresentante la riga del resultset su cui il cursore è posizionato
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public DataRow getDataRow(int position) throws EMFInternalError {
		moveTo(position);
		return getDataRow();
	} // public DataRow getDataRow(int position) throws EMFInternalError

	public int getColumnCount() {
		return _columnCount;
	} // public int getColumnCount()

	/**
	 * Ritorna un vettore contenente i valori sql.Types delle colonne dell'oggetto ScrollableDataResult
	 * 
	 * @return int[] - il vettore contenente i valori sql.Types delle colonne dell'oggetto ScrollableDataResult
	 */
	public int[] getColumnTypes() {
		return _resultSetColumnsTypes;
	} // public int[] getColumnTypes()

	public String[] getColumnNames() {
		return _resultSetColumnsNames;
	} // public String[] getColumnNames()

	/**
	 * Ritorna il numero di righe dell'oggetto
	 * 
	 * @return int - il numero di righe dell'oggetto
	 */
	public int getRowsNumber() throws EMFInternalError {
		return _rowsNumber;
	} // public int getRowsNumber() throws EMFInternalError

	private void setRowsNumber() throws EMFInternalError {
		try {
			int currentPosition = _rs.getRow();
			_rs.last();
			_rowsNumber = _rs.getRow();
			if (currentPosition == 0)
				currentPosition = 1;
			_rs.absolute(currentPosition);
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "DefaultScrollableDataResult::setRowsNumber: ");
		} // catch (SQLException sqle)
	} // private void setRowsNumber() throws EMFInternalError

	/**
	 * Ritorna sempre DataResultInterface.SCROLLABLE_DATA_RESULT
	 * 
	 * @return <B>String</B> - Ritorna sempre DataResultInterface.SCROLLABLE_DATA_RESULT
	 */
	public String getDataResultType() {
		return DataResultInterface.SCROLLABLE_DATA_RESULT;
	} // public String getDataResultType()

	/**
	 * Questo metodo permette il posizionamento del cursore sulla riga i-esima dell'oggetto ScrollableDataResult
	 * 
	 * @param int
	 *            position - il numero di riga sulla quale ci si vuole posizionare
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public void moveTo(int position) throws EMFInternalError {
		try {
			if (position == 0)
				position = 1;
			_rs.absolute(position);
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "DefaultScrollableDataResult::moveTo: ");
		} // catch (SQLException sqle)
	} // public void moveTo(int position) throws EMFInternalError

	/*
	 * Ritorna un oggetto sorurce bean rappresentante l'oggetto @return la rappresentazione dell'oggetto fi tipo
	 * <B>DataResultInterface</B> come <B>SourceBean</B>
	 */
	public SourceBean getSourceBean() throws EMFInternalError {
		// Monitor monitor =
		// MonitorFactory.start(
		// "model.data-access.default-scrollable-data-result.get-source-bean");
		try {
			int currentPosition = _rs.getRow();
			moveTo(1);
			SourceBean rowsSourceBean = new SourceBean(ScrollableDataResult.ROWS_TAG);
			DataRow dr = null;
			SourceBean dataRowSourceBean = null;
			int rowsNumber = this.getRowsNumber();
			for (int i = 1; i <= rowsNumber; i++) {
				dr = getDataRow(i);
				dataRowSourceBean = dr.getSourceBean();
				rowsSourceBean.setAttribute(dataRowSourceBean);
			} // for (int i = 1; i <= rowsNumber; i++)
			moveTo(currentPosition);
			return rowsSourceBean;
		} // try
		catch (Exception ex) {
			throw Utils.generateInternalError(ex, "DefaultScrollableDataResult::getSourceBean: ");
		} // catch (SourceBeanException sbe)
			// finally {
			// monitor.stop();
			// } // finally
	} // public SourceBean getSourceBean() throws EMFInternalError

	/**
	 * Questo metodo permette di forzare l'aggiornamento dei dati sull'oggetto ScrollableDataResult
	 * 
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public void refresh() throws EMFInternalError {
		_logger.debug("DefaultScrollableDataResult::refresh: invocato");

		// Monitor monitor =
		// MonitorFactory.start(
		// "model.data-access.default-scrollable-data-result.refresh");
		try {
			_rs.close();
			DataResult dr = null;
			DefaultScrollableDataResult sdr = null;
			if ((_inputParameters == null) || (_inputParameters.size() == 0))
				dr = _sqlCommand.execute();
			else
				dr = _sqlCommand.execute(_inputParameters);
			sdr = (DefaultScrollableDataResult) dr.getDataObject();
			_rs = sdr._rs;
			_rsmd = _rs.getMetaData();
			_columnCount = _rsmd.getColumnCount();
			_resultSetColumnsNames = new String[_columnCount];
			_resultSetColumnsTypes = new int[_columnCount];
			String columnName = null;
			int columnType = -1;
			for (int i = 0; i < _columnCount; i++) {
				columnName = _rsmd.getColumnName(i + 1);
				columnType = _rsmd.getColumnType(i + 1);
				_resultSetColumnsNames[i] = columnName;
				_resultSetColumnsTypes[i] = columnType;
			} // for (int i = 0; i < _columnCount; i++)
			setRowsNumber();
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "DefaultScrollableDataResult::refresh: ");
		} // catch (SQLException sqle)
			// finally {
			// monitor.stop();
			// } // finally
	} // public void refresh() throws EMFInternalError

	public ResultSet getResultSet() {
		return _rs;
	} // public ResultSet getResultSet()

	/**
	 * Questo metodo permette di chiudere l'oggetto ScrollableDataResult
	 * 
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public void close() throws EMFInternalError {
		try {
			if ((_rs != null) && (!_closed)) {
				_closed = true;
				_rs.close();
			} // if ((_rs != null) && (!_closed))
			else
				return;
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "DefaultScrollableDataResult::close: ");
		} // catch (SQLException sqle)
	} // public void close() throws EMFInternalError
} // public class DefaultScrollableDataResult implements ScrollableDataResult
