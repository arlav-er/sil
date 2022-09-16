package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

public class GetDidInps extends AbstractModule {

	private static final long serialVersionUID = 5863792564864524603L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetDidInps.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = null;
		boolean prgPresente = true;

		String prgDid = (String) request.getAttribute("prgElencoAnagrafico");
		if (prgPresente) {
			statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_DIDINPS");

			SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
					getResponseContainer(), pool, statement, "SELECT");

			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);

			Object rowObject = null;

			try {
				response.setAttribute(rowsSourceBean);
			} // try
			catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						className + "::select: response.setAttribute((SourceBean)rowObject)", ex);

			} // catch (Exception ex)
		}
	}// end service

}
