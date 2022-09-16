package it.eng.sil.module.patto;

/*
 import com.engiweb.framework.base.SourceBean;
 import com.engiweb.framework.dbaccess.sql.DataRow;
 import com.engiweb.framework.dispatching.module.AbstractModule;
 import com.engiweb.framework.tracing.TracerSingleton;
 import com.engiweb.framework.util.QueryExecutor;


 public class ElencoIndisponibilita extends AbstractModule {
 private String className = this.getClass().getName();

 public void service(SourceBean request, SourceBean response) {
 String pool = (String) getConfig().getAttribute("POOL");
 SourceBean statement = (SourceBean) getConfig().getAttribute("QUERY");

 SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
 getResponseContainer(), pool, statement, "SELECT");

 TracerSingleton.log( it.eng.sil.Values.APP_NAME, TracerSingleton.DEBUG, className + "::select: rowsSourceBean", rowsSourceBean);

 Object rowObject = null;

 try {
 response.setAttribute(rowsSourceBean);
 } // try
 catch (Exception ex) {
 TracerSingleton.log( it.eng.sil.Values.APP_NAME, TracerSingleton.CRITICAL,
 className + "::select: response.setAttribute((SourceBean)rowObject)", ex);
 }
 // catch (Exception ex)  
 }
 }
 */
import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class ElencoIndisponibilita extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		doSelect(request, response);
	}
}