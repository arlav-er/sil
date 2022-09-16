package com.engiweb.framework.dispatching.service.list.smart.impl;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.service.RequestContextIFace;
import com.engiweb.framework.dispatching.service.ServiceIFace;
import com.engiweb.framework.dispatching.service.list.smart.IFaceSmartListService;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.init.InitializerIFace;
import com.engiweb.framework.paginator.smart.IFaceListProvider;
import com.engiweb.framework.paginator.smart.IFacePageProvider;
import com.engiweb.framework.paginator.smart.IFaceRowHandler;
import com.engiweb.framework.paginator.smart.IFaceRowProvider;

public class DelegatedSmartListService {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DelegatedSmartListService.class.getName());
	public static final String LIST_PAGE = "LIST_PAGE";
	public static final String LIST_FIRST = "LIST_FIRST";
	public static final String LIST_PREV = "LIST_PREV";
	public static final String LIST_NEXT = "LIST_NEXT";
	public static final String LIST_LAST = "LIST_LAST";
	public static final String LIST_CURRENT = "LIST_CURRENT";
	public static final String LIST_NOCACHE = "LIST_NOCACHE";
	public static final String LIST_DELETE = "LIST_DELETE";

	public DelegatedSmartListService() {
		super();
	} // public DelegatedSmartListService()

	public static void init(ServiceIFace service, SourceBean config) {
		_logger.debug("DelegatedSmartListService::init: invocato");

		IFaceSmartListService listService = (IFaceSmartListService) service;
		try {
			String rowProviderClass = (String) config.getAttribute("ROW_PROVIDER.CLASS");
			_logger.debug("DelegatedSmartListService::init: rowHandlerClass [" + rowProviderClass + "]");

			IFaceRowProvider rowProvider = (IFaceRowProvider) Class.forName(rowProviderClass).newInstance();
			InitializerIFace rowProviderInitializer = (InitializerIFace) rowProvider;
			SourceBean rowProviderConfig = (SourceBean) config.getAttribute("ROW_PROVIDER.CONFIG");
			rowProviderInitializer.init(rowProviderConfig);
			String pageProviderClass = (String) config.getAttribute("PAGE_PROVIDER.CLASS");
			_logger.debug("DelegatedSmartListService::init: pageProviderClass [" + pageProviderClass + "]");

			IFacePageProvider pageProvider = (IFacePageProvider) Class.forName(pageProviderClass).newInstance();
			pageProvider.setRowProvider(rowProvider);
			InitializerIFace pageProviderInitializer = (InitializerIFace) pageProvider;
			SourceBean pageProviderConfig = (SourceBean) config.getAttribute("PAGE_PROVIDER.CONFIG");
			pageProviderInitializer.init(pageProviderConfig);
			String listProviderClass = (String) config.getAttribute("LIST_PROVIDER.CLASS");
			_logger.debug("DelegatedSmartListService::init: listProviderClass [" + listProviderClass + "]");

			IFaceListProvider listProvider = (IFaceListProvider) Class.forName(listProviderClass).newInstance();
			listProvider.setPageProvider(pageProvider);
			InitializerIFace listProviderInitializer = (InitializerIFace) listProvider;
			SourceBean listProviderConfig = (SourceBean) config.getAttribute("LIST_PROVIDER.CONFIG");
			listProviderInitializer.init(listProviderConfig);
			listService.setList(listProvider);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					"DelegatedSmartListService::init: errore inizializzazione paginatore", ex);

			listService.setList(null);
		} // catch (Exception ex)
	} // public static void init(ServiceIFace service, SourceBean config)

	public static void service(ServiceIFace service, SourceBean request, SourceBean response) throws Exception {
		IFaceSmartListService listService = (IFaceSmartListService) service;
		IFaceListProvider listProvider = listService.getList();
		if (listProvider == null) {
			_logger.warn("DelegatedSmartListService::service: listProvider nullo !");

			return;
		} // if (listProvider == null)
		IFacePageProvider pageProvider = listProvider.getPageProvider();
		if (pageProvider == null) {
			_logger.warn("DelegatedSmartListService::service: pageProvider nullo !");

			return;
		} // if (pageProvider == null)
		IFaceRowProvider rowProvider = pageProvider.getRowProvider();
		if (rowProvider == null) {
			_logger.warn("DelegatedSmartListService::service: rowProvider nullo !");

			return;
		} // if (rowProvider == null)
		RequestContextIFace serviceRequestContext = (RequestContextIFace) service;
		RequestContextIFace listProviderRequestContext = (RequestContextIFace) listProvider;
		listProviderRequestContext.setRequestContext(serviceRequestContext);
		String message = getMessage(request);
		if ((message == null) || message.equalsIgnoreCase("BEGIN"))
			message = LIST_FIRST;
		String list_nocache = (String) request.getAttribute(LIST_NOCACHE);
		if (list_nocache == null)
			list_nocache = "FALSE";
		if (message.equalsIgnoreCase(LIST_DELETE)) {
			listService.delete(request, response);
			message = LIST_PAGE;
			list_nocache = "TRUE";
		} // if (message.equalsIgnoreCase(LIST_DELETE))
		if (list_nocache.equalsIgnoreCase("TRUE"))
			listProvider.reload();
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
						"DelegatedSmartListService::service: Integer.parseInt(list_page)", ex);

			} // catch (Exception ex) try
		} // if (message.equalsIgnoreCase(LIST_PAGE))
		else if (message.equalsIgnoreCase(LIST_FIRST))
			pagedListNumber = 1;
		else if (message.equalsIgnoreCase(LIST_PREV)) {
			pagedListNumber = listProvider.getCurrentPage() - 1;
			if (pagedListNumber < 1)
				pagedListNumber = 1;
		} // if (message.equalsIgnoreCase(LIST_PREV))
		else if (message.equalsIgnoreCase(LIST_NEXT))
			pagedListNumber = listProvider.getCurrentPage() + 1;
		else if (message.equalsIgnoreCase(LIST_LAST))
			pagedListNumber = IFaceListProvider.LAST;
		else if (message.equalsIgnoreCase(LIST_CURRENT))
			pagedListNumber = listProvider.getCurrentPage();
		_logger.debug("DelegatedSmartListService::service: pagedListNumber richiesto [" + pagedListNumber + "]");

		listProvider.clearDynamicData();
		listService.callback(request, response, listProvider, pagedListNumber);
		SourceBean pagedList = (SourceBean) listProvider.getListPage(pagedListNumber);
		if (pagedList == null) {
			_logger.warn("DelegatedSmartListService::service: pagedList nullo");

			return;
		} // if (pagedList == null)
		_logger.debug("DelegatedSmartListService::service: pagedListNumber ritornato [" + listProvider.getCurrentPage()
				+ "]");

		try {
			response.setAttribute(pagedList);
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					"DelegatedSmartListService::service: response.setAttribute(pagedList)", ex);

		} // catch (SourceBeanException ex) try
	} // public static void service(ServiceIFace service, SourceBean request,

	// SourceBean response) throws Exception
	public static IFaceListProvider getList(ServiceIFace service, SourceBean request, SourceBean response)
			throws Exception {
		IFaceSmartListService listService = (IFaceSmartListService) service;
		IFaceListProvider listProvider = listService.getList();
		return listProvider;
	} // public static IFaceListProvider getList(ServiceIFace service,

	// SourceBean request, SourceBean response) throws Exception
	public static boolean delete(ServiceIFace service, SourceBean request, SourceBean response) {
		IFaceSmartListService listService = (IFaceSmartListService) service;
		IFaceListProvider listProvider = listService.getList();
		if (listProvider == null) {
			_logger.warn("DelegatedSmartListService::delete: listProvider nullo !");

			return false;
		} // if (listProvider == null)
		IFacePageProvider pageProvider = listProvider.getPageProvider();
		if (pageProvider == null) {
			_logger.warn("DelegatedSmartListService::delete: pageProvider nullo !");

			return false;
		} // if (pageProvider == null)
		IFaceRowProvider rowProvider = pageProvider.getRowProvider();
		if (rowProvider == null) {
			_logger.warn("DelegatedSmartListService::delete: rowProvider nullo !");

			return false;
		} // if (rowProvider == null)
		if (!(rowProvider instanceof IFaceRowHandler)) {
			_logger.warn("DelegatedSmartListService::delete: rowProvider non implementa IFaceRowHandler");

			return false;
		} // if (rowProvider == null)
		IFaceRowHandler rowHandler = (IFaceRowHandler) rowProvider;
		boolean isOK = rowHandler.deleteRow();
		RequestContextIFace serviceRequestContext = (RequestContextIFace) service;
		EMFErrorHandler engErrorHandler = serviceRequestContext.getErrorHandler();
		if (isOK)
			engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.INFORMATION, 10002));
		else {
			_logger.error("DelegatedSmartListService::delete: errore cancellazione riga");

			engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.WARNING, 10003));
		} // if (isOK) else
		return isOK;
	} // public static boolean delete(ServiceIFace service, SourceBean
		// request,

	// SourceBean response)
	public static String getMessage(SourceBean serviceRequest) {
		return (String) serviceRequest.getAttribute("MESSAGE");
	} // public static String getMessage(SourceBean serviceRequest)
} // public class DelegatedSmartListService
