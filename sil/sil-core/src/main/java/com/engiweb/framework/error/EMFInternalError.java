package com.engiweb.framework.error;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import com.engiweb.framework.base.CloneableObject;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

/**
 * Un'istanza di <code>EMFInternalError</code> rappresenta un errore non codificato. Questo significa che non esiste
 * nessun repository contenente il riferimento a questo errore.
 * 
 * @version 1.0, 06/03/2002
 * @author Luigi Bellio
 * @see EMFErrorHandler
 */
public class EMFInternalError extends EMFAbstractError implements Serializable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(EMFInternalError.class.getName());
	public static final String INTERNAL_ERROR_ELEMENT = "INTERNAL_ERROR";
	public static final String ERROR_NATIVE_EXCEPTION = "NATIVE_EXCEPTION";
	private Exception _nativeException = null;

	/**
	 * Costruisce un oggetto di tipo <code>EMFInternalError</code> identificandolo tramite una severity e una
	 * descrizione dell'errore .
	 * 
	 * @param severity
	 *            severity dell'errore.
	 * @param description
	 *            descrizione dell'errore.
	 */
	public EMFInternalError(String severity, String description) {
		super();
		init(severity, description, null, null);
	} // public EMFInternalError(String severity, String description)

	/**
	 * Costruisce un oggetto di tipo <code>EMFInternalError</code> identificandolo tramite una severity e un'eccezione.
	 * 
	 * @param severity
	 *            severity dell'errore.
	 * @param ex
	 *            eccezione .
	 */
	public EMFInternalError(String severity, Exception ex) {
		super();
		init(severity, null, ex, null);
	} // public EMFInternalError(String severity, Exception ex)

	/**
	 * Costruisce un oggetto di tipo <code>EMFInternalError</code> identificandolo tramite una severity e un oggetto di
	 * qualsiasi natura.
	 * 
	 * @param severity
	 *            severity dell'errore.
	 * @param additionalInfo
	 *            oggetto di qualsiasi natura.
	 */
	public EMFInternalError(String severity, Object additionalInfo) {
		super();
		if (additionalInfo instanceof String)
			init(severity, (String) additionalInfo, null, null);
		else if (additionalInfo instanceof Exception)
			init(severity, null, (Exception) additionalInfo, null);
		else
			init(severity, null, null, additionalInfo);
	} // public EMFInternalError(String severity, Object additionalInfo)

	/**
	 * Costruisce un oggetto di tipo <code>EMFInternalError</code> identificandolo tramite una severity , una
	 * descrizione e un'eccezione.
	 * 
	 * @param severity
	 *            severity dell'errore.
	 * @param description
	 *            descrizione dell'errore.
	 * @param ex
	 *            eccezione.
	 */
	public EMFInternalError(String severity, String description, Exception ex) {
		super();
		init(severity, description, ex, null);
	} // public EMFInternalError(String severity, String description,
		// Exception ex)

	/**
	 * Costruisce un oggetto di tipo <code>EMFInternalError</code> identificandolo tramite una severity , una
	 * descrizione e un oggetto di qualsiasi natura.
	 * 
	 * @param severity
	 *            severity dell'errore.
	 * @param description
	 *            descrizione dell'errore.
	 * @param additionalInfo
	 *            oggetto di qualsiasi natura.
	 */
	public EMFInternalError(String severity, String description, Object additionalInfo) {
		super();
		if (additionalInfo instanceof Exception)
			init(severity, description, (Exception) additionalInfo, null);
		else
			init(severity, description, null, additionalInfo);
	} // public EMFInternalError(String severity, String description,

	// Object additionalInfo)

	/**
	 * Costruisce un oggetto di tipo <code>EMFInternalError</code> identificandolo tramite una severity , una
	 * descrizione, un'eccezione e un oggetto di qualsiasi natura.
	 * 
	 * @param severity
	 *            severity dell'errore.
	 * @param description
	 *            descrizione dell'errore.
	 * @param ex
	 *            eccezione.
	 * @param additionalInfo
	 *            oggetto di qualsiasi natura.
	 */
	public EMFInternalError(String severity, String description, Exception ex, Object additionalInfo) {
		super();
		init(severity, description, ex, additionalInfo);
	} // public EMFInternalError(String severity, String description,

	// Exception ex, Object additionalInfo)

	/**
	 * Costruisce un oggetto di tipo <code>EMFInternalError</code> utilizzando lo stato del parametro in input .
	 * 
	 * @param internalError
	 *            oggetto della stessa classe.
	 */
	public EMFInternalError(EMFInternalError internalError) {
		super(internalError);
		if (internalError._nativeException != null)
			_nativeException = internalError._nativeException;
	} // public EMFInternalError(EMFInternalError internalError)

	/**
	 * Ritorna un clone dell'oggetto stesso.
	 * 
	 * @return CloneableObject il clone dell'oggetto.
	 * @see CloneableObject
	 */
	public CloneableObject cloneObject() {
		return new EMFInternalError(this);
	} // public CloneableObject cloneObject()

	/**
	 * Questo metodo ha il compito di inizializzare lo stato dell'oggetto,viene invocato da tutti i costruttori di
	 * <code>EMFInternalError</code>.
	 */
	private void init(String severity, String description, Exception ex, Object additionalInfo) {
		_logger.debug("EMFInternalError::init: invocato");

		setSeverity(severity);
		_logger.debug("EMFInternalError::init: severity [" + getSeverity() + "]");

		_nativeException = null;
		if (ex != null) {
			_nativeException = ex;
			StringWriter exStringWriter = new StringWriter();
			PrintWriter exPrintWriter = new PrintWriter(exStringWriter);
			ex.printStackTrace(exPrintWriter);
			if (description == null)
				description = exStringWriter.toString();
			else
				description += "\n" + exStringWriter.toString();
		} // if (ex != null)
		setDescription(description);
		_logger.debug("EMFInternalError::init: description [" + getDescription() + "]");

		setAdditionalInfo(additionalInfo);
	} // private void init(String severity, String description, Exception

	// ex, Object additionalInfo)

	/**
	 * Ritorna un <code>SourceBean</code> popolato con gli attributi dell'oggetto.
	 * 
	 * @return <code>SourceBean</code> contenente gli attributi dell'oggetto.
	 * @see SourceBean
	 */
	public SourceBean getSourceBean() {
		SourceBean errorBean = null;
		try {
			errorBean = new SourceBean(INTERNAL_ERROR_ELEMENT);
			errorBean.setAttribute(EMFAbstractError.ERROR_SEVERITY, getSeverity());
			errorBean.setAttribute(EMFAbstractError.ERROR_DESCRIPTION, getDescription());
			if (_nativeException != null)
				errorBean.setAttribute(ERROR_NATIVE_EXCEPTION, _nativeException);
			Object additionalInfo = getAdditionalInfo();
			if (additionalInfo != null)
				errorBean.setAttribute(EMFAbstractError.ERROR_ADDITIONAL_INFO, additionalInfo);
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "EMFInternalError::toXML:", ex);

		} // catch (SourceBeanException ex) try
		return errorBean;
	} // public SourceBean getSourceBean()

	/**
	 * Se l'oggetto è stato costruito ricevendo come parametro un' eccezione questa viene ritornata.
	 * 
	 * @return <code>Exception</code> parametro passato in input.
	 */
	public Exception getNativeException() {
		return _nativeException;
	} // public Exception getNativeException()
} // public class EMFInternalError extends EMFAbstractError implements
	// Serializable
