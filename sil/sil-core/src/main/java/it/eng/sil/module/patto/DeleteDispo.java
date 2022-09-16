/*package it.eng.sil.module.patto;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.tracing.TracerSingleton;
import com.engiweb.framework.util.QueryExecutor;

public class DeleteDispo  extends AbstractModule 
{
  private String className = this.getClass().getName();
  
  public void service(SourceBean Request, SourceBean Response) throws Exception
  {
      TracerSingleton.log( it.eng.sil.Values.APP_NAME,TracerSingleton.DEBUG,className + "Sono entrato in cancellazione");

    String pool = (String) getConfig().getAttribute("POOL");
    SourceBean statement =  (SourceBean) getConfig().getAttribute("QUERY");

    Boolean esito =  (Boolean) QueryExecutor.executeQuery(getRequestContainer(),getResponseContainer(),
        pool,statement,"DELETE");

    TracerSingleton.log( it.eng.sil.Values.APP_NAME,TracerSingleton.DEBUG,className + "::delete: request",
        Request);
      Object rowObject = null;

    try {
      Response.setAttribute("ESITO", esito);
    } // try
    catch (Exception ex) {
      TracerSingleton.log( it.eng.sil.Values.APP_NAME,TracerSingleton.CRITICAL,className + "::delete: errore nella setattribute");
    } // catch (Exception ex)  
  } 
}
 */

package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class DeleteDispo extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		doDelete(request, response);
	}
}
