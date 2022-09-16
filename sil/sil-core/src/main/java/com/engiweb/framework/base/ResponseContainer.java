package com.engiweb.framework.base;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.engiweb.framework.error.EMFErrorHandler;

/**
 * La classe <code>ResponseContainer</code> implementa un contenitore di oggetti legati alla risposta ad una richiesta
 * di un servizio, su un canale/device qualsiasi.
 * <p>
 * 
 * @version 1.0, 11/03/2002
 * @author Luigi Bellio
 * @see BaseContainer
 * @see SessionContainer
 * @see ApplicationContainer
 * @see RequestContainer
 */
public class ResponseContainer extends BaseContainer implements Serializable {
	private String _businessType = null;
	private String _businessName = null;
	private SourceBean _serviceResponse = null;
	private SourceBean _loopbackServiceRequest = null;
	private EMFErrorHandler _errorHandler = null;

	/**
	 * Costruisce un <code>ResponseContainer</code> vuoto.
	 * <p>
	 * 
	 * @see ResponseContainer#ResponseContainer(ResponseContainer)
	 */
	public ResponseContainer() {
		super();
		_businessType = null;
		_businessName = null;
		_serviceResponse = null;
		_loopbackServiceRequest = null;
		_errorHandler = null;
	} // public ResponseContainer()

	/**
	 * Costruisce un <code>ResponseContainer</code> copia di <em>container</em>.
	 * <p>
	 * 
	 * @param container
	 *            <code>ResponseContainer</code> da copiare
	 * @see ResponseContainer#ResponseContainer()
	 */
	public ResponseContainer(ResponseContainer container) {
		super(container);
		_businessType = container._businessType;
		_businessName = container._businessName;
		if (container._serviceResponse != null)
			_serviceResponse = (SourceBean) container._serviceResponse.cloneObject();
		if (container._loopbackServiceRequest != null)
			_loopbackServiceRequest = (SourceBean) container._loopbackServiceRequest.cloneObject();
		if (container._errorHandler != null)
			_errorHandler = (EMFErrorHandler) container._errorHandler.cloneObject();
	} // public ResponseContainer(ResponseContainer container)

	/**
	 * Ritorna un <code>ResponseContainer</code> copia dell'oggetto stesso.
	 * <p>
	 * 
	 * @return una copia del <code>ResponseContainer</code> stesso
	 */
	public synchronized CloneableObject cloneObject() {
		return new ResponseContainer(this);
	} // public synchronized CloneableObject cloneObject()

	/**
	 * Questo metodo permette di inizializzare lo stato dell'oggetto da un oggetto della stessa classe.
	 * 
	 * @param container
	 *            oggetto della stessa classe.
	 */
	public synchronized void setContainer(ResponseContainer container) {
		super.setContainer(container);
		if (container == null) {
			_businessType = null;
			_businessName = null;
			_serviceResponse = null;
			_loopbackServiceRequest = null;
			_errorHandler = null;
		} // if (container == null)
		else {
			_businessType = container._businessType;
			_businessName = container._businessName;
			_serviceResponse = container._serviceResponse;
			_loopbackServiceRequest = container._loopbackServiceRequest;
			_errorHandler = container._errorHandler;
		} // if (container == null) else
	} // public synchronized void setContainer(ResponseContainer container)

	/**
	 * Questo metodo permette di recuperare il tipo di oggetto di business utilizzato per gestire la richiesta di
	 * servizio.
	 * 
	 * @return il tipo di oggetto di business.
	 */
	public synchronized String getBusinessType() {
		return _businessType;
	} // public synchronized String getBusinessType()

	/**
	 * Questo metodo permette di impostare il tipo di oggetto di business utilizzato per gestire la richiesta di
	 * servizio.
	 * 
	 * @param il
	 *            tipo di oggetto di business.
	 */
	public synchronized void setBusinessType(String businessType) {
		_businessType = businessType;
	} // public synchronized void setBusinessType(String businessType)

	/**
	 * Questo metodo permette di recuperare il nome dell'oggetto di business utilizzato per gestire la richiesta di
	 * servizio.
	 * 
	 * @return il nome dell'oggetto di business.
	 */
	public synchronized String getBusinessName() {
		return _businessName;
	} // public synchronized String getBusinessName()

	/**
	 * Questo metodo permette di impostare il nome dell'oggetto di business utilizzato per gestire la richiesta di
	 * servizio.
	 * 
	 * @param il
	 *            nome dell'oggetto di business.
	 */
	public synchronized void setBusinessName(String businessName) {
		_businessName = businessName;
	} // public synchronized void setBusinessName(String businessName)

	/**
	 * Ritorna il <code>SourceBean</code> contenente gli oggetti istanziati nella business logic in risposta ad una
	 * richiesta di servizio.
	 * 
	 * @return il <code>SourceBean</code> contenente gli oggetti di risposta.
	 */
	public synchronized SourceBean getServiceResponse() {
		return _serviceResponse;
	} // public synchronized SourceBean getActionResponseBean()

	/**
	 * Imposta il <code>SourceBean</code> contenente gli oggetti istanziati nella business logic in risposta ad una
	 * richiesta di servizio.
	 * 
	 * @param il
	 *            <code>SourceBean</code> contenente gli oggetti di risposta.
	 */
	public synchronized void setServiceResponse(SourceBean serviceResponse) {
		_serviceResponse = serviceResponse;
	} // public synchronized void setServiceResponse(SourceBean
		// serviceResponse)

	public synchronized SourceBean getLoopbackServiceRequest() {
		return _loopbackServiceRequest;
	} // public synchronized SourceBean getLoopbackServiceRequest()

	public synchronized void setLoopbackServiceRequest(SourceBean loopbackServiceRequest) {
		_loopbackServiceRequest = loopbackServiceRequest;
	} // public synchronized void setLoopbackServiceRequest(SourceBean
		// loopbackServiceRequest)

	/**
	 * Ritorna il <code>EMFErrorHandler</code> gestore degli errori per una richiesta di servizio.
	 * <p>
	 * 
	 * @return il <code>EMFErrorHandler</code> gestore degli errori.
	 */
	public synchronized EMFErrorHandler getErrorHandler() {
		return _errorHandler;
	} // public synchronized EMFErrorHandler getErrorHandler()

	/**
	 * Imposta il <code>EMFErrorHandler</code> gestore degli errori per una richiesta di servizio.
	 * <p>
	 * 
	 * @return il <code>EMFErrorHandler</code> gestore degli errori.
	 */
	public synchronized void setErrorHandler(EMFErrorHandler errorHandler) {
		_errorHandler = errorHandler;
	} // public synchronized void setErrorHandler(EMFErrorHandler
		// errorHandler)

	/**
	 * Ritorna un oggetto di tipo Element che verrà utilizzato nella rappresentazione in XML dell'oggetto.
	 * 
	 * @return <code>Document<code> un oggetto di tipo Document.
	 */
	public synchronized Element toElement(Document document) {
		String containerName = "RESPONSE_CONTAINER";
		Element responseContainerElement = document.createElement(containerName);
		if (_businessType == null)
			responseContainerElement.setAttribute("BUSINESS_TYPE", "NOT DEFINED");
		else
			responseContainerElement.setAttribute("BUSINESS_TYPE", _businessType);
		if (_businessName == null)
			responseContainerElement.setAttribute("BUSINESS_NAME", "NOT DEFINED");
		else
			responseContainerElement.setAttribute("BUSINESS_NAME", _businessName);
		if (_serviceResponse == null)
			responseContainerElement.setAttribute("SERVICE_RESPONSE", "NOT DEFINED");
		else
			responseContainerElement.appendChild(_serviceResponse.toElement(document));
		if (_errorHandler == null)
			responseContainerElement.setAttribute("ERROR_HANDLER", "NOT DEFINED");
		else
			responseContainerElement.appendChild(_errorHandler.toElement(document));
		responseContainerElement.appendChild(super.toElement(document));
		return responseContainerElement;
	} // public synchronized Element toElement(Document document)
} // public class ResponseContainer extends BaseContainer implements
	// Serializable
