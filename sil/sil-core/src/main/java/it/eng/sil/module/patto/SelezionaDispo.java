package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

public class SelezionaDispo extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SelezionaDispo.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY");

		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
				getResponseContainer(), pool, statement, "SELECT");

		it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);

		Object rowObject = null;

		if (rowsSourceBean != null)
			rowObject = rowsSourceBean.getAttribute(DataRow.ROW_TAG);
		EMFErrorHandler engErrorHandler = getErrorHandler();
		if (rowObject == null) {
			_logger.debug(className + "::select: nessuna riga selezionata");

			engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.WARNING, 10006));
			return;
		} // if (rowObject == null)

		if (!(rowObject instanceof SourceBean)) {
			_logger.debug(className + "::select: più righe selezionate");

			engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.WARNING, 10006));
			return;
		} // if (!(rowObject instanceof SourceBean))

		try {
			response.setAttribute((SourceBean) rowObject);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowObject)", ex);

		} // catch (Exception ex)
	}
}