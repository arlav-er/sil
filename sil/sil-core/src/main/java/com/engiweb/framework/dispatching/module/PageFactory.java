package com.engiweb.framework.dispatching.module;

import com.engiweb.framework.base.ApplicationContainer;
import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.init.InitializerIFace;

public class PageFactory {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(PageFactory.class.getName());

	private PageFactory() {
		super();
	} // private PageFactory()

	public static PageIFace getPage(RequestContainer requestContainer, String pageName) throws ModuleException {
		_logger.debug("PageFactory::getPage: pageName [" + pageName + "]");

		ConfigSingleton configure = ConfigSingleton.getInstance();
		SourceBean pageBean = (SourceBean) configure.getFilteredSourceBeanAttribute("PAGES.PAGE", "NAME", pageName);
		if (pageBean == null) {
			_logger.fatal("PageFactory::getPage: nome della page non valido !");

			throw new ModuleException("Nome della page [" + pageName + "] non valido");
		} // if (pageBean == null)
		String pageClass = (String) pageBean.getAttribute("CLASS");
		if (pageClass == null) {
			_logger.debug(
					"PageFactory::getPage: classe della page non censita, default [com.engiweb.framework.dispatching.module.DefaultPage]");

			pageClass = "com.engiweb.framework.dispatching.module.DefaultPage";
		} // if (pageScope == null)
		String pageScope = (String) pageBean.getAttribute("SCOPE");
		if (pageScope == null) {
			_logger.debug("PageFactory::getPage: scope della page non censito, default [REQUEST]");

			pageScope = "REQUEST";
		} // if (pageScope == null)
		if ((pageScope == null) || (!pageScope.equalsIgnoreCase("REQUEST") && !pageScope.equalsIgnoreCase("SESSION")
				&& !pageScope.equalsIgnoreCase("APPLICATION"))) {
			_logger.warn("PageFactory::getPage: scope della page non valido [" + pageScope + "]");

			pageScope = "REQUEST";
		} // if ((pageScope == null) || ...
		_logger.debug("PageFactory::getPage: pageScope [" + pageScope + "]");

		PageIFace page = null;
		if (pageScope.equalsIgnoreCase("REQUEST")) {
			page = getInstance(pageClass, pageBean);
			return page;
		} // if (pageScope.equalsIgnoreCase("REQUEST"))
		else if (pageScope.equalsIgnoreCase("SESSION")) {
			SessionContainer sessionContainer = requestContainer.getSessionContainer();
			page = (PageIFace) sessionContainer.getAttribute("AF_PAGE_" + pageName);
			if (page == null) {
				synchronized (sessionContainer) {
					page = (PageIFace) sessionContainer.getAttribute("AF_PAGE_" + pageName);
					if (page == null) {
						_logger.debug("PageFactory::getPage: page non trovata in SESSION !");

						page = getInstance(pageClass, pageBean);
						sessionContainer.setAttribute("AF_PAGE_" + pageName, page);
					} // if (page == null)
				} // synchronized (sessionContainer)
			} // if (page == null)
			else
				_logger.debug("PageFactory::getPage: page trovata in SESSION");

			return page;
		} // if (pageScope.equalsIgnoreCase("SESSION"))
		else {
			ApplicationContainer applicationContainer = ApplicationContainer.getInstance();
			page = (PageIFace) applicationContainer.getAttribute("AF_PAGE_" + pageName);
			if (page == null) {
				synchronized (applicationContainer) {
					page = (PageIFace) applicationContainer.getAttribute("AF_PAGE_" + pageName);
					if (page == null) {
						_logger.debug("PageFactory::getPage: page non trovata in APPLICATION !");

						page = getInstance(pageClass, pageBean);
						applicationContainer.setAttribute("AF_PAGE_" + pageName, page);
					} // if (page == null)
				} // synchronized (applicationContainer)
			} // if (page == null)
			else
				_logger.debug("PageFactory::getPage: page trovata in APPLICATION");

			return page;
		} // if (pageScope.equalsIgnoreCase("SESSION")) else
	} // public static PageIFace getPage(RequestContainer requestContainer,
		// String pageName) throws ModuleException

	private static PageIFace getInstance(String pageClass, SourceBean config) throws ModuleException {
		PageIFace page = null;
		try {
			page = (PageIFace) Class.forName(pageClass).newInstance();
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "PageFactory::getInstance: ", ex);

			throw new ModuleException(ex.getMessage());
		} // catch (Exception ex)
		if (page instanceof InitializerIFace)
			((InitializerIFace) page).init(config);
		return page;
	} // private static PageIFace getInstance(String pageClass, SourceBean
		// config) throws ModuleException
} // public class PageFactory
