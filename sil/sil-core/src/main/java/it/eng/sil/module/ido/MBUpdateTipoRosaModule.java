package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

public class MBUpdateTipoRosaModule extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		this.disableMessageIdSuccess();
		this.disableMessageIdFail();
		boolean ret = false;

		this.setSectionQueryUpdate("QUERY_UPDATE");
		ret = doUpdate(request, response);

		if (!ret) {
			throw new Exception("aggiornamento della rosa fallito. Operazione interrotta");
		}

		this.setSectionQueryUpdate("UPDATE_EVASIONE");

		ret = doUpdate(request, response);

		if (!ret) {
			throw new Exception("aggiornamento dello stato di evasione della richiesta fallito. Operazione interrotta");
		}
	}
}