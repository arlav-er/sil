package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetInfCorrPatto extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetInfCorrPatto.class.getName());

	public void service(SourceBean request, SourceBean response)
			throws Exception { /*
								 * String pool = (String) getConfig().getAttribute("POOL"); SourceBean statement =
								 * (SourceBean) getConfig().getAttribute("QUERY");
								 * 
								 * SourceBean rowsSourceBean = (SourceBean)
								 * QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
								 * pool,statement,"SELECT");
								 * 
								 * it.eng.sil.util.TraceWrapper.debug( _logger,className + "::select: rowsSourceBean",
								 * rowsSourceBean);
								 * 
								 * Object rowObject = null;
								 * 
								 * try { response.setAttribute(rowsSourceBean); } // try catch (Exception ex) {
								 * it.eng.sil.util.TraceWrapper.fatal( _logger,className + "::select:
								 * response.setAttribute((SourceBean)rowObject)", ex); } // catch (Exception ex) }//end
								 * service
								 */
		doSelect(request, response);
	}
}
// end class GetInfCorrPatto
