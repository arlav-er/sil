package it.eng.sil.module.agenda;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

public class RicercaAgendaLavoratori extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RicercaAgendaLavoratori.class.getName());

	public RicercaAgendaLavoratori() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.QUERY");

		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
				getResponseContainer(), pool, statement, "SELECT");

		it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);

		// Object rowObject = null;
		// if (rowsSourceBean != null) rowObject =
		// rowsSourceBean.getAttribute(DataRow.ROW_TAG);

		if (rowsSourceBean != null) {
			// String a=rowsSourceBean.toXML();
			BigDecimal prgazienda = (BigDecimal) rowsSourceBean.getAttribute("ROW.PRGAZIENDA");
			BigDecimal prgunita = (BigDecimal) rowsSourceBean.getAttribute("ROW.PRGUNITA");

			if ((prgazienda != null) || (prgunita != null)) {
				try {
					response.setAttribute("AZIENDA", "S");
				} catch (Exception ex) {
					it.eng.sil.util.TraceWrapper.fatal(_logger,
							className + "::select: response.setAttribute((SourceBean)rowsSourceBean)", ex);

				}
			} else {
				try {
					response.setAttribute("AZIENDA", "N");
				} catch (Exception ex) {
					it.eng.sil.util.TraceWrapper.fatal(_logger,
							className + "::select: response.setAttribute((SourceBean)rowsSourceBean)", ex);

				}
			} // end if prgazienda
		} // end if rowsSourceBean
	} // end service
}