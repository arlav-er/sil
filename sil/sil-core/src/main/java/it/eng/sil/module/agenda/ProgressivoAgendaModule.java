package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

public class ProgressivoAgendaModule extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProgressivoAgendaModule.class.getName());

	public ProgressivoAgendaModule() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY");

		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
				getResponseContainer(), pool, statement, "SELECT");

		it.eng.sil.util.TraceWrapper.debug(_logger, className + "::Progressivo agenda", rowsSourceBean);

		// Object rowObject = null;
		// if (rowsSourceBean != null) rowObject =
		// rowsSourceBean.getAttribute(DataRow.ROW_TAG);

		String prgappuntamento = (String) rowsSourceBean.getAttribute("ROW.PRGAPPUNTAMENTO").toString();

		try {
			// request.setAttribute("PRGAPPUNTAMENTO",prgappuntamento);
			// response.setAttribute("PRGAPPUNTAMENTO",prgappuntamento);
			response.setAttribute((SourceBean) rowsSourceBean);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::Progressivo agenda: response.setAttribute((SourceBean)rowsSourceBean)", ex);

		} // catch (Exception ex)
	}

}