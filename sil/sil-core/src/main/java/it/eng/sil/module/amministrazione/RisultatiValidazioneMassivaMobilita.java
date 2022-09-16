package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.ResultLogFormatter;

//modulo per la navigazione dei risultati della validazione
public class RisultatiValidazioneMassivaMobilita extends AbstractSimpleModule {
	private static final int LOG_VALIDAZIONE_MOBILITA_MASSIVA = 0;

	public void service(SourceBean request, SourceBean response) throws Exception {
		ResultLogFormatter formatter = null;
		BigDecimal prgRisUltimaVal = null;
		// Recupero oggetto per la visualizzazione dei risultati
		formatter = (ResultLogFormatter) getRequestContainer().getSessionContainer()
				.getAttribute("RISULTATI_ULTIMA_VALIDAZIONE_MOBILITA");
		if (formatter == null) {
			if (request.containsAttribute("prgUltimaValidazioneMassiva")
					&& !request.getAttribute("prgUltimaValidazioneMassiva").toString().equals("")) {
				prgRisUltimaVal = new BigDecimal(request.getAttribute("prgUltimaValidazioneMassiva").toString());
				formatter = new ResultLogFormatter(prgRisUltimaVal);
				getRequestContainer().getSessionContainer().setAttribute("RISULTATI_ULTIMA_VALIDAZIONE_MOBILITA",
						formatter);
			}
		}
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
				response.setAttribute(formatter.getPreviousPage(LOG_VALIDAZIONE_MOBILITA_MASSIVA));
			} else if (pageId.equalsIgnoreCase("NEXT")) {
				response.setAttribute(formatter.getNextPage(LOG_VALIDAZIONE_MOBILITA_MASSIVA));
			} else if (pageId.equalsIgnoreCase("SAME")) {
				response.setAttribute(formatter.getSamePage(LOG_VALIDAZIONE_MOBILITA_MASSIVA));
			} else if (pageId.equalsIgnoreCase("LAST")) {
				response.setAttribute(formatter.getLastPage(LOG_VALIDAZIONE_MOBILITA_MASSIVA));
			} else if (i > 0) {
				response.setAttribute(formatter.getPageNumber(i, LOG_VALIDAZIONE_MOBILITA_MASSIVA));
			} else {
				response.setAttribute(formatter.getFirstPage(LOG_VALIDAZIONE_MOBILITA_MASSIVA));
			}
		}

	}
}
