/*
 * Creato il 20-mag-04
 * Author: vuoto
 * 
 */
package it.eng.sil.action;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.AbstractAction;

/**
 * 
 * /**
 * 
 * @author vuoto
 * 
 */
public class AccessoNegato extends AbstractAction {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AccessoNegato.class.getName());
	private final String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {

		_logger.debug(className + "::service()");

	}

}