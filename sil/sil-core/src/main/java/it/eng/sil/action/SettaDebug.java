package it.eng.sil.action;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.AbstractAction;

/**
 * @author author
 * 
 *         Gestisce l'autenticazione
 * 
 */
public class SettaDebug extends AbstractAction {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SettaDebug.class.getName());

	public void service(SourceBean request,
			SourceBean response) {/*
									 * boolean debug = false; int minLogSeverity = 0; if
									 * (request.containsAttribute("DEBUG")) { debug = ((String)
									 * request.getAttribute("DEBUG")).equalsIgnoreCase("true");
									 * TracerSingleton.setDebug(debug, "SIL"); _logger.debug("Settato debug = [" + debug
									 * + "]");
									 * 
									 * }
									 * 
									 * if (request.containsAttribute("MINLOGSEVERITY")) { minLogSeverity = new
									 * BigDecimal((String) request.getAttribute("MINLOGSEVERITY")).intValue();
									 * TracerSingleton.setMinLogSeverity(minLogSeverity, "SIL");
									 * _logger.debug("Settato livello di log = [" + minLogSeverity + "]");
									 * 
									 * } try { response.setAttribute("DEBUG", new
									 * Boolean(TracerSingleton.getDebug("SIL")));
									 * response.setAttribute("MINLOGSEVERITY", new
									 * Integer(TracerSingleton.getMinLogSeverity("SIL"))); } catch (Exception e) { }
									 */
	}

}

// end action
