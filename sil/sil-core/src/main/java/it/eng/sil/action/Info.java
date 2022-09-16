package it.eng.sil.action;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.AbstractAction;

/**
 * @author vuoto
 * 
 */

public class Info extends AbstractAction {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Info.class.getName());
	private final String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {

		_logger.debug(className + "::service()");

		/*
		 * // recupera sessione eed utente connesso RequestContainer requestContainer = getRequestContainer();
		 * SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer(); User user =
		 * (User) sessionContainer.getAttribute(User.USERID); BigDecimal userid = new BigDecimal(user.getCodut());
		 * 
		 * 
		 * try {
		 * 
		 * 
		 * 
		 * 
		 * 
		 * } catch (Exception ex) {
		 * 
		 * _logger.warn( className+ "::service()\r\n" + ex); }
		 * 
		 */
	}

}