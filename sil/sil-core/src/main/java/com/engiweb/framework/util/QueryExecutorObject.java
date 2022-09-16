/*
 * Creato il 8-set-04
 * Author: vuoto
 * 
 */
package com.engiweb.framework.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TableDescriptor;

/**
 * @author vuoto
 * 
 */
public class QueryExecutorObject {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(QueryExecutorObject.class.getName());

	public static final String UPDATE = "UPDATE";
	public static final String DELETE = "DELETE";
	public static final String SELECT = "SELECT";
	public static final String INSERT = "INSERT";

	private RequestContainer requestContainer = null;
	private ResponseContainer responseContainer = null;
	private DataConnection dataConnection = null;
	private SQLCommand sqlCommand = null;
	private SQLCommand sqlLogCommand = null;
	private DataResult dataResult = null;
	private String statement = null;
	private String type = null;
	private List inputParameters = null;
	private boolean isTransactional = false; // FALSE=fa commit/rollback e
												// rilascia la connessione
	private boolean dontForgetException = false;

	private SourceBean query = null;
	private String tokenStatement = null;

	public QueryExecutorObject() {

	}

	public Object exec() {
		// Monitor monitor = null;
		// String monitorToken = null;
		Object result = null;
		// Savino 01/09/05 spostato blocco try. initialize() lancia una
		// eccezione se query e statement sono null
		try {
			result = execWithException();
		} catch (Exception ex) {

			it.eng.sil.util.TraceWrapper.error(_logger, "QueryExecutor::executeQuery:", (Exception) ex);

			_logger.error(toString());

			if (dontForgetException)
				result = ex;

			try {
				// Se non transazionale, fa il rollback
				if (!isTransactional)
					dataConnection.rollBackTransaction();
			} catch (EMFInternalError e) {

				_logger.error("QueryExecutor::executeQuery: errore nel rollback");

			}
		} finally {
			Utils.releaseResources(null, sqlCommand, dataResult);
			Utils.releaseResources(null, sqlLogCommand, null);
			// Se non transazionale, rilascia anche la connessione
			if (!isTransactional) {
				Utils.releaseResources(dataConnection, null, null);
			}
			// monitor.stop();

		}
		return result;
	}

	protected Object execWithException() throws EMFInternalError, EMFUserError {
		Object result;

		initialize();
		// String statementProviderClassName = null;
		if (statement == null) {

			if (query != null) {
				tokenStatement = (String) query.getAttribute("STATEMENT");
				// monitorToken = tokenStatement;

			}

			if (tokenStatement != null) {
				statement = SQLStatements.getStatement(tokenStatement);
			}
		}
		// else {
		// monitorToken = "QUERY_HASH_" + statement.hashCode();
		// }

		// monitor = MonitorFactory.start(monitorToken);

		// Savino 24/08/2005: controllo che lo statement non sia vuoto;
		// in questo caso e' inutile proseguire.
		if (statement == null || statement.equals(""))
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "Lo statement e' vuoto: operazione interrotta. "
					+ "QUERY_XML=" + query + ", STATEMENT=" + tokenStatement);
		result = doExec();

		return result;
	}

	private Object doExec() throws EMFInternalError, EMFUserError {

		Object result = null;
		boolean mustLog = false;
		String logStatement = null;
		List logInputParameters = new ArrayList();

		if (type.equalsIgnoreCase(INSERT))
			sqlCommand = dataConnection.createInsertCommand(statement);

		else if (type.equalsIgnoreCase(UPDATE)) {
			// ******* UPDATE *****************************************
			sqlCommand = dataConnection.createUpdateCommand(statement);
			// System.out.println("SQL> " + statement);

			// Recupera nome tabella dallo statement
			statement = statement.trim();
			String tmpNomeTab = statement.substring(7).trim();
			int idxPrimoSpazio = tmpNomeTab.indexOf(" ");
			String nomeTab = tmpNomeTab.substring(0, idxPrimoSpazio);

			// Costruisce l'SQL per il log
			Connection conn = dataConnection.getInternalConnection();
			TableDescriptor tab = new TableDescriptor(conn, nomeTab.toUpperCase());

			if (tab.loadCols()) {
				mustLog = true;

				int idxWhere = statement.toUpperCase().indexOf("WHERE");
				String parteWhere = statement.substring(idxWhere);

				int cdnUtente = -1;
				requestContainer = RequestContainer.getRequestContainer();
				if (requestContainer != null) {
					SessionContainer session = requestContainer.getSessionContainer();
					if (session != null) {
						BigDecimal cdnUtenteObj = (BigDecimal) session.getAttribute("_CDUT_");
						if (cdnUtenteObj != null)
							cdnUtente = cdnUtenteObj.intValue();
					}

				}

				logStatement = tab.getInsertIntoLogTabForUpdate(parteWhere, cdnUtente);

				if (inputParameters == null) {
					inputParameters = new ArrayList(0);
					logInputParameters = inputParameters;
				} else {

					// contiamo quanti paraemtri prende la parte di where
					int quantiPars = 0;
					for (int i = 0; i < logStatement.length(); i++) {
						if (logStatement.charAt(i) == '?')
							quantiPars++;
					}

					int sizeIP = inputParameters.size();
					for (int i = 0; i < quantiPars; i++) {
						DataField valore = (DataField) inputParameters.get(sizeIP - quantiPars + i);
						logInputParameters.add(valore);
					}
				}
			}

		} else if (type.equalsIgnoreCase("DELETE")) {

			// ******* DELETE *****************************************
			sqlCommand = dataConnection.createDeleteCommand(statement);
			// System.out.println("SQL> " + statement);

			// Recupera nome tabella dallo statement
			statement = statement.trim();
			int idxDopoFrom = statement.toUpperCase().indexOf("FROM");
			String tmpNomeTab = statement.substring(idxDopoFrom + 5).trim();
			int idxPrimoSpazio = tmpNomeTab.indexOf(" ");
			String nomeTab = tmpNomeTab.substring(0, idxPrimoSpazio);

			// Costruisce l'SQL per il log
			Connection conn = dataConnection.getInternalConnection();
			TableDescriptor tab = new TableDescriptor(conn, nomeTab.toUpperCase());

			if (tab.loadCols()) {
				mustLog = true;

				int idxWhere = statement.toUpperCase().indexOf("WHERE");
				String parteWhere = statement.substring(idxWhere);

				// Recupero del cdnUtente del log
				int cdnUtente = -1;
				requestContainer = RequestContainer.getRequestContainer();
				if (requestContainer != null) {
					SessionContainer session = requestContainer.getSessionContainer();
					if (session != null) {
						BigDecimal cdnUtenteObj = (BigDecimal) session.getAttribute("_CDUT_");
						if (cdnUtenteObj != null)
							cdnUtente = cdnUtenteObj.intValue();
					}

				}
				logStatement = tab.getInsertIntoLogTabForDelete(parteWhere, cdnUtente);

				if (inputParameters == null)
					inputParameters = new ArrayList(0);

				logInputParameters = inputParameters;

			}

		} else {
			mustLog = false;

			sqlCommand = dataConnection.createSelectCommand(statement);
		}

		if (_logger.isDebugEnabled())
			_logger.debug(toString());

		if (mustLog) {
			if (!isTransactional)
				dataConnection.initTransaction();
			sqlLogCommand = dataConnection.createInsertCommand(logStatement);

			try {

				_logger.debug("QueryExecutorObject::doExec:  **** Esecuzione QUERY DI LOG (INSERT)****");

				dataResult = sqlLogCommand.execute(logInputParameters);

			} catch (EMFInternalError ex) {

				it.eng.sil.util.TraceWrapper.error(_logger,
						"QueryExecutorObject::doExec: errore nella scrittura della tabella di log.\r\n" + toString(),
						(Exception) ex);

				EMFUserError errUser = new EMFUserError(EMFErrorSeverity.ERROR,
						MessageCodes.General.ERR_IN_WRITING_LOG);
				if (responseContainer != null) {
					responseContainer.getErrorHandler().addError(errUser);
				}

				throw errUser;

			}

			_logger.debug("QueryExecutorObject::doExec:  **** Esecuzione QUERY ****");

			dataResult = sqlCommand.execute(inputParameters);

			// Se non transazionale, fa il commit (ho scritto nel LOG!)
			if (!isTransactional)
				dataConnection.commitTransaction();

		} else {

			dataResult = sqlCommand.execute(inputParameters);

		}

		if (type.equalsIgnoreCase("SELECT")) {
			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			result = scrollableDataResult.getSourceBean();
		} else {
			// InformationDataResult informationDataResult = (InformationDataResult) dataResult.getDataObject();
			// FV 26-01-2005
			// Si ritorna true ANCHE quando l'update non aggiorna nessuna
			// riga!!!
			// result = new Boolean(informationDataResult.getAffectedRows() >
			// 0);
			result = Boolean.TRUE;
		}

		return result;

	}

	private void recuperaPars() {

		inputParameters = new ArrayList();
		// se e' presente uno statement i parametri non vanno recuperati.
		// L'Array inputParameters risultera' vuoto.
		if (statement != null && query == null)
			return;
		// se sono presenti sia lo statement che la query allora siamo in
		// presenza del "mergeOnSelect"
		// si recuperano i parametri dai container
		Vector parameters = query.getAttributeAsVector("PARAMETER");
		for (int i = 0; parameters != null && i < parameters.size(); i++) {
			SourceBean parameter = (SourceBean) parameters.elementAt(i);
			String parameterType = (String) parameter.getAttribute("TYPE");
			String parameterValue = (String) parameter.getAttribute("VALUE");
			String parameterScope = (String) parameter.getAttribute("SCOPE");
			String inParameterValue = null;
			if (parameterType.equalsIgnoreCase("ABSOLUTE"))
				inParameterValue = parameterValue;
			else {
				Object parameterValueObject = ContextScooping.getScopedParameter(requestContainer, responseContainer,
						parameterValue, parameterScope);
				if (parameterValueObject != null)
					inParameterValue = parameterValueObject.toString();
			} // if (parameterType.equalsIgnoreCase("ABSOLUTE")) else
			if (inParameterValue == null)
				inParameterValue = "";
			inputParameters.add(dataConnection.createDataField("", Types.VARCHAR, inParameterValue));
		} // for (int i = 0; i < fields.size(); i++)

		// return inputParameters;
	}

	/**
	 * @return
	 */
	public DataConnection getDataConnection() {
		return dataConnection;
	}

	/**
	 * @return
	 */
	public DataResult getDataResult() {
		return dataResult;
	}

	/**
	 * @return
	 */
	public List getInputParameters() {
		return inputParameters;
	}

	/**
	 * @return
	 */
	public boolean isTransactional() {
		return isTransactional;
	}

	/**
	 * @return
	 */
	public RequestContainer getRequestContainer() {
		return requestContainer;
	}

	/**
	 * @return
	 */
	public ResponseContainer getResponseContainer() {
		return responseContainer;
	}

	/**
	 * @return
	 */
	public String getStatement() {
		return statement;
	}

	/**
	 * @return
	 */
	public String getTokenStatement() {
		return tokenStatement;
	}

	/**
	 * @return
	 */

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param connection.
	 *            Se viene passata una connessione nulla verra' lanciata una eccezione da initialize()
	 */
	public void setDataConnection(DataConnection connection) {
		dataConnection = connection;
	}

	/**
	 * @param result
	 */
	public void setDataResult(DataResult result) {
		dataResult = result;
	}

	/**
	 * @param list
	 */
	public void setInputParameters(List list) {
		inputParameters = list;
	}

	/**
	 * @param b
	 */
	public void setTransactional(boolean b) {
		isTransactional = b;
	}

	/**
	 * @param container
	 */
	public void setRequestContainer(RequestContainer container) {
		requestContainer = container;
	}

	/**
	 * @param container
	 */
	public void setResponseContainer(ResponseContainer container) {
		responseContainer = container;
	}

	/**
	 * @param string
	 */
	public void setStatement(String string) {
		statement = string;
	}

	/**
	 * @param string
	 */
	public void setTokenStatement(String string) {
		tokenStatement = string;
	}

	/**
	 * @param bean
	 */

	/**
	 * @param string
	 */
	public void setType(String string) {
		type = string;
	}

	/**
	 * @return
	 */
	public boolean isDontForgetException() {
		return dontForgetException;
	}

	/**
	 * @param b
	 */
	public void setDontForgetException(boolean b) {
		dontForgetException = b;
	}

	/**
	 * @return
	 */
	public SourceBean getQuery() {
		return query;
	}

	/**
	 * @param bean
	 */
	public void setQuery(SourceBean bean) {
		query = bean;
		// Savino 01/09/05 il recupero dei parametri e' stato spostato nel
		// metodo initialize().
		// recuperaPars();

	}

	/**
	 * Controlla che i parametri necessari ci siano. Inizializza l'Array dei parametri di input.
	 * 
	 * @throws EMFInternalError
	 *             se la connessione e' nulla, se mancano sia lo statement da eseguire che il SourceBean della query
	 */
	private void initialize() throws EMFInternalError {
		// 1.
		if (dataConnection == null)
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "DataConnection is null");
		// 2. i parametri di input sono stati passati con metodo relativo: non
		// bisogna recuperarli dal RequestContainer
		if (inputParameters != null)
			return;
		// 3. lo statement da eseguire o il SourceBean della query non possono
		// essere contemporaneamente nulli
		if (statement == null && query == null)
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
					"Mancano sia lo statement da eseguire che il SourceBean della query ");
		// 4. almeno una delle due variabili e' stata valorizzata. Si procede
		// con il recupero dei parametri
		recuperaPars();
	}

	public String toString() {

		StringBuffer buf = new StringBuffer();

		buf.append("\r\n*** DUMP DEL QUERY EXECUTOR OBJECT ***\r\n");
		if (tokenStatement != null) {
			buf.append("TOKEN-STATEMENT: " + tokenStatement + "\r\n");
		}

		if (type.equalsIgnoreCase("DELETE") || type.equalsIgnoreCase("UPDATE")) {
			buf.append("TRANSAZIONALE: " + Boolean.valueOf(isTransactional).toString() + "\r\n");
		}

		if (statement != null)
			buf.append("STATEMENT: " + statement + "\r\n");

		if (inputParameters != null) {
			buf.append("PARAMETRI:\r\n");
			int kk = 1;
			for (Object object : inputParameters) {
				buf.append("\tpar[ " + (kk++) + "]=" + ((DataField) object).getStringValue() + "\r\n");
			}
		}
		return buf.toString();

	}

}