package it.eng.sil.module.patto;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

public class GetElAnag extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetElAnag.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = null;
		boolean prgPresente = true;

		if (!request.containsAttribute("prgElencoAnagrafico")
				|| ((String) request.getAttribute("prgElencoAnagrafico")).length() <= 0) {
			/*
			 * statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_PROGRESSIVO"); } else {
			 */
			statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_PRGELANAG_DA_CDNLAV");
			SourceBean estraiPrg = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
					getResponseContainer(), pool, statement, "SELECT");
			if (estraiPrg != null && estraiPrg.containsAttribute("ROW.PRGELENCOANAGRAFICO")
					&& !(estraiPrg.getAttribute("ROW.PRGELENCOANAGRAFICO")).equals("")) {
				BigDecimal prgElAnag = (BigDecimal) estraiPrg.getAttribute("ROW.PRGELENCOANAGRAFICO");
				if (prgElAnag != null) {
					try {
						request.delAttribute("PRGELENCOANAGRAFICO");
						request.setAttribute("PRGELENCOANAGRAFICO", prgElAnag.toString());
					} catch (SourceBeanException sbEx) {
						it.eng.sil.util.TraceWrapper.fatal(_logger,
								className + "::select: response.setAttribute((SourceBean)rowObject)", sbEx);

						prgPresente = false;
					}
				}
			} else
				prgPresente = false;
			// statement = (SourceBean)
			// getConfig().getAttribute("QUERIES.SELECT_CDNLAVORATORE");
		}
		// Se è presente il prgElencoAnagrafico viene eseguita semplicemente la
		// query di
		// selezione a partire dal progressivo
		if (prgPresente) {
			statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_PROGRESSIVO");

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

}// class GetElAnag
