/**
 * 
 */
package it.eng.sil.module.budget;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author Fatale
 *
 */
public class GetElencoAzioni extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetElencoAzioni.class.getName());
	private String className = this.getClass().getName();

	/**
	 * 
	 */
	private static final long serialVersionUID = -3331840146646267339L;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		// SourceBean serviceReq = serviceRequest.getServiceRequest();

		String cfSel = (String) serviceRequest.getAttribute("CODICEFISCALE");
		String codSEde = (String) serviceRequest.getAttribute("CODSEDE");

		String newQuery = "select prgazioni as codice, strdescrizione  descrizione from de_azione a where not exists (select 1 ";
		newQuery = newQuery + " from vch_ente_accreditato e where a.prgazioni = e.prgazioni and ";
		newQuery = newQuery + " e.strcodicefiscale='" + cfSel + "'";
		newQuery = newQuery + " and e.codsede='" + codSEde + "')";

		SourceBean statement = null;
		String strTipoRicerca = serviceRequest.getAttribute("tipoRicerca").toString();
		String pool = (String) getConfig().getAttribute("POOL");

		statement = (SourceBean) getConfig().getAttribute(newQuery);

		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
				getResponseContainer(), pool, statement, "SELECT");

		Object rowObject = null;

		try {
			serviceResponse.setAttribute(rowsSourceBean);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowObject)", ex);

		} // catch
	}

}
