package com.engiweb.framework.util;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.module.patto.SelectMerge;

/**
 * QueryExecutor
 * 
 * NOTA: non viene più fatta la chiusura della connessione aperta poiché ci pensa il QueryExecutorObject a farla.
 * 
 * (versione modificata e ottimizzata da Luigi Antenucci).
 */
public abstract class QueryExecutor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(QueryExecutor.class.getName());
	public static final String CREATE = "CREATE";
	public static final String READ = "READ";
	public static final String UPDATE = "UPDATE";
	public static final String DELETE = "DELETE";

	/**
	 * INPUT: DataConnection dataConnection Nota: se isTransactional=false, la connessione viene rilasciata a fine
	 * operazione.
	 */
	public static Object executeQuery(RequestContainer requestContainer, ResponseContainer responseContainer,
			DataConnection dataConnection, SourceBean query, String type) {
		return executeQuery(requestContainer, responseContainer, dataConnection, query, type, false, false);
	}

	public static Object executeQuery(RequestContainer requestContainer, ResponseContainer responseContainer,
			DataConnection dataConnection, SourceBean query, String type, boolean isTransactional,
			boolean dontForgetException) {

		QueryExecutorObject queryExecObj = new QueryExecutorObject();

		queryExecObj.setRequestContainer(requestContainer);
		queryExecObj.setResponseContainer(responseContainer);
		queryExecObj.setDataConnection(dataConnection);
		queryExecObj.setQuery(query);
		queryExecObj.setType(type);

		queryExecObj.setTransactional(isTransactional);
		queryExecObj.setDontForgetException(dontForgetException);

		return queryExecObj.exec();
		// original return executeQuery(requestContainer, responseContainer,
		// dataConnection, query, type, false, false);
	}

	/**
	 * INPUT: String pool Nota: manca isTransactional poiché la connessione viene aperta all'interno del metodo.
	 */
	public static Object executeQuery(RequestContainer requestContainer, ResponseContainer responseContainer,
			String pool, SourceBean query, String type) {
		return executeQuery(requestContainer, responseContainer, pool, query, type, false);
	}

	public static Object executeQuery(RequestContainer requestContainer, ResponseContainer responseContainer,
			String pool, SourceBean query, String type, boolean dontForgetException) {

		Object returned;
		DataConnection dataConnection = null;

		try {
			dataConnection = DataConnectionManager.getInstance().getConnection(pool);

			returned = executeQuery(requestContainer, responseContainer, dataConnection, query, type, false,
					dontForgetException);

		} catch (EMFInternalError e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"QueryExecutor::executeQuery: errore nel reperimento della connessione", (Exception) e);

			responseContainer.getErrorHandler()
					.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.ERR_NO_DB_CONNECTION));

			returned = Boolean.FALSE;
		} finally {
			// Utils.releaseResources(dataConnection, null, null);
			// GG: si noti che *non* rilascio la connessione!
			// Lo ha già fatto il "executeQuery()".
		}
		return returned;
	}

	/* Il metodo "recuperaPars()" è ora nell'oggetto QueryExecutorObject */

	/**
	 * Variante 1 INPUT: String pool, ma anche Object[] fieldWhere (e firma diversa)
	 */
	public static Object executeQuery(String stmName, Object[] fieldWhere, String type, String pool) {

		Object returned;
		DataConnection dataConnection = null;

		try {
			dataConnection = DataConnectionManager.getInstance().getConnection(pool);

			String statement = SQLStatements.getStatement(stmName);

			List inputParameters;
			if (fieldWhere == null) {
				inputParameters = Collections.EMPTY_LIST;
			} else {
				inputParameters = new ArrayList(fieldWhere.length);
				for (int i = 0; i < fieldWhere.length; i++) {
					setQueryParameter("", inputParameters, fieldWhere[i], dataConnection);
				}
			}

			QueryExecutorObject queryExecObj = new QueryExecutorObject();

			queryExecObj.setRequestContainer(null);
			queryExecObj.setResponseContainer(null);
			queryExecObj.setDataConnection(dataConnection);
			queryExecObj.setInputParameters(inputParameters);
			queryExecObj.setType(type);
			queryExecObj.setStatement(statement);

			queryExecObj.setTransactional(false);
			queryExecObj.setDontForgetException(false);

			returned = queryExecObj.exec();

		} catch (EMFInternalError e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"QueryExecutor::executeQuery: errore nel reperimento della connessione", (Exception) e);

			returned = Boolean.FALSE;
		} finally {
			// Utils.releaseResources(dataConnection, null, null);
			// GG: si noti che *non* rilascio la connessione!
			// Lo ha già fatto il "queryExecObj.exec()".
		}
		return returned;
	}

	/**
	 * Variante 2 INPUT: String pool, ma anche SelectMerge merger
	 * 
	 * NB: SelectMerge è una classe del pacchetto "it.eng.sil.module.patto".
	 */
	public static Object executeQuery(RequestContainer requestContainer, ResponseContainer responseContainer,
			String pool, SourceBean query, SelectMerge merger, String type) {

		Object returned;
		DataConnection dataConnection = null;

		try {
			dataConnection = DataConnectionManager.getInstance().getConnection(pool);

			String tokenStatement = (String) query.getAttribute("STATEMENT");
			// C'era anche questo: com.jamonapi.Monitor monitor =
			// com.jamonapi.MonitorFactory.start("QueryExecutor::executeQuery[2]:"
			// + tokenStatement);

			String statement = SQLStatements.getStatement(tokenStatement);
			statement = merger.merge(statement);

			QueryExecutorObject queryExecObj = new QueryExecutorObject();

			queryExecObj.setRequestContainer(requestContainer);
			queryExecObj.setResponseContainer(responseContainer);
			queryExecObj.setDataConnection(dataConnection);
			queryExecObj.setQuery(query);
			queryExecObj.setStatement(statement);

			queryExecObj.setType(type);

			queryExecObj.setTransactional(false);
			queryExecObj.setDontForgetException(false);

			returned = queryExecObj.exec();

		} catch (EMFInternalError e1) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"QueryExecutor::executeQuery: errore nel reperimento della connessione", (Exception) e1);

			returned = Boolean.FALSE;
		} catch (Exception e2) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"QueryExecutor::executeQuery: errore durante il 'merge' della query", e2);

			returned = Boolean.FALSE;
		} finally {
			// Utils.releaseResources(dataConnection, null, null);
			// Idem, non rilascio la connessione perché è già stato fatto.
		}
		return returned;
	}

	/**
	 * interna
	 */
	private static void setQueryParameter(String fieldname, List parameter, Object field, DataConnection dc) {
		if (fieldname == null) {
			fieldname = "";
		}

		if (field instanceof String) {
			parameter.add(dc.createDataField(fieldname, Types.VARCHAR, field));
		} else if (field instanceof Integer) {
			parameter.add(dc.createDataField(fieldname, Types.INTEGER, field));
		} else if (field instanceof Double) {
			parameter.add(dc.createDataField(fieldname, Types.DOUBLE, field));
		} else if (field instanceof Float) {
			parameter.add(dc.createDataField(fieldname, Types.FLOAT, field));
		} else if (field instanceof Character) {
			parameter.add(dc.createDataField(fieldname, Types.CHAR, field));
		} else if (field instanceof BigDecimal) {
			parameter.add(dc.createDataField(fieldname, Types.NUMERIC, field));
		} else if (field instanceof Long) {
			parameter.add(dc.createDataField(fieldname, Types.NUMERIC, field));
		} else if (field instanceof Object) {
			if (field == null) {
				parameter.add(dc.createDataField(fieldname, Types.VARCHAR, field));
			} else {
				parameter.add(dc.createDataField(fieldname, Types.VARCHAR, field.toString()));
			}
		} else {
			parameter.add(dc.createDataField(fieldname, Types.VARCHAR, field));
		}
	}

	/*
	 * +++++++++++++++++++++++++++++++++ Metodi non usati. +++++++++++++++++++++++++++++++++ Vengono lasciati e svuotati
	 * al solo fine di non rompere la compatibilità con alcuni packages del framework.
	 */

	public static SourceBean executeProfilingQuery(String queryType, String query, ArrayList parameters)
			throws Exception {

		throw new UnsupportedOperationException();
		// GG: preferisco lanciare eccezione per dire che il metodo non è
		// supportato
		// piuttosto che rendere 'null' come veniva fatto in origine.
	}

	public static SourceBean executeProfilingQuery(DataConnection dataConnection, String queryType, String query,
			ArrayList parameters) throws Exception {

		throw new UnsupportedOperationException();
	}

}