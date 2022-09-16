package com.engiweb.framework.dispatching.httpchannel;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.HttpJspPage;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.RequestContainerAccess;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.ResponseContainerAccess;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorHandler;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public abstract class AbstractHttpJspPage extends HttpServlet implements HttpJspPage {
	private static final long serialVersionUID = 1L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AbstractHttpJspPage.class.getName());

	public final void init(ServletConfig config) throws ServletException {
		super.init(config);
		jspInit();
		_jspInit();
	}

	public String getServletInfo() {
		return "AbstractHttpJspPage";
	}

	public final void destroy() {
		jspDestroy();
		_jspDestroy();
	}

	public final void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Monitor monitor = MonitorFactory.start("view.jsp." + this.getClass().getName().toLowerCase());
		try {
			_jspService(request, response);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "AbstractHttpJspPage::service: _jspService(request, response)",
					ex);

			HttpSession session = request.getSession(true);
			session.setAttribute("_EXCEPTION_", ex);

			/*
			 * request.getRequestDispatcher("/jsp/af/error.jsp").forward( request, response);
			 */

			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 0);
			response.sendRedirect(request.getContextPath() + "/jsp/af/jspError.jsp");
		} // catch (Exception ex)
		finally {
			RequestContainerAccess.delRequestContainer(request);
			ResponseContainerAccess.delResponseContainer(request);
			monitor.stop();
		} // finally
	}

	public void jspInit() {
	}

	public void _jspInit() {
	}

	public void jspDestroy() {
	}

	protected void _jspDestroy() {
	}

	public abstract void _jspService(HttpServletRequest paramHttpServletRequest,
			HttpServletResponse paramHttpServletResponse) throws ServletException, IOException;

	public RequestContainer getRequestContainer(HttpServletRequest request) {
		RequestContainer requestContainer = RequestContainerAccess.getRequestContainer(request);
		if (requestContainer == null)
			_logger.warn("AbstractHttpJspPage::getRequestContainer: requestContainer nullo");

		return requestContainer;
	} // public RequestContainer getRequestContainer(HttpServletRequest
		// request)

	public SourceBean getServiceRequest(HttpServletRequest request) {
		RequestContainer requestContainer = getRequestContainer(request);
		if (requestContainer == null) {
			_logger.warn("AbstractHttpJspPage::getServiceRequest: requestContainer nullo");

			return null;
		} // if (requestContainer == null)
		SourceBean serviceRequest = requestContainer.getServiceRequest();
		if (serviceRequest == null)
			_logger.warn("AbstractHttpJspPage::getServiceRequest: serviceRequest nullo");

		return serviceRequest;
	} // public SourceBean getServiceRequest(HttpServletRequest request)

	public ResponseContainer getResponseContainer(HttpServletRequest request) {
		ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(request);
		if (responseContainer == null)
			_logger.warn("AbstractHttpJspPage::getResponseContainer: responseContainer nullo");

		return responseContainer;
	} // public ResponseContainer getResponseContainer(HttpServletRequest
		// request)

	public SourceBean getServiceResponse(HttpServletRequest request) {
		ResponseContainer responseContainer = getResponseContainer(request);
		if (responseContainer == null) {
			_logger.warn("AbstractHttpJspPage::getServiceResponse: responseContainer nullo");

			return null;
		} // if (responseContainer == null)
		SourceBean serviceResponse = responseContainer.getServiceResponse();
		if (serviceResponse == null)
			_logger.warn("AbstractHttpJspPage::getServiceResponse: serviceResponse nullo");

		return serviceResponse;
	} // public SourceBean getServiceResponse(HttpServletRequest request)

	public EMFErrorHandler getErrorHandler(HttpServletRequest request) {
		ResponseContainer responseContainer = getResponseContainer(request);
		if (responseContainer == null) {
			_logger.warn("AbstractHttpJspPage::getErrorHandler: responseContainer nullo");

			return null;
		} // if (responseContainer == null)
		EMFErrorHandler errorHandler = responseContainer.getErrorHandler();
		if (errorHandler == null)
			_logger.warn("AbstractHttpJspPage::getErrorHandler: errorHandler nullo");

		return errorHandler;
	} // public EMFErrorHandler getErrorHandler(HttpServletRequest request)

}