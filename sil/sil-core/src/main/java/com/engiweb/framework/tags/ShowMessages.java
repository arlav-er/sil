package com.engiweb.framework.tags;

import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.ResponseContainerAccess;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.MessageAppender;

/**
 * Custom Tag che permette di visualizzare nella pagina JSP i messaggi che, ad esempio, un modulo può avere generato
 * durante la sua elaborazione.
 * 
 * Il modulo inserisce questi messaggi utilizzando della classe <code>it.eng.afExt.utils.ReportOperationResult</code> il
 * metodo <code>reportSuccess</code>.
 * 
 * --Elenco degli attributi--
 * 
 * prefix : Stringa, attributo opzionale. Indica un prefisso arbitrario da anteporre alla stringa usata per la ricerca
 * dei messaggi nella service-response, perché ci potrebbero essere più moduli che generano messaggi e in questo modo si
 * può scegliere quali visualizzare. Solitamente "prefix" è il nome del modulo che ha generato il messaggio (con
 * ReportOperationResult).
 * 
 * --Esempio--
 * 
 * Nella pagina JSP si include questo codice nel punto in cui si vuole vedere i messaggi che saranno visualizzati alla
 * fine dell'esecuzione del modulo, in questo caso si vuole visualizzare in verde i messaggi generati dal modulo
 * "M_AnagMain":
 * 
 * <font color="green"> <af:showMessages prefix="M_AnagMain"/> </font>
 */
public class ShowMessages extends TagSupport {

	/**
	 * Prefisso opzionale associabile ai messaggi da visualizzare in modo da distinguerli da altri con prefissi
	 * differenti.
	 */
	private String prefix = "";

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

		SourceBean serviceResponse = responseContainer.getServiceResponse();

		if (serviceResponse == null) {
			LogUtils.logError("doStartTag", "serviceResponse is null", this);
			throw new JspException("serviceResponse is null");
		}

		if (prefix.length() > 0) {
			prefix += ".";
		}

		Vector messages = serviceResponse.getAttributeAsVector(prefix + MessageAppender.USER_MESSAGE);

		MessagesHtmlBuilder builder = new MessagesHtmlBuilder(messages);
		builder.build();

		try {
			pageContext.getOut().print(builder.getResult());
		} catch (Exception exp) {

			LogUtils.logError("doStartTag", "print result error", exp, this);
			throw new JspException("print result error", exp);
		}

		return SKIP_BODY;
	}

	/**
	 * 
	 */
	public void release() {
		prefix = "";
	}

	/**
	 * Assegna il prefisso (opzionale).
	 * 
	 * @param prefix
	 *            Valore del prefisso.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Costruisce su una stringa il codice HTML necessario a rappresentare i messaggi passati.
	 */
	private class MessagesHtmlBuilder {

		private Vector messages;
		private StringBuffer result = new StringBuffer();

		/**
		 * Costruttore.
		 * 
		 * @param messages
		 *            Messaggi da essere rappresentati.
		 */
		MessagesHtmlBuilder(Vector messages) {
			this.messages = messages;
		}

		/**
		 * Crea il codice HTML dei messaggi.
		 */
		void build() throws JspException {

			result = new StringBuffer();

			if (messages.size() > 0) {
				result.append("<ul>");
			}

			for (Iterator iter = messages.iterator(); iter.hasNext();) {

				try {

					SourceBean sb = (SourceBean) iter.next();

					String message = (String) sb.getAttribute(MessageAppender.TEXT);
					result.append("<li>" + message + "</li>");

				} catch (Exception exp) {
					LogUtils.logError("build", "SourceBean MESSAGE do not contain TEXT", exp, this);
					throw new JspException("SourceBean MESSAGE do not contain TEXT", exp);
				}
			}

			if (messages.size() > 0) {
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
	}
}