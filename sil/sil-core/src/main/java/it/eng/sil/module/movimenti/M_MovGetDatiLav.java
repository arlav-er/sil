package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;
//import com.engiweb.framework.tracing.TracerSingleton;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class M_MovGetDatiLav extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		TransactionQueryExecutor transExec = null;
		boolean ret = false;

		try {
			transExec = new TransactionQueryExecutor(getPool());
			transExec.initTransaction();
			this.setSectionQuerySelect("GET_LAV_CF_PROVINCIA");
			SourceBean rowProv = this.doSelect(request, response);
			if (rowProv.containsAttribute("ROW")) /* il lavoratore è fuori provincia */
				response.setAttribute("IscrizionePossibile", "true");
			else {
				this.setSectionQuerySelect("GET_LAV_CF_COLLMIRATO");
				SourceBean rowNum = this.doSelect(request, response);
				if (rowNum.containsAttribute("ROW")) // il lavoratore è iscritto al collocamento mirato
					response.setAttribute("IscrizionePossibile", "true");
				else
					response.setAttribute("IscrizionePossibile", "false");
			}
			transExec.commitTransaction();
			this.setMessageIdSuccess(idSuccess);
			this.setMessageIdFail(idFail);
			reportOperation.reportSuccess(idSuccess);
		} catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
		}
	}
}