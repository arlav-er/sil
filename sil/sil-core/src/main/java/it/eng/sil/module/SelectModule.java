package it.eng.sil.module;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.StringUtils;

/**
 * Semplice SELECT. Recupera lo statement dall'XML di config del modulo. Prima prova in CONFIG.QUERY e se non lo trova,
 * in CONFIG.QUERIES.SELECT_QUERY.
 * 
 * @author Luigi Antenucci
 */
public class SelectModule extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SelectModule.class.getName());
	protected String className = StringUtils.getClassName(this);

	public void service(SourceBean request, SourceBean response) {
		_logger.debug(className + ".service() INIZIO");

		try {
			String pool = (String) getConfig().getAttribute("POOL");

			// Recupera il SB con il nome dello statement e eventuali parametri.
			SourceBean statement = (SourceBean) getConfig().getAttribute("QUERY");
			if (statement == null) {
				statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY");
				if (statement == null) {
					throw new Exception("Impossibile recuperare lo statement per la query dal CONFIG del modulo");
				}
			}

			// Esegue query
			SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
					getResponseContainer(), pool, statement, "SELECT");

			response.setAttribute(rowsSourceBean);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + ".service()", ex);

		}

		_logger.debug(className + ".service() FINE");

	}

}