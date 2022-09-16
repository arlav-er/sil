/* Generated by Together */

package com.engiweb.framework.dbaccess.sql;

import java.util.HashMap;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

/**
 * Questa Classe rappresenta una riga del database ovvero un insieme ordinato di DataField.
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class DataRow {
	public static final String ROW_TAG = "ROW";
	private int _columnSize;
	private DataField[] _columns;
	private HashMap _columnsMap = null;

	/**
	 * Costruttore
	 * 
	 * @param int
	 *            columnSize - il numero di colonna di una riga.
	 */
	public DataRow(int columnsSize) {
		_columns = new DataField[columnsSize];
		_columnsMap = new HashMap();
		_columnSize = columnsSize;
	} // public DataRow(int columnsSize)

	/**
	 * Questo metodo aggiunge alla riga il DataField nella posizione specificata.
	 * 
	 * @param int
	 *            position - l'indice della colonna dove aggiungere il dataField.
	 * @param <code>DataField</code>
	 *            il valore del campo da inserire nella posizione specificata.
	 */
	public void addColumn(int position, DataField dataColumn) {
		_columns[position] = dataColumn;
		_columnsMap.put(dataColumn.getName().toUpperCase(), new Integer(position));
	} // public void addColumn(int position, DataField dataColumn)

	/**
	 * Questo metodo recupera il DataField relativo alla colonna specificata dalla posizione fornita.
	 * 
	 * @param int
	 *            position - l'indice della colonna.
	 * @return <code>DataField</code> il DataField con il dato della colonna .
	 */
	public DataField getColumn(int position) {
		return _columns[position];
	} // public DataField getColumn(int position)

	/**
	 * Questo metodo recupera il DataField relativo alla colonna individuata da columnName.
	 * 
	 * @param <code>String</code>
	 *            columnName - il nome della colonna.
	 * @return <code>DataField</code> il DataField con il dato della colonna specificata.
	 */
	public DataField getColumn(String columnName) {
		int position = ((Integer) _columnsMap.get(columnName.toUpperCase())).intValue();
		return getColumn(position);
	} // public DataField getColumn(String columnName)

	/**
	 * Questo metodo ricava il numero di colonne .
	 * 
	 * @return <code>int</code> il numero di colonne .
	 */
	public int getColumnSize() {
		return _columnSize;
	} // public DataField getColumn(String columnName)

	/**
	 * Questo metodo crea un'istanza della classe SourceBean inserendovi i valori delle colonne.
	 * 
	 * @throws <code>EMFInternalError</code>
	 * - Se risulta impossibile popolare l'oggetto SourceBean.
	 */
	public SourceBean getSourceBean() throws EMFInternalError {
		try {
			SourceBean sb = new SourceBean(DataRow.ROW_TAG);
			Object columnObjectValue = null;
			String columnName = null;
			for (int i = 0; i < _columnSize; i++) {
				columnObjectValue = _columns[i].getObjectValue();
				columnName = _columns[i].getName();
				if (columnObjectValue != null)
					sb.setAttribute(columnName, columnObjectValue);
				else
					continue;
			} // for (int i = 0; i < _columnSize; i++)
			return sb;
		} catch (SourceBeanException sbe) {
			throw new EMFInternalError(EMFErrorSeverity.ERROR,
					"DataRow :: getSourceBean() :: error :: " + sbe.getMessage());
		} // end try-catcj
	} // public SourceBean getSourceBean() throws EMFInternalError
} // end Class DataRow
