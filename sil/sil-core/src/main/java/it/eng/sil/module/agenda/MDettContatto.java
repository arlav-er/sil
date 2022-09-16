package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.security.User;

public class MDettContatto extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MDettContatto.class.getName());
	private String className = this.getClass().getName();

	public MDettContatto() {
	}

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		SourceBean statement = null;
		SourceBean rowsSourceBean = null;

		String prgContatto = (String) request.getAttribute("PRGCONTATTO");

		try {
			if ((prgContatto == null) || (prgContatto.equals(""))) {
				prgContatto = (String) getServiceResponse().getAttribute("MSALVACONTATTO.prgContatto");
				request.updAttribute("prgContatto", prgContatto);
			}

			String cpi = (String) request.getAttribute("CODCPICONTATTO");

			// Verifica che il contatto è di un CPI diverso
			Object[] inputParameter = new Object[1];
			inputParameter[0] = prgContatto;
			rowsSourceBean = (SourceBean) QueryExecutor.executeQuery("S_GETCPI_CONTATTO", inputParameter, "SELECT",
					"SIL_DATI");
			String cpiContattoDiverso = (String) rowsSourceBean.getAttribute("ROW.codcpicontatto");
			if (!cpi.equals(cpiContattoDiverso)) {
				request.updAttribute("CODCPICONTATTO", cpiContattoDiverso);
			}
			rowsSourceBean.clearBean();

			statement = (SourceBean) getConfig().getAttribute("QUERIES.QUERY");
			rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
					pool, statement, "SELECT");
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service() rowsSourceBean", rowsSourceBean);

			response.setAttribute(rowsSourceBean);

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

		}
	}
}