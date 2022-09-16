package it.eng.sil.module.login;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

/**
 * 
 * @author Franco Vuoto
 * @version 1.0
 */
public class LoginNO extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(LoginNO.class.getName());
	private String className = this.getClass().getName();

	public LoginNO() {
	}

	public void service(SourceBean request, SourceBean response) {
		_logger.debug(className + "::service()");

	}
}