package com.engiweb.framework.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * La classe <code>BaseContainer</code> implementa un contenitore di oggetti. Ogni oggetto memorizzato nel contenitore
 * &egrave; associato ad una chiave che ne consente il recupero. Un solo oggetto pu&ograve; essere memorizzato con una
 * data chiave. Un <code>BaseContainer</code> pu&ograve; essere annidato all'interno di un altro
 * <code>BaseContainer</code>.
 * <p>
 * Il contenitore &egrave; in grado di ritornare una sua rappresentazione XML.
 * <p>
 * Un esempio d'uso &egrave; il seguente:
 * <p>
 * <blockquote>
 * 
 * <pre>
 * BaseContainer inner = new BaseContainer();
 * inner.setAttribute(&quot;param1&quot;, &quot;value1&quot;);
 * BaseContainer outer = new BaseContainer();
 * outer.setAttribute(&quot;param2&quot;, &quot;value2&quot;);
 * outer.setParent(inner);
 * </pre>
 * 
 * </blockquote>
 * 
 * @version 1.0, 11/03/2002
 * @author Luigi Bellio
 * @see RequestContainer
 * @see SessionContainer
 * @see ApplicationContainer
 * @see ResponseContainer
 */
public class BaseContainer extends AbstractXMLObject implements CloneableObject, Serializable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(BaseContainer.class.getName());
	/**
	 * Contenitore degli attributi del <code>BaseContainer</code> corrente.
	 */
	private HashMap _visibleAttributes = null;

	/**
	 * Contenitore degli attributi eliminati nel <code>BaseContainer</code> corrente.
	 */
	private ArrayList _hiddenAttributes = null;
	private BaseContainer _parent = null;

	/**
	 * Costruisce un <code>BaseContainer</code> vuoto.
	 * <p>
	 * 
	 * @see BaseContainer#BaseContainer(BaseContainer)
	 */
	public BaseContainer() {
		_visibleAttributes = new HashMap();
		_hiddenAttributes = new ArrayList();
		_parent = null;
	} // public BaseContainer()

	/**
	 * Costruisce un <code>BaseContainer</code> copia di <em>container</em>.
	 * <p>
	 * 
	 * @param container
	 *            <code>BaseContainer</code> da copiare
	 * @see BaseContainer#BaseContainer()
	 */
	public BaseContainer(BaseContainer container) {
		if (container == null) {
			_logger.debug("BaseContainer::BaseContainer: container nullo");

			return;
		} // if (container == null)
		_visibleAttributes = (HashMap) container._visibleAttributes.clone();
		_hiddenAttributes = (ArrayList) container._hiddenAttributes.clone();
		if (container._parent != null)
			_parent = (BaseContainer) container._parent.cloneObject();
	} // public BaseContainer(BaseContainer container)

	/**
	 * Ritorna un <code>BaseContainer</code> copia dell'oggetto stesso.
	 * <p>
	 * 
	 * @return una copia del <code>BaseContainer</code> stesso
	 */
	public synchronized CloneableObject cloneObject() {
		return new BaseContainer(this);
	} // public synchronized CloneableObject cloneObject()

	/**
	 * Ritorna il valore dell'attributo con chiave <em>key</em> cercandolo nel <code>BaseContainer</code> corrente e
	 * <em>non</em> nei <code>BaseContainer</code> annidati.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo
	 * @return <em>null</em> se l'attributo non esiste, altrimenti il valore associato.
	 * @see BaseContainer#getAttribute(String)
	 */
	protected synchronized Object oneStepGetAttribute(String key) {
		if (key == null)
			return null;
		if (_hiddenAttributes.contains(key))
			return null;
		return _visibleAttributes.get(key);
	} // protected synchronized Object oneStepGetAttribute(String key)

	/**
	 * Ritorna il valore dell'attributo con chiave <em>key</em> cercandolo nel <code>BaseContainer</code> corrente e nei
	 * <code>BaseContainer</code> annidati.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo
	 * @return <em>null</em> se l'attributo non esiste, altrimenti il valore associato.
	 * @see BaseContainer#oneStepGetAttribute(String)
	 * @see BaseContainer#setAttribute(String, Object)
	 * @see BaseContainer#delAttribute(String)
	 */
	public synchronized Object getAttribute(String key) {
		if (key == null)
			return null;
		if (_hiddenAttributes.contains(key))
			return null;
		Object value = _visibleAttributes.get(key);
		if (value != null)
			return value;
		if (_parent == null)
			return null;
		return _parent.getAttribute(key);
	} // protected synchronized Object getAttribute(String key)

	public synchronized ArrayList getAttributeNames() {
		ArrayList attributeNames = new ArrayList();
		ArrayList attributeDeletedNames = new ArrayList();
		return getAttributeNames(attributeNames, attributeDeletedNames);
	} // public synchronized ArrayList getAttributeNames()

	protected synchronized ArrayList getAttributeNames(ArrayList attributeNames, ArrayList attributeDeletedNames) {
		attributeDeletedNames.addAll(_hiddenAttributes);
		Set visibleAttributesKeys = _visibleAttributes.keySet();
		Iterator visibleAttributesKeysIterator = visibleAttributesKeys.iterator();
		while (visibleAttributesKeysIterator.hasNext()) {
			String visibleAttributeKey = (String) visibleAttributesKeysIterator.next();
			if (attributeDeletedNames.contains(visibleAttributeKey))
				continue;
			else
				attributeNames.add(visibleAttributeKey);
		}

		if (_parent == null)
			return attributeNames;
		return _parent.getAttributeNames(attributeNames, attributeDeletedNames);
	}

	/**
	 * Imposta o modifica il valore dell'attributo con chiave <em>key</em> con il nuovo valore <em>value</em> nel
	 * <code>BaseContainer</code> corrente.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo
	 * @param value
	 *            valore dell'attributo
	 * @see BaseContainer#getAttribute(String)
	 * @see BaseContainer#delAttribute(String)
	 */
	public synchronized void setAttribute(String key, Object value) {
		if (key == null)
			return;
		_hiddenAttributes.remove(key);
		_visibleAttributes.remove(key);
		if (value == null)
			return;
		_visibleAttributes.put(key, value);
	} // public synchronized void setAttribute(String key, Object value)

	/**
	 * Elimina l'attributo con chiave <em>key</em>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo
	 * @see BaseContainer#getAttribute(String)
	 * @see BaseContainer#setAttribute(String, Object)
	 */
	public synchronized void delAttribute(String key) {
		if (key == null)
			return;
		if (!_hiddenAttributes.contains(key))
			_hiddenAttributes.add(key);
		_visibleAttributes.remove(key);
	} // public synchronized void delAttribute(String key)

	/**
	 * Sostituisce il contenuto del <code>BaseContainer</code> corrente con <em>container</em>.
	 * <p>
	 * 
	 * @param container
	 *            <code>BaseContainer</code> da copiare
	 */
	public synchronized void setContainer(BaseContainer container) {
		if (container == null)
			return;
		_visibleAttributes = container._visibleAttributes;
		_hiddenAttributes = container._hiddenAttributes;
		_parent = container._parent;
	} // public synchronized void setContainer(BaseContainer container)

	/**
	 * Ritorna il <code>BaseContainer</code> annidato.
	 * <p>
	 * 
	 * @return <em>null</em> se il <code>BaseContainer</code> annidato non esiste, altrimenti il
	 *         <code>BaseContainer</code> annidato.
	 * @see BaseContainer#setParent(BaseContainer)
	 * @see BaseContainer#delParent()
	 */
	public synchronized BaseContainer getParent() {
		return _parent;
	} // public synchronized BaseContainer getParent()

	/**
	 * Imposta o modifica il <code>BaseContainer</code> annidato con <em>parent</em>.
	 * <p>
	 * 
	 * @param container
	 *            <code>BaseContainer</code> annidato
	 * @see BaseContainer#getParent()
	 * @see BaseContainer#delParent()
	 */
	public synchronized void setParent(BaseContainer container) {
		_parent = container;
	} // public synchronized void setParent(BaseContainer container)

	/**
	 * Elimina il <code>BaseContainer</code> annidato.
	 * <p>
	 * 
	 * @see BaseContainer#getParent()
	 * @see BaseContainer#setParent(BaseContainer)
	 */
	public synchronized void delParent() {
		_parent = null;
	} // public synchronized void setParent(BaseContainer container)

	public synchronized Element toElement(Document document) {
		String containerName = this.getClass().getName().toUpperCase();
		Element baseContainerElement = document.createElement(containerName);
		Element hiddenAttributesElement = document.createElement("HIDDEN_ATTRIBUTES");
		for (int i = 0; i < _hiddenAttributes.size(); i++) {
			Element hiddenAttributeElement = document.createElement("ATTRIBUTE");
			hiddenAttributeElement.setAttribute("name", (String) _hiddenAttributes.get(i));
			hiddenAttributesElement.appendChild(hiddenAttributeElement);
		} // for (int i = 0; i < _hiddenAttributes.size(); i++)
		baseContainerElement.appendChild(hiddenAttributesElement);
		Element visibleAttributesElement = document.createElement("VISIBLE_ATTRIBUTES");
		Set visibleAttributesKeys = _visibleAttributes.keySet();
		Iterator visibleAttributesKeysIterator = visibleAttributesKeys.iterator();
		while (visibleAttributesKeysIterator.hasNext()) {
			Element visibleAttributeElement = document.createElement("ATTRIBUTE");
			String visibleAttributeKey = (String) visibleAttributesKeysIterator.next();
			visibleAttributeElement.setAttribute("name", visibleAttributeKey);
			String visibleAttributeValue = null;
			try {
				visibleAttributeValue = _visibleAttributes.get(visibleAttributeKey).toString();
			} // try
			catch (Exception ex) {
				visibleAttributeValue = "NOT_AVAILABLE";
			} // catch (Exception ex)
			visibleAttributeElement.setAttribute("value", visibleAttributeValue);
			visibleAttributesElement.appendChild(visibleAttributeElement);
		} // while (visibleAttributesKeysIterator.hasNext())
		baseContainerElement.appendChild(visibleAttributesElement);
		Element parentElement = document.createElement("PARENT");
		if (_parent != null)
			parentElement.appendChild(_parent.toElement(document));
		baseContainerElement.appendChild(parentElement);
		return baseContainerElement;
	} // public synchronized Element toElement(Document document)

	/**
	 * FV Aggiunto in data 15/04/2004
	 * 
	 * @return
	 */
	public ArrayList getHiddenAttributes() {
		return _hiddenAttributes;
	}

	/**
	 * FV Aggiunto in data 15/04/2004
	 * 
	 * @return
	 */
	public HashMap getVisibleAttributes() {
		return _visibleAttributes;
	}

	/**
	 * FV Aggiunto in data 15/04/2004
	 * 
	 * @param list
	 */
	public void setHiddenAttributes(ArrayList list) {
		_hiddenAttributes = list;
	}

	/**
	 * FV Aggiunto in data 15/04/2004
	 * 
	 * @param map
	 */
	public void setVisibleAttributes(HashMap map) {
		_visibleAttributes = map;
	}

} // public class BaseContainer extends AbstractXMLObject implements
	// CloneableObject, Serializable
