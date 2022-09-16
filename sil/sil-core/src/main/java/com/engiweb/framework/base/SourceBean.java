package com.engiweb.framework.base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Date;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.util.XMLUtil;

/**
 * La classe <code>SourceBean</code> implementa un contenitore di oggetti. Ogni oggetto memorizzato nel contenitore
 * &egrave; associato ad una chiave che ne consente il recupero. Pi&ugrave; oggetti possono essere memorizzati con la
 * stessa chiave. Al contenitore &egrave; associato un nome.
 * <p>
 * Il contenitore &egrave; in grado di ritornare una sua rappresentazione XML e di costruirsi a partire da uno stream
 * XML (File, String, ...).
 * <p>
 * Un esempio d'uso &egrave; il seguente:
 * <p>
 * <blockquote>
 * 
 * <pre>
 * SourceBean inner = new SourceBean(&quot;inner&quot;);
 * inner.setAttribute(&quot;param1&quot;, 1);
 * inner.setAttribute(&quot;param2&quot;, &quot;value2&quot;);
 * SourceBean outer = new SourceBean(&quot;outer&quot;);
 * outer.setAttribute(&quot;param3&quot;, &quot;value3&quot;);
 * outer.setAttribute(inner);
 * </pre>
 * 
 * </blockquote>
 * <p>
 * e la relativa rappresentazione XML &egrave;:
 * <p>
 * <blockquote>
 * 
 * <pre>
 * &lt;OUTER param3=&quot;value3&quot;&gt;
 *     &lt;INNER param1=&quot;1&quot; param2=&quot;value2&quot;&gt;
 *     &lt;/INNER&gt;
 * &lt;/OUTER&gt;
 * </pre>
 * 
 * </blockquote>
 * <p>
 * Per recuperare il/i valori di un attributo il servizio da invocare &egrave; del tipo: <blockquote>
 * 
 * <pre>
 * String value2 = (String) outer.getAttribute(&quot;inner.param2&quot;);
 * </pre>
 * 
 * </blockquote>
 * <p>
 * La chiave di un attributo pu&ograve; sempre essere espressa con una dot-notation: <blockquote>
 * 
 * <pre>
 * String key = &quot;key1.key2.key3&quot;;
 * </pre>
 * 
 * </blockquote>
 * 
 * @version 1.0, 06/03/2002
 * @author Luigi Bellio
 * @see SourceBeanAttribute
 */
public class SourceBean extends AbstractXMLObject implements CloneableObject, Serializable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SourceBean.class.getName());
	/**
	 * Nome del <code>SourceBean</code>.
	 */
	private String _sourceBeanName = null;

	/**
	 * Vettore di oggetti di tipo <code>SourceBeanAttribute</code>.
	 */
	private Vector _attributes = null;

	/**
	 * Testo contenuto nel <code>SourceBean</code>.
	 */
	private String _characters = null;

	/**
	 * Costruisce un <code>SourceBean</code> vuoto con nome <em>name</em>.
	 * <p>
	 * 
	 * @param name
	 *            nome del <code>SourceBean</code>
	 * @exception SourceBeanException
	 *                viene lanciata se il parametro di input <em>name</em> non &egrave; una nome valido
	 * @see SourceBean#SourceBean(SourceBean)
	 */
	public SourceBean(String name) throws SourceBeanException {
		// TracerSingleton.log(Constants.NOME_MODULO,// TracerSingleton.DEBUG,
		// "SourceBean::SourceBean: name [" + name + "]");
		_sourceBeanName = SourceBeanAttribute.validateKey(name);
		_attributes = new Vector();
		_characters = null;
	} // public SourceBean(String name) throws SourceBeanException

	/**
	 * Costruisce un <code>SourceBean</code> copia di <em>sourceBean</em>.
	 * <p>
	 * 
	 * @param sourceBean
	 *            <code>SourceBean</code> da copiare
	 * @exception SourceBeanException
	 *                viene lanciata se il parametro di input <em>sourceBean</em> &egrave; nullo
	 * @see SourceBean#SourceBean(String)
	 */
	public SourceBean(SourceBean sourceBean) throws SourceBeanException {
		// TracerSingleton.log(Constants.NOME_MODULO,// TracerSingleton.DEBUG,
		// "SourceBean::SourceBean: invocato");
		if (sourceBean == null)
			throw new SourceBeanException("sourceBean non valido");
		_sourceBeanName = sourceBean._sourceBeanName;
		_attributes = new Vector();
		for (int i = 0; i < sourceBean._attributes.size(); i++) {
			SourceBeanAttribute attribute = (SourceBeanAttribute) (sourceBean._attributes.elementAt(i));
			_attributes.addElement(attribute.cloneObject());
		} // for (int i = 0; i < sourceBean._attributes.size(); i++)
		_characters = sourceBean._characters;
	} // public SourceBean(SourceBean sourceBean) throws SourceBeanException

	/**
	 * Ritorna un <code>CloneableObject</code> copia <em>non profonda</em> dell'oggetto stesso.
	 * <p>
	 * 
	 * @return una copia <em>non profonda</em> del <code>SourceBean</code> stesso
	 */
	public synchronized CloneableObject cloneObject() {
		SourceBean clonedObject = null;
		try {
			clonedObject = new SourceBean(this);
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "SourceBean::cloneObject: clonedObject = new SourceBean(this)",
					ex);

		} // catch (SourceBeanException ex) try
		return clonedObject;
	} // public synchronized CloneableObject cloneObject()

	/**
	 * Elimina tutto il conenuto del <code>SourceBean</code>.
	 * <p>
	 * 
	 * @see SourceBean#clearBean(String)
	 */
	public/* synchronized */void clearBean() {
		delContainedAttributes();
		delCharacters();
	} // public /*synchronized*/ void clearBean()

	/**
	 * Elimina tutto il contenuto del <code>SourceBean</code> corrispondente all'attributo di chiave <em>key</em>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo in dot-notation
	 * @see SourceBean#clearBean()
	 */
	public/* synchronized */void clearBean(String key) {
		delContainedAttributes(key);
		delCharacters(key);
	} // public /*synchronized*/ void clearBean(String key)

	/**
	 * Copia tutto il contenuto del parametro <code>SourceBean</code> nel proprio stato.
	 * <p>
	 * 
	 * @param sourceBean
	 *            <code>SourceBean</code> di riferimento.
	 */
	public/* synchronized */void setBean(SourceBean sourceBean) {
		clearBean();
		if (sourceBean == null)
			return;
		setContainedAttributes(sourceBean.getContainedAttributes());
		setCharacters(sourceBean.getCharacters());
	} // public /*synchronized*/ void setBean(SourceBean sourceBean)

	/**
	 * Copia il contenuto del parametro <code>SourceBean</code> nel proprio stato a partire dall'attributo con chiave
	 * <em>key</em>.
	 * <p>
	 * 
	 * @param key
	 *            <code>String</code> key che identifica un elemento del sourceBean.
	 * @param sourceBean
	 *            <code>SourceBean</code> di riferimento.
	 * @exception SourceBeanException
	 *                viene lanciata se la chiave non fa riferimento a nessun elemento.
	 */
	public/* synchronized */void setBean(String key, SourceBean sourceBean) throws SourceBeanException {
		Object attribute = getAttribute(key);
		if ((attribute == null) || (!(attribute instanceof SourceBean))) {
			_logger.warn("SourceBean::setBean: chiave errata");

			throw new SourceBeanException("Chiave errata");
		} // if ((attribute == null) || (!(attribute instanceof SourceBean)))
		((SourceBean) attribute).setBean(sourceBean);
	} // public /*synchronized*/ void setBean(String key, SourceBean
		// sourceBean) throws SourceBeanException

	/**
	 * Ritorna il nome del <code>SourceBean</code>.
	 * <p>
	 * 
	 * @return il nome del <code>SourceBean</code>
	 * @see SourceBean#SourceBean(String)
	 */
	public/* synchronized */String getName() {
		return _sourceBeanName;
	} // public /*synchronized*/ String getName()

	/*
	 * public void setName(String name) throws SourceBeanException { _sourceBeanName =
	 * SourceBeanAttribute.validateKey(name); } // public String getName()
	 */

	/**
	 * Ritorna tutti gli oggetti di tipo <code>SourceBeanAttribute</code> il cui campo chiave vale <em>key</em>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo in dot-notation
	 * @return
	 *         <ul>
	 *         <li><em>null</em> se l'attributo non esiste
	 *         <li>il <code>SourceBeanAttribute</code> corrispondente alla chiave se l'attributo &egrave; single-value
	 *         <li><em>Vector</em> il vettore di <code>SourceBeanAttribute</code> corrispondenti alla chiave se
	 *         l'attributo &egrave; multi-value
	 *         </ul>
	 * @see SourceBean#getAttribute(String)
	 */
	public/* synchronized */Object getAttributeItem(String key) {
		if (key == null) {
			_logger.warn("SourceBean::getAttributeItem: chiave nulla");

			return null;
		} // if (key == null)
			// TracerSingleton.log(Constants.NOME_MODULO,// TracerSingleton.DEBUG,
			// "SourceBean::getAttributeItem: key [" + key + "]");
		boolean deepSearch = (key.indexOf('.') != -1);
		String searchKey = key;
		if (deepSearch)
			searchKey = key.substring(0, key.indexOf('.'));
		// TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.DEBUG,//
		// "SourceBean::getAttributeItem: searchKey [" + searchKey + "]");
		Vector values = new Vector();
		for (int i = 0; i < _attributes.size(); i++) {
			SourceBeanAttribute attribute = (SourceBeanAttribute) (_attributes.elementAt(i));
			if (attribute.getKey().equalsIgnoreCase(searchKey))
				values.addElement(attribute);
		} // for (int i = 0; i < _attributes.size(); i++)
		if (values.size() == 0) {
			// TracerSingleton.log(Constants.NOME_MODULO,//
			// TracerSingleton.DEBUG,// "SourceBean::getAttributeItem: attributo
			// ["// + searchKey + "] non trovato");
			return null;
		} // if (values.size() == 0)
		if (values.size() == 1) {
			if (deepSearch) {
				if (!((((SourceBeanAttribute) (values.elementAt(0))).getValue()) instanceof SourceBean)) {
					_logger.warn("SourceBean::getAttributeItem: attributo [" + searchKey + "] non è un SourceBean");

					return null;
				} // if (!(value instanceof SourceBean))
				String childKey = key.substring(key.indexOf('.') + 1, key.length());
				return ((SourceBean) (((SourceBeanAttribute) (values.elementAt(0))).getValue()))
						.getAttributeItem(childKey);
			} // if (deepSearch)
			return values.elementAt(0);
		} // if (values.size() == 1)
		if (deepSearch) {
			_logger.warn("SourceBean::getAttributeItem: attributo [" + searchKey
					+ "] multivalore, impossibile eseguire una ricerca nidificata");

			return null;
		} // if (deepSearch)
		return values;
	} // public /*synchronized*/ Object getAttributeItem(String key)

	/**
	 * Ritorna true se l'oggetto sourceBean contiene almento un elemento con chiave <em>key</em>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo in dot-notation
	 * @return esito della ricerca dell'elemento.
	 */
	public/* synchronized */boolean containsAttribute(String key) {
		Object value = getAttributeItem(key);
		return (value != null) ? true : false;
	} // public /*synchronized*/ boolean containsAttribute(String key)

	/**
	 * Ritorna tutti i valori dell'attributo con chiave <em>key</em>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo in dot-notation
	 * @return
	 *         <ul>
	 *         <li><em>null</em> se l'attributo non esiste
	 *         <li>l'oggetto corrispondente alla chiave se l'attributo &egrave; single-value
	 *         <li><em>Vector</em> il vettore degli oggetti corrispondenti alla chiave se l'attributo &egrave;
	 *         multi-value
	 *         </ul>
	 * @see SourceBean#setAttribute(String, Object)
	 * @see SourceBean#setAttribute(SourceBean)
	 * @see SourceBean#updAttribute(String, Object)
	 * @see SourceBean#delAttribute(String)
	 */
	public/* synchronized */Object getAttribute(String key) {
		// TracerSingleton.log(Constants.NOME_MODULO,// TracerSingleton.DEBUG,
		// "SourceBean::getAttribute: key [" + key + "]");
		Object attributeItem = getAttributeItem(key);
		if (attributeItem == null)
			return null;
		if (attributeItem instanceof SourceBeanAttribute) {
			// TracerSingleton.log(Constants.NOME_MODULO,//
			// TracerSingleton.DEBUG,// "SourceBean::getAttributeItem: attributo
			// ["// + key + "] trovato");
			SourceBeanAttribute sourceBeanAttribute = (SourceBeanAttribute) attributeItem;
			return sourceBeanAttribute.getValue();
		} // if (attributeItem instanceof SourceBeanAttribute)
			// TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.DEBUG,//
			// "SourceBean::getAttributeItem: attributo [" + key +// "]
			// multivalore");
		Vector values = new Vector();
		Vector sourceBeanAttributes = (Vector) attributeItem;
		for (int i = 0; i < sourceBeanAttributes.size(); i++) {
			SourceBeanAttribute sourceBeanAttribute = (SourceBeanAttribute) (sourceBeanAttributes.elementAt(i));
			values.addElement(sourceBeanAttribute.getValue());
		} // for (int i = 0; i < sourceBeanAttributes.size(); i++)
		return values;
	} // public /*synchronized*/ Object getAttribute(String key)

	/**
	 * Ritorna tutti i valori dell'attributo con chiave <em>key</em>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo in dot-notation
	 * @return <em>Vector</em> il vettore degli oggetti corrispondenti alla chiave, di dimensione nulla se nessun
	 *         attributo viene trovato
	 * @see SourceBean#setAttribute(String, Object)
	 * @see SourceBean#setAttribute(SourceBean)
	 * @see SourceBean#updAttribute(String, Object)
	 * @see SourceBean#delAttribute(String)
	 */
	public/* synchronized */Vector getAttributeAsVector(String key) {
		Object value = getAttribute(key);
		if (value == null)
			return new Vector();
		if (value instanceof Vector)
			return (Vector) value;
		Vector values = new Vector();
		values.add(value);
		return values;
	} // public /*synchronized*/ Vector getAttributeAsVector(String key)

	/**
	 * Aggiunge al <code>SourceBean</code> un nuovo attributo con chiave <em>key</em> e valore <em>value</em>. Se il
	 * valore dell'attributo &egrave; un <code>SourceBean</code> il servizio &egrave; equivalente a: <blockquote>
	 * 
	 * <pre>
	 * SourceBean keySourceBean = new SourceBean(key);
	 * keySourceBean.setAttribute(value);
	 * this.setAttribute(keySourceBean);
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param key
	 *            chiave dell'attributo in dot-notation
	 * @param value
	 *            valore dell'attributo
	 * @exception SourceBeanException
	 *                viene lanciata in tutti i casi in cui la chiave espressa in dot-notation non sia corretta
	 * @see SourceBean#getAttribute(String)
	 * @see SourceBean#setAttribute(SourceBean)
	 * @see SourceBean#updAttribute(String, Object)
	 * @see SourceBean#delAttribute(String)
	 */
	public/* synchronized */void setAttribute(String key, Object value) throws SourceBeanException {
		if (key == null) {
			_logger.warn("SourceBean::setAttribute: chiave nulla");

			throw new SourceBeanException("Chiave nulla");
		} // if (key == null)
			// TracerSingleton.log(Constants.NOME_MODULO,// TracerSingleton.DEBUG,
			// "SourceBean::setAttribute: key [" + key + "]");
		if (key.indexOf('.') != -1) {
			String searchKey = key.substring(0, key.indexOf('.'));
			String childKey = key.substring(key.indexOf('.') + 1, key.length());
			Object attribute = getAttribute(searchKey);
			if ((attribute != null) && (attribute instanceof Vector)) {
				Vector filteredAttribute = new Vector();
				for (int i = 0; i < ((Vector) attribute).size(); i++) {
					Object attributeItem = ((Vector) attribute).elementAt(i);
					if (attributeItem instanceof SourceBean)
						filteredAttribute.addElement(attributeItem);
				} // for (int i = 0; i < ((Vector)attribute).size(); i++)
				if (filteredAttribute.size() == 0)
					attribute = null;
				else if (filteredAttribute.size() == 1)
					attribute = filteredAttribute.elementAt(0);
				else
					attribute = filteredAttribute;
			} // if (attribute instanceof Vector)
			if ((attribute != null) && !(attribute instanceof Vector) && !(attribute instanceof SourceBean))
				attribute = null;
			if (attribute == null) {
				SourceBean sourceBean = new SourceBean(searchKey);
				sourceBean.setAttribute(childKey, value);
				setAttribute(sourceBean);
				return;
			} // if (attribute == null)
			if (attribute instanceof SourceBean) {
				SourceBean sourceBean = (SourceBean) attribute;
				sourceBean.setAttribute(childKey, value);
				return;
			} // if (attribute instanceof SourceBean)
			_logger.warn("SourceBean::setAttribute: attributo [" + searchKey
					+ "] multivalore, impossibile eseguire una ricerca nidificata");

			throw new SourceBeanException(
					"Attributo [" + searchKey + "] multivalore, impossibile eseguire una ricerca nidificata");
		} // if (key.indexOf('.') != -1)
		if (value instanceof SourceBean) {
			SourceBean sourceBean = new SourceBean(key);
			sourceBean.setAttribute((SourceBean) value);
			setAttribute(sourceBean);
			return;
		} // if (value instanceof SourceBean)
		_attributes.addElement(new SourceBeanAttribute(key, value));
	} // public /*synchronized*/ void setAttribute(Object key, Object value)
		// throws SourceBeanException

	/**
	 * Aggiunge al <code>SourceBean</code> un nuovo attributo il cui valore &egrave; un <code>SourceBean</code>. La
	 * chiave con cui il secondo <code>SourceBean</code> viene aggiunto &egrave; pari al nome del contenitore stesso.
	 * <p>
	 * 
	 * @param value
	 *            <code>SourceBean</code> da aggiungere
	 * @exception SourceBeanException
	 *                viene lanciata se <em>value</em> &egrave; <em>null</em>
	 * @see SourceBean#getAttribute(String)
	 * @see SourceBean#setAttribute(String, Object)
	 * @see SourceBean#updAttribute(String, Object)
	 * @see SourceBean#delAttribute(String)
	 */
	public/* synchronized */void setAttribute(SourceBean value) throws SourceBeanException {
		// TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.DEBUG,//
		// "SourceBean::setAttribute: value [" + value.getName() + "]");
		SourceBeanAttribute.validateValue(value);
		_attributes.addElement(new SourceBeanAttribute(value.getName(), value));
	} // public /*synchronized*/ void setAttribute(SourceBean value) throws
		// SourceBeanException

	/**
	 * Sostituisce il valore dell'attributo con chiave <em>key</em> con il nuovo valore <em>value</em>. Se l'attributo
	 * non esiste viene aggiunto. Nel caso in cui alla chiave corrisponda un attributo multi-value viene lanciata
	 * l'eccezione <code>SourceBeanException</code>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo in dot-notation
	 * @param value
	 *            valore dell'attributo
	 * @exception SourceBeanException
	 *                viene lanciata in tutti i casi in cui la chiave espressa in dot-notation non sia corretta
	 * @see SourceBean#getAttribute(String)
	 * @see SourceBean#setAttribute(String, Object)
	 * @see SourceBean#setAttribute(SourceBean)
	 * @see SourceBean#delAttribute(String)
	 */
	public/* synchronized */void updAttribute(String key, Object value) throws SourceBeanException {
		if (key == null) {
			_logger.warn("SourceBean::updAttribute: chiave nulla");

			throw new SourceBeanException("Chiave nulla");
		} // if (key == null)
			// TracerSingleton.log(Constants.NOME_MODULO,// TracerSingleton.DEBUG,
			// "SourceBean::updAttribute: key [" + key + "]");
		if (key.indexOf('.') != -1) {
			String searchKey = key.substring(0, key.indexOf('.'));
			String childKey = key.substring(key.indexOf('.') + 1, key.length());
			Object attribute = getAttribute(searchKey);
			if (attribute == null) {
				SourceBean sourceBean = new SourceBean(searchKey);
				sourceBean.updAttribute(childKey, value);
				setAttribute(sourceBean);
				return;
			} // if (attribute == null)
			if (attribute instanceof SourceBean) {
				SourceBean sourceBean = (SourceBean) attribute;
				sourceBean.updAttribute(childKey, value);
				return;
			} // if (attribute instanceof SourceBean)
			if (attribute instanceof Vector) {
				_logger.warn("SourceBean::updAttribute: attributo [" + searchKey
						+ "] multivalore, impossibile eseguire una ricerca nidificata");

				throw new SourceBeanException(
						"Attributo [" + searchKey + "] multivalore, impossibile eseguire una ricerca nidificata");
			} // if (attribute instanceof Vector)
			_logger.warn("SourceBean::updAttribute: attributo [" + searchKey + "] non è un SourceBean");

			throw new SourceBeanException("Attributo [" + searchKey + "] non è un SourceBean");
		} // if (key.indexOf('.') != -1)
		Object attributeItem = getAttributeItem(key);
		if (attributeItem == null) {
			setAttribute(key, value);
			return;
		} // if (attributeItem == null)
		if (attributeItem instanceof Vector) {
			_logger.warn("SourceBean::updAttribute: attributo [" + key
					+ "] multivalore, impossibile eseguire aggiornamento");

			throw new SourceBeanException("Attributo [" + key + "] multivalore, impossibile eseguire aggiornamento");
		} // if (attributeItem instanceof Vector)
		SourceBeanAttribute sourceBeanAttribute = (SourceBeanAttribute) attributeItem;
		if (value instanceof SourceBean) {
			SourceBean sourceBean = new SourceBean(key);
			sourceBean.setAttribute((SourceBean) value);
			sourceBeanAttribute.setValue(sourceBean);
			return;
		} // if (value instanceof SourceBean)
		sourceBeanAttribute.setValue(value);
	} // public /*synchronized*/ void updAttribute(Object key, Object value)
		// throws SourceBeanException

	public/* synchronized */void updAttribute(SourceBean value) throws SourceBeanException {
		Object attributeItem = getAttributeItem(value.getName());
		if (attributeItem == null) {
			setAttribute(value);
			return;
		} // if (attributeItem == null)
		if (attributeItem instanceof Vector) {
			_logger.warn("SourceBean::updAttribute: attributo [" + value.getName()
					+ "] multivalore, impossibile eseguire aggiornamento");

			throw new SourceBeanException(
					"Attributo [" + value.getName() + "] multivalore, impossibile eseguire aggiornamento");
		} // if (attributeItem instanceof Vector)
		SourceBeanAttribute sourceBeanAttribute = (SourceBeanAttribute) attributeItem;
		// sourceBeanAttribute.setKey(value.getName());
		sourceBeanAttribute.setValue(value);
	} // public /*synchronized*/ void updAttribute(SourceBean value) throws
		// SourceBeanException

	/**
	 * Elimina tutti i valori dell'attributo con chiave <em>key</em>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo in dot-notation
	 * @exception SourceBeanException
	 *                viene lanciata in tutti i casi in cui la chiave espressa in dot-notation non sia corretta
	 * @see SourceBean#getAttribute(String)
	 * @see SourceBean#setAttribute(String, Object)
	 * @see SourceBean#setAttribute(SourceBean)
	 * @see SourceBean#updAttribute(String, Object)
	 */
	public/* synchronized */void delAttribute(String key) throws SourceBeanException {
		if (key == null) {
			_logger.warn("SourceBean::delAttribute: chiave nulla");

			throw new SourceBeanException("Chiave nulla");
		} // if (key == null)
			// TracerSingleton.log(Constants.NOME_MODULO,// TracerSingleton.DEBUG,
			// "SourceBean::delAttribute: key [" + key + "]");
		if (key.indexOf('.') != -1) {
			String searchKey = key.substring(0, key.indexOf('.'));
			Vector values = new Vector();
			for (int i = 0; i < _attributes.size(); i++) {
				SourceBeanAttribute attribute = (SourceBeanAttribute) (_attributes.elementAt(i));
				if (attribute.getKey().equalsIgnoreCase(searchKey))
					values.addElement(attribute.getValue());
			} // for (int i = 0; i < _attributes.size(); i++)
			if (values.size() == 0) {
				// TracerSingleton.log(Constants.NOME_MODULO,//
				// TracerSingleton.DEBUG,// "SourceBean::delAttribute://
				// attributo [" + searchKey + "] non trovato");
				return;
			} // if (values.size() == 0)
			if (values.size() == 1) {
				if (!((values.elementAt(0)) instanceof SourceBean)) {
					_logger.warn("SourceBean::delAttribute: attributo [" + searchKey + "] non è un SourceBean");

					throw new SourceBeanException("Attributo [" + searchKey + "] non è un SourceBean");
				} // if (!(value instanceof SourceBean))
				((SourceBean) (values.elementAt(0))).delAttribute(key.substring(key.indexOf('.') + 1, key.length()));
				return;
			} // if (values.size() == 1)
			_logger.warn("SourceBean::delAttribute: attributo [" + searchKey
					+ "] multivalore, impossibile eseguire una ricerca nidificata");

			throw new SourceBeanException(
					"Attributo [" + searchKey + "] multivalore, impossibile eseguire una ricerca nidificata");
		} // if (key.indexOf('.') != -1)
		Vector sourceBeanAttributesToRemove = new Vector();
		for (int i = 0; i < _attributes.size(); i++)
			if ((((SourceBeanAttribute) (_attributes.elementAt(i))).getKey()).equalsIgnoreCase(key))
				sourceBeanAttributesToRemove.addElement(_attributes.elementAt(i));
		for (int i = 0; i < sourceBeanAttributesToRemove.size(); i++)
			_attributes.removeElement(sourceBeanAttributesToRemove.elementAt(i));
	} // public /*synchronized*/ void delAttribute(String key) throws
		// SourceBeanException

	/**
	 * Ritorna il testo contenuto nel <code>SourceBean</code>.
	 * <p>
	 * 
	 * @return il testo contenuto
	 * @see SourceBean#getCharacters(String)
	 * @see SourceBean#setCharacters(String)
	 * @see SourceBean#setCharacters(String, String)
	 * @see SourceBean#delCharacters()
	 * @see SourceBean#delCharacters(String)
	 */
	public/* synchronized */String getCharacters() {
		return _characters;
	} // public /*synchronized*/ String getCharacters()

	/**
	 * Ritorna il testo contenuto nel <code>SourceBean</code> corrispondente all'attributo di chiave <em>key</em>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo in dot-notation
	 * @return il testo contenuto
	 * @see SourceBean#getCharacters()
	 * @see SourceBean#setCharacters(String)
	 * @see SourceBean#setCharacters(String, String)
	 * @see SourceBean#delCharacters()
	 * @see SourceBean#delCharacters(String)
	 */
	public/* synchronized */String getCharacters(String key) {
		// TracerSingleton.log(Constants.NOME_MODULO,// TracerSingleton.DEBUG,
		// "SourceBean::getCharacters: key [" + key + "]");
		Object attribute = getAttribute(key);
		if ((attribute == null) || (!(attribute instanceof SourceBean)))
			return null;
		return ((SourceBean) attribute).getCharacters();
	} // public /*synchronized*/ String getCharacters(String key)

	/**
	 * Sostituisce il testo contenuto con quello del parametro <em>characters</em>.
	 * <p>
	 * 
	 * @param characters
	 *            il nuovo testo
	 * @see SourceBean#getCharacters(String)
	 * @see SourceBean#getCharacters(String, String)
	 * @see SourceBean#setCharacters(String, String)
	 * @see SourceBean#delCharacters()
	 * @see SourceBean#delCharacters(String)
	 */
	public/* synchronized */void setCharacters(String characters) {
		delCharacters();
		if ((characters == null) || (characters.trim().length() == 0))
			return;
		_characters = characters.trim();
	} // public /*synchronized*/ void setCharacters(String characters)

	/**
	 * Sostituisce il testo contenuto nel <code>SourceBean</code> corrispondente all'attributo di chiave <em>key</em>
	 * con quello del parametro <em>characters</em>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo in dot-notation
	 * @param characters
	 *            il nuovo testo
	 * @see SourceBean#getCharacters(String)
	 * @see SourceBean#getCharacters(String, String)
	 * @see SourceBean#setCharacters(String)
	 * @see SourceBean#delCharacters()
	 * @see SourceBean#delCharacters(String)
	 */
	public/* synchronized */void setCharacters(String key, String characters) throws SourceBeanException {
		Object attribute = getAttribute(key);
		if ((attribute == null) || (!(attribute instanceof SourceBean))) {
			_logger.warn("SourceBean::setCharacters: chiave errata");

			throw new SourceBeanException("Chiave errata");
		} // if ((attribute == null) || (!(attribute instanceof SourceBean)))
		((SourceBean) attribute).setCharacters(characters);
	} // public /*synchronized*/ void setCharacters(String key, String
		// characters) throws SourceBeanException

	/**
	 * Elimina il testo contenuto nel <code>SourceBean</code>.
	 * <p>
	 * 
	 * @see SourceBean#getCharacters()
	 * @see SourceBean#getCharacters(String)
	 * @see SourceBean#setCharacters(String)
	 * @see SourceBean#setCharacters(String, String)
	 * @see SourceBean#delCharacters(String)
	 */
	public/* synchronized */void delCharacters() {
		_characters = null;
	} // public /*synchronized*/ void delCharacters()

	/**
	 * Elimina il testo contenuto nel <code>SourceBean</code> corrispondente all'attributo di chiave <em>key</em>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo in dot-notation
	 * @see SourceBean#getCharacters()
	 * @see SourceBean#getCharacters(String)
	 * @see SourceBean#setCharacters(String)
	 * @see SourceBean#setCharacters(String, String)
	 * @see SourceBean#delCharacters()
	 */
	public/* synchronized */void delCharacters(String key) {
		// TracerSingleton.log(Constants.NOME_MODULO,// TracerSingleton.DEBUG,
		// "SourceBean::getCharacters: key [" + key + "]");
		Object attribute = getAttribute(key);
		if ((attribute == null) || (!(attribute instanceof SourceBean)))
			return;
		((SourceBean) attribute).delCharacters();
	} // public /*synchronized*/ void delCharacters(String key)

	/**
	 * Ritorna un vector contenente tutti i valori dell'attributo con chiave <em>key</em> che sono di tipo
	 * <code>SourceBean</code> e contengono un attributo single-value con chiave <em>paramName</em> e valore
	 * <em>paramValue</em>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo in dot-notation
	 * @param paramName
	 *            nome del parametro di filtro
	 * @param paramValue
	 *            valore del parametro di filtro
	 * @return <em>Vector</em> il vettore di <code>SourceBean</code> se pi&ugrave; valori vengono trovati
	 *         </ul>
	 * @see SourceBean#getAttribute(String)
	 */
	public/* synchronized */Vector getFilteredSourceBeanAttributeAsVector(String key, String paramName,
			String paramValue) {
		Vector sourceBeanAttributeValues = new Vector();
		if (key == null) {
			_logger.warn("SourceBean::getFilteredSourceBeanAttributeAsVector: chiave nulla");

			return sourceBeanAttributeValues;
		} // if (key == null)
		if (paramName == null) {
			_logger.warn("SourceBean::getFilteredSourceBeanAttributeAsVector: nome parametro nullo");

			return sourceBeanAttributeValues;
		} // if ((paramName == null)
			// TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.DEBUG,//
			// "SourceBean::getFilteredSourceBeanAttribute: key// [" + key + "],
			// paramName [" + paramName +// "], paramValue [" + paramValue + "]");
		Object attributeValue = getAttribute(key);
		if (attributeValue == null)
			return sourceBeanAttributeValues;
		Vector attributeValues = null;
		if (attributeValue instanceof Vector)
			attributeValues = (Vector) attributeValue;
		else {
			attributeValues = new Vector();
			attributeValues.addElement(attributeValue);
		} // if (attributeValue instanceof Vector)
		for (int i = 0; i < attributeValues.size(); i++) {
			attributeValue = attributeValues.elementAt(i);
			if (!(attributeValue instanceof SourceBean))
				continue;
			Object localParamValue = ((SourceBean) attributeValue).getAttribute(paramName);
			if (((localParamValue == null) && (paramValue != null))
					|| ((localParamValue != null) && (paramValue == null)))
				continue;
			if (((localParamValue == null) && (paramValue == null)) || ((localParamValue instanceof String)
					&& (((String) localParamValue).equalsIgnoreCase(paramValue))))
				sourceBeanAttributeValues.addElement(attributeValue);
		} // for (int i = 0; i < attributeValues.size(); i++)
		return sourceBeanAttributeValues;
	} // public /*synchronized*/ Vector

	// getFilteredSourceBeanAttributeAsVector(String key, String
	// paramName, String paramValue)

	/**
	 * Ritorna tutti i valori dell'attributo con chiave <em>key</em> che sono di tipo <code>SourceBean</code> e
	 * contengono un attributo single-value con chiave <em>paramName</em> e valore <em>paramValue</em>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo in dot-notation
	 * @param paramName
	 *            nome del parametro di filtro
	 * @param paramValue
	 *            valore del parametro di filtro
	 * @return
	 *         <ul>
	 *         <li><em>null</em> se nessun valore viene trovato
	 *         <li><code>SourceBean</code> se un solo valore viene trovato
	 *         <li><em>Vector</em> il vettore di <code>SourceBean</code> se pi&ugrave; valori vengono trovati
	 *         </ul>
	 * @see SourceBean#getAttribute(String)
	 */
	public/* synchronized */Object getFilteredSourceBeanAttribute(String key, String paramName, String paramValue) {
		Vector sourceBeanAttributeValues = getFilteredSourceBeanAttributeAsVector(key, paramName, paramValue);
		if (sourceBeanAttributeValues.size() == 0)
			return null;
		if (sourceBeanAttributeValues.size() == 1)
			return sourceBeanAttributeValues.get(0);
		return sourceBeanAttributeValues;
	} // public /*synchronized*/ Object getFilteredSourceBeanAttribute(String

	// key, String paramName, String paramValue)

	/**
	 * Ritorna tutti gli oggetti di tipo <code>SourceBeanAttribute</code> contenuti.
	 * <p>
	 * 
	 * @return <code>Vector</code> il vettore di <code>SourceBeanAttribute</code> contenuti
	 * @see SourceBean#getContainedAttributes(String)
	 * @see SourceBean#setContainedAttributes(Vector)
	 * @see SourceBean#setContainedAttributes(String, Vector)
	 * @see SourceBean#delContainedAttributes()
	 * @see SourceBean#delContainedAttributes(String)
	 */
	public/* synchronized */Vector getContainedAttributes() {
		return (Vector) _attributes.clone();
	} // public /*synchronized*/ Vector getContainedAttributes()

	/**
	 * Ritorna tutti gli oggetti di tipo <code>SourceBeanAttribute</code> contenuti nel <code>SourceBean</code>
	 * corrispondente all'attributo di chiave <em>key</em>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo in dot-notation
	 * @return <code>Vector</code> il vettore di <code>SourceBeanAttribute</code> contenuti
	 * @see SourceBean#getContainedAttributes()
	 * @see SourceBean#setContainedAttributes(Vector)
	 * @see SourceBean#setContainedAttributes(String, Vector)
	 * @see SourceBean#delContainedAttributes()
	 * @see SourceBean#delContainedAttributes(String)
	 */
	public/* synchronized */Vector getContainedAttributes(String key) {
		// TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.DEBUG,//
		// "SourceBean::getContainedAttributes: key [" + key + "]");
		Object attribute = getAttribute(key);
		if ((attribute == null) || (!(attribute instanceof SourceBean)))
			return new Vector();
		return ((SourceBean) attribute).getContainedAttributes();
	} // public /*synchronized*/ Vector getContainedAttributes(String key)

	/**
	 * Sostituisce tutti gli oggetti di tipo <code>SourceBeanAttribute</code> contenuti con quelli del vettore
	 * <em>attributes</em>.
	 * <p>
	 * 
	 * @param attributes
	 *            <code>Vector</code> il vettore di <code>SourceBeanAttribute</code> dei nuovi attributi
	 * @see SourceBean#getContainedAttributes()
	 * @see SourceBean#getContainedAttributes(String)
	 * @see SourceBean#setContainedAttributes(String, Vector)
	 * @see SourceBean#delContainedAttributes()
	 * @see SourceBean#delContainedAttributes(String)
	 */
	public/* synchronized */void setContainedAttributes(Vector attributes) {
		delContainedAttributes();
		if (attributes == null)
			return;
		_attributes = (Vector) attributes.clone();
	} // public /*synchronized*/ void setContainedAttributes(Vector
		// attributes)

	/**
	 * Sostituisce tutti gli oggetti di tipo <code>SourceBeanAttribute</code> contenuti nel <code>SourceBean</code>
	 * corrispondente all'attributo di chiave <em>key</em> con quelli del vettore <em>attributes</em>.
	 * <p>
	 * 
	 * @param attributes
	 *            <code>Vector</code> il vettore di <code>SourceBeanAttribute</code> dei nuovi attributi
	 * @see SourceBean#getContainedAttributes()
	 * @see SourceBean#getContainedAttributes(String)
	 * @see SourceBean#setContainedAttributes(Vector)
	 * @see SourceBean#delContainedAttributes()
	 * @see SourceBean#delContainedAttributes(String)
	 */
	public/* synchronized */void setContainedAttributes(String key, Vector attributes) throws SourceBeanException {
		Object attribute = getAttribute(key);
		if ((attribute == null) || (!(attribute instanceof SourceBean))) {
			_logger.warn("SourceBean::setContainedAttributes: chiave errata");

			throw new SourceBeanException("Chiave errata");
		} // if ((attribute == null) || (!(attribute instanceof SourceBean)))
		((SourceBean) attribute).setContainedAttributes(attributes);
	} // public /*synchronized*/ Vector getContainedAttributes()

	public/* synchronized */void updContainedAttributes(Vector attributes) throws SourceBeanException {
		if (attributes == null)
			return;
		for (int i = 0; i < attributes.size(); i++) {
			SourceBeanAttribute attribute = (SourceBeanAttribute) attributes.get(i);
			String attributeKey = attribute.getKey();
			Object attributeValue = attribute.getValue();
			if (attributeValue instanceof SourceBean)
				updAttribute((SourceBean) attributeValue);
			else
				updAttribute(attributeKey, attributeValue);
		} // for (int i = 0; i < attributes.size(); i++)
	} // public /*synchronized*/ void updContainedAttributes(Vector
		// attributes) throws SourceBeanException

	public/* synchronized */void updContainedAttributes(String key, Vector attributes) throws SourceBeanException {
		Object attribute = getAttribute(key);
		if ((attribute == null) || (!(attribute instanceof SourceBean))) {
			_logger.warn("SourceBean::updContainedAttributes: chiave errata");

			throw new SourceBeanException("Chiave errata");
		} // if ((attribute == null) || (!(attribute instanceof SourceBean)))
		((SourceBean) attribute).updContainedAttributes(attributes);
	} // public /*synchronized*/ Vector getContainedAttributes()

	/**
	 * Elimina tutti gli attributi contenuti nel <code>SourceBean</code>.
	 * <p>
	 * 
	 * @see SourceBean#getContainedAttributes()
	 * @see SourceBean#getContainedAttributes(String)
	 * @see SourceBean#setContainedAttributes(Vector)
	 * @see SourceBean#setContainedAttributes(String, Vector)
	 * @see SourceBean#delContainedAttributes(String)
	 */
	public/* synchronized */void delContainedAttributes() {
		_attributes.removeAllElements();
	} // public /*synchronized*/ void delContainedAttributes()

	/**
	 * Elimina tutti gli attributi contenuti nel <code>SourceBean</code> corrispondente all'attributo di chiave
	 * <em>key</em>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo in dot-notation
	 * @see SourceBean#getContainedAttributes()
	 * @see SourceBean#getContainedAttributes(String)
	 * @see SourceBean#setContainedAttributes(Vector)
	 * @see SourceBean#setContainedAttributes(String, Vector)
	 * @see SourceBean#delContainedAttributes()
	 */
	public/* synchronized */void delContainedAttributes(String key) {
		// TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.DEBUG,//
		// "SourceBean::delContainedAttributes: key [" + key + "]");
		Object attribute = getAttribute(key);
		if ((attribute == null) || (!(attribute instanceof SourceBean)))
			return;
		((SourceBean) attribute).delContainedAttributes();
	} // public /*synchronized*/ void delContainedAttributes(String key)

	/**
	 * Ritorna gli oggetti di tipo <code>SourceBeanAttribute</code> contenuti il cui valore associato &egrave; di tipo
	 * <code>SourceBean</code>.
	 * <p>
	 * 
	 * @return <code>Vector</code> il vettore di <code>SourceBeanAttribute</code>
	 * @see SourceBean#getContainedSourceBeanAttributes(String)
	 * @see SourceBean#getContainedAttributes()
	 * @see SourceBean#getContainedAttributes(String)
	 */
	public/* synchronized */Vector getContainedSourceBeanAttributes() {
		Vector attributes = new Vector();
		for (int i = 0; i < _attributes.size(); i++) {
			SourceBeanAttribute attribute = (SourceBeanAttribute) (_attributes.elementAt(i));
			if (attribute.getValue() instanceof SourceBean)
				attributes.addElement(attribute);
		} // for (int i = 0; i < _attributes.size(); i++)
		return attributes;
	} // public /*synchronized*/ void Object
		// getContainedSourceBeanAttributes()

	/**
	 * Ritorna gli oggetti di tipo <code>SourceBeanAttribute</code> contenuti nel <code>SourceBean</code> corrispondente
	 * all'attributo di chiave <em>key</em> ed il cui valore associato &egrave; di tipo <code>SourceBean</code>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo in dot-notation
	 * @return <code>Vector</code> il vettore di <code>SourceBeanAttribute</code>
	 * @see SourceBean#getContainedSourceBeanAttributes()
	 * @see SourceBean#getContainedAttributes()
	 * @see SourceBean#getContainedAttributes(String)
	 */
	public/* synchronized */Vector getContainedSourceBeanAttributes(String key) {
		// TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.DEBUG,//
		// "SourceBean::getContainedSourceBeanAttributes:// key [" + key + "]");
		Object attribute = getAttribute(key);
		if ((attribute == null) || (!(attribute instanceof SourceBean)))
			return new Vector();
		return ((SourceBean) attribute).getContainedSourceBeanAttributes();
	} // public /*synchronized*/ Vector
		// getContainedSourceBeanAttributes(String key)

	/**
	 * Ritorna gli oggetti di tipo <code>SourceBeanAttribute</code> contenuti il cui valore associato &egrave; di tipo
	 * <code>XMLObject</code>.
	 * <p>
	 * 
	 * @return <code>Vector</code> il vettore di <code>SourceBeanAttribute</code>
	 * @see SourceBean#getContainedNotXMLObjectAttributes()
	 * @see SourceBean#getContainedAttributes()
	 * @see SourceBean#getContainedAttributes(String)
	 */
	private/* synchronized */Vector getContainedXMLObjectAttributes() {
		Vector attributes = new Vector();
		for (int i = 0; i < _attributes.size(); i++) {
			SourceBeanAttribute attribute = (SourceBeanAttribute) (_attributes.elementAt(i));
			if (attribute.getValue() instanceof XMLObject)
				attributes.addElement(attribute);
		} // for (int i = 0; i < _attributes.size(); i++)
		return attributes;
	} // private /*synchronized*/ Vector getContainedXMLObjectAttributes()

	/**
	 * Ritorna gli oggetti di tipo <code>SourceBeanAttribute</code> contenuti il cui valore associato non &egrave; di
	 * tipo <code>XMLObject</code>.
	 * <p>
	 * 
	 * @return <code>Vector</code> il vettore di <code>SourceBeanAttribute</code>
	 * @see SourceBean#getContainedXMLObjectAttributes()
	 * @see SourceBean#getContainedAttributes()
	 * @see SourceBean#getContainedAttributes(String)
	 */
	private/* synchronized */Vector getContainedNotXMLObjectAttributes() {
		Vector attributes = new Vector();
		for (int i = 0; i < _attributes.size(); i++) {
			SourceBeanAttribute attribute = (SourceBeanAttribute) (_attributes.elementAt(i));
			if (!(attribute.getValue() instanceof XMLObject))
				attributes.addElement(attribute);
		} // for (int i = 0; i < _attributes.size(); i++)
		return attributes;
	} // private /*synchronized*/ Vector getContainedNotXMLObjectAttributes()

	/**
	 * Ritorna il vettore di chiavi in dot-notation degli attributi a cui &egrave; associata la chiave <em>key</em>.
	 * <p>
	 * 
	 * @param key
	 *            chiave dell'attributo <em>non</em> in dot-notation
	 * @return <code>Vector</code> il vettore di chiavi in dot-notation
	 */
	public/* synchronized */Vector getFullKeyPaths(String key) {
		Vector fullKeyPaths = new Vector();
		getFullKeyPaths(key, "", fullKeyPaths);
		return fullKeyPaths;
	} // public /*synchronized*/ Vector getFullKeyPaths(String key)

	private/* synchronized */void getFullKeyPaths(String key, String path, Vector fullKeyPaths) {
		for (int i = 0; i < _attributes.size(); i++) {
			SourceBeanAttribute attribute = (SourceBeanAttribute) (_attributes.elementAt(i));
			if (attribute.getKey().equalsIgnoreCase(key)) {
				fullKeyPaths.addElement(path + "." + key);
				break;
			} // if (attribute.getKey().equalsIgnoreCase(key))
		} // for (int i = 0; i < _attributes.size(); i++)
		for (int i = 0; i < _attributes.size(); i++) {
			SourceBeanAttribute attribute = (SourceBeanAttribute) (_attributes.elementAt(i));
			if (attribute.getValue() instanceof SourceBean) {
				SourceBean sourceBean = (SourceBean) (attribute.getValue());
				String newPath = null;
				if (path == null)
					newPath = sourceBean.getName();
				else
					newPath = path + "." + sourceBean.getName();
				sourceBean.getFullKeyPaths(key, newPath, fullKeyPaths);
			} // if (attribute.getValue() instanceof SourceBean)
		} // for (int i = 0; i < _attributes.size(); i++)
	} // private /*synchronized*/ void getFullKeyPaths(String key, String

	// path, Vector fullKeyPaths)

	/**
	 * Ritorna il <code>SourceBean</code> ottenuto dal parsing dell'<code>InputSource</code> <em>stream</em>.
	 * <p>
	 * 
	 * @param stream
	 *            rappresentazione XML del <code>SourceBean</code>
	 * @return il <code>SourceBean</code> corrispondente allo stream XML
	 * @see SourceBean#fromXMLString(String)
	 * @see SourceBean#fromXMLFile(String)
	 */
	public static SourceBean fromXMLStream(InputSource stream) throws SourceBeanException {
		// TracerSingleton.log(Constants.NOME_MODULO,// TracerSingleton.DEBUG,
		// "SourceBean::fromXMLStream: invocato");
		if (stream == null) {
			_logger.warn("SourceBean::fromXMLStream: stream nullo");

			return null;
		} // if (stream == null)
		SourceBean sourceBean = null;


		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
	        SAXParser sp = spf.newSAXParser();
	        XMLReader parser = sp.getXMLReader();
			
			SourceBeanContentHandler contentHandler = new SourceBeanContentHandler();
			parser.setContentHandler(contentHandler);
			parser.parse(stream);
			sourceBean = contentHandler.getSourceBean();
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					"SourceBean::fromXMLString: impossibile elaborare lo stream XML", ex);

			throw new SourceBeanException("Impossibile elaborare lo stream XML");
		} // catch (Exception ex) try
			// if (sourceBean == null)
			// TracerSingleton.log(Constants.NOME_MODULO,// TracerSingleton.DEBUG,
			// "SourceBean::fromXMLString: output nullo");
		return sourceBean;
	} // public static SourceBean fromXMLStream(InputSource stream) throws
		// SourceBeanException

	/**
	 * Ritorna il <code>SourceBean</code> ottenuto dal parsing della stringa <em>xmlSourceBean</em>.
	 * <p>
	 * 
	 * @param xmlSourceBean
	 *            rappresentazione XML del <code>SourceBean</code>
	 * @return il <code>SourceBean</code> corrispondente allo stream XML
	 * @see SourceBean#fromXMLStream(InputSource)
	 * @see SourceBean#fromXMLFile(String)
	 */
	public static SourceBean fromXMLString(String xmlSourceBean) throws SourceBeanException {
		// TracerSingleton.log(Constants.NOME_MODULO,// TracerSingleton.DEBUG,
		// "SourceBean::fromXMLString: invocato");
		if (xmlSourceBean == null) {
			_logger.fatal("SourceBean::fromXMLString: xmlSourceBean non valido");

			throw new SourceBeanException("xmlSourceBean non valido");
		} // if (xmlSourceBean == null)

		// 03/08/2006 SAVINO: MODIFICA NECESSARIA QUANDO SI VUOLE USARE
		// SOURCEBEAN FUORI IL CONTESTO WEB
		String uri = null;
		try {
			uri = (String) ConfigSingleton.getInstance().getAttribute("COMMON.FILE_URI_PREFIX");
		} catch (Exception ee) {
			//
		}

		if (uri == null)
			uri = "";
		if (!xmlSourceBean.startsWith("<")) {
			xmlSourceBean = XMLUtil.XML_HEADER_PREFIX + " version=\"" + XMLUtil.XML_HEADER_DEFAULT_VERSION + "\" "
					+ "encoding=\"" + XMLUtil.XML_HEADER_DEFAULT_ENCODING + "\"?>\n" + "<!DOCTYPE SOURCEBEAN SYSTEM \""
					+ uri + XMLUtil.getDoctypeFilename() + "\">\n" + "<XMLSOURCEBEAN>\n" + xmlSourceBean
					+ "\n</XMLSOURCEBEAN>";
			SourceBean xmlRequestBean = fromXMLStream(new InputSource(new StringReader(xmlSourceBean)));
			xmlSourceBean = xmlRequestBean.getCharacters();
		} // if (!xmlSourceBean.startsWith("<"))
		if (!xmlSourceBean.startsWith(XMLUtil.XML_HEADER_PREFIX))
			xmlSourceBean = XMLUtil.XML_HEADER_PREFIX + " version=\"" + XMLUtil.XML_HEADER_DEFAULT_VERSION + "\" "
					+ "encoding=\"" + XMLUtil.XML_HEADER_DEFAULT_ENCODING + "\"?>\n" + "<!DOCTYPE SOURCEBEAN SYSTEM \""
					+ uri + XMLUtil.getDoctypeFilename() + "\">\n" + xmlSourceBean;
		// TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.DEBUG,//
		// "SourceBean::fromXMLString:\n" + xmlSourceBean);
		return fromXMLStream(new InputSource(new StringReader(xmlSourceBean)));
	} // public static SourceBean fromXMLString(String xmlSourceBean) throws
		// SourceBeanException

	/**
	 * Ritorna il <code>SourceBean</code> ottenuto dal parsing del file <em>xmlSourceBean</em>.
	 * <p>
	 * 
	 * @param xmlSourceBean
	 *            nome del file che contiene la rappresentazione XML del <code>SourceBean</code>
	 * @return il <code>SourceBean</code> corrispondente allo stream XML
	 * @see SourceBean#fromXMLStream(InputSource)
	 * @see SourceBean#fromXMLString(String)
	 */
	public static SourceBean fromXMLFile(String xmlSourceBean) throws SourceBeanException {
		// TracerSingleton.log(Constants.NOME_MODULO,// TracerSingleton.DEBUG,
		// "SourceBean::fromXMLFile: invocato");
		if (xmlSourceBean == null) {
			_logger.fatal("SourceBean::fromXMLFile: xmlSourceBean non valido");

			throw new SourceBeanException("XMLSourceBean non valido");
		} // if (xmlSourceBean == null)
		String rootPath = ConfigSingleton.getRootPath();
		if (rootPath == null) {
			_logger.fatal("SourceBean::fromXMLFile: root path non valido");

			throw new SourceBeanException("Root path non valido");
		} // if (rootPath == null)
		xmlSourceBean = rootPath + xmlSourceBean;
		// TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.DEBUG,//
		// "SourceBean::fromXMLFile: xmlSourceBean [" +// xmlSourceBean + "]");

		if ((xmlSourceBean != null) && xmlSourceBean.toLowerCase().indexOf("[$hostname_context]") > 0) {

			String hostname_context = ConfigSingleton.getHostname() + "_" + ConfigSingleton.getContextName();
			// System.out.println("[$hostname_context]=" + hostname_context);
			xmlSourceBean = replace(xmlSourceBean, "[$hostname_context]", hostname_context);

		}

		// FV 29/08/2006
		// Aggiunta la possibilità di avere un file XML spezzato in vari file
		// presenti nelle sottocartelle

		File f = null;

		if ((xmlSourceBean != null) && xmlSourceBean.endsWith(".FOLDER")) {
			// si tratta di una cartella.
			// il file XML va generato dai file (pezzetti di xml) presenti nelle
			// sottocartelle

			String forderPath = xmlSourceBean.substring(0, xmlSourceBean.length() - 7);
			String rootTag = forderPath.substring(forderPath.lastIndexOf("/") + 1);
			f = ricostruisciXML(forderPath, rootTag);
			xmlSourceBean = f.getAbsolutePath();
			System.out.println(rootTag + ".xml ricostruito in " + xmlSourceBean);

		}

		System.out.println("Caricamento di " + xmlSourceBean + " in  corso...");

		SourceBean sourceBean = null;
		try {
			InputSource stream = new InputSource(new FileReader(xmlSourceBean));
			sourceBean = fromXMLStream(stream);

			if (f != null) {
				if (f.delete()) {
					System.out.println("Cancellato " + xmlSourceBean);

				}
				
			}
		} 
		catch (FileNotFoundException ex) {
			_logger.fatal("SourceBean::fromXMLFile: file non trovato");

			throw new SourceBeanException("File non trovato");
		} // catch (FileNotFoundException ex)
		return sourceBean;
	} // public static SourceBean fromXMLFile(String xmlSourceBean) throws
		// SourceBeanException

	/**
	 * Ritorna un oggetto di tipo Element che verrà utilizzato nella rappresentazione in XML dell'oggetto.
	 * 
	 * @return <code>Document<code> un oggetto di tipo Document.
	 */
	public/* synchronized */Element toElement(Document document) {
		Element element = document.createElement(_sourceBeanName.toUpperCase());
		Vector notXMLObjectAttributes = getContainedNotXMLObjectAttributes();
		for (int i = 0; i < notXMLObjectAttributes.size(); i++) {
			SourceBeanAttribute sourceBeanAttribute = (SourceBeanAttribute) (notXMLObjectAttributes.elementAt(i));
			String attributeValue = null;
			try {
				attributeValue = sourceBeanAttribute.getValue().toString();
			} // try
			catch (Exception ex) {
				attributeValue = "NOT_AVAILABLE";
			} // catch (Exception ex)
			element.setAttribute(sourceBeanAttribute.getKey(), attributeValue);
		} // for (int i = 0; i < notXMLObjectAttributes.size(); i++)
		if (_characters != null)
			element.appendChild(document.createTextNode(_characters));
		// element.appendChild(document.createCDATASection(_characters));
		else {
			Vector xmlObjectAttributes = getContainedXMLObjectAttributes();
			for (int i = 0; i < xmlObjectAttributes.size(); i++) {
				SourceBeanAttribute sourceBeanAttribute = (SourceBeanAttribute) (xmlObjectAttributes.elementAt(i));
				XMLObject xmlObject = (XMLObject) (sourceBeanAttribute.getValue());
				if (xmlObject instanceof SourceBean)
					element.appendChild(xmlObject.toElement(document));
				else {
					Element keyElement = document.createElement(sourceBeanAttribute.getKey().toUpperCase());
					keyElement.appendChild(xmlObject.toElement(document));
					element.appendChild(keyElement);
				} // if (xmlObject instanceof SourceBean)
			} // for (int i = 0; i < xmlObjectAttributes.size(); i++)
		} // if (_characters != null) else
		return element;
	} // public /*synchronized*/ Element toElement(Document document)

	public String toString() {
		return toXML(false);
	} // public String toString()

	private static File ricostruisciXML(String directory, String rootTag) {

		String nomeFileOut = rootTag.toLowerCase();

		try {

			File fileOut = File.createTempFile(rootTag, ".xml");
			String outputDir = fileOut.getAbsolutePath();
			FileOutputStream out = new FileOutputStream(outputDir);

			StringBuffer testata = new StringBuffer();
			testata.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
			testata.append("<!-- Generato il      : " + new Date() + "-->\r\n");
			testata.append("<!-- Directory di conf: " + directory + "-->\r\n");
			testata.append("<!-- Output in        : " + outputDir + "-->\r\n");

			testata.append("<" + rootTag + ">\r\n");

			out.write(testata.toString().getBytes());
			File dir = new File(directory);

			scriviDir(dir, out);

			out.write(new String("</" + rootTag + ">").getBytes());
			out.close();

			System.out.println(nomeFileOut + ".xml generato in " + outputDir);
			return fileOut;

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return null;

	}

	private static void scriviDir(File dir, FileOutputStream out) throws IOException {
		byte[] buffer = new byte[1024];

		File[] fileXML = null;

		if (dir.isDirectory()) {
			fileXML = dir.listFiles();

			for (int i = 0; i < fileXML.length; i++) {
				File f = fileXML[i];

				if (f.isFile()) {
					String nomeFile = f.getName();
					boolean isXmlFile = nomeFile.toLowerCase().endsWith(".xml");

					if (isXmlFile) {
						BufferedInputStream bir = new BufferedInputStream(new FileInputStream(f.getAbsolutePath()));

						StringBuffer testata = new StringBuffer();
						testata.append("<!-- File di origine: " + f.getAbsolutePath() + "-->\r\n");

						out.write(testata.toString().getBytes());

						// inizia la scrittura su out...
						int len = bir.read(buffer, 0, 1024);

						while (len != -1) {
							out.write(buffer, 0, len);
							len = bir.read(buffer, 0, 1024);
						}

						bir.close();
					}
				} else if (f.isDirectory()) {
					scriviDir(f, out);

				}
			}
		}

	}

	// 30/08/2006 FV

	/**
	 * Efficient string replace function. Replaces instances of the substring find with replace in the string subject.
	 * karl@xk72.com
	 * 
	 * @param subject
	 *            The string to search for and replace in.
	 * @param find
	 *            The substring to search for.
	 * @param replace
	 *            The string to replace instances of the string find with.
	 */
	public static String replace(String subject, String find, String replace) {
		StringBuffer buf = new StringBuffer();
		int l = find.length();
		int s = 0;
		int i = subject.indexOf(find);
		while (i != -1) {
			buf.append(subject.substring(s, i));
			buf.append(replace);
			s = i + l;
			i = subject.indexOf(find, s);
		}
		buf.append(subject.substring(s));
		return buf.toString();
	}

} // public class SourceBean extends AbstractXMLObject implements
	// CloneableObject, Serializable
