package com.engiweb.framework.base;

import java.io.Serializable;

import com.engiweb.framework.util.XMLUtil;

/**
 * La classe <code>SourceBeanAttribute</code> contiene una singola coppia chiave-valore di un attributo memorizzato in
 * un <code>SourceBean</code>. La rappresentazione XML di un <code>SourceBeanAttribute</code> a seconda del valore
 * contenuto &egrave;:
 * <ul>
 * <li><em>SourceBean</em> <blockquote>
 * 
 * <pre>
 * SourceBean value = new SourceBean(&quot;foo&quot;);
 * value.setAttribute(&quot;param1&quot;, &quot;value1&quot;);
 * SourceBeanAttribute attribute = new SourceBeanAttribute(value.getName(), value);
 * 
 * attribute.toXML() =&gt;
 * 
 * &lt;FOO param1=&quot;value1&quot;&gt;
 * &lt;/FOO&gt;
 * </pre>
 * 
 * </blockquote>
 * <li><em>XMLObject</em> <blockquote>
 * 
 * <pre>
 * XMLObject value = ...
 * SourceBeanAttribute attribute = new SourceBeanAttribute(&quot;foo&quot;, value);
 * 
 * attribute.toXML() =&gt;
 * 
 * &lt;FOO&gt;
 * <em>
 * value.toXML()
 * </em>
 * &lt;/FOO&gt;
 * </pre>
 * 
 * </blockquote>
 * <li><em>Object</em> <blockquote>
 * 
 * <pre>
 * Object value = ...
 * SourceBeanAttribute attribute = new SourceBeanAttribute(&quot;foo&quot;, value);
 * 
 * attribute.toXML() =&gt;
 * 
 * &lt;FOO foo=
 * <em>
 * value.toString()
 * </em>
 * &gt;
 * &lt;/FOO&gt;
 * </pre>
 * 
 * </blockquote>
 * </ul>
 * 
 * @version 1.0, 11/03/2002
 * @author Luigi Bellio
 * @see SourceBean
 */
public class SourceBeanAttribute implements CloneableObject, Serializable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SourceBeanAttribute.class.getName());

	/**
	 * Costruisce un <code>SourceBeanAttribute</code> con chiave <em>key</em> e valore <em>value</em>.
	 * 
	 * @param key
	 *            chiave dell'attributo
	 * @param value
	 *            valore dell'attributo
	 * @exception SourceBeanException
	 *                viene lanciata se la chiave o il valore non sono validi
	 * @see SourceBeanAttribute#SourceBeanAttribute(SourceBeanAttribute)
	 */
	public SourceBeanAttribute(String key, Object value) throws SourceBeanException {
		_key = validateKey(key);
		_value = validateValue(value);
		// TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.DEBUG,//
		// "SourceBeanAttribute::SourceBeanAttribute: key [" + key + "]");
		// TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.DEBUG,//
		// "SourceBeanAttribute::SourceBeanAttribute: value [" + value + "]");
	} // public SourceBeanAttribute(String key, Object value) throws
		// SourceBeanException

	/**
	 * Costruisce un <code>SourceBeanAttribute</code> copia di <em>attribute</em>.
	 * <p>
	 * 
	 * @param attribute
	 *            <code>SourceBeanAttribute</code> da copiare
	 * @exception SourceBeanException
	 *                viene lanciata se il parametro di input <em>attribute</em> &egrave; nullo
	 * @see SourceBeanAttribute#SourceBeanAttribute(String, Object)
	 */
	public SourceBeanAttribute(SourceBeanAttribute attribute) throws SourceBeanException {
		if (attribute == null)
			throw new SourceBeanException("attribute non valido");
		_key = attribute._key;
		if (attribute._value instanceof CloneableObject)
			_value = ((CloneableObject) (attribute._value)).cloneObject();
		else
			_value = attribute._value;
	} // public SourceBeanAttribute(SourceBeanAttribute attribute) throws
		// SourceBeanException

	/**
	 * Ritorna un <code>CloneableObject</code> copia <em>non profonda</em> dell'oggetto stesso.
	 * <p>
	 * 
	 * @return una copia <em>non profonda</em> del <code>SourceBeanAttribute</code> stesso
	 */
	public/* synchronized */CloneableObject cloneObject() {
		SourceBeanAttribute clonedObject = null;
		try {
			clonedObject = new SourceBeanAttribute(this);
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"SourceBeanAttribute::cloneObject: clonedObject = new SourceBeanAttribute(this)", ex);

		} // catch (SourceBeanException ex) try
		return clonedObject;
	} // public /*synchronized*/ CloneableObject cloneObject()

	/**
	 * Valida la stringa <em>key</em>, quale nome formalmente corretto di un elemento o parametro XML.
	 */
	protected static String validateKey(String key) throws SourceBeanException {
		if (key == null)
			throw new SourceBeanException("key NULLA non valida");
		if ((key.indexOf(' ') != -1) || (key.indexOf('/') != -1) || (key.indexOf('.') != -1))
			throw new SourceBeanException("key '" + key + "' non valida");
		return key;
	} // protected static String validateKey(String key) throws
		// SourceBeanException

	/**
	 * Verifica che l'oggetto <em>value</em> sia diverso da <em>null</em>.
	 */
	protected static Object validateValue(Object value) throws SourceBeanException {
		if (value == null)
			throw new SourceBeanException("value non valido");
		return value;
	} // protected static Object validateValue(Object value) throws
		// SourceBeanException

	/**
	 * Ritorna la chiave associata all'attributo.
	 */
	public/* synchronized */String getKey() {
		return _key;
	} // public /*synchronized*/ String getKey()

	/**
	 * Imposta la chiave associata all'attributo.
	 */
	public/* synchronized */void setKey(String key) throws SourceBeanException {
		_key = validateKey(key);
	} // public /*synchronized*/ void setKey(String key) throws
		// SourceBeanException

	/**
	 * Ritorna il valore associato all'attributo.
	 */
	public/* synchronized */Object getValue() {
		return _value;
	} // public /*synchronized*/ Object getValue()

	/**
	 * Imposta il valore associato all'attributo.
	 */
	public/* synchronized */void setValue(Object value) throws SourceBeanException {
		_value = validateValue(value);
	} // public /*synchronized*/ void setValue(Object value) throws
		// SourceBeanException

	/**
	 * Ritorna una stringa contenente la rappresentazione XML del <code>SourceBeanAttribute</code>.
	 */
	public String toXMLString() {
		return toXMLString(0);
	} // public String toXMLString()

	/**
	 * Ritorna una stringa contenente la rappresentazione XML del <code>SourceBeanAttribute</code> con un livello di
	 * indentazione pari a <em>level</em>.
	 */
	public/* synchronized */String toXMLString(int level) {
		StringBuffer xmlStream = new StringBuffer();
		if (_value instanceof SourceBean)
			xmlStream.append(((SourceBean) _value).toXML(level));
		else if (_value instanceof XMLObject) {
			xmlStream.append(XMLUtil.getIndentation(level));
			xmlStream.append("<");
			xmlStream.append(XMLUtil.normalizeAttribute(XMLUtil.parseAttribute(_key.toUpperCase())));
			xmlStream.append(">\n");
			xmlStream.append(((XMLObject) _value).toXML(level + 1));
			xmlStream.append(XMLUtil.getIndentation(level));
			xmlStream.append("</");
			xmlStream.append(XMLUtil.normalizeAttribute(XMLUtil.parseAttribute(_key.toUpperCase())));
			xmlStream.append(">\n");
		} // if (_value instanceof XMLObject)
		else {
			xmlStream.append(XMLUtil.getIndentation(level));
			xmlStream.append(XMLUtil.normalizeAttribute(XMLUtil.parseAttribute(_key)));
			xmlStream.append("=\"");
			xmlStream.append(XMLUtil.normalizeAttribute(XMLUtil.parseAttribute(_value.toString())));
			xmlStream.append("\"");
		} // if (_value instanceof XMLObject) else
		return xmlStream.toString();
	} // public /*synchronized*/ String toXMLString(int level)

	/**
	 * Chiave associata all'attributo.
	 */
	private String _key = null;

	/**
	 * Valore associato all'attributo.
	 */
	private Object _value = null;
} // public class SourceBeanAttribute implements CloneableObject, Serializable
