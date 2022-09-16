/*
 * Creato il Nov 18, 2004
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.util;

import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import com.engiweb.framework.tags.Util;

/**
 * @author savino
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class JSPReportUtil {
	public static String getFormToAction(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		Enumeration names = request.getParameterNames();
		//
		sb.append(
				"<html><head></head><body onload=\"window.parent.chiamaReport();\"><form name=\"form1\" method=\"post\" action=\"AdapterHTTP\" target=\"REPORT\">");
		String action = request.getParameter("ACTION_REDIRECT");
		sb.append(makeInput("ACTION_NAME", action));
		while (names.hasMoreElements()) {
			String key = (String) names.nextElement();
			if (key.toUpperCase().equals("PAGE") || key.toUpperCase().equals("ACTION_REDIRECT")
					|| key.toUpperCase().equals("QUERY_STRING") || key.equals("REQUEST_CONTAINER")
					|| key.equals("RESPONSE_CONTAINER"))
				continue;
			String[] values = request.getParameterValues(key);
			for (int i = 0; i < values.length; i++)
				sb.append(makeInput(key, values[i]));
		}
		sb.append("</form></body></html>");
		return sb.toString();
	}

	public static String makeInput(String name, String value) {
		return "<input type=\"hidden\" name=\"" + name + "\" value=\"" + Util.replace(value, "'", "\\'") + "\">";
	}

	public static String getQueryString(HttpServletRequest request) {
		String qs = request.getParameter("QUERY_STRING");
		/*
		 * StringTokenizer st = new StringTokenizer(qs, "&26"); StringBuffer sb = new StringBuffer(); while
		 * (st.hasMoreTokens()) { String par = (String)st.nextToken(); int i = par.indexOf("%3D"); if (i<0)continue;
		 * sb.append(par.substring(0,i)); sb.append("="); if (par.length()>i+3) sb.append(par.substring(i+3));
		 * sb.append("&"); } return sb.toString();
		 */
		return qs;
	}

	public static String getQueryStringForURL(String qs) {
		StringTokenizer st = new StringTokenizer(qs, "&");
		StringBuffer sb = new StringBuffer();
		while (st.hasMoreTokens()) {
			String par = (String) st.nextToken();
			int i = par.indexOf("=");
			if (i < 0)
				continue;
			sb.append(par.substring(0, i));
			sb.append("%3D");
			if (par.length() > i + 1)
				sb.append(par.substring(i + 1));
			sb.append("%26");
		}
		return sb.toString();
	}

	/**
	 * Crea lo url per la chiamata della Action che genera il pdf. 1) Elimina la page che ha chiamato questa jsp
	 * (ReportFramePage) 2) Imposta il parametro ACTION_NAME valorizzandolo col valore del parametro ACTION_REDIRECT 3)
	 * I restanti paramtri vengono replicati
	 * 
	 * @deprecated
	 */
	String getURLAction(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		Enumeration names = request.getParameterNames();
		String action = request.getParameter("ACTION_REDIRECT");
		sb.append("ACTION_NAME=");
		sb.append(action);
		sb.append("&");
		while (names.hasMoreElements()) {
			String key = (String) names.nextElement();
			if (key.toUpperCase().equals("PAGE") || key.toUpperCase().equals("ACTION_REDIRECT")
					|| key.toUpperCase().equals("QUERY_STRING"))
				continue;
			sb.append(key);
			sb.append("=");
			sb.append(request.getParameter(key));
			sb.append("&");
		}
		return sb.toString();
	}
}
