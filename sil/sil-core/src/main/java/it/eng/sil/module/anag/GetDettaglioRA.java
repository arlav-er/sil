package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

public class GetDettaglioRA extends AbstractModule {

	private static final long serialVersionUID = 5863792564864524604L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetDettaglioRA.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");

		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_DETT_RA");

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

		}
	}
}