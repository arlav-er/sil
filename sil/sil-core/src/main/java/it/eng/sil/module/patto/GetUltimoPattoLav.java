package it.eng.sil.module.patto;

import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

public class GetUltimoPattoLav extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetUltimoPattoLav.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();

		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERY");

		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
				getResponseContainer(), pool, statement, "SELECT");

		it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);

		Object rowObject = null;

		if (rowsSourceBean != null)
			rowObject = rowsSourceBean.getAttribute(DataRow.ROW_TAG);

		try {
			response.setAttribute((SourceBean) rowObject);

			String cdnLavoratore = "";
			Vector vect = rowsSourceBean.getAttributeAsVector("ROW");
			if ((vect != null) && (vect.size() > 0)) {
				SourceBean row = (SourceBean) vect.get(0);
				if (row != null) {
					cdnLavoratore = row.getAttribute("CDNLAVORATORE").toString();
				}
			}

		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowObject)", ex);

		} // catch (Exception ex)
	}
}