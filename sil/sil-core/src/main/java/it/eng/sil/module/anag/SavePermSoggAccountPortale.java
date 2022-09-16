package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class SavePermSoggAccountPortale extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(SavePermSoggAccountPortale.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {

		try {
			boolean ret = doInsert(request, response);
			if (!ret) {
				response.setAttribute("operationResult", "ERROR");
				response.setAttribute("RISULTATO",
						"Non è stato possibile allineare i dati relativi al documento di soggiorno.");
			} else {
				response.setAttribute("operationResult", "SUCCESS");
			}
		} catch (Exception ex) {
			response.setAttribute("operationResult", "ERROR");
			response.setAttribute("RISULTATO",
					"Errore generico aggiornamento dati relativi al documento di soggiorno.");
			_logger.error("Errore aggiornamento notizie su cittadini stranieri", ex);
		}
	}
}