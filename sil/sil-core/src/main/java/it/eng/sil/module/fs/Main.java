package it.eng.sil.module.fs;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.security.User;

/**
 * 
 * @author Franco Vuoto
 * @version 1.0
 */
public class Main extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Main.class.getName());
	private String className = this.getClass().getName();

	public Main() {
	}

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY");
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);

		try {
			SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
					getResponseContainer(), pool, statement, "SELECT");

			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service() rowsSourceBean", rowsSourceBean);

			int len = rowsSourceBean.getAttributeAsVector("ROW").size();

			if (len > 1) {
				// piu' profili
				user.setPrgProfilo(0);
				response.setAttribute(rowsSourceBean);
			} else if (len == 1) {
				// Un solo profilo
				BigDecimal cod = (BigDecimal) rowsSourceBean.getAttribute("ROW.CODICE");
				user.setPrgProfilo(cod.intValue());
			}
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

		}
	}
}