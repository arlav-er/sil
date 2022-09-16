package com.engiweb.framework.dbaccess.sql.command.std;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.engiweb.framework.error.EMFInternalError;

/**
 * Questa Classe rappresenta un comando SQL di SELECT per driver jdbc 2.0 o superiori
 * 
 * @author Andrea Zoppello - andrea.zoppello@engiweb.com
 * @version 1.0
 */
public class SelectCommand extends SQLCommand {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SelectCommand.class.getName());
	private boolean _scroll = true;

	/**
	 * Costruttore
	 * 
	 * @param <B>DataConnection
	 *            </B> dataConnection - l'oggetto dataConnection legato al comando
	 * @param <B>String
	 *            </B> commandString - La Stringa rappresentante il comando
	 */
	public SelectCommand(DataConnection dataConnection, String commandString, boolean scroll) {
		super(dataConnection, commandString);
		_scroll = scroll;
	} // public SelectCommand(DataConnection dataConnection, String
		// commandString, boolean scroll)

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
		// "model.data-access.select." + _commandString.toLowerCase());
		try {
			this.validate();
			this.closeInternal();
			DataResultFactory dataResultFactory = new DefaultDataResultFactory();
			if (_scroll)
				_stmt = getInternalConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
			else
				_stmt = getInternalConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
			_logger.debug("Command String : " + _commandString);

			ResultSet rs = _stmt.executeQuery(_commandString);

			/*
			 * _stmt = getInternalConnection().prepareStatement(_commandString, ResultSet.TYPE_SCROLL_SENSITIVE,
			 * ResultSet.CONCUR_READ_ONLY); ResultSet rs = ((PreparedStatement)_stmt).executeQuery(); ResultSetMetaData
			 * rsMetaData = rs.getMetaData(); _tracer.log("execute",_tracer.INFORMATION,"rmetadata :" +
			 * rsMetaData.getColumnCount());
			 */

			DataResultInterface internalDataResult = null;
			if (rs.getType() == ResultSet.TYPE_FORWARD_ONLY)
				internalDataResult = dataResultFactory.createScrollableDataResult(this, null, rs, false);
			else
				internalDataResult = dataResultFactory.createScrollableDataResult(this, null, rs, true);
			return new DataResult(internalDataResult, DataResultInterface.SCROLLABLE_DATA_RESULT);
		} // try
		catch (SQLException sqle) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "SelectCommand::execute:", sqle);

			String method = "SelectCommand :: execute() :: ";
			throw Utils.generateInternalError(sqle, method);
		} // catch (SQLException sqle)
			// finally {
			// monitor.stop();
			// } // finally
	} // public DataResult execute() throws EMFInternalError

	/**
	 * Questo comando serve per eseguire il comando con dei parametri di input
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
		// "model.data-access.select." + _commandString.toLowerCase());
		try {
			this.validate();
			this.closeInternal();
			if (_scroll)
				_stmt = getInternalConnection().prepareStatement(_commandString, ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
			else
				_stmt = getInternalConnection().prepareStatement(_commandString, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
			_logger.debug("Command String : " + _commandString);

			DataField inputParameterDataField = null;
			for (int i = 0; i < inputParameters.size(); i++) {
				inputParameterDataField = (DataField) inputParameters.get(i);
				_logger.debug("inputParameters[" + (i + 1) + "] " + inputParameterDataField.getObjectValue());

				((PreparedStatement) _stmt).setObject(i + 1, inputParameterDataField.getObjectValue(),
						inputParameterDataField.getSqlType());
			} // end for
			ResultSet rs = ((PreparedStatement) _stmt).executeQuery();
			DataResultFactory dataResultFactory = new DefaultDataResultFactory();
			DataResultInterface internalDataResult = null;
			if (rs.getType() == ResultSet.TYPE_FORWARD_ONLY)
				internalDataResult = dataResultFactory.createScrollableDataResult(this, inputParameters, rs, false);
			else
				internalDataResult = dataResultFactory.createScrollableDataResult(this, inputParameters, rs, true);
			return new DataResult(internalDataResult, DataResultInterface.SCROLLABLE_DATA_RESULT);
		} // try
		catch (SQLException sqle) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "SelectCommand::execute:", sqle);

			String method = "SelectCommand :: execute(inputParameters) :: ";
			throw Utils.generateInternalError(sqle, method);
		} // catch (SQLException sqle) try
			// finally {
			// monitor.stop();
			// } // finally
	} // public DataResult execute(List inputParameters) throws
		// EMFInternalError
} // public class SelectCommand extends SQLCommand
