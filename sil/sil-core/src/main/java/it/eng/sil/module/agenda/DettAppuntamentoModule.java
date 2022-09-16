package it.eng.sil.module.agenda;

/*
 * Questa classe restituisce il dettaglio di un appuntamento .
 * 
 * @author: Stefania Orioli
 */

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

public class DettAppuntamentoModule extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DettAppuntamentoModule.class.getName());

	public DettAppuntamentoModule() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.QUERY");

		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
				getResponseContainer(), pool, statement, "SELECT");

		it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);

		Object rowObject = null;

		if (rowsSourceBean != null)
			rowObject = rowsSourceBean.getAttribute(DataRow.ROW_TAG);

		try {
			response.setAttribute((SourceBean) rowObject);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowObject)", ex);

		} // catch (Exception ex)
	}

}