package it.eng.sil.module.login;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;

/**
 * 
 * @author Franco Vuoto
 * @version 1.0
 */
public class Login extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Login.class.getName());
	private String className = this.getClass().getName();

	public Login() {
	}

	public void service(SourceBean request, SourceBean response) {
		_logger.debug(className + "::service()");

		// verifica la presenza della login utenti convenzionati
		Object params[] = null;
		SourceBean row = null;
		row = (SourceBean) QueryExecutor.executeQuery("QUERY_PARAM_TENTATIVI", params, "SELECT", Values.DB_SIL_DATI);

		String flgLoginConvenzione = row.getAttribute("ROW.FLGLOGINCONVENZIONE") == null ? "N"
				: (String) row.getAttribute("ROW.FLGLOGINCONVENZIONE");
		try {
			response.setAttribute("FLG_LOGIN_CONVENZIONE", flgLoginConvenzione);
		} catch (SourceBeanException e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", e);
		}

	}
}