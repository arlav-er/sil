package it.eng.sil.util;

import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.Constants;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.util.JavaScript;

/**
 * QueryString
 * 
 * Restituisce la QueryString relativa al servizio richiesto (@serviceRequest) escludendo i parametri: PAGE,
 * REQUEST_CONTAINER, ACTION_REDIRECT, ecc..
 * 
 * @author: Stefania Orioli - September 2003 / Luigi Antenucci
 */
public abstract class QueryString {

	public static String GetQueryString(SourceBean serviceRequest) {

		StringBuffer queryStringBuffer = new StringBuffer();
		Vector queryParameters = serviceRequest.getContainedAttributes();
		SourceBeanAttribute parameter = null;
		String parameterKey = "";
		String parameterValue = "";

		for (int i = 0; i < queryParameters.size(); i++) {
			parameter = (SourceBeanAttribute) queryParameters.get(i);
			parameterKey = parameter.getKey();
			if (!(parameterKey.equalsIgnoreCase("PAGE") || parameterKey.equalsIgnoreCase("MODULE")
					|| parameterKey.equalsIgnoreCase("MESSAGE") || parameterKey.equalsIgnoreCase("ACTION_NAME"))) {
				parameterValue = parameter.getValue().toString();
				queryStringBuffer.append("&");
				queryStringBuffer.append(JavaScript.escape(parameterKey.toUpperCase()));
				queryStringBuffer.append("=");
				queryStringBuffer.append(JavaScript.escape(parameterValue));
			}
		}
		return queryStringBuffer.toString();
	}

	/**
	 * Rende la stringa con la QueryString corrente. NB: *non* contiene i parametri relativi all'Application Framework,
	 * ossia PAGE, ACTION_REDIRECT, QUERY_STRING, REQUEST_CONTAINER e RESPONSE_CONTAINER"). In realtà il "page" è
	 * presente solo se "includePage" è TRUE (per default lo è).
	 * 
	 * @author Luigi Antenucci
	 */
	public static final String getFromServiceRequest(SourceBean serviceRequest) {
		return getFromServiceRequest(serviceRequest, true);
	}

	public static final String getFromServiceRequest(SourceBean serviceRequest, boolean includePage) {
		StringBuffer queryString = new StringBuffer();

		Vector params = serviceRequest.getContainedAttributes();
		for (int i = 0; i < params.size(); i++) {
			SourceBeanAttribute param = (SourceBeanAttribute) params.elementAt(i);
			String key = (String) param.getKey();
			key = key.toUpperCase();

			if ((key.equals(Constants.PAGE) && !includePage) || (key.equals(Constants.ACTION_NAME) && !includePage)
					|| key.equals(Constants.REQUEST_CONTAINER) || key.equals(Constants.RESPONSE_CONTAINER)
					|| key.equals("AF_MODULE_NAME")) {
				// non riporto il parametro
			} else {
				String valStr = param.getValue().toString();

				queryString.append('&');
				queryString.append(JavaScript.escape(key));
				queryString.append('=');
				queryString.append(JavaScript.escape(valStr));
			}

		}
		return queryString.toString();
	}

	/**
	 * Elimina un parametro (attributo) dalla QueryString. Rende la nuova QueryString senza il parametro passato. La
	 * stringa resa non comincia e non termina mai con il carattere "&".
	 * 
	 * @author Luigi Antenucci
	 */
	public static final String removeParameter(String url, String paramToRemove) {
		return removeParameter(url, new String[] { paramToRemove });
	}

	public static final String removeParameter(String url, String[] paramsToRemove) {

		StringBuffer urlNew = new StringBuffer();
		StringTokenizer tokenizer = new StringTokenizer(url, "&?", false);
		// syntax: StringTokenizer(String str, String delim, boolean
		// returnDelims)

		while (tokenizer.hasMoreTokens()) {
			String param = tokenizer.nextToken();

			int pos = param.indexOf('=');
			if (pos >= 0) {
				String key = param.substring(0, pos);

				boolean toRemove = false;
				int p = 0;
				while ((p < paramsToRemove.length) && !toRemove) {
					toRemove = key.equalsIgnoreCase(paramsToRemove[p]);
					p++;
				}
				if (!toRemove) {
					urlNew.append(param);
					urlNew.append('&');
				}
			}
		}
		if (urlNew.length() > 0) {
			return urlNew.substring(0, urlNew.length() - 1); // tolgo "&"
																// finale
		} else {
			return "";
		}
	}

	/**
	 * Rende TRUE se un parametro (la parte di chiave, prima del carattaere di uguale) è presente nella QueryString;
	 * rende FALSE altrimenti.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final boolean existsParameter(String url, String paramToSearch) {

		StringTokenizer tokenizer = new StringTokenizer(url, "&?", false);

		while (tokenizer.hasMoreTokens()) {
			String param = tokenizer.nextToken();

			int pos = param.indexOf('=');
			if (pos >= 0) {
				String key = param.substring(0, pos);
				if (key.equalsIgnoreCase(paramToSearch)) {
					return true;
				}
			}
		}
		return false;
	}

	public static String toInputHidden(SourceBean serviceRequest, String parameter) {
		StringBuffer html = new StringBuffer();
		Vector v = serviceRequest.getAttributeAsVector(parameter);
		for (int i = 0; i < v.size(); i++) {
			String value = v.get(i).toString();
			html.append("<input type='hidden' name='");
			html.append(parameter);
			html.append("' value='");
			html.append(value);
			html.append("'>\n\r");
		}
		return html.toString();
	}

	public static String toInputHidden(SourceBean serviceRequest, String parameterIn, String parameterOut) {
		StringBuffer html = new StringBuffer();
		Vector v = serviceRequest.getAttributeAsVector(parameterIn);
		for (int i = 0; i < v.size(); i++) {
			String value = v.get(i).toString();
			html.append("<input type='hidden' name='");
			html.append(parameterOut);
			html.append("' value='");
			html.append(value);
			html.append("'>\n\r");
		}
		return html.toString();
	}

	public static String toURLParameter(SourceBean serviceRequest, String parameter) {
		StringBuffer html = new StringBuffer();
		Vector v = serviceRequest.getAttributeAsVector(parameter);
		for (int i = 0; i < v.size(); i++) {
			String value = v.get(i).toString();
			html.append(parameter);
			html.append("=");
			html.append(value);
			html.append("&");
		}
		return html.toString();
	}

	public static String toURLParameter(SourceBean serviceRequest, String parameterIn, String parameterOut) {
		StringBuffer html = new StringBuffer();
		Vector v = serviceRequest.getAttributeAsVector(parameterIn);
		for (int i = 0; i < v.size(); i++) {
			String value = v.get(i).toString();
			html.append(parameterOut);
			html.append("=");
			html.append(value);
			html.append("&");
		}
		return html.toString();
	}

	public static void main(String[] args) {
		System.out.println(removeParameter("uno=1&due=2&tre=3", "uno"));
		System.out.println(removeParameter("uno=1&due=2&tre=3", "due"));
		System.out.println(removeParameter("uno=1&due=2&tre=3", "tre"));
		System.out.println(removeParameter("?uno=1&due=2&tre=3", "uno"));
		System.out.println(removeParameter("xxxx?uno=1&due=2&&&tre=3&yy&&&&", "uno"));
		System.out.println("------------");
		System.out.println(removeParameter("uno=1&due=2&tre=3", new String[] { "uno" }));
		System.out.println(removeParameter("uno=1&due=2&tre=3", new String[] { "uno", "due" }));
	}

}