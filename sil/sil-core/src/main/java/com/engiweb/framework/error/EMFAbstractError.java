package com.engiweb.framework.error;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.engiweb.framework.base.CloneableObject;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.XMLObject;
import com.engiweb.framework.util.XMLUtil;

/**
 * La classe astratta <code>EMFAbstractError</code> dev'essere estesa da tutte quelle classi che rappresentano un errore
 * gestito da <code>EMFErrorHandler</code>.
 * 
 * @exception Exception
 *                viene lanciata quando non è possibile gestire gli attributi dell'errore.
 * @author Luigi Bellio
 * @see EMFErrorHandler
 */
public abstract class EMFAbstractError extends Exception implements XMLObject, CloneableObject, Serializable {
	public static final String ABSTRACT_ERROR_ELEMENT = "ABSTRACT_ERROR";
	public static final String ERROR_SEVERITY = "SEVERITY";
	public static final String ERROR_DESCRIPTION = "DESCRIPTION";
	public static final String ERROR_ADDITIONAL_INFO = "ADDITIONAL_INFO";
	private String _severity = null;
	private String _description = null;
	private Object _additionalInfo = null;

	/**
	 * In questo costruttore vengono definiti alcuni attributi di classe.
	 */
	protected EMFAbstractError() {
		super();
		_severity = EMFErrorSeverity.ERROR;
		_description = "NOT DEFINED";
		_additionalInfo = null;
	} // protected EMFAbstractError(String severity, String description)

	/**
	 * Costruisce un <code>EMFAbstractError</code> partendo da un'altra istanza della stessa classe.
	 * 
	 * @param abstractError
	 *            istanza della stessa classe.
	 */
	protected EMFAbstractError(EMFAbstractError abstractError) {
		this();
		if (abstractError._severity != null)
			_severity = abstractError._severity;
		if (abstractError._description != null)
			_description = abstractError._description;
		if (abstractError._additionalInfo != null)
			if (abstractError._additionalInfo instanceof CloneableObject)
				_additionalInfo = ((CloneableObject) abstractError._additionalInfo).cloneObject();
			else
				_additionalInfo = abstractError._additionalInfo;
	} // protected EMFAbstractError(EMFAbstractError abstractError)

	/**
	 * Ritorna il messaggio di errore composto dal severity e dalla descrizione dell'errore.
	 * 
	 * @return <code>String</code> composta da severity e descrizione dell'errore.
	 */
	public String getMessage() {
		String message = new String();
		message += "severity [" + _severity + "] ";
		message += "description [" + _description + "]";
		return message;
	} // public String getMessage()

	/**
	 * Ritorna il severity dell'errore.
	 * 
	 * @return <code>String</code> il severity dell'errore.
	 */
	public String getSeverity() {
		return _severity;
	} // public String getSeverity()

	/**
	 * Permette di impostare il severity dell'errore. Ad uso esclusivo della classe figlia.
	 * 
	 * @param severity
	 *            l'attributo di severity.
	 */
	protected void setSeverity(String severity) {
		if (!EMFErrorSeverity.isSeverityValid(severity))
			_severity = EMFErrorSeverity.ERROR;
		else
			_severity = severity;
	} // protected void setSeverity(String severity)

	/**
	 * Ritorna la descrizione dell'errore.
	 * 
	 * @return <code>String</code> la descrizione dell'errore.
	 */
	public String getDescription() {
		return _description;
	} // public String getDescription()

	/**
	 * Permette di impostare la descrizione dell'errore. Ad uso esclusivo della classe figlia.
	 * 
	 * @param description
	 *            l'attributo descrizione.
	 */
	protected void setDescription(String description) {
		if (description == null)
			_description = "NOT DEFINED";
		else
			_description = description;
	} // protected void setDescription(String description)

	/**
	 * Ritorna un oggetto rappresentante un 'informazione aggiuntiva dell'errore.
	 * 
	 * @return <code>Object</code> un 'informazione aggiuntiva dell'errore.
	 */
	public Object getAdditionalInfo() {
		return _additionalInfo;
	} // public Object getAdditionalInfo()

	/**
	 * Permette di aggiungere all'errore un'informazione espressa con qualsiasi oggetto.
	 * 
	 * @param additionalInfo
	 *            l'iformazione aggiuntiva.
	 */
	protected void setAdditionalInfo(Object additionalInfo) {
		_additionalInfo = additionalInfo;
	} // protected void setAdditionalInfo(Object additionalInfo)

	/**
	 * Dev'essere implementato dalla classe figlia e deve ritornare un <code>sourceBean</code> contenente tutte le
	 * informazioni dell'errore.
	 * 
	 * @return <code>SourceBean</code> il contenitore con le informazioni dell'errore.
	 */
	public abstract SourceBean getSourceBean();

	/**
	 * Ritorna la rappresentazione in XML dell'errore.
	 * 
	 * @return <code>String</code> la rappresentazione in XML dell'errore.
	 */
	public String toXML() {
		return XMLUtil.toXML(this, true);
	} // public String toXML()

	/**
	 * Ritorna la rappresentazione in XML dell'errore.Se il parametro è true allora nella stringa di ritorno sarà
	 * presente anche la sezione del doc type Entity.
	 * 
	 * @return <code>String</code> la rappresentazione in XML dell'errore.
	 */
	public String toXML(boolean inlineEntity) {
		return XMLUtil.toXML(this, inlineEntity);
	} // public String toXML(boolean inlineEntity)

	/**
	 * Ritorna la rappresentazione in XML dell'errore.
	 * 
	 * @return <code>String</code> la rappresentazione in XML dell'errore.
	 */
	public String toXML(int level) {
		return toXML();
	} // public String toXML(int level)

	/**
	 * Ritorna un oggetto di tipo <code>Document</code> partendo dalla classe stessa. L'oggetto <code>Document</code> è
	 * utilizzato per ottenere la rappresentazione XML dell'errore.
	 * 
	 * @return <code>Document</code> rappresentazione dell'errore.
	 */
	public Document toDocument() {
		return XMLUtil.toDocument(this);
	} // public Document toDOM()

	/**
	 * Ritorna un oggetto di tipo <code>Element</code> partendo dall'oggetto <code>Document</code> creato dalla classe
	 * stessa. L'oggetto <code>Element</code> è utilizzato per ottenere la rappresentazione XML dell'errore.
	 * 
	 * @return <code>Element</code> rappresentazione dell'errore.
	 */
	public Element toElement(Document document) {
		SourceBean errorBean = getSourceBean();
		if (errorBean == null)
			return document.createElement(ABSTRACT_ERROR_ELEMENT);
		return errorBean.toElement(document);
	} // public Element toElement(Document document)
} // public abstract class EMFAbstractError extends Exception implements
	// XMLObject, Serializable
