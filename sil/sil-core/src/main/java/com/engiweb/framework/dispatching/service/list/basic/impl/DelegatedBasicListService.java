package com.engiweb.framework.dispatching.service.list.basic.impl;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dispatching.service.RequestContextIFace;
import com.engiweb.framework.dispatching.service.ServiceIFace;
import com.engiweb.framework.dispatching.service.list.basic.IFaceBasicListService;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.init.InitializerIFace;
import com.engiweb.framework.paginator.basic.ListIFace;
import com.engiweb.framework.paginator.basic.PaginatorIFace;
import com.engiweb.framework.paginator.basic.impl.GenericList;
import com.engiweb.framework.paginator.basic.impl.GenericPaginator;
import com.engiweb.framework.util.QueryExecutor;

public class DelegatedBasicListService {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DelegatedBasicListService.class.getName());
	public static final String LIST_PAGE = "LIST_PAGE";
	public static final String LIST_FIRST = "LIST_FIRST";
	public static final String LIST_PREV = "LIST_PREV";
	public static final String LIST_NEXT = "LIST_NEXT";
	public static final String LIST_LAST = "LIST_LAST";
	public static final String LIST_CURRENT = "LIST_CURRENT";
	public static final String LIST_NOCACHE = "LIST_NOCACHE";
	public static final String LIST_DELETE = "LIST_DELETE";

	private DelegatedBasicListService() {
		super();
	} // private DelegatedBasicListService()

	public static void service(ServiceIFace service, SourceBean request, SourceBean response) throws Exception {
		if ((service == null) || (request == null) || (response == null)) {
			_logger.warn("DelegatedBasicListService::service: parametri non validi");

			return;
		} // if ((service == null) || (request == null) || (response == null))
		it.eng.sil.util.TraceWrapper.debug(_logger, "DelegatedBasicListService::service: request", request);

		String message = getMessage(request);
		if ((message == null) || message.equalsIgnoreCase("BEGIN"))
			message = LIST_FIRST;
		String list_nocache = (String) request.getAttribute(LIST_NOCACHE);
		if (list_nocache == null)
			list_nocache = "FALSE";
		IFaceBasicListService listService = (IFaceBasicListService) service;
		if (message.equalsIgnoreCase(LIST_DELETE)) {
			listService.delete(request, response);
			message = LIST_PAGE;
			list_nocache = "TRUE";
		} // if (message.equalsIgnoreCase(LIST_DELETE))
		if ((listService.getList() == null) || list_nocache.equalsIgnoreCase("TRUE"))
			listService.setList(listService.getList(request, response));
		if (listService.getList() == null) {
			_logger.warn("DelegatedBasicListService::service: _list nullo");

			return;
		} // if (listService.getList() == null)
		int pagedListNumber = 1;
		if (message.equalsIgnoreCase(LIST_PAGE)) {
			String list_page = (String) request.getAttribute(LIST_PAGE);
			if (list_page == null)
				list_page = "1";
			try {
				pagedListNumber = Integer.parseInt(list_page);
			} // try
			catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						"DelegatedBasicListService::service: Integer.parseInt(list_page)", ex);

			} // catch (Exception ex) try
		} // if (message.equalsIgnoreCase(LIST_PAGE))
		else if (message.equalsIgnoreCase(LIST_FIRST))
			pagedListNumber = 1;
		else if (message.equalsIgnoreCase(LIST_PREV))
			pagedListNumber = listService.getList().getPrevPage();
		else if (message.equalsIgnoreCase(LIST_NEXT))
			pagedListNumber = listService.getList().getNextPage();
		else if (message.equalsIgnoreCase(LIST_LAST))
			pagedListNumber = listService.getList().pages();
		else if (message.equalsIgnoreCase(LIST_CURRENT))
			pagedListNumber = listService.getList().getCurrentPage();
		_logger.debug("DelegatedBasicListService::service: pagedListNumber [" + pagedListNumber + "]");

		listService.getList().clearDynamicData();
		listService.callback(request, response, listService.getList(), pagedListNumber);
		SourceBean pagedList = listService.getList().getPagedList(pagedListNumber);
		if (pagedList == null) {
			_logger.warn("DelegatedBasicListService::service: pagedList nullo");

			return;
		} // if (pagedList == null)
		try {
			response.setAttribute(pagedList);
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					"DelegatedBasicListService::service: response.setAttribute(pagedList)", ex);

		} // catch (SourceBeanException ex) try
		it.eng.sil.util.TraceWrapper.debug(_logger, "DelegatedBasicListService::service: response", response);

	} // public static void service(ServiceIFace service, SourceBean request,

	// SourceBean response) throws Exception
	public static ListIFace getList(ServiceIFace service, SourceBean request, SourceBean response) throws Exception {
		PaginatorIFace paginator = new GenericPaginator();
		InitializerIFace serviceInitializer = (InitializerIFace) service;
		int pagedRows = Integer.parseInt((String) serviceInitializer.getConfig().getAttribute("ROWS"));
		paginator.setPageSize(pagedRows);
		String pool = (String) serviceInitializer.getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) serviceInitializer.getConfig().getAttribute("QUERIES.SELECT_QUERY");
		RequestContextIFace serviceRequestContext = (RequestContextIFace) service;
		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(serviceRequestContext.getRequestContainer(),
				serviceRequestContext.getResponseContainer(), pool, statement, "SELECT");
		Vector rowsVector = null;
		if (rowsSourceBean != null)
			rowsVector = rowsSourceBean.getAttributeAsVector(DataRow.ROW_TAG);
		if ((rowsSourceBean == null) || (rowsVector.size() == 0)) {
			EMFErrorHandler engErrorHandler = serviceRequestContext.getErrorHandler();
			engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.INFORMATION, 10001));
		} // if ((rowsSourceBean == null) || (rowsVector.size() == 0))
		else
			for (int i = 0; i < rowsVector.size(); i++)
				paginator.addRow(rowsVector.elementAt(i));
		ListIFace list = new GenericList();
		list.setPaginator(paginator);
		// list.addStaticData(firstData);
		return list;
	} // public static ListIFace getList(ServiceIFace service, SourceBean

	// request, SourceBean response) throws Exception
	public static boolean delete(ServiceIFace service, SourceBean request, SourceBean response) {
		InitializerIFace serviceInitializer = (InitializerIFace) service;
		String pool = (String) serviceInitializer.getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) serviceInitializer.getConfig().getAttribute("QUERIES.DELETE_QUERY");
		RequestContextIFace serviceRequestContext = (RequestContextIFace) service;
		Boolean isOK = (Boolean) QueryExecutor.executeQuery(serviceRequestContext.getRequestContainer(),
				serviceRequestContext.getResponseContainer(), pool, statement, "DELETE");
		EMFErrorHandler engErrorHandler = serviceRequestContext.getErrorHandler();
		if ((isOK != null) && isOK.booleanValue()) {
			engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.INFORMATION, 10002));
			return true;
		} // if ((isOK != null) && isOK.booleanValue())
		_logger.error("DelegatedBasicListService::delete: errore cancellazione riga");

		engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.WARNING, 10003));
		return false;
	} // public static boolean delete( ServiceIFace service, SourceBean
		// request,

	// SourceBean response)
	public static String getMessage(SourceBean request) {
		return (String) request.getAttribute("MESSAGE");
	} // public static String getMessage(SourceBean request)
} // public class DelegatedBasicListService
