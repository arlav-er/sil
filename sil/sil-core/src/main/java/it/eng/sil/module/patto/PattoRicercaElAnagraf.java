package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

public class PattoRicercaElAnagraf extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(PattoRicercaElAnagraf.class.getName());
	private String className = this.getClass().getName();

	public PattoRicercaElAnagraf() {
	}

	public void service(SourceBean request, SourceBean response) {

		// FV non deve far niente!!!!!!!!!!!!!!!!!!!
		_logger.debug(className);

	}

}
