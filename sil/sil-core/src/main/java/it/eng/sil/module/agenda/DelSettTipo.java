package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.LogUtils;

//import it.eng.sil.module.AbstractSimpleModule;

//public class DelSettTipo extends AbstractSimpleModule
public class DelSettTipo extends AbstractModule {
	private String className = this.getClass().getName();

	public DelSettTipo() {
	}

	public void service(SourceBean request, SourceBean response) {
		// doDelete(request, response);
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = null;
		Boolean esito = null;

		// 1. Delete records from ag_slot_tipo
		statement = (SourceBean) getConfig().getAttribute("QUERIES.QUERY_DEP");
		esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, statement,
				"DELETE");
		LogUtils.logDebug(className, "Delete from ag_slot_tipo", statement);
		// 2. Delete records from de_settimana_tipo
		statement = (SourceBean) getConfig().getAttribute("QUERIES.QUERY");
		esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, statement,
				"DELETE");
		LogUtils.logDebug(className, "Delete from de_settimana_tipo", statement);
	}

}