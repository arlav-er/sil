/* Generated by Together */

package com.engiweb.framework.dbaccess.sql;

import java.sql.Date;
import java.sql.Timestamp;

import com.engiweb.framework.dbaccess.sql.mappers.SQLMapper;

/**
 * L'oggetto DataField rappresenta un campo del database avente associato associato il tipo SQL . Esso è utilizzato in
 * due contesti :
 * <li>Come parametro nell'esecuzione dei comandi ( in questo caso il nome non è importante ).
 * <li>Come valore di una colonna in un oggetto di tipo DataRow. Questo oggetto gestisce in maniera automatica le
 * conversioni tra tipi.
 */
public class DataField {
	/**
	 * Costruttore.
	 * 
	 * @param <code>String</code>
	 *            name - il nome del DataField.
	 * @param int
	 *            sqlType - tipo SQL del DataField.
	 * @param <code>Object</code>
	 *            objectValue - valore dell'oggetto rappresentato dal DataField.
	 * @param <code>SQLMapper</code>
	 *            sqlMapper - l'oggetto che si occupa del mapper per i tipi date e timestamp.
	 */
	public DataField(String name, int sqlType, Object objectValue, SQLMapper sqlMapper) {
		_name = name;
		_sqlType = sqlType;
		_sqlMapper = sqlMapper;
		_typeName = null;
		if (objectValue instanceof String)
			setStringValue((String) objectValue);
		else
			setObjectValue(objectValue);
	} // public DataField(String name, int sqlType, Object objectValue,
		// SQLMapper sqlMapper)

	/**
	 * Costruttore.
	 * 
	 * @param <code>String</code>
	 *            name - il nome del DataField.
	 * @param int
	 *            sqlType - tipo SQL del DataField.
	 * @param <code>Object</code>
	 *            objectValue - valore dell'oggetto rappresentato dal DataField.
	 * @param <code>String
	 *            typeName</code> - fully-qualified name di un SQL structured type.
	 * @param <code>SQLMapper</code>
	 *            sqlMapper - l'oggetto che si occupa del mapper per i tipi date e timestamp.
	 */
	// Modifica Monica del 27/01/2004 - inizio
	public DataField(String name, int sqlType, Object objectValue, SQLMapper sqlMapper, String typeName) {
		_name = name;
		_sqlType = sqlType;
		_sqlMapper = sqlMapper;
		_typeName = typeName;
		if (objectValue instanceof String)
			setStringValue((String) objectValue);
		else
			setObjectValue(objectValue);
	} // public DataField(String name, int sqlType, Object objectValue,
		// SQLMapper sqlMapper, String typeName)

	/**
	 * Costruttore
	 * 
	 * @param <code>String</code>
	 *            name - il nome del DataField.
	 * @param int
	 *            sqlType - tipo SQL del DataField.
	 * @param <code>String</code>
	 *            stringValue - valore dell'oggetto rappresentato dal DataField.
	 * @param <code>String
	 *            typeName</code> - fully-qualified name di un SQL structured type.
	 * @param <code>SQLMapper</code>
	 *            sqlMapper - l'oggetto che si occupa del mapper per i tipi date e timestamp.
	 */
	public DataField(String name, int sqlType, String stringValue, SQLMapper sqlMapper, String typeName) {
		_name = name;
		_sqlType = sqlType;
		_sqlMapper = sqlMapper;
		_typeName = typeName;
		setStringValue(stringValue);
	} // public DataField(String name, int sqlType, String stringValue,
		// SQLMapper sqlMapper, String typeName)

	// Modifica Monica del 27/01/2004 - fine
	/**
	 * Costruttore
	 * 
	 * @param <code>String</code>
	 *            name - il nome del DataField.
	 * @param int
	 *            sqlType - tipo SQL del DataField.
	 * @param <code>String</code>
	 *            stringValue - valore dell'oggetto rappresentato dal DataField.
	 * @param <code>SQLMapper</code>
	 *            sqlMapper - l'oggetto che si occupa del mapper per i tipi date e timestamp.
	 */
	public DataField(String name, int sqlType, String stringValue, SQLMapper sqlMapper) {
		_name = name;
		_sqlType = sqlType;
		_sqlMapper = sqlMapper;
		_typeName = null;
		setStringValue(stringValue);
	} // public DataField(String name, int sqlType, String stringValue,
		// SQLMapper sqlMapper)

	/**
	 * Permette di recuperare il tipo SQL associato al DataField.
	 * <p>
	 * 
	 * @return <code>int</code> il valore intero associato al tipo SQL.
	 */
	public int getSqlType() {
		return _sqlType;
	} // public int getSqlType()

	/**
	 * Permette di impostare il tipo SQL associato al DataField.
	 * <p>
	 * 
	 * @param <code>int</code>
	 *            il valore intero associato al tipo SQL.
	 */
	public void setSqlType(int sqlType) {
		this._sqlType = sqlType;
	} // public void setSqlType(int sqlType)

	/**
	 * Permette di recuperare il nome logico del campo associato al DataField.
	 * 
	 * @return <code>String</code> il nome logico del campo associato al DataField.
	 */
	public String getName() {
		return _name;
	} // public String getName()

	/**
	 * Permette di impostare il nome logico del campo associato al DataField.
	 * 
	 * @param <code>String</code>
	 *            il nome logico del campo associato al DataField.
	 */
	public void setName(String name) {
		this._name = name;
	} // public void setName(String name)

	/**
	 * Permette di recuperare il valore del campo associato al DataField.
	 * 
	 * @return <code>Object</code> il valore del campo associato al DataField.
	 */
	public Object getObjectValue() {
		return _objectValue;
	} // public Object getObjectValue()

	/**
	 * Permette di impostare il valore del campo associato al DataField.
	 * 
	 * @param <code>Object</code>
	 *            il valore del campo associato al DataField.
	 */
	public void setObjectValue(Object objectValue) {
		if ((objectValue == null) || ((objectValue instanceof String) && (((String) objectValue).length() == 0))) {
			_objectValue = null;
			_stringValue = null;
			return;
		} // if ((objectValue == null) || ((objectValue instanceof String) &&
			// (((String)objectValue).length() == 0)))
		_objectValue = objectValue;
		_stringValue = _sqlMapper.getStringValue(_sqlType, _objectValue);
		if (_objectValue instanceof Date)
			_objectValue = new DateDecorator((Date) _objectValue, _stringValue);
		else if (_objectValue instanceof Timestamp)
			_objectValue = new TimestampDecorator((Timestamp) _objectValue, _stringValue);
	} // public void setObjectValue(Object objectValue)

	/**
	 * Permette di recuperare il valore del campo associato al DataField nel formato String.
	 * 
	 * @return <code>String</code> il valore del campo associato al DataField.
	 */
	public String getStringValue() {
		return _stringValue;
	} // public String getStringValue()

	/**
	 * Permette di impostare il valore del campo associato al DataField nel formato String.
	 * 
	 * @param <code>String</code>
	 *            il valore del campo associato al DataField.
	 */
	public void setStringValue(String stringValue) {
		if ((stringValue == null) || (stringValue.length() == 0)) {
			_objectValue = null;
			_stringValue = null;
			return;
		} // if ((stringValue == null) || (stringValue.length() == 0))
		_stringValue = stringValue;
		_objectValue = _sqlMapper.getObjectValue(_sqlType, _stringValue);
	} // public void setStringValue(String stringValue)

	/**
	 * Permette di recuperare il valore del campo associato al DataField nel formato String.
	 * 
	 * @return <code>String</code> il valore del campo associato al DataField.
	 */
	public String getTypeName() {
		return _typeName;
	}// public String getTypeName()

	/**
	 * Permette di impostare il valore del campo associato al DataField nel formato String.
	 * 
	 * @param <code>String</code>
	 *            il valore del campo associato al DataField.
	 */
	public void setTypeName(String typeName) {
		_typeName = typeName;
	}// public void setTypeName(String typeName)

	private int _sqlType;
	private String _name = null;
	private Object _objectValue = null;
	private String _stringValue = null;
	// Modifica Monica del 27/01/2004 - inizio
	// valore di default = null, quindi se non viene passato al costruttore,
	// resta tale
	private String _typeName = null;
	// Modifica Monica del 27/01/2004 - fine
	private SQLMapper _sqlMapper = null;

} // public class DataField
