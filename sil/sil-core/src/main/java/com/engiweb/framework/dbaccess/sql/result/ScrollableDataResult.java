package com.engiweb.framework.dbaccess.sql.result;

import java.sql.ResultSet;

import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.error.EMFInternalError;

/**
 * Questa interfaccia definisce il comportamento di un oggetto <code>ScrollableDataResult</code>.
 * 
 * @see ScrollableDataResult
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public interface ScrollableDataResult extends DataResultInterface {
	public static final String ROWS_TAG = "ROWS";

	/**
	 * Questo metodo viene usato per scorrere l'oggetto in maniera sequenziale. Il metodo ritorna true se esistono altre
	 * righe rispetto alla posizione corrente in cui si è posizionati
	 * 
	 * @return true se l'oggetto ha altre righe in avanti rispetto alla posizione corrente in cui si è posizionati false
	 *         altrimenti
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public boolean hasRows() throws EMFInternalError;

	/**
	 * Questo metodo ritorna la riga corrente su cui il cursore è posizionato
	 * 
	 * @return <B>DataRow</B> - l'oggetto rappresentante la riga del resultset su cui il cursore è posizionato
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public DataRow getDataRow() throws EMFInternalError;

	/**
	 * Questo metodo ritorna la riga della posizione i-esima dell'oggetto scrollableDataResult
	 * 
	 * @param int
	 *            position - il numero della riga che si vuole ottenere
	 * @return <B>DataRow</B> - l'oggetto rappresentante la riga del resultset su cui il cursore è posizionato
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public DataRow getDataRow(int position) throws EMFInternalError;

	public int getColumnCount();

	/**
	 * Ritorna un vettore contenente i valori sql.Types delle colonne dell'oggetto ScrollableDataResult
	 * 
	 * @return int[] - il vettore contenente i valori sql.Types delle colonne dell'oggetto ScrollableDataResult
	 */
	public int[] getColumnTypes();

	public String[] getColumnNames();

	/**
	 * Ritorna il numero di righe dell'oggetto
	 * 
	 * @return int - il numero di righe dell'oggetto
	 */
	public int getRowsNumber() throws EMFInternalError;

	/**
	 * Ritorna sempre DataResultInterface.SCROLLABLE_DATA_RESULT
	 * 
	 * @return <B>String</B> - Ritorna sempre DataResultInterface.SCROLLABLE_DATA_RESULT
	 */
	public String getDataResultType();

	/**
	 * Questo metodo permette il posizionamento del cursore sulla riga i-esima dell'oggetto ScrollableDataResult
	 * 
	 * @param int
	 *            position - il numero di riga sulla quale ci si vuole posizionare
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public void moveTo(int position) throws EMFInternalError;

	/**
	 * Questo metodo permette di forzare l'aggiornamento dei dati sull'oggetto ScrollableDataResult
	 * 
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public void refresh() throws EMFInternalError;

	/**
	 * Questo metodo permette di chiudere l'oggetto ScrollableDataResult
	 * 
	 * @throws <B>EMFInternalError
	 *             </B> - Se qualche errore si verifica
	 */
	public void close() throws EMFInternalError;

	public ResultSet getResultSet();
} // public interface ScrollableDataResult extends DataResultInterface
