package com.engiweb.framework.dbaccess;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

/**
 * Questa classe ha il compito di recuperare la stringa rappresentante lo statement SQL da un file XML.
 */
public class SQLStatements {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SQLStatements.class.getName());

	/**
	 * Questo metodo recupera lo statement SQL avente associato alla <em>key</em> name.
	 * 
	 * @param String
	 *            nome logico dello statement SQL.
	 * @return <code>String</code> corrispondente allo statement SQL
	 */
	public static String getStatement(String name) {
		_logger.debug("SQLStatements::getStatement: invocato");

		if (name == null) {
			_logger.debug("SQLStatements::getStatement: nome dello statement nullo");

			return null;
		} // if (name == null)
		_logger.debug("SQLStatements::getStatement: name [" + name + "]");

		ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean statementConfig = (SourceBean) configure.getFilteredSourceBeanAttribute("STATEMENTS.STATEMENT",
				"NAME", name);
		if (statementConfig == null) {
			_logger.debug("SQLStatements::getStatement: statement non trovato");

			return null;
		} // if (statementConfig == null)
		String statement = (String) statementConfig.getAttribute("QUERY");
		if (statement == null)
			_logger.debug("SQLStatements::getStatement: statement non trovato");

		return statement;
	} // public static String getStatement(String name)

	/**
	 * Questo metodo recupera lo statement SQL avente associato alla <em>key</em> name.
	 * 
	 * @param String
	 *            nome logico dello statement SQL.
	 * @return <code>String</code> corrispondente allo statement SQL
	 */
	public static String getStatementInit(String name) {
		_logger.debug("SQLStatements::getStatement: invocato");

		if (name == null) {
			_logger.debug("SQLStatements::getStatement: nome dello statement nullo");

			return null;
		} // if (name == null)
		_logger.debug("SQLStatements::getStatement: name [" + name + "]");

		ConfigSingleton configure = ConfigSingleton.getInstanceAndInit();
		SourceBean statementConfig = (SourceBean) configure.getFilteredSourceBeanAttribute("STATEMENTS.STATEMENT",
				"NAME", name);
		if (statementConfig == null) {
			_logger.debug("SQLStatements::getStatement: statement non trovato");

			return null;
		} // if (statementConfig == null)
		String statement = (String) statementConfig.getAttribute("QUERY");
		if (statement == null)
			_logger.debug("SQLStatements::getStatement: statement non trovato");

		return statement;
	}
} // public class SQLStatements
