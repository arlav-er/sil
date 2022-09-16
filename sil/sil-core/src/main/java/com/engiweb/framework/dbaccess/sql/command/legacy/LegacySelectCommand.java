package com.engiweb.framework.dbaccess.sql.command.legacy;

import java.util.List;

import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.error.EMFInternalError;

/**
 * Questa Classe rappresenta un comando SQL di SELECT per driver jdbc 1.0
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class LegacySelectCommand extends SQLCommand {
	private boolean _scroll = true;

	/**
	 * Costruttore
	 * 
	 * @param <B>DataConnection
	 *            </B> dataConnection - l'oggetto dataConnection legato al comando
	 * @param <B>String
	 *            </B> commandString - La Stringa rappresentante il comando
	 */
	public LegacySelectCommand(DataConnection dataConnection, String commandString, boolean scroll) {
		super(dataConnection, commandString);
		_scroll = scroll;
	} // public LegacySelectCommand(DataConnection dataConnection, String
		// commandString, boolean scroll)

	/**
	 * Questo metodo server per eseguire il comnado senza parametri
	 * 
	 * @return un oggetto di tipo <B>DataResult</B> contente il risultato dell'esecuzione del comando
	 * @throws <B>EMFInternalError
	 *             </B> - Se si verifica qualche problema durante l'esecuzione del comando
	 */
	public DataResult execute() throws EMFInternalError {
		return null;
	} // public DataResult execute() throws EMFInternalError

	/**
	 * Questo metodo serve per eseguire il comando con dei parametri di input
	 * 
	 * @param <B>List
	 *            </B> inputParameters - una lista di <B>DataField</B> object
	 * @return un oggetto di tipo <B>DataResult</B> contente il risultato dell'esecuzione del comando
	 * @throws <B>EMFInternalError
	 *             </B> - Se si verifica qualche problema durante l'esecuzione del comando
	 */
	public DataResult execute(List inputParameters) throws EMFInternalError {
		return null;
	} // public DataResult execute(List inputParameters) throws
		// EMFInternalError
} // end Class LegacyInsertCommand
