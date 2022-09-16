package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.security.User;

public class SelectAgendaModule extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SelectAgendaModule.class.getName());

	public SelectAgendaModule() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = null;
		SourceBean rowsSourceBean = null;
		String prgAppuntamento = (String) request.getAttribute("PRGAPPUNTAMENTO");
		String codCpi = request.containsAttribute("CODCPI") ? (String) request.getAttribute("CODCPI") : "";

		try {
			if ((prgAppuntamento == null) || (prgAppuntamento.equals(""))) {
				prgAppuntamento = (String) getServiceResponse().getAttribute("MSALVAAPPUNTAMENTO.PRGAPPUNTAMENTO");
				request.updAttribute("PRGAPPUNTAMENTO", prgAppuntamento);
			}

			/*
			 * Se non trovo il cpi dalla request (nel caso provengo dai link nel footer del patto), recupero il cpi
			 * dalla sessione.
			 */
			if ((codCpi == null) || (codCpi.equals(""))) {
				User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
				codCpi = user.getCodRif();
				request.delAttribute("CODCPI");
				request.setAttribute("CODCPI", codCpi);
			}

			statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY");
			rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
					pool, statement, "SELECT");
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);

			response.setAttribute(rowsSourceBean);
			/*
			 * EMFErrorHandler engErrorHandler = getErrorHandler(); if (rowObject == null) { _logger.debug( className +
			 * "::select: nessuna riga selezionata");
			 * 
			 * //engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.WARNING, 10006)); return; } // if (rowObject
			 * == null) if (!(rowObject instanceof SourceBean)) { _logger.debug( className +
			 * "::select: più righe selezionate");
			 * 
			 * //engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.WARNING, 10006));
			 * response.setAttribute((SourceBean) rowObject); return; } // if (!(rowObject instanceof SourceBean))
			 */

		} // try

		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowObject)", ex);

		} // catch (Exception ex)
	}
}