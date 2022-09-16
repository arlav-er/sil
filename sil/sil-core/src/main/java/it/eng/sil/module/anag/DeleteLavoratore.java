package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

public class DeleteLavoratore extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DeleteLavoratore.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean Request, SourceBean Response) throws Exception {
		_logger.debug(className + "Sono entrato in cancellazione");

		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERY");

		Boolean esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
				statement, "DELETE");

		it.eng.sil.util.TraceWrapper.debug(_logger, className + "::delete: request", Request);

		Object rowObject = null;

		try {
			Response.setAttribute("ESITO", esito);
		} // try
		catch (Exception ex) {
			_logger.fatal(className + "::delete: errore nella setattribute");

		} // catch (Exception ex)
	}
}