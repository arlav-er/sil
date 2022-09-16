package com.engiweb.framework.tags;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.ResponseContainerAccess;
import com.engiweb.framework.error.EMFAbstractError;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.LogUtils;

/**
 * Custom Tag che permette di visualizzare nella pagina JSP gli errori che, ad esempio, un modulo può avere generato
 * durante la sua elaborazione.
 * 
 * Il modulo inserisce questi messaggi utilizzando della classe <code>it.eng.afExt.utils.ReportOperationResult</code> il
 * metodo <code>reportFailure</code>.
 * 
 * --Elenco degli attributi--
 * 
 * showUserError : Boolean, attributo opzionale, default true. Indica se mostrare i messaggi di errore utente.
 * showInternalError : Boolean, attributo opzionale, default false. Indica se mostrare i messaggi di errore interno.
 * 
 * --Esempio--
 * 
 * Nella pagina JSP si include questo codice nel punto in cui si vuole vedere i messaggi di errore presenti
 * nell'error-handler della response-container. In questo caso si vuole visualizzare in rosso i messaggi generati.
 * 
 * <font color="red"> <af:showErrors/> </font>
 */
public class ShowErrors extends TagSupport {

	private boolean showUserError = true;
	private boolean showInternalError = false;

	/**
	 * 
	 */
	public int doStartTag() throws JspException {

		if (pageContext == null) {
			LogUtils.logDebug("doStartTag", "pageContext is null", this);
			throw new JspException("pageContext is null");
		}

		HttpServletRequest httpRequest = null;
		try {
			httpRequest = (HttpServletRequest) pageContext.getRequest();
		} catch (Exception exp) {
			LogUtils.logError("doStartTag", "pageContext error", exp, this);
			throw new JspException("pageContext error", exp);
		}

		if (httpRequest == null) {
			LogUtils.logError("doStartTag", "httpRequest is null", this);
			throw new JspException("httpRequest is null");
		}

		ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(httpRequest);

		if (responseContainer == null) {
			LogUtils.logError("doStartTag", "responseContainer is null", this);
			throw new JspException("responseContainer is null");
		}

		EMFErrorHandler engErrorHandler = responseContainer.getErrorHandler();

		ErrorsHtmlBuilder builder = new ErrorsHtmlBuilder(engErrorHandler);
		builder.build();

		if (builder.errorsPresent()) {

			try {

				pageContext.getOut().print(builder.getResult());
			} catch (Exception exp) {

				LogUtils.logError("doStartTag", "print result error", exp, this);
				throw new JspException("print result error", exp);
			}

		}

		return SKIP_BODY;
	}

	/**
	 * 
	 */
	public void release() {
		showUserError = true;
		showInternalError = false;
	}

	/**
	 * Indica se visualizzare o meno gli errori interni.
	 * 
	 * @param showInternalError
	 */
	public void setShowInternalError(boolean showInternalError) {
		this.showInternalError = showInternalError;
	}

	/**
	 * Indica se visualizzare o meno gli errori utente.
	 * 
	 * @param showUserError
	 */
	public void setShowUserError(boolean showUserError) {
		this.showUserError = showUserError;
	}

	/**
	 * Costruisce su una stringa il codice HTML necessario a rappresentare gli errori presenti.
	 */
	private class ErrorsHtmlBuilder {

		private EMFErrorHandler engErrorHandler;
		private StringBuffer result = new StringBuffer();

		/**
		 * Costruttore.
		 * 
		 * @param engErrorHandler
		 *            Contenitore degli errori.
		 */
		ErrorsHtmlBuilder(EMFErrorHandler engErrorHandler) {
			this.engErrorHandler = engErrorHandler;
		}

		/**
		 * Crea il codice HTML degli errori.
		 */
		void build() throws JspException {

			if ((engErrorHandler == null) || engErrorHandler.isOK())
				return;

			Collection errors = engErrorHandler.getErrors();
			if (errors.size() > 0) {
				result.append("<ul>");
			}

			StringBuffer outUerros = new StringBuffer();
			StringBuffer outIerros = new StringBuffer();

			for (Iterator errorsIterator = errors.iterator(); errorsIterator.hasNext();) {

				EMFAbstractError error = (EMFAbstractError) errorsIterator.next();

				if (error instanceof EMFUserError) {

					EMFUserError uError = (EMFUserError) error;
					outUerros.append("<li>" + uError.getDescription() + "</li>");
				}

				if (error instanceof EMFInternalError) {

					EMFInternalError iError = (EMFInternalError) error;
					outIerros.append("<li>" + iError.getDescription() + "</li>");
				}
			}

			if (errors.size() > 0) {

				if (showUserError)
					result.append(outUerros.toString());
				if (showInternalError)
					result.append(outIerros.toString());

				result.append("</ul>");
			}
		}

		/**
		 * Recupera il risultato.
		 * 
		 * @return Stringa contenente il codice HTML.
		 */
		String getResult() {

			return this.result.toString();
		}

		/**
		 * Indica se ci sono errori presenti.
		 * 
		 * @return
		 */
		boolean errorsPresent() {
			return result.length() > 0;
		}
	}

}