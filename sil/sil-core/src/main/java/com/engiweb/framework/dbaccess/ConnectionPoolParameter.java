package com.engiweb.framework.dbaccess;

/**
 * Questa classe rappresenta un parametro di un pool di connessioni
 * <li>un nome
 * <li>un tipo
 * <li>un valore
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class ConnectionPoolParameter {
	private String _name = null;
	private String _type = null;
	private String _value = null;

	/**
	 * Costruttore
	 * 
	 * @param <B>String
	 *            </B> name - il nome del parametro
	 * @param <B>String
	 *            </B> name - il valore del parametro
	 */
	public ConnectionPoolParameter(String name, String value) {
		_name = name;
		_value = value;
	} // public ConnectionPoolParameter (String name, String value)

	/**
	 * Metodo getter per il parametro name
	 * 
	 * @return un oggetto di tipo <B>String</B> con il nome del parametro
	 */
	public String getName() {
		return _name;
	} // public String getName()

	/**
	 * Metodo setter per il parametro name
	 * 
	 * @param un
	 *            oggetto di tipo <B>String</B> con il nome del parametro
	 */
	public void setName(String newName) {
		_name = newName;
	} // public void setName(String newName)

	/**
	 * Metodo getter per il parametro type
	 * 
	 * @return un oggetto di tipo <B>String</B> con il tipo del parametro
	 */
	public String getType() {
		return _type;
	} // public String getType()

	/**
	 * Metodo setter per il parametro type
	 * 
	 * @param un
	 *            oggetto di tipo <B>String</B> con il tipo del parametro
	 */
	public void setType(String newType) {
		_type = newType;
	} // public void setType(String newType)

	/**
	 * Metodo getter per il parametro value
	 * 
	 * @return un oggetto di tipo <B>String</B> con il valore del parametro
	 */
	public String getValue() {
		return _value;
	} // public String getValue()

	/**
	 * Metodo setter per il parametro value
	 * 
	 * @param un
	 *            oggetto di tipo <B>String</B> con il valore del parametro
	 */
	public void setValue(String newValue) {
		_value = newValue;
	} // public void setValue(String newValue)
} // end Class ConnectionPoolParameter
