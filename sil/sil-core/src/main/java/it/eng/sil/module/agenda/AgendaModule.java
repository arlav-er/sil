package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

public class AgendaModule extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AgendaModule.class.getName());
	private String className = this.getClass().getName();
	private boolean insertOK;

	public AgendaModule() {
	}

	/**
	 * Dispatcher delle azioni del modulo. La logica è guidata dall'attributo MESSAGE che viene reperito dalla request
	 */
	public void service(SourceBean request, SourceBean response) {
		/*
		 * Fv Da rifare
		 */

		/*
		 * if ((request == null) || (response == null)) { _logger.warn( className+"::service: parametri non validi");
		 * 
		 * return; } // if ((request == null) || (response == null)) it.eng.sil.util.TraceWrapper.debug(
		 * _logger,className+"::service: request", request);
		 * 
		 * String message = getMessage(request); if ((message == null) || (message.length() == 0) ||
		 * message.equalsIgnoreCase("BEGIN")) message = DETAIL_NEW;
		 * 
		 * String moduleMode = MODULE_MODE_INSERT;
		 * 
		 * if (message.equalsIgnoreCase(DETAIL_INSERT)){ insertOK=true; insert(request, response);
		 * 
		 * if(insertOK){ moduleMode = MODULE_MODE_UPDATE; }else{ moduleMode = MODULE_MODE_INSERT; } } else if
		 * (message.equalsIgnoreCase(DETAIL_SELECT)) { select(request, response); moduleMode = MODULE_MODE_UPDATE; } //
		 * if (message.equalsIgnoreCase(DETAIL_SELECT)) else if (message.equalsIgnoreCase(DETAIL_UPDATE)) {
		 * update(request, response); moduleMode = MODULE_MODE_UPDATE; } // if (message.equalsIgnoreCase(DETAIL_UPDATE))
		 * else if (message.equalsIgnoreCase(DETAIL_DELETE)) { delete(request, response); moduleMode =
		 * MODULE_MODE_DELETE; }// if (message.equalsIgnoreCase(DETAIL_DELETE))
		 * 
		 * try { response.setAttribute(MODULE_MODE, moduleMode); } // try catch (Exception ex) {
		 * it.eng.sil.util.TraceWrapper.fatal( _logger,className+"::service: response.setAttribute(\"MODULE_MODE\",
		 * moduleMode)", ex); } // catch (Exception ex) it.eng.sil.util.TraceWrapper.debug(
		 * _logger,className+"::service: response", response);
		 * 
		 */
	}

	/* Selezione dell'appuntamento dati CODCPI e PRGAPPUNTAMENTO */

	public void select(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY");

		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
				getResponseContainer(), pool, statement, "SELECT");

		it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);

		Object rowObject = null;

		if (rowsSourceBean != null)
			rowObject = rowsSourceBean.getAttribute(DataRow.ROW_TAG);
		EMFErrorHandler engErrorHandler = getErrorHandler();
		if (rowObject == null) {
			_logger.debug(className + "::select: nessuna riga selezionata");

			engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.WARNING, 10006));
			return;
		} // if (rowObject == null)

		if (!(rowObject instanceof SourceBean)) {
			_logger.debug(className + "::select: più righe selezionate");

			engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.WARNING, 10006));
			return;
		} // if (!(rowObject instanceof SourceBean))

		try {
			response.setAttribute((SourceBean) rowObject);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowObject)", ex);

		} // catch (Exception ex)
	} // public void select(SourceBean request, SourceBean response)

	/* Inserimento nuovo appuntamento nell'agenda */
	public void insert(SourceBean request, SourceBean response) {
		boolean checkOK = true;

		/*
		 * SourceBean fieldCheck = (SourceBean) getConfig().getAttribute("FIELD_CHECKS"); checkOK = checkOK &
		 * ServerValidator.checkFields(request, response, fieldCheck);
		 */
		if (checkOK) {
			try {
				String pool = (String) getConfig().getAttribute("POOL");
				SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.INSERT_QUERY");

				Boolean esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
						pool, statement, "INSERT");

				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::insert: request", request);

				Object rowObject = null;

				EMFErrorHandler engErrorHandler = getErrorHandler();
				if (!esito.booleanValue()) {
					_logger.debug(className + "::insert: nessuna riga inserita");

					engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.WARNING, 10006));
					return;
				} // if (rowObject == null)

				select(request, response);

			} catch (Exception e) {
				checkOK = false;
				it.eng.sil.util.TraceWrapper.error(_logger, className + "::insert:", e);

			}
		} // fine di if (checkOK)

		if (!checkOK) {
			// restoreFieldsFromRequest(request, response);
			try {
				response.setAttribute("ESITO", "ERRORE CONTROLLO CAMPI");
			} catch (SourceBeanException Sbe) {
				it.eng.sil.util.TraceWrapper.error(_logger, className + "::insert:", Sbe);

			}
		} else {
			select(request, response);
		}
	} // private void insert(SourceBean request, SourceBean response)

	/* Update di un appuntamento nell'agenda dati CODCPI e PRGAPPUNTAMENTO */
	public void update(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.UPDATE_QUERY");

		Boolean esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
				statement, "UPDATE");

		it.eng.sil.util.TraceWrapper.debug(_logger, className + "::update: request", request);

		Object rowObject = null;

		EMFErrorHandler engErrorHandler = getErrorHandler();
		if (!esito.booleanValue()) {
			_logger.debug(className + "::update: nessuna riga inserita");

			engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.WARNING, 10006));
			return;
		} // if (rowObject == null)

		select(request, response);
		/*
		 * try { //response.setAttribute("ESITO",esito); //response.setAttribute("ROWS",request); } // try catch
		 * (Exception ex) { it.eng.sil.util.TraceWrapper.fatal( _logger,className +
		 * "::update: response.setAttribute di ESITO e VALORI", ex); } // catch (Exception ex)
		 */
	} // private void update(SourceBean request, SourceBean response)

	public void delete(SourceBean request, SourceBean response) {
		Boolean isOK = new Boolean(false);
		String pool = (String) getConfig().getAttribute("POOL");

		try {
			SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.DELETE_QUERY");
			Boolean esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
					statement, "UPDATE");

			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::delete: request", request);

		} catch (Exception e) {
			isOK = new Boolean(false);
			_logger.fatal(className + "::delete: " + e.toString());

		}

		try {
			response.setAttribute("DELETE_AGENDA_OK", "TRUE");
		} catch (SourceBeanException sbe) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::delete: response.setAttribute(DELETE_AGENDA_OK,TRUE)", sbe);

		}

		if (isOK != null && !isOK.booleanValue()) {
			// restoreFieldsFromRequest(request, response);
			try {
				response.setAttribute("DELETE_UNITA_KO", "TRUE");
			} catch (SourceBeanException sbe) {
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						className + "::delete: response.setAttribute(DELETE_UNITA_OK,TRUE)", sbe);

			}
		} else {
			// select(request,response);
		}
	}// Chiudo il DELETE
}
