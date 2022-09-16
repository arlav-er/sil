package com.engiweb.framework.dispatching.httpchannel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.engiweb.framework.base.Constants;
import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.SessionExpiredAction;
import com.engiweb.framework.dispatching.coordinator.CoordinatorIFace;
import com.engiweb.framework.dispatching.coordinator.DispatcherManager;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.dispatching.service.RequestContextIFace;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.presentation.PresentationRendering;
import com.engiweb.framework.presentation.Publisher;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class AdapterHTTP extends HttpServlet {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AdapterHTTP.class.getName());
	public static final String NEW_SESSION = "NEW_SESSION";
	public static final String REQUEST_CONTAINER = "REQUEST_CONTAINER";
	public static final String HTTP_CONTENT_TYPE = "text/html";
	// public static final String WAP_CONTENT_TYPE = "text/vnd.wap.wml";
	// public static final String HTTP_REQUEST_AUTH_TYPE =
	// "HTTP_REQUEST_AUTH_TYPE";
	// public static final String HTTP_REQUEST_CHARACTER_ENCODING =
	// "HTTP_REQUEST_CHARACTER_ENCODING";
	// public static final String HTTP_REQUEST_CONTENT_LENGTH =
	// "HTTP_REQUEST_CONTENT_LENGTH";
	// public static final String HTTP_REQUEST_CONTENT_TYPE =
	// "HTTP_REQUEST_CONTENT_TYPE";
	// public static final String HTTP_REQUEST_CONTEXT_PATH =
	// "HTTP_REQUEST_CONTEXT_PATH";
	// public static final String HTTP_REQUEST_METHOD = "HTTP_REQUEST_METHOD";
	// public static final String HTTP_REQUEST_PATH_INFO =
	// "HTTP_REQUEST_PATH_INFO";
	// public static final String HTTP_REQUEST_PATH_TRANSLATED =
	// "HTTP_REQUEST_PATH_TRANSLATED";
	// public static final String HTTP_REQUEST_PROTOCOL =
	// "HTTP_REQUEST_PROTOCOL";
	public static final String HTTP_REQUEST_QUERY_STRING = "HTTP_REQUEST_QUERY_STRING";
	// public static final String HTTP_REQUEST_REMOTE_ADDR =
	// "HTTP_REQUEST_REMOTE_ADDR";
	// public static final String HTTP_REQUEST_REMOTE_HOST =
	// "HTTP_REQUEST_REMOTE_HOST";
	// public static final String HTTP_REQUEST_REMOTE_USER =
	// "HTTP_REQUEST_REMOTE_USER";
	// public static final String HTTP_REQUEST_REQUESTED_SESSION_ID =
	// "HTTP_REQUEST_REQUESTED_SESSION_ID";
	// public static final String HTTP_REQUEST_REQUEST_URI =
	// "HTTP_REQUEST_REQUEST_URI";
	// public static final String HTTP_REQUEST_SCHEME = "HTTP_REQUEST_SCHEME";
	// public static final String HTTP_REQUEST_SERVER_NAME =
	// "HTTP_REQUEST_SERVER_NAME";
	// public static final String HTTP_REQUEST_SERVER_PORT =
	// "HTTP_REQUEST_SERVER_PORT";
	// public static final String HTTP_REQUEST_SERVLET_PATH =
	// "HTTP_REQUEST_SERVLET_PATH";
	// public static final String HTTP_REQUEST_USER_PRINCIPAL =
	// "HTTP_REQUEST_USER_PRINCIPAL";
	// public static final String HTTP_REQUEST_REQUESTED_SESSION_ID_FROM_COOKIE
	// =
	// "HTTP_REQUEST_REQUESTED_SESSION_ID_FROM_COOKIE";
	// public static final String HTTP_REQUEST_REQUESTED_SESSION_ID_FROM_URL =
	// "HTTP_REQUEST_REQUESTED_SESSION_ID_FROM_URL";
	// public static final String HTTP_REQUEST_REQUESTED_SESSION_ID_VALID =
	// "HTTP_REQUEST_REQUESTED_SESSION_ID_VALID";
	// public static final String HTTP_REQUEST_SECURE = "HTTP_REQUEST_SECURE";
	// public static final String HTTP_ACCEPT_HEADER = "ACCEPT";
	// public static final String WAP_MIME_TYPE = "vnd.wap";
	// public static final String HTTP_SESSION_ID = "HTTP_SESSION_ID";
	private static final String SERVLET_PUBLISHER_TYPE = "SERVLET";
	private static final String JSP_PUBLISHER_TYPE = "JSP";
	private static final String FORWARD_PUBLISHING_MODE = "FORWARD";
	private static final String SENDREDIRECT_PUBLISHING_MODE = "SENDREDIRECT";

	private static final String MSG_INIZIO_RICHIESTA = " *** INIZIO RICHIESTA ****";
	private static final String MSG_FINE_RICHIESTA = " ***  FINE RICHIESTA  ***\r\n\r\n\r\n\r\n\r\n";

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		_logger.debug("AdapterHTTP::service:" + MSG_INIZIO_RICHIESTA);

		Monitor monitor = null;
		try {
			SourceBean serviceRequest = null;
			EMFErrorHandler emfErrorHandler = null;

			// ResponseContainer loopbackResponseContainer =
			// ResponseContainerAccess.getResponseContainer(request);
			//
			// if (loopbackResponseContainer != null) {
			// TracerSingleton.log(// Constants.NOME_MODULO,//
			// TracerSingleton.DEBUG,// "AdapterHTTP::service: loop-back
			// rilevato");
			// serviceRequest =
			// loopbackResponseContainer.getLoopbackServiceRequest();
			// if (serviceRequest == null)
			// serviceRequest = new SourceBean(Constants.SERVICE_REQUEST);
			// else {
			// Object newServiceRequest =
			// serviceRequest.getAttribute(Constants.SERVICE_REQUEST);
			// if ((newServiceRequest != null)
			// && (newServiceRequest instanceof SourceBean))
			// serviceRequest = (SourceBean) newServiceRequest;
			// } // if (serviceRequest == null)
			// emfErrorHandler = loopbackResponseContainer.getErrorHandler();
			// if (emfErrorHandler == null)
			// emfErrorHandler = new EMFErrorHandler();
			// } // if (loopbackResponseContainer != null)
			// else {

			monitor = MonitorFactory.start("controller.adapter.http");
			serviceRequest = new SourceBean(Constants.SERVICE_REQUEST);
			request.setCharacterEncoding("UTF-8");
			Enumeration names = request.getParameterNames();
			while (names.hasMoreElements()) {
				String parameterName = (String) names.nextElement();
				String[] parameterValues = request.getParameterValues(parameterName);
				if (parameterValues != null)
					for (int i = 0; i < parameterValues.length; i++)
						serviceRequest.setAttribute(parameterName, parameterValues[i]);
			} // while (names.hasMoreElements())
			emfErrorHandler = new EMFErrorHandler();

			// } // if (loopbackResponseContainer != null) else

			RequestContainer requestContainer = new RequestContainer();
			RequestContainer.setRequestContainer(requestContainer);
			requestContainer.setServiceRequest(serviceRequest);
			boolean isRequestedSessionIdValid = true;
			HttpSession session = request.getSession(true);
			
			if (session.isNew()) {
				String newSessionString = (String) (serviceRequest.getAttribute(NEW_SESSION));
				isRequestedSessionIdValid = ((newSessionString != null) && (newSessionString.equalsIgnoreCase("TRUE")));
			} // if (session.isNew())
			synchronized (session) {
				RequestContainer parentRequestContainer = (RequestContainer) session.getAttribute(REQUEST_CONTAINER);
				// if (!Navigator.isNavigatorEnabled())
				if (parentRequestContainer == null)
					requestContainer.setSessionContainer(new SessionContainer(true));
				else
					requestContainer.setSessionContainer(parentRequestContainer.getSessionContainer());
				// else {
				// if (parentRequestContainer == null)
				// requestContainer.setSessionContainer(
				// new SessionContainer(true));
				// else {
				// requestContainer.setSessionContainer(
				// new SessionContainer(false));
				// requestContainer.setParent(parentRequestContainer);
				// } // if (parentRequestContainer == null) else
				// } // if (!Navigator.isNavigatorEnabled())
				session.setAttribute(REQUEST_CONTAINER, requestContainer);
			} // synchronized (session)
			if (!isRequestedSessionIdValid) {
				_logger.warn("AdapterHTTP::service: sessione scaduta !");

				SessionExpiredAction.setSessionExpiredAction(serviceRequest);
			} // if (!isRequestedSessionIdValid)
			setHttpRequestData(request, requestContainer);
			// String channelType = Constants.HTTP_CHANNEL;
			// String channelTypeParameter =
			// (String) (serviceRequest.getAttribute(Constants.CHANNEL_TYPE));
			// String channelTypeHeader =
			// (String) (requestContainer.getAttribute(HTTP_ACCEPT_HEADER));
			// if (((channelTypeParameter != null)
			// && channelTypeParameter.equalsIgnoreCase(Constants.WAP_CHANNEL))
			// || ((channelTypeHeader != null)
			// && (channelTypeHeader.indexOf(WAP_MIME_TYPE) != -1)))
			// channelType = Constants.WAP_CHANNEL;
			requestContainer.setChannelType(Constants.HTTP_CHANNEL);
			requestContainer.setAttribute(Constants.HTTP_REQUEST, request);
			requestContainer.setAttribute(Constants.HTTP_RESPONSE, response);

			requestContainer.setAttribute(Constants.SERVLET_CONFIG, getServletConfig());

			it.eng.sil.util.TraceWrapper.debug(_logger,
					"AdapterHTTP::service: **** requestContainer (prima di qualunque elaborazione) ***\r\n",
					requestContainer);

			it.eng.sil.util.TraceWrapper.debug(_logger,
					"AdapterHTTP::service: **** sessionContainer (prima di qualunque elaborazione) ***\r\n",
					requestContainer.getSessionContainer());

			ResponseContainer responseContainer = new ResponseContainer();
			responseContainer.setErrorHandler(emfErrorHandler);
			SourceBean serviceResponse = new SourceBean(Constants.SERVICE_RESPONSE);
			responseContainer.setServiceResponse(serviceResponse);

			// *****************************************
			// ** Gestione Navigator
			// ******************************************
			// FV 26-01-2005 Disabilitata!!!

			// try {
			// Navigator.checkNavigation(requestContainer);
			// } // try
			// catch (NavigationException ne) {
			// TracerSingleton.log(// Constants.NOME_MODULO,//
			// TracerSingleton.CRITICAL,// "AdapterHTTP::service: ",// ne);
			// requestContainer.setServiceRequest(
			// NavigationErrorAction.getNavigationErrorServiceRequest());
			// } // catch (NavigationException ne)

			serviceRequest = requestContainer.getServiceRequest();
			RequestContextIFace requestContext = new DefaultRequestContext(requestContainer, responseContainer);
			CoordinatorIFace coordinator = DispatcherManager.getCoordinator(requestContext);
			Exception serviceException = null;
			if (coordinator == null) {
				_logger.warn("AdapterHTTP::service: coordinator nullo !");

				serviceException = new Exception("Coordinatore non trovato");
				emfErrorHandler.addError(new EMFInternalError(EMFErrorSeverity.ERROR, "Coordinatore non trovato !"));
			} // if (coordinator == null)
			else {
				((RequestContextIFace) coordinator).setRequestContext(requestContext);
				try {
					coordinator.service(serviceRequest, serviceResponse);
				} // try
				catch (Exception ex) {
					it.eng.sil.util.TraceWrapper.fatal(_logger, "AdapterHTTP::service:", ex);

					serviceException = ex;
					emfErrorHandler.addError(new EMFInternalError(EMFErrorSeverity.ERROR, ex));
				} // catch (Exception ex)
				((RequestContextIFace) coordinator).setRequestContext(null);
				responseContainer.setBusinessType(coordinator.getBusinessType());
				responseContainer.setBusinessName(coordinator.getBusinessName());
			} // if (coordinator == null) else
			requestContainer.setAttribute(Constants.HTTP_REQUEST, null);
			requestContainer.setAttribute(Constants.HTTP_RESPONSE, null);
			requestContainer.setAttribute(Constants.SERVLET_CONFIG, null);
			synchronized (session) {
				session.setAttribute(REQUEST_CONTAINER, session.getAttribute(REQUEST_CONTAINER));
			} // synchronized (session)

			// *****************************************
			// ** Gestione eventi
			// ******************************************
			// FV 26-01-2005 Disabilitata!!!

			// try {
			// EventExecutor.service(requestContainer, responseContainer);
			// } // try
			// catch (Exception ex) {
			// TracerSingleton.log(// Constants.NOME_MODULO,//
			// TracerSingleton.CRITICAL,// "AdapterHTTP::service:",// ex);
			// emfErrorHandler.addError(
			// new EMFInternalError(EMFErrorSeverity.ERROR, ex));
			// } // catch (Exception ex)

			it.eng.sil.util.TraceWrapper.debug(_logger,
					"AdapterHTTP::service: *** responseContainer (dopo l'esecuzione dei moduli/action e prima del publisher) ***\r\n",
					responseContainer);

			it.eng.sil.util.TraceWrapper.debug(_logger,
					"AdapterHTTP::service: *** sessionContainer (dopo l'esecuzione dei moduli/action e prima del publisher) ***\r\n",
					requestContainer.getSessionContainer());

			// Boolean isHttpResponseFreezed =
			// (Boolean) responseContainer.getAttribute(
			// Constants.HTTP_RESPONSE_FREEZED);
			// if ((isHttpResponseFreezed == null)
			// || (!isHttpResponseFreezed.booleanValue())) {

			// *********************************
			// ** Recupero publisher
			// *********************************

			String publisherType = null;
			ArrayList resources = null;
			Publisher publisher = Publisher.getPublisher(requestContainer, responseContainer, serviceException);

			if (publisher == null) {
				// *********************************
				// ** Publisher NULLO
				// *********************************

				publisherType = Constants.NOTHING_PUBLISHER_TYPE;
				resources = null;
			} // if (publisher == null)
			else {
				publisherType = publisher.getType();
				if (publisherType.equalsIgnoreCase(Constants.NOTHING_PUBLISHER_TYPE))
					resources = null;
				else if (publisherType.equalsIgnoreCase(Constants.LOOP_PUBLISHER_TYPE)) {
					resources = new ArrayList();
					// Modifica Monica 15/01/2004 - inizio
					// costruito SourceBean per busta ITEM contenente
					// l'attributo resource ="/servlet/AdapterHTTP"
					SourceBean item = new SourceBean("ITEM");
					item.setAttribute("prog", "0");
					item.setAttribute("resource", "/servlet/AdapterHTTP");
					resources.add(item);
					// Modifica Monica 15/01/2004 - fine
				} // if (publisherType.equalsIgnoreCase(AF_PUBLISHER_TYPE))
				else {
					resources = publisher.getResources();
					if (resources.size() == 0) {
						publisherType = Constants.NOTHING_PUBLISHER_TYPE;
						resources = null;
					} // if (resources.size() == 0)
				} // if
					// (publisherType.equalsIgnoreCase(Constants.LOOP_PUBLISHER_TYPE))
					// else
			} // if (publisher == null) else
			if (publisherType.equalsIgnoreCase(Constants.LOOP_PUBLISHER_TYPE)
					|| publisherType.equalsIgnoreCase(SERVLET_PUBLISHER_TYPE)
					|| publisherType.equalsIgnoreCase(JSP_PUBLISHER_TYPE)) {
				// Modifica Monica 15/01/2004 - inizio
				// recupero il SourceBean della busta ITEM, contenente la busta
				// CONFIG
				String resource = (String) ((SourceBean) resources.get(0)).getAttribute("resource");
				// Modifica Monica 15/01/2004 - fine

				String publisherMode = publisher.getMode();
				if (publisherMode == null) {
					_logger.warn("AdapterHTTP::service: publisherMode nullo, default [FORWARD]");

					publisherMode = FORWARD_PUBLISHING_MODE;
				} // if (publisherMode == null)

				String requestContainerName = Constants.REQUEST_CONTAINER + requestContainer.hashCode();
				String responseContainerName = Constants.RESPONSE_CONTAINER + responseContainer.hashCode();
				Router router = null;

				if (publisherMode.equalsIgnoreCase(SENDREDIRECT_PUBLISHING_MODE)) {
					session.setAttribute(requestContainerName, requestContainer);
					session.setAttribute(responseContainerName, responseContainer);
					router = new Router(resource, false);
				} // if
					// (publisherMode.equalsIgnoreCase(SENDREDIRECT_PUBLISHING_MODE))
				else {
					request.setAttribute(requestContainerName, requestContainer);
					request.setAttribute(responseContainerName, responseContainer);
					router = new Router(resource, true);
				} // if
					// (publisherMode.equalsIgnoreCase(SENDREDIRECT_PUBLISHING_MODE))
					// else
				router.setParameter(Constants.REQUEST_CONTAINER, requestContainerName);
				router.setParameter(Constants.RESPONSE_CONTAINER, responseContainerName);
				router.route(getServletContext(), request, response);
			} // if (publisherType.equalsIgnoreCase(AF_PUBLISHER_TYPE)
				// || publisherType.equalsIgnoreCase(SERVLET_PUBLISHER_TYPE) ||
				// publisherType.equalsIgnoreCase(JSP_PUBLISHER_TYPE))
			else {
				// if (requestContainer
				// .getChannelType()
				// .equalsIgnoreCase(Constants.WAP_CHANNEL))
				// response.setContentType(WAP_CONTENT_TYPE);
				// else
				response.setContentType(HTTP_CONTENT_TYPE);
				Monitor renderingMonitor = null;
				if (publisher == null)
					renderingMonitor = MonitorFactory
							.start("view." + Constants.HTTP_CHANNEL + "." + publisherType.toLowerCase());
				else
					renderingMonitor = MonitorFactory.start("view." + Constants.HTTP_CHANNEL + "."
							+ publisherType.toLowerCase() + "." + publisher.getName().toLowerCase());
				try {
					response.getWriter().print(PresentationRendering.render(responseContainer, resources));
				} // try
				catch (Exception ex) {
					throw ex;
				} // catch (Exception ex)
				finally {
					renderingMonitor.stop();
				} // finally
				response.getWriter().flush();
				return;
			} // if (publisherType.equalsIgnoreCase(AF_PUBLISHER_TYPE)
				// || publisherType.equalsIgnoreCase(SERVLET_PUBLISHER_TYPE) ||
				// publisherType.equalsIgnoreCase(JSP_PUBLISHER_TYPE)) else
				// } // if ((isHttpResponseFreezed == null) ||
				// // (!isHttpResponseFreezed.getBoolean()))
				// else
				// TracerSingleton.log(// Constants.NOME_MODULO,//
				// TracerSingleton.DEBUG,// "AdapterHTTP::service: http response
				// congelata");
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "AdapterHTTP::service: ", ex);

		} // catch (Excpetion ex) try
		finally {
			RequestContainer.delRequestContainer();
			if (monitor != null)
				monitor.stop();

			_logger.debug("AdapterHTTP::service: " + MSG_FINE_RICHIESTA);

		} // finally
	} // public void service(HttpServletRequest request, HttpServletResponse

	// response) throws IOException, ServletException

	private void setHttpRequestData(HttpServletRequest request, RequestContainer requestContainer) {
		// requestContainer.setAttribute(
		// HTTP_REQUEST_AUTH_TYPE,
		// request.getAuthType());
		// requestContainer.setAttribute(
		// HTTP_REQUEST_CHARACTER_ENCODING,
		// String.valueOf(request.getCharacterEncoding()));
		// requestContainer.setAttribute(
		// HTTP_REQUEST_CONTENT_LENGTH,
		// String.valueOf(request.getContentLength()));
		// requestContainer.setAttribute(
		// HTTP_REQUEST_CONTENT_TYPE,
		// request.getContentType());
		// requestContainer.setAttribute(
		// HTTP_REQUEST_CONTEXT_PATH,
		// request.getContextPath());
		// requestContainer.setAttribute(HTTP_REQUEST_METHOD,
		// request.getMethod());
		// requestContainer.setAttribute(
		// HTTP_REQUEST_PATH_INFO,
		// request.getPathInfo());
		// requestContainer.setAttribute(
		// HTTP_REQUEST_PATH_TRANSLATED,
		// request.getPathTranslated());
		// requestContainer.setAttribute(
		// HTTP_REQUEST_PROTOCOL,
		// request.getProtocol());
		requestContainer.setAttribute(HTTP_REQUEST_QUERY_STRING, request.getQueryString());
		// requestContainer.setAttribute(
		// HTTP_REQUEST_REMOTE_ADDR,
		// request.getRemoteAddr());
		// requestContainer.setAttribute(
		// HTTP_REQUEST_REMOTE_HOST,
		// request.getRemoteHost());
		// requestContainer.setAttribute(
		// HTTP_REQUEST_REMOTE_USER,
		// request.getRemoteUser());
		// requestContainer.setAttribute(
		// HTTP_REQUEST_REQUESTED_SESSION_ID,
		// request.getRequestedSessionId());
		// requestContainer.setAttribute(
		// HTTP_REQUEST_REQUEST_URI,
		// request.getRequestURI());
		// requestContainer.setAttribute(HTTP_REQUEST_SCHEME,
		// request.getScheme());
		// requestContainer.setAttribute(
		// HTTP_REQUEST_SERVER_NAME,
		// request.getServerName());
		// requestContainer.setAttribute(
		// HTTP_REQUEST_SERVER_PORT,
		// String.valueOf(request.getServerPort()));
		// requestContainer.setAttribute(
		// HTTP_REQUEST_SERVLET_PATH,
		// request.getServletPath());
		// if (request.getUserPrincipal() != null)
		// requestContainer.setAttribute(
		// HTTP_REQUEST_USER_PRINCIPAL,
		// request.getUserPrincipal());
		// requestContainer.setAttribute(
		// HTTP_REQUEST_REQUESTED_SESSION_ID_FROM_COOKIE,
		// String.valueOf(request.isRequestedSessionIdFromCookie()));
		// requestContainer.setAttribute(
		// HTTP_REQUEST_REQUESTED_SESSION_ID_FROM_URL,
		// String.valueOf(request.isRequestedSessionIdFromURL()));
		// requestContainer.setAttribute(
		// HTTP_REQUEST_REQUESTED_SESSION_ID_VALID,
		// String.valueOf(request.isRequestedSessionIdValid()));
		// requestContainer.setAttribute(
		// HTTP_REQUEST_SECURE,
		// String.valueOf(request.isSecure()));
		// Enumeration headerNames = request.getHeaderNames();
		// while (headerNames.hasMoreElements()) {
		// String headerName = (String) headerNames.nextElement();
		// String headerValue = request.getHeader(headerName);
		// requestContainer.setAttribute(headerName, headerValue);
		// } // while (headerNames.hasMoreElements())
		// requestContainer.setAttribute(
		// HTTP_SESSION_ID,
		// request.getSession().getId());
	} // private void setHttpRequestData(HttpServletRequest request,
		// RequestContainer requestContainer)
} // public class ActionServlet extends HttpServlet
