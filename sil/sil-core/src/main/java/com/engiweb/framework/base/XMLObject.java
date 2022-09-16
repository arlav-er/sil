package com.engiweb.framework.base;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Definisce l'interfaccia che deve essere implementata da tutti gli oggetti il cui stato può essere pubblicato in XML.
 */
public interface XMLObject {
	/**
	 * Ritorna la rappresentazione XML dell'oggetto . La stringa XML generata contiene la sezione del doc type Entity.
	 */
	String toXML();

	/**
	 * Ritorna la rappresentazione XML dell'oggetto in formato stringa.
	 * 
	 * @param inlineEntity
	 *            boolean indica se la stringa XML generata deve contenere la sezione del doc type Entity.
	 * @return <code>String<code> la rappresentazione XML dell'oggetto.
	 */
	String toXML(boolean inlineEntity);

	/**
	 * Ritorna la rappresentazione XML dell'oggetto in formato stringa.
	 * 
	 * @return <code>String<code> la rappresentazione XML dell'oggetto.
	 */
	String toXML(int level);

	/**
	 * Ritorna un oggetto di tipo Document .
	 * 
	 * @return <code>Document<code> un oggetto di tipo Document.
	 */
	Document toDocument();

	/**
	 * Ritorna un oggetto di tipo Element che verrà utilizzato nella rappresentazione in XML dell'oggetto.
	 * 
	 * @return <code>Document<code> un oggetto di tipo Document.
	 */
	Element toElement(Document document);
} // public interface XMLObject
