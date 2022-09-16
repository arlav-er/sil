package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

public class PattoRicercaDisponibilita extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(PattoRicercaDisponibilita.class.getName());
	private String className = this.getClass().getName();

	public PattoRicercaDisponibilita() {
	}

	public void service(SourceBean request, SourceBean response) {

		// FV non deve far niente!!!!!!!!!!!!!!!!!!!
		_logger.debug(className);

	}

}
