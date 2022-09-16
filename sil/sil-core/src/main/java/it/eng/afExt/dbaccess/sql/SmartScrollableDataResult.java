package it.eng.afExt.dbaccess.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFInternalError;

/**
 * Questa classe è l'implementazione dell'interfaccia ScrollableDataResult implementata basandosi sun un result set jdbc
 * 2.0
 * 
 * @author Franco Vuoto - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class SmartScrollableDataResult {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(SmartScrollableDataResult.class.getName());
	private DataConnection _dataConnection;
	private ResultSetMetaData _rsmd = null;
	private String[] _resultSetColumnsNames = null;
	// private List _inputParameters = null;
	private int[] _resultSetColumnsTypes;
	// private SQLCommand _sqlCommand = null;
	private ResultSet _rs = null;
	private boolean _closed = false;
	private int _columnCount = -1;
	private int _rowsNumber = -1;
	private int _totalPages = -1;

	private boolean recordCount = false;

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
	public SmartScrollableDataResult(DataConnection dataConnection, ResultSet rs, String statement,
			boolean enableRecordcount) throws EMFInternalError {
		_logger.debug("DefaultScrollableDataResult::SmartScrollableDataResult: invocato");

		try {
			_rs = rs;
			_dataConnection = dataConnection;

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
			}

			if (recordCount)
				calculateRowsNumber(statement);
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "SmartScrollableDataResult::SmartScrollableDataResult: ");
		}
	}

	public SmartScrollableDataResult(DataConnection dataConnection, ResultSet rs, boolean enableRecordcount)
			throws EMFInternalError {
		_logger.debug("DefaultScrollableDataResult::SmartScrollableDataResult: invocato");

		try {
			_rs = rs;
			_dataConnection = dataConnection;

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
			}

			if (recordCount)
				calculateRowsNumber(null);
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "SmartScrollableDataResult::SmartScrollableDataResult: ");
		}
	}

	/**
	 * Questo metodo ritorna la riga corrente su cui il cursore è posizionato
	 * 
	 * @return <B>DataRow</B> - l'oggetto rappresentante la riga del resultset su cui il cursore è posizionato
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public DataRow getDataRow() throws EMFInternalError {
		// Monitor monitor =
		// MonitorFactory.start("model.data-access.default-scrollable-data-result.get-data-row");
		try {
			DataRow dataRow = new DataRow(_resultSetColumnsNames.length);
			for (int i = 0; i < _columnCount; i++) {
				dataRow.addColumn(i, _dataConnection.createDataField(_resultSetColumnsNames[i],
						_resultSetColumnsTypes[i], _rs.getObject(_resultSetColumnsNames[i])));
			} // for (int i = 0; i < _columnCount; i++)
			_rs.next();
			return dataRow;
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "SmartScrollableDataResult::getDataRow: ");
		} // catch (SQLException sqle)
		finally {
			// monitor.stop();
		} // finally
	}

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
	private void calculateRowsNumber(String statement) throws EMFInternalError {

		if (statement != null) {

			try {
				String stmtCountSQL = "SELECT COUNT(*) NUM_REC FROM ( " + statement + ")";

				Statement stmtCount = _dataConnection.getInternalConnection()
						.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

				ResultSet rsCount = stmtCount.executeQuery(stmtCountSQL);

				if (rsCount.next()) {
					_rowsNumber = rsCount.getInt("NUM_REC");
				}
				rsCount.close();
			} catch (SQLException e) {
				it.eng.sil.util.TraceWrapper.error(_logger,
						this.getClass().getName() + "::calculateRowsNumber: " + e.getMessage(), e);

			}
			return;

		}

		// Se non si riesce a ricavare la
		// SELECT COUNT(*)
		// non c'e' altra strada che scorrere tutto il recordset per
		// sapere il numero di record

		try {
			int currentPosition = _rs.getRow();
			_rs.last();
			int lastRow = _rs.getRow();
			if (currentPosition == 0)
				currentPosition = 1;
			_rs.absolute(currentPosition);
			_rowsNumber = lastRow;
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "SmartScrollableDataResult::getRowsNumber: ");
		} // catch (SQLException sqle)
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
		try {
			if (position == 0)
				position = 1;
			_rs.absolute(position);
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "SmartScrollableDataResult::moveTo: ");
		} // catch (SQLException sqle)
	} // public void moveTo(int position) throws EMFInternalError

	/*
	 * Ritorna un oggetto sorurce bean rappresentante l'oggetto @return la rappresentazione dell'oggetto fi tipo
	 * <B>DataResultInterface</B> come <B>SourceBean</B>
	 */
	public SourceBean getSourceBean(int pageNumber, int pagedRows) throws EMFInternalError {
		// Monitor monitor =
		// MonitorFactory.start("model.data-access.default-scrollable-data-result.get-source-bean");

		if (pageNumber == -1 && !recordCount) {
			// richiesta l'ultima pagina
			// il calcolo e' comunque necesssario
			calculateRowsNumber(null);
		}

		if (recordCount || pageNumber == -1) {
			// conteggio dei record attivato
			// vecchia procedura

			_totalPages = (_rowsNumber / pagedRows);

			if ((_rowsNumber % pagedRows) != 0) {
				_totalPages++;
			}

			// FV 20-01-2005
			// Se si richiede una pagina oltre il toatale di pagine,
			// viene inviata l'ultima pagina

			if ((pageNumber > _totalPages) && (_totalPages != 0)) {
				pageNumber = _totalPages;
			}

			try {
				int firstRow, lastRow;

				if (pageNumber == -1) {
					pageNumber = _totalPages;
				}
				firstRow = (pageNumber - 1) * pagedRows + 1;
				lastRow = firstRow - 1 + pagedRows;

				if (lastRow > _rowsNumber)
					lastRow = _rowsNumber;

				SourceBean rowsSourceBean = new SourceBean(ScrollableDataResult.ROWS_TAG);

				rowsSourceBean.setAttribute("NUM_RECORDS", new Integer(_rowsNumber));

				rowsSourceBean.setAttribute("NUM_PAGES", new Integer(_totalPages));

				rowsSourceBean.setAttribute("CURRENT_PAGE", new Integer(pageNumber));
				rowsSourceBean.setAttribute("ROWS_X_PAGE", new Integer(pagedRows));

				DataRow dr = null;
				SourceBean dataRowSourceBean = null;

				if (_totalPages == 0) {
					rowsSourceBean.setAttribute("ROWS_X_PAGE_PARTIAL", new Integer(_totalPages));
				} else {
					moveTo(firstRow);
					for (int i = firstRow; i <= lastRow; i++) {
						dr = getDataRow();
						dataRowSourceBean = dr.getSourceBean();
						rowsSourceBean.setAttribute(dataRowSourceBean);
					}
				}

				return rowsSourceBean;
			} catch (SourceBeanException sbe) {
				throw Utils.generateInternalError(sbe, "SmartScrollableDataResult::getSourceBean: ");
			}
		} else {
			// conteggio dei record disattivato
			// NUOVA PROCEDURA

			if (pageNumber == -1) {
				// richiesta l'ultima pagina
				calculateRowsNumber(null);
			}

			SourceBean rowsSourceBean = null;
			int firstRow = (pageNumber - 1) * pagedRows + 1;
			try {

				if (firstRow != 1)
					_rs.absolute(firstRow - 1);
				// else
				// _rs.beforeFirst();

				rowsSourceBean = new SourceBean(ScrollableDataResult.ROWS_TAG);

				DataRow dr = null;
				SourceBean dataRowSourceBean = null;

				int numCampi = _resultSetColumnsNames.length;
				int contRighe = 0;

				while ((contRighe < pagedRows) && _rs.next()) {
					contRighe++;

					DataRow dataRow = new DataRow(numCampi);
					try {
						for (int i = 0; i < _columnCount; i++) {
							dataRow.addColumn(i, _dataConnection.createDataField(_resultSetColumnsNames[i],
									_resultSetColumnsTypes[i], _rs.getObject(_resultSetColumnsNames[i])));
						}
					} catch (SQLException sqle) {
						throw Utils.generateInternalError(sqle, "SmartScrollableDataResult::getDataRow: ");
					}

					dataRowSourceBean = dataRow.getSourceBean();
					rowsSourceBean.setAttribute(dataRowSourceBean);
				}

				rowsSourceBean.setAttribute("NUM_RECORDS", new Integer(firstRow + contRighe - 1));

				if (!_rs.next()) {
					_totalPages = pageNumber;
				}

				rowsSourceBean.setAttribute("NUM_PAGES", new Integer(_totalPages));
				rowsSourceBean.setAttribute("CURRENT_PAGE", new Integer(pageNumber));
				rowsSourceBean.setAttribute("ROWS_X_PAGE", new Integer(pagedRows));
				if (contRighe == 0) {
					rowsSourceBean.setAttribute("ROWS_X_PAGE_PARTIAL", new Integer(contRighe));
				}

			} catch (SourceBeanException e) {
				throw Utils.generateInternalError(e, "SmartScrollableDataResult::getSourceBean: ");

			} catch (SQLException e) {
				throw Utils.generateInternalError(e, "SmartScrollableDataResult::getSourceBean: ");
			}

			return rowsSourceBean;

		}
	}

	public ResultSet getResultSet() {
		return _rs;
	}

	/**
	 * Questo metodo permette di chiudere l'oggetto ScrollableDataResult
	 * 
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public void close() throws EMFInternalError {
		try {
			if ((_rs != null) && (!_closed)) {
				_rs.close();
				_closed = true;
			} // if ((_rs != null) && (!_closed))
				// chiudo la dataconnection
			else
				return;
			if (_dataConnection != null) {
				_dataConnection.close();
			} else
				return;
		} // try
		catch (SQLException sqle) {
			throw Utils.generateInternalError(sqle, "SmartScrollableDataResult::close: ");
		} // catch (SQLException sqle)
	} // public void close() throws EMFInternalError

	/**
	 * @return
	 */
	public int getRowsNumber() {
		return _rowsNumber;
	}

	public int getTotalPages() {
		return _totalPages;
	}

	/**
	 * @return
	 */
	public boolean isRecordCountEnabled() {
		return recordCount;
	}

}