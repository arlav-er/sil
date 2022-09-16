package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class StampaPromemoria extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaPromemoria.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		_logger.debug(className);

	}
}