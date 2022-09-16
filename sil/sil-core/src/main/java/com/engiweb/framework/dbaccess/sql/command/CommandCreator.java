package com.engiweb.framework.dbaccess.sql.command;

import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;

/**
 * Questa interfaccia è un AbstractFactory per le factory concrete di creazione dei comandi SQL DefaultCommandCreator e
 * LegacyCommandCreator
 */
public interface CommandCreator {
	/**
	 * Crea un SQLCommand di select data una DataConnection e la CommandString
	 * 
	 * @param DataConnection
	 *            dataConnection - la connessione al db
	 * @param <B>String
	 *            </B> commandString - La stringa contenente il comando sql
	 * @return un oggetto di tipo <B>SQLCommand</B> rapprsentante il comando di select contenuto nella command string
	 */
	public SQLCommand createSelectCommand(DataConnection dataConnection, String commandString, boolean scroll);

	/**
	 * Crea un SQLCommand di insert data una DataConnection e la CommandString
	 * 
	 * @param DataConnection
	 *            dataConnection - la connessione al db
	 * @param <B>String
	 *            </B> commandString - La stringa contenente il comando sql
	 * @return un oggetto di tipo <B>SQLCommand</B> rapprsentante il comando di insert contenuto nella command string
	 */
	public SQLCommand createInsertCommand(DataConnection dataConnection, String commandString);

	/**
	 * Crea un SQLCommand di update data una DataConnection e la CommandString
	 * 
	 * @param DataConnection
	 *            dataConnection - la connessione al db
	 * @param <B>String
	 *            </B> commandString - La stringa contenente il comando sql
	 * @return un oggetto di tipo <B>SQLCommand</B> rapprsentante il comando di update contenuto nella command string
	 */
	public SQLCommand createUpdateCommand(DataConnection dataConnection, String commandString);

	/**
	 * Crea un SQLCommand di delete data una DataConnection e la CommandString
	 * 
	 * @param DataConnection
	 *            dataConnection - la connessione al db
	 * @param <B>String
	 *            </B> commandString - La stringa contenente il comando sql
	 * @return un oggetto di tipo <B>SQLCommand</B> rapprsentante il comando di delete contenuto nella command string
	 */
	public SQLCommand createDeleteCommand(DataConnection dataConnection, String commandString);

	/**
	 * Crea un SQLCommand per l'esecuzione di una stored procedure data una DataConnection e la CommandString
	 * 
	 * @param DataConnection
	 *            dataConnection - la connessione al db
	 * @param <B>String
	 *            </B> commandString - La stringa contenente il comando sql
	 * @return un oggetto di tipo <B>SQLCommand</B> rapprsentante il comando di delete contenuto nella command string
	 */
	public SQLCommand createStoredProcedureCommand(DataConnection dataConnection, String commandString);
}
