package com.engiweb.framework.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * La classe <code>ResponseContainerAccess</code> permette di accede all'oggetto <code>ResponseContainer</code>
 * <p>
 * 
 * @version 1.0, 15/01/2003
 * @author Daniela Butano
 * @see BaseContainer
 * @see SessionContainer
 * @see ApplicationContainer
 * @see ResponseContainer
 */
public class ResponseContainerAccess {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ResponseContainerAccess.class.getName());

	/**
	 * Ritorna il <code>ResponseContainer</code> il cui riferimento &egrave; memorizzato nell'oggetto
	 * <code>HttpServletRequest</code> <em>request</em> della richiesta HTTP corrente.
	 * <p>
	 * 
	 * @param request
	 *            <code>HttpServletRequest</code> della richiesta HTTP corrente
	 * @return <code>ResponseContainer</code> il contenitore dei dati di sessione
	 */
	public static ResponseContainer getResponseContainer(HttpServletRequest request) {
		if (request == null) {
			_logger.debug("ResponseContainerAccess::getResponseContainer: request nullo");

			return null;
		} // if (request == null)
		String responseContainerName = request.getParameter(Constants.RESPONSE_CONTAINER);
		if (responseContainerName == null) {
			_logger.debug("ResponseContainerAccess::getResponseContainer: responseContainerName nullo");

			return null;
		} // if (responseContainerName == null)
		ResponseContainer responseContainer = (ResponseContainer) request.getAttribute(responseContainerName);
		if (responseContainer == null) {
			_logger.debug("ResponseContainerAccess::getResponseContainer: responseContainer non contenuto in request");

			HttpSession session = request.getSession(true);
			responseContainer = (ResponseContainer) session.getAttribute(responseContainerName);
			if (responseContainer == null)
				_logger.debug(
						"ResponseContainerAccess::getResponseContainer: responseContainer non contenuto in sessione");

			else
				_logger.debug("ResponseContainerAccess::getResponseContainer: responseContainer contenuto in sessione");

		} // if (requestContainer == null)
		else
			_logger.debug("ResponseContainerAccess::getResponseContainer: responseContainer contenuto in request");

		return responseContainer;
	} // public static ResponseContainer
		// getResponseContainer(HttpServletRequest request)

	public static void delResponseContainer(HttpServletRequest request) {
		if (request == null) {
			_logger.debug("ResponseContainerAccess::delResponseContainer: request nullo");

			return;
		} // if (request == null)
		String responseContainerName = request.getParameter(Constants.RESPONSE_CONTAINER);
		if (responseContainerName == null) {
			_logger.debug("ResponseContainerAccess::delResponseContainer: responseContainerName nullo");

			return;
		} // if (responseContainerName == null)
		request.setAttribute(responseContainerName, null);
		HttpSession session = request.getSession(true);
		session.setAttribute(responseContainerName, null);
		_logger.debug("ResponseContainerAccess::delResponseContainer: responseContainer rimosso");

	} // public static void delResponseContainer(HttpServletRequest request)
} // public class ResponseContainerAccess
