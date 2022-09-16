/**
 * 
 */
package it.eng.sil.module.voucher;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

/**
 * @author Fatale
 *
 */
public class SelVoucher extends AbstractModule {

	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SelVoucher.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		_logger.debug("Sono dentro la classe SelVoucher");

		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY");

		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
				getResponseContainer(), pool, statement, "SELECT");

		it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);

		try {
			serviceResponse.setAttribute((SourceBean) rowsSourceBean);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowsSourceBean)", ex);

		}
	}

}
