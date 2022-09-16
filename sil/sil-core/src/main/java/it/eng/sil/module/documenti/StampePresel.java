package it.eng.sil.module.documenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class StampePresel extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampePresel.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		_logger.debug(className);

	}
}