/*
 * Creato il 26-nov-04
 * Author: vuoto
 * 
 */
package it.eng.sil.action;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.AbstractAction;

/**
 * @author vuoto
 * 
 */
public class WarningDetectBrowser extends AbstractAction {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(WarningDetectBrowser.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		_logger.debug(className + "::service()");

	}

}