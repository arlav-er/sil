/* Generated by Together */

package com.engiweb.framework.dbaccess.sql.command.std;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultFactory;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.DefaultDataResultFactory;
import com.engiweb.framework.dbaccess.sql.result.InformationDataResult;
import com.engiweb.framework.error.EMFInternalError;

/**
 * Questa Classe rappresenta un comando SQL di INSERT per driver jdbc 2.0 o superiori
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class InsertCommand extends SQLCommand {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertCommand.class.getName());

	/**
	 * Costruttore
	 * 
	 * @param <B>DataConnection
	 *            </B> dataConnection - l'oggetto dataConnection legato al comando
	 * @param <B>String
	 *            </B> commandString - La Stringa rappresentante il comando
	 */
	public InsertCommand(DataConnection dataConnection, String commandString) {
		super(dataConnection, commandString);
	} // public InsertCommand(DataConnection dataConnection, String
		// commandString)

	/**
	 * Questo metodo server per eseguire il comnado senza parametri
	 * 
	 * @return un oggetto di tipo <B>DataResult</B> contente il risultato dell'esecuzione del comando
	 * @throws <B>EMFInternalError
	 *             </B> - Se si verifica qualche problema durante l'esecuzione del comando
	 */
	public DataResult execute() throws EMFInternalError {
		// Monitor monitor =
		// MonitorFactory.start(
		// "model.data-access.insert." + _commandString.toLowerCase());
		try {
			this.validate();
			this.closeInternal();
			_stmt = getInternalConnection().createStatement();
			int rowsAffected = _stmt.executeUpdate(_commandString);
			DataResultFactory dataResultFactory = new DefaultDataResultFactory();
			DataResultInterface internalDataResult = dataResultFactory.createInformationDataResult(this, null,
					InformationDataResult.CORRECT_EXECUTION, rowsAffected,
					" CORRECT EXECUTION : " + rowsAffected + " ROWS AFFECTED");
			return new DataResult(internalDataResult, DataResultInterface.INFORMATION_DATA_RESULT);
		} // try
		catch (SQLException sqle) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "InsertCommand::execute:", sqle);

			String method = "InsertCommand :: execute() :: ";
			throw Utils.generateInternalError(sqle, method);
		} // catch (SQLException sqle) try
			// finally {
			// monitor.stop();
			// } // finally
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
		// Monitor monitor =
		// MonitorFactory.start(
		// "model.data-access.inser." + _commandString.toLowerCase());
		try {
			this.validate();
			this.closeInternal();
			_stmt = getInternalConnection().prepareStatement(_commandString);
			DataField inputParameterDataField = null;
			for (int i = 0; i < inputParameters.size(); i++) {
				inputParameterDataField = (DataField) inputParameters.get(i);
				_logger.debug("inputParameters[" + (i + 1) + "] " + inputParameterDataField.getObjectValue());

				((PreparedStatement) _stmt).setObject(i + 1, inputParameterDataField.getObjectValue(),
						inputParameterDataField.getSqlType());
			} // end for
			int rowsAffected = ((PreparedStatement) _stmt).executeUpdate();
			DataResultFactory dataResultFactory = new DefaultDataResultFactory();
			DataResultInterface internalDataResult = dataResultFactory.createInformationDataResult(this,
					inputParameters, InformationDataResult.CORRECT_EXECUTION, rowsAffected,
					" CORRECT EXECUTION : " + rowsAffected + " ROWS AFFECTED");
			return new DataResult(internalDataResult, DataResultInterface.INFORMATION_DATA_RESULT);
		} // try
		catch (SQLException sqle) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "InsertCommand::execute:", sqle);

			String method = "InsertCommand :: execute(inputParameters) :: ";
			throw Utils.generateInternalError(sqle, method);
		} // catch (SQLException sqle) try
			// finally {
			// monitor.stop();
			// } // finally
	} // public DataResult execute(List inputParameters) throws
		// EMFInternalError
} // public class InsertCommand extends SQLCommand
