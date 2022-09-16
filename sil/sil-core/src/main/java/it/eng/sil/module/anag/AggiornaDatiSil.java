package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;

public class AggiornaDatiSil extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AggiornaDatiSil.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		try {
			// FIXME CHIAMARE SERVIZIO AGGIORNAMENTO DATI SU SIL
			SourceBean risultato = new SourceBean("Message");
		} catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowObject)", ex);
		}

	}
}