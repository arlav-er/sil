package it.eng.sil.module.patto;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class AnnullaDichAnnuale extends AbstractSimpleModule {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AnnullaDichAnnuale.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		TransactionQueryExecutor transactionExecutor = null;
		int idSuccess = this.disableMessageIdSuccess();
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		try {
			transactionExecutor = new TransactionQueryExecutor(getPool(), this);
			transactionExecutor.initTransaction();
			enableTransactions(transactionExecutor);
			//
			setSectionQueryUpdate("QUERY_UPDATE_DICH");
			boolean ris = doUpdate(serviceRequest, serviceResponse);
			if (ris) {
				setSectionQuerySelect("QUERY_SELECT_DOC");
				SourceBean rowDoc = doSelect(serviceRequest, serviceResponse);
				if (rowDoc != null) {
					BigDecimal prgDocumento = (BigDecimal) rowDoc.getAttribute("row.prgdocumento");
					BigDecimal numkloDidAnn = (BigDecimal) rowDoc.getAttribute("row.numklodocumento");
					numkloDidAnn = numkloDidAnn.add(new BigDecimal("1"));
					serviceRequest.updAttribute("NUMKLODOCUMENTODICH", numkloDidAnn);
					serviceRequest.updAttribute("PRGDOCUMENTO", prgDocumento);
					setSectionQueryUpdate("QUERY_UPDATE_DOC_DICH");
					ris = doUpdate(serviceRequest, serviceResponse);
					if (ris) {
						transactionExecutor.commitTransaction();
						this.setMessageIdSuccess(MessageCodes.General.UPDATE_SUCCESS);
						reportOperation.reportSuccess(idSuccess);
					} else {
						transactionExecutor.rollBackTransaction();
						reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
					}
				} else {
					transactionExecutor.rollBackTransaction();
					reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
				}
			} else {
				transactionExecutor.rollBackTransaction();
				reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
			}
		} catch (Exception e) {
			if (transactionExecutor != null) {
				transactionExecutor.rollBackTransaction();
			}
			reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
		}
	}
}