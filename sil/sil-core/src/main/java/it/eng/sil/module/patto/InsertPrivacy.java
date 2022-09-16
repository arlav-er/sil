package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.QueryStrategyException;
import it.eng.sil.util.ExceptionUtils;

public class InsertPrivacy extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		Object res = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			res = doInsert(request, response, true);
			ExceptionUtils.controlloDateRecordPrecedente(res,
					MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);
			reportOperation.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
		} catch (QueryStrategyException qse) {
			reportOperation.reportFailure(MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);
		}
	}
}
