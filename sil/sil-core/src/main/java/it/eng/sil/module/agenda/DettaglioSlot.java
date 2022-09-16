package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

public class DettaglioSlot extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DettaglioSlot.class.getName());

	public DettaglioSlot() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = null;
		SourceBean rowsSourceBean = null;
		String prgSlot = (String) request.getAttribute("PRGSLOT");
		// String codCpi = (String) request.getAttribute("CODCPI");
		try {
			if ((prgSlot == null) || (prgSlot.equals(""))) {
				prgSlot = (String) getServiceResponse().getAttribute("MSALVASLOT.PRGSLOT");
				request.updAttribute("PRGSLOT", prgSlot);
			}

			statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY");
			rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
					pool, statement, "SELECT");
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);

			response.setAttribute(rowsSourceBean);
		} // try

		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowObject)", ex);

		} // catch (Exception ex)
	}
}