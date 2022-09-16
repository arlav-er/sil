package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.ResultLogFormatter;

//modulo per la navigazione dei risultati della validazione
public class RisultatiCopiaProspettiMassiva extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		ResultLogFormatter formatter = null;
		BigDecimal prgRisUltimaCopia = null;
		// Recupero oggetto per la visualizzazione dei risultati
		formatter = (ResultLogFormatter) getRequestContainer().getSessionContainer()
				.getAttribute("RISULTATI_ULTIMA_COPIA_PROSP");
		if (formatter == null) {
			if (request.containsAttribute("prgUltimaCopiaMassiva")
					&& !request.getAttribute("prgUltimaCopiaMassiva").toString().equals("")) {
				prgRisUltimaCopia = new BigDecimal(request.getAttribute("prgUltimaCopiaMassiva").toString());
				formatter = new ResultLogFormatter(prgRisUltimaCopia);
				getRequestContainer().getSessionContainer().setAttribute("RISULTATI_ULTIMA_COPIA_PROSP", formatter);
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
				response.setAttribute(formatter.getPreviousPageProsp());
			} else if (pageId.equalsIgnoreCase("NEXT")) {
				response.setAttribute(formatter.getNextPageProsp());
			} else if (pageId.equalsIgnoreCase("SAME")) {
				response.setAttribute(formatter.getSamePageProsp());
			} else if (pageId.equalsIgnoreCase("LAST")) {
				response.setAttribute(formatter.getLastPageProsp());
			} else if (i > 0) {
				response.setAttribute(formatter.getPageNumberProsp(i));
			} else {
				response.setAttribute(formatter.getFirstPageProsp());
			}
		}

	}
}
