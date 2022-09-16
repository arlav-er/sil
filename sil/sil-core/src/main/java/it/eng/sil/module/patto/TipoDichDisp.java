package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean; /*
												import org.apache.log4j.*;
												import com.engiweb.framework.dbaccess.sql.DataRow;
												import com.engiweb.framework.dispatching.module.AbstractModule;
												import com.engiweb.framework.tracing.TracerSingleton;
												import com.engiweb.framework.util.QueryExecutor;
												*/

import it.eng.sil.module.AbstractSimpleModule;

public class TipoDichDisp extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(TipoDichDisp.class.getName());

	public void service(SourceBean request, SourceBean response)
			throws Exception { /*
								 * 
								 * String pool = (String) getConfig().getAttribute("POOL"); SourceBean statement =
								 * (SourceBean) getConfig().getAttribute("QUERY");
								 * 
								 * SourceBean rowsSourceBean = (SourceBean)
								 * QueryExecutor.executeQuery(getRequestContainer(),getResponseContainer(),
								 * pool,statement,"SELECT");
								 * 
								 * it.eng.sil.util.TraceWrapper.debug( _logger,className + "::select: rowsSourceBean",
								 * rowsSourceBean);
								 * 
								 * Object rowObject = null;
								 * 
								 * try { response.setAttribute(rowsSourceBean); } // try catch (Exception ex) {
								 * it.eng.sil.util.TraceWrapper.fatal( _logger,className + "::select:
								 * response.setAttribute((SourceBean)rowObject)", ex); } // catch (Exception ex) }
								 */
		doSelect(request, response);
	}
}