package it.eng.sil.module.agenda;

import java.math.BigDecimal;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.security.User;

public class MSalvaAppuntamento extends AbstractModule {
	private String className = this.getClass().getName();

	public MSalvaAppuntamento() {
	}

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		SourceBean statement = null;
		Boolean esito = null;
		ReportOperationResult result = new ReportOperationResult(this, response);

		String prgAppuntamentoStr = (String) request.getAttribute("PRGAPPUNTAMENTO");
		try {
			if ((prgAppuntamentoStr == null) || (prgAppuntamentoStr.equals(""))) {
				// INSERT
				BigDecimal prgApp = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, "S_AG_AGENDA");
				// prgApp = prgApp.add(new BigDecimal(1));
				request.updAttribute("PRGAPPUNTAMENTO", prgApp);
				response.setAttribute("PRGAPPUNTAMENTO", prgApp.toString());
				statement = (SourceBean) getConfig().getAttribute("QUERIES.INSERT_QUERY");
				esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
						statement, "INSERT");
				sessionContainer.setAttribute("AG_NEW_PRG_APP", prgApp.toString());
			} else {
				response.setAttribute("PRGAPPUNTAMENTO", prgAppuntamentoStr);
				statement = (SourceBean) getConfig().getAttribute("QUERIES.UPDATE_QUERY");
				esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
						statement, "UPDATE");
			}
		} catch (SourceBeanException e) {
			esito = new Boolean(false);
			result.reportFailure(e, className, "Errore durante l'inserimento/modifica dell'appuntamento");
		}

		if ((esito != null) && (esito.booleanValue() == true)) {
			result.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
		} else {
			result.reportFailure(MessageCodes.General.UPDATE_FAIL);
		}
	}
}
