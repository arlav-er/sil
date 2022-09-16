package com.engiweb.framework.dispatching.service.detail.impl;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dispatching.service.RequestContextIFace;
import com.engiweb.framework.dispatching.service.ServiceIFace;
import com.engiweb.framework.dispatching.service.detail.IFaceDetailService;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.init.InitializerIFace;
import com.engiweb.framework.util.QueryExecutor;

public class DelegatedDetailService {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DelegatedDetailService.class.getName());
	public static final String DETAIL_NEW = "DETAIL_NEW";
	public static final String DETAIL_INSERT = "DETAIL_INSERT";
	public static final String DETAIL_SELECT = "DETAIL_SELECT";
	public static final String DETAIL_UPDATE = "DETAIL_UPDATE";
	public static final String SERVICE_MODE = "SERVICE_MODE";
	public static final String SERVICE_MODE_INSERT = "INSERT";
	public static final String SERVICE_MODE_UPDATE = "UPDATE";

	private DelegatedDetailService() {
		super();
	} // public DelegatedDetailService()

	public static void service(ServiceIFace service, SourceBean request, SourceBean response) throws Exception {
		if ((service == null) || (request == null) || (response == null)) {
			_logger.debug("DelegatedDetailService::service: parametri non validi");

			return;
		} // if ((service == null) || (request == null) || (response == null))
		IFaceDetailService detailService = (IFaceDetailService) service;
		it.eng.sil.util.TraceWrapper.debug(_logger, "DelegatedDetailService::service: request", request);

		String message = getMessage(request);
		if ((message == null) || message.equalsIgnoreCase("BEGIN"))
			message = DETAIL_NEW;
		try {
			if (message.equalsIgnoreCase(DETAIL_INSERT)) {
				response.setAttribute(SERVICE_MODE, SERVICE_MODE_INSERT);
				detailService.insert(request, response);
			} // if (message.equalsIgnoreCase(DETAIL_INSERT))
			else if (message.equalsIgnoreCase(DETAIL_SELECT)) {
				response.setAttribute(SERVICE_MODE, SERVICE_MODE_UPDATE);
				detailService.select(request, response);
			} // if (message.equalsIgnoreCase(DETAIL_SELECT))
			else if (message.equalsIgnoreCase(DETAIL_UPDATE)) {
				response.setAttribute(SERVICE_MODE, SERVICE_MODE_UPDATE);
				detailService.update(request, response);
			} // if (message.equalsIgnoreCase(DETAIL_UPDATE))
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "DelegatedDetailService::service:", ex);

		} // catch (Exception ex)
		it.eng.sil.util.TraceWrapper.debug(_logger, "DelegatedDetailService::service: response", response);

	} // public static void service(ServiceIFace service, SourceBean request,

	// SourceBean response) throws Exception

	/**
	 * Questo metodo viene invocato quando viene eseguita una richiesta di inserimento di un nuovo dettaglio.
	 * 
	 * @param request
	 *            sourceBean contenente i parametri della richiesta.
	 * @param response
	 *            sourceBean di risposta.
	 */
	public static boolean insert(ServiceIFace service, SourceBean request, SourceBean response) {
		InitializerIFace serviceInitializer = (InitializerIFace) service;
		String pool = (String) serviceInitializer.getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) serviceInitializer.getConfig().getAttribute("QUERIES.INSERT_QUERY");
		RequestContextIFace serviceRequestContext = (RequestContextIFace) service;
		Boolean isOK = (Boolean) QueryExecutor.executeQuery(serviceRequestContext.getRequestContainer(),
				serviceRequestContext.getResponseContainer(), pool, statement, "INSERT");
		EMFErrorHandler engErrorHandler = serviceRequestContext.getErrorHandler();
		if ((isOK != null) && isOK.booleanValue()) {
			engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.INFORMATION, 10007));
			return true;
		} // if ((isOK != null) && isOK.booleanValue())
		_logger.debug("DefaultDetailModule::insert: errore inserimento riga");

		engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.WARNING, 10008));
		restoreFields(service, request, response);
		return false;
	} // public static boolean insert(ServiceIFace service, SourceBean
		// request,

	// SourceBean response)

	/**
	 * Questo metodo viene invocato quando viene eseguita una richiesta di visualizzazione dei dati di un dettaglio.
	 * 
	 * @param request
	 *            sourceBean contenente i parametri della richiesta.
	 * @param response
	 *            sourceBean di risposta.
	 */
	public static boolean select(ServiceIFace service, SourceBean request, SourceBean response) {
		InitializerIFace serviceInitializer = (InitializerIFace) service;
		String pool = (String) serviceInitializer.getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) serviceInitializer.getConfig().getAttribute("QUERIES.SELECT_QUERY");
		RequestContextIFace serviceRequestContext = (RequestContextIFace) service;
		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(serviceRequestContext.getRequestContainer(),
				serviceRequestContext.getResponseContainer(), pool, statement, "SELECT");
		it.eng.sil.util.TraceWrapper.debug(_logger, "DefaultDetailModule::select: rowsSourceBean", rowsSourceBean);

		Object rowObject = null;
		if (rowsSourceBean != null)
			rowObject = rowsSourceBean.getAttribute(DataRow.ROW_TAG);
		EMFErrorHandler engErrorHandler = serviceRequestContext.getErrorHandler();
		if (rowObject == null) {
			_logger.debug("DefaultDetailModule::select: nessuna riga selezionata");

			engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.WARNING, 10006));
			return false;
		} // if (rowObject == null)
		if (!(rowObject instanceof SourceBean)) {
			_logger.debug("DefaultDetailModule::select: più righe selezionate");

			engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.WARNING, 10006));
			return false;
		} // if (!(rowObject instanceof SourceBean))
		try {
			response.setAttribute((SourceBean) rowObject);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"DefaultDetailModule::select: response.setAttribute((SourceBean)rowObject)", ex);

		} // catch (Exception ex)
		return true;
	} // public static boolean select(ServiceIFace service, SourceBean
		// request,

	// SourceBean response)

	/**
	 * Questo metodo viene invocato quando viene eseguita una richiesta di modifica dei dati nel dettaglio.
	 * 
	 * @param request
	 *            sourceBean contenente i parametri della richiesta.
	 * @param response
	 *            sourceBean di risposta.
	 */
	public static boolean update(ServiceIFace service, SourceBean request, SourceBean response) {
		InitializerIFace serviceInitializer = (InitializerIFace) service;
		String pool = (String) serviceInitializer.getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) serviceInitializer.getConfig().getAttribute("QUERIES.UPDATE_QUERY");
		RequestContextIFace serviceRequestContext = (RequestContextIFace) service;
		Boolean isOK = (Boolean) QueryExecutor.executeQuery(serviceRequestContext.getRequestContainer(),
				serviceRequestContext.getResponseContainer(), pool, statement, "UPDATE");
		EMFErrorHandler engErrorHandler = serviceRequestContext.getErrorHandler();
		boolean success = false;
		if ((isOK != null) && isOK.booleanValue()) {
			engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.INFORMATION, 10004));
			success = true;
		} // if ((isOK != null) && isOK.booleanValue())
		else {
			_logger.debug("DefaultDetailModule::update: errore aggiornamento riga");

			engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.WARNING, 10005));
		} // if ((isOK != null) && isOK.booleanValue()) else
		restoreFields(service, request, response);
		return success;
	} // public static boolean update(ServiceIFace service, SourceBean
		// request,

	// SourceBean response)
	public static String getMessage(SourceBean request) {
		return (String) request.getAttribute("MESSAGE");
	} // public static String getMessage(SourceBean request)

	/**
	 * Questo metodo ripropone in response i dati presenti nella richiesta.
	 * 
	 * @param request
	 *            sourceBean contenente i parametri della richiesta.
	 * @param response
	 *            sourceBean di risposta.
	 */
	public static void restoreFields(ServiceIFace service, SourceBean request, SourceBean response) {
		InitializerIFace serviceInitializer = (InitializerIFace) service;
		try {
			SourceBean row = new SourceBean("ROW");
			Vector fields = serviceInitializer.getConfig().getAttributeAsVector("FIELDS.FIELD");
			for (int i = 0; i < fields.size(); i++) {
				SourceBean field = (SourceBean) fields.elementAt(i);
				String fieldName = (String) field.getAttribute("NAME");
				String fieldValue = (String) request.getAttribute(fieldName);
				row.setAttribute(fieldName, fieldValue);
			} // for (int i = 0; i < fields.size(); i++)
			response.setAttribute(row);
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "DefaultDetailModule::restoreFields:", ex);

		} // catch (Exception ex)
	} // public static void restoreFields(ServiceIFace service, SourceBean
		// request, SourceBean response)
} // public class DelegatedDetailService
