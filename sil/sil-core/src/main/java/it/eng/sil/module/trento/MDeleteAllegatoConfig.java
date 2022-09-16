package it.eng.sil.module.trento;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class MDeleteAllegatoConfig extends AbstractSimpleModule {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MDeleteAllegatoConfig.class.getName());

	public void service(SourceBean request, SourceBean response) {
		doUpdate(request, response);
	}
}