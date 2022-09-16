package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class LoadIscrCig extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(LoadIscrCig.class.getName());

	public void service(SourceBean request, SourceBean response) {
		try {
			SourceBean iscrCig = doSelect(request, response);
			if (iscrCig != null && iscrCig.containsAttribute("row")) {
				response.setAttribute("iscrCigPresenti", "true");
			} else {
				response.setAttribute("iscrCigPresenti", "false");
			}
		} catch (Exception e) {
			e.printStackTrace();
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile recuperare le iscrizioni CIG.", e);

		}
	}
}
