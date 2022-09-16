package it.eng.sil.module.trento;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class MAggiornaInsTemplate extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MAggiornaInsTemplate.class.getName());

	public MAggiornaInsTemplate() {
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// Segnalazione soli errori/problemi
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int errorCode = MessageCodes.General.INSERT_FAIL;
		TransactionQueryExecutor trans = null;
		boolean result = true;

		try {
			trans = new TransactionQueryExecutor(getPool());
			this.enableTransactions(trans);
			trans.initTransaction();

			request.delAttribute("PRGCONFIGPROT");
			setSectionQuerySelect("QUERY_CONFIG_NEXT_VAL");
			SourceBean rowConfig = doSelect(request, response, false);
			Object progressivoConfigurazione = rowConfig.getAttribute("row.nextval");
			request.setAttribute("PRGCONFIGPROT", progressivoConfigurazione);
			setSectionQueryInsert("QUERY_INSERT_CONFIG");
			result = doInsert(request, response);
			if (!result) {
				trans.rollBackTransaction();
				reportOperation.reportFailure(errorCode);
				return;
			}
			setSectionQueryUpdate("QUERY_UPDATE_TEMPLATE");
			result = doUpdate(request, response);
			if (!result) {
				trans.rollBackTransaction();
				response.setAttribute("aggiornato", "false");
				reportOperation.reportFailure(errorCode);
				return;
			}

			trans.commitTransaction();
			response.setAttribute("aggiornato", "true");
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);

		} catch (Throwable ex) {
			if (trans != null) {
				trans.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, "MAggiornaInsTemplate.service()", ex);
			reportOperation.reportFailure(errorCode);
		}
	}
}
