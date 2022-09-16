package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

/*
 import com.engiweb.framework.dbaccess.sql.DataRow;
 import com.engiweb.framework.dispatching.module.AbstractModule;
 import com.engiweb.framework.tracing.TracerSingleton;
 import com.engiweb.framework.util.QueryExecutor;
 */
import it.eng.sil.module.AbstractSimpleModule;

public class StatoAtto extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {

		doSelect(request, response);

	}
}
