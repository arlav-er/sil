package com.engiweb.framework.dbaccess.sql.result.legacy;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * Questa classe è l'implementazione dell'interfaccia ScrollableDataResult implementata basandosi sun un result set jdbc
 * 2.0
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class LegacyScrollableDataResult implements ScrollableDataResult {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(LegacyScrollableDataResult.class.getName());
	private ResultSetMetaData _rsmd = null;
	private String[] _resultSetColumnsNames = null;
	private List _inputParameters = null;
	private int[] _resultSetColumnsTypes;
	private SQLCommand _sqlCommand = null;
	private ResultSet _rs = null;
	private boolean _closed = false;
	private int _columnCount = -1;
	private boolean _dataRetrieved = false;
	private ArrayList _dataRows = null;
	private int _currentRow = 0;

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
	public LegacyScrollableDataResult(SQLCommand sqlCommand, List inputParameters, ResultSet rs)
			throws EMFInternalError {
		_logger.debug("LegacyScrollableDataResult::LegacyScrollableDataResult: invocato");

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
			_dataRetrieved = false;
			_dataRows = null;
			_currentRow = 0;
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "LegacyScrollableDataResult::LegacyScrollableDataResult: ");
		} // catch (SQLException sqle)
	} // public LegacyScrollableDataResult(SQLCommand sqlCommand, List

	// inputParameters, ResultSet rs)

	private void retrieveData() throws EMFInternalError {
		if (_dataRetrieved)
			return;
		try {
			_dataRows = new ArrayList();
			while (_rs.next()) {
				DataRow dataRow = new DataRow(_resultSetColumnsNames.length);
				for (int i = 0; i < _columnCount; i++) {
					dataRow.addColumn(i, _sqlCommand.getDataConnection().createDataField(_resultSetColumnsNames[i],
							_resultSetColumnsTypes[i], _rs.getObject(_resultSetColumnsNames[i])));
				} // for (int i = 0; i < _columnCount; i++)
				_dataRows.add(dataRow);
			} // while (_rs.next())
			_currentRow = (_dataRows.size() == 0) ? 0 : 1;
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "LegacyScrollableDataResult::LegacyScrollableDataResult: ");
		} // catch (SQLException sqle)
		_dataRetrieved = true;
	} // private void retrieveData() throws EMFInternalError

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
		retrieveData();
		return ((_currentRow >= 1) && (_currentRow <= _dataRows.size()));
	} // public boolean hasRows() throws EMFInternalError

	/**
	 * Questo metodo ritorna la riga corrente su cui il cursore è posizionato
	 * 
	 * @return <B>DataRow</B> - l'oggetto rappresentante la riga del resultset su cui il cursore è posizionato
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public DataRow getDataRow() throws EMFInternalError {
		retrieveData();
		if (_currentRow == 0)
			return null;
		DataRow dataRow = (DataRow) _dataRows.get(_currentRow - 1);
		if (_currentRow == _dataRows.size())
			_currentRow = 0;
		else
			_currentRow++;
		return dataRow;
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
		retrieveData();
		return _dataRows.size();
	} // public int getRowsNumber() throws EMFInternalError

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
		retrieveData();
		if ((position >= 1) && (position <= _dataRows.size()))
			return;
		_currentRow = position;
	} // public void moveTo(int position) throws EMFInternalError

	/*
	 * Ritorna un oggetto sorurce bean rappresentante l'oggetto @return la rappresentazione dell'oggetto fi tipo
	 * <B>DataResultInterface</B> come <B>SourceBean</B>
	 */
	public SourceBean getSourceBean() throws EMFInternalError {
		retrieveData();
		try {
			SourceBean rowsSourceBean = new SourceBean(ScrollableDataResult.ROWS_TAG);
			for (int i = 0; i < _dataRows.size(); i++) {
				SourceBean dataRowSourceBean = ((DataRow) _dataRows.get(i)).getSourceBean();
				rowsSourceBean.setAttribute(dataRowSourceBean);
			} // for (int i = 0; i < _dataRows.size(); i++)
			return rowsSourceBean;
		} // try
		catch (SourceBeanException sbe) {
			throw Utils.generateInternalError(sbe, "LegacyScrollableDataResult::getSourceBean: ");
		} // catch (SourceBeanException sbe)
	} // public SourceBean getSourceBean() throws EMFInternalError

	/**
	 * Questo metodo permette di forzare l'aggiornamento dei dati sull'oggetto ScrollableDataResult
	 * 
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public void refresh() throws EMFInternalError {
		_logger.debug("LegacyScrollableDataResult::refresh: invocato");

		Monitor monitor = MonitorFactory.start("model.data-access.legacy-scrollable-data-result.refresh");
		try {
			_rs.close();
			DataResult dr = null;
			if ((_inputParameters == null) || (_inputParameters.size() == 0))
				dr = _sqlCommand.execute();
			else
				dr = _sqlCommand.execute(_inputParameters);
			LegacyScrollableDataResult sdr = (LegacyScrollableDataResult) dr.getDataObject();
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
			_dataRetrieved = false;
			_dataRows = null;
			_currentRow = 0;
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "LegacyScrollableDataResult::refresh: ");
		} // catch (SQLException sqle)
		finally {
			monitor.stop();
		} // finally
	} // public void refresh() throws EMFInternalError

	public ResultSet getResultSet() {
		if (_dataRetrieved) {
			try {
				refresh();
			} // try
			catch (EMFInternalError emfie) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "LegacyScrollableDataResult::getResultSet: ",
						(Exception) emfie);

			} // catch (EMFInternalError e)
		} // if (_dataRetrieved)
		return _rs;
	} // public ResultSet getResultSet()

	/**
	 * Questo metodo permette di chiudere l'oggetto ScrollableDataResult
	 * 
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public void close() throws EMFInternalError {
		if (_closed)
			return;
		_closed = true;
		_inputParameters = null;
		_sqlCommand = null;
		_rsmd = null;
		_columnCount = 0;
		_resultSetColumnsNames = null;
		_resultSetColumnsTypes = null;
		_dataRetrieved = false;
		_dataRows = null;
		_currentRow = 0;
		try {
			if (_rs != null)
				_rs.close();
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "LegacyScrollableDataResult::close: ");
		} // catch (SQLException sqle)
		finally {
			_rs = null;
		} // finally
	} // public void close() throws EMFInternalError
} // public class LegacyScrollableDataResult implements ScrollableDataResult
