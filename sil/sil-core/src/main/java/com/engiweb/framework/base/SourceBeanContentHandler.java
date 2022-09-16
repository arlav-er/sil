package com.engiweb.framework.base;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * La classe <code>SourceBeanContentHandler</code> implementa l'interfaccia standard di un parser SAX. Questo parser
 * ricostruisce un oggetto di tipo <code>SourceBean</code> da uno stream XML.
 * 
 * @version 1.0, 11/03/2002
 * @author Luigi Bellio
 * @see SourceBeanAttribute
 * @see SourceBean
 */
public class SourceBeanContentHandler extends DefaultHandler {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(SourceBeanContentHandler.class.getName());

	/**
	 * Costruisce ed inizializza il parser.
	 */
	public SourceBeanContentHandler() {
		_sourceBeans = new Stack();
		_sourceBean = null;
		_parentBean = null;
		_characters = new String();
	} // public SourceBeanContentHandler()

	/**
	 * Gestisce l'evento di inizio stream XML.
	 */
	public void startDocument() throws SAXException {
		// TracerSingleton.log(Constants.NOME_MODULO,// TracerSingleton.DEBUG,
		// "SourceBeanContentHandler::startDocument: invocato");
	} // public void startDocument() throws SAXException

	/**
	 * Gestisce l'evento di fine stream XML.
	 */
	public void endDocument() throws SAXException {
		// TracerSingleton.log(Constants.NOME_MODULO,// TracerSingleton.DEBUG,
		// "SourceBeanContentHandler::endDocument: invocato");
	} // public void endDocument() throws SAXException

	/**
	 * Gestisce l'evento di inizio elemento XML.
	 */
	public void startElement(String namespaceURI, String localName, String rawName, Attributes attributes)
			throws SAXException {
		// TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.DEBUG,//
		// "SourceBeanContentHandler::startElement: " + localName);
		try {
			SourceBean currentBean = new SourceBean(rawName);
			for (int i = 0; i < attributes.getLength(); i++) {
				// TracerSingleton.log(Constants.NOME_MODULO,//
				// TracerSingleton.DEBUG,////
				// "SourceBeanContentHandler::startElement: " +//
				// attributes.getLocalName(i) + " [" +// attributes.getValue(i)
				// + "]");
				
				currentBean.setAttribute(attributes.getQName(i), attributes.getValue(i));
			} // for (int i = 0; i < attributes.getLength(); i++)
			if (_parentBean != null)
				_parentBean.setAttribute(currentBean);
			_sourceBeans.push(currentBean);
			_parentBean = currentBean;
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "SourceBeanContentHandler::startElement:", ex);

			throw new SAXException(ex.getMessage());
		} // catch (SourceBeanException ex) try
	} // public void startElement(String namespaceURI, String localName,

	// String rawName, Attributes attributes) throws SAXException

	/**
	 * Gestisce l'evento di fine elemento XML.
	 */
	public void endElement(String namespaceURI, String localName, String rawName) throws SAXException {
		// TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.DEBUG,//
		// "SourceBeanContentHandler::endElement: " + localName);
		if (_characters.length() > 0) {
			// TracerSingleton.log(Constants.NOME_MODULO,//
			// TracerSingleton.DEBUG,// "SourceBeanContentHandler::endElement:
			// [" +// _characters + "]");
			_parentBean.setCharacters(_characters);
			_characters = new String();
		} // if (_characters.length() > 0)
		_sourceBean = (SourceBean) _sourceBeans.pop();
		if (_sourceBeans.empty())
			_parentBean = null;
		else
			_parentBean = (SourceBean) _sourceBeans.peek();
	} // public void endElement(String namespaceURI, String localName, String
		// rawName) throws SAXException

	/**
	 * Gestisce il testo di un elemento XML.
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
		try {
			_characters += new String(ch, start, length);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"SourceBeanContentHandler::characters: _characters += new String(ch, start, length)", ex);

			throw new SAXException(ex.getMessage());
		} // catch (Exception ex) try
	} // public void characters(char[] ch, int start, int length) throws
		// SAXException

	/**
	 * Ritorna il <code>SourceBean</code> costruito a partire dallo stream XML.
	 */
	public synchronized SourceBean getSourceBean() {
		return _sourceBean;
	} // public synchronized SourceBean getSourceBean()

	/**
	 * Stack dei <code>SourceBean</code> intermedi.
	 */
	private Stack _sourceBeans = null;

	/**
	 * <code>SourceBean</code> costruito a partire dallo stream XML.
	 */
	private SourceBean _sourceBean = null;

	/**
	 * <code>SourceBean</code> padre di quello corrente.
	 */
	private SourceBean _parentBean = null;
	private static String _characters = null;
} // public class SourceBeanContentHandler extends DefaultHandler
