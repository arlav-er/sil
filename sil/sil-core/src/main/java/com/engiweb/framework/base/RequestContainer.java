package com.engiweb.framework.base;

import java.io.Serializable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * La classe <code>RequestContainer</code> implementa un contenitore di oggetti legati ai parametri della richiesta di
 * un servizio, su un canale/device qualsiasi. Dal <code>RequestContainer</code> &egrave; possibile recuperare il
 * riferimento al <code>SessionContainer</code> contenitore dei dati di sessione.
 * <p>
 * 
 * @version 1.0, 11/03/2002
 * @author Luigi Bellio
 * @see BaseContainer
 * @see SessionContainer
 * @see ApplicationContainer
 * @see ResponseContainer
 */
public class RequestContainer extends BaseContainer implements Serializable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RequestContainer.class.getName());
	private static final String THREAD = "THREAD";
	private String _channelType = null;
	private SourceBean _serviceRequest = null;
	private SessionContainer _sessionContainer = null;

	/**
	 * Costruisce un <code>RequestContainer</code> vuoto.
	 * <p>
	 * 
	 * @see RequestContainer#RequestContainer(RequestContainer)
	 */
	public RequestContainer() {
		super();
		_channelType = null;
		_serviceRequest = null;
		_sessionContainer = null;
	} // public RequestContainer()

	/**
	 * Costruisce un <code>RequestContainer</code> copia di <em>container</em>.
	 * <p>
	 * 
	 * @param container
	 *            <code>RequestContainer</code> da copiare
	 * @see RequestContainer#RequestContainer()
	 */
	public RequestContainer(RequestContainer container) {
		super(container);
		_channelType = container._channelType;
		if (container._serviceRequest != null)
			_serviceRequest = (SourceBean) container._serviceRequest.cloneObject();
		if (container._sessionContainer != null)
			_sessionContainer = (SessionContainer) container._sessionContainer.cloneObject();
	} // public RequestContainer(RequestContainer container)

	/**
	 * Ritorna un <code>RequestContainer</code> copia dell'oggetto stesso.
	 * <p>
	 * 
	 * @return una copia del <code>RequestContainer</code> stesso
	 */
	public synchronized CloneableObject cloneObject() {
		return new RequestContainer(this);
	} // public synchronized CloneableObject cloneObject()

	/**
	 * Ritorna il valore dell'attributo con chiave <em>key</em> cercandolo nel <code>RequestContainer</code> corrente e
	 * <em>non</em> nei <code>RequestContainer</code> annidati.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo
	 * @return <em>null</em> se l'attributo non esiste, altrimenti il valore associato.
	 */
	public Object getAttribute(String key) {
		return oneStepGetAttribute(key);
	} // protected Object getAttribute(String key)

	/**
	 * Questo metodo permette di inizializzare lo stato dell'oggetto da un oggetto della stessa classe.
	 * 
	 * @param container
	 *            oggetto della stessa classe.
	 */
	public synchronized void setContainer(RequestContainer container) {
		super.setContainer(container);
		if (container == null) {
			_channelType = null;
			_serviceRequest = null;
			_sessionContainer = null;
		} // if (container == null)
		else {
			_channelType = container._channelType;
			_serviceRequest = container._serviceRequest;
			_sessionContainer = container._sessionContainer;
		} // if (container == null) else
	} // public synchronized void setContainer(RequestContainer container)

	/**
	 * Imposta o modifica il <code>RequestContainer</code> annidato con <em>container</em>.
	 * <p>
	 * 
	 * @param parent
	 *            <code>RequestContainer</code> annidato
	 */
	public synchronized void setParent(RequestContainer container) {
		if (container == null)
			return;
		super.setParent(container);
		if (_sessionContainer == null)
			_sessionContainer = container._sessionContainer;
		else
			_sessionContainer.setParent(container._sessionContainer);
	} // public synchronized void setParent(RequestContainer container)

	/**
	 * Elimina il <code>RequestContainer</code> annidato.
	 * <p>
	 * 
	 * @see RequestContainer#setParent(RequestContainer)
	 */
	public synchronized void delParent() {
		super.delParent();
		if (_sessionContainer != null)
			_sessionContainer.delParent();
	} // public synchronized void delParent()

	/**
	 * Ritorna il <code>SourceBean</code> contenente i parametri della richiesta del servizio.
	 * <p>
	 * 
	 * @return <code>SourceBean</code> il contenitore dei parametri della richiesta
	 * @see RequestContainer#updServiceRequest(SourceBean)
	 */
	public synchronized SourceBean getServiceRequest() {
		return _serviceRequest;
	} // public synchronized SourceBean getServiceRequest()

	public synchronized void setServiceRequest(SourceBean serviceRequest) {
		_serviceRequest = serviceRequest;
	} // public synchronized void setServiceRequest(SourceBean serviceRequest)

	/**
	 * Imposta o sostituisce i parametri del <code>SourceBean</code> contenente i dati della richiesta del servizio con
	 * quelli di <em>serviceRequest</em>.
	 * <p>
	 * 
	 * @param serviceRequest
	 *            <code>SourceBean</code> il contenitore dei parametri da impostare o sostituire
	 * @see RequestContainer#getServiceRequest()
	 */
	public synchronized void updServiceRequest(SourceBean serviceRequest) {
		if (serviceRequest == null) {
			_logger.debug("RequestContainer::updServiceRequest: serviceRequest nullo");

			return;
		} // if (serviceRequest == null)
		it.eng.sil.util.TraceWrapper.debug(_logger,
				"RequestContainer::updServiceRequest: _serviceRequest prima updAttribute", _serviceRequest);

		Vector attributes = serviceRequest.getContainedAttributes();
		for (int i = 0; i < attributes.size(); i++) {
			SourceBeanAttribute sourceBeanAttribute = (SourceBeanAttribute) (attributes.elementAt(i));
			try {
				Object value = sourceBeanAttribute.getValue();
				if (value instanceof SourceBean)
					_serviceRequest.updAttribute((SourceBean) value);
				else
					_serviceRequest.updAttribute(sourceBeanAttribute.getKey(), value);
			} // try
			catch (SourceBeanException ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"RequestContainer::updServiceRequest: _serviceRequest.updAttribute(sourceBeanAttribute.getKey(), sourceBeanAttribute.getValue())",
						ex);

			} // catch (SourceBeanException ex) try
		} // for (int i = 0; i < attributes.size(); i++)
		it.eng.sil.util.TraceWrapper.debug(_logger,
				"RequestContainer::updServiceRequest: _serviceRequest dopo updAttribute", _serviceRequest);

	} // public synchronized void updServiceRequest(SourceBean serviceRequest)

	/**
	 * Ritorna il <code>SessionContainer</code> contenitore dei dati di sessione relativo al
	 * <code>RequestContainer</code> corrente.
	 * <p>
	 * 
	 * @return <code>SessionContainer</code> il contenitore dei dati di sessione
	 * @see RequestContainer#setSessionContainer(SessionContainer)
	 */
	public synchronized SessionContainer getSessionContainer() {
		return _sessionContainer;
	} // public synchronized SessionContainer getSessionContainer()

	/**
	 * Imposta o sostituisce il <code>SessionContainer</code> <em>sessionContainer</em> contenitore dei dati di sessione
	 * relativo al <code>RequestContainer</code> corrente.
	 * <p>
	 * 
	 * @param sessionContainer
	 *            <code>SessionContainer</code> il contenitore dei dati di sessione
	 * @see RequestContainer#getSessionContainer()
	 */
	public synchronized void setSessionContainer(SessionContainer sessionContainer) {
		_sessionContainer = sessionContainer;
	} // public synchronized void setSessionContainer(SessionContainer
		// sessionContainer)

	public synchronized boolean isRequestDistributed() {
		boolean distributeBoolean = false;
		String distributeString = (String) getAttribute("AF_DISTRIBUTED");
		if (distributeString != null)
			distributeBoolean = distributeString.equalsIgnoreCase("TRUE");
		return distributeBoolean;
	} // public synchronized boolean isRequestDistributed()

	public synchronized void setRequestDistributed(boolean distributed) {
		if (!distributed)
			setAttribute("AF_DISTRIBUTED", "FALSE");
		else
			setAttribute("AF_DISTRIBUTED", "TRUE");
	} // public synchronized void setRequestDistributed(boolean distributed)

	/**
	 * Ritorna il tipo di canale utilizzato per le richieste dei servizi.
	 * <p>
	 * 
	 * @return il nome del canale.
	 */
	public synchronized String getChannelType() {
		return _channelType;
	} // public synchronized String getChannelType()

	/**
	 * Questo metodo permette di impostare il tipo di canale utilizzato per le richieste dei servizi.
	 * <p>
	 * 
	 * @param channelType
	 *            il tipo di canale.
	 */
	public synchronized void setChannelType(String channelType) {
		_channelType = channelType;
	} // public synchronized void setChannelType(String channelType)

	/**
	 * Questo metodo statico permette di recuperare l'istanza di <code>RequestContainer</code> dal
	 * <code>ApplicationContainer</code> .
	 * <p>
	 * 
	 * @return <code>RequestContainer</code> il contenitore dei dati .
	 */
	public static RequestContainer getRequestContainer() {
		Thread thread = Thread.currentThread();
		String threadName = thread.toString();
		return (RequestContainer) ApplicationContainer.getInstance().getAttribute(THREAD + "_" + threadName);
	} // public static RequestContainer getRequestContainer()

	/**
	 * Questo metodo statico permette di memorizzare l'istanza di <code>RequestContainer</code> nel
	 * <code>ApplicationContainer</code>.
	 * <p>
	 * 
	 * @param requestContainer
	 *            l'istanza da memorizzare.
	 */
	public static void setRequestContainer(RequestContainer requestContainer) {
		Thread thread = Thread.currentThread();
		String threadName = thread.toString();
		ApplicationContainer.getInstance().setAttribute(THREAD + "_" + threadName, requestContainer);
	} // public static void setRequestContainer(RequestContainer
		// requestContainer)

	/**
	 * Questo metodo statico permette di cancellare l'istanza di <code>RequestContainer</code> dal
	 * <code>ApplicationContainer</code>.
	 */
	public static void delRequestContainer() {
		Thread thread = Thread.currentThread();
		String threadName = thread.toString();
		ApplicationContainer.getInstance().delAttribute(THREAD + "_" + threadName);
	} // public static void delRequestContainer()

	/**
	 * Questo metodo permette di modificare l'istanza di <code>RequestContainer</code>.
	 * <p>
	 * 
	 * @param requestContainer
	 *            l'istanza da cui prendere le modifiche.
	 */
	public synchronized void updRequestContext(RequestContainer request) {
		if (request == null) {
			_logger.debug("RequestContainer::updRequestContext: request nullo");

			return;
		} // if (request == null)
		setAttribute(Constants.HTTP_REQUEST, request.getAttribute(Constants.HTTP_REQUEST));
		setAttribute(Constants.HTTP_RESPONSE, request.getAttribute(Constants.HTTP_RESPONSE));
		setAttribute(Constants.SERVLET_CONFIG, request.getAttribute(Constants.SERVLET_CONFIG));
	} // public synchronized void updRequestContext(RequestContainer request)

	/**
	 * Ritorna un oggetto di tipo Element che verrà utilizzato nella rappresentazione in XML dell'oggetto.
	 * 
	 * @return <code>Document<code> un oggetto di tipo Document.
	 */
	public synchronized Element toElement(Document document) {
		String containerName = "REQUEST_CONTAINER";
		Element requestContainerElement = document.createElement(containerName);
		if (_channelType == null)
			requestContainerElement.setAttribute("CHANNEL_TYPE", "NOT DEFINED");
		else
			requestContainerElement.setAttribute("CHANNEL_TYPE", _channelType);
		if (_serviceRequest == null)
			requestContainerElement.setAttribute("SERVICE_REQUEST", "NOT DEFINED");
		else
			requestContainerElement.appendChild(_serviceRequest.toElement(document));
		requestContainerElement.appendChild(super.toElement(document));
		return requestContainerElement;
	} // public synchronized Element toElement(Document document)
} // public class RequestContainer extends BaseContainer implements
	// Serializable
