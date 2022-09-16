package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class CMProspPersDipSave extends AbstractSimpleModule {

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
			String tipoPartTime = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoPartTime");
			String tipoIntermittenti = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoIntermittenti");

			this.setSectionQueryUpdate("QUERY_UPDATE_PROSP");
			check = doUpdate(serviceRequest, serviceResponse);
			if (!check) {
				throw new Exception("Errore durante l'aggiornamento del prospetto. Operazione interrotta");
			}

			if (("INSERT").equalsIgnoreCase(message)) {
				this.setSectionQueryInsert("QUERY_INSERT_ESCL");
				check = doInsert(serviceRequest, serviceResponse);
				if (!check) {
					throw new Exception("Errore durante l'inserimento esclusione. Operazione interrotta");
				}
			} else {
				this.setSectionQueryUpdate("QUERY_UPDATE_ESCL");
				check = doUpdate(serviceRequest, serviceResponse);
				if (!check) {
					throw new Exception("Errore durante l'aggiornamento esclusione. Operazione interrotta");
				}

				if (("diretta").equalsIgnoreCase(tipoPartTime)) {
					this.setSectionQueryDelete("QUERY_DELETE_ALL_PARTTIME");
					check = doDelete(serviceRequest, serviceResponse);
					if (!check) {
						throw new Exception("Errore durante la cancellazione dei part-time. Operazione interrotta");
					}
				}

				if (("diretta").equalsIgnoreCase(tipoIntermittenti)) {
					this.setSectionQueryDelete("QUERY_DELETE_ALL_INTERMITTENTI");
					check = doDelete(serviceRequest, serviceResponse);
					if (!check) {
						throw new Exception(
								"Errore durante la cancellazione degli intermittenti. Operazione interrotta");
					}
				}
			}

			transExec.commitTransaction();

			reportOperation.reportSuccess(idSuccess);

		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(idFail, e, "services()",
					"errore in transazione linguetta personale dipendente");

		} finally {
		}
	}

}
