package it.eng.sil.module.trento;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class MSalvaAllegatoConfig extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MSalvaAllegatoConfig.class.getName());

	public MSalvaAllegatoConfig() {
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// Segnalazione soli errori/problemi
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int errorCode = MessageCodes.General.INSERT_FAIL;
		String tipoAllegatoTemplate = "";
		TransactionQueryExecutor trans = null;
		boolean result = true;

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

			request.delAttribute("PRGCONFIGPROTDOCTIPO");
			setSectionQuerySelect("QUERY_ALLEGATO_NEXT_VAL");
			SourceBean rowKey = doSelect(request, response, false);
			Object progressivokey = rowKey.getAttribute("row.nextval");
			request.setAttribute("PRGCONFIGPROTDOCTIPO", progressivokey);

			setSectionQueryInsert("QUERY_INSERT_ALLEGATO");

			result = doInsert(request, response);
			if (!result) {
				trans.rollBackTransaction();
				reportOperation.reportFailure(errorCode);
				return;
			}

			trans.commitTransaction();
			response.setAttribute("PRGCONFIGPROTDOCTIPO", progressivokey);
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		} catch (Throwable ex) {
			if (trans != null) {
				trans.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, "MSalvaAllegatoConfig.service()", ex);
			reportOperation.reportFailure(errorCode);
		}
	}
}
