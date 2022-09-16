package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

public class CercaCCNL_Approssimata extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CercaCCNL_Approssimata.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		SourceBean statement = null;
		String strTipoRicerca = request.getAttribute("tipoRicerca").toString();
		String pool = (String) getConfig().getAttribute("POOL");
		if (strTipoRicerca.compareTo("descrizione") == 0) {

			statement = (SourceBean) getConfig().getAttribute("QUERIES.DESC_QUERY");
		} else {
			statement = (SourceBean) getConfig().getAttribute("QUERIES.COD_QUERY");
		}

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
}