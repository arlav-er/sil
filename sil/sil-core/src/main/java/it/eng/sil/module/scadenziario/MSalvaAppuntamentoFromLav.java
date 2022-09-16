/*
 * Creato il 21-feb-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.scadenziario;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.security.User;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

public class MSalvaAppuntamentoFromLav extends AbstractModule {
	private String className = this.getClass().getName();

	public MSalvaAppuntamentoFromLav() {
	}

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		SourceBean statement = null;
		Boolean esito = null;
		ReportOperationResult result = new ReportOperationResult(this, response);

		String codCpi = request.containsAttribute("CODCPI") ? (String) request.getAttribute("CODCPI") : "";
		String prgAppuntamentoStr = (String) request.getAttribute("PRGAPPUNTAMENTO");
		try {
			if ((codCpi == null) || (codCpi.equals(""))) {
				codCpi = user.getCodRif();
				request.delAttribute("CODCPI");
				request.setAttribute("CODCPI", codCpi);
			}
			response.setAttribute("CODCPI", codCpi);
			response.setAttribute("PRGAPPUNTAMENTO", prgAppuntamentoStr);
			statement = (SourceBean) getConfig().getAttribute("QUERIES.UPDATE_QUERY");
			esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, statement,
					"UPDATE");
		} catch (SourceBeanException e) {
			esito = new Boolean(false);
			result.reportFailure(e, className, "Errore durante la modifica dell'appuntamento");
		}

		if ((esito != null) && (esito.booleanValue() == true)) {
			result.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
		} else {
			result.reportFailure(MessageCodes.General.UPDATE_FAIL);
		}
	}
}