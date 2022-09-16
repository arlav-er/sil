/*
 * Creato il 4-nov-04
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * Modulo per la navigazione dei risultati della validazioen massiva
 * <p/>
 * 
 * @author roccetti
 */
public class SelezionaPaginaRisultati extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		// Recupero oggetto visualizzazione dati in sessione
		ResultLogFormatter formatter = (ResultLogFormatter) getRequestContainer().getSessionContainer()
				.getAttribute("RISULTATICORRENTI");
		if (formatter != null) {
			// Guardo quale pagina devo mostrare (di default è la prima)
			String pageId = StringUtils.getAttributeStrNotNull(request, "PAGERISULTVALMASSIVA");
			// prelevo il numero di pagina se non sono stati premuti gli altri
			// bottoni di navigazione PREVIOUS, NEXT, SAME, FIRST, LAST
			int i = 0;
			try {
				i = Integer.parseInt(pageId);
			} catch (Exception e) {
				// non faccio nulla -> i rimane a zero
			}
			if (pageId.equalsIgnoreCase("PREVIOUS")) {
				response.setAttribute(formatter.getPreviousPage());
			} else if (pageId.equalsIgnoreCase("NEXT")) {
				response.setAttribute(formatter.getNextPage());
			} else if (pageId.equalsIgnoreCase("SAME")) {
				response.setAttribute(formatter.getSamePage());
			} else if (pageId.equalsIgnoreCase("LAST")) {
				response.setAttribute(formatter.getLastPage());
			} else if (i > 0) {
				response.setAttribute(formatter.getPageNumber(i));
			} else {
				response.setAttribute(formatter.getFirstPage());
			}
		}
	}
}
