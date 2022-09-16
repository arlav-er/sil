package it.eng.sil.module.scadenziario;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

public class GetTipiScadenze extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetTipiScadenze.class.getName());

	public GetTipiScadenze() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.QUERY");

		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
				getResponseContainer(), pool, statement, "SELECT");

		it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);

		try {
			response.setAttribute((SourceBean) rowsSourceBean);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowsSourceBean)", ex);

		}
	}
}