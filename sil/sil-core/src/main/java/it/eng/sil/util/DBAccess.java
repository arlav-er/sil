package it.eng.sil.util;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

/**
 * Questa classe mette a disposizione alcuni servizi per l'accesso al database. I metodi che mette a disposizione son
 * quelli per effettuare query di selezione,inserimento, aggiornameto e cancellazione di records del database. La
 * definizione degli statements da eseguire <b>deve</b> essere effettuata in un file di configurazione xml.
 * 
 * @author Marco Conti; marco.conti@engiweb.com. Engiweb.com - Torino.
 * @version 1.0
 */
public class DBAccess {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DBAccess.class.getName());
	final private static String MODULO = "DBAccess";

	/**
	 * Costruttore.
	 */
	public DBAccess() {
	}

	/**
	 * Metodo per ottenere una connessione dal pool identificato dal nome passato come parametro. Restituisce un'oggetto
	 * com.engiweb.framework.dbaccess.sql.DataConnection.
	 * 
	 * @param connectionPoolName
	 *            nome del pool di connessione dal quale si vuole ottenere una connessione.
	 * @return com.engiweb.framework.dbaccess.sql.DataConnection la connessione al database.
	 * @throws com.engiweb.framework.error.EMFInternalError
	 *             in caso di errore.
	 */
	public DataConnection getConnection(String connectionPoolName) throws EMFInternalError {
		try {
			DataConnectionManager dcm = DataConnectionManager.getInstance();

			return dcm.getConnection(connectionPoolName);
		} catch (EMFInternalError emfie) {
			throw emfie;
		}
	}

	// public DataConnection getConnection(String connectionPoolName) throws
	// EMFInternalError{

	/**
	 * Metodo per ottenere una connessione dal pool connessioni. Di default viene utilizzata il primo nome registrato
	 * nel file xml di configurazione delle connessioni data_access.xml. Restituisce un'oggetto
	 * com.engiweb.framework.dbaccess.sql.DataConnection.
	 * 
	 * @return com.engiweb.framework.dbaccess.sql.DataConnection la connessione al database.
	 * @throws com.engiweb.framework.error.EMFInternalError
	 *             in caso di errore
	 */
	public DataConnection getConnection() throws EMFInternalError {
		DataConnection conn = null;

		try {
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection();
		} catch (EMFInternalError emfie) {
			throw emfie;
		}

		return conn;
	}

	// public DataConnection getConnection() throws EMFInternalError

	/**
	 * Metodo per effettuare una query di selezione. Il primo parametro è un array di <code>Object</code> contenente i
	 * valori delle condizioni di where, se vale null equivale a fieldWhere[0]. Il secondo è il nome con il quale è
	 * referenziata una query nel file di configurazione. Restituisce un array di oggetti
	 * com.engiweb.framework.dbaccess.sql.DataRow
	 * 
	 * @param fieldWhere
	 *            valore dei campi della condizione di where in ordine come definito stmtQuery.
	 * @param stmtQuery
	 *            query definita nel file xml di configurazione.
	 * @return com.engiweb.framework.dbaccess.sql.DataRow array di oggetti DataRow rappresentante le righe del result
	 *         set restituite dalla query.
	 * @throws com.engiweb.wfmanager.errors.EMFInternalError
	 *             in caso di errore nell'esecuzione della query di selezione.
	 */
	public DataRow[] selectQuery(Object[] fieldWhere, String stmtQuery) throws EMFInternalError {
		String thismethod = "::selectQuery(Object[], String) ";
		DataConnection dc = null;
		SQLCommand selectCommand = null;
		DataRow[] datarows = null;

		try {
			_logger.debug(MODULO + thismethod + "CALLED...");

			dc = getConnection();
			selectCommand = dc.createSelectCommand(SQLStatements.getStatement(stmtQuery));

			ArrayList inputParameters = null;

			if (fieldWhere == null) {
				inputParameters = new ArrayList(0);
			} else {
				inputParameters = new ArrayList(fieldWhere.length);

				for (int i = 0; i < fieldWhere.length; i++) {
					setQueryParameter("", inputParameters, fieldWhere[i], dc);
				}
			}

			DataResult dr = selectCommand.execute(inputParameters);

			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
				int nroRows = sdr.getRowsNumber();
				datarows = new DataRow[nroRows];

				for (int i = 0; i < nroRows; i++) {
					datarows[i] = sdr.getDataRow(i + 1);
				}

				sdr.close();
			} else {
				_logger.debug(MODULO + thismethod + "Il result set non è di tipo "
						+ DataResultInterface.SCROLLABLE_DATA_RESULT);

				throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "Il result set della query '" + stmtQuery
						+ "' non è di tipo " + DataResultInterface.SCROLLABLE_DATA_RESULT);
			}
		} catch (EMFInternalError e) {
			_logger.debug(e.getMessage());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (selectCommand != null) {
				try {
					selectCommand.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + "Errore in selectCommand.close() :" + e.toString());

					throw e;
				}
			}

			if (dc != null) {
				try {
					dc.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + "Errore in chiusura connessione dc.close() :" + e.toString());

					throw e;
				}
			}
		}

		return datarows;
	}

	// public DataRow[] selectQuery(Object fieldWhere[], String stmtQuery)

	/**
	 * Metodo per effettuare una query di selezione. Il primo parametro è un array di <code>Object</code> contenente i
	 * valori delle condizioni di where, se vale null equivale a fieldWhere[0]. Il secondo è il nome con il quale è
	 * referenziata una query nel file di configurazione xml.Il terzo è la connessione al database. Restituisce un array
	 * di oggetti com.engiweb.framework.dbaccess.sql.DataRow <BR>
	 * <B>ATTENZIONE : </B>questo metodo non chiude la connessione al database (DataConnection dc).
	 * 
	 * @param fieldWhere
	 *            valore dei campi della condizione di where in ordine come definito stmtQuery.
	 * @param stmtQuery
	 *            query definita nel file xml di configurazione.
	 * @param dc
	 *            è la connessione al database
	 * @return com.engiweb.framework.dbaccess.sql.DataRow array di oggetti DataRow rappresentante le righe del result
	 *         set restituite dalla query.
	 * @throws com.engiweb.wfmanager.errors.EMFInternalError
	 *             in caso di errore nell'esecuzione della query di selezione.
	 */
	public DataRow[] selectQuery(Object[] fieldWhere, String stmtQuery, DataConnection dc) throws EMFInternalError {
		String thismethod = "::selectQuery(Object[], String, DataConnection) ";
		SQLCommand selectCommand = null;
		DataRow[] datarows = null;
		_logger.debug(MODULO + thismethod + "CALLED...");

		if (dc == null) {
			_logger.debug(MODULO + thismethod + "La connessione al database DataConnection è nulla.");

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "La connessione al database DataConnection è nulla.");
		}

		try {
			selectCommand = dc.createSelectCommand(SQLStatements.getStatement(stmtQuery));

			ArrayList inputParameters = null;

			if (fieldWhere == null) {
				inputParameters = new ArrayList(0);
			} else {
				inputParameters = new ArrayList(fieldWhere.length);

				for (int i = 0; i < fieldWhere.length; i++) {
					setQueryParameter("", inputParameters, fieldWhere[i], dc);
				}
			}

			DataResult dr = selectCommand.execute(inputParameters);

			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
				int nroRows = sdr.getRowsNumber();
				datarows = new DataRow[nroRows];

				for (int i = 0; i < nroRows; i++) {
					datarows[i] = sdr.getDataRow(i + 1);
				}

				sdr.close();
			} else {
				_logger.debug(MODULO + thismethod + "Il result set non è di tipo "
						+ DataResultInterface.SCROLLABLE_DATA_RESULT);

				throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "Il result set della query '" + stmtQuery
						+ "' non è di tipo " + DataResultInterface.SCROLLABLE_DATA_RESULT);
			}
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (selectCommand != null) {
				try {
					selectCommand.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + "Errore in selectCommand.close() : " + e.toString());

					throw e;
				}
			}
		}

		return datarows;
	}

	// public DataRow[] selectQuery(Object fieldWhere[], String stmtQuery)

	/**
	 * Metodo per effettuare una query di selezione. La query da eseguire viene passata come argomento del metodo.
	 * Restituisce un array di oggetti com.engiweb.framework.dbaccess.sql.DataRow
	 * 
	 * @param stmt
	 *            è la query da esequire
	 * @return com.engiweb.framework.dbaccess.sql.DataRow array di oggetti DataRow rappresentante le righe del result
	 *         set restituite dalla query.
	 * @throws com.engiweb.wfmanager.errors.EMFInternalError
	 *             in caso di errore nell'esecuzione della query di selezione.
	 */
	public DataRow[] selectQuery(String stmt) throws EMFInternalError {
		String thismethod = "::selectQuery(String) ";
		DataConnection dc = null;
		SQLCommand selectCommand = null;
		DataRow[] datarows = null;

		try {
			_logger.debug(MODULO + thismethod + "CALLED...");

			dc = getConnection();
			selectCommand = dc.createSelectCommand(stmt);

			DataResult dr = selectCommand.execute();

			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
				int nroRows = sdr.getRowsNumber();
				datarows = new DataRow[nroRows];

				for (int i = 0; i < nroRows; i++) {
					datarows[i] = sdr.getDataRow(i + 1);
				}

				sdr.close();
			} else {
				_logger.debug(MODULO + thismethod + "Il result set non è di tipo "
						+ DataResultInterface.SCROLLABLE_DATA_RESULT);

				throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "Il result set della query '" + stmt
						+ "' non è di tipo " + DataResultInterface.SCROLLABLE_DATA_RESULT);
			}
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (selectCommand != null) {
				try {
					selectCommand.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + "Errore in chiusura selectCommand:" + e.toString());

					throw e;
				}
			}

			if (dc != null) {
				try {
					dc.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + "Errore in chiusura connessione: " + e.toString());

					throw e;
				}
			}
		}

		return datarows;
	}

	// public DataRow[] selectQuery(String stmt)

	/**
	 * Metodo per effettuare una query di selezione. La query da eseguire viene passata come argomento del metodo.
	 * Restituisce un array di oggetti com.engiweb.framework.dbaccess.sql.DataRow
	 * 
	 * @param stmt
	 *            è la query da eseguire.
	 * @param dc
	 *            è la connnessione al database.
	 * @return com.engiweb.framework.dbaccess.sql.DataRow array di oggetti DataRow rappresentante le righe del result
	 *         set restituite dalla query.
	 * @throws com.engiweb.wfmanager.errors.EMFInternalError
	 *             in caso di errore nell'esecuzione della query di selezione.
	 */
	public DataRow[] selectQuery(String stmt, DataConnection dc) throws EMFInternalError {
		String thismethod = "::selectQuery(String,DataConnection) ";
		SQLCommand selectCommand = null;
		DataRow[] datarows = null;

		if (dc == null) {
			_logger.debug(MODULO + thismethod + "La connessione al database DataConnection è nulla.");

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "La connessione al database DataConnection è nulla.");
		}

		try {
			_logger.debug(MODULO + thismethod + "CALLED...");

			selectCommand = dc.createSelectCommand(stmt);

			DataResult dr = selectCommand.execute();

			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
				int nroRows = sdr.getRowsNumber();
				datarows = new DataRow[nroRows];

				for (int i = 0; i < nroRows; i++) {
					datarows[i] = sdr.getDataRow(i + 1);
				}

				sdr.close();
			} else {
				_logger.debug(MODULO + thismethod + "Il result set non è di tipo "
						+ DataResultInterface.SCROLLABLE_DATA_RESULT);

				throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "Il result set della query '" + stmt
						+ "' non è di tipo " + DataResultInterface.SCROLLABLE_DATA_RESULT);
			}
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (selectCommand != null) {
				try {
					selectCommand.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + "Errore in selectCommand.close() :" + e.toString());

					throw e;
				}
			}
		}

		return datarows;
	}

	// public DataRow[] selectQuery(String stmt, DataConnection dc)

	/**
	 * Metodo per effettuare query di selezione (SELECT ...) il primo parametro è un array di stringhe contenente i
	 * valori delle condizioni di where, il secondo è il nome con il quale è referenziata una query nel file di
	 * configurazione sqls.xml.Restituisce un SourceBean tipo es.:
	 * <p>
	 * <em> <blockquote>
	 * 
	 * <pre>
	 *   &lt;ROWS&gt;
	 *       &lt;ROW
	 *           DATO_1=&quot;VALORE_1&quot;
	 *           DATO_2=&quot;VALORE_2&quot;
	 *           DATO_3=&quot;VALORE_3&quot;&gt;
	 *       &lt;/ROW&gt;
	 *   &lt;/ROWS&gt;
	 * </pre>
	 * 
	 * </blockquote> </em>
	 * 
	 * @param fieldWhere
	 *            = valore dei campi della condizione di where in ordine come definito stmtQuery.
	 * @param stmtQuery
	 *            query definita nel file di configurazione.
	 * @return com.engiweb.framework.base.SourceBean.
	 * @throws EMFInternalError
	 *             in caso di errore nell'esecuzione della query di selezione.
	 */
	public SourceBean selectToSourceBean(Object[] fieldWhere, String stmtQuery) throws EMFInternalError {
		DataConnection dc = null;
		SQLCommand selectCommand = null;
		SourceBean resultSourceBean = null;
		String thismethod = "::selectToSourceBean(Object[],String)";
		_logger.debug(MODULO + thismethod + " CALLED...");

		try {
			dc = getConnection();

			selectCommand = dc.createSelectCommand(SQLStatements.getStatement(stmtQuery));

			ArrayList inputParameters = null;

			if (fieldWhere == null) {
				inputParameters = new ArrayList(0);
			} else {
				inputParameters = new ArrayList(fieldWhere.length);

				for (int i = 0; i < fieldWhere.length; i++) {
					setQueryParameter("", inputParameters, fieldWhere[i], dc);
				}
			}

			DataResult dr = selectCommand.execute(inputParameters);

			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
				resultSourceBean = sdr.getSourceBean();
				sdr.close();
			} else {
				_logger.debug(MODULO + thismethod + "Il result set non è di tipo "
						+ DataResultInterface.SCROLLABLE_DATA_RESULT);

				throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "Il result set della query '" + stmtQuery
						+ "' non è di tipo " + DataResultInterface.SCROLLABLE_DATA_RESULT);
			}
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (selectCommand != null) {
				try {
					selectCommand.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + "Errorre in chiusura selectCommand : " + e.toString());

					_logger.debug(e.toString());

				}
			}

			if (dc != null) {
				try {
					dc.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + " Eng Internal Error : " + e.toString());

				}
			}
		}

		return resultSourceBean;
	}

	/**
	 * Metodo per effettuare una SELECT con un set di parametri e con una DataConnection
	 * 
	 * @param fieldWhere
	 * @param stmtQuery
	 * @param dc
	 * @return
	 * @throws EMFInternalError
	 */
	public SourceBean selectToSourceBean(String stmtQuery, Object[] fieldWhere, DataConnection dc)
			throws EMFInternalError {
		SQLCommand selectCommand = null;
		SourceBean resultSourceBean = null;
		String thismethod = "::selectToSourceBean(Object[],String)";
		_logger.debug(MODULO + thismethod + " CALLED...");

		try {
			selectCommand = dc.createSelectCommand(SQLStatements.getStatementInit(stmtQuery));

			ArrayList inputParameters = null;

			if (fieldWhere == null) {
				inputParameters = new ArrayList(0);
			} else {
				inputParameters = new ArrayList(fieldWhere.length);

				for (int i = 0; i < fieldWhere.length; i++) {
					setQueryParameter("", inputParameters, fieldWhere[i], dc);
				}
			}

			DataResult dr = selectCommand.execute(inputParameters);

			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
				resultSourceBean = sdr.getSourceBean();
				sdr.close();
			} else {
				_logger.debug(MODULO + thismethod + "Il result set non è di tipo "
						+ DataResultInterface.SCROLLABLE_DATA_RESULT);

				throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "Il result set della query '" + stmtQuery
						+ "' non è di tipo " + DataResultInterface.SCROLLABLE_DATA_RESULT);
			}
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (selectCommand != null) {
				try {
					selectCommand.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + "Errorre in chiusura selectCommand : " + e.toString());

					_logger.debug(e.toString());

				}
			}
		}

		return resultSourceBean;
	}

	/**
	 * Metodo per effettuare query di selezione (SELECT ...).L'argomento è la query di selezione da eseguire Restituisce
	 * un SourceBean tipo es.:
	 * <p>
	 * <em> <blockquote>
	 * 
	 * <pre>
	 *   &lt;ROWS&gt;
	 *       &lt;ROW
	 *           DATO_1=&quot;VALORE_1&quot;
	 *           DATO_2=&quot;VALORE_2&quot;
	 *           DATO_3=&quot;VALORE_3&quot;&gt;
	 *       &lt;/ROW&gt;
	 *   &lt;/ROWS&gt;
	 * </pre>
	 * 
	 * </blockquote> </em>
	 * 
	 * @param query
	 *            è la query da eseguire.
	 * @return com.engiweb.framework.base.SourceBean.
	 * @throws EMFInternalError
	 *             in caso di errore nell'esecuzione della query di selezione.
	 */
	public SourceBean selectToSourceBean(String query) throws EMFInternalError {
		DataConnection dc = null;
		SQLCommand selectCommand = null;
		SourceBean resultSourceBean = null;
		String thismethod = "::selectToSourceBean(String)";

		try {
			_logger.debug(MODULO + thismethod + "CALLED...");

			dc = getConnection();
			selectCommand = dc.createSelectCommand(query);

			DataResult dr = selectCommand.execute();

			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
				resultSourceBean = sdr.getSourceBean();
				sdr.close();
			} else {
				_logger.debug(MODULO + thismethod + "Il result set non è di tipo "
						+ DataResultInterface.SCROLLABLE_DATA_RESULT);

				throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "Il result set della query '" + query
						+ "' non è di tipo " + DataResultInterface.SCROLLABLE_DATA_RESULT);
			}
		} catch (EMFInternalError e) {
			_logger.debug(MODULO + thismethod + "ERRORE : " + e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (selectCommand != null) {
				try {
					selectCommand.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + "Errore in chiusura selectCommand : " + e.toString());

				}
			}

			if (dc != null) {
				try {
					dc.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + "Errore in chiusura connession : " + e.toString());

				}
			}
		}

		return resultSourceBean;
	}

	/**
	 * Metodo per effettuare query di selezione (SELECT ...).Il primo argomento è la query di selezione da eseguire,il
	 * secondo è la connessione al database.
	 * 
	 * <b>ATTENZIONE :</b> questo metodo non chiude la connessione DataConnection.
	 * 
	 * Restituisce un SourceBean tipo es.:
	 * <p>
	 * <em> <blockquote>
	 * 
	 * <pre>
	 *   &lt;ROWS&gt;
	 *       &lt;ROW
	 *           DATO_1=&quot;VALORE_1&quot;
	 *           DATO_2=&quot;VALORE_2&quot;
	 *           DATO_3=&quot;VALORE_3&quot;&gt;
	 *       &lt;/ROW&gt;
	 *   &lt;/ROWS&gt;
	 * </pre>
	 * 
	 * </blockquote> </em>
	 * 
	 * @param query
	 *            è la query da eseguire.
	 * @param DataConnection
	 *            è la connessione al database.
	 * @return com.engiweb.framework.base.SourceBean.
	 * @throws EMFInternalError
	 *             in caso di errore nell'esecuzione della query di selezione.
	 */
	public SourceBean selectToSourceBean(String query, DataConnection dc) throws EMFInternalError {
		SQLCommand selectCommand = null;
		SourceBean resultSourceBean = null;
		String thismethod = "::selectToSourceBean(String,DataConnection)";

		if (dc == null) {
			_logger.debug(MODULO + thismethod + "La connessione al database DataConnection è nulla.");

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "La connessione al database DataConnection è nulla.");
		}

		try {
			_logger.debug(MODULO + thismethod + "CALLED...");

			selectCommand = dc.createSelectCommand(query);

			DataResult dr = selectCommand.execute();

			if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
				ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
				resultSourceBean = sdr.getSourceBean();
				sdr.close();
			} else {
				_logger.debug(MODULO + thismethod + "Il result set non è di tipo "
						+ DataResultInterface.SCROLLABLE_DATA_RESULT);

				throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "Il result set della query '" + query
						+ "' non è di tipo " + DataResultInterface.SCROLLABLE_DATA_RESULT);
			}
		} catch (EMFInternalError e) {
			_logger.debug(MODULO + thismethod + "ERRORE : " + e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (selectCommand != null) {
				try {
					selectCommand.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + " Failed To release connection : " + e.toString());

				}
			}
		}

		return resultSourceBean;
	}

	// public SourceBean selectToSourceBean(String query,DataConnection dc)

	/**
	 * Metodo per effettuare una query di cancellazione. Il primo parametro è un array di <code>Object</code> contenente
	 * i valori delle condizioni di where, se vale null equivale a fieldWhere[0]. Il secondo è il nome con il quale è
	 * referenziata una query nel file di configurazione xml.
	 * 
	 * @param fieldWhere
	 *            valore dei campi della condizione di where in ordine come definito stmtQuery.
	 * @param stmtQuery
	 *            query definita nel file xml di configurazione.
	 * @throws com.engiweb.wfmanager.errors.EMFInternalError
	 *             in caso di errore nell'esecuzione della query di cancellazione.
	 */
	public void deleteQuery(Object[] fieldWhere, String stmtQuery) throws EMFInternalError {
		String thismethod = "::deleteQuery(Object[], String) ";
		DataConnection dc = null;
		SQLCommand deleteCommand = null;

		try {
			_logger.debug(MODULO + thismethod + "CALLED...");

			dc = getConnection();
			deleteCommand = dc.createDeleteCommand(SQLStatements.getStatement(stmtQuery));

			ArrayList inputParameters = null;

			if (fieldWhere == null) {
				inputParameters = new ArrayList(0);
			} else {
				inputParameters = new ArrayList(fieldWhere.length);

				for (int i = 0; i < fieldWhere.length; i++) {
					setQueryParameter("", inputParameters, fieldWhere[i], dc);
				}
			}

			DataResult dr = deleteCommand.execute(inputParameters);
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (deleteCommand != null) {
				try {
					deleteCommand.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + "Errore in selectCommand.close() : " + e.toString());

					throw e;
				}
			}

			if (dc != null) {
				try {
					dc.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + "Errore in chiusura connessione dc.close() : " + e.toString());

					throw e;
				}
			}
		}
	}

	// public void deleteQuery(Object fieldWhere[], String stmtQuery)

	/**
	 * Metodo per effettuare una query di cancellazione. Il primo parametro è un array di <code>Object</code> contenente
	 * i valori delle condizioni di where, se vale null equivale a fieldWhere[0]. Il secondo è il nome con il quale è
	 * referenziata una query nel file di configurazione xml,il terzo è la connessione al database. <BR>
	 * <B>ATTENZIONE : </B>questo metodo non chiude la connessione al database (DataConnection dc).
	 * 
	 * @param fieldWhere
	 *            valore dei campi della condizione di where in ordine come definito stmtQuery.
	 * @param stmtQuery
	 *            query definita nel file xml di configurazione.
	 * @param dc
	 *            è la connessione al database.
	 * @throws com.engiweb.wfmanager.errors.EMFInternalError
	 *             in caso di errore nell'esecuzione della query di cancellazione.
	 */
	public void deleteQuery(Object[] fieldWhere, String stmtQuery, DataConnection dc) throws EMFInternalError {
		String thismethod = "::deleteQuery(Object[],String,DataConnection) ";
		_logger.debug(MODULO + thismethod + "CALLED...");

		SQLCommand deleteCommand = null;

		if (dc == null) {
			_logger.debug(MODULO + thismethod + "La connessione al database DataConnection è nulla.");

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "La connessione al database DataConnection è nulla.");
		}

		try {
			deleteCommand = dc.createDeleteCommand(SQLStatements.getStatement(stmtQuery));

			ArrayList inputParameters = null;

			if (fieldWhere == null) {
				inputParameters = new ArrayList(0);
			} else {
				inputParameters = new ArrayList(fieldWhere.length);

				for (int i = 0; i < fieldWhere.length; i++) {
					setQueryParameter("", inputParameters, fieldWhere[i], dc);
				}
			}

			DataResult dr = deleteCommand.execute(inputParameters);
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (deleteCommand != null) {
				try {
					deleteCommand.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + "Errore in selectCommand.close() : " + e.toString());

					throw e;
				}
			}
		}
	}

	// public void deleteQuery(Object fieldWhere[], String stmtQuery,
	// DataConnection dc)

	/**
	 * Metodo per effettuare una query di cancellazione. La query viene passata come argomento del metodo.
	 * 
	 * @param statement
	 *            query da eseguire.
	 * @throws com.engiweb.wfmanager.errors.EMFInternalError
	 *             in caso di errore nell'esecuzione della query di cancellazione.
	 */
	public void deleteQuery(String statement) throws EMFInternalError {
		String thismethod = "::deleteQuery(String) ";
		DataConnection dc = null;
		SQLCommand deleteCommand = null;

		try {
			_logger.debug(MODULO + thismethod + "CALLED...");

			dc = getConnection();
			deleteCommand = dc.createDeleteCommand(statement);

			DataResult dr = deleteCommand.execute();
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (deleteCommand != null) {
				try {
					deleteCommand.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + "Errore in selectCommand.close() : " + e.toString());

					throw e;
				}
			}

			if (dc != null) {
				try {
					dc.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + "Errore in chiusura connessione dc.close() : " + e.toString());

					throw e;
				}
			}
		}
	}

	// public void deleteQuery(String statement) throws EMFInternalError

	/**
	 * Metodo per effettuare una query di cancellazione. La query viene passata come argomento del metodo. <BR>
	 * <B>ATTENZIONE :</B>questo metodo non chiude la connessione al database (DataConnection dc).
	 * 
	 * @param statement
	 *            query da eseguire.
	 * @param dc
	 *            è la connessione al database
	 * @throws com.engiweb.wfmanager.errors.EMFInternalError
	 *             in caso di errore nell'esecuzione della query di cancellazione.
	 */
	public void deleteQuery(String statement, DataConnection dc) throws EMFInternalError {
		String thismethod = "::deleteQuery(String,DataConnection) ";
		SQLCommand deleteCommand = null;

		if (dc == null) {
			_logger.debug(MODULO + thismethod + "La connessione al database DataConnection è nulla.");

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "La connessione al database DataConnection è nulla.");
		}

		try {
			_logger.debug(MODULO + thismethod + "CALLED...");

			deleteCommand = dc.createDeleteCommand(statement);

			DataResult dr = deleteCommand.execute();
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (deleteCommand != null) {
				try {
					deleteCommand.close();
				} catch (EMFInternalError e) {
					_logger.debug(MODULO + thismethod + "Errore in deleteCommand.close() : " + e.toString());

					_logger.debug(e.toString());

					throw e;
				}
			}
		}
	}

	// public void deleteQuery(String statement, DataConnection dc)

	/**
	 * Questo metodo effettua una query di inserimento di record nel database. Il primo argomento è l'elenco dei valori
	 * da inserire, il secondo è il nome con il quale è referenziata la query nel file di configurazione xml di
	 * configurazione.
	 * 
	 * @param fieldWhere
	 *            valore dei campi da inserire e delle condizioni di where in ordine come definito stmtQuery.
	 * @param stmtQuery
	 *            query definita nel file xml di configurazione.
	 * @throws com.engiweb.wfmanager.errors.EMFInternalError
	 *             in caso di errore nell'esecuzione della query di inserimento.
	 */
	public void insertQuery(Object[] fieldWhere, String stmtQuery) throws EMFInternalError {
		String thismethod = "::insertQuery(Object[], String) ";
		DataConnection dc = null;
		SQLCommand insertCommand = null;

		try {
			_logger.debug(MODULO + thismethod + "CALLED...");

			dc = getConnection();
			insertCommand = dc.createInsertCommand(SQLStatements.getStatement(stmtQuery));

			ArrayList inputParameters = null;

			if (fieldWhere == null) {
				inputParameters = new ArrayList(0);
			} else {
				inputParameters = new ArrayList(fieldWhere.length);

				for (int i = 0; i < fieldWhere.length; i++) {
					setQueryParameter("", inputParameters, fieldWhere[i], dc);
				}
			}

			DataResult dr = insertCommand.execute(inputParameters);
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (insertCommand != null) {
				try {
					insertCommand.close();
				} catch (EMFInternalError emf) {
					_logger.debug(MODULO + thismethod + "Errore in insertCommand.close() : " + emf.getMessage());

					throw emf;
				}
			}

			if (dc != null) {
				try {
					dc.close();
				} catch (EMFInternalError emfi) {
					_logger.debug(
							MODULO + thismethod + "Errore in chiusura connessione dc.close() : " + emfi.getMessage());

					throw emfi;
				}
			}
		}
	}

	// public void insertQuery(Object fieldWhere[], String stmtQuery)

	/**
	 * Questo metodo effettua una query di inserimento di record nel database. Il primo argomento è l'elenco dei valori
	 * da inserire, il secondo è il nome con il quale è referenziata la query nel file di configurazione xml, il terzo è
	 * la connessione al database. <BR>
	 * <B>ATTENZIONE :</B>questo metodo non chiude la connessione al database (DataConnection dc).
	 * 
	 * @param fieldWhere
	 *            valore dei campi da inserire e delle condizioni di where in ordine come definito stmtQuery.
	 * @param stmtQuery
	 *            query definita nel file xml di configurazione.
	 * @param dc
	 *            è la connessione al database.
	 * @throws com.engiweb.wfmanager.errors.EMFInternalError
	 *             in caso di errore nell'esecuzione della query di inserimento.
	 */
	public void insertQuery(Object[] fieldWhere, String stmtQuery, DataConnection dc) throws EMFInternalError {
		String thismethod = "::insertQuery(Object[], String, DataConnection) ";
		SQLCommand insertCommand = null;
		_logger.debug(MODULO + thismethod + "CALLED...");

		if (dc == null) {
			_logger.debug(MODULO + thismethod + "La connessione al database DataConnection è nulla.");

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "La connessione al database DataConnection è nulla.");
		}

		try {
			insertCommand = dc.createInsertCommand(SQLStatements.getStatement(stmtQuery));

			ArrayList inputParameters = null;

			if (fieldWhere == null) {
				inputParameters = new ArrayList(0);
			} else {
				inputParameters = new ArrayList(fieldWhere.length);

				for (int i = 0; i < fieldWhere.length; i++) {
					setQueryParameter("", inputParameters, fieldWhere[i], dc);
				}
			}

			DataResult dr = insertCommand.execute(inputParameters);
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (insertCommand != null) {
				try {
					insertCommand.close();
				} catch (EMFInternalError emf) {
					_logger.debug(MODULO + thismethod + "Errore in insertCommand.close() : " + emf.getMessage());

					throw emf;
				}
			}
		}
	}

	// public void insertQuery(Object fieldWhere[], String stmtQuery,
	// DataConnection dc)

	/**
	 * Questo metodo effettua una query di inserimento di record nel database. La query di inserimento viene passata in
	 * argomento al metodo.
	 * 
	 * @param statement
	 *            query di inserimento.
	 * @throws com.engiweb.wfmanager.errors.EMFInternalError
	 *             in caso di errore nell'esecuzione della query di inserimento.
	 */
	public void insertQuery(String statement) throws EMFInternalError {
		String thismethod = "::insertQuery(String) ";
		DataConnection dc = null;
		SQLCommand insertCommand = null;

		try {
			_logger.debug(MODULO + thismethod + "CALLED...");

			dc = getConnection();
			insertCommand = dc.createInsertCommand(statement);

			DataResult dr = insertCommand.execute();
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (insertCommand != null) {
				try {
					insertCommand.close();
				} catch (EMFInternalError emf) {
					_logger.debug(MODULO + thismethod + "Errore in insertCommand.close() : " + emf.getMessage());

					throw emf;
				}
			}

			if (dc != null) {
				try {
					dc.close();
				} catch (EMFInternalError emfi) {
					_logger.debug(MODULO + thismethod + "Errore in chiusura connessione : " + emfi.getMessage());

					throw emfi;
				}
			}
		}
	}

	// public void insertQuery(String statement) throws EMFInternalError

	/**
	 * Questo metodo effettua una query di inserimento di record nel database. La query di inserimento viene passata in
	 * argomento al metodo. <BR>
	 * <B>ATTENZIONE :</B>questo metodo non chiude la connessione al database (DataConnection dc).
	 * 
	 * @param statement
	 *            query di inserimento.
	 * @param dc
	 *            è connessione al database.
	 * @throws com.engiweb.wfmanager.errors.EMFInternalError
	 *             in caso di errore nell'esecuzione della query di inserimento.
	 */
	public void insertQuery(String statement, DataConnection dc) throws EMFInternalError {
		String thismethod = "::insertQuery(String,DataConnection) ";
		SQLCommand insertCommand = null;

		if (dc == null) {
			_logger.debug(MODULO + thismethod + "La connessione al database DataConnection è nulla.");

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "La connessione al database DataConnection è nulla.");
		}

		try {
			_logger.debug(MODULO + thismethod + "CALLED...");

			insertCommand = dc.createInsertCommand(statement);

			DataResult dr = insertCommand.execute();
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (insertCommand != null) {
				try {
					insertCommand.close();
				} catch (EMFInternalError emf) {
					_logger.debug(MODULO + thismethod + "Errore in insertCommand.close() : " + emf.getMessage());

					throw emf;
				}
			}
		}
	}

	// public void insertQuery(String statement, DataConnection dc) throws
	// EMFInternalError

	/**
	 * Questo metodo effettua una query di aggiornamento di record su database. Il primo argomento è l'elenco dei valori
	 * da inserire, il secondo è il nome con il quale è referenziata la query nel file di configurazione xml.
	 * 
	 * @param fieldWhere
	 *            valore dei campi da inserire e delle condizioni di where in ordine come definito stmtQuery.
	 * @param stmtQuery
	 *            query definita nel file xml di configurazione.
	 * @throws com.engiweb.wfmanager.errors.EMFInternalError
	 *             in caso di errore nell'esecuzione della query di inserimento.
	 */
	public void updateQuery(Object[] fieldWhere, String stmtQuery) throws EMFInternalError {
		String thismethod = "::updateQuery(Object[], String) ";
		DataConnection dc = null;
		SQLCommand updateCommand = null;

		try {
			_logger.debug(MODULO + thismethod + "CALLED...");

			dc = getConnection();
			updateCommand = dc.createUpdateCommand(SQLStatements.getStatement(stmtQuery));

			ArrayList inputParameters = null;

			if (fieldWhere == null) {
				inputParameters = new ArrayList(0);
			} else {
				inputParameters = new ArrayList(fieldWhere.length);

				for (int i = 0; i < fieldWhere.length; i++) {
					setQueryParameter("", inputParameters, fieldWhere[i], dc);
				}
			}

			DataResult dr = updateCommand.execute(inputParameters);
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (updateCommand != null) {
				try {
					updateCommand.close();
				} catch (EMFInternalError emf) {
					_logger.debug(MODULO + thismethod + "Errore in updateCommand.close() : " + emf.getMessage());

					throw emf;
				}
			}

			if (dc != null) {
				try {
					dc.close();
				} catch (EMFInternalError emfi) {
					_logger.debug(
							MODULO + thismethod + "Errore in chiusura connessione dc.close() : " + emfi.getMessage());

					throw emfi;
				}
			}
		}
	}

	// public void updateQuery(Object fieldWhere[], String stmtQuery)

	/**
	 * Questo metodo effettua una query di aggiornamento di record su database. Il primo argomento è l'elenco dei valori
	 * da inserire,il secondo è il nome con il quale è referenziata la query nel file di configurazione xml, il terzo è
	 * la connessione al database. <BR>
	 * <B>ATTENZIONE : </B>questo metodo non chiude la connessione al database (DataConnection dc).
	 * 
	 * @param fieldWhere
	 *            valore dei campi da inserire e delle condizioni di where in ordine come definito stmtQuery.
	 * @param stmtQuery
	 *            query definita nel file xml di configurazione.
	 * @param dc
	 *            è la connessione al database.
	 * @throws com.engiweb.wfmanager.errors.EMFInternalError
	 *             in caso di errore nell'esecuzione della query di inserimento.
	 */
	public void updateQuery(Object[] fieldWhere, String stmtQuery, DataConnection dc) throws EMFInternalError {
		String thismethod = "::updateQuery(Object[], String, DataConnection) ";
		SQLCommand updateCommand = null;
		_logger.debug(MODULO + thismethod + "CALLED...");

		if (dc == null) {
			_logger.debug(MODULO + thismethod + "La connessione al database DataConnection è nulla.");

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "La connessione al database DataConnection è nulla.");
		}

		try {
			String sqlquery = SQLStatements.getStatement(stmtQuery);
			_logger.debug(MODULO + thismethod + "COMMAND QUERY :\n" + sqlquery);

			updateCommand = dc.createUpdateCommand(sqlquery);

			ArrayList inputParameters = null;

			if (fieldWhere == null) {
				inputParameters = new ArrayList(0);
			} else {
				inputParameters = new ArrayList(fieldWhere.length);

				for (int i = 0; i < fieldWhere.length; i++) {
					setQueryParameter("", inputParameters, fieldWhere[i], dc);
				}
			}

			DataResult dr = updateCommand.execute(inputParameters);
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (updateCommand != null) {
				try {
					updateCommand.close();
				} catch (EMFInternalError emf) {
					_logger.debug(MODULO + thismethod + "Errore in updateCommand.close() : " + emf.getMessage());

					throw emf;
				}
			}
		}
	}

	// public void updateQuery(Object fieldWhere[], String stmtQuery,
	// DataConnection dc)

	/**
	 * Questo metodo effettua una query di aggiornamento di record su database. La query di update viene passata come
	 * argomento del metodo.
	 * 
	 * @param statement
	 *            è la query di update.
	 * @throws com.engiweb.wfmanager.errors.EMFInternalError
	 *             in caso di errore nell'esecuzione della query di inserimento.
	 */
	public void updateQuery(String statement) throws EMFInternalError {
		String thismethod = "::updateQuery(String) ";
		DataConnection dc = null;
		SQLCommand updateCommand = null;

		try {
			_logger.debug(MODULO + thismethod + "CALLED...");

			dc = getConnection();
			updateCommand = dc.createUpdateCommand(statement);

			DataResult dr = updateCommand.execute();
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (updateCommand != null) {
				try {
					updateCommand.close();
				} catch (EMFInternalError emf) {
					_logger.debug(MODULO + thismethod + "Errore in updateCommand.close() : " + emf.getMessage());

					throw emf;
				}
			}

			if (dc != null) {
				try {
					dc.close();
				} catch (EMFInternalError emfi) {
					_logger.debug(MODULO + thismethod + "Errore in chiusura connessione : " + emfi.getMessage());

					throw emfi;
				}
			}
		}
	}

	// public void updateQuery(String statement) throws EMFInternalError

	/**
	 * Questo metodo effettua una query di aggiornamento di record su database. La query di update viene passata come
	 * argomento del metodo. <BR>
	 * <B>ATTENZIONE :</B>questo metodo non chiude la connessione al database (DataConnection dc).
	 * 
	 * @param statement
	 *            è la query di update.
	 * @param dc
	 *            è la connessione al database
	 * @throws com.engiweb.wfmanager.errors.EMFInternalError
	 *             in caso di errore nell'esecuzione della query di inserimento.
	 */
	public void updateQuery(String statement, DataConnection dc) throws EMFInternalError {
		String thismethod = "::updateQuery(String,DataConnection) ";
		SQLCommand updateCommand = null;

		if (dc == null) {
			_logger.debug(MODULO + thismethod + "La connessione al database DataConnection è nulla.");

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "La connessione al database DataConnection è nulla.");
		}

		try {
			_logger.debug(MODULO + thismethod + "CALLED...");

			updateCommand = dc.createUpdateCommand(statement);

			DataResult dr = updateCommand.execute();
		} catch (EMFInternalError e) {
			_logger.debug(e.toString());

			throw e;
		} catch (Exception xe) {
			_logger.debug(xe.toString());

			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "ERRORE : " + xe.toString());
		} finally {
			if (updateCommand != null) {
				try {
					updateCommand.close();
				} catch (EMFInternalError emf) {
					_logger.debug(MODULO + thismethod + "Errore in updateCommand.close() : " + emf.getMessage());

					throw emf;
				}
			}
		}
	}

	// public void updateQuery(String statement, DataConnection dc) throws
	// EMFInternalError

	/**
	 * Questo metodo restituisce il numero di colonne di una oggetto com.engiweb.framework.dbaccess.sql.DataRow.
	 * 
	 * @param row
	 *            è l'oggetto del rappresentante un riga del result set di una query.
	 * @return il numero di colonne della riga.
	 */
	public static int getColoumns(DataRow row) {
		int a = 0;

		try {
			while (true) {
				row.getColumn(a);
				a++;
			}
		} catch (IndexOutOfBoundsException iobe) {
		}

		return a;
	}

	// public static int getColoumns(DataRow row)
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

	// private void setQueryParameter(String fieldname, ArrayList parameter,
	// Object field, DataConnection dc)
}