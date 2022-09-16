package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Sottosistema;

/**
 * Modulo per la selezione delle mansioni per cui si è data "Disponibilità a lavorare con la mansione" attraverso il
 * campo FLGDISPONIBILE = 'S'
 */
public class ListMansioniDisponibileLavoro extends AbstractSimpleModule {
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		// INIT-PARTE-TEMP
		if (Sottosistema.CM.isOff()) {
			// END-PARTE-TEMP
			doSelect(request, response);
			// INIT-PARTE-TEMP
		} else {
			// END-PARTE-TEMP
			doSelectCM(request, response);
			// INIT-PARTE-TEMP
		}
		// END-PARTE-TEMP

	}

	/**
	 * il metodo sostituisce il nome dello statement per recuperare la select nel caso di interruttore Collocamento
	 * mirato attivo
	 * 
	 * @param request
	 * @param response
	 */
	private void doSelectCM(SourceBean request, SourceBean response) {
		ReportOperationResult ror = new ReportOperationResult(this, response);
		SourceBean statement = (SourceBean) getConfig().getAttribute(QUERY);
		try {
			statement.updAttribute("statement", "LOAD_MANSIONI_DISPO_LAV_CM");
		} catch (SourceBeanException e) {
			String msg = "Errore recupero informazioni";
			ror.reportFailure(e, className, msg);
		}

		doSelect(request, response);
	}
}