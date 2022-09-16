package com.engiweb.framework.base;

import java.io.Serializable;

import org.w3c.dom.Document;

import com.engiweb.framework.util.XMLUtil;

/**
 * Tutti gli oggetti che estendono la classe <code>AbstractXMLObject</code> sono conosciuti dal framework come oggetti
 * aventi una rappresentazione in XML . Se un oggetto estende questa classe deve implementare il metodo
 * toElement(Document document) inserendovi la logica di generazione del XML. Viene utilizzato un oggetto Document per
 * effettuare la generazione del XML.
 * 
 * @author Luigi Bellio
 */
public abstract class AbstractXMLObject implements XMLObject, Serializable {
	public AbstractXMLObject() {
		super();
	} // public AbstractXMLObject()

	/**
	 * Ritorna la rappresentazione XML dell'oggetto in formato stringa.
	 * 
	 * @return <code>String<code> la rappresentazione XML dell'oggetto.
	 */
	public String toXML() {
		return XMLUtil.toXML(this, true);
	} // public String toXML()

	/**
	 * Ritorna la rappresentazione XML dell'oggetto in formato stringa.
	 * 
	 * @param inlineEntity
	 *            boolean indica se la stringa XML generata deve contenere la sezione del doc type Entity. * Ritorna la
	 *            rappresentazione in XML dell'errore.Se il parametro è true allora nella stringa di ritorno
	 * @return <code>String<code> la rappresentazione XML dell'oggetto.
	 */
	public String toXML(boolean inlineEntity) {
		return XMLUtil.toXML(this, inlineEntity);
	} // public String toXML(boolean inlineEntity)

	/**
	 * Ritorna la rappresentazione XML dell'oggetto in formato stringa.
	 * 
	 * @param inlineEntity
	 *            boolean indica se la stringa XML generata deve contenere la sezione del doc type Entity.
	 * @param indent
	 *            boolean indica se la stringa XML generata deve essere identata.
	 * @return <code>String<code> la rappresentazione XML dell'oggetto.
	 */
	public String toXML(boolean inlineEntity, boolean indent) {
		return XMLUtil.toXML(this, inlineEntity, indent);
	} // public String toXML(boolean inlineEntity)

	/**
	 * Ritorna la rappresentazione XML dell'oggetto in formato stringa.
	 * 
	 * @param inlineEntity
	 *            boolean indica se la stringa XML generata deve contenere la sezione del doc type Entity.
	 * @param indent
	 *            boolean indica se la stringa XML generata deve essere identata.
	 * @param omitDeclaration
	 *            boolean indica se deve essere generato il tag di dichiarazione del documento XML (Ad es: &lt;?xml
	 *            version="1.0" encoding="UTF-8"?&gt;)
	 * @return <code>String<code> la rappresentazione XML dell'oggetto.
	 * @author roccetti
	 */
	public String toXML(boolean inlineEntity, boolean indent, boolean omitDeclaration) {
		return XMLUtil.toXML(this, inlineEntity, indent, omitDeclaration);
	}

	/**
	 * Ritorna la rappresentazione XML dell'oggetto in formato stringa.
	 * 
	 * @return <code>String<code> la rappresentazione XML dell'oggetto.
	 */
	public String toXML(int level) {
		return toXML();
	} // public String toXML(int level)

	/**
	 * Ritorna un oggetto di tipo Document .
	 * 
	 * @return <code>Document<code> un oggetto di tipo Document.
	 */
	public Document toDocument() {
		return XMLUtil.toDocument(this);
	} // public Document toDOM()
} // public abstract class AbstractXMLObject implements XMLObject,
	// Serializable
