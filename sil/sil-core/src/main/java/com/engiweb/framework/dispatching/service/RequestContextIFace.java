package com.engiweb.framework.dispatching.service;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorHandler;

/**
 * Questa interfaccia definisce tutti i metodi necessari per gestire il contesto di una richiesta di servizio.
 */
public interface RequestContextIFace {
	/**
	 * Imposta lo stato della classe partendo da un 'istanza di <code>RequestContextIFace</code>
	 * <p>
	 * 
	 * @param requestContext
	 *            l'istanza da cui impostare lo stato.
	 * @see RequestContainer
	 * @see ResponseContainer
	 */
	void setRequestContext(RequestContextIFace requestContext);

	/**
	 * Ritorna il <code>RequestContainer</code> attributo di classe.
	 * <p>
	 * 
	 * @see RequestContainer
	 * @return il <code>RequestContainer</code> attributo di classe.
	 */
	RequestContainer getRequestContainer();

	/**
	 * Ritorna il <code>ResponseContainer</code> attributo di classe.
	 * 
	 * @return il <code>ResponseContainer</code> attributo di classe.
	 * @see ResponseContainer
	 */
	ResponseContainer getResponseContainer();

	/**
	 * Ritorna il <code>SourceBean</code> contenente tutti i parametri della richiesta di servizio.
	 * 
	 * @return il <code>SourceBean</code> contenente i parametri della richiesta.
	 */
	SourceBean getServiceRequest();

	/**
	 * Ritorna il <code>SourceBean</code> contenente gli oggetti istanziati nella business logic in risposta ad una
	 * richiesta di servizio.
	 * 
	 * @return il <code>SourceBean</code> contenente gli oggetti di risposta.
	 */
	SourceBean getServiceResponse();

	/**
	 * Ritorna il <code>EMFErrorHandler</code> gestore degli errori per una richiesta di servizio.
	 * <p>
	 * 
	 * @return il <code>EMFErrorHandler</code> gestore degli errori.
	 */
	EMFErrorHandler getErrorHandler();
} // public interface RequestContextIFace
