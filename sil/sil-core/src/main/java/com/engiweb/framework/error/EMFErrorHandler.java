package com.engiweb.framework.error;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.engiweb.framework.base.AbstractXMLObject;
import com.engiweb.framework.base.CloneableObject;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.base.XMLObject;

/**
 * La classe <code>EMFErrorHandler</code> è il gestore degli errori nell'ambito dell' application framework.
 * <code>EMFErrorHandler</code> contiene gli errori (ovvero le istanze di classe che estendono
 * <code>EMFAbstractError</code>) e mette a disposizioni dei servizi per la loro gestione.
 * 
 * @author Luigi Bellio
 * @see EMFAbstractError
 */
public class EMFErrorHandler extends AbstractXMLObject implements CloneableObject, Serializable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(EMFErrorHandler.class.getName());
	private ArrayList _errorStack = null;
	public static final String ERRORS_ELEMENT = "ERRORS";

	/**
	 * Costruisce un <code>EMFErrorHandler</code> inizializzando lo stack degli errori .
	 */
	public EMFErrorHandler() {
		_errorStack = new ArrayList();
	} // public EMFErrorHandler()

	/**
	 * Costruisce un <code>EMFErrorHandler</code> inizializzando lo stack con gli errori presenti nell'istanza di
	 * <code>EMFErrorHandler</code> ricevuta come parametro.
	 * 
	 * @param errorHandler
	 *            istanza di <code>EMFErrorHandler</code>.
	 */
	public EMFErrorHandler(EMFErrorHandler errorHandler) {
		this();
		_errorStack = new ArrayList(errorHandler.getErrors());
	} // public EMFErrorHandler(EMFErrorHandler errorHandler)

	/**
	 * Ritorna un <code>CloneableObject</code> copia dell'oggetto stesso.
	 * 
	 * @return una copia del <code>EMFErrorHandler</code> stesso.
	 */
	public CloneableObject cloneObject() {
		return new EMFErrorHandler(this);
	} // public CloneableObject cloneObject()

	/**
	 * Ripulisce lo stack degli errori .
	 */
	public void clear() {
		_errorStack = new ArrayList();
	} // public void clear()

	/**
	 * Questo metodo elimina dallo stack degli errori tutti quegli oggetti che hanno il <em>severity</em> uguale a
	 * quello passato come parametro.
	 * 
	 * @param severity
	 *            identifica gli oggetti da eliminare.
	 */
	public void clearBySeverity(String severity) {
		if (!EMFErrorSeverity.isSeverityValid(severity))
			return;
		ArrayList newErrorStack = new ArrayList();
		for (int i = 0; i < _errorStack.size(); i++) {
			EMFAbstractError errorItem = (EMFAbstractError) _errorStack.get(i);
			if ((errorItem.getSeverity() != null) && !errorItem.getSeverity().equals(severity))
				newErrorStack.add(errorItem);
		} // for (int i = 0; i < this.errorStack.size(); i++)
		_errorStack = newErrorStack;
	} // public void clearBySeverity(String severity)

	/**
	 * Questo metodo permette di aggiungere un errore nello stack del gestore.
	 * 
	 * @param errorItem
	 *            oggetto errore da aggiungere nello stack.
	 */
	public void addError(EMFAbstractError errorItem) {
		if ((errorItem != null) && (errorItem.getSeverity() != null)
				&& EMFErrorSeverity.isSeverityValid(errorItem.getSeverity()) && (errorItem.getDescription() != null)
				&& (errorItem.getDescription().length() > 0)) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "EMFErrorHandler::addError: errore aggiunto",
					(XMLObject) errorItem);

			_errorStack.add(errorItem);
		} // if ((errorItem != null) && ...
		else
			it.eng.sil.util.TraceWrapper.debug(_logger, "EMFErrorHandler::addError: errore non aggiunto",
					(XMLObject) errorItem);

	} // public void addError(EMFAbstractError errorItem)

	/**
	 * Ritorna un <code>String</code> risultato della concatenazione di tutte le descrizioni degli errori presenti nello
	 * stack.
	 * 
	 * @return concatenazione di tutte le descrizioni degli errori.
	 */
	public String getStackTrace() {
		String returnStackTrace = new String();
		for (int i = 0; i < _errorStack.size(); i++) {
			EMFAbstractError errorItem = (EMFAbstractError) _errorStack.get(i);
			if ((errorItem.getDescription() != null) && (errorItem.getDescription().length() > 0)) {
				returnStackTrace = returnStackTrace.concat("\n");
				returnStackTrace = returnStackTrace.concat(errorItem.getDescription());
			} // if ((errorItem.getDescription() != null) &&
				// (errorItem.getDescription().length() > 0))
		} // for (int i = 0; i < this.errorStack.size(); i++)
		return returnStackTrace;
	} // public String getStackTrace()

	/**
	 * Ritorna un <code>String</code> risultato della concatenazione di tutte le descrizioni degli errori presenti nello
	 * stack aventi un severity uguale al parametro in input.
	 * 
	 * @return concatenazione di tutte le descrizioni degli errori.
	 */
	public String getStackTraceBySeverity(String severity) {
		if (!EMFErrorSeverity.isSeverityValid(severity))
			return null;
		String returnStackTrace = new String();
		for (int i = 0; i < _errorStack.size(); i++) {
			EMFAbstractError errorItem = (EMFAbstractError) _errorStack.get(i);
			if ((errorItem.getSeverity() != null) && errorItem.getSeverity().equals(severity)
					&& (errorItem.getDescription() != null) && (errorItem.getDescription().length() > 0)) {
				returnStackTrace = returnStackTrace.concat("\n");
				returnStackTrace = returnStackTrace.concat(errorItem.getDescription());
			} // if ((errorItem.getSeverity() != null) &&
				// errorItem.getSeverity().equals(severity) && ...
		} // for (int i = 0; i < this.errorStack.size(); i++)
		return returnStackTrace;
	} // public String getStackTraceBySeverity(String severity)

	/**
	 * Ritorna un <code>boolean</code> avente il seguente significato: <em>true</em> lo stack degli errori non contiene
	 * elementi. <em>false</em> lo stack degli errori contiene elementi.
	 * 
	 * @return <code>boolean</code> indica la presenza di errori nello stack.
	 */
	public boolean isOK() {
		if (_errorStack.size() == 0)
			return true;
		return false;
	} // public boolean isOK()

	/**
	 * Ritorna un <code>boolean</code> avente il seguente significato: <em>true</em> lo stack degli errori non contiene
	 * elementi aventi severity uguale a quella specificata in input. <em>false</em> lo stack degli errori contiene
	 * elementi aventi severity uguale a quella specificata in input.
	 * 
	 * @return <code>boolean</code> indica la presenza di errori nello stack.
	 */
	public boolean isOKBySeverity(String severity) {
		if (!EMFErrorSeverity.isSeverityValid(severity))
			return false;
		for (int i = 0; i < _errorStack.size(); i++) {
			EMFAbstractError errorItem = (EMFAbstractError) _errorStack.get(i);
			if ((errorItem.getSeverity() != null) && errorItem.getSeverity().equals(severity))
				return false;
		} // for (int i = 0; i < this.errorStack.size(); i++)
		return true;
	} // public boolean isOKBySeverity(String severity)

	/**
	 * Ritorna un <code>Collection</code> di <code>EMFAbstractError</code> presenti nello stack degli errori.
	 * 
	 * @return <code>Collection</code> di <code>EMFAbstractError</code>.
	 */
	public Collection getErrors() {
		ArrayList newErrorStack = new ArrayList();
		for (int i = 0; i < _errorStack.size(); i++) {
			EMFAbstractError abstractError = (EMFAbstractError) _errorStack.get(i);
			if (abstractError != null)
				newErrorStack.add(abstractError.cloneObject());
		} // for (int i = 0; i < errorHandler._errorStack.size(); i++)
		return newErrorStack;
	} // public Collection getErrors()

	/**
	 * Ritorna un <code>SourceBean</code> popolato di <code>EMFAbstractError</code> presenti nello stack degli errori.Ad
	 * ogni elemento dello stack si recupera il suo <code>SourceBean</code>.
	 * 
	 * @return <code>SourceBean</code> di <code>EMFAbstractError</code>.
	 */
	public SourceBean getSourceBean() {
		SourceBean errorsBean = null;
		try {
			errorsBean = new SourceBean(ERRORS_ELEMENT);
			for (int i = 0; i < _errorStack.size(); i++) {
				SourceBean errorBean = ((EMFAbstractError) _errorStack.get(i)).getSourceBean();
				if (errorBean != null)
					errorsBean.setAttribute(errorBean);
			} // for (int i = 0; i < _errorStack.size(); i++)
		} // try
		catch (SourceBeanException sbe) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "EMFErrorHandler::getSourceBean:", sbe);

		} // catch (SourceBeanException sbe) try
		return errorsBean;
	} // public SourceBean getSourceBean()

	/**
	 * In quanto subclass della classe <code>AbstractXMLObject</code> è necessario implementare questo metodo * che
	 * viene invocato indirettamente dal metodo <code>toXML()</code> per rappresentare l'oggetto in XML. *
	 * 
	 * @return <code>Element</code> partendo dall'oggetto <code>Document</code> della classe stessa.
	 */
	public Element toElement(Document document) {

		SourceBean errorsBean = getSourceBean();
		if (errorsBean == null) {
			_logger.debug("EMFErrorHandler::toElement: errorsBean nullo");

			return document.createElement(ERRORS_ELEMENT);
		} // if (errorsBean == null)
		return errorsBean.toElement(document);
	} // public Element toElement(Document document)
} // public class EMFErrorHandler extends AbstractXMLObject implements
	// Serializable
