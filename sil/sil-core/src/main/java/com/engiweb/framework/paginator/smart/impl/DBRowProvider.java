package com.engiweb.framework.paginator.smart.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.paginator.smart.AbstractRowProvider;
import com.engiweb.framework.util.ContextScooping;

/**
 * La classe <code>DBRowProvider</code> estende <code>AbstractRowProvider</code> ed implementa i metodi di accesso alle
 * sorgenti di dati riconducibili al pacchetto dbaccess del framework.
 * <p>
 * 
 * @version 1.0, 15/03/2003
 * @see AbstractRowProvider
 */
public class DBRowProvider extends AbstractRowProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DBRowProvider.class.getName());
	private DataConnection _dataConnection = null;
	private SQLCommand _sqlCommand = null;
	private DataResult _dataResult = null;

	/**
	 * Costruisce un <code>DBRowProvider</code>. Questo costruttore &egrave; vuoto ed ogni azione di inizializzazione
	 * &egrave; demandata al metodo <code>init(SourceBean config)</code>.
	 * <p>
	 * 
	 * @see DBRowProvider#init(SourceBean)
	 */
	public DBRowProvider() {
		super();
		_dataConnection = null;
		_sqlCommand = null;
		_dataResult = null;
	} // public DBRowProvider()

	/**
	 * Questo metodo apre la connessione alla sorgente dati e recupera tutte le righe relative alla query configurata.
	 * <p>
	 * 
	 * @see DBRowProvider#init(SourceBean)
	 */
	public void open() {
		_logger.debug("DBRowProvider::open: invocato");

		super.open();
		SourceBean config = getConfig();
		SourceBean query = (SourceBean) config.getAttribute("LIST_QUERY");
		String pool = (String) config.getAttribute("POOL");
		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			_dataConnection = dataConnectionManager.getConnection(pool);

			// Modifiche Monica 21/01/2004 - inizio
			String stmt = (String) query.getAttribute("STATEMENT");
			String statement = null;
			// cerco attributo class se statement non è valorizzato
			if ((stmt == null) || stmt.equals("")) {
				String className = (String) query.getAttribute("CLASS");
				if ((className == null) || className.equals("")) {
					_logger.warn("DBRowProvider::open: statement non censito");

					return;
				} // if ((className == null) || className.equals(""))
				IFaceQueryProvider queryProvider = null;
				try {
					queryProvider = (IFaceQueryProvider) Class.forName(className).newInstance();
				} // try
				catch (Exception ex) {
					it.eng.sil.util.TraceWrapper.fatal(_logger, "DBRowProvider::open: classe query provider non valida",
							ex);

				} // catch (Exception ex) try
				statement = queryProvider.getQuery(this);
			} // if((stmt == null) || stmt.equals(""))
			else {
				statement = SQLStatements.getStatement(stmt);
			} // else

			// Modifiche Monica 21/01/2004 - fine
			_sqlCommand = _dataConnection.createSelectCommand(statement);
			ArrayList inputParameters = new ArrayList();
			Vector parameters = query.getAttributeAsVector("PARAMETER");
			for (int i = 0; i < parameters.size(); i++) {
				SourceBean parameter = (SourceBean) parameters.elementAt(i);
				String parameterType = (String) parameter.getAttribute("TYPE");
				String parameterValue = (String) parameter.getAttribute("VALUE");
				String parameterScope = (String) parameter.getAttribute("SCOPE");
				String inParameterValue = null;
				if (parameterType.equalsIgnoreCase("ABSOLUTE"))
					inParameterValue = parameterValue;
				else {
					Object parameterValueObject = ContextScooping.getScopedParameter(getRequestContainer(),
							getResponseContainer(), parameterValue, parameterScope);
					if (parameterValueObject != null)
						inParameterValue = parameterValueObject.toString();
				} // if (parameterType.equalsIgnoreCase("ABSOLUTE")) else
				if (inParameterValue == null)
					inParameterValue = "";
				inputParameters.add(_dataConnection.createDataField("", Types.VARCHAR, inParameterValue));
			} // for (int i = 0; i < fields.size(); i++)
			_dataResult = _sqlCommand.execute(inputParameters);
			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) _dataResult.getDataObject();
			setRows(scrollableDataResult.getRowsNumber());
			setCurrentRow(0);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DBRowProvider::executeQuery:", ex);

			Utils.releaseResources(_dataConnection, _sqlCommand, _dataResult);
			_dataConnection = null;
			_sqlCommand = null;
			_dataResult = null;
		} // catch (Exception ex)
	} // public void open()

	/**
	 * Questo metodo serve per posizionare il <em>cursore</em> di lettura alla riga individuata da <em>row</em>.
	 * <p>
	 * 
	 * @param row
	 *            <code>int</code> il numero della riga su cui posizionarsi.
	 */
	public void absolute(int row) {
		_logger.debug("DBRowProvider::absolute: invocato");

		if (_dataResult == null) {
			_logger.warn("DBRowProvider::absolute: row provider non aperto correttamente !");

			return;
		} // if (_dataResult == null)
		if (row < 1)
			row = 1;
		else if (row > rows())
			row = rows();
		setCurrentRow(row - 1);
		ScrollableDataResult scrollableDataResult = (ScrollableDataResult) _dataResult.getDataObject();
		try {
			scrollableDataResult.moveTo(row);
		} // try
		catch (EMFInternalError emfie) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DBRowProvider::absolute:", (Exception) emfie);

		} // catch (EMFInternalError emfie)
	} // public void absolute(int row)

	/**
	 * Ritorna un <code>Object</code> che rappresenta la riga individuata da <em>row</em>.
	 * <p>
	 * 
	 * @return <code>Object</code> la riga individuata da <em>row</em>.
	 * @param row
	 *            <code>int</code> il numero della riga da ritornare.
	 */
	public Object getRow(int row) {
		_logger.debug("DBRowProvider::getRow: invocato");

		if (_dataResult == null) {
			_logger.warn("DBRowProvider::getRow: row provider non aperto correttamente !");

			return null;
		} // if (_dataResult == null)
		if (row < 1)
			row = 1;
		else if (row > rows())
			row = rows();
		setCurrentRow(row);
		ScrollableDataResult scrollableDataResult = (ScrollableDataResult) _dataResult.getDataObject();
		SourceBean rowBean = null;
		try {
			rowBean = scrollableDataResult.getDataRow(row).getSourceBean();
		} // try
		catch (EMFInternalError emfie) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DBRowProvider::getRow:", (Exception) emfie);

		} // catch (EMFInternalError emfie)
		return rowBean;
	} // public Object getRow(int row)

	/**
	 * Ritorna un <code>Object</code> che rappresenta la riga successiva all'ultima recuperata o individuata con il
	 * comando <code>absolute(int)
	 * </code>.
	 * <p>
	 * 
	 * @return <code>Object</code> la riga individuata da <em>row</em>.
	 * @param row
	 *            <code>int</code> il numero della riga da ritornare.
	 */
	public Object getNextRow() {
		_logger.debug("DBRowProvider::getNextRow: invocato");

		if (_dataResult == null) {
			_logger.warn("DBRowProvider::getNextRow: row provider non aperto correttamente !");

			return null;
		} // if (_dataResult == null)
		ScrollableDataResult scrollableDataResult = (ScrollableDataResult) _dataResult.getDataObject();
		SourceBean rowBean = null;
		try {
			if (scrollableDataResult.hasRows()) {
				rowBean = scrollableDataResult.getDataRow().getSourceBean();
				setCurrentRow(getCurrentRow() + 1);
			} // if (scrollableDataResult.hasRows())
		} // try
		catch (EMFInternalError emfie) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "DBRowProvider::getNextRow:", (Exception) emfie);

		} // catch (EMFInternalError emfie)
		return rowBean;
	} // public Object getNextRow()

	/**
	 * Questo metodo chiude la connessione alla sorgente dati.
	 * <p>
	 * 
	 * @see DBRowProvider#init(SourceBean)
	 */
	public void close() {
		_logger.debug("DBRowProvider::close: invocato");

		Utils.releaseResources(_dataConnection, _sqlCommand, _dataResult);
		_dataConnection = null;
		_sqlCommand = null;
		_dataResult = null;
		setRows(0);
		setCurrentRow(0);
	} // public void close()
} // public abstract class AbstractRowProvider implements IFacePageProvider
