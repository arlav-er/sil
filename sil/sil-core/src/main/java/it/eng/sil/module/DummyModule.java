package it.eng.sil.module;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.afExt.utils.StringUtils;

/**
 * Modulo che non fa nulla di nulla. Ma proprio nulla!
 * 
 * @author Luigi Antenucci
 */
public class DummyModule extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DummyModule.class.getName());

	private static final String className = StringUtils.getClassName(DummyModule.class);

	public DummyModule() {
	}

	public void service(SourceBean request, SourceBean response) {
		_logger.debug(className + ".service() - non faccio nulla");

		// NOP
	}

}