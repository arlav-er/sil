package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.security.User;

public class MDettGSett extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MDettGSett.class.getName());
	private String className = this.getClass().getName();

	public MDettGSett() {
	}

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		SourceBean statement = null;
		SourceBean rowsSourceBean = null;

		String prgGiornoNl = (String) request.getAttribute("PRGGIORNONL");

		try {
			if ((prgGiornoNl == null) || (prgGiornoNl.equals(""))) {
				prgGiornoNl = (String) getServiceResponse().getAttribute("MSALVAGSETT.prgGiornoNl");
				request.updAttribute("PRGGIORNONL", prgGiornoNl);
			}

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