package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Sottosistema;

public class GetTestataRichiesta extends AbstractSimpleModule {
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		ReportOperationResult ror = new ReportOperationResult(this, response);
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		String strNewPrgRichiestaAz = "";
		if (sessionContainer.getAttribute("IDO_NEW_PRGRICHIESTAAZ") != null) {
			strNewPrgRichiestaAz = sessionContainer.getAttribute("IDO_NEW_PRGRICHIESTAAZ").toString();
			if (strNewPrgRichiestaAz.compareTo("") != 0) {
				try {
					request.delAttribute("prgRichiestaAZ");
					request.setAttribute("prgRichiestaAZ", strNewPrgRichiestaAz);
				} catch (Exception e) {
					String msg = "Errore recupero progressivo richiesta";
					ror.reportFailure(e, className, msg);
				}
				sessionContainer.delAttribute("IDO_NEW_PRGRICHIESTAAZ");
			}
		}

		// INIT-PARTE-TEMP
		if (Sottosistema.CM.isOff()) {
			// END-PARTE-TEMP
			doSelectAS(request, response);
			// INIT-PARTE-TEMP
		} else {
			// END-PARTE-TEMP
			doSelectASCM(request, response);
			// INIT-PARTE-TEMP
		}
		// END-PARTE-TEMP
	}

	/**
	 * il metodo recupera le informazione della richiesta con l'aggiunta dei campi per l'Art.16
	 * 
	 * @param request
	 * @param response
	 */
	private void doSelectAS(SourceBean request, SourceBean response) {
		this.setSectionQuerySelect("QUERY_AS");
		doSelect(request, response);
	}

	/**
	 * il metodo recupera le informazione della richiesta con l'aggiunta dei campi per l'Art.16 e per il Collocamento
	 * Mirato
	 * 
	 * @param request
	 * @param response
	 */
	private void doSelectASCM(SourceBean request, SourceBean response) {
		this.setSectionQuerySelect("QUERY_CM");
		doSelect(request, response);
	}

	/**
	 * il metodo sostituisce il nome dello statement per recuperare la vecchia versione della select escludendo i campi
	 * sulla tabella per l'Art.16
	 * 
	 * @param request
	 * @param response
	 */
	private void doSelectOld(SourceBean request, SourceBean response) {
		ReportOperationResult ror = new ReportOperationResult(this, response);
		SourceBean statement = (SourceBean) getConfig().getAttribute(QUERY);
		try {
			statement.updAttribute("statement", "GET_TESTATA_RICHIESTA_OLD");
		} catch (SourceBeanException e) {
			String msg = "Errore recupero informazioni richieste";
			ror.reportFailure(e, className, msg);
		}

		doSelect(request, response);
	}

}