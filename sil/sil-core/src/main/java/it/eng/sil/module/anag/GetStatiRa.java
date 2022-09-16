package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.SourceBeanUtils;

public class GetStatiRa extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetStatiRa.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERY_SELECT");

		SourceBean rowDettRA = (SourceBean) getServiceResponse().getAttribute("M_GetDettaglioRA.ROWS.ROW");
		if (rowDettRA != null) {
			String codStatoRa = SourceBeanUtils.getAttrStrNotNull(rowDettRA, "CODSTATORA");
			if (codStatoRa.equals("DAV"))
				statement = (SourceBean) getConfig().getAttribute("QUERY_SELECT_CON_FLG_S");
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
