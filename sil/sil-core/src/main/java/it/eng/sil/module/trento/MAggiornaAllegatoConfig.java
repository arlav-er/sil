package it.eng.sil.module.trento;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class MAggiornaAllegatoConfig extends AbstractSimpleModule {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MAggiornaAllegatoConfig.class.getName());

	public MAggiornaAllegatoConfig() {
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// Segnalazione soli errori/problemi
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int errorCode = MessageCodes.General.UPDATE_FAIL;
		String tipoAllegatoTemplate = "";
		boolean result = true;
		TransactionQueryExecutor trans = null;

		try {
			trans = new TransactionQueryExecutor(getPool());
			this.enableTransactions(trans);
			trans.initTransaction();

			tipoAllegatoTemplate = (String) request.getAttribute("tipoAllegatoTemplate");
			if (tipoAllegatoTemplate != null && tipoAllegatoTemplate.equals("1")) {
				request.delAttribute("CODTIPODOCUMENTO");
				request.delAttribute("STRDESCRIZIONE");
			} else {
				request.delAttribute("PRGTEMPLATEALLEGATO");
			}

			setSectionQueryUpdate("QUERY_UPDATE_ALLEGATO");
			result = doUpdate(request, response);
			if (!result) {
				reportOperation.reportFailure(errorCode);
				trans.rollBackTransaction();
				return;
			}

			trans.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		} catch (Throwable ex) {
			if (trans != null) {
				trans.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, "MAggiornaAllegatoConfig.service()", ex);
			reportOperation.reportFailure(errorCode);
		}
	}

}
