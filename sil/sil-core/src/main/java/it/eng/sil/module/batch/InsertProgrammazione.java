package it.eng.sil.module.batch;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertProgrammazione extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		Object res = null;
		int errorCode = MessageCodes.General.INSERT_FAIL;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		TransactionQueryExecutor transExec = null;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		String codTipoBatch = "";
		try {
			transExec = new TransactionQueryExecutor(getPool(), this);
			enableTransactions(transExec);
			transExec.initTransaction();
			this.setSectionQuerySelect("QUERY_NEXTVAL");
			SourceBean row = doSelect(request, response, false);
			if (row.containsAttribute("ROW.DO_NEXTVAL")) {
				Object key = row.getAttribute("ROW.DO_NEXTVAL");
				request.updAttribute("PRGPROGRAMMABATCH", key);
			} else {
				throw new Exception("Impossibile leggere il progressivo per la nuova programmazione");
			}

			codTipoBatch = StringUtils.getAttributeStrNotNull(request, "CODTIPOBATCH");
			Integer chiaveBatch = Constants.mapBatch.get(codTipoBatch);
			switch (chiaveBatch.intValue()) {
			case Constants.APPUNTAMENTI: {
				setSectionQueryInsert("QUERY_INSERT_PROGRAMMAZIONE_BATCH_APP");
				break;
			}
			case Constants.AZIONI_PROGRAMMATE: {
				setSectionQueryInsert("QUERY_INSERT_PROGRAMMAZIONE_BATCH_AZIONE");
				break;
			}
			case Constants.CONFERMA_DID: {
				setSectionQueryInsert("QUERY_INSERT_PROGRAMMAZIONE_BATCH_DID");
				break;
			}
			case Constants.PERDITA_DISOCC: {
				setSectionQueryInsert("QUERY_INSERT_PROGRAMMAZIONE_BATCH_PERDDISOCC");
				break;
			}
			}

			res = this.doInsert(request, response, true);

			ret = ((Boolean) res).booleanValue();
			if (!ret) {
				throw new Exception("impossibile inserire la nuova programmazione");
			}

			transExec.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
		} catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			reportOperation.reportFailure(errorCode);
		}
	}

}
