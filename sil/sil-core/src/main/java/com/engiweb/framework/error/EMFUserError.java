package com.engiweb.framework.error;

import java.io.Serializable;
import java.util.Vector;

import com.engiweb.framework.base.CloneableObject;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.message.MessageBundle;

/**
 * Un'istanza di <code>EMFUserError</code> rappresenta un errore codificato. Questo significa che esiste un repository
 * contenente il riferimento a questo errore e la sua descrizione.
 * 
 * @version 1.0, 06/03/2002
 * @author Luigi Bellio
 * @see EMFErrorHandler
 */
public class EMFUserError extends EMFAbstractError implements Serializable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(EMFUserError.class.getName());
	public static final String USER_ERROR_ELEMENT = "USER_ERROR";
	public static final String USER_ERROR_CODE = "CODE";
	private int _code = 0;

	/**
	 * Costruisce un oggetto di tipo <code>EMFUserError</code> identificandolo tramite una severity e un codice di
	 * errore .
	 * 
	 * @param severity
	 *            severity dell'errore.
	 * @param code
	 *            codice di errore.
	 */
	public EMFUserError(String severity, int code) {
		super();
		init(severity, code, null, null);
	} // public EMFUserError(String severity, int code)

	/**
	 * Costruisce un oggetto di tipo <code>EMFUserError</code> identificandolo tramite una severity ,un codice di errore
	 * e una collezione di parametri che andranno a sostituire i caratteri <em>%</em> nella stringa di descrizione.
	 * 
	 * @param severity
	 *            severity dell'errore.
	 * @param code
	 *            codice di errore.
	 * @param params
	 *            vettore di parametri che verranno inseriti nella stringa di descrizione.
	 */
	public EMFUserError(String severity, int code, Vector params) {
		super();
		init(severity, code, params, null);
	} // public EMFUserError(String severity, int code, Vector params)

	/**
	 * Costruisce un oggetto di tipo <code>EMFUserError</code> identificandolo tramite una severity ,un codice di errore
	 * , una collezione di parametri che andranno a sostituire i caratteri <em>%</em> nella stringa di descrizione e un
	 * oggetto di qualsiasi natura.
	 * 
	 * @param severity
	 *            severity dell'errore.
	 * @param code
	 *            codice di errore.
	 * @param params
	 *            vettore di parametri che verranno inseriti nella stringa di descrizione.
	 * @param additionalInfo
	 *            oggetto di qualsiasi natura.
	 */
	public EMFUserError(String severity, int code, Vector params, Object additionalInfo) {
		super();
		init(severity, code, params, additionalInfo);
	} // public EMFUserError(String severity, int code, Vector params, Object
		// additionalInfo)

	public EMFUserError(String severity, int code, Vector params, Object additionalInfo, String dettaglio) {
		super();
		init(severity, code, params, additionalInfo, dettaglio);
	} // public EMFUserError(String severity, int code, Vector params, Object
		// additionalInfo, String dettaglio)

	/**
	 * Costruisce un oggetto di tipo <code>EMFUserError</code> utilizzando lo stato del parametro in input .
	 * 
	 * @param EMFUserError
	 *            oggetto della stessa classe.
	 */
	public EMFUserError(EMFUserError userError) {
		super(userError);
		_code = userError._code;
	} // public EMFUserError(EMFUserError userError)

	/**
	 * Costruttore con la possibilità di passare i parametri come lista di Stringhe
	 * 
	 * @param severity
	 * @param code
	 * @param paramstring
	 */
	public EMFUserError(String severity, int code, String... paramstring) {
		super();
		Vector<String> params = new Vector<String>();
		for (String parametro : paramstring) {
			params.add(parametro);
		}
		init(severity, code, params, null);
	}

	/**
	 * Costruttore con la possibilità di passare il dettaglio Errore
	 * 
	 * @param severity
	 * @param code
	 * @param paramstring
	 */
	public EMFUserError(String severity, int code, boolean existDettaglio, String dettaglio) {
		super();
		if (existDettaglio) {
			init(severity, code, dettaglio);
		} else {
			init(severity, code, null, null);
		}

	}

	/**
	 * Ritorna un clone dell'oggetto stesso.
	 * 
	 * @return CloneableObject il clone dell'oggetto.
	 * @see CloneableObject
	 */
	public CloneableObject cloneObject() {
		return new EMFUserError(this);
	} // public CloneableObject cloneObject()

	private void init(String severity, int code, String dettaglio) {
		_logger.debug("EMFUserError::init: invocato");

		setSeverity(severity);
		_logger.debug("EMFUserError::init: severity [" + getSeverity() + "]");

		_code = code;
		_logger.debug("EMFUserError::init: code [" + code + "]");

		String description = MessageBundle.getMessage(String.valueOf(code));
		if (description == null) {
			description = dettaglio;
		} else {
			description = description + "<br>" + dettaglio;
		}
		setDescription(description);
	}

	/**
	 * Questo metodo ha il compito di inizializzare lo stato dell'oggetto,viene invocato da tutti i costruttori di
	 * <code>EMFUserError</code>.
	 */
	private void init(String severity, int code, Vector params, Object additionalInfo) {
		_logger.debug("EMFUserError::init: invocato");

		setSeverity(severity);
		_logger.debug("EMFUserError::init: severity [" + getSeverity() + "]");

		_code = code;
		_logger.debug("EMFUserError::init: code [" + code + "]");

		String description = MessageBundle.getMessage(String.valueOf(code));
		setDescription(description);
		if ((params != null) && (description != null)) {
			for (int i = 0; i < params.size(); i++) {
				String toParse = description;
				String replacing = "%" + i;
				String replaced = (String) params.elementAt(i);
				StringBuffer parsed = new StringBuffer();
				int parameterIndex = toParse.indexOf(replacing);
				while (parameterIndex != -1) {
					parsed.append(toParse.substring(0, parameterIndex));
					parsed.append(replaced);
					toParse = toParse.substring(parameterIndex + replacing.length(), toParse.length());
					parameterIndex = toParse.indexOf(replacing);
				} // while (parameterIndex != -1)
				parsed.append(toParse);
				description = parsed.toString();
			} // for (int i = 0; i < params.size(); i++)
			setDescription(description);
		} // if ((params != null) && (description != null))
		_logger.debug("EMFUserError::init: description [" + getDescription() + "]");

		setAdditionalInfo(additionalInfo);
	} // private void init(String severity, int code, Vector params, Object
		// additionalInfo)

	/**
	 * Questo metodo ha il compito di inizializzare lo stato dell'oggetto,viene invocato da tutti i costruttori di
	 * <code>EMFUserError</code>.
	 */
	private void init(String severity, int code, Vector params, Object additionalInfo, String dettaglio) {
		_logger.debug("EMFUserError::init: invocato");

		setSeverity(severity);
		_logger.debug("EMFUserError::init: severity [" + getSeverity() + "]");

		_code = code;
		_logger.debug("EMFUserError::init: code [" + code + "]");

		String description = MessageBundle.getMessage(String.valueOf(code));
		setDescription(description);
		if ((params != null) && (description != null)) {
			for (int i = 0; i < params.size(); i++) {
				String toParse = description;
				String replacing = "%" + i;
				String replaced = (String) params.elementAt(i);
				StringBuffer parsed = new StringBuffer();
				int parameterIndex = toParse.indexOf(replacing);
				while (parameterIndex != -1) {
					parsed.append(toParse.substring(0, parameterIndex));
					parsed.append(replaced);
					toParse = toParse.substring(parameterIndex + replacing.length(), toParse.length());
					parameterIndex = toParse.indexOf(replacing);
				} // while (parameterIndex != -1)
				parsed.append(toParse);
				description = parsed.toString();
			} // for (int i = 0; i < params.size(); i++)
			setDescription(description);
		} // if ((params != null) && (description != null))
		else {
			if (description != null && dettaglio != null && !description.equalsIgnoreCase(dettaglio)) {
				description = description + "<br>" + dettaglio;
				setDescription(description);
			}
		}
		_logger.debug("EMFUserError::init: description [" + getDescription() + "]");

		setAdditionalInfo(additionalInfo);
	} // private void init(String severity, int code, Vector params, Object
		// additionalInfo, String dettaglio)

	/**
	 * Ritorna il codice dell'errore.
	 * <p>
	 * 
	 * @return <em>int</em> codice dell'errore.
	 */
	public int getCode() {
		return _code;
	} // public Integer getCode()

	/**
	 * Ritorna un <code>SourceBean</code> popolato con gli attributi dell'oggetto.
	 * 
	 * @return <code>SourceBean</code> contenente gli attributi dell'oggetto.
	 * @see SourceBean
	 */
	public SourceBean getSourceBean() {
		SourceBean errorBean = null;
		try {
			errorBean = new SourceBean(USER_ERROR_ELEMENT);
			errorBean.setAttribute(EMFAbstractError.ERROR_SEVERITY, getSeverity());
			errorBean.setAttribute(USER_ERROR_CODE, String.valueOf(_code));
			errorBean.setAttribute(EMFAbstractError.ERROR_DESCRIPTION, getDescription());
			// TODO Savino: inserimento nel SourceBean dell'id dell'errore(hash
			// code)
			errorBean.setAttribute("ERROR_ID", String.valueOf(this.hashCode()));
			Object additionalInfo = getAdditionalInfo();
			if (additionalInfo != null)
				errorBean.setAttribute(EMFAbstractError.ERROR_ADDITIONAL_INFO, additionalInfo);
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "EMFUserError::getSourceBean:", ex);

		} // catch (SourceBeanException ex) try
		return errorBean;
	} // public SourceBean getSourceBean()
} // public class EMFUserError extends EMFAbstractError implements
	// Serializable
