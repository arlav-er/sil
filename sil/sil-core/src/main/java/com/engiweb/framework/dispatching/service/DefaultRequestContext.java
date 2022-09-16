package com.engiweb.framework.dispatching.service;

import java.io.Serializable;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorHandler;

/**
 * Questa classe incapsula il contesto di una richiesta di servizio. Le classi che estendono
 * <code>DefaultRequestContext</code> ereditano gli attributi che rappresentano il servizio
 * (<code>RequestContainer</code>,<code>ResponseContainer</code>) e gli attributi che gli permettono di gestirlo
 * (<code>EMFErrorHandler</code>).
 */
public class DefaultRequestContext implements RequestContextIFace, Serializable {
	private RequestContainer _requestContainer = null;
	private ResponseContainer _responseContainer = null;

	/**
	 * Costruisce un <code>DefaultRequestContext</code> avente attributi non definiti.
	 * <p>
	 */
	public DefaultRequestContext() {
		super();
		_requestContainer = null;
		_responseContainer = null;
	} // public DefaultRequestContext()

	/**
	 * Costruisce un <code>DefaultRequestContext</code> avente attributi definiti dai parametri passati.
	 * 
	 * @param requestContainer
	 *            l'istanza da cui impostare lo stato.
	 * @param responseContainer
	 *            l'istanza da cui impostare lo stato.
	 */
	public DefaultRequestContext(RequestContainer requestContainer, ResponseContainer responseContainer) {
		super();
		_requestContainer = requestContainer;
		_responseContainer = responseContainer;
	} // public DefaultRequestContext(RequestContainer requestContainer,

	// ResponseContainer responseContainer)

	/**
	 * Imposta lo stato della classe partendo da un 'istanza di <code>RequestContextIFace</code>
	 * <p>
	 * 
	 * @param requestContext
	 *            l'istanza da cui impostare lo stato.
	 * @see RequestContainer
	 * @see ResponseContainer
	 */
	public void setRequestContext(RequestContextIFace requestContext) {
		if (requestContext == null) {
			_requestContainer = null;
			_responseContainer = null;
		} // if (requestContext == null)
		else {
			_requestContainer = requestContext.getRequestContainer();
			_responseContainer = requestContext.getResponseContainer();
		} // if (requestContext == null) else
	} // public void setRequestContext(RequestContextIFace requestContext)

	/**
	 * Ritorna il <code>RequestContainer</code> attributo di classe.
	 * <p>
	 * 
	 * @see RequestContainer
	 * @return il <code>RequestContainer</code> attributo di classe.
	 */
	public RequestContainer getRequestContainer() {
		return _requestContainer;
	} // public RequestContainer getRequestContainer()

	/**
	 * Ritorna il <code>ResponseContainer</code> attributo di classe.
	 * 
	 * @return il <code>ResponseContainer</code> attributo di classe.
	 * @see ResponseContainer
	 */
	public ResponseContainer getResponseContainer() {
		return _responseContainer;
	} // public ResponseContainer getResponseContainer()

	/**
	 * Ritorna il <code>EMFErrorHandler</code> gestore degli errori per una richiesta di servizio.
	 * <p>
	 * 
	 * @return il <code>EMFErrorHandler</code> gestore degli errori.
	 */
	public EMFErrorHandler getErrorHandler() {
		if (_responseContainer == null)
			return null;
		return _responseContainer.getErrorHandler();
	} // public EngErrorHadler getErrorHandler()

	/**
	 * Ritorna il <code>SourceBean</code> contenente tutti i parametri della richiesta di servizio.
	 * 
	 * @return il <code>SourceBean</code> contenente i parametri della richiesta.
	 */
	public SourceBean getServiceRequest() {
		if (_requestContainer == null)
			return null;
		return _requestContainer.getServiceRequest();
	} // public SourceBean getServiceRequest()

	/**
	 * Ritorna il <code>SourceBean</code> contenente gli oggetti istanziati nella business logic in risposta ad una
	 * richiesta di servizio.
	 * 
	 * @return il <code>SourceBean</code> contenente gli oggetti di risposta.
	 */
	public SourceBean getServiceResponse() {
		if (_responseContainer == null)
			return null;
		return _responseContainer.getServiceResponse();
	} // public SourceBean getServiceResponse()
} // public class DefaultRequestContext implements RequestContextIFace,
	// Serializable
