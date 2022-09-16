package com.engiweb.framework.dbaccess.sql.command.std;

import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.command.CommandCreator;

/**
 * Questa Classe è una factory concreta di CommandCreator per la creazione di oggetti SQLCommand che fanno uso delle
 * caratteristiche di jdbc 2.0 per l'esecuzione
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class DefaultCommandCreator implements CommandCreator {
	/**
	 * Crea un SQLCommand di select data una DataConnection e la CommandString per driver jdbc 2.0
	 * 
	 * @param DataConnection
	 *            dataConnection - la connessione al db
	 * @param String
	 *            commandString - La stringa contenente il comando sql
	 * @return un oggetto di tipo <B>SQLCommand</B> rapprsentante il comando di select contenuto nella command string
	 */
	public SQLCommand createSelectCommand(DataConnection dataConnection, String commandString, boolean scroll) {
		return new SelectCommand(dataConnection, commandString, scroll);
	} // public SQLCommand createSelectCommand(DataConnection dataConnection,
		// String commandString, boolean scroll)

	/**
	 * Crea un SQLCommand di insert data una DataConnection e la CommandString per driver jdbc 2.0
	 * 
	 * @param DataConnection
	 *            dataConnection - la connessione al db
	 * @param String
	 *            commandString - La stringa contenente il comando sql
	 * @return un oggetto di tipo <B>SQLCommand</B> rapprsentante il comando di insert contenuto nella command string
	 */
	public SQLCommand createInsertCommand(DataConnection dataConnection, String commandString) {
		return new InsertCommand(dataConnection, commandString);
	} // public SQLCommand createInsertCommand(DataConnection dataConnection,

	// String commandString)

	/**
	 * Crea un SQLCommand di update data una DataConnection e la CommandString per driver jdbc 2.0
	 * 
	 * @param DataConnection
	 *            dataConnection - la connessione al db
	 * @param String
	 *            commandString - La stringa contenente il comando sql
	 * @return un oggetto di tipo <B>SQLCommand</B> rapprsentante il comando di update contenuto nella command string
	 */
	public SQLCommand createUpdateCommand(DataConnection dataConnection, String commandString) {
		return new UpdateCommand(dataConnection, commandString);
	} // public SQLCommand createUpdateCommand(DataConnection dataConnection,

	// String commandString)

	/**
	 * Crea un SQLCommand di delete data una DataConnection e la CommandString per driver jdbc 2.0
	 * 
	 * @param DataConnection
	 *            dataConnection - la connessione al db
	 * @param String
	 *            commandString - La stringa contenente il comando sql
	 * @return un oggetto di tipo <B>SQLCommand</B> rapprsentante il comando di delete contenuto nella command string
	 */
	public SQLCommand createDeleteCommand(DataConnection dataConnection, String commandString) {
		return new DeleteCommand(dataConnection, commandString);
	} // public SQLCommand createDeleteCommand(DataConnection dataConnection,

	// String commandString)

	/**
	 * Crea un SQLCommand per l'esecuzione di una stored procedure data una DataConnection e la CommandString
	 * 
	 * @param DataConnection
	 *            dataConnection - la connessione al db
	 * @param <B>String
	 *            </B> commandString - La stringa contenente il comando sql
	 * @return un oggetto di tipo <B>SQLCommand</B> rapprsentante il comando di delete contenuto nella command string
	 */
	public SQLCommand createStoredProcedureCommand(DataConnection dataConnection, String commandString) {
		return new StoredProcedureCommand(dataConnection, commandString);
	} // public SQLCommand createStoredProcedureCommand(DataConnection
		// dataConnection, String commandString){
} // end Class DefaultCommandCreator
