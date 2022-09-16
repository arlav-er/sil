package it.eng.sil.action;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.AbstractAction;

/**
 * @author Franco Vuoto
 * 
 *         Reindirizza ad una pagina di cortesia quando la sessione scade.
 * 
 */
public class SessionExpired extends AbstractAction {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SessionExpired.class.getName());
	private String className = this.getClass().getName();

	public void init(SourceBean config) {
	}

	public void service(SourceBean request, SourceBean response) {
		_logger.debug(className + "::service()");

	}
}