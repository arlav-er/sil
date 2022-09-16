/*
 * Created on 20-apr-07
 *
 */
package it.eng.sil.module.appEsterne;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author vuoto
 * 
 */

public class AppEsterne extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AppEsterne.class.getName());

	// private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {

		// TracerSingleton.log(it.eng.sil.Values.APP_NAME,
		// TracerSingleton.DEBUG, className);

		doSelect(request, response);

	}
}