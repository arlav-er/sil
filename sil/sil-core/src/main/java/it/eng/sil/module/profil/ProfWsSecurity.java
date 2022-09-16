/*
 * Created on 13-mar-07
 */
package it.eng.sil.module.profil;

import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.sil.security.handlers.Utility;

/**
 * @author vuoto
 */

public class ProfWsSecurity extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProfWsSecurity.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		_logger.debug(className + "::service()");

		try {
			List props = Utility.loadPropertiesAsUnorderedMap();
			response.setAttribute("props", props);
		} catch (Exception e) {

			it.eng.sil.util.TraceWrapper.debug(_logger, "service::" + className, e);

		}

	}

}