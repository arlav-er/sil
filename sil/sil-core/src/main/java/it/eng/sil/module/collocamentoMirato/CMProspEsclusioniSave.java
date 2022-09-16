package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class CMProspEsclusioniSave extends AbstractSimpleModule {

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		boolean check = true;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			String message = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");

			if (("INSERT").equalsIgnoreCase(message)) {
				this.setSectionQueryInsert("QUERY_INSERT");
				check = doInsert(serviceRequest, serviceResponse);
				if (!check) {
					throw new Exception("Errore durante l'inserimento esclusione. Operazione interrotta");
				}
			} else {
				this.setSectionQueryUpdate("QUERY_UPDATE");
				check = doUpdate(serviceRequest, serviceResponse);
				if (!check) {
					throw new Exception("Errore durante l'aggiornamento esclusione. Operazione interrotta");
				}
			}

			transExec.commitTransaction();

			reportOperation.reportSuccess(idSuccess);

		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(idFail, e, "services()", "errore in transazione linguetta esclusioni");

		} finally {
		}
	}

}
