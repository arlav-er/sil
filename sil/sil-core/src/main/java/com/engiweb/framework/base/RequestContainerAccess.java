package com.engiweb.framework.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * La classe <code>RequestContainerAccess</code> permette di accede all'oggetto <code>RequestContainer</code>
 * <p>
 * 
 * @version 1.0, 15/01/2003
 * @author Daniela Butano
 * @see BaseContainer
 * @see SessionContainer
 * @see ApplicationContainer
 * @see ResponseContainer
 */
public class RequestContainerAccess {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RequestContainerAccess.class.getName());

	/**
	 * Ritorna il <code>RequestContainer</code> il cui riferimento &egrave; memorizzato nell'oggetto
	 * <code>HttpServletRequest</code> <em>request</em> della richiesta HTTP corrente.
	 * <p>
	 * 
	 * @param request
	 *            <code>HttpServletRequest</code> della richiesta HTTP corrente
	 * @return <code>RequestContainer</code> il contenitore dei dati di sessione
	 */
	public static RequestContainer getRequestContainer(HttpServletRequest request) {
		if (request == null) {
			_logger.debug("RequestContainerAccess::getRequestContainer: request nullo");

			return null;
		} // if (request == null)
		String requestContainerName = request.getParameter(Constants.REQUEST_CONTAINER);
		if (requestContainerName == null) {
			_logger.debug("RequestContainerAccess::getRequestContainer: requestContainerName nullo");

			return null;
		} // if (requestContainerName == null)
		RequestContainer requestContainer = (RequestContainer) request.getAttribute(requestContainerName);
		if (requestContainer == null) {
			_logger.debug("RequestContainerAccess::getRequestContainer: requestContainer non contenuto in request");

			HttpSession session = request.getSession(true);
			requestContainer = (RequestContainer) session.getAttribute(requestContainerName);
			if (requestContainer == null)
				_logger.debug(
						"RequestContainerAccess::getRequestContainer: requestContainer non contenuto in sessione");

			else
				_logger.debug("RequestContainerAccess::getRequestContainer: requestContainer contenuto in sessione");

		} // if (requestContainer == null)
		else
			_logger.debug("RequestContainerAccess::getRequestContainer: requestContainer contenuto in request");

		return requestContainer;
	} // public static RequestContainer getRequestContainer(HttpServletRequest
		// request)

	public static void delRequestContainer(HttpServletRequest request) {
		if (request == null) {
			_logger.debug("RequestContainerAccess::delRequestContainer: request nullo");

			return;
		} // if (request == null)
		String requestContainerName = request.getParameter(Constants.REQUEST_CONTAINER);
		if (requestContainerName == null) {
			_logger.debug("RequestContainerAccess::delRequestContainer: requestContainerName nullo");

			return;
		} // if (requestContainerName == null)
		request.setAttribute(requestContainerName, null);
		HttpSession session = request.getSession(true);
		session.setAttribute(requestContainerName, null);
		_logger.debug("RequestContainerAccess::delRequestContainer: requestContainer rimosso");

	} // public static void delRequestContainer(HttpServletRequest request)
} // public class RequestContainerAccess
