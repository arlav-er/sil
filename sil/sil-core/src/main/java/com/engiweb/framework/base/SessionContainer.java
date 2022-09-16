package com.engiweb.framework.base;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * La classe <code>SessionContainer</code> implementa un contenitore di oggetti legati alla sessione dell'utente. Questo
 * contenitore è trasversale alle varie richieste di servizio e può può  essere di tipo <em>permanente</em> oppure
 * <em>non permanente</em>. Il contenitore <em>permanente</em> non viene modificato da nessun comando di navigazione a
 * differenza di quello di tipo <em>non permanente</em>.
 * <p>
 * 
 * @version 1.0, 11/03/2002
 * @author Luigi Bellio
 * @see BaseContainer
 * @see ResponseContainer
 * @see ApplicationContainer
 * @see RequestContainer
 */
public class SessionContainer extends BaseContainer implements Serializable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SessionContainer.class.getName());
	private SessionContainer _permanentContainer = null;

	/**
	 * Costruisce un <code>SessionContainer</code> vuoto. Se il parametro in input è uguale a <em>true</em> allora viene
	 * creato anche un contenitore di tipo permanente.
	 * <p>
	 * 
	 * @param permanent
	 *            valore di tipo boolean che indica se dev'essere costruito il contenitore permanente.
	 * @see BaseContainer
	 */
	public SessionContainer(boolean permanent) {
		super();
		_permanentContainer = null;
		if (permanent)
			_permanentContainer = new SessionContainer(false);
	} // public SessionContainer(boolean permanent)

	/**
	 * Costruisce un <code>SessionContainer</code> copia di <em>container</em>.
	 * <p>
	 * 
	 * @param container
	 *            <code>SessionContainer</code> da copiare
	 */
	public SessionContainer(SessionContainer container) {
		super(container);
		_permanentContainer = container._permanentContainer;
	} // public SessionContainer(SessionContainer container)

	/**
	 * Ritorna un <code>SessionContainer</code> copia dell'oggetto stesso.
	 * <p>
	 * 
	 * @return una copia del <code>SessionContainer</code> stesso
	 */
	public synchronized CloneableObject cloneObject() {
		return new SessionContainer(this);
	} // public synchronized CloneableObject cloneObject()

	/**
	 * Ritorna un <code>CloneableObject</code> copia dell'oggetto richiesto e indentificato da una chiave. Questo
	 * oggetto viene anche inserito nel contenitore di ultimo livello.
	 * <p>
	 * 
	 * @return una copia dell'oggetto richiesto.
	 */
	public synchronized CloneableObject getClonedAttribute(String key) {
		CloneableObject cloneable = null;
		try {
			cloneable = (CloneableObject) getAttribute(key);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"SessionContainer::getClonedAttribute: (CloneableObject)getAttribute(key)", ex);

			return null;
		} // catch (Exception ex)
		if (cloneable == null)
			return null;
		CloneableObject cloned = cloneable.cloneObject();
		setAttribute(key, cloned);
		return cloned;
	} // public synchronized CloneableObject getClonedAttribute(String key)

	/**
	 * Questo metodo permette di inizializzare lo stato dell'oggetto da un oggetto della stessa classe.
	 * 
	 * @param container
	 *            oggetto della stessa classe.
	 */
	public synchronized void setContainer(SessionContainer container) {
		super.setContainer(container);
		if (container == null)
			_permanentContainer = null;
		else
			_permanentContainer = container._permanentContainer;
	} // public synchronized void setContainer(SessionContainer container)

	/**
	 * Questo metodo permette di impostare il riferimento all'istanza padre .
	 * 
	 * @param container
	 *            oggetto della stessa classe.
	 */
	public synchronized void setParent(SessionContainer container) {
		if (container == null)
			return;
		super.setParent(container);
		_permanentContainer = null;
	} // public synchronized void setParent(SessionContainer container)

	/**
	 * Questo metodo permette di eliminare il riferimento all'istanza padre .
	 */
	public synchronized void delParent() {
		_permanentContainer = getPermanentContainer();
		super.delParent();
	} // public synchronized void delParent()

	/**
	 * Questo metodo ritorna l'istanza di <code>SessionContainer</code> di tipo permanente.
	 * 
	 * @return container di tipo permanente.
	 */
	public synchronized SessionContainer getPermanentContainer() {
		if (_permanentContainer == null) {
			SessionContainer parentContainer = (SessionContainer) getParent();
			if (parentContainer != null)
				_permanentContainer = parentContainer.getPermanentContainer();
		} // if (_permanentContainer == null)
		return _permanentContainer;
	} // public synchronized SessionContainer getPermanentContainer()

	/**
	 * Ritorna un oggetto di tipo Element che verrà utilizzato nella rappresentazione in XML dell'oggetto.
	 * 
	 * @return <code>Document<code> un oggetto di tipo Document.
	 */
	public synchronized Element toElement(Document document) {
		String containerName = "SESSION_CONTAINER";
		Element sessionContainerElement = document.createElement(containerName);
		sessionContainerElement.appendChild(super.toElement(document));
		if (_permanentContainer != null) {
			String permanentContainerName = "PERMANENT_CONTAINER";
			Element permanentContainerElement = document.createElement(permanentContainerName);
			permanentContainerElement.appendChild(_permanentContainer.toElement(document));
			sessionContainerElement.appendChild(permanentContainerElement);
		} // if (_permanentContainer != null)
		return sessionContainerElement;
	} // public synchronized Element toElement(Document document)
} // public class SessionContainer extends BaseContainer implements
	// Serializable
